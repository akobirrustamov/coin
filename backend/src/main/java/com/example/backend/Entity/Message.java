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
@Entity
@Builder
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String phone;
    private String message;

    public Message(String name, String phone, String message) {
        this.name = name;
        this.phone = phone;
        this.message = message;
    }

    public Message(String name, String phone, String message, LocalDateTime date) {
        this.name = name;
        this.phone = phone;
        this.message = message;
        this.date = date;
    }

    private LocalDateTime date;

}
