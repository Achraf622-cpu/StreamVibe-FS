package com.streamvibe.videoservice.repository;

import com.streamvibe.videoservice.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByType(com.streamvibe.videoservice.model.VideoType type);

    List<Video> findByCategory(com.streamvibe.videoservice.model.Category category);

    List<Video> findByTitleContainingIgnoreCase(String title);
}
