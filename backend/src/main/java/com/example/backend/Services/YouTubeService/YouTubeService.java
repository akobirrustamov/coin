package com.example.backend.Services.YouTubeService;

import com.example.backend.Entity.YouTube;

import java.util.List;

public interface YouTubeService {
    YouTube addYouTube(YouTube youTube);
    void deleteYouTube(Integer id);
    List<YouTube> getAllYouTube();
}
