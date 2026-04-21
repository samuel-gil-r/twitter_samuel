package com.twitter.monolith.service;

import com.twitter.monolith.dto.CurrentUserResponse;
import com.twitter.monolith.entity.AppUser;
import com.twitter.monolith.exception.ResourceNotFoundException;
import com.twitter.monolith.exception.UsernameAlreadyTakenException;
import com.twitter.monolith.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;

    @Override
    @Transactional
    public AppUser findOrCreate(String auth0Id, String email, String preferredUsername) {
        return userRepository.findByAuth0Id(auth0Id).map(existing -> {
            if (existing.getEmail().endsWith("@placeholder.com")) {
                if (email != null) existing.setEmail(email);
                if (preferredUsername != null) {
                    String unique = uniqueUsername(preferredUsername, existing.getUsername());
                    existing.setUsername(unique);
                }
                return userRepository.save(existing);
            }
            return existing;
        }).orElseGet(() -> {
            String baseUsername = resolveBaseUsername(preferredUsername, email, auth0Id);
            String username = uniqueUsername(baseUsername, null);
            AppUser newUser = AppUser.builder()
                    .auth0Id(auth0Id)
                    .email(email != null ? email : auth0Id + "@placeholder.com")
                    .username(username)
                    .profileComplete(false)
                    .build();
            return userRepository.save(newUser);
        });
    }

    @Override
    @Transactional
    public CurrentUserResponse updateUsername(String auth0Id, String displayName) {
        AppUser user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        String trimmed = displayName.trim();
        if (!trimmed.equals(user.getUsername()) && userRepository.existsByUsername(trimmed)) {
            throw new UsernameAlreadyTakenException(trimmed);
        }
        user.setUsername(trimmed);
        user.setProfileComplete(true);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(String auth0Id) {
        return toResponse(userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + auth0Id)));
    }

    private CurrentUserResponse toResponse(AppUser user) {
        return CurrentUserResponse.builder()
                .id(user.getId())
                .auth0Id(user.getAuth0Id())
                .email(user.getEmail())
                .username(user.getUsername())
                .profileComplete(user.isProfileComplete())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String resolveBaseUsername(String preferredUsername, String email, String auth0Id) {
        if (preferredUsername != null && !preferredUsername.isBlank()) {
            return sanitize(preferredUsername);
        }
        if (email != null && !email.isBlank()) {
            return sanitize(email.split("@")[0]);
        }
        String sub = auth0Id.contains("|") ? auth0Id.split("\\|")[1] : auth0Id;
        return sanitize(sub);
    }

    private String sanitize(String raw) {
        String clean = raw.replaceAll("[^a-zA-Z0-9_]", "_");
        return clean.isEmpty() ? "user" : clean;
    }

    private String uniqueUsername(String base, String currentUsername) {
        if (base.equals(currentUsername)) return base;
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }
}
