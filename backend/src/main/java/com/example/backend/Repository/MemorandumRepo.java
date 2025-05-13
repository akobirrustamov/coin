package com.example.backend.Repository;

import com.example.backend.Entity.Book;
import com.example.backend.Entity.Memorandum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemorandumRepo extends JpaRepository<Memorandum,Integer> {
    List<Memorandum> findAllByOrderByCreatedAtDesc();
}
