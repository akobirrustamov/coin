package com.example.backend.Repository;

import com.example.backend.Entity.TaskCinema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCinemaRepo extends JpaRepository<TaskCinema,Integer> {
}
