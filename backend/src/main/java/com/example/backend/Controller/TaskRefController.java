package com.example.backend.Controller;

import com.example.backend.DTO.TaskRefDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.TaskRef;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.TaskRefRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-ref")
@RequiredArgsConstructor
public class TaskRefController {

    private TaskRefRepo taskRefRepo;
    private AttachmentRepo attachmentRepo;

    @GetMapping
    public HttpEntity<?> findAll(){
        List<TaskRef> all = taskRefRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PostMapping
    public HttpEntity<?> save(@RequestBody TaskRefDto taskRefDto){
        Optional<Attachment> byId = attachmentRepo.findById(taskRefDto.getMainPhoto());
        if (byId.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TaskRef taskRef = new TaskRef(byId.get(), taskRefDto.getTitle(), taskRefDto.getDescription(), taskRefDto.getCoinCount(), taskRefDto.getCountRef(), LocalDateTime.now(), true);
        TaskRef save = taskRefRepo.save(taskRef);
        return new ResponseEntity<>(save, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public HttpEntity<?> update(@PathVariable Integer id, @RequestBody TaskRefDto taskRefDto) {
        Optional<TaskRef> optionalTaskRef = taskRefRepo.findById(id);
        if (optionalTaskRef.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Attachment> optionalAttachment = attachmentRepo.findById(taskRefDto.getMainPhoto());
        if (optionalAttachment.isEmpty()) {
            return new ResponseEntity<>("Attachment not found", HttpStatus.NOT_FOUND);
        }

        TaskRef existing = optionalTaskRef.get();
        existing.setMainPhoto(optionalAttachment.get());
        existing.setTitle(taskRefDto.getTitle());
        existing.setDescription(taskRefDto.getDescription());
        existing.setCoinCount(taskRefDto.getCoinCount());
        existing.setCountRef(taskRefDto.getCountRef());
        existing.setStatus(taskRefDto.getStatus()); // Or update based on your logic

        TaskRef updated = taskRefRepo.save(existing);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

}
