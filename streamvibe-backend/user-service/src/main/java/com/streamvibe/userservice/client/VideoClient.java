package com.streamvibe.userservice.client;

import com.streamvibe.userservice.dto.VideoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Connects to the VIDEO-SERVICE via Eureka
@FeignClient(name = "VIDEO-SERVICE")
public interface VideoClient {

    @GetMapping("/api/videos/{id}")
    VideoDTO getVideoById(@PathVariable("id") Long id);
}
