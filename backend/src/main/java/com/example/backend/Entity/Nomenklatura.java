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
@Table(name = "nomenklatura")
public class Nomenklatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    private Integer numberPackages;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    private Staff user;

    public Nomenklatura(String name, String code, Integer numberPackages, LocalDateTime createdAt, Staff user) {
        this.name = name;
        this.code = code;
        this.numberPackages = numberPackages;
        this.createdAt = createdAt;
        this.user = user;
    }
}
