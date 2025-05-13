package com.example.backend.Controller;

import com.example.backend.DTO.NomenklaturaDto;
import com.example.backend.Entity.Administrator;
import com.example.backend.Entity.Nomenklatura;
import com.example.backend.Entity.Staff;
import com.example.backend.Repository.AdministratorRepo;
import com.example.backend.Repository.NomenklaturaRepo;
import com.example.backend.Repository.StaffRepo;
import com.example.backend.Repository.StudentRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/nomenklatura")
public class AppNomenklaturaController {
    private final NomenklaturaRepo nomenklaturaRepo;
    private final AdministratorRepo administratorRepo;
    private final StaffRepo staffRepo;
    @GetMapping
    public HttpEntity<?> getAllNomenklatura() {
        List<Nomenklatura> all = nomenklaturaRepo.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getAllNomenklatura(@PathVariable Integer id) {
        Nomenklatura n = nomenklaturaRepo.findById(id).orElseThrow();
        return ResponseEntity.ok(n);
    }

    @PostMapping("/add")
    public HttpEntity<?> addNomenklatura(@RequestBody NomenklaturaDto nn) {
        System.out.println(nn);
        Staff staff = staffRepo.findById(nn.getUser()).orElseThrow(() -> new RuntimeException("Administrator not found"));
        Nomenklatura n = new Nomenklatura(nn.getName(), nn.getCode(), nn.getNumberPackages(),LocalDateTime.now(),staff);
        nomenklaturaRepo.save(n);
        return ResponseEntity.ok(n);
    }

    // Delete Nomenklatura
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteNomenklatura(@PathVariable Integer id) {
        nomenklaturaRepo.deleteById(id);
        return ResponseEntity.ok("Nomenklatura deleted successfully");
    }




    @PutMapping("/{id}")
    public HttpEntity<?> updateNomenklatura(@PathVariable Integer id, @RequestBody NomenklaturaDto nomenklaturaDto) {
        return nomenklaturaRepo.findById(id).map(existingNomenklatura -> {
            try {
                // Update name
                existingNomenklatura.setName(nomenklaturaDto.getName());

                // Update code
                existingNomenklatura.setCode(nomenklaturaDto.getCode());

                // Update number of packages
                existingNomenklatura.setNumberPackages(nomenklaturaDto.getNumberPackages());

                // Update user
                Staff staff = staffRepo.findById(nomenklaturaDto.getUser())
                        .orElseThrow(() -> new RuntimeException("Staff not found"));
                existingNomenklatura.setUser(staff);

                // Save updated Nomenklatura
                nomenklaturaRepo.save(existingNomenklatura);

                return ResponseEntity.ok("Nomenklatura updated successfully");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error updating Nomenklatura: " + e.getMessage());
            }
        }).orElse(ResponseEntity.badRequest().body("Nomenklatura not found"));
    }
    @GetMapping("/me/{token}")
    public HttpEntity<?> getNomenklaturaByToken(@PathVariable String token) {
        try {
            String staffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            if (staffOpt.isPresent()) {
                List<Nomenklatura> allByUser = nomenklaturaRepo.findAllByUserId(Integer.parseInt(staffId));
                System.out.println(allByUser);
                return ResponseEntity.ok(allByUser);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
    private String getStaffIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject();
    }
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode("333aae7133c19eda8f7f61ce07e64281c295df67681b1ed47c9c270a488f94d0");
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
