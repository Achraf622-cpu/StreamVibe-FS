package com.streamvibe.videoservice.service;

import com.streamvibe.videoservice.dto.VideoDTO;
import java.util.List;

public interface VideoService {
    VideoDTO createVideo(VideoDTO videoDTO);

    VideoDTO getVideoById(Long id);

    List<VideoDTO> getAllVideos();

    List<VideoDTO> searchVideos(String query);

    VideoDTO updateVideo(Long id, VideoDTO videoDTO);

    void deleteVideo(Long id);

    boolean existsById(Long id);
}
