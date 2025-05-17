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
@Table(name = "task_ref")
@Entity
@Builder
public class TaskRef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private Attachment mainPhoto;
    private String title;
    private String description;
    private Integer coinCount;
    private Integer countRef;
    private LocalDateTime createdAt;
    private Boolean status;

    public TaskRef(Attachment mainPhoto, String title, String description, Integer coinCount, Integer countRef, LocalDateTime createdAt, Boolean status) {
        this.mainPhoto = mainPhoto;
        this.title = title;
        this.description = description;
        this.coinCount = coinCount;
        this.countRef = countRef;
        this.createdAt = createdAt;
        this.status = status;
    }
}
