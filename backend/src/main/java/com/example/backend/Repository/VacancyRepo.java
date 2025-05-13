package com.example.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepo extends JpaRepository<com.example.backend.Entity.Vacancy, Integer> {
}
