package com.example.backend.Controller;

import com.example.backend.DTO.HemisDTO;
import com.example.backend.Entity.TokenHemis;
import com.example.backend.Repository.TokenHemisRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hemis")
@RequiredArgsConstructor
public class HemisController {
    private final TokenHemisRepo tokenHemisRepo;
    private final String BASE_URL = "https://student.buxpxti.uz/rest";

    @PostMapping
    public HttpEntity<?> hemisConnector(@RequestBody HemisDTO hemisDTO) {
        try {
            System.out.println(hemisDTO);
            // 1. Get latest token from DB
            List<TokenHemis> all = tokenHemisRepo.findAll();
            if (all.isEmpty()) {
                return ResponseEntity.badRequest().body("No tokens found in database.");
            }
            TokenHemis tokenHemis = all.get(all.size() - 1);
            String token = tokenHemis.getName();
            System.out.println(token);


            // 2. Prepare RestTemplate and request
            RestTemplate restTemplate = new RestTemplate();

            String fullUrl = BASE_URL + hemisDTO.getEndpoint(); // endpoint comes from DTO

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Object> request = new HttpEntity<>(hemisDTO.getData(), headers);

            // 3. Send POST request
            ResponseEntity<Object> response = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    request,
                    Object.class // You can change this to Map.class or custom class if needed
            );

            // 4. Return response to frontend
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error while sending request: " + e.getMessage());
        }
    }

}
