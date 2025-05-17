package com.example.backend.Controller;

import com.example.backend.DTO.TaskCinemaDto;
import com.example.backend.DTO.TaskLevelDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.TaskCinema;
import com.example.backend.Entity.TaskLevel;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.TaskCinemaRepo;
import com.example.backend.Repository.TaskLevelRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-level")
@RequiredArgsConstructor
public class TaskLevelController {
    private final TaskLevelRepo taskLevelRepo;
    private final AttachmentRepo attachmentRepo;

    @GetMapping
    public HttpEntity<?> getAllTaskLevel(){
        List<TaskLevel> all = taskLevelRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }


    @PostMapping
    public HttpEntity<?> addTaskLevel(@RequestBody TaskLevelDto taskLevelDto){
        Optional<Attachment> byId = attachmentRepo.findById(taskLevelDto.getMainPhoto());
        if (byId.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TaskLevel taskLevel =   new TaskLevel(byId.get(), taskLevelDto.getTitle(), taskLevelDto.getDescription(), taskLevelDto.getSeconds(), taskLevelDto.getCoinCount(), LocalDateTime.now(), taskLevelDto.getUserLevel(), true);
        TaskLevel save = taskLevelRepo.save(taskLevel);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> updateTaskLevel(@PathVariable Integer id, @RequestBody TaskLevelDto taskLevelDto) {
        Optional<TaskLevel> optionalTaskLevel = taskLevelRepo.findById(id);
        if (optionalTaskLevel.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Attachment> optionalAttachment = attachmentRepo.findById(taskLevelDto.getMainPhoto());
        if (optionalAttachment.isEmpty()) {
            return new ResponseEntity<>("Attachment not found", HttpStatus.NOT_FOUND);
        }

        TaskLevel existing = optionalTaskLevel.get();
        existing.setMainPhoto(optionalAttachment.get());
        existing.setTitle(taskLevelDto.getTitle());
        existing.setDescription(taskLevelDto.getDescription());
        existing.setSeconds(taskLevelDto.getSeconds());
        existing.setCoinCount(taskLevelDto.getCoinCount());
        existing.setUserLevel(taskLevelDto.getUserLevel());
        existing.setStatus(taskLevelDto.getStatus()); // You may allow setting status from DTO if needed

        TaskLevel updated = taskLevelRepo.save(existing);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


}
