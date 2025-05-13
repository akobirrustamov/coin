package com.example.backend.Controller;

import com.example.backend.Entity.DailyControl;
import com.example.backend.Repository.DailyControlRepo;
import com.example.backend.Repository.DailyControlSubFolderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rector")
@RequiredArgsConstructor
public class RektorController {
    private final DailyControlRepo dailyControlRepo;
    private final DailyControlSubFolderRepo dailyControlSubFolderRepo;

    @GetMapping
    public HttpEntity<?> getAllDailyControls() {
        System.out.println(dailyControlRepo.findAll());
        List<DailyControl> dailyControls = dailyControlRepo.findAll();
        return ResponseEntity.ok(dailyControls);
    }



}
