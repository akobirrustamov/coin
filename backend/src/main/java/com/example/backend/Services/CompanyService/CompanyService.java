package com.example.backend.Services.CompanyService;

import com.example.backend.DTO.CompanyDto;
import org.springframework.http.HttpEntity;

public interface CompanyService {
    HttpEntity<?> getInfo(String accessToken);
    HttpEntity<?> addCompany(CompanyDto company);
    HttpEntity<?> updateCompany(Integer id, CompanyDto company);
    HttpEntity<?> deleteCompany(Integer id);
    HttpEntity<?> getCompanyById(Integer id);
    HttpEntity<?> getAllCompanies();
}
