package com.twitter.user.function;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jwt.JWTClaimsSet;
import com.twitter.user.auth.JwtValidator;
import com.twitter.user.entity.AppUser;
import com.twitter.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class GetCurrentUserFunction {

    private final JwtValidator jwtValidator;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getMe() {
        return event -> {
            try {
                String authorization = event.getHeaders() != null
                        ? event.getHeaders().get("Authorization")
                        : null;
                if (authorization == null) {
                    authorization = event.getHeaders() != null
                            ? event.getHeaders().get("authorization")
                            : null;
                }

                JWTClaimsSet claims = jwtValidator.validate(authorization);
                String auth0Id = claims.getSubject();
                String email = (String) claims.getClaim("email");

                AppUser user = userService.findOrCreate(auth0Id, email);
                String body = objectMapper.writeValueAsString(userService.toResponse(user));

                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withHeaders(Map.of("Content-Type", "application/json"))
                        .withBody(body);
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
}
