package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "task_level")
@Entity
@Builder
public class TaskLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private Attachment mainPhoto;
    private String title;
    private String description;
    private Integer seconds;
    private Integer coinCount;
    private LocalDateTime createdAt;
    private Integer userLevel;
    private Boolean status;

    public TaskLevel(Attachment mainPhoto, String title, String description, Integer seconds, Integer coinCount, LocalDateTime createdAt, Integer userLevel, Boolean status) {
        this.mainPhoto = mainPhoto;
        this.title = title;
        this.description = description;
        this.seconds = seconds;
        this.coinCount = coinCount;
        this.createdAt = createdAt;
        this.userLevel = userLevel;
        this.status = status;
    }
}
