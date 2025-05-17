package com.example.backend.Controller;

import com.example.backend.DTO.TaskSpecialDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.TaskSpecial;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.TaskSpecialRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-special")
@RequiredArgsConstructor
public class TaskSpecialController {

    private final TaskSpecialRepo taskSpecialRepo;
    private final AttachmentRepo attachmentRepo;

    @GetMapping
    public HttpEntity<?> getAllTaskSpecial() {
        List<TaskSpecial> all = taskSpecialRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PostMapping
    public HttpEntity<?> addTaskSpecial(@RequestBody TaskSpecialDto taskSpecialDto) {
        Optional<Attachment> byId = attachmentRepo.findById(taskSpecialDto.getMainPhoto());
        if (byId.isEmpty()) {
            return new ResponseEntity<>("Attachment not found", HttpStatus.NOT_FOUND);
        }

        TaskSpecial taskSpecial = new TaskSpecial(
                taskSpecialDto.getTitle(),
                byId.get(),
                taskSpecialDto.getDescription(),
                taskSpecialDto.getCoinCount(),
                taskSpecialDto.getCountRef(), // Use countRef as seconds
                LocalDateTime.now(),
                true
        );

        TaskSpecial saved = taskSpecialRepo.save(taskSpecial);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> updateTaskSpecial(@PathVariable Integer id, @RequestBody TaskSpecialDto taskSpecialDto) {
        Optional<TaskSpecial> optionalTaskSpecial = taskSpecialRepo.findById(id);
        if (optionalTaskSpecial.isEmpty()) {
            return new ResponseEntity<>("TaskSpecial not found", HttpStatus.NOT_FOUND);
        }

        Optional<Attachment> optionalAttachment = attachmentRepo.findById(taskSpecialDto.getMainPhoto());
        if (optionalAttachment.isEmpty()) {
            return new ResponseEntity<>("Attachment not found", HttpStatus.NOT_FOUND);
        }

        TaskSpecial existing = optionalTaskSpecial.get();
        existing.setTitle(taskSpecialDto.getTitle());
        existing.setMainPhoto(optionalAttachment.get());
        existing.setDescription(taskSpecialDto.getDescription());
        existing.setCoinCount(taskSpecialDto.getCoinCount());
        existing.setSeconds(taskSpecialDto.getCountRef()); // or add `seconds` in DTO
        existing.setStatus(taskSpecialDto.getStatus()); // or from DTO

        TaskSpecial updated = taskSpecialRepo.save(existing);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
