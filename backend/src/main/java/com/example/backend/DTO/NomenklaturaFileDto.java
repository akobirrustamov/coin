package com.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NomenklaturaFileDto {
    private String description;
    private Integer userId;
    private Integer nomenklaturaId;
    private Integer folder;
    private UUID fileId;
}
