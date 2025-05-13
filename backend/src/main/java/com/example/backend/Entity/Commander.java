package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "commander")
public class Commander {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Rank rank;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Rank> ranks;
    @CreationTimestamp
    private LocalDateTime createdAt;


    public Commander(Rank rank, List<Rank> ranks, LocalDateTime createdAt) {
        this.rank = rank;
        this.ranks = ranks;
        this.createdAt = createdAt;
    }
}
