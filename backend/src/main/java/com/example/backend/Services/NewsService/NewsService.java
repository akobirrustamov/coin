package com.example.backend.Services.NewsService;

import com.example.backend.DTO.NewsDto;
import com.example.backend.Entity.News;
import org.springframework.http.HttpEntity;

public interface NewsService {
    HttpEntity<?> addNews(NewsDto news);
    HttpEntity<?> getAllNews();
    HttpEntity<?> deleteNews(Integer id);

    News getNewsById(Integer id);
}
