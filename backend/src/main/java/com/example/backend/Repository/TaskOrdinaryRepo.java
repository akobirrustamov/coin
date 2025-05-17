package com.example.backend.Repository;

import com.example.backend.Entity.TaskOrdinary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskOrdinaryRepo extends JpaRepository<TaskOrdinary,Integer> {
}
