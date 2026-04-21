package com.twitter.posts.function;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jwt.JWTClaimsSet;
import com.twitter.posts.auth.JwtValidator;
import com.twitter.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class CreatePostFunction {

    private final JwtValidator jwtValidator;
    private final PostService postService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> createPost() {
        return event -> {
            try {
                String authorization = getHeader(event, "Authorization");
                JWTClaimsSet claims = jwtValidator.validate(authorization);

                if (!jwtValidator.hasScope(claims, "write:posts")) {
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(403)
                            .withBody("{\"error\":\"Forbidden\",\"message\":\"Requires scope write:posts\"}");
                }

                Map<?, ?> body = objectMapper.readValue(event.getBody(), Map.class);
                String content = (String) body.get("content");

                if (content == null || content.isBlank()) {
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(400)
                            .withBody("{\"error\":\"Bad Request\",\"message\":\"Content must not be blank\"}");
                }
                if (content.length() > 140) {
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(400)
                            .withBody("{\"error\":\"Bad Request\",\"message\":\"Content must be at most 140 characters\"}");
                }

                Map<String, Object> result = postService.createPost(content.trim(), claims.getSubject());
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(201)
                        .withHeaders(Map.of("Content-Type", "application/json"))
                        .withBody(objectMapper.writeValueAsString(result));

            } catch (IllegalArgumentException e) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(401)
                        .withBody("{\"error\":\"Unauthorized\",\"message\":\"" + e.getMessage() + "\"}");
            } catch (Exception e) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(500)
                        .withBody("{\"error\":\"Internal Server Error\"}");
            }
        };
    }

    private String getHeader(APIGatewayProxyRequestEvent event, String name) {
        if (event.getHeaders() == null) return null;
        String val = event.getHeaders().get(name);
        return val != null ? val : event.getHeaders().get(name.toLowerCase());
    }
}
