package com.example.controller;

import com.example.pojo.Article;
import com.example.pojo.AiSummaryRequest;
import com.example.pojo.AiAssistantRequest;
import com.example.pojo.AiAssistantResponse;
import com.example.pojo.PageBean;
import com.example.pojo.Result;
import com.example.service.ArticleService;
import com.example.service.ArticleAiService;
import com.example.utils.JwtUtil;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleAiService articleAiService;

    @PostMapping("/ai/summary")
    public Result<String> generateSummary(@RequestBody @Validated AiSummaryRequest request) {
        return Result.success(articleAiService.generateSummary(request.content()));
    }

    @PostMapping("/ai/assistant")
    public Result<AiAssistantResponse> generateAssistantSuggestions(@RequestBody @Validated AiAssistantRequest request) {
        return Result.success(articleAiService.generateAssistantSuggestions(request.articleId(), request.content()));
    }

    @PostMapping
    public Result add(@RequestBody @Validated Article article){
        articleService.add(article);
        return Result.success();
    }

    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state
    ) {
        PageBean<Article> pb = articleService.list(pageNum, pageSize, categoryId, state);
        return Result.success(pb);
    }

    @GetMapping("/detail")
    public Result<Article> detail(Integer id) {
        Article article = articleService.findById(id);
        return Result.success(article);
    }

    @PutMapping
    public Result update(@RequestBody @Validated Article article) {
        if (!articleService.update(article)) {
            return Result.error("文章已被其他请求修改，请刷新后重试");
        }
        return Result.success();
    }

    @DeleteMapping
    public Result delete(Integer id) {
        articleService.delete(id);
        return Result.success();
    }

}