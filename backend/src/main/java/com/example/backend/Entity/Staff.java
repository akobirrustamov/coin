package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String password;
    private String phone;
    private Integer score;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    private Attachment file;


    @Column(unique = true)
    private String passportPin;

    @Column(unique = true)
    private String passportNumber;
    private String fcmToken;
    private String telegramId;
    public Staff(String name, String password, String phone, Integer score) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.score = score;
    }

    public Staff(String name, String password, String phone, Integer score, LocalDateTime createdAt, Attachment attachment, String passportPin, String passportNumber, String telegramId) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.score = score;
        this.createdAt = createdAt;
        this.file = attachment;
        this.passportPin = passportPin;
        this.passportNumber = passportNumber;
        this.telegramId = telegramId;
    }
}
