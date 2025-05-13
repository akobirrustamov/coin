package com.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandRequestDto {
    private String text;
    private String description;
    private Integer ball;
    private Integer commandRankId;
    private Integer commandStaffId;
    private UUID file;
    private LocalDateTime dateTime;
    private List<StaffRank> selectedStaffList;

}
