package com.example.backend.Repository;

import com.example.backend.Entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RankRepo extends JpaRepository<Rank,Integer> {
    @Query(value = "SELECT * FROM rank WHERE name = :name LIMIT 1", nativeQuery = true)
    Optional<Rank> findByName(String name);
}
