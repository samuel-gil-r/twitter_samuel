package com.twitter.feed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter @NoArgsConstructor @AllArgsConstructor
public class Post {
    @Id
    private UUID id;
    private String content;
    private Instant createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser author;
}
