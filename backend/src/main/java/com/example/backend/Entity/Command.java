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
@Table(name = "command")
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;

    @ManyToOne
    private Attachment file;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(length = 1000)
    private String description;

    private LocalDateTime timeLimit;

    private Integer status;

    @ManyToOne
    private Staff commandStaff;

    @ManyToOne
    private Staff staff;

    private String responseText;

    @ManyToOne
    private Attachment responseFile;
    @Column(length = 1000)
    private String responseDescription;

    private LocalDateTime responseTime;

    private Integer ball;

    private Integer number;
    private Boolean messageSend;

    // Custom constructor, if needed
    public Command(String text, Attachment file, String description, LocalDateTime tileLimit, Integer status, Staff commandStaff, Staff staff, String responseText, Attachment responseFile, String responseDescription, LocalDateTime responseTime, Integer ball, Integer number, LocalDateTime createdAt, Boolean messageSend) {
        this.text = text;
        this.file = file;
        this.description = description;
        this.timeLimit = tileLimit;
        this.status = status;
        this.commandStaff = commandStaff;
        this.staff = staff;
        this.responseText = responseText;
        this.responseFile = responseFile;
        this.responseDescription = responseDescription;
        this.responseTime = responseTime;
        this.ball = ball;
        this.number = number;
        this.createdAt = createdAt;
        this.messageSend = messageSend;
    }
}
