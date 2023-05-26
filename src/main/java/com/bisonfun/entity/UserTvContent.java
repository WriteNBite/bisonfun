package com.bisonfun.entity;

import com.bisonfun.dto.enums.VideoConsumingStatus;

public interface UserTvContent {
    int getEpisodes();
    VideoConsumingStatus getStatus();
}
