package com.twitter.monolith.service;

import com.twitter.monolith.dto.CreatePostRequest;
import com.twitter.monolith.dto.PostResponse;
import com.twitter.monolith.entity.AppUser;
import com.twitter.monolith.entity.Post;
import com.twitter.monolith.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request, String auth0Id, String email, String preferredUsername) {
        AppUser author = userService.findOrCreate(auth0Id, email, preferredUsername);
        Post post = Post.builder()
                .content(request.getContent().trim())
                .author(author)
                .build();
        return PostResponse.from(postRepository.save(post));
    }
}
