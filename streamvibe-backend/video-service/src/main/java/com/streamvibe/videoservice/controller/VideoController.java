package com.streamvibe.videoservice.controller;

import com.streamvibe.videoservice.dto.VideoDTO;
import com.streamvibe.videoservice.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(@RequestBody VideoDTO videoDTO) {
        return new ResponseEntity<>(videoService.createVideo(videoDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.existsById(id));
    }

    @GetMapping
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/search")
    public ResponseEntity<List<VideoDTO>> searchVideos(@RequestParam String query) {
        return ResponseEntity.ok(videoService.searchVideos(query));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable Long id, @RequestBody VideoDTO videoDTO) {
        return ResponseEntity.ok(videoService.updateVideo(id, videoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
