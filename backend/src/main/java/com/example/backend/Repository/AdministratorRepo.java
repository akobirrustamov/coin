package com.example.backend.Repository;

import com.example.backend.Entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdministratorRepo extends JpaRepository<Administrator, Integer> {
    @Query(value = "select * from administrator where administrator.login=:login", nativeQuery = true)
    Administrator findAllByLogin(String login);
}
