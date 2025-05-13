package com.example.backend.Controller;

import com.example.backend.Entity.Faculty;
import com.example.backend.Repository.FacultyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faculty")
@RequiredArgsConstructor
public class FacultyController {
    private final FacultyRepo facultyRepo;
    @GetMapping
    public HttpEntity<?> getFaculty(){
        List<Faculty> faculties = facultyRepo.findAll();
        return ResponseEntity.ok(faculties);
    }
}
