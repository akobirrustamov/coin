package com.example.backend.Services.GalleryService;

import com.example.backend.DTO.GalleryDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.GalleryImage;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.GalleryImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {
    private final GalleryImageRepository galleryImageRepository;
    private final AttachmentRepo attachmentRepo;

    @Override
    public HttpEntity<?> addGalleryImage(GalleryDto galleryImage) {
        Optional<Attachment> mainPhotoOpt = attachmentRepo.findById(galleryImage.getMainPhoto());
        if (mainPhotoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Main photo not found");
        }
        Attachment mainPhoto = mainPhotoOpt.get();

        try {
            GalleryImage galleryImage1 = new GalleryImage(mainPhoto);
            GalleryImage savedGalleryImage = galleryImageRepository.save(galleryImage1);
            return ResponseEntity.ok(savedGalleryImage);
        } catch (Exception e) {
            // Log the exception
            return new ResponseEntity<>("Failed to save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public HttpEntity<?> getAllGalleryImages() {
        List<GalleryImage> galleryImages = galleryImageRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(galleryImages);
    }

    @Override
    public HttpEntity<?> getGalleryImageById(Integer id) {
        Optional<GalleryImage> galleryImage = galleryImageRepository.findById(id);
        if (galleryImage.isPresent()) {
            return ResponseEntity.ok(galleryImage.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public HttpEntity<?> deleteGalleryImage(Integer id) {
        if (galleryImageRepository.existsById(id)) {
            GalleryImage byId = galleryImageRepository.findById(id).orElseThrow();
            galleryImageRepository.deleteById(id);
            deleteAttachment(byId.getMainPhoto());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    private void deleteAttachment(Attachment attachment) {
        File file = new File("backend/files" + attachment.getPrefix() + "/" + attachment.getName());
        if (file.exists()) {
            file.delete();
        }
        attachmentRepo.deleteById(attachment.getId());
    }
}
