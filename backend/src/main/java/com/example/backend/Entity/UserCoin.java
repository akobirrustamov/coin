package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_coin")
@Entity
@Builder
public class UserCoin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private LocalDateTime createdAt;
    private Integer amount;
    @ManyToOne
    private TelegramUser telegramUser;


    public UserCoin(LocalDateTime createdAt, Integer amount, TelegramUser telegramUser) {
        this.createdAt = createdAt;
        this.amount = amount;
        this.telegramUser = telegramUser;
    }



}
