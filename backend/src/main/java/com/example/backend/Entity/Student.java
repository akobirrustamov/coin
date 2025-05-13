package com.example.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "students")
@Entity
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String first_name;
    private String second_name;
    private String third_name;
    private String image;
    private String phone;
    private String group_name;
    private String gender;
    private String specialty;
    private String studentStatus;
    private String educationForm;
    private String educationType;
    private String paymentForm;
    private String faculty;
    private String semester;
    private String address;
    private String province;
    private String level;
    private String passport_pin;
    @Column(unique = true)
    private String passport_number;
    @CreationTimestamp
    private LocalDateTime updated_at;
    public Student(String first_name, String second_name, String image, String third_name, String phone, String group_name, String gender, String specialty, String educationForm, String studentStatus, String educationType, String paymentForm, String faculty, String semester, String address, String province, String level, String passport_pin, LocalDateTime updated_at, String passport_number) {
        this.first_name = first_name;
        this.second_name = second_name;
        this.image = image;
        this.third_name = third_name;
        this.phone = phone;
        this.group_name = group_name;
        this.gender = gender;
        this.specialty = specialty;
        this.educationForm = educationForm;
        this.studentStatus = studentStatus;
        this.educationType = educationType;
        this.paymentForm = paymentForm;
        this.faculty = faculty;
        this.semester = semester;
        this.address = address;
        this.province = province;
        this.level = level;
        this.passport_pin = passport_pin;
        this.updated_at = updated_at;
        this.passport_number = passport_number;
    }



}
