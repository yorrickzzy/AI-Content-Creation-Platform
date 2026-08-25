package com.example.controller;

import com.example.pojo.Article;
import com.example.pojo.PageBean;
import com.example.pojo.Result;
import com.example.service.PublicArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/articles")
public class PublicArticleController {
    private final PublicArticleService publicArticleService;

    public PublicArticleController(PublicArticleService publicArticleService) {
        this.publicArticleService = publicArticleService;
    }

    @GetMapping
    public Result<PageBean<Article>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.success(publicArticleService.list(pageNum, pageSize, categoryId, keyword));
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Integer id) {
        Article article = publicArticleService.detail(id);
        if (article == null) {
            return Result.error("文章不存在或尚未发布");
        }
        return Result.success(article);
    }
}