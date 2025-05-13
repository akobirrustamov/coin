package com.example.backend.Controller;

import com.example.backend.Entity.StatisticTestSuitsid;
import com.example.backend.Entity.Student;
import com.example.backend.Entity.TestSuitsid;
import com.example.backend.Repository.StatisticTestSuitsidRepo;
import com.example.backend.Repository.StudentRepo;
import com.example.backend.Repository.TestSuitsidRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Get;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/test-suitsid")
@RequiredArgsConstructor
public class TestSuitsidController {
    private final TestSuitsidRepo testSuitsidRepo;
    private final StatisticTestSuitsidRepo statisticTestSuitsidRepo;
    private final StudentRepo studentRepo;
    @GetMapping
    public HttpEntity<?> testSuitsid(){
        List<TestSuitsid> all = testSuitsidRepo.findAll();
        return ResponseEntity.ok(all);
    }


    @PostMapping("/result/{studentId}/{result}")
    public HttpEntity<?> testSuitsidResult(@PathVariable UUID studentId, @PathVariable Integer result){
        Optional<Student> byId = studentRepo.findById(studentId);
        if (byId.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        statisticTestSuitsidRepo.save(new StatisticTestSuitsid(byId.get(), result));
        return ResponseEntity.ok("Test submitted successfully");
    }


    @GetMapping("/student/{studentId}")
    public HttpEntity<?> testSuitsidStudent(@PathVariable UUID studentId){
        Optional<StatisticTestSuitsid> byId = statisticTestSuitsidRepo.findByStuentId(studentId);
        if (byId.isEmpty()){
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/statistic")
    public HttpEntity<?> gettestSuitsidStatistic() {
        List<StatisticTestSuitsid> all = statisticTestSuitsidRepo.findAll();

        // Updated 3-category result map
        Map<String, List<StatisticTestSuitsid>> resultMap = new HashMap<>();
        resultMap.put("0-4 ball (Minimal bezotalanish)", new ArrayList<>());
        resultMap.put("5-13 ball (O‘rtacha bezotalanish)", new ArrayList<>());
        resultMap.put("14-20 ball (Yuqori bezotalanish)", new ArrayList<>());

        for (StatisticTestSuitsid statistic : all) {
            int score = statistic.getScore();

            if (score >= 0 && score <= 4) {
                resultMap.get("0-4 ball (Minimal bezotalanish)").add(statistic);
            } else if (score >= 5 && score <= 13) {
                resultMap.get("5-13 ball (O‘rtacha bezotalanish)").add(statistic);
            } else if (score >= 14 && score <= 20) {
                resultMap.get("14-20 ball (Yuqori bezotalanish)").add(statistic);
            }
        }

        return ResponseEntity.ok(resultMap);
    }


    @GetMapping("/statistic-all")
    public HttpEntity<?> getAlltestSuitsidStatistic(){

        List<StatisticTestSuitsid> all = statisticTestSuitsidRepo.findAll();
        return ResponseEntity.ok(all);
    }



}
