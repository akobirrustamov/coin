package com.example.backend.Controller;

import com.example.backend.DTO.NewsDto;
import com.example.backend.Entity.News;
import com.example.backend.Entity.Newspaper;
import com.example.backend.Repository.NewspaperRepo;
import com.example.backend.Services.NewsService.NewsService;
import com.example.backend.Services.NewspaperService.NewspaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/newspaper")
@RequiredArgsConstructor
public class NewspaperController {
    private final NewspaperService newsService;
    private final NewspaperRepo newspaperRepo;

    @PostMapping
    public HttpEntity<?> addNews(@RequestBody NewsDto news) {
        return newsService.addNews(news);
    }

    @GetMapping
    public HttpEntity<?> getAllNews() {
        return newsService.getAllNews();
    }
    @GetMapping("/all")
    public HttpEntity<?> getAllNewsAll() {
        return newsService.getAllNewsAll();
    }
    @GetMapping("/{id}")
    public HttpEntity<?> getNewsById(@PathVariable Integer id) {
        Newspaper newsItem = newsService.getNewsById(id);
        if (newsItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(newsItem, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteNews(@PathVariable Integer id) {

        return newsService.deleteNews(id);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> updateNews(@RequestBody NewsDto news, @PathVariable Integer id) {

        Newspaper newspaper = newspaperRepo.findById(id).orElse(null);
        if (newspaper == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        newspaper.setTitle(news.getTitle());
        newspaper.setDescription(news.getDescription());
        newspaperRepo.save(newspaper);
        return ResponseEntity.ok("ok");

    }
}
