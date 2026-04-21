package com.twitter.posts.service;

import com.twitter.posts.entity.AppUser;
import com.twitter.posts.entity.Post;
import com.twitter.posts.repository.AppUserRepository;
import com.twitter.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AppUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId().toString(),
                        "content", p.getContent(),
                        "createdAt", p.getCreatedAt().toString(),
                        "authorId", p.getAuthor().getId().toString(),
                        "authorUsername", p.getAuthor().getUsername()
                )).toList();
    }

    @Transactional
    public Map<String, Object> createPost(String content, String auth0Id) {
        AppUser author = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + auth0Id));
        Post post = postRepository.save(Post.builder().content(content).author(author).build());
        return Map.of(
                "id", post.getId().toString(),
                "content", post.getContent(),
                "createdAt", post.getCreatedAt().toString(),
                "authorUsername", post.getAuthor().getUsername()
        );
    }
}
