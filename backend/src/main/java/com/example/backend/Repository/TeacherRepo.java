package com.example.backend.Repository;

import com.example.backend.Entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TeacherRepo extends JpaRepository<Teacher, Integer> {

    // Find all teachers ordered by 'place'
    @Query(value = "SELECT * FROM teachers ORDER BY place ASC", nativeQuery = true)
    List<Teacher> findAllByOrderByPlaceAsc();

    // Find all teachers by faculty ID and order by 'place'
    @Query(value = "SELECT * FROM teachers WHERE faculty_id = :facultyId ORDER BY place ASC", nativeQuery = true)
    List<Teacher> findAllByFacultyIdOrderByPlaceAsc(@Param("facultyId") Integer facultyId);
}
