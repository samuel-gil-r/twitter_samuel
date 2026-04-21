package com.twitter.monolith.service;

import com.twitter.monolith.dto.CreatePostRequest;
import com.twitter.monolith.dto.PostResponse;

import java.util.List;

public interface PostService {
    List<PostResponse> getAllPosts();
    PostResponse createPost(CreatePostRequest request, String auth0Id, String email, String preferredUsername);
}
