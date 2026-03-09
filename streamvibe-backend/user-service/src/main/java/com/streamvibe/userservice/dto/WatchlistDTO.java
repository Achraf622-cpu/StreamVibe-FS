package com.streamvibe.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistDTO {
    private Long id;
    private Long userId;
    private Long videoId;
    private LocalDateTime addedAt;

    // The video details, dynamically fetched via OpenFeign
    private VideoDTO video;
}
