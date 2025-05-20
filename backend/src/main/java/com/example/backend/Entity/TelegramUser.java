package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "telegram_users")
@Entity
@Builder
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @Column(name = "telegram_id", unique = true, nullable = false)
    private BigInteger telegramId;

    private String username;
    private String fullName;

    @Column(columnDefinition = "TIMESTAMP DEFAULT (now() AT TIME ZONE 'Asia/Tashkent')")
    private LocalDateTime createdAt;

    @ColumnDefault("true")
    private Boolean isFirstTime;

    @ColumnDefault("0")
    private Integer availableCoin;

    @ColumnDefault("1")
    private Integer level;

    @ColumnDefault("1000")
    private Integer energy;


}