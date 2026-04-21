package com.twitter.monolith.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Provide a valid Auth0 access token. Obtain it from Auth0 with audience: https://twitter-clone-api"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Twitter Clone API")
                        .version("1.0.0")
                        .description("""
                                Simplified Twitter-like REST API.

                                **Public endpoints:** GET /api/posts, GET /api/stream

                                **Protected endpoints (Auth0 JWT required):**
                                - POST /api/posts — requires scope `write:posts`
                                - GET /api/me — requires scope `read:profile`
                                """))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("https://YOUR_API_GATEWAY_URL").description("AWS API Gateway")
                ));
    }
}
