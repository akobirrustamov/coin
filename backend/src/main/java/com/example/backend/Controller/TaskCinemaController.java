package com.example.backend.Controller;

import com.example.backend.DTO.TaskCinemaDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.TaskCinema;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.TaskCinemaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-cinema")
@RequiredArgsConstructor
public class TaskCinemaController {

    private final TaskCinemaRepo taskCinemaRepo;
    private final AttachmentRepo attachmentRepo;


    @GetMapping
    public HttpEntity<?> getAllTaskCinema(){
        List<TaskCinema> all = taskCinemaRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }


    @PostMapping
    public HttpEntity<?> addTaskCinema(@RequestBody TaskCinemaDto taskCinema){
        Optional<Attachment> byId = attachmentRepo.findById(taskCinema.getMainPhoto());
        if (byId.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TaskCinema taskCinemaEntity = new TaskCinema(byId.get(), taskCinema.getTitle(), taskCinema.getDescription(), taskCinema.getCoinCount(), taskCinema.getSeconds(), LocalDateTime.now(), true);
        TaskCinema save = taskCinemaRepo.save(taskCinemaEntity);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public HttpEntity<?> updateTaskCinema(@PathVariable Integer id, @RequestBody TaskCinemaDto taskCinemaDto) {
        Optional<TaskCinema> optionalTaskCinema = taskCinemaRepo.findById(id);
        if (optionalTaskCinema.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Attachment> optionalAttachment = attachmentRepo.findById(taskCinemaDto.getMainPhoto());
        if (optionalAttachment.isEmpty()) {
            return new ResponseEntity<>("Attachment not found", HttpStatus.NOT_FOUND);
        }

        TaskCinema existing = optionalTaskCinema.get();
        existing.setMainPhoto(optionalAttachment.get());
        existing.setTitle(taskCinemaDto.getTitle());
        existing.setDescription(taskCinemaDto.getDescription());
        existing.setCoinCount(taskCinemaDto.getCoinCount());
        existing.setSeconds(taskCinemaDto.getSeconds());
        existing.setStatus(taskCinemaDto.getStatus()); // or update status from DTO if needed

        TaskCinema updated = taskCinemaRepo.save(existing);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

}
