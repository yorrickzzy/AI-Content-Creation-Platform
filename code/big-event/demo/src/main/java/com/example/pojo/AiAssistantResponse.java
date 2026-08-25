package com.example.pojo;

import java.util.List;

public record AiAssistantResponse(String summary, List<String> titleSuggestions, List<String> tags) {
}