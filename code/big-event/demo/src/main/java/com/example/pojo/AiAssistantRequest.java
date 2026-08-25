package com.example.pojo;

import jakarta.validation.constraints.NotBlank;

public record AiAssistantRequest(Integer articleId, @NotBlank String content) {
}