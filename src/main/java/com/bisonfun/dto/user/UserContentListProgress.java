package com.bisonfun.dto.user;

import com.bisonfun.dto.Progress;
import com.bisonfun.model.enums.VideoConsumingStatus;

public class UserContentListProgress implements Progress<VideoConsumingStatus, Long> {
    VideoConsumingStatus key;
    Long value;

    public UserContentListProgress(VideoConsumingStatus key, Long value) {
        this.value = value;
        this.key = key;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public VideoConsumingStatus getKey() {
        return key;
    }
}
