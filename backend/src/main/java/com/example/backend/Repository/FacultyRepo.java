package com.example.backend.Repository;

import com.example.backend.Entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepo extends JpaRepository<Faculty, Integer> {
}
