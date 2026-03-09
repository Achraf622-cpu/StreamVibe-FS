package com.streamvibe.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "watch_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long videoId;

    private LocalDateTime watchedAt;

    // Progress in seconds
    private Integer progressTime;

    private Boolean completed;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        watchedAt = LocalDateTime.now();
        if (completed == null) {
            completed = false;
        }
    }
}
