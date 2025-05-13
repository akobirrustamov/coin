package com.example.backend.DTO;

import com.example.backend.Entity.Rank;
import com.example.backend.Entity.Staff;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListStaff {
  private Rank rank;
  private List<Staff> staff;

}
