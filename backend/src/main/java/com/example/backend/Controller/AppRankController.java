package com.example.backend.Controller;

import com.example.backend.Entity.Rank;
import com.example.backend.Repository.RankRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/rank")
public class AppRankController {
    private final RankRepo rankRepo;
    @GetMapping
    public HttpEntity<?> getAppRank() {
        List<Rank> ranks = rankRepo.findAll();
        return ResponseEntity.ok(ranks);
    }
    @GetMapping("/{id}")
    public HttpEntity<?> getRankById(@PathVariable Integer id) {
        Optional<Rank> rank = rankRepo.findById(id);
        return ResponseEntity.ok(rank);
    }
    @PostMapping
    public HttpEntity<?> createRank(@RequestBody Rank rank) {
        Rank savedRank = rankRepo.save(rank);
        return ResponseEntity.ok(savedRank);
    }
    // PUT (update) an existing rank
    @PutMapping("/{id}")
    public HttpEntity<?> updateRank(@PathVariable Integer id, @RequestBody Rank rank) {
        Optional<Rank> existingRank = rankRepo.findById(id);
        if (existingRank.isPresent()) {
            rank.setId(id);
            Rank updatedRank = rankRepo.save(rank);
            return ResponseEntity.ok(updatedRank);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rank not found");
        }
    }
    // DELETE a rank by ID
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteRank(@PathVariable Integer id) {
        Optional<Rank> rank = rankRepo.findById(id);
        if (rank.isPresent()) {
            rankRepo.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rank not found");
        }
    }


}
