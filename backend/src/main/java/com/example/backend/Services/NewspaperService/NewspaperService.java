package com.example.backend.Services.NewspaperService;

import com.example.backend.DTO.NewsDto;
import com.example.backend.Entity.News;
import com.example.backend.Entity.Newspaper;
import org.springframework.http.HttpEntity;

public interface NewspaperService {
    HttpEntity<?> addNews(NewsDto news);
    HttpEntity<?> getAllNews();
    HttpEntity<?> deleteNews(Integer id);

    Newspaper getNewsById(Integer id);

    HttpEntity<?> getAllNewsAll();
}
