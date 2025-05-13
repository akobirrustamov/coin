package com.example.backend.Controller;

import com.example.backend.Entity.TokenHemis;
import com.example.backend.Repository.TokenHemisRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/attendance")
public class AppAttendanceController {
    private final TokenHemisRepo tokenHemisRepo;

//    private static final String FILE_PATH = System.getProperty("user.home") + "/Downloads/attendance.xlsx";
    private static final String FILE_PATH =  "./attendance.xlsx";
    private static final String SCHEDULE_API_URL = "https://student.buxpxti.uz/rest/v1/data/schedule-list?_education_year=2024&_group={groupId}&limit=200&lesson_date_from=1738386261";
    private static final String STUDENT_API_URL = "https://student.buxpxti.uz/rest/v1/data/student-list?_group={groupId}&limit=50";
    private static final String ATTENDANCE_API_URL = "https://student.buxpxti.uz/rest/v1/data/attendance-list?_group={groupId}&_subject={subjectId}&lesson_date_from={lessonDateFrom}";

    @GetMapping("/{groupId}")
    public ResponseEntity<byte[]> generateExcel(@PathVariable Integer groupId) {
        System.out.printf("Generating excel file for groupId: %d\n", groupId);
        try {
            Map<String, Object> scheduleData = fetchScheduleData(groupId);
            List<Map<String, Object>> students = fetchStudentData(groupId);
            saveExcelFile(scheduleData, students);
            File file = new File(FILE_PATH);
            byte[] contents = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(contents);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.xlsx");
            headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(contents, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private Map<String, Object> fetchScheduleData(Integer groupId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        List<TokenHemis> all = tokenHemisRepo.findAll();
        TokenHemis tokenHemis = all.get(all.size() - 1);

        headers.set("Authorization", "Bearer " + tokenHemis.getName());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                SCHEDULE_API_URL,
                HttpMethod.GET,
                entity,
                Map.class,
                groupId
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (Map<String, Object>) response.getBody().get("data");
        } else {
            throw new RuntimeException("Failed to fetch schedule data");
        }
    }

    private List<Map<String, Object>> fetchStudentData(Integer groupId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        List<TokenHemis> all = tokenHemisRepo.findAll();
        TokenHemis tokenHemis = all.get(all.size() - 1);

        headers.set("Authorization", "Bearer " + tokenHemis.getName());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                STUDENT_API_URL,
                HttpMethod.GET,
                entity,
                Map.class,
                groupId
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return (List<Map<String, Object>>) data.get("items");
        } else {
            throw new RuntimeException("Failed to fetch student data");
        }
    }

    private List<Map<String, Object>> fetchAttendanceData(Integer groupId, Integer subjectId, Long lessonDateFrom) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        List<TokenHemis> all = tokenHemisRepo.findAll();
        TokenHemis tokenHemis = all.get(all.size() - 1);

        headers.set("Authorization", "Bearer " + tokenHemis.getName());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                ATTENDANCE_API_URL,
                HttpMethod.GET,
                entity,
                Map.class,
                groupId, subjectId, lessonDateFrom
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return (List<Map<String, Object>>) data.get("items");
        } else {
            throw new RuntimeException("Failed to fetch attendance data");
        }
    }

    private void saveExcelFile(Map<String, Object> scheduleData, List<Map<String, Object>> students) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        if (scheduleData.containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) scheduleData.get("items");

            Set<String> subjects = new HashSet<>();
            for (Map<String, Object> item : items) {
                String subjectName = (String) ((Map<String, Object>) item.get("subject")).get("name");
                subjects.add(subjectName);
            }

            for (String subject : subjects) {
                Sheet sheet = workbook.createSheet(subject);
                Row headerRow = sheet.createRow(0);
                Row headerRow1 = sheet.createRow(1);

                headerRow.createCell(2).setCellValue("Sana");
                headerRow1.createCell(1).setCellValue("Talaba");
                headerRow1.createCell(2).setCellValue("Semester/Dars turi");

                int rowNum = 2; // Start adding student names from row 2 (row 0 is header)
                for (Map<String, Object> student : students) {
                    Row row = sheet.createRow(rowNum++);

                    // Set the student name in the first column
                    row.createCell(0).setCellValue(rowNum-2); // Student's full name
                    row.createCell(1).setCellValue((String) student.get("short_name")); // Student's full name

                    // Set the semester name in the second column (new column after student name)
                    String semesterName = (String) ((Map<String, Object>) student.get("semester")).get("name");
                    row.createCell(2).setCellValue(semesterName); // Add semester name to the second column
                }


                int colNum = 3; // Start at column 1 (after "Student Name")
                for (Map<String, Object> item : items) {
                    if (((Map<String, Object>) item.get("subject")).get("name").equals(subject) ) {
                        long lessonDateTimestamp = Long.parseLong(item.get("lesson_date").toString());
                        Instant instant = Instant.ofEpochSecond(lessonDateTimestamp);
                        LocalDate lessonDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                        // Create header cells
                        headerRow1.createCell(colNum).setCellValue(
                                ((Map<String, Object>) item.get("trainingType")).get("name").toString() + " " +
                                        ((Map<String, Object>) item.get("lessonPair")).get("name").toString() + "-para");
                        headerRow.createCell(colNum).setCellValue(lessonDate.toString());

                        // Fetch attendance data for this subject and date
                        Integer subjectId = (Integer) ((Map<String, Object>) item.get("subject")).get("id");

                        List<Map<String, Object>> attendanceData = fetchAttendanceData((Integer) ((Map<String, Object>) item.get("group")).get("id"), subjectId, lessonDateTimestamp);

                        // Populate attendance data in the cells
                        for (Map<String, Object> attendance : attendanceData) {
                            for (int i = 2; i < rowNum; i++) {
                                Row row = sheet.getRow(i);

                                // Get the student name from attendance data
                                String studentName = ((Map<String, Object>) attendance.get("student")).get("name").toString();
                                String _subjectId = ((Map<String, Object>) attendance.get("subject")).get("id").toString();
                                String lessonPair = ((Map<String, Object>) attendance.get("lessonPair")).get("name").toString();
                                String _lessonPair = ((Map<String, Object>) item.get("lessonPair")).get("name").toString();
                                String trainingType = ((Map<String, Object>) attendance.get("trainingType")).get("name").toString();
                                String _trainingType = ((Map<String, Object>) item.get("trainingType")).get("name").toString();
                                String lesson_date = (attendance.get("lesson_date")).toString();
                                String _lesson_date = (item.get("lesson_date")).toString();

                                if (row.getCell(1) != null && row.getCell(1).getStringCellValue().equals(studentName)  && subjectId.toString().equals(_subjectId)  && lessonPair.toString().equals(_lessonPair) && _trainingType.equals(trainingType) && _lesson_date.equals(lesson_date)) {
                                    Object absentOn = attendance.get("absent_on") != null ? attendance.get("absent_on").toString() : "N/A";
                                    Object absentOff = attendance.get("absent_off") != null ? attendance.get("absent_off").toString() : "N/A";

                                    // Ensure the cell exists before setting the value
                                    Cell cell = row.getCell(colNum);
                                    if (cell == null) {
                                        cell = row.createCell(colNum);
                                    }
                                    cell.setCellValue("NB");
                                }
                            }
                        }

                        colNum++;
                    }
                }
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }


}