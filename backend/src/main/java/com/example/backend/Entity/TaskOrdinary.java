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
@Table(name = "task_ordinary")
@Entity
@Builder
public class TaskOrdinary {
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
    private Boolean status;
    private String url;


    public TaskOrdinary(Attachment mainPhoto, String title, Integer seconds, String description, Integer coinCount, LocalDateTime createdAt, Boolean status, String url) {
        this.mainPhoto = mainPhoto;
        this.title = title;
        this.seconds = seconds;
        this.description = description;
        this.coinCount = coinCount;
        this.createdAt = createdAt;
        this.status = status;
        this.url = url;
    }
}
