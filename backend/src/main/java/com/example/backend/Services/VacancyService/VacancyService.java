package com.example.backend.Services.VacancyService;

import com.example.backend.Entity.Vacancy;
import org.springframework.http.HttpEntity;

import java.util.List;

public interface VacancyService {

    List<Vacancy> getVacancy();

    Vacancy saveVacancy(Vacancy vacancy);

    Vacancy updateVacancy(Integer id, Vacancy vacancy);

    void deleteVacancy(Integer id);
}
