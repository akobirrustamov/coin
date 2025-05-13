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
@Table(name = "nomenklatura_file")
public class NomenklaturaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private Attachment file;
    private String description;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne
    private Nomenklatura nomenklatura;
    private Integer folder;


    public NomenklaturaFile(Attachment file, String description, LocalDateTime createdAt, Nomenklatura nomenklatura, Integer folder) {
        this.file = file;
        this.description = description;
        this.createdAt = createdAt;
        this.nomenklatura = nomenklatura;
        this.folder = folder;
    }
}
