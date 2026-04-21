package com.twitter.monolith.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CurrentUserResponse {
    private UUID id;
    private String auth0Id;
    private String email;
    private String username;
    private boolean profileComplete;
    private Instant createdAt;
}
