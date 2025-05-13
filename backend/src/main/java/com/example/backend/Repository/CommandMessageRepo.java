package com.example.backend.Repository;

import com.example.backend.Entity.CommandMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommandMessageRepo extends JpaRepository<CommandMessage,Integer> {
    @Query(value = "select * from command_message where command_id=:commandId", nativeQuery = true)
    List<CommandMessage> findByCommandId(Integer commandId);
}
