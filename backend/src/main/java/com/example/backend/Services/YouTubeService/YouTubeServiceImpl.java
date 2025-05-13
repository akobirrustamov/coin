package com.example.backend.Services.YouTubeService;

import com.example.backend.Entity.YouTube;
import com.example.backend.Repository.YouTubeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YouTubeServiceImpl implements YouTubeService {


    private final YouTubeRepository youTubeRepository;

    @Override
    public YouTube addYouTube(YouTube youTube) {
        return youTubeRepository.save(youTube);
    }

    @Override
    public void deleteYouTube(Integer id) {
        youTubeRepository.deleteById(id);
    }

    @Override
    public List<YouTube> getAllYouTube() {
        return youTubeRepository.findAllByOrderByCreatedAtDesc();
    }
}
