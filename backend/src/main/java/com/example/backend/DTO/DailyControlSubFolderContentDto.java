package com.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyControlSubFolderContentDto {
    private String name;
    private String description;
    private String linkToFile;
    private UUID filePath;
    private Integer dailyControlSubFolder;

}
