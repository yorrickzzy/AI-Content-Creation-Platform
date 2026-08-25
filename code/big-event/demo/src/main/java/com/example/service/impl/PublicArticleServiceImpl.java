package com.example.service.impl;

import com.example.mapper.ArticleMapper;
import com.example.pojo.Article;
import com.example.pojo.PageBean;
import com.example.service.PublicArticleService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicArticleServiceImpl implements PublicArticleService {
    private final ArticleMapper articleMapper;

    public PublicArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.listPublished(categoryId, keyword);
        Page<Article> page = (Page<Article>) articles;
        PageBean<Article> result = new PageBean<>();
        result.setTotal(page.getTotal());
        result.setItems(page.getResult());
        return result;
    }

    @Override
    @Transactional
    public Article detail(Integer id) {
        if (articleMapper.incrementViewCount(id) == 0) {
            return null;
        }
        return articleMapper.findPublishedById(id);
    }
}