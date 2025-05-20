package com.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCoinDto {
    private LocalDateTime createdAt;
    private Integer amount;
    private UUID userId;
    private Integer type;
    private Integer energy;
    private Timestamp timestamp;
}
