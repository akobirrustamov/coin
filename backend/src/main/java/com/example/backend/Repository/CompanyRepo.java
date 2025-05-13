package com.example.backend.Repository;

import com.example.backend.Entity.Company;
import com.example.backend.Projection.DashboardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CompanyRepo extends JpaRepository<Company, Integer> {
    @Query(value = "SELECT support_phone as support_phone, now() as current_date_and_time FROM company WHERE company.owner_id = :id", nativeQuery = true)
    DashboardProjection getDashboardInfo(UUID id);
}
