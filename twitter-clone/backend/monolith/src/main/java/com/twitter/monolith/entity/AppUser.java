package com.twitter.monolith.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String auth0Id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean profileComplete = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
