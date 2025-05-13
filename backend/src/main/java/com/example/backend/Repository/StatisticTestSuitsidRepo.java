package com.example.backend.Repository;

import com.example.backend.Entity.StatisticTestSuitsid;
import com.example.backend.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StatisticTestSuitsidRepo extends JpaRepository<StatisticTestSuitsid,Integer> {

    @Query(value = "select * from  statistic_test_suitsid where student_id=:studentId", nativeQuery = true)
    Optional<StatisticTestSuitsid> findByStuentId(UUID studentId);

}
