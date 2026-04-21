package com.twitter.feed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter @NoArgsConstructor @AllArgsConstructor
public class AppUser {
    @Id
    private UUID id;
    private String username;
}
