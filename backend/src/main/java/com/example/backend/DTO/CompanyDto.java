package com.example.backend.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyDto {
    private Integer id;
    private String login;
    private String password;
    private Integer regionId;
    private String name;
    private String owner;
    private String supportPhone;
    private String email;
    private Integer vacancy;
    private Integer vacancyId;




}
