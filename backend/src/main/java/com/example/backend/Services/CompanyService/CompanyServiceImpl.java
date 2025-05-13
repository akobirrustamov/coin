package com.example.backend.Services.CompanyService;

import com.example.backend.DTO.CompanyDto;
import com.example.backend.Entity.Company;
import com.example.backend.Entity.Region;
import com.example.backend.Entity.User;
import com.example.backend.Entity.Vacancy;
import com.example.backend.Projection.DashboardProjection;
import com.example.backend.Repository.CompanyRepo;
import com.example.backend.Repository.RegionRepo;
import com.example.backend.Repository.VacancyRepo;
import com.example.backend.Services.AuthService.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepo companyRepo;
    private final AuthService authService;
    private final RegionRepo regionRepo;
    private final VacancyRepo vacancyRepo;

    @Override
    public HttpEntity<?> getInfo(String accessToken) {
        User decode = authService.decode(accessToken);
        DashboardProjection dashboardInfo = companyRepo.getDashboardInfo(decode.getId());
        return ResponseEntity.ok(dashboardInfo);
    }

    @Override
    public HttpEntity<?> addCompany(CompanyDto companyDto) {
        Optional<Region> region = regionRepo.findById(companyDto.getRegionId());
        Optional<Vacancy> vacancy = vacancyRepo.findById(companyDto.getVacancyId());

        if (region.isPresent() && vacancy.isPresent()) {
            Company company = new Company();
            company.setLogin(companyDto.getLogin());
            company.setPassword(companyDto.getPassword());
            company.setRegion(region.get());
            company.setName(companyDto.getName());
            company.setOwner(companyDto.getOwner());
            company.setSupportPhone(companyDto.getSupportPhone());
            company.setEmail(companyDto.getEmail());
            company.setVacancy(companyDto.getVacancy());
            company.setVacancyName(vacancy.get());

            Company savedCompany = companyRepo.save(company);
            return ResponseEntity.ok(savedCompany);
        } else {
            return ResponseEntity.badRequest().body("Invalid Region or Vacancy ID");
        }
    }

    @Override
    public HttpEntity<?> updateCompany(Integer id, CompanyDto companyDto) {
        if (companyRepo.existsById(id)) {
            Optional<Region> region = regionRepo.findById(companyDto.getRegionId());
            Optional<Vacancy> vacancy = vacancyRepo.findById(companyDto.getVacancyId());

            if (region.isPresent() && vacancy.isPresent()) {
                Company company = companyRepo.findById(id).orElseThrow();
                company.setLogin(companyDto.getLogin());
                company.setPassword(companyDto.getPassword());
                company.setRegion(region.get());
                company.setName(companyDto.getName());
                company.setOwner(companyDto.getOwner());
                company.setSupportPhone(companyDto.getSupportPhone());
                company.setEmail(companyDto.getEmail());
                company.setVacancy(companyDto.getVacancy());
                company.setVacancyName(vacancy.get());

                Company updatedCompany = companyRepo.save(company);
                return ResponseEntity.ok(updatedCompany);
            } else {
                return ResponseEntity.badRequest().body("Invalid Region or Vacancy ID");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public HttpEntity<?> deleteCompany(Integer id) {
        if (companyRepo.existsById(id)) {
            companyRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public HttpEntity<?> getCompanyById(Integer id) {
        return companyRepo.findById(id)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public HttpEntity<?> getAllCompanies() {
        List<Company> companies = companyRepo.findAll();
        return ResponseEntity.ok(companies);
    }
}
