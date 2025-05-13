package com.example.backend.Controller;

import com.example.backend.DTO.CommanderDto;
import com.example.backend.DTO.ListStaff;
import com.example.backend.Entity.Commander;
import com.example.backend.Entity.Duty;
import com.example.backend.Entity.Rank;
import com.example.backend.Entity.Staff;
import com.example.backend.Repository.CommanderRepo;
import com.example.backend.Repository.DutyRepo;
import com.example.backend.Repository.RankRepo;
import com.example.backend.Repository.StaffRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/commander")
public class AppCommanderController {

    private final CommanderRepo commanderRepo;
    private final RankRepo rankRepo;  // Assuming you have a RankRepo to get rank details
    private final StaffRepo staffRepo;
    private final DutyRepo dutyRepo;

    // Get all commanders by rankId
    @GetMapping("/{rankId}")
    public HttpEntity<?> findByRankId(@PathVariable Integer rankId) {
        List<Commander> commanders = commanderRepo.findByRankRepo(rankId);

        return ResponseEntity.ok(commanders);
    }

    // Get commander by ID
    @GetMapping("/commander/{id}")
    public HttpEntity<?> findById(@PathVariable Integer id) {
        Optional<Commander> commander = commanderRepo.findById(id);
        return commander.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new commander
    @PostMapping
    public HttpEntity<?> save(@RequestBody CommanderDto commanderDto) {
        Rank rank = rankRepo.findById(commanderDto.getRankId()).orElse(null);
        if (rank == null) {
            return ResponseEntity.badRequest().body("Rank not found");
        }

        List<Rank> ranks = rankRepo.findAllById(commanderDto.getRankIds());
        Commander commander = new Commander(rank, ranks, null); // createdAt is handled by @CreationTimestamp
        commanderRepo.save(commander);
        return ResponseEntity.ok(commander);
    }
    @PutMapping("/rank/{id}/{rankId}")
    public ResponseEntity<?> update(@PathVariable Integer id, @PathVariable Integer rankId) {
        if(id == null ){
            Commander commander = new Commander(rankRepo.findById(rankId).orElse(null), null, null);
        }
        try {
            Rank rank = rankRepo.findById(rankId)
                    .orElseThrow(() -> new RuntimeException("Rank not found with ID: " + rankId));
            Commander commander = commanderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Commander not found with ID: " + id));
            if (commander.getRanks().contains(rank)) {
                return ResponseEntity.badRequest().body("Rank already exists in the commander's ranks list.");
            }
            commander.getRanks().add(rank);
            commanderRepo.save(commander);
            return ResponseEntity.ok(commander);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating commander's ranks: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}/{rankId}")
    public HttpEntity<?> delete(@PathVariable Integer id, @PathVariable Integer rankId) {
        // Find the commander by ID
        Optional<Commander> commanderOptional = commanderRepo.findById(id);
        if (!commanderOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Commander commander = commanderOptional.get();

        // Find the rank by rankId
        Optional<Rank> rankOptional = rankRepo.findById(rankId);
        if (!rankOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Rank not found with ID: " + rankId);
        }

        Rank rank = rankOptional.get();

        // Remove the rank from the commander's ranks list
        if (commander.getRanks().contains(rank)) {
            commander.getRanks().remove(rank);
            commanderRepo.save(commander); // Save the updated commander entity
            return ResponseEntity.ok("Rank removed from commander successfully");
        } else {
            return ResponseEntity.badRequest().body("Rank is not assigned to this commander");
        }
    }


    @GetMapping("/list-staff/{rankId}")
    public HttpEntity<?> getListStaff(@PathVariable Integer rankId) {
        System.out.printf("Listing staff for rank %d\n", rankId);
        Commander commander = commanderRepo.findByRank(rankId);
//                .orElseThrow(() -> new RuntimeException("Commander not found"));
        List<ListStaff> listStaffs = new ArrayList<>();
        for (Rank rank : commander.getRanks()) {
            List<Duty> duty = dutyRepo.findByRankId(rank.getId());
            List<Staff> staff = new ArrayList<>();
            for (Duty duty1 : duty) {
                staff.add(duty1.getStaff());
            }
            ListStaff listStaff = new ListStaff(rank, staff);
            listStaffs.add(listStaff);
        }
        return ResponseEntity.ok(listStaffs);
    }


}
