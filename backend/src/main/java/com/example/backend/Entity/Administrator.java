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
@Table(name = "administrator")
public class  Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(unique = true)  // This ensures the 'login' field is unique in the database
    private String login;

    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Administrator(String name, String login, String password, LocalDateTime createdAt) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.createdAt = createdAt;
    }
}
