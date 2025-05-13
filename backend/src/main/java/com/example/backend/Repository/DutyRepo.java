package com.example.backend.Repository;


import com.example.backend.Entity.Duty;
import com.example.backend.Entity.Rank;
import com.example.backend.Entity.Staff;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DutyRepo extends CrudRepository<Duty, Integer> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM duty WHERE staff_id = :staffId", nativeQuery = true)
    void deleteByStaff(Integer staffId);

   List<Duty> findByStaff(Staff staff);

   @Query(value = "SELECT * from duty where rank_id=:rank", nativeQuery = true)
   List<Duty> findByRankId (Integer rank);

}

