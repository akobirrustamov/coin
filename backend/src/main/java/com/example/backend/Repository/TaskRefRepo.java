package com.example.backend.Repository;

import com.example.backend.Entity.TaskRef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRefRepo extends JpaRepository<TaskRef,Integer> {
}
