package com.example.backend.Controller;

import com.example.backend.Entity.ConcertQuestionnaire;
import com.example.backend.Repository.ConcertQuestionnaireRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/concert-questionnaire")
public class ConcertQuestionnaireController {

    private final ConcertQuestionnaireRepo concertQuestionnaireRepo;


    @GetMapping
    public HttpEntity<?> getAll(){
        List<ConcertQuestionnaire> concertQuestionnaireList = concertQuestionnaireRepo.findAll();
        return new ResponseEntity<>(concertQuestionnaireList, HttpStatus.OK);

    }


    @PostMapping
    public HttpEntity<?> save(@RequestBody ConcertQuestionnaire concertQuestionnaire){
        concertQuestionnaireRepo.save(concertQuestionnaire);
        return new ResponseEntity<>(concertQuestionnaire, HttpStatus.CREATED);
    }
}
