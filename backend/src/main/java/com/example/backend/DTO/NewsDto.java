package com.example.backend.DTO;

import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    private String title;
    private String description;
    private UUID mainPhoto;
    private List<UUID> photos = new ArrayList<>();

}
