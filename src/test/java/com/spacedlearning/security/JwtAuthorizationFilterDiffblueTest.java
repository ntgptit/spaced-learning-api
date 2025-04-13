package com.spacedlearning.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ContextConfiguration(classes = {JwtAuthorizationFilter.class})
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
@EnableConfigurationProperties
@DisabledInAotMode
class JwtAuthorizationFilterDiffblueTest {
    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsService userDetailsService;

    /**
     * Test {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}.
     * <ul>
     *   <li>Given {@code Bearer}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName("Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); given 'Bearer'")
    @Tag("MaintainedByDiffblue")
    void testDoFilterInternal_givenBearer() throws ServletException, IOException {
        // Arrange
        HttpServletRequestWrapper request = mock(HttpServletRequestWrapper.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("Bearer ");
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
        verify(request).getHeader(eq("Authorization"));
    }

    /**
     * Test {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}.
     * <ul>
     *   <li>Given {@code https://example.org/example}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName("Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); given 'https://example.org/example'")
    @Tag("MaintainedByDiffblue")
    void testDoFilterInternal_givenHttpsExampleOrgExample() throws ServletException, IOException {
        // Arrange
        HttpServletRequestWrapper request = mock(HttpServletRequestWrapper.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("https://example.org/example");
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
        verify(request).getHeader(eq("Authorization"));
    }

    /**
     * Test {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}.
     * <ul>
     *   <li>Given {@link ObjectMapper} {@link ObjectMapper#writeValue(OutputStream, Object)} does nothing.</li>
     *   <li>Then calls {@link ObjectMapper#writeValue(OutputStream, Object)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName("Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); given ObjectMapper writeValue(OutputStream, Object) does nothing; then calls writeValue(OutputStream, Object)")
    @Tag("MaintainedByDiffblue")
    void testDoFilterInternal_givenObjectMapperWriteValueDoesNothing_thenCallsWriteValue()
            throws ServletException, IOException {
        // Arrange
        when(jwtTokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
        when(jwtTokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);
        doNothing().when(objectMapper).writeValue(Mockito.<OutputStream>any(), Mockito.<Object>any());
        HttpServletRequestWrapper request = mock(HttpServletRequestWrapper.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("Bearer https://example.org/example");
        HttpServletResponseWrapper response = mock(HttpServletResponseWrapper.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(new ByteArrayOutputStream(1)));
        doNothing().when(response).setContentType(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, mock(FilterChain.class));

        // Assert
        verify(objectMapper).writeValue(isA(OutputStream.class), isA(Object.class));
        verify(jwtTokenProvider).getUsernameFromToken(eq("https://example.org/example"));
        verify(jwtTokenProvider).validateToken(eq("https://example.org/example"));
        verify(response).getOutputStream();
        verify(response).setContentType(eq("application/json"));
        verify(request).getHeader(eq("Authorization"));
        verify(response).setStatus(eq(500));
    }

    /**
     * Test {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}.
     * <ul>
     *   <li>When {@link MockHttpServletRequest#MockHttpServletRequest()}.</li>
     *   <li>Then calls {@link FilterChain#doFilter(ServletRequest, ServletResponse)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName("Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); when MockHttpServletRequest(); then calls doFilter(ServletRequest, ServletResponse)")
    @Tag("MaintainedByDiffblue")
    void testDoFilterInternal_whenMockHttpServletRequest_thenCallsDoFilter() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
    }

    /**
     * Test {@link JwtAuthorizationFilter#shouldNotFilter(HttpServletRequest)}.
     * <ul>
     *   <li>When {@link MockHttpServletRequest#MockHttpServletRequest()}.</li>
     *   <li>Then return {@code false}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthorizationFilter#shouldNotFilter(HttpServletRequest)}
     */
    @Test
    @DisplayName("Test shouldNotFilter(HttpServletRequest); when MockHttpServletRequest(); then return 'false'")
    @Tag("MaintainedByDiffblue")
    void testShouldNotFilter_whenMockHttpServletRequest_thenReturnFalse() {
        // Arrange, Act and Assert
        assertFalse(jwtAuthorizationFilter.shouldNotFilter(new MockHttpServletRequest()));
    }
}
