package com.example.service.impl;

import com.example.mapper.AiGenerationLogMapper;
import com.example.pojo.AiAssistantResponse;
import com.example.utils.ThreadLocalUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.service.ArticleAiService;

@Service
public class ArticleAiServiceImpl implements ArticleAiService {

    private final RestClient restClient;
    private final String model;
    private final ObjectMapper objectMapper;
    private final AiGenerationLogMapper aiGenerationLogMapper;

    public ArticleAiServiceImpl(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            AiGenerationLogMapper aiGenerationLogMapper,
            @Value("${ai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${ai.api-key:}") String apiKey,
            @Value("${ai.model:gpt-4o-mini}") String model) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.model = model;
            this.objectMapper = objectMapper;
            this.aiGenerationLogMapper = aiGenerationLogMapper;
    }

    @Override
    public String generateSummary(String content) {
        if (content.length() > 12000) {
            throw new IllegalArgumentException("文章内容不能超过12000个字符");
        }

        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", "你是中文文章编辑。请根据用户提供的文章内容生成一段简洁、客观的中文摘要，不超过120字，只返回摘要正文。"
        );
        Map<String, Object> userMessage = Map.of("role", "user", "content", content);
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("messages", List.of(systemMessage, userMessage));
        request.put("temperature", 0.3);

        Map<?, ?> response = restClient.post()
            .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || !(response.get("choices") instanceof List<?> choices) || choices.isEmpty()) {
            throw new IllegalStateException("AI服务未返回摘要");
        }
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map<?, ?> choice)
                || !(choice.get("message") instanceof Map<?, ?> message)
                || !(message.get("content") instanceof String summary)
                || summary.isBlank()) {
            throw new IllegalStateException("AI服务返回格式不正确");
        }
        return summary.trim();
    }

    @Override
    public AiAssistantResponse generateAssistantSuggestions(Integer articleId, String content) {
        if (content.length() > 12000) {
            throw new IllegalArgumentException("文章内容不能超过12000个字符");
        }

        long startedAt = System.currentTimeMillis();
        Integer userId = currentUserId();
        try {
            String rawContent = chatCompletion(
                    "你是中文内容创作助手。根据用户的文章内容生成建议。只返回合法 JSON，不要 Markdown 代码块或其他文字。"
                            + "JSON 必须包含：summary（不超过120字字符串）、titleSuggestions（3个不同标题的字符串数组）、tags（3到5个简短中文标签的字符串数组）。",
                    content);
            AiAssistantResponse response = objectMapper.readValue(extractJson(rawContent), AiAssistantResponse.class);
            validateAssistantResponse(response);
            aiGenerationLogMapper.insert(userId, articleId, "CONTENT_ASSISTANT", model, content.length(), rawContent,
                    "SUCCESS", System.currentTimeMillis() - startedAt, null, LocalDateTime.now());
            return response;
        } catch (Exception exception) {
            aiGenerationLogMapper.insert(userId, articleId, "CONTENT_ASSISTANT", model, content.length(), null,
                    "FAILED", System.currentTimeMillis() - startedAt, abbreviate(exception.getMessage()), LocalDateTime.now());
            if (exception instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
            if (exception instanceof RestClientResponseException responseException
                    && responseException.getStatusCode().value() == 401) {
                throw new IllegalStateException("模型服务鉴权失败，请检查 AI_API_KEY、AI_BASE_URL 和 AI_MODEL 配置");
            }
            throw new IllegalStateException("AI创作建议生成失败，请稍后重试");
        }
    }

    private String chatCompletion(String systemPrompt, String content) {
        Map<String, Object> systemMessage = Map.of("role", "system", "content", systemPrompt);
        Map<String, Object> userMessage = Map.of("role", "user", "content", content);
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("messages", List.of(systemMessage, userMessage));
        request.put("temperature", 0.4);
        request.put("response_format", Map.of("type", "json_object"));

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);
        if (response == null || !(response.get("choices") instanceof List<?> choices) || choices.isEmpty()) {
            throw new IllegalStateException("AI服务未返回创作建议");
        }
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map<?, ?> choice)
                || !(choice.get("message") instanceof Map<?, ?> message)
                || !(message.get("content") instanceof String text)
                || text.isBlank()) {
            throw new IllegalStateException("AI服务返回格式不正确");
        }
        return text.trim();
    }

    private void validateAssistantResponse(AiAssistantResponse response) {
        if (response == null || response.summary() == null || response.summary().isBlank()
                || response.summary().length() > 120 || response.titleSuggestions() == null
                || response.titleSuggestions().size() < 3 || response.tags() == null || response.tags().isEmpty()) {
            throw new IllegalStateException("AI服务返回的创作建议不完整");
        }
    }

    private Integer currentUserId() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims == null || !(claims.get("id") instanceof Integer userId)) {
            throw new IllegalStateException("未获取到当前用户");
        }
        return userId;
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("AI服务未返回JSON结果");
        }
        return content.substring(start, end + 1);
    }

    private String abbreviate(String value) {
        if (value == null) {
            return "未知错误";
        }
        return value.length() > 1000 ? value.substring(0, 1000) : value;
    }
}