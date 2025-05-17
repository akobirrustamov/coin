package com.example.backend.DTO;

import com.example.backend.Entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskOrdinaryDto {

    private UUID mainPhoto;
    private String title;
    private String description;
    private Integer seconds;
    private Integer coinCount;
    private Boolean status;
    private String url;

}
