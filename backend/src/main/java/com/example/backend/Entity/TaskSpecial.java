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
@Table(name = "task_special")
@Entity
@Builder
public class TaskSpecial {
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


    public TaskSpecial(String title, Attachment mainPhoto, String description, Integer coinCount, Integer seconds, LocalDateTime createdAt, Boolean status) {
        this.title = title;
        this.mainPhoto = mainPhoto;
        this.description = description;
        this.coinCount = coinCount;
        this.seconds = seconds;
        this.createdAt = createdAt;
        this.status = status;
    }
}
