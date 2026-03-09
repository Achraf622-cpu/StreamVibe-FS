package com.streamvibe.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamvibe.userservice.dto.UserDTO;
import com.streamvibe.userservice.dto.WatchHistoryDTO;
import com.streamvibe.userservice.dto.WatchlistDTO;
import com.streamvibe.userservice.model.User;
import com.streamvibe.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("johndoe")
                .email("john@streamvibe.com")
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .username("johndoe")
                .email("john@streamvibe.com")
                .build();
    }

    @Test
    void registerUser_Success() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    void addToWatchlist_Success() throws Exception {
        WatchlistDTO watchlistDTO = WatchlistDTO.builder().id(10L).userId(1L).videoId(101L).build();
        when(userService.addToWatchlist(1L, 101L)).thenReturn(watchlistDTO);

        mockMvc.perform(post("/api/users/{userId}/watchlist/{videoId}", 1L, 101L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.videoId").value(101L));
    }

    @Test
    void removeFromWatchlist_Success() throws Exception {
        doNothing().when(userService).removeFromWatchlist(1L, 101L);

        mockMvc.perform(delete("/api/users/{userId}/watchlist/{videoId}", 1L, 101L))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).removeFromWatchlist(1L, 101L);
    }

    @Test
    void getUserWatchlist_Success() throws Exception {
        WatchlistDTO watchlistDTO = WatchlistDTO.builder().id(10L).userId(1L).videoId(101L).build();
        when(userService.getUserWatchlist(1L)).thenReturn(Arrays.asList(watchlistDTO));

        mockMvc.perform(get("/api/users/{userId}/watchlist", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].videoId").value(101L));
    }

    @Test
    void saveWatchHistory_Success() throws Exception {
        WatchHistoryDTO historyDTO = WatchHistoryDTO.builder().id(20L).userId(1L).videoId(101L).progressTime(120)
                .completed(true).build();
        when(userService.saveWatchHistory(1L, 101L, 120, true)).thenReturn(historyDTO);

        mockMvc.perform(post("/api/users/{userId}/history/{videoId}", 1L, 101L)
                .param("progressTime", "120")
                .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressTime").value(120))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void getUserWatchStatistics_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVideosWatched", 5L);
        when(userService.getUserWatchStatistics(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/users/{userId}/statistics", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVideosWatched").value(5));
    }
}
