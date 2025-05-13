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
@Table(name = "daily_control_sub_folder_content")
public class DailyControlSubFolderContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    @ManyToOne
    private DailyControlSubFolder dailyControlSubFolder;
    @ManyToOne
    private Staff staff;
    private String linkToFile;
    @ManyToOne
    private Attachment attachment;
}
