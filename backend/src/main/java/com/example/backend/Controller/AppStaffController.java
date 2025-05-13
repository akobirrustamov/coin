package com.example.backend.Controller;

import com.example.backend.DTO.CommandRequestEditDto;
import com.example.backend.DTO.ReqStudentLogin;
import com.example.backend.DTO.StaffDto;
import com.example.backend.Entity.*;
import com.example.backend.Repository.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.PanelUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/staff")
public class AppStaffController {

    private final StaffRepo staffRepo;
    private final DutyRepo dutyRepo;
    private final CommanderRepo commanderRepo;
    private final RankRepo rankRepo;
    private final PasswordEncoder passwordEncoder;
    private final CommandRepo commandRepo;
    private final AttachmentRepo attachmentRepo;
    private final HistoryRepo historyRepo;
    private final CommandRatingRepo commandRatingRepo;
    private final CommandMessageRepo commandMessageRepo;

    @GetMapping
    public HttpEntity<?> getAllStaff() {
        List<Staff> staff = staffRepo.findAll();
        return ResponseEntity.ok(staff);
    }
    @GetMapping("/duties")
    public HttpEntity<?> getAllDuties() {
        List<Duty> duties = new ArrayList<>();
        dutyRepo.findAll().forEach(duties::add);
        return ResponseEntity.ok(duties);
    }



    // Save new duty
    @PostMapping("/duty")
    public HttpEntity<?> saveDuty(@RequestBody Duty duty) {
        Duty savedDuty = dutyRepo.save(duty);
        return new ResponseEntity<>(savedDuty, HttpStatus.CREATED);
    }


    @DeleteMapping("/{staffId}")
    public HttpEntity<?> deleteStaff(@PathVariable Integer staffId) {
        // First, delete all duties associated with the staff
        dutyRepo.deleteByStaff(staffId);

        // Then, delete the staff itself
        staffRepo.deleteBySatffId(staffId);
        return ResponseEntity.ok("Deleted");
    }


