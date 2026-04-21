package com.twitter.monolith.service;

import com.twitter.monolith.dto.CurrentUserResponse;
import com.twitter.monolith.entity.AppUser;
import com.twitter.monolith.exception.ResourceNotFoundException;
import com.twitter.monolith.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock AppUserRepository userRepository;
    @InjectMocks UserServiceImpl userService;

    @Test
    void findOrCreate_createsNewUserWithNickname() {
        when(userRepository.findByAuth0Id("auth0|new")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("mynick")).thenReturn(false);
        AppUser saved = AppUser.builder()
                .id(UUID.randomUUID()).auth0Id("auth0|new")
                .email("new@example.com").username("mynick").createdAt(Instant.now()).build();
        when(userRepository.save(any())).thenReturn(saved);

        AppUser result = userService.findOrCreate("auth0|new", "new@example.com", "mynick");
        assertThat(result.getUsername()).isEqualTo("mynick");
        verify(userRepository).save(any(AppUser.class));
    }

    @Test
    void findOrCreate_fallsBackToEmailPrefixWhenNoNickname() {
        when(userRepository.findByAuth0Id("auth0|new")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        AppUser saved = AppUser.builder()
                .id(UUID.randomUUID()).auth0Id("auth0|new")
                .email("newuser@example.com").username("newuser").createdAt(Instant.now()).build();
        when(userRepository.save(any())).thenReturn(saved);

        AppUser result = userService.findOrCreate("auth0|new", "newuser@example.com", null);
        assertThat(result.getUsername()).isEqualTo("newuser");
    }

    @Test
    void findOrCreate_returnsExistingUserWithoutSave() {
        AppUser existing = AppUser.builder()
                .id(UUID.randomUUID()).auth0Id("auth0|existing")
                .email("ex@example.com").username("existing").createdAt(Instant.now()).build();
        when(userRepository.findByAuth0Id("auth0|existing")).thenReturn(Optional.of(existing));

        AppUser result = userService.findOrCreate("auth0|existing", "ex@example.com", "existing");
        assertThat(result.getUsername()).isEqualTo("existing");
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrCreate_updatesPlaceholderUserWithRealData() {
        AppUser placeholder = AppUser.builder()
                .id(UUID.randomUUID()).auth0Id("auth0|u")
                .email("auth0|u@placeholder.com").username("user").createdAt(Instant.now()).build();
        when(userRepository.findByAuth0Id("auth0|u")).thenReturn(Optional.of(placeholder));
        when(userRepository.existsByUsername("realname")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AppUser result = userService.findOrCreate("auth0|u", "real@example.com", "realname");
        assertThat(result.getEmail()).isEqualTo("real@example.com");
        assertThat(result.getUsername()).isEqualTo("realname");
        verify(userRepository).save(any());
    }

    @Test
    void getCurrentUser_mapsToDto() {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID()).auth0Id("auth0|test")
                .email("test@example.com").username("testuser").createdAt(Instant.now()).build();
        when(userRepository.findByAuth0Id("auth0|test")).thenReturn(Optional.of(user));

        CurrentUserResponse result = userService.getCurrentUser("auth0|test");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getAuth0Id()).isEqualTo("auth0|test");
    }

    @Test
    void getCurrentUser_throwsWhenNotFound() {
        when(userRepository.findByAuth0Id("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getCurrentUser("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
