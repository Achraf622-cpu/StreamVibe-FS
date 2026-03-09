package com.streamvibe.videoservice.mapper;

import com.streamvibe.videoservice.dto.VideoDTO;
import com.streamvibe.videoservice.model.Video;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {

    public VideoDTO toDto(Video video) {
        if (video == null)
            return null;

        return VideoDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .thumbnailUrl(video.getThumbnailUrl())
                .trailerUrl(video.getTrailerUrl())
                .duration(video.getDuration())
                .releaseYear(video.getReleaseYear())
                .type(video.getType())
                .category(video.getCategory())
                .rating(video.getRating())
                .director(video.getDirector())
                .castMembers(video.getCastMembers())
                .build();
    }

    public Video toEntity(VideoDTO dto) {
        if (dto == null)
            return null;

        return Video.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .thumbnailUrl(dto.getThumbnailUrl())
                .trailerUrl(dto.getTrailerUrl())
                .duration(dto.getDuration())
                .releaseYear(dto.getReleaseYear())
                .type(dto.getType())
                .category(dto.getCategory())
                .rating(dto.getRating())
                .director(dto.getDirector())
                .castMembers(dto.getCastMembers())
                .build();
    }
}
