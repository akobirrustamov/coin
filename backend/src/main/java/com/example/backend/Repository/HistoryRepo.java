package com.example.backend.Repository;

import com.example.backend.Entity.Command;
import com.example.backend.Entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepo extends JpaRepository<History,Integer> {

    List<History> findAllByCommand(Command command);
}
