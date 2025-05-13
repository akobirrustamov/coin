package com.example.backend.Repository;

import com.example.backend.Entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface StaffRepo extends JpaRepository<Staff,Integer> {
    @Query(value="DELETE from staff where id=:staffId", nativeQuery=true)
    void deleteBySatffId(Integer staffId);





    @Query(value = "SELECT * FROM staff WHERE phone = :phone", nativeQuery = true)
    Optional<Staff> findByPhone(String phone);



}
