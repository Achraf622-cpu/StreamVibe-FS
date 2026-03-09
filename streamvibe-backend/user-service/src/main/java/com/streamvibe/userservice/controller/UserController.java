package com.streamvibe.userservice.controller;

import com.streamvibe.userservice.dto.UserDTO;
import com.streamvibe.userservice.dto.WatchHistoryDTO;
import com.streamvibe.userservice.dto.WatchlistDTO;
import com.streamvibe.userservice.model.User;
import com.streamvibe.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- User Endpoints ---
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // INTERNAL API: Used exclusively by Auth Service
    @GetMapping("/internal/username/{username}")
    public ResponseEntity<com.streamvibe.userservice.dto.AuthUserDTO> getAuthUserByUsername(
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getAuthUserByUsername(username));
    }

    // --- Watchlist Endpoints ---
    @PostMapping("/{userId}/watchlist/{videoId}")
    public ResponseEntity<WatchlistDTO> addToWatchlist(@PathVariable Long userId, @PathVariable Long videoId) {
        return new ResponseEntity<>(userService.addToWatchlist(userId, videoId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/watchlist/{videoId}")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable Long userId, @PathVariable Long videoId) {
        userService.removeFromWatchlist(userId, videoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/watchlist")
    public ResponseEntity<List<WatchlistDTO>> getUserWatchlist(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserWatchlist(userId));
    }

    // --- Watch History Endpoints ---
    @PostMapping("/{userId}/history/{videoId}")
    public ResponseEntity<WatchHistoryDTO> saveWatchHistory(
            @PathVariable Long userId,
            @PathVariable Long videoId,
            @RequestParam Integer progressTime,
            @RequestParam(required = false, defaultValue = "false") Boolean completed) {

        return ResponseEntity.ok(userService.saveWatchHistory(userId, videoId, progressTime, completed));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<WatchHistoryDTO>> getUserWatchHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserWatchHistory(userId));
    }

    @GetMapping("/{userId}/statistics")
    public ResponseEntity<java.util.Map<String, Object>> getUserWatchStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserWatchStatistics(userId));
    }
}
