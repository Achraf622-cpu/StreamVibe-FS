package com.streamvibe.userservice.service;

import com.streamvibe.userservice.dto.AuthUserDTO;
import com.streamvibe.userservice.dto.UserDTO;
import com.streamvibe.userservice.dto.WatchHistoryDTO;
import com.streamvibe.userservice.dto.WatchlistDTO;
import com.streamvibe.userservice.model.User;

import java.util.List;

public interface UserService {
    // User Management
    UserDTO createUser(User user);

    UserDTO getUserById(Long id);

    UserDTO getUserByUsername(String username);

    AuthUserDTO getAuthUserByUsername(String username);

    // Watchlist Management
    WatchlistDTO addToWatchlist(Long userId, Long videoId);

    void removeFromWatchlist(Long userId, Long videoId);

    List<WatchlistDTO> getUserWatchlist(Long userId);

    // Watch History Management
    WatchHistoryDTO saveWatchHistory(Long userId, Long videoId, Integer progressTime, Boolean completed);

    List<WatchHistoryDTO> getUserWatchHistory(Long userId);

    // Watch Statistics Management
    java.util.Map<String, Object> getUserWatchStatistics(Long userId);
}
