package com.twitter.monolith.dto;

import com.twitter.monolith.entity.Post;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PostResponse {
    private UUID id;
    private String content;
    private Instant createdAt;
    private UUID authorId;
    private String authorUsername;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .build();
    }
}
