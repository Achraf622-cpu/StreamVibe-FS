package com.streamvibe.videoservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;

    // Only storing the YouTube embed URL instead of video files
    private String trailerUrl;

    // Duration in minutes
    private Integer duration;

    private Integer releaseYear;

    @Enumerated(EnumType.STRING)
    private VideoType type;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Double rating;

    private String director;

    private String castMembers;
}
