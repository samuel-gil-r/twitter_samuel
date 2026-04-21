package com.twitter.feed.service;

import com.twitter.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStream() {
        return feedRepository.findTop50ByOrderByCreatedAtDesc().stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId().toString(),
                        "content", p.getContent(),
                        "createdAt", p.getCreatedAt().toString(),
                        "authorId", p.getAuthor().getId().toString(),
                        "authorUsername", p.getAuthor().getUsername()
                )).toList();
    }
}
