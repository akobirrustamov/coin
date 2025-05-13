package com.example.backend.Repository;

import com.example.backend.Entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepo extends JpaRepository<News, Integer> {
    List<News> findAllByOrderByCreatedAtDesc();
}
