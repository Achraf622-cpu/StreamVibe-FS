package com.streamvibe.videoservice.service;

import com.streamvibe.videoservice.dto.VideoDTO;
import com.streamvibe.videoservice.mapper.VideoMapper;
import com.streamvibe.videoservice.model.Video;
import com.streamvibe.videoservice.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoMapper videoMapper;

    @InjectMocks
    private VideoServiceImpl videoService;

    private Video video;
    private VideoDTO videoDTO;

    @BeforeEach
    void setUp() {
        video = Video.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .build();

        videoDTO = VideoDTO.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .build();
    }

    @Test
    void createVideo_Success() {
        when(videoMapper.toEntity(any(VideoDTO.class))).thenReturn(video);
        when(videoRepository.save(any(Video.class))).thenReturn(video);
        when(videoMapper.toDto(any(Video.class))).thenReturn(videoDTO);

        VideoDTO result = videoService.createVideo(videoDTO);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        verify(videoRepository, times(1)).save(video);
    }

    @Test
    void getVideoById_Success() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoMapper.toDto(video)).thenReturn(videoDTO);

        VideoDTO result = videoService.getVideoById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(videoRepository, times(1)).findById(1L);
    }

    @Test
    void getVideoById_NotFound() {
        when(videoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> videoService.getVideoById(1L));
        assertEquals("Video not found with id 1", exception.getMessage());
    }

    @Test
    void getAllVideos_Success() {
        when(videoRepository.findAll()).thenReturn(Arrays.asList(video));
        when(videoMapper.toDto(video)).thenReturn(videoDTO);

        List<VideoDTO> result = videoService.getAllVideos();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(videoRepository, times(1)).findAll();
    }

    @Test
    void searchVideos_Success() {
        when(videoRepository.findByTitleContainingIgnoreCase("incept")).thenReturn(Arrays.asList(video));
        when(videoMapper.toDto(video)).thenReturn(videoDTO);

        List<VideoDTO> result = videoService.searchVideos("incept");

        assertFalse(result.isEmpty());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void updateVideo_Success() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenReturn(video);
        when(videoMapper.toDto(any(Video.class))).thenReturn(videoDTO);

        VideoDTO result = videoService.updateVideo(1L, videoDTO);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        verify(videoRepository, times(1)).save(video);
    }

    @Test
    void deleteVideo_Success() {
        when(videoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(videoRepository).deleteById(1L);

        assertDoesNotThrow(() -> videoService.deleteVideo(1L));
        verify(videoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteVideo_NotFound() {
        when(videoRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> videoService.deleteVideo(1L));
        assertEquals("Video not found with id 1", exception.getMessage());
    }
}
