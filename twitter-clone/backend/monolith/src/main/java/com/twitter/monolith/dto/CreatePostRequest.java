package com.twitter.monolith.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Content must not be blank")
    @Size(max = 140, message = "Content must be at most 140 characters")
    private String content;
}
