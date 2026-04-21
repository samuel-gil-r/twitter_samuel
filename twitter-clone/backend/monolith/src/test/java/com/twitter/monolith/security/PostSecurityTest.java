package com.twitter.monolith.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.monolith.dto.CreatePostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class PostSecurityTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void getPostsIsPublicAndReturns200() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk());
    }

    @Test
    void createPostWithoutTokenReturns401() throws Exception {
        CreatePostRequest req = new CreatePostRequest();
        req.setContent("No token");
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPostWithValidTokenReturns201() throws Exception {
        String token = JwtTestHelper.generateToken("auth0|poster", "poster@example.com",
                List.of("write:posts", "read:profile"));
        CreatePostRequest req = new CreatePostRequest();
        req.setContent("Hello from integration test!");
        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello from integration test!"))
                .andExpect(jsonPath("$.authorUsername").value("poster"));
    }

    @Test
    void getMeProvisionesUserAndReturns200() throws Exception {
        String token = JwtTestHelper.generateToken("auth0|meuser", "meuser@example.com",
                List.of("read:profile"));
        mockMvc.perform(get("/api/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("meuser@example.com"))
                .andExpect(jsonPath("$.username").value("meuser"));
    }
}
