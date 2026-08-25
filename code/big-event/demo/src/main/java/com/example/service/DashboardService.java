package com.example.service;

import com.example.pojo.Article;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Long> overview();

    List<Map<String, Object>> publishTrend();

    List<Map<String, Object>> categoryDistribution();

    List<Article> hotArticles(Integer limit);
}