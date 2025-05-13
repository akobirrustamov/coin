package com.example.backend.Services.NewsService;

import com.example.backend.DTO.NewsDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.News;
import com.example.backend.Entity.Newspaper;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.NewsRepo;
import com.example.backend.Repository.NewspaperRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepo newsRepo;
    private final AttachmentRepo attachmentRepo;

    @Override
    public HttpEntity<?> addNews(NewsDto news) {
        Optional<Attachment> mainPhotoOpt = attachmentRepo.findById(news.getMainPhoto());
        if (mainPhotoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Main photo not found");
        }
        Attachment mainPhoto = mainPhotoOpt.get();

        List<Attachment> photos = news.getPhotos().stream()
                .map(attachmentRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        News newsEntity = new News(news.getTitle(), news.getDescription(), mainPhoto, photos);
        newsRepo.save(newsEntity);
        return ResponseEntity.ok(newsEntity);
    }

    @Override
    public HttpEntity<?> getAllNews() {
        List<News> newsList = newsRepo.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(newsList);
    }

    @Override
    public HttpEntity<?> deleteNews(Integer id) {
        Optional<News> newsOpt = newsRepo.findById(id);
        if (newsOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        News news = newsOpt.get();
        System.out.println(news.getPhotos());

        newsRepo.delete(news);
        for (Attachment photo : news.getPhotos()) {
                deleteAttachment(photo);
            }
        if (news.getMainPhoto() != null) {
            deleteAttachment(news.getMainPhoto());
        }

        return ResponseEntity.ok().build();
    }

    private void deleteAttachment(Attachment attachment) {
        File file = new File("backend/files" + attachment.getPrefix() + "/" + attachment.getName());
        if (file.exists()) {
            file.delete();
        }
        attachmentRepo.deleteById(attachment.getId());
    }

    @Override
    public News getNewsById(Integer id) {
        return newsRepo.findById(id).orElseThrow();
    }
}
