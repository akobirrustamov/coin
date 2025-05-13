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
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer fromStatus;
    private Integer toStatus;
    @ManyToOne
    private Command command;
    @CreationTimestamp
    private LocalDateTime createdAt;


    public History(Integer fromStatus, Integer toStatus, Command command, LocalDateTime createdAt) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.command = command;
        this.createdAt = createdAt;
    }
}
