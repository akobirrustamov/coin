package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "youtube")
public class YouTube {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    @Column(length = 500)
    private String iframe;
    private String hashTag;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public YouTube(String title, String iframe, String hashTag) {
        this.title = title;
        this.iframe = iframe;
        this.hashTag = hashTag;
    }
}
