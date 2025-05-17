package com.example.backend.Repository;

import com.example.backend.Entity.TaskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskLevelRepo extends JpaRepository<TaskLevel,Integer> {
}
