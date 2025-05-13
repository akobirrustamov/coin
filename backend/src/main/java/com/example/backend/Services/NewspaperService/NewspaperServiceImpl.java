package com.example.backend.Services.NewspaperService;

import com.example.backend.DTO.NewsDto;
import com.example.backend.DTO.YearlyNewsResponse;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.News;
import com.example.backend.Entity.Newspaper;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.NewspaperRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewspaperServiceImpl implements NewspaperService {
    private final NewspaperRepo newsRepo;
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

        Newspaper newsEntity = new Newspaper(news.getTitle(), news.getDescription(), mainPhoto, photos);
        newsRepo.save(newsEntity);
        return ResponseEntity.ok(newsEntity);
    }

    @Override
    public HttpEntity<?> getAllNews() {
        List<Newspaper> newsList = newsRepo.findAllByOrderByCreatedAtDesc();

        // Group by year extracted from the description field
        Map<Integer, List<Newspaper>> groupedByYear = newsList.stream()
                .filter(news -> news.getDescription() != null && news.getDescription().length() >= 4)
                .collect(Collectors.groupingBy(news -> {
                    try {
                        // Extract year from the description (assumes format "yyyy-MM-dd")
                        return Integer.parseInt(news.getDescription().substring(0, 4));
                    } catch (NumberFormatException e) {
                        return 0; // Handle invalid format by assigning to a default year
                    }
                }));

        // Transform grouped data into the desired JSON structure
        List<YearlyNewsResponse> response = groupedByYear.entrySet().stream()
                .map(entry -> new YearlyNewsResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @Override
    public HttpEntity<?> deleteNews(Integer id) {
        Optional<Newspaper> newsOpt = newsRepo.findById(id);
        if (newsOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Newspaper news = newsOpt.get();
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
    public Newspaper getNewsById(Integer id) {
        return newsRepo.findById(id).orElseThrow();
    }

    @Override
    public HttpEntity<?> getAllNewsAll() {
        List<Newspaper> all = newsRepo.findAll();
        return ResponseEntity.ok(all);
    }
}
