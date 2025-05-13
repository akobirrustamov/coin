package com.example.backend.Controller;

import com.example.backend.DTO.GalleryDto;
import com.example.backend.Entity.GalleryImage;
import com.example.backend.Services.GalleryService.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gallery")
@RequiredArgsConstructor
public class GalleryImageController {
    private final GalleryService galleryImageService;

    @PostMapping
    public HttpEntity<?> addGalleryImage(@RequestBody GalleryDto galleryImage) {
        System.out.println(galleryImage);
        System.out.println("hi");
        return galleryImageService.addGalleryImage(galleryImage);
    }

    @GetMapping
    public HttpEntity<?> getAllGalleryImages() {
        return galleryImageService.getAllGalleryImages();
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getGalleryImageById(@PathVariable Integer id) {
        return galleryImageService.getGalleryImageById(id);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteGalleryImage(@PathVariable Integer id) {
        return galleryImageService.deleteGalleryImage(id);
    }
}
