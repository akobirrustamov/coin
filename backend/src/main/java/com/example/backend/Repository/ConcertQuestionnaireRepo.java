package com.example.backend.Repository;

import com.example.backend.Entity.ConcertQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertQuestionnaireRepo extends JpaRepository<ConcertQuestionnaire,Integer> {
}
