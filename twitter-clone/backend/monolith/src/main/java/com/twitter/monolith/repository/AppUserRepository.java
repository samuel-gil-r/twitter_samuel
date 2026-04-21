package com.twitter.monolith.repository;

import com.twitter.monolith.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByAuth0Id(String auth0Id);
    Optional<AppUser> findByEmail(String email);
    boolean existsByUsername(String username);
}
