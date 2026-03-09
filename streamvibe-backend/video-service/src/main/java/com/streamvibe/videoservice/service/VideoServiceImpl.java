package com.streamvibe.videoservice.service;

import com.streamvibe.videoservice.dto.VideoDTO;
import com.streamvibe.videoservice.mapper.VideoMapper;
import com.streamvibe.videoservice.model.Video;
import com.streamvibe.videoservice.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    @Override
    public VideoDTO createVideo(VideoDTO videoDTO) {
        Video video = videoMapper.toEntity(videoDTO);
        Video savedVideo = videoRepository.save(video);
        return videoMapper.toDto(savedVideo);
    }

    @Override
    public VideoDTO getVideoById(Long id) {
        return videoRepository.findById(id)
                .map(videoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Video not found with id " + id));
    }

    @Override
    public List<VideoDTO> getAllVideos() {
        return videoRepository.findAll().stream()
                .map(videoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VideoDTO> searchVideos(String query) {
        return videoRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(videoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VideoDTO updateVideo(Long id, VideoDTO videoDTO) {
        Video existingVideo = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id " + id));

        existingVideo.setTitle(videoDTO.getTitle());
        existingVideo.setDescription(videoDTO.getDescription());
        existingVideo.setThumbnailUrl(videoDTO.getThumbnailUrl());
        existingVideo.setTrailerUrl(videoDTO.getTrailerUrl());
        existingVideo.setDuration(videoDTO.getDuration());
        existingVideo.setReleaseYear(videoDTO.getReleaseYear());
        existingVideo.setType(videoDTO.getType());
        existingVideo.setCategory(videoDTO.getCategory());
        existingVideo.setRating(videoDTO.getRating());
        existingVideo.setDirector(videoDTO.getDirector());
        existingVideo.setCastMembers(videoDTO.getCastMembers());

        Video updatedVideo = videoRepository.save(existingVideo);
        return videoMapper.toDto(updatedVideo);
    }

    @Override
    public void deleteVideo(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new RuntimeException("Video not found with id " + id);
        }
        videoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return videoRepository.existsById(id);
    }
}
