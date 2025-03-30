package com.spacedlearning.config.security;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.security.JwtAuthenticationFilter;
import com.spacedlearning.security.JwtTokenFilter;
import com.spacedlearning.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;
	@Lazy
	private final UserMapper userMapper;
	private final JwtTokenFilter jwtTokenFilter;


	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authConfig)
			throws Exception {
		final AuthenticationManager authenticationManager = authenticationManager(authConfig);

		// Tắt CSRF vì chúng ta dùng JWT (stateless)
		http.csrf(AbstractHttpConfigurer::disable);

		// Cấu hình CORS
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		// Cho phép hiển thị nội dung frame (các trường hợp dev, h2-console), xem xét
		// tắt khi production
		http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

		// Áp dụng rule cho các request
		http.authorizeHttpRequests(auth -> auth
				// Cho phép preflight request của CORS
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				// Các endpoint public
				.requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
				// Tất cả request còn lại yêu cầu đăng nhập
				.anyRequest().authenticated());

		// Không dùng session để lưu thông tin user => stateless
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Thêm Provider xác thực
		http.authenticationProvider(authenticationProvider());

		// Filter tự viết, để giải quyết JWT
		http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

		// Tùy biến Handling exception
		http.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
			log.error("Authentication failed: {}", authException.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			final Map<String, Object> errorDetails = Map.of("message", "Unauthorized access", "success", false,
					"status", HttpServletResponse.SC_UNAUTHORIZED);
			objectMapper.writeValue(response.getOutputStream(), errorDetails);
		}));

		// Filter xử lý đăng nhập (nếu bạn muốn custom flow đăng nhập)
		final JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,
				jwtTokenProvider, objectMapper, userMapper);
		jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
		http.addFilter(jwtAuthenticationFilter);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		// Chỉ dùng "*" cho dev/test. Production thì thay bằng domain cụ thể
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
		configuration.setExposedHeaders(List.of("Authorization"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
        return source;
    }

	@Bean
	AuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		// BCRYPT with strength phù hợp (để default 10 hoặc nâng lên)
		return new BCryptPasswordEncoder();
	}

    @Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		if (config == null) {
			throw new IllegalArgumentException("AuthenticationConfiguration must not be null");
		}
		return config.getAuthenticationManager();
    }
}
