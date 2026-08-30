package com.example.pojo;

import jakarta.validation.constraints.NotBlank;

public record AiSummaryRequest(@NotBlank String content) {
}