package com.example.backend.DTO;

import com.example.backend.Entity.Newspaper;

import java.util.List;

public class YearlyNewsResponse {
    private Integer year;
    private List<Newspaper> newspapers;

    public YearlyNewsResponse(Integer year, List<Newspaper> newspapers) {
        this.year = year;
        this.newspapers = newspapers;
    }

    // Getters and setters
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public List<Newspaper> getNewspapers() { return newspapers; }
    public void setNewspapers(List<Newspaper> newspapers) { this.newspapers = newspapers; }
}
