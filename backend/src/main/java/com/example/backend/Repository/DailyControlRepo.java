package com.example.backend.Repository;

import com.example.backend.Entity.DailyControl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyControlRepo extends JpaRepository<DailyControl,Integer> {
}
