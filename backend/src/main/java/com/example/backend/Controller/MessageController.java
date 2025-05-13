package com.example.backend.Controller;

import com.example.backend.Entity.Message;
import com.example.backend.Repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageRepo messageRepo;

    @PostMapping
    public HttpEntity<?> postMessage(@RequestBody Message message) {
        Message newMessage = new Message(message.getName(), message.getPhone(), message.getMessage(), LocalDateTime.now());
        messageRepo.save(newMessage);

        return ResponseEntity.ok(newMessage);
    }
    @GetMapping
    public HttpEntity<?> getMessages() {
        return ResponseEntity.ok(messageRepo.findAll());
    }
}
