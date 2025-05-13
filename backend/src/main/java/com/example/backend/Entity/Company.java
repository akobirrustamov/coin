package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String login;
    private String password;
    @ManyToOne
    private Region region;
    private String name;
    private String owner;
    private String supportPhone;
    private String email;

    private Integer vacancy;
    @ManyToOne
    private Vacancy vacancyName;

    public Company(Region region, String name, String owner, String supportPhone, String email, Integer vacancy, Vacancy vacancyName, String login, String password) {
        this.region = region;
        this.name = name;
        this.owner = owner;
        this.supportPhone = supportPhone;
        this.email = email;
        this.vacancy = vacancy;
        this.vacancyName = vacancyName;
        this.login = login;
        this.password = password;
    }



}
