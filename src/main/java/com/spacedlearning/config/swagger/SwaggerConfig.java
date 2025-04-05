package com.spacedlearning.config.swagger;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Enhanced Swagger/OpenAPI configuration for API documentation
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${springdoc.swagger-ui.enabled:true}")
    private boolean swaggerEnabled;

//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Spaced Learning API")
//                        .version("1.0")
//                        .description("API for spaced repetition learning"));
//    }

    /**
     * Creates a detailed OpenAPI configuration for Swagger documentation.
     */
    @Bean
    OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Spaced Learning API Documentation")
                        .description(
                                "RESTful API for spaced repetition learning system that helps users study more effectively")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Spaced Learning Team")
                                .email("contact@spacedlearning.com")
                                .url("https://spacedlearning.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                        .termsOfService("https://spacedlearning.com/terms"))
                .externalDocs(new ExternalDocumentation()
                        .description("Spaced Learning User Documentation")
                        .url("https://spacedlearning.com/docs"))
                .servers(List.of(
                        new Server().url("http://localhost:8088").description("Development Server"),
                        new Server().url("https://api.spacedlearning.com").description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                        "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"")));
    }

    /**
     * Configuration for User API group
     */
    @Bean
    GroupedOpenApi userApiGroup() {
        return GroupedOpenApi.builder()
                .group("User Management")
                .pathsToMatch("/api/v1/users/**", "/api/v1/auth/**")
                .packagesToScan("com.spacedlearning.controller")
                .build();
    }

    /**
     * Configuration for Book API group
     */
    @Bean
    GroupedOpenApi bookApiGroup() {
        return GroupedOpenApi.builder()
                .group("Book-Management") // Thay đổi tên group, loại bỏ khoảng trắng
                .pathsToMatch("/api/v1/books/**", "/api/v1/modules/**")
                .packagesToScan("com.spacedlearning.controller")
                .build();
    }

    /**
     * Configuration for Learning Progress API group
     */
    @Bean
    GroupedOpenApi progressApiGroup() {
        return GroupedOpenApi.builder()
                .group("Learning Progress")
                .pathsToMatch("/api/v1/progress/**", "/api/v1/repetitions/**", "/api/v1/stats/**")
                .packagesToScan("com.spacedlearning.controller")
                .build();
    }
}