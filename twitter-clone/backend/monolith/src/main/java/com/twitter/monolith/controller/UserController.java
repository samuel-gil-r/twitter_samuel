package com.twitter.monolith.controller;

import com.twitter.monolith.dto.CurrentUserResponse;
import com.twitter.monolith.dto.UpdateUsernameRequest;
import com.twitter.monolith.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Get current user (protected)",
            description = "Returns the authenticated user's profile. Provisions a local record on first call.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CurrentUserResponse> getMe(JwtAuthenticationToken token) {
        String auth0Id = token.getName();
        String email = token.getToken().getClaimAsString("email");
        String preferredUsername = resolvePreferredUsername(token);
        userService.findOrCreate(auth0Id, email, preferredUsername);
        return ResponseEntity.ok(userService.getCurrentUser(auth0Id));
    }

    @PatchMapping("/me")
    @Operation(
            summary = "Set display name (protected)",
            description = "Sets the user's display name and marks the profile as complete.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CurrentUserResponse> updateMe(
            @Valid @RequestBody UpdateUsernameRequest request,
            JwtAuthenticationToken token) {
        return ResponseEntity.ok(userService.updateUsername(token.getName(), request.getDisplayName()));
    }

    static String resolvePreferredUsername(JwtAuthenticationToken token) {
        String nickname = token.getToken().getClaimAsString("nickname");
        if (nickname != null && !nickname.isBlank()) return nickname;
        String name = token.getToken().getClaimAsString("name");
        if (name != null && !name.isBlank()) return name;
        return null;
    }
}
