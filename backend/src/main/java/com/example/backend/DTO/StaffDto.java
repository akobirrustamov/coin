package com.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffDto {
    private String name;
    private String phone;
    private String password;
    private List<Integer> rankIds;
    private UUID file;
    private String passportPin;
    private String passportNumber;
    private String telegramId;
}
