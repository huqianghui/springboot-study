package com.hu.study.chapter51.service;

import com.hu.study.chapter51.entity.Article;

import java.util.List;

public interface IArticleService {
     List<Article> getAllArticles();
     Article getArticleById(int articleId);
     boolean addArticle(Article article);
     void updateArticle(Article article);
     void deleteArticle(int articleId);
}
