package com.streamvibe.userservice.repository;

import com.streamvibe.userservice.model.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUserId(Long userId);

    Optional<WatchHistory> findByUserIdAndVideoId(Long userId, Long videoId);
}
