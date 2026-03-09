package com.streamvibe.videoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamvibe.videoservice.dto.VideoDTO;
import com.streamvibe.videoservice.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Autowired
    private ObjectMapper objectMapper;

    private VideoDTO videoDTO;

    @BeforeEach
    void setUp() {
        videoDTO = VideoDTO.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .build();
    }

    @Test
    void createVideo_Success() throws Exception {
        when(videoService.createVideo(any(VideoDTO.class))).thenReturn(videoDTO);

        mockMvc.perform(post("/api/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(videoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getVideoById_Success() throws Exception {
        when(videoService.getVideoById(1L)).thenReturn(videoDTO);

        mockMvc.perform(get("/api/videos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getAllVideos_Success() throws Exception {
        when(videoService.getAllVideos()).thenReturn(Arrays.asList(videoDTO));

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    void searchVideos_Success() throws Exception {
        when(videoService.searchVideos("incept")).thenReturn(Arrays.asList(videoDTO));

        mockMvc.perform(get("/api/videos/search")
                .param("query", "incept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void updateVideo_Success() throws Exception {
        when(videoService.updateVideo(eq(1L), any(VideoDTO.class))).thenReturn(videoDTO);

        mockMvc.perform(put("/api/videos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(videoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void deleteVideo_Success() throws Exception {
        doNothing().when(videoService).deleteVideo(1L);

        mockMvc.perform(delete("/api/videos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(videoService, times(1)).deleteVideo(1L);
    }
}
