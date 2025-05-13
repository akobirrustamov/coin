package com.example.backend.Repository;

import com.example.backend.Entity.NomenklaturaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NomenklaturaFileRepo extends JpaRepository<NomenklaturaFile, Integer> {

    @Query(value = "select * from nomenklatura_file where nomenklatura_id=:nomenklaturaId", nativeQuery = true)
    List<NomenklaturaFile> finAllByNomenklaturaId(Integer nomenklaturaId);

    @Query(value = "select * from nomenklatura_file where nomenklatura_id=:nomenklaturaId and  folder=:folder", nativeQuery = true)
    List<NomenklaturaFile> finAllByNomenklaturaIdAndFolder(Integer nomenklaturaId, Integer folder);
}
