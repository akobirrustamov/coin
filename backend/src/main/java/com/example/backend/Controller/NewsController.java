package com.example.backend.Controller;

import com.example.backend.DTO.NewsDto;
import com.example.backend.Entity.News;
import com.example.backend.Services.NewsService.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @PostMapping
    public HttpEntity<?> addNews(@RequestBody NewsDto news) {
        return newsService.addNews(news);
    }
    @PutMapping("/{id}")
    public HttpEntity<?> updateNews(@PathVariable Integer id, @RequestBody NewsDto news) {
        return newsService.updateNews(id, news);
    }

    @GetMapping
    public HttpEntity<?> getAllNews() {
        return newsService.getAllNews();
    }
    @GetMapping("/{id}")
    public HttpEntity<?> getNewsById(@PathVariable Integer id) {
        News newsItem = newsService.getNewsById(id);
        if (newsItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(newsItem, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteNews(@PathVariable Integer id) {
        System.out.println(id);
        return newsService.deleteNews(id);
    }
}
