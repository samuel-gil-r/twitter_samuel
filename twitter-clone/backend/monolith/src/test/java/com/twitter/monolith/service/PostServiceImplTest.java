package com.twitter.monolith.service;

import com.twitter.monolith.dto.CreatePostRequest;
import com.twitter.monolith.dto.PostResponse;
import com.twitter.monolith.entity.AppUser;
import com.twitter.monolith.entity.Post;
import com.twitter.monolith.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock PostRepository postRepository;
    @Mock UserService userService;
    @InjectMocks PostServiceImpl postService;

    private AppUser user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = AppUser.builder()
                .id(UUID.randomUUID())
                .auth0Id("auth0|test")
                .email("test@example.com")
                .username("testuser")
                .createdAt(Instant.now())
                .build();
        post = Post.builder()
                .id(UUID.randomUUID())
                .content("Hello world")
                .createdAt(Instant.now())
                .author(user)
                .build();
    }

    @Test
    void getAllPosts_returnsMappedList() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));
        List<PostResponse> result = postService.getAllPosts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Hello world");
        assertThat(result.get(0).getAuthorUsername()).isEqualTo("testuser");
    }

    @Test
    void getAllPosts_returnsEmptyList() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());
        assertThat(postService.getAllPosts()).isEmpty();
    }

    @Test
    void createPost_persistsAndReturnsResponse() {
        when(userService.findOrCreate("auth0|test", "test@example.com", "testuser")).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        CreatePostRequest req = new CreatePostRequest();
        req.setContent("Hello world");

        PostResponse result = postService.createPost(req, "auth0|test", "test@example.com", "testuser");
        assertThat(result.getContent()).isEqualTo("Hello world");
        assertThat(result.getAuthorUsername()).isEqualTo("testuser");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_usesNicknameAsUsername() {
        AppUser nicknameUser = AppUser.builder()
                .id(UUID.randomUUID()).auth0Id("auth0|test")
                .email("test@example.com").username("mynickname").createdAt(Instant.now()).build();
        Post nicknamePost = Post.builder()
                .id(UUID.randomUUID()).content("Hello").createdAt(Instant.now()).author(nicknameUser).build();

        when(userService.findOrCreate("auth0|test", "test@example.com", "mynickname")).thenReturn(nicknameUser);
        when(postRepository.save(any(Post.class))).thenReturn(nicknamePost);

        CreatePostRequest req = new CreatePostRequest();
        req.setContent("Hello");

        PostResponse result = postService.createPost(req, "auth0|test", "test@example.com", "mynickname");
        assertThat(result.getAuthorUsername()).isEqualTo("mynickname");
    }
}
