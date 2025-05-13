package com.example.backend.Repository;

import com.example.backend.Entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

public interface CommandRepo extends JpaRepository<Command, Integer> {


    @Query(value = "SELECT * FROM command where command_staff_id=:staffId and status=:status", nativeQuery = true)
    List<Command> findAllByCommanderStaffAdnStatus(Integer staffId, Integer status);


    @Query(value = "SELECT * FROM command where staff_id=:staffId and status=:status", nativeQuery = true)
    List<Command> findAllByStaffAdnStatus(Integer staffId, Integer status);


    @Query(value = "SELECT COALESCE(MAX(c.number), 0) FROM command c", nativeQuery = true)
    Integer findMaxNumber();

    @Query(value = "SELECT * FROM command where staff_id=:staffId and command_staff_id=:commanderId", nativeQuery = true)
    List<Command> findAllByStaffAdnCommander(Integer commanderId, Integer staffId);


    @Query(value = "SELECT * FROM command where  command_staff_id=:id", nativeQuery = true)
    List<Command> findAllByCommanderCommands(Integer id);


    @Query(value = "SELECT * FROM command where  staff_id=:id", nativeQuery = true)
    List<Command> findAllByStaffCommands(Integer id);

}
