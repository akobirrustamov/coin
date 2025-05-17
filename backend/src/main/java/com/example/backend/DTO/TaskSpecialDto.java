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
public class TaskSpecialDto {

    private UUID mainPhoto;
    private String title;
    private String description;
    private Integer coinCount;
    private Integer countRef;
    private Boolean status;



}
