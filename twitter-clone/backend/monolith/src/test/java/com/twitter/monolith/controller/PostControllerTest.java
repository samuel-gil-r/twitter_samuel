package com.twitter.monolith.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.monolith.dto.CreatePostRequest;
import com.twitter.monolith.dto.PostResponse;
import com.twitter.monolith.config.CorsConfig;
import com.twitter.monolith.config.SecurityConfig;
import com.twitter.monolith.security.TestSecurityConfig;
import com.twitter.monolith.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import({TestSecurityConfig.class, SecurityConfig.class, CorsConfig.class})
class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean PostService postService;

    @Test
    void getPostsReturnsEmptyListWhenNoPosts() throws Exception {
        when(postService.getAllPosts()).thenReturn(List.of());
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getPostsReturnsList() throws Exception {
        PostResponse post = PostResponse.builder()
                .id(UUID.randomUUID()).content("Hello").createdAt(Instant.now())
                .authorId(UUID.randomUUID()).authorUsername("user1").build();
        when(postService.getAllPosts()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"))
                .andExpect(jsonPath("$[0].authorUsername").value("user1"));
    }

    @Test
    void createPostReturns201WithValidJwt() throws Exception {
        PostResponse response = PostResponse.builder()
                .id(UUID.randomUUID()).content("My post").createdAt(Instant.now())
                .authorId(UUID.randomUUID()).authorUsername("user1").build();
        when(postService.createPost(any(CreatePostRequest.class), anyString(), any(), any())).thenReturn(response);

        CreatePostRequest req = new CreatePostRequest();
        req.setContent("My post");

        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.subject("auth0|test")
                                .claim("scope", "write:posts")
                                .claim("nickname", "user1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("My post"));
    }

    @Test
    void createPostReturns400WhenContentTooLong() throws Exception {
        String longContent = "a".repeat(141);
        CreatePostRequest req = new CreatePostRequest();
        req.setContent(longContent);

        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.subject("auth0|test")
                                .claim("scope", "write:posts")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPostReturns400WhenContentBlank() throws Exception {
        CreatePostRequest req = new CreatePostRequest();
        req.setContent("   ");

        mockMvc.perform(post("/api/posts")
                        .with(jwt().jwt(j -> j.subject("auth0|test")
                                .claim("scope", "write:posts")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
