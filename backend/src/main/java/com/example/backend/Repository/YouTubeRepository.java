package com.example.backend.Repository;

import com.example.backend.Entity.YouTube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeRepository extends JpaRepository<YouTube, Integer> {
    List<YouTube> findAllByOrderByCreatedAtDesc();
}
