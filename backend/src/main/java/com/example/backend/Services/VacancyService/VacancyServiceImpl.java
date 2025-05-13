package com.example.backend.Services.VacancyService;

import com.example.backend.Entity.Vacancy;
import com.example.backend.Repository.VacancyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    private final VacancyRepo vacancyRepo;

    @Override
    public List<Vacancy> getVacancy() {
        return vacancyRepo.findAll();
    }

    @Override
    public Vacancy saveVacancy(Vacancy vacancy) {
        return vacancyRepo.save(vacancy);
    }

    @Override
    public Vacancy updateVacancy(Integer id, Vacancy vacancy) {
        Optional<Vacancy> optionalVacancy = vacancyRepo.findById(id);
        if (optionalVacancy.isPresent()) {
            Vacancy existingVacancy = optionalVacancy.get();
            existingVacancy.setName(vacancy.getName());
            return vacancyRepo.save(existingVacancy);
        } else {
            throw new RuntimeException("Vacancy not found with id " + id);
        }
    }

    @Override
    public void deleteVacancy(Integer id) {
        vacancyRepo.deleteById(id);
    }
}
