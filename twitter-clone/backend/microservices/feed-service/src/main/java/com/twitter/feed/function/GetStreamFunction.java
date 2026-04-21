package com.twitter.feed.function;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twitter.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class GetStreamFunction {

    private final FeedService feedService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getStream() {
        return event -> {
            try {
                String body = objectMapper.writeValueAsString(feedService.getStream());
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withHeaders(Map.of("Content-Type", "application/json"))
                        .withBody(body);
            } catch (Exception e) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(500)
                        .withBody("{\"error\":\"Internal Server Error\"}");
            }
        };
    }
}
