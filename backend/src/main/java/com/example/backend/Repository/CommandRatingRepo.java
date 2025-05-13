package com.example.backend.Repository;

import com.example.backend.Entity.CommandRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRatingRepo extends JpaRepository<CommandRating,Integer> {
}
