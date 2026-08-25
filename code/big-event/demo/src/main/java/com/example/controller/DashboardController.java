package com.example.controller;

import com.example.pojo.Article;
import com.example.pojo.Result;
import com.example.service.DashboardService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public Result<Map<String, Long>> overview() {
        return Result.success(dashboardService.overview());
    }

    @GetMapping("/publish-trend")
    public Result<List<Map<String, Object>>> publishTrend() {
        return Result.success(dashboardService.publishTrend());
    }

    @GetMapping("/category-distribution")
    public Result<List<Map<String, Object>>> categoryDistribution() {
        return Result.success(dashboardService.categoryDistribution());
    }

    @GetMapping("/hot-articles")
    public Result<List<Article>> hotArticles(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(dashboardService.hotArticles(Math.min(Math.max(limit, 1), 20)));
    }
}