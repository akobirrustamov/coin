package com.example.backend.Controller;

import com.example.backend.DTO.CommandRequestDto;
import com.example.backend.DTO.StaffRank;
import com.example.backend.Entity.*;
import com.example.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/command")
public class AppCommandController {
    private final CommandRepo commandRepo;
    private final StaffRepo staffRepo;
    private final AttachmentRepo attachmentRepo;
    private final HistoryRepo historyRepo;
    private final CommandMessageRepo commandMessageRepo;

    @GetMapping("/message/{commandId}")
    private    HttpEntity<?> getCommandMessage(@PathVariable Integer commandId){
        List<CommandMessage> commandMessages = commandMessageRepo.findByCommandId(commandId);
        return ResponseEntity.ok(commandMessages);
    }


    @PostMapping
    public HttpEntity<?> addCommand(@RequestBody CommandRequestDto commandDto) {
        List<Command> commands = new ArrayList<>();
        Attachment attachment = null;

        if (commandDto.getFile() != null) {
            attachment = attachmentRepo.findById(commandDto.getFile()).orElseThrow(() -> new RuntimeException("Attachment not found"));
        }

        Staff commandStaff = staffRepo.findById(commandDto.getCommandStaffId()).orElseThrow(() -> new RuntimeException("Command staff not found"));
        Integer number = 1; // Default to 1 for the first command

        try {
            Integer maxNumber = commandRepo.findMaxNumber();
            if (maxNumber != null) {
                number = maxNumber + 1; // Start counting from maxNumber + 1
            }
        } catch (Exception e) {
            System.out.println("Error finding max number: " + e.getMessage());
        }

        int index = 0; // Counter for incrementing the number
        for (StaffRank staffRank : commandDto.getSelectedStaffList()) {
            Staff staff = staffRepo.findById(staffRank.getStaffId()).orElseThrow(() -> new RuntimeException("Staff not found"));
            String staffFcmToken = staff.getFcmToken();  // Assuming Staff entity has an fcmToken field
            LocalDateTime commandDateTime = commandDto.getDateTime().plusHours(5); // Add 5 hours to the commandDateTime

            Command command = new Command(
                    commandDto.getText(),
                    attachment,
                    commandDto.getDescription(),
                    commandDateTime,
                    1,
                    commandStaff,
                    staff,
                    null, null, null, null,
                    commandDto.getBall(),
                    number + index, // Increment the number for each command
                    LocalDateTime.now(),
                    false
            );



            commands.add(command);
            index++; // Increment the index for the next command
        }

        commandRepo.saveAll(commands);
        return ResponseEntity.ok("Saved commands");
    }

    @GetMapping("/get-history/{commandId}")
    public HttpEntity<?> getCommandHistory(@PathVariable Integer commandId) {
        Command command = commandRepo.findById(commandId).orElseThrow(() -> new RuntimeException("Command not found"));
        List<History> histories = historyRepo.findAllByCommand(command);
        return ResponseEntity.ok(histories);
    }



}
