package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    @Column(length = 3000)
    private String description;
    @ManyToOne
    private Faculty faculty;
    @OneToOne
    private Attachment mainPhoto;
    private Integer place;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Teacher(String title, String description, Attachment mainPhoto, Integer place, Faculty faculty, LocalDateTime localDateTime ) {
        this.title = title;
        this.description = description;
        this.mainPhoto = mainPhoto;
        this.place = place;
        this.faculty = faculty;
        this.createdAt = localDateTime;
    }
}
