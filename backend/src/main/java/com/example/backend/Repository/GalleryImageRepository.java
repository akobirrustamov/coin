package com.example.backend.Repository;

import com.example.backend.Entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryImageRepository extends JpaRepository<GalleryImage, Integer> {
    List<GalleryImage> findAllByOrderByCreatedAtDesc();
}
