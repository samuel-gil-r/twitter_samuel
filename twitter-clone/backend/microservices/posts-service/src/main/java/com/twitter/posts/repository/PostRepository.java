package com.twitter.posts.repository;

import com.twitter.posts.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    @Query("SELECT p FROM Post p JOIN FETCH p.author ORDER BY p.createdAt DESC")
    List<Post> findAllByOrderByCreatedAtDesc();
}
