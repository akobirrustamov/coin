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
@Table(name = "command_message")
public class CommandMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Command command;
    private Integer statusCommand;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    private History history;
    private String message;
    @ManyToOne
    private Attachment file;
    private Integer Status;


    public CommandMessage(Command command,History history, Integer statusCommand, LocalDateTime createdAt, String message, Attachment file, Integer status) {
        this.command = command;
        this.statusCommand = statusCommand;
        this.createdAt = createdAt;
        this.message = message;
        this.file = file;
        this.Status = status;
        this.history = history;
    }
}