    // Edit duty
    @PutMapping("/duty/{dutyId}")
    public HttpEntity<?> editDuty(@PathVariable Integer dutyId, @RequestBody Duty duty) {
        Optional<Duty> existingDutyOpt = dutyRepo.findById(dutyId);
        if (existingDutyOpt.isPresent()) {
            Duty existingDuty = existingDutyOpt.get();
            existingDuty.setRank(duty.getRank());
            existingDuty.setStaff(duty.getStaff()); // Updating staff if needed
            Duty updatedDuty = dutyRepo.save(existingDuty);
            return ResponseEntity.ok(updatedDuty);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete duty
    @DeleteMapping("/duty/{dutyId}")
    public HttpEntity<?> deleteDuty(@PathVariable Integer dutyId) {
        Optional<Duty> existingDutyOpt = dutyRepo.findById(dutyId);
        if (existingDutyOpt.isPresent()) {
            dutyRepo.delete(existingDutyOpt.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode("333aae7133c19eda8f7f61ce07e64281c295df67681b1ed47c9c270a488f94d0");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @PostMapping
    public HttpEntity<?> saveStaff(@RequestBody StaffDto staffDto) {
        String encodedPassword = passwordEncoder.encode(staffDto.getPassword());
        Attachment attachment = null;
        if(staffDto.getFile()!=null){
            attachment = attachmentRepo.findById(staffDto.getFile()).orElse(null);
        }
        Staff staff = new Staff(staffDto.getName(), encodedPassword, staffDto.getPhone(), 100,LocalDateTime.now(),attachment, staffDto.getPassportPin(), staffDto.getPassportNumber(), staffDto.getTelegramId());
        Staff savedStaff = staffRepo.save(staff);

        if (staffDto.getRankIds() != null && !staffDto.getRankIds().isEmpty()){
            staffDto.getRankIds().forEach(rankId -> {
                Duty duty = new Duty();
                duty.setStaff(savedStaff);
                duty.setRank(rankRepo.findById(rankId).orElse(null)); // Handle missing rank properly
                dutyRepo.save(duty);
            });
        }

        return new ResponseEntity<>(savedStaff, HttpStatus.CREATED);
    }

    // Edit staff (PUT)
    @PutMapping("/{staffId}")
    public HttpEntity<?> editStaff(@PathVariable Integer staffId, @RequestBody StaffDto staffDto) {
        Optional<Staff> existingStaffOpt = staffRepo.findById(staffId);
        if (existingStaffOpt.isPresent()) {
            Staff existingStaff = existingStaffOpt.get();

            if (staffDto.getPassword() != null && !staffDto.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(staffDto.getPassword());
                existingStaff.setPassword(encodedPassword);
            }
            Attachment attachment =existingStaff.getFile();
            if (staffDto.getFile()!=null){
                attachment = attachmentRepo.findById(staffDto.getFile()).orElse(null);

            }
            existingStaff.setName(staffDto.getName());
            existingStaff.setPhone(staffDto.getPhone());
            existingStaff.setFile(attachment);
            existingStaff.setPassportPin(staffDto.getPassportPin());
            existingStaff.setPassportNumber(staffDto.getPassportNumber());
            existingStaff.setTelegramId(staffDto.getTelegramId());

            Staff updatedStaff = staffRepo.save(existingStaff);
            dutyRepo.deleteByStaff(existingStaff.getId());  // Assuming you have a method to delete duties for a staff member
            if (staffDto.getRankIds() != null && !staffDto.getRankIds().isEmpty()) {
                staffDto.getRankIds().forEach(rankId -> {
                    Duty duty = new Duty();
                    duty.setStaff(updatedStaff);
                    duty.setRank(rankRepo.findById(rankId).orElse(null));
                    dutyRepo.save(duty);
                });
            }

            return ResponseEntity.ok(updatedStaff);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }




    @PostMapping("/login")
    public HttpEntity<?> loginStaff(@RequestBody ReqStudentLogin staffDto) {
//        System.out.println(staffDto);
        Optional<Staff> staffOpt = staffRepo.findByPhone(staffDto.getLogin());
        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            if (passwordEncoder.matches(staffDto.getPassword(), staff.getPassword())) {
                String jwtToken = generateJwtToken(staff);
                staff.setPassword(null);  // Remove password from the response
                System.out.println(staffDto);

                return ResponseEntity.ok(Map.of(
                        "role", "ROLE_STAFF",
                        "token", jwtToken
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
        }
    }


    // Get staff details by JWT token (Authenticated)
    @PostMapping("/password/{token}")
    public ResponseEntity<?> setStaffPasswordToken(@PathVariable String token, @RequestBody Map<String, String> payload) {
        try {
            // Extract staff ID from the token
            String staffId = getStaffIdFromToken(token);
            String password = payload.get("password");
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }

            // Find the staff by ID
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();

                // Hash the new password
                String hashedPassword = passwordEncoder.encode(password);

                staff.setPassword(hashedPassword);

                staffRepo.save(staff);

                // Remove sensitive data (password) before returning the response
                staff.setPassword(null);

                return ResponseEntity.ok(staff);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/phone/{token}")
    public ResponseEntity<?> setStaffPhoneToken(@PathVariable String token, @RequestBody Map<String, String> payload) {
        System.out.println(payload);
        try {
            System.out.println(payload);
            String staffId = getStaffIdFromToken(token);
            String password = payload.get("phone");
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();


                // Set the hashed password
                staff.setTelegramId(password);

                staffRepo.save(staff);

                // Remove sensitive data (password) before returning the response
                staff.setPassword(null);

                return ResponseEntity.ok(staff);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }



    @GetMapping("/me/{token}")
    public HttpEntity<?> getStaffByToken(@PathVariable String token) {
        try {
            String staffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                System.out.println(staff);
                staff.setPassword(null);  // Remove password from the response
                return ResponseEntity.ok(staff);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    @GetMapping("/rank/{token}")
    public HttpEntity<?> getDutyByToken(@PathVariable String token) {
        try {
            String staffId = getStaffIdFromToken(token);

            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            List<Duty> duties =dutyRepo.findByStaff(staffOpt.get()); // Using the native query
            List<Rank> ranks = new ArrayList<>();
            duties.forEach(duty -> {
                ranks.add(duty.getRank());
            });
            return ResponseEntity.ok(ranks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    @GetMapping("/commander/{token}")
    public HttpEntity<?> getCommanderByToken(@PathVariable String token) {
        try {
            String staffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));

            if (staffOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            List<Duty> duties = dutyRepo.findByStaff(staffOpt.get());
            List<Commander> commanders = new ArrayList<>();

            duties.forEach(duty -> {
                Rank rank = duty.getRank();
                if (rank != null) {
                    Commander commander = commanderRepo.findByRank(rank.getId());
                    if (commander != null) {
                        commanders.add(commander);
                    }
                }
            });

            return ResponseEntity.ok(commanders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    @GetMapping("/commands/{token}/{status}")
    public HttpEntity<?> getCommandByStatusId(@PathVariable String token, @PathVariable Integer status) {
        try {
            System.out.println("Token: " + token);
            String staffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            System.out.println(staffOpt.get());
            if (staffOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
            List<Command> commands = commandRepo.findAllByCommanderStaffAdnStatus(staffOpt.get().getId(), status);
            if (status == 2) {
                List<Command> additionalCommands = commandRepo.findAllByCommanderStaffAdnStatus(staffOpt.get().getId(), 1);
                commands.addAll(additionalCommands);
            }
            System.out.println("Commands: " + commands);
            return ResponseEntity.ok(commands);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid staff ID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/my-commands/{token}/{status}")
    public HttpEntity<?> getMyCommandByStatusId(@PathVariable String token, @PathVariable Integer status) {
        try {
            System.out.println("Token: " + token);
            String staffId = getStaffIdFromToken(token);
            System.out.println(staffId);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            System.out.println(staffOpt.get());
            if (staffOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
            List<Command> commands = commandRepo.findAllByStaffAdnStatus(staffOpt.get().getId(), status);
            System.out.println("Commands: " + commands);
            if(status == 1){
                for (Command command : commands) {
                    command.setStatus(2);
                    History history = new History(1, 2, command, LocalDateTime.now());
                    historyRepo.save(history);
                }
                commandRepo.saveAll(commands);
            }
            return ResponseEntity.ok(commands);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid staff ID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
    private String generateJwtToken(Staff staff) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setSubject(staff.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 6000 * 24 * 30)) // 1000 * 6000 ms = 1 hour
                .addClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @PutMapping("/my-commands/{commandId}")
    public HttpEntity<?> updateStaff(@RequestBody CommandRequestEditDto commandRequestEditDto, @PathVariable Integer commandId) {
        Command command = commandRepo.findById(commandId).orElseThrow();
        command.setResponseText(commandRequestEditDto.getResponseText());
        Attachment attachment = null;
        if (commandRequestEditDto.getFileId() != null) {
            attachment = attachmentRepo.findById(commandRequestEditDto.getFileId()).orElseThrow();
        }
        command.setResponseFile(attachment);
        command.setResponseTime(LocalDateTime.now());
        command.setStatus(3);
        History history = new History(2, 3, command, LocalDateTime.now());
        historyRepo.save(history);
        commandRepo.save(command);
        return ResponseEntity.ok("Successfully updated");
    }


    @PostMapping("/completed/{commandId}/{rating}")
    public HttpEntity<?> complete(@PathVariable Integer commandId, @PathVariable Integer rating) {
        Command command = commandRepo.findById(commandId).orElseThrow();
        command.setStatus(4);
        History history = new History(3, 4, command, LocalDateTime.now());
        historyRepo.save(history);
        commandRepo.save(command);
        if(rating>0){
            commandRatingRepo.save(new CommandRating(command, rating));
        }
        return ResponseEntity.ok("Successfully completed");

    }
    @PostMapping("/reject/{commandId}")
    public HttpEntity<?> rejectCommand(@RequestBody CommandRequestEditDto commandRequestEditDto, @PathVariable Integer commandId) {
        Command command = commandRepo.findById(commandId).orElseThrow();
        Attachment attachment = null;

        if (commandRequestEditDto.getFileId() != null) {
            attachment = attachmentRepo.findById(commandRequestEditDto.getFileId()).orElseThrow();
        }
        command.setStatus(1);
        History history = new History(3, 1, command, LocalDateTime.now());
        CommandMessage commandMessage = new CommandMessage(command, history,3, LocalDateTime.now(),commandRequestEditDto.getResponseText(),attachment, 1);

        historyRepo.save(history);
        commandMessageRepo.save(commandMessage);
        commandRepo.save(command);
        return ResponseEntity.ok("Successfully updated");
    }

    private String getStaffIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject();
    }


    @GetMapping("/statistic/{staffId}/{token}")
    public HttpEntity<?> getStatistic(@PathVariable Integer staffId, @PathVariable String token) {
        try {
            String extractedStaffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(extractedStaffId));
            if (staffOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
            List<Command> commands = commandRepo.findAllByStaffAdnCommander(staffOpt.get().getId(), staffId);
            Integer newCommandsCount = 0;
            Integer inProgressCommandsCount = 0;
            Integer completedCommandsCount = 0;
            Integer pendingCommandsCount = 0;
            for (Command command : commands) {
                switch (command.getStatus()) {
                    case 1 -> newCommandsCount++;
                    case 2 -> inProgressCommandsCount++;
                    case 4 -> completedCommandsCount++;
                    case 3 -> pendingCommandsCount++;
                }
            }
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("newCommandsCount", newCommandsCount);
            statistics.put("allCommandsCount", commands.size());
            statistics.put("inProgressCommandsCount", inProgressCommandsCount);
            statistics.put("completedCommandsCount", completedCommandsCount);
            statistics.put("pendingCommandsCount", pendingCommandsCount);
            statistics.put("commands", commands);
            System.out.println(statistics);
            return ResponseEntity.ok(statistics);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid staff ID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/rector/statistic/{staffId}/{token}")
    public HttpEntity<?> getRectorStatistic(@PathVariable Integer staffId, @PathVariable String token) {
        try {
            String extractedStaffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(extractedStaffId));
            if (staffOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
            List<Command> commands = commandRepo.findAllByStaffCommands( staffId);
            Integer newCommandsCount = 0;
            Integer inProgressCommandsCount = 0;
            Integer completedCommandsCount = 0;
            Integer pendingCommandsCount = 0;
            for (Command command : commands) {
                switch (command.getStatus()) {
                    case 1 -> newCommandsCount++;
                    case 2 -> inProgressCommandsCount++;
                    case 4 -> completedCommandsCount++;
                    case 3 -> pendingCommandsCount++;
                }
            }
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("newCommandsCount", newCommandsCount);
            statistics.put("allCommandsCount", commands.size());
            statistics.put("inProgressCommandsCount", inProgressCommandsCount);
            statistics.put("completedCommandsCount", completedCommandsCount);
            statistics.put("pendingCommandsCount", pendingCommandsCount);
            statistics.put("commands", commands);
            System.out.println(statistics);
            return ResponseEntity.ok(statistics);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid staff ID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    @PutMapping("/setPhoto/{token}")
    public HttpEntity<?> setPhoto(@PathVariable String token, @RequestBody Map<String, UUID> photoId) {
        System.out.println(photoId);
        try {
            String staffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(staffId));
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                Attachment attachment = attachmentRepo.findById(photoId.get("uuid")).orElseThrow();
                staff.setFile(attachment);
                staffRepo.save(staff);
                staff.setPassword(null);

                return ResponseEntity.ok(staff);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

    }

    @GetMapping("/add/all")
    public ResponseEntity<?> addAll() {
//         String filePath = System.getProperty("user.home") + "/Downloads/xodim.xlsx";
         String filePath =  "./xodim.xlsx";

        try (FileInputStream fileInputStream = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from 1 to skip header row
                Row row = sheet.getRow(i);

                if (row != null) {
                    try {
                        Staff staff = new Staff();
                        String passportNumber = getCellValue(row.getCell(2));
                        String passportPin = getCellValue(row.getCell(3));
                        String name = getCellValue(row.getCell(7)) + " " + getCellValue(row.getCell(6)) + " " + getCellValue(row.getCell(8));
                        staff.setPassportNumber(passportNumber);
                        staff.setPassportPin(passportPin);
                        staff.setName(name);
                        staff.setPassword(passwordEncoder.encode("00000000"));
                        staff.setPhone(passportPin);
                        staff.setScore(100);
                        staff.setFile(null);
                        staff.setTelegramId(String.valueOf(0L));

                        staffRepo.save(staff);
                    } catch (Exception e) {
                        System.err.println("Error saving staff at row " + (i + 1) + ": " + e.getMessage());
                    }
                }
            }

            return ResponseEntity.ok("All staff data has been processed and added to the database where possible.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error reading the Excel file: " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }


    @GetMapping("/commands/buyruq/statistic/{token}")
    public HttpEntity<?> getStatistic(@PathVariable String token) {
        System.out.println("adkasddkjajdjadjkjsajk");
        try {
            String extractedStaffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(extractedStaffId));
            if (staffOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
            List<Command> commands = commandRepo.findAllByCommanderCommands(staffOpt.get().getId());
            Integer inProgressCommandsCount = 0;
            Integer completedCommandsCount = 0;
            Integer pendingCommandsCount = 0;
            for (Command command : commands) {
                switch (command.getStatus()) {
                    case 1 -> inProgressCommandsCount++;
                    case 2 -> inProgressCommandsCount++;
                    case 4 -> completedCommandsCount++;
                    case 3 -> pendingCommandsCount++;
                }
            }
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("allCommandsCount", commands.size());
            statistics.put("inProgressCommandsCount", inProgressCommandsCount);
            statistics.put("completedCommandsCount", completedCommandsCount);
            statistics.put("pendingCommandsCount", pendingCommandsCount);
            System.out.println(statistics);
            return ResponseEntity.ok(statistics);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid staff ID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }



    @GetMapping("/commands/topshiriq/statistic/{token}")
    public HttpEntity<?> getStatisticTopshiriq(@PathVariable String token) {
        try {
            String extractedStaffId = getStaffIdFromToken(token);
            Optional<Staff> staffOpt = staffRepo.findById(Integer.parseInt(extractedStaffId));
            if (staffOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }
            List<Command> commands = commandRepo.findAllByStaffCommands(staffOpt.get().getId());
            Integer newCommandsCount = 0;
            Integer inProgressCommandsCount = 0;
            Integer completedCommandsCount = 0;
            Integer pendingCommandsCount = 0;
            for (Command command : commands) {
                switch (command.getStatus()) {
                    case 1 -> newCommandsCount++;
                    case 2 -> inProgressCommandsCount++;
                    case 4 -> completedCommandsCount++;
                    case 3 -> pendingCommandsCount++;
                }
            }
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("newCommandsCount", newCommandsCount);
            statistics.put("allCommandsCount", commands.size());
            statistics.put("inProgressCommandsCount", inProgressCommandsCount);
            statistics.put("completedCommandsCount", completedCommandsCount);
            statistics.put("pendingCommandsCount", pendingCommandsCount);
            System.out.println(statistics);
            return ResponseEntity.ok(statistics);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid staff ID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


}
