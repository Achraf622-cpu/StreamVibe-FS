package com.streamvibe.videoservice.dto;

import com.streamvibe.videoservice.model.Category;
import com.streamvibe.videoservice.model.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String trailerUrl;
    private Integer duration;
    private Integer releaseYear;
    private VideoType type;
    private Category category;
    private Double rating;
    private String director;
    private String castMembers;
}
