package com.example.backend.Repository;

import com.example.backend.Entity.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionnaireRepo extends JpaRepository<Questionnaire,Integer> {
}
