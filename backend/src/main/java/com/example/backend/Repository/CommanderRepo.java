package com.example.backend.Repository;

import com.example.backend.Entity.Commander;
import com.example.backend.Entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
public interface CommanderRepo extends JpaRepository<Commander, Integer> {

    @Query(value="Select * from commander where rank_id = :rankId", nativeQuery = true)
    List<Commander> findByRankRepo(Integer rankId);

    @Query(value = "SELECT * FROM commander WHERE rank_id = :rankId", nativeQuery = true)
    Commander findByRank( Integer rankId);
}
