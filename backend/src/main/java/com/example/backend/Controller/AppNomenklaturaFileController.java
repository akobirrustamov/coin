package com.example.backend.Controller;

import com.example.backend.DTO.NomenklaturaFileDto;
import com.example.backend.Entity.*;
import com.example.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/nomenklaturafile")
public class AppNomenklaturaFileController {
    private final AttachmentRepo attachmentRepo;
    private final NomenklaturaFileRepo nomenklaturaFileRepo;
    private final AdministratorRepo administratorRepo;
    private final NomenklaturaRepo nomenklaturaRepo;
    private final StaffRepo staffRepo;

    @GetMapping("/{nomenklaturaId}/{folder}")
    public HttpEntity<?> getFile(@PathVariable Integer nomenklaturaId, @PathVariable Integer folder) {
        List<NomenklaturaFile> allfiles= nomenklaturaFileRepo.finAllByNomenklaturaIdAndFolder(nomenklaturaId, folder);
        return ResponseEntity.ok(allfiles);
    }



    @PutMapping
    public HttpEntity<?> postFile(@RequestBody NomenklaturaFileDto nomenklaturaFile) {
        System.out.println(nomenklaturaFile);
        Attachment attachment = attachmentRepo.findById(nomenklaturaFile.getFileId()).orElse(null);
        Staff staff = staffRepo.findById(nomenklaturaFile.getUserId()).orElse(null);
        Nomenklatura nomenklatura = nomenklaturaRepo.findById(nomenklaturaFile.getNomenklaturaId()).orElse(null);
        NomenklaturaFile newFile= new NomenklaturaFile(attachment, nomenklaturaFile.getDescription(), LocalDateTime.now(), nomenklatura, nomenklaturaFile.getFolder());
        if(nomenklatura.getUser().getId()==staff.getId()){
            nomenklaturaFileRepo.save(newFile);
            return ResponseEntity.ok("Nomenklatura file saved");
        }
        return ResponseEntity.ok("Nomenklatura file not saved");

    }


}