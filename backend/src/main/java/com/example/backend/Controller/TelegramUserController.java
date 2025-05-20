package com.example.backend.Controller;

import com.example.backend.Entity.TelegramUser;
import com.example.backend.Repository.TelegramUserRepo;
import com.example.backend.Security.JwtServiceTelegramUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/app/telegram-user")
@RequiredArgsConstructor
public class TelegramUserController {
    private final TelegramUserRepo telegramUserRepo;
    private final JwtServiceTelegramUser jwtServiceTelegramUser;

    @GetMapping
    public HttpEntity<?> findAll() {
        List<TelegramUser> all = telegramUserRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }



    @PutMapping("/is-first-time/{token}")
    public HttpEntity<?> isFirstTime(@PathVariable String token) {
        if (!jwtServiceTelegramUser.validateToken(token)) {
            throw new RuntimeException("Token is expired or invalid");
        }
        String tgUserId = jwtServiceTelegramUser.extractSubjectFromJwt(token);
        Optional<TelegramUser> byId = telegramUserRepo.findById(UUID.fromString(tgUserId));
        if (!byId.isPresent()) {
            throw new RuntimeException("User not found");
        }
        TelegramUser telegramUser = byId.get();
        telegramUser.setIsFirstTime(false);
        TelegramUser save = telegramUserRepo.save(telegramUser);
        return new ResponseEntity<>(save, HttpStatus.OK);
    }


}
