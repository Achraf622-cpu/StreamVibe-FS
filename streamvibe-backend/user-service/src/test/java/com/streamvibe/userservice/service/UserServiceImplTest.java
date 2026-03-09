package com.streamvibe.userservice.service;

import com.streamvibe.userservice.client.VideoClient;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private WatchHistoryRepository watchHistoryRepository;

    @Mock
    private VideoClient videoClient;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private VideoDTO videoDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@streamvibe.com")
                .build();

        videoDTO = VideoDTO.builder()
                .id(101L)
                .title("Matrix")
                .description("Action sci-fi")
                .build();
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_ThrowsWhenUsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void addToWatchlist_Success() {
        Watchlist watchlist = Watchlist.builder().id(10L).userId(1L).videoId(101L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(watchlistRepository.findByUserIdAndVideoId(1L, 101L)).thenReturn(Optional.empty());
        when(videoClient.getVideoById(101L)).thenReturn(videoDTO);
        when(watchlistRepository.save(any(Watchlist.class))).thenReturn(watchlist);

        WatchlistDTO result = userService.addToWatchlist(1L, 101L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Matrix", result.getVideo().getTitle());
    }

    @Test
    void addToWatchlist_ThrowsWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.addToWatchlist(1L, 101L));
    }

    @Test
    void removeFromWatchlist_Success() {
        Watchlist watchlist = Watchlist.builder().id(10L).userId(1L).videoId(101L).build();
        when(watchlistRepository.findByUserIdAndVideoId(1L, 101L)).thenReturn(Optional.of(watchlist));
        doNothing().when(watchlistRepository).delete(watchlist);

        assertDoesNotThrow(() -> userService.removeFromWatchlist(1L, 101L));
        verify(watchlistRepository, times(1)).delete(watchlist);
    }

    @Test
    void getUserWatchlist_Success() {
        Watchlist watchlist = Watchlist.builder().id(10L).userId(1L).videoId(101L).build();
        when(watchlistRepository.findByUserId(1L)).thenReturn(Arrays.asList(watchlist));
        when(videoClient.getVideoById(101L)).thenReturn(videoDTO);

        List<WatchlistDTO> result = userService.getUserWatchlist(1L);

        assertFalse(result.isEmpty());
        assertEquals(101L, result.get(0).getVideoId());
        assertNotNull(result.get(0).getVideo());
    }

    @Test
    void saveWatchHistory_NewHistory_Success() {
        WatchHistory history = WatchHistory.builder().id(20L).userId(1L).videoId(101L).progressTime(120)
                .completed(false).build();
        when(watchHistoryRepository.findByUserIdAndVideoId(1L, 101L)).thenReturn(Optional.empty());
        when(watchHistoryRepository.save(any(WatchHistory.class))).thenReturn(history);
        when(videoClient.getVideoById(101L)).thenReturn(videoDTO);

        WatchHistoryDTO result = userService.saveWatchHistory(1L, 101L, 120, false);

        assertNotNull(result);
        assertEquals(120, result.getProgressTime());
        assertFalse(result.getCompleted());
    }

    @Test
    void getUserWatchStatistics_Success() {
        WatchHistory h1 = WatchHistory.builder().userId(1L).videoId(101L).progressTime(3600).completed(true).build();
        WatchHistory h2 = WatchHistory.builder().userId(1L).videoId(102L).progressTime(1800).completed(false).build();
        when(watchHistoryRepository.findByUserId(1L)).thenReturn(Arrays.asList(h1, h2));

        Map<String, Object> stats = userService.getUserWatchStatistics(1L);

        assertEquals(2L, stats.get("totalVideosWatched"));
        assertEquals(1L, stats.get("completedVideos"));
        assertEquals(1.5, stats.get("totalWatchTimeHours"));
        assertEquals(50.0, stats.get("completionRate"));
    }
}
