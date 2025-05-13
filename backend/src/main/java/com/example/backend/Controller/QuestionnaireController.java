package com.example.backend.Controller;

import com.example.backend.Entity.Questionnaire;
import com.example.backend.Repository.QuestionnaireRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questionnaire")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireRepo questionnaireRepo;

    @GetMapping
    public HttpEntity<?> getAllQuestionnaire(){
        List<Questionnaire> all = questionnaireRepo.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }


    @PostMapping
    public HttpEntity<?> addQuestionnaire(@RequestBody Questionnaire questionnaire){
        questionnaireRepo.save(questionnaire);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
