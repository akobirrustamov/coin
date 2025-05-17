package com.example.backend.Repository;

import com.example.backend.Entity.TaskSpecial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskSpecialRepo extends JpaRepository<TaskSpecial,Integer> {
}
