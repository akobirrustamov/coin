package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
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
    private Integer energy;
    private Timestamp timestamp;
    @ManyToOne
    private TelegramUser telegramUser;
    private Integer type;

//    1 from click
//    2 from task Cinema
//    3 from task level
//    4 task ordinary
//    5 task ref
//    6 task special
//    7 daily reward
//    8 puzzle
//    9 any reward


    public UserCoin(LocalDateTime createdAt, Integer amount, TelegramUser telegramUser, Integer type, Integer energy, Timestamp timestamp) {
        this.createdAt = createdAt;
        this.amount = amount;
        this.telegramUser = telegramUser;
        this.type = type;
        this.energy = energy;
        this.timestamp = timestamp;
    }



}
