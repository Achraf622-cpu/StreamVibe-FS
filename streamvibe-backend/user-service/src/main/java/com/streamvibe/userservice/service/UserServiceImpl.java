package com.streamvibe.userservice.service;

import com.streamvibe.userservice.client.VideoClient;
import com.streamvibe.userservice.dto.AuthUserDTO;
import com.streamvibe.userservice.dto.UserDTO;
import com.streamvibe.userservice.dto.VideoDTO;
import com.streamvibe.userservice.dto.WatchHistoryDTO;
import com.streamvibe.userservice.dto.WatchlistDTO;
import com.streamvibe.userservice.model.User;
import com.streamvibe.userservice.model.WatchHistory;
import com.streamvibe.userservice.model.Watchlist;
import com.streamvibe.userservice.repository.UserRepository;
import com.streamvibe.userservice.repository.WatchHistoryRepository;
import com.streamvibe.userservice.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WatchlistRepository watchlistRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final VideoClient videoClient;
    private final PasswordEncoder passwordEncoder;

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserDTO createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        // Hash the password securely
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public AuthUserDTO getAuthUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username)) // Support logging in via Email
                .map(user -> AuthUserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .build())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public WatchlistDTO addToWatchlist(Long userId, Long videoId) {
        // Verify User exists
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already in watchlist
        if (watchlistRepository.findByUserIdAndVideoId(userId, videoId).isPresent()) {
            throw new RuntimeException("Video already in watchlist");
        }

        // Verify Video exists via OpenFeign
        VideoDTO videoInfo = videoClient.getVideoById(videoId);
        if (videoInfo == null) {
            throw new RuntimeException("Video not found in Video Service");
        }

        Watchlist watchlist = Watchlist.builder()
                .userId(userId)
                .videoId(videoId)
                .build();

        Watchlist saved = watchlistRepository.save(watchlist);

        return WatchlistDTO.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .videoId(saved.getVideoId())
                .addedAt(saved.getAddedAt())
                .video(videoInfo) // Stitch the fetched data
                .build();
    }

    @Override
    public void removeFromWatchlist(Long userId, Long videoId) {
        Watchlist item = watchlistRepository.findByUserIdAndVideoId(userId, videoId)
                .orElseThrow(() -> new RuntimeException("Video not found in user's watchlist"));
        watchlistRepository.delete(item);
    }

    @Override
    public List<WatchlistDTO> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId).stream().map(item -> {
            VideoDTO videoInfo = null;
            try {
                videoInfo = videoClient.getVideoById(item.getVideoId());
            } catch (Exception e) {
                log.error("Failed to fetch video details for ID: {}", item.getVideoId(), e);
            }

            return WatchlistDTO.builder()
                    .id(item.getId())
                    .userId(item.getUserId())
                    .videoId(item.getVideoId())
                    .addedAt(item.getAddedAt())
                    .video(videoInfo)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public WatchHistoryDTO saveWatchHistory(Long userId, Long videoId, Integer progressTime, Boolean completed) {
        WatchHistory history = watchHistoryRepository.findByUserIdAndVideoId(userId, videoId)
                .orElse(WatchHistory.builder().userId(userId).videoId(videoId).build());

        history.setProgressTime(progressTime);
        if (completed != null) {
            history.setCompleted(completed);
        }

        WatchHistory saved = watchHistoryRepository.save(history);

        VideoDTO videoInfo = null;
        try {
            videoInfo = videoClient.getVideoById(videoId);
        } catch (Exception e) {
            log.error("Failed to fetch video details for history ID: {}", videoId, e);
        }

        return WatchHistoryDTO.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .videoId(saved.getVideoId())
                .watchedAt(saved.getWatchedAt())
                .progressTime(saved.getProgressTime())
                .completed(saved.getCompleted())
                .video(videoInfo)
                .build();
    }

    @Override
    public List<WatchHistoryDTO> getUserWatchHistory(Long userId) {
        return watchHistoryRepository.findByUserId(userId).stream().map(item -> {
            VideoDTO videoInfo = null;
            try {
                videoInfo = videoClient.getVideoById(item.getVideoId());
            } catch (Exception e) {
                log.error("Failed to fetch video details for history ID: {}", item.getVideoId(), e);
            }

            return WatchHistoryDTO.builder()
                    .id(item.getId())
                    .userId(item.getUserId())
                    .videoId(item.getVideoId())
                    .watchedAt(item.getWatchedAt())
                    .progressTime(item.getProgressTime())
                    .completed(item.getCompleted())
                    .video(videoInfo)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public java.util.Map<String, Object> getUserWatchStatistics(Long userId) {
        List<WatchHistory> history = watchHistoryRepository.findByUserId(userId);

        long totalVideosWatched = history.size();
        long completedVideos = history.stream().filter(WatchHistory::getCompleted).count();
        int totalProgressSeconds = history.stream().mapToInt(WatchHistory::getProgressTime).sum();

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("userId", userId);
        stats.put("totalVideosWatched", totalVideosWatched);
        stats.put("completedVideos", completedVideos);
        stats.put("totalWatchTimeHours", totalProgressSeconds / 3600.0);
        stats.put("completionRate",
                totalVideosWatched == 0 ? 0.0 : ((double) completedVideos / totalVideosWatched) * 100);

        return stats;
    }
}
