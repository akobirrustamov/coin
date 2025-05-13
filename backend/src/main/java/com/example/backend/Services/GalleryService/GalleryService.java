package com.example.backend.Services.GalleryService;

import com.example.backend.DTO.GalleryDto;
import org.springframework.http.HttpEntity;

public interface GalleryService {
    HttpEntity<?> addGalleryImage(GalleryDto galleryImage);

    HttpEntity<?> getAllGalleryImages();

    HttpEntity<?> getGalleryImageById(Integer id);

    HttpEntity<?> deleteGalleryImage(Integer id);
}
