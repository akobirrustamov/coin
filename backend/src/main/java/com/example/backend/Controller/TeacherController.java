package com.example.backend.Controller;

import com.example.backend.DTO.NewsDto;
import com.example.backend.DTO.TeacherDto;
import com.example.backend.Entity.Attachment;
import com.example.backend.Entity.Faculty;
import com.example.backend.Entity.Teacher;
import com.example.backend.Repository.AttachmentRepo;
import com.example.backend.Repository.FacultyRepo;
import com.example.backend.Repository.TeacherRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final FacultyRepo facultyRepo;
    private final TeacherRepo teacherRepo;
    private final AttachmentRepo attachmentRepo;

    @GetMapping
    public HttpEntity<?> getAllTeachers() {
        List<Teacher> all = teacherRepo.findAllByOrderByPlaceAsc();  // Sort all teachers by place
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{facultyId}")
    public HttpEntity<?> getFacultyTeacher(@PathVariable Integer facultyId) {
        List<Teacher> all = teacherRepo.findAllByFacultyIdOrderByPlaceAsc(facultyId);  // Sort by place
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public HttpEntity<?> addTeacher(@RequestBody TeacherDto teacherDto) {
        System.out.println(teacherDto);

        // Find the main photo (attachment)
        Attachment byId = attachmentRepo.findById(teacherDto.getMainPhoto())
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Retrieve all teachers by faculty and order by place
        List<Teacher> sortedTeachers = teacherRepo.findAllByFacultyIdOrderByPlaceAsc(teacherDto.getFaculty());

        // Determine the last place among the current teachers
        int lastPlace = sortedTeachers.isEmpty() ? 0 : sortedTeachers.get(sortedTeachers.size() - 1).getPlace();

        // If the new teacher's place is greater than the last place, assign the next available place
        int newTeacherPlace = teacherDto.getPlace();
        if (newTeacherPlace > lastPlace + 1) {
            newTeacherPlace = lastPlace + 1;
        }

        // Adjust places for existing teachers if needed
        for (Teacher teacher : sortedTeachers) {
            if (teacher.getPlace() >= newTeacherPlace) {
                teacher.setPlace(teacher.getPlace() + 1);
                teacherRepo.save(teacher); // Update teacher place
            }
        }

        // Retrieve the faculty
        Faculty faculty = facultyRepo.findById(teacherDto.getFaculty()).orElseThrow();

        // Save the new teacher at the specified or adjusted place
        Teacher newTeacher = new Teacher(teacherDto.getTitle(), teacherDto.getDescription(), byId, newTeacherPlace, faculty, LocalDateTime.now());
        teacherRepo.save(newTeacher);

        // Return updated list of teachers by faculty
        sortedTeachers = teacherRepo.findAllByFacultyIdOrderByPlaceAsc(teacherDto.getFaculty());
        return ResponseEntity.ok(sortedTeachers);
    }

    @PutMapping("/{teacherId}")
    public HttpEntity<?> updateTeacher(@PathVariable Integer teacherId, @RequestBody TeacherDto teacherDto) {
        // Fetch the teacher by ID
        System.out.println(teacherDto);
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Attachment byId = null;
        if(teacherDto.getMainPhoto()==null){
             byId = teacher.getMainPhoto();
        }else{
            // Fetch the main photo (attachment)
             byId = attachmentRepo.findById(teacherDto.getMainPhoto())
                    .orElseThrow(() -> new RuntimeException("Attachment not found"));

        }


        // Fetch the faculty by ID
        Faculty faculty = facultyRepo.findById(teacherDto.getFaculty())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        // Get the list of teachers sorted by place for the same faculty
        List<Teacher> sortedTeachers = teacherRepo.findAllByFacultyIdOrderByPlaceAsc(teacherDto.getFaculty());

        // Determine the last place among the current teachers
        int lastPlace = sortedTeachers.isEmpty() ? 0 : sortedTeachers.get(sortedTeachers.size() - 1).getPlace();

        // If the new place is greater than the last place, assign the next available place
        int newTeacherPlace = teacherDto.getPlace();
        if (newTeacherPlace > lastPlace + 1) {
            newTeacherPlace = lastPlace + 1;
        }

        // If the teacher's place is changed, adjust the places for the other teachers
        if (teacher.getPlace() != newTeacherPlace) {
            for (Teacher t : sortedTeachers) {
                if (t.getId() != teacherId && t.getPlace() >= newTeacherPlace) {
                    t.setPlace(t.getPlace() + 1);
                    teacherRepo.save(t);  // Update place for other teachers
                }
            }
        }

        // Update the teacher fields
        teacher.setTitle(teacherDto.getTitle());
        teacher.setDescription(teacherDto.getDescription());
        teacher.setMainPhoto(byId);
        teacher.setPlace(newTeacherPlace);
        teacher.setFaculty(faculty);  // Update the faculty

        teacherRepo.save(teacher);

        // Return the updated list of teachers for the faculty
        sortedTeachers = teacherRepo.findAllByFacultyIdOrderByPlaceAsc(teacherDto.getFaculty());
        return ResponseEntity.ok(sortedTeachers);
    }

    @DeleteMapping("/{teacherId}")
    public HttpEntity<?> deleteTeacher(@PathVariable Integer teacherId) {
        // Fetch the teacher by ID
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Fetch the faculty and sorted teachers for the same faculty
        List<Teacher> sortedTeachers = teacherRepo.findAllByFacultyIdOrderByPlaceAsc(teacher.getFaculty().getId());

        // Delete the teacher
        teacherRepo.deleteById(teacherId);

        // Adjust the places for the remaining teachers
        int deletedPlace = teacher.getPlace();
        for (Teacher t : sortedTeachers) {
            if (t.getPlace() > deletedPlace) {
                t.setPlace(t.getPlace() - 1);
                teacherRepo.save(t);  // Update place and time for other teachers
            }
        }

        // Return the updated list of teachers for the faculty
        sortedTeachers = teacherRepo.findAllByFacultyIdOrderByPlaceAsc(teacher.getFaculty().getId());
        return ResponseEntity.ok(sortedTeachers);
    }




}
