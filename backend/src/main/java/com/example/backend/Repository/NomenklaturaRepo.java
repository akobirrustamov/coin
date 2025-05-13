package com.example.backend.Repository;

import com.example.backend.Entity.Administrator;
import com.example.backend.Entity.Nomenklatura;
import com.example.backend.Entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NomenklaturaRepo extends JpaRepository<Nomenklatura, Integer> {

    List<Nomenklatura> findAllByUser(Staff user);

@Query(value = "select * from nomenklatura where user_id=:staffId", nativeQuery = true)
    List<Nomenklatura> findAllByUserId(Integer staffId);
}
