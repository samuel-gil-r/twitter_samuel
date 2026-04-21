package com.twitter.user.service;

import com.twitter.user.entity.AppUser;
import com.twitter.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;

    @Transactional
    public AppUser findOrCreate(String auth0Id, String email) {
        return userRepository.findByAuth0Id(auth0Id).orElseGet(() -> {
            String base = email != null
                    ? email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_")
                    : "user";
            String username = base;
            int suffix = 1;
            while (userRepository.existsByUsername(username)) {
                username = base + suffix++;
            }
            return userRepository.save(AppUser.builder()
                    .auth0Id(auth0Id)
                    .email(email != null ? email : auth0Id + "@placeholder.com")
                    .username(username)
                    .createdAt(Instant.now())
                    .build());
        });
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> getByAuth0Id(String auth0Id) {
        return userRepository.findByAuth0Id(auth0Id);
    }

    public Map<String, Object> toResponse(AppUser user) {
        return Map.of(
                "id", user.getId().toString(),
                "email", user.getEmail(),
                "username", user.getUsername(),
                "createdAt", user.getCreatedAt().toString()
        );
    }
}
