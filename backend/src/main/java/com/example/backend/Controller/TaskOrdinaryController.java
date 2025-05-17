package com.example.backend.Controller;

import com.example.backend.DTO.TaskOrdinaryDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.TaskLevel;
import com.example.backend.Entity.TaskOrdinary;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.TaskOrdinaryRepo;
import com.example.backend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-ordinary")
@RequiredArgsConstructor
public class TaskOrdinaryController {
    private final TaskOrdinaryRepo taskOrdinaryRepo;
    private final AttachmentRepo attachmentRepo;

    @GetMapping
    public HttpEntity<?> getAllTaskOrdinary(){
        List<TaskOrdinary> all = taskOrdinaryRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }


    @PostMapping
    public HttpEntity<?> addTaskOrdinary(@RequestBody TaskOrdinaryDto taskOrdinaryDto){
        Optional<Attachment> byId = attachmentRepo.findById(taskOrdinaryDto.getMainPhoto());
        if (byId.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TaskOrdinary taskOrdinary = new TaskOrdinary(byId.get(), taskOrdinaryDto.getTitle(), taskOrdinaryDto.getSeconds(), taskOrdinaryDto.getDescription(), taskOrdinaryDto.getCoinCount(), LocalDateTime.now(), true);
        TaskOrdinary save = taskOrdinaryRepo.save(taskOrdinary);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public HttpEntity<?> updateTaskOrdinary(@PathVariable Integer id, @RequestBody TaskOrdinaryDto taskOrdinaryDto) {
        Optional<TaskOrdinary> optionalTaskOrdinary = taskOrdinaryRepo.findById(id);
        if (optionalTaskOrdinary.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Attachment> optionalAttachment = attachmentRepo.findById(taskOrdinaryDto.getMainPhoto());
        if (optionalAttachment.isEmpty()) {
            return new ResponseEntity<>("Attachment not found", HttpStatus.NOT_FOUND);
        }

        TaskOrdinary existing = optionalTaskOrdinary.get();
        existing.setMainPhoto(optionalAttachment.get());
        existing.setTitle(taskOrdinaryDto.getTitle());
        existing.setDescription(taskOrdinaryDto.getDescription());
        existing.setSeconds(taskOrdinaryDto.getSeconds());
        existing.setCoinCount(taskOrdinaryDto.getCoinCount());
        existing.setStatus(taskOrdinaryDto.getStatus()); // Set as needed

        TaskOrdinary updated = taskOrdinaryRepo.save(existing);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

}
