package com.twitter.posts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AppUser {
    @Id
    private UUID id;
    private String auth0Id;
    private String username;
}
