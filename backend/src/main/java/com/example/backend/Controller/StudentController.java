package com.example.backend.Controller;


import com.example.backend.DTO.ReqStudentLogin;
import com.example.backend.Entity.Role;
import com.example.backend.Entity.Student;
import com.example.backend.Enums.UserRoles;
import com.example.backend.Repository.RoleRepo;
import com.example.backend.Repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;


@RestController
@RequestMapping("/api/v1/app/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentRepo studentRepo;
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final String baseUrl = "https://student.buxpxti.uz/rest";
    private final RoleRepo roleRepo;



    @PostMapping("/login")
    public HttpEntity<?> loginStudent(@RequestBody ReqStudentLogin studentLogin) {
        System.out.println(studentLogin);
        try {
            RestTemplate restTemplate = new RestTemplate();
            String loginUrl = baseUrl + "/v1/auth/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> loginRequest = Map.of(
                    "login", studentLogin.getLogin(),
                    "password", studentLogin.getPassword()
            );

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(loginRequest, headers);
            ResponseEntity<Map> loginResponse = restTemplate.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (loginResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = loginResponse.getBody();
                if (responseBody == null || !responseBody.containsKey("data")) {
                    return new ResponseEntity<>("Data not found in login response", HttpStatus.BAD_REQUEST);
                }

                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data == null || !data.containsKey("token")) {
                    return new ResponseEntity<>("Token not found in data", HttpStatus.BAD_REQUEST);
                }

                // Extract token and process student data
                String token = (String) data.get("token");
                getStudentByToken(token); // Save student data to the database

                Role byName = roleRepo.findByName(UserRoles.ROLE_STUDENT);
                return ResponseEntity.ok(Map.of("token", token, "role", byName.getName()));
            } else {
                return new ResponseEntity<>("Invalid login credentials", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error during student login: ", e);
            return new ResponseEntity<>("Error occurred during login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private Student saveStudentData(Map<String, Object> data) {
        try {
            Student student = Student.builder()
                    .first_name((String) data.get("first_name"))
                    .second_name((String) data.get("second_name"))
                    .third_name((String) data.get("third_name"))
                    .image((String) data.get("image"))
                    .phone((String) data.get("phone"))
                    .group_name(((Map<String, Object>) data.get("group")).get("name").toString())
                    .gender(((Map<String, Object>) data.get("gender")).get("name").toString())
                    .specialty(((Map<String, Object>) data.get("specialty")).get("name").toString())
                    .studentStatus(((Map<String, Object>) data.get("studentStatus")).get("name").toString())
                    .educationForm(((Map<String, Object>) data.get("educationForm")).get("name").toString())
                    .educationType(((Map<String, Object>) data.get("educationType")).get("name").toString())
                    .paymentForm(((Map<String, Object>) data.get("paymentForm")).get("name").toString())
                    .faculty(((Map<String, Object>) data.get("faculty")).get("name").toString())
                    .semester(((Map<String, Object>) data.get("semester")).get("name").toString())
                    .address((String) data.get("address"))
                    .province(((Map<String, Object>) data.get("province")).get("name").toString())
                    .level(((Map<String, Object>) data.get("level")).get("name").toString())
                    .passport_pin((String) data.get("passport_pin"))
                    .passport_number((String) data.get("passport_number"))
                    .updated_at(LocalDateTime.now()) // Set current timestamp
                    .build();

//            Student save = studentRepo.save(student);
//            logger.info("Student data saved successfully: {}", student);
//            return save;

            try {
                Student savedStudent = studentRepo.save(student);
                logger.info("Student saved successfully: {}", savedStudent);
                return savedStudent;
            } catch (Exception saveEx) {
                Optional<Student> byPassportPin = studentRepo.findByPassport_pin(student.getPassport_pin());
                if (byPassportPin.isPresent()) {
                    return byPassportPin.get();
                }
                logger.error("❌ Error during studentRepo.save: {}", saveEx.getMessage(), saveEx);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error saving student data: ", e);
            return null;
        }
    }

    @GetMapping("/group/{groupName}")
    public HttpEntity<?> getStudentsByGroup(@PathVariable String groupName) {
        List<Student> studentsInGroup = studentRepo.findAllByGroup(groupName);
        return ResponseEntity.ok(studentsInGroup);
    }

    @GetMapping("/{passportPin}")
    public HttpEntity<?> getStudentByPassportPin(@PathVariable String passportPin) {
        Student student = studentRepo.findByPassport_pin(passportPin)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(student);
    }

    @GetMapping("/account/{token}")
    public HttpEntity<?> getStudentByToken(@PathVariable String token) {
        System.out.println("Token received: " + token);
        try {
            RestTemplate restTemplate = new RestTemplate();
            String externalApiUrl = baseUrl + "/v1/account/me";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody == null || !responseBody.containsKey("data")) {
                    return new ResponseEntity<>("Invalid API response", HttpStatus.BAD_REQUEST);
                }

                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                String passportNumber = (String) data.get("passport_number");

                // ✅ Try to find the student in the DB
                Optional<Student> optionalStudent = studentRepo.findByPassport_pin(passportNumber);
                if (optionalStudent.isPresent()) {
                    logger.info("Student already exists in DB: {}", optionalStudent.get());
                    return ResponseEntity.ok(optionalStudent.get());
                }

                // ❗If not exists, create and save new student
                Student newStudent = saveStudentData(data);
                if (newStudent == null) {
                    return new ResponseEntity<>("Error saving student", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return ResponseEntity.ok(newStudent);
            } else {
                return new ResponseEntity<>("Failed to fetch student data", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error fetching student data by token: ", e);
            return new ResponseEntity<>("Error occurred while fetching student data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/debt/{token}")
    public HttpEntity<?> getDebtOfStudent(@PathVariable String token) {
        try {
            System.out.println(token);
            RestTemplate restTemplate = new RestTemplate();
            String externalApiUrl = "https://student.bmti.uz/rest/v1/education/subject-list";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                Object data = responseBody.get("data");

                if (data instanceof List) {
                    List<?> dataList = (List<?>) data;

                    // Filter and transform the list based on the required criteria
                    List<Map<String, Object>> filteredResults = dataList.stream()
                            .filter(item -> {
                                Map<String, Object> overallScore = (Map<String, Object>) ((Map<String, Object>) item).get("overallScore");
                                return overallScore != null &&
                                        (int) overallScore.get("grade") < 0.6 * (int) overallScore.get("max_ball");
                            })

                            .map(item -> {
                                Map<String, Object> curriculumSubject = (Map<String, Object>) ((Map<String, Object>) item).get("curriculumSubject");
                                Map<String, Object> subject = (Map<String, Object>) curriculumSubject.get("subject");
                                Map<String, Object> overallScore = (Map<String, Object>) ((Map<String, Object>) item).get("overallScore");

                                Map<String, Object> result = Map.of(
                                        "_semester", ((Map<String, Object>) item).get("_semester"),
                                        "credit", curriculumSubject.get("credit"),
                                        "total_acload", curriculumSubject.get("total_acload"),
                                        "subjectName", subject.get("name"),
                                        "overallScore", Map.of(
                                                "grade", overallScore.get("grade"),
                                                "max_ball", overallScore.get("max_ball"),
                                                "percent", overallScore.get("percent"),
                                                "label", overallScore.get("label")
                                        )
                                );
                                return result;
                            })
                            .toList();

                    return ResponseEntity.ok(filteredResults);
                } else {
                    return new ResponseEntity<>("Unexpected data format", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Failed to fetch student data", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Error fetching student data by token: ", e);
            return new ResponseEntity<>("Error occurred while fetching student data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/contract/{passportPin}/{level}")
    public HttpEntity<?> getContractByPassportPin(@PathVariable String passportPin, @PathVariable String level) {
//        String filePath = "/Users/akobirrustamov/Desktop/contract.xlsx"; // Path to your Excel file
        String filePath = "./contract.xlsx"; // Path to your Excel file
        String sheetName = level; // Sheet name based on the level
        logger.info("Retrieving contract data for Passport Pin: {} at Level: {}", passportPin, level);

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName); // Get the sheet by level name
            if (sheet == null) {
                logger.error("Sheet not found: {}", sheetName);
                return new ResponseEntity<>("Sheet not found: " + sheetName, HttpStatus.BAD_REQUEST);
            }
            for (Row row : sheet) {
                String cellValue = getCellValue(row.getCell(2)).toString(); // Retrieve value from the 3rd column (passport pin)
                if (cellValue.equals(passportPin)) {
                    System.out.println(row.getCell(26).getNumericCellValue());
                    System.out.println(row.getCell(27).getNumericCellValue());
                    System.out.println(row.getCell(28).getNumericCellValue());
                    DecimalFormat decimalFormat = new DecimalFormat("#");

                    Map<String, Object> data = new HashMap<>();
                    data.put("kontrakt", decimalFormat.format(row.getCell(26).getNumericCellValue()));
                    data.put("tolov", decimalFormat.format(row.getCell(27).getNumericCellValue()));
                    data.put("qarzi", decimalFormat.format(row.getCell(28).getNumericCellValue()));
                    data.put("ortiqcha", decimalFormat.format(row.getCell(29).getNumericCellValue()));
                    return ResponseEntity.ok(data);

                }
            }
            logger.error("Passport Pin {} not found in the contract sheet", passportPin);
            return new ResponseEntity<>("Passport PIN not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error occurred while reading contract data: ", e);
            return new ResponseEntity<>("Error occurred while reading contract data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Check if the cell contains a date or a number
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue(); // Return date if formatted as a date
                } else {
                    // For large numbers, convert them to String to avoid scientific notation
                    return String.format("%.0f", cell.getNumericCellValue());
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return ""; // Return empty string for unknown cell types
        }
    }
}
