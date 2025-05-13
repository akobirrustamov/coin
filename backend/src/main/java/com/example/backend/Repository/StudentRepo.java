package com.example.backend.Repository;

import com.example.backend.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepo extends JpaRepository<Student, UUID> {

    @Query(value = "SELECT * FROM students WHERE passport_pin = :passportPin", nativeQuery = true)
    Optional<Student> findByPassport_pin(@Param("passportPin") String passport_pin);

    @Query(value = "SELECT * FROM students WHERE group_name = :groupName", nativeQuery = true)
    List<Student> findAllByGroup(@Param("groupName") String groupName);


}
