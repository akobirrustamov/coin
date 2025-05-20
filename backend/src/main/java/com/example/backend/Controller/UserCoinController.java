package com.example.backend.Controller;

import com.example.backend.DTO.UserCoinDto;
import com.example.backend.Entity.TelegramUser;
import com.example.backend.Entity.UserCoin;
import com.example.backend.Repository.TelegramUserRepo;
import com.example.backend.Repository.UserCoinRepo;
import com.example.backend.Security.JwtServiceTelegramUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/app/user-coin")
@RequiredArgsConstructor
public class UserCoinController {
    private final UserCoinRepo userCoinRepo;
    private final TelegramUserRepo telegramUserRepo;
    private final JwtServiceTelegramUser jwtServiceTelegramUser;


    @GetMapping("/{token}")
    public HttpEntity<?> getUserCoin(@PathVariable String token) {

        if (!jwtServiceTelegramUser.validateToken(token)) {
            throw new RuntimeException("Token is expired or invalid");
        }
        String tgUserId = jwtServiceTelegramUser.extractSubjectFromJwt(token);
        Optional<TelegramUser> byId = telegramUserRepo.findById(UUID.fromString(tgUserId));
        if (!byId.isPresent()) {
            throw new RuntimeException("User not found");
        }
        TelegramUser telegramUser = byId.get();
//        UserCoin userCoin = userCoinRepo.getUserCoinByUserId(telegramUser.getId());

        return ResponseEntity.ok(telegramUser);
    }



    @PostMapping("/{token}")
    public UserCoin saveCoin(@PathVariable String token, @RequestBody UserCoinDto userCoin){
        if (!jwtServiceTelegramUser.validateToken(token)) {
            throw new RuntimeException("Token is expired or invalid");
        }
        String tgUserId = jwtServiceTelegramUser.extractSubjectFromJwt(token);
        Optional<TelegramUser> byId = telegramUserRepo.findById(UUID.fromString(tgUserId));
        if (!byId.isPresent()) {
            throw new RuntimeException("User not found");
        }
        TelegramUser telegramUser = byId.get();
        UserCoin coin = new UserCoin();
        coin.setAmount(userCoin.getAmount());
        coin.setTelegramUser(telegramUser);
        coin.setCreatedAt(LocalDateTime.now());
        coin.setType(userCoin.getType());
        coin.setEnergy(userCoin.getEnergy());
        telegramUser.setAvailableCoin(telegramUser.getAvailableCoin()+userCoin.getAmount());

        telegramUserRepo.save(telegramUser);
        userCoinRepo.save(coin);
        return coin;
    }








}
