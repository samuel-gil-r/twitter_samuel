package com.twitter.monolith.controller;

import com.twitter.monolith.dto.CreatePostRequest;
import com.twitter.monolith.dto.PostResponse;
import com.twitter.monolith.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Post creation and retrieval")
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    @Operation(summary = "Get all posts (public)", description = "Returns all posts ordered by creation date descending")
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/stream")
    @Operation(summary = "Get global stream (public)", description = "Alias for GET /api/posts — the public global feed")
    public ResponseEntity<List<PostResponse>> getStream() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping("/posts")
    @Operation(
            summary = "Create a post (protected)",
            description = "Creates a new post for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            JwtAuthenticationToken token) {
        String auth0Id = token.getName();
        String email = token.getToken().getClaimAsString("email");
        String preferredUsername = UserController.resolvePreferredUsername(token);
        PostResponse response = postService.createPost(request, auth0Id, email, preferredUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
