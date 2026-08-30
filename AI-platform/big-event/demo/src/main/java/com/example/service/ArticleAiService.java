package com.example.service;

import com.example.pojo.AiAssistantResponse;

public interface ArticleAiService {

    String generateSummary(String content);

    AiAssistantResponse generateAssistantSuggestions(Integer articleId, String content);
}