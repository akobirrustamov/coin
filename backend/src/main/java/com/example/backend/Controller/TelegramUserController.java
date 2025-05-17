package com.example.backend.Controller;

import com.example.backend.Entity.TelegramUser;
import com.example.backend.Repository.TelegramUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app/telegram-user")
@RequiredArgsConstructor
public class TelegramUserController {
    private final TelegramUserRepo telegramUserRepo;

    @GetMapping
    public HttpEntity<?> findAll() {
        List<TelegramUser> all = telegramUserRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }




}
