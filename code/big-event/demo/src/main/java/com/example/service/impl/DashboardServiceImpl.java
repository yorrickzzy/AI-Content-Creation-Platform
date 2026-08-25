package com.example.service.impl;

import com.example.mapper.ArticleMapper;
import com.example.pojo.Article;
import com.example.service.DashboardService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final ArticleMapper articleMapper;

    public DashboardServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public Map<String, Long> overview() {
        Map<String, Long> overview = new LinkedHashMap<>();
        overview.put("articleCount", articleMapper.countAll());
        overview.put("draftCount", articleMapper.countDrafts());
        overview.put("publishedCount", articleMapper.countPublished());
        overview.put("totalViews", articleMapper.sumViewCount());
        return overview;
    }

    @Override
    public List<Map<String, Object>> publishTrend() {
        Map<String, Object> countsByDate = new LinkedHashMap<>();
        for (Map<String, Object> item : articleMapper.publishTrend()) {
            countsByDate.put(String.valueOf(item.get("date")), item.get("count"));
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        for (int offset = 6; offset >= 0; offset--) {
            String date = LocalDate.now().minusDays(offset).toString();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date);
            item.put("count", countsByDate.getOrDefault(date, 0));
            trend.add(item);
        }
        return trend;
    }

    @Override
    public List<Map<String, Object>> categoryDistribution() {
        return articleMapper.categoryDistribution();
    }

    @Override
    public List<Article> hotArticles(Integer limit) {
        return articleMapper.hotArticles(limit);
    }
}