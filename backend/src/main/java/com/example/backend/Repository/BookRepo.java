package com.example.backend.Repository;

import com.example.backend.Entity.Book;
import com.example.backend.Entity.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepo extends JpaRepository<Book,Integer> {
    List<Book> findAllByOrderByCreatedAtDesc();
}
