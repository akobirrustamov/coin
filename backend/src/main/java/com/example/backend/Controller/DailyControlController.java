package com.example.backend.Controller;

import com.example.backend.Entity.DailyControl;
import com.example.backend.Entity.DailyControlSubFolder;
import com.example.backend.Entity.Staff;
import com.example.backend.Repository.DailyControlRepo;
import com.example.backend.Repository.DailyControlSubFolderRepo;
import com.example.backend.Repository.StaffRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/daily-control")
@RequiredArgsConstructor
public class DailyControlController {
    private final DailyControlRepo dailyControlRepo;
    private final DailyControlSubFolderRepo dailyControlSubFolderRepo;
    private final StaffRepo staffRepo;
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode("333aae7133c19eda8f7f61ce07e64281c295df67681b1ed47c9c270a488f94d0");
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String getStaffIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject();
    }

    // Get all DailyControls
    @GetMapping
    public HttpEntity<?> getAllDailyControls() {
        List<DailyControl> dailyControls = dailyControlRepo.findAll();
        return ResponseEntity.ok(dailyControls);
    }

    @GetMapping("/staff/{token}")
    public HttpEntity<?> getDailyControl(@PathVariable String token) {
        String staffId = getStaffIdFromToken(token);

        Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
        if (!staffOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<DailyControlSubFolder> byStaff = dailyControlSubFolderRepo.findByStaffId(staffOpt.get().getId());
        System.out.println(byStaff);
        return ResponseEntity.ok(byStaff);
    }


    // Get a single DailyControl by ID
    @GetMapping("/{id}")
    public HttpEntity<?> getDailyControlById(@PathVariable Integer id) {
        Optional<DailyControl> dailyControl = dailyControlRepo.findById(id);
        return dailyControl.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new DailyControl (without subfolders)
    @PostMapping
    public HttpEntity<?> createDailyControl(@RequestBody DailyControl dailyControl) {
        dailyControl.setCreatedAt(LocalDateTime.now());
        DailyControl savedControl = dailyControlRepo.save(dailyControl);
        return ResponseEntity.ok(savedControl);
    }

    // Create DailyControl with SubFolders in one request
    @PostMapping("/with-subfolders")
    public HttpEntity<?> createDailyControlWithSubFolders(
            @RequestBody DailyControlWithSubFoldersRequest request) {
        // Save DailyControl
        DailyControl dailyControl = new DailyControl();
        dailyControl.setName(request.getDailyControl().getName());
        dailyControl.setDescription(request.getDailyControl().getDescription());
        dailyControl.setCreatedAt(LocalDateTime.now());
        DailyControl savedControl = dailyControlRepo.save(dailyControl);

        // Save SubFolders
        for (SubFolderRequest subFolderRequest : request.getSubFolders()) {
            DailyControlSubFolder subFolder = new DailyControlSubFolder();
            subFolder.setName(subFolderRequest.getName());
            subFolder.setDescription(subFolderRequest.getDescription());
            subFolder.setCreatedAt(LocalDateTime.now());
            subFolder.setDailyControl(savedControl);

            // Set staff if staffId is provided
            if (subFolderRequest.getStaffId() != null) {
                staffRepo.findById(subFolderRequest.getStaffId()).ifPresent(subFolder::setStaff);
            }

            dailyControlSubFolderRepo.save(subFolder);
        }

        return ResponseEntity.ok(savedControl);
    }

    // Update DailyControl
    @PutMapping("/{id}")
    public HttpEntity<?> updateDailyControl(
            @PathVariable Integer id,
            @RequestBody DailyControl dailyControl) {
        if (!dailyControlRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dailyControl.setId(id);
        DailyControl updatedControl = dailyControlRepo.save(dailyControl);
        return ResponseEntity.ok(updatedControl);
    }

    // Delete DailyControl (will cascade delete subfolders if cascade is configured)
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteDailyControl(@PathVariable Integer id) {
        if (!dailyControlRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dailyControlRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Get all SubFolders for a DailyControl
    @GetMapping("/{dailyControlId}/subfolders")
    public HttpEntity<?> getSubFoldersByDailyControl(
            @PathVariable Integer dailyControlId) {
        List<DailyControlSubFolder> subFolders =
                dailyControlSubFolderRepo.findByDailyControlId(dailyControlId);
        return ResponseEntity.ok(subFolders);
    }

    // Get a single SubFolder
    @GetMapping("/subfolders/{id}")
    public HttpEntity<?> getSubFolderById(@PathVariable Integer id) {
        Optional<DailyControlSubFolder> subFolder = dailyControlSubFolderRepo.findById(id);
        return subFolder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new SubFolder
    @PostMapping("/{dailyControlId}/subfolders")
    public HttpEntity<?> createSubFolder(
            @PathVariable Integer dailyControlId,
            @RequestBody DailyControlSubFolder subFolder) {
        System.out.println(subFolder);
        Optional<DailyControl> dailyControl = dailyControlRepo.findById(dailyControlId);
        if (dailyControl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        subFolder.setDailyControl(dailyControl.get());
        subFolder.setCreatedAt(LocalDateTime.now());
        DailyControlSubFolder savedSubFolder = dailyControlSubFolderRepo.save(subFolder);
        return ResponseEntity.ok(savedSubFolder);
    }

    // Update SubFolder
    @PutMapping("/subfolders/{id}")
    public HttpEntity<?> updateSubFolder(
            @PathVariable Integer id,
            @RequestBody DailyControlSubFolder subFolder) {
        System.out.println(subFolder);
        if (!dailyControlSubFolderRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        subFolder.setId(id);
        DailyControlSubFolder updatedSubFolder = dailyControlSubFolderRepo.save(subFolder);
        return ResponseEntity.ok(updatedSubFolder);
    }

    // Delete SubFolder
    @DeleteMapping("/subfolders/{id}")
    public HttpEntity<?> deleteSubFolder(@PathVariable Integer id) {
        if (!dailyControlSubFolderRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dailyControlSubFolderRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Request DTOs
    public static class DailyControlWithSubFoldersRequest {
        private DailyControl dailyControl;
        private List<SubFolderRequest> subFolders;

        // getters and setters
        public DailyControl getDailyControl() {
            return dailyControl;
        }

        public void setDailyControl(DailyControl dailyControl) {
            this.dailyControl = dailyControl;
        }

        public List<SubFolderRequest> getSubFolders() {
            return subFolders;
        }

        public void setSubFolders(List<SubFolderRequest> subFolders) {
            this.subFolders = subFolders;
        }
    }

    public static class SubFolderRequest {
        private String name;
        private String description;
        private Integer staffId;

        // getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getStaffId() {
            return staffId;
        }

        public void setStaffId(Integer staffId) {
            this.staffId = staffId;
        }
    }
}