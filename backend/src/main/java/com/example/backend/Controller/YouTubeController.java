package com.example.backend.Controller;

import com.example.backend.Entity.YouTube;
import com.example.backend.Services.YouTubeService.YouTubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/youtube")
@RequiredArgsConstructor
public class YouTubeController {
    private final YouTubeService youTubeService;

    @PostMapping("/add")
    public ResponseEntity<YouTube> addYouTube(@RequestBody YouTube youTube) {
        YouTube createdYouTube = youTubeService.addYouTube(youTube);
        return ResponseEntity.ok(createdYouTube);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteYouTube(@PathVariable Integer id) {
        youTubeService.deleteYouTube(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<YouTube>> getAllYouTube() {
        List<YouTube> youTubeList = youTubeService.getAllYouTube();
        return ResponseEntity.ok(youTubeList);
    }
}
