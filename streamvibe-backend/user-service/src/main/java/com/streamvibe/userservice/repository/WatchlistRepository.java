package com.streamvibe.userservice.repository;

import com.streamvibe.userservice.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserId(Long userId);

    Optional<Watchlist> findByUserIdAndVideoId(Long userId, Long videoId);
}
