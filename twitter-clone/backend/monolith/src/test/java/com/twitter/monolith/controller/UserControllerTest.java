package com.twitter.monolith.controller;

import com.twitter.monolith.config.CorsConfig;
import com.twitter.monolith.config.SecurityConfig;
import com.twitter.monolith.dto.CurrentUserResponse;
import com.twitter.monolith.entity.AppUser;
import com.twitter.monolith.security.TestSecurityConfig;
import com.twitter.monolith.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({TestSecurityConfig.class, SecurityConfig.class, CorsConfig.class})
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean UserService userService;

    @Test
    void getMeReturnsUserProfile() throws Exception {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID()).auth0Id("auth0|test")
                .email("test@example.com").username("testuser").createdAt(Instant.now()).build();
        CurrentUserResponse profile = CurrentUserResponse.builder()
                .id(user.getId()).auth0Id("auth0|test")
                .email("test@example.com").username("testuser")
                .createdAt(user.getCreatedAt()).build();

        when(userService.findOrCreate(anyString(), anyString(), any())).thenReturn(user);
        when(userService.getCurrentUser("auth0|test")).thenReturn(profile);

        mockMvc.perform(get("/api/me")
                        .with(jwt().jwt(j -> j.subject("auth0|test")
                                .claim("scope", "read:profile")
                                .claim("email", "test@example.com")
                                .claim("nickname", "testuser"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.auth0Id").value("auth0|test"));
    }
}
