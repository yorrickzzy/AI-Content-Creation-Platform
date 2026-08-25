package com.example.service;

import com.example.pojo.Article;
import com.example.pojo.PageBean;

public interface PublicArticleService {
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String keyword);

    Article detail(Integer id);
}