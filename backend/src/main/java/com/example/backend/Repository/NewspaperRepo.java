package com.example.backend.Repository;

import com.example.backend.Entity.News;
import com.example.backend.Entity.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewspaperRepo extends JpaRepository<Newspaper,Integer> {
    List<Newspaper> findAllByOrderByCreatedAtDesc();
}
