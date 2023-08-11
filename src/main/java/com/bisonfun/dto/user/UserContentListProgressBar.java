package com.bisonfun.dto.user;

import com.bisonfun.dto.Progress;
import com.bisonfun.dto.ProgressBar;
import com.bisonfun.model.enums.VideoConsumingStatus;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserContentListProgressBar implements ProgressBar<VideoConsumingStatus, Long> {
    Set<Progress<VideoConsumingStatus, Long>> contentListProgresses;

    public UserContentListProgressBar() {
        this.contentListProgresses = new LinkedHashSet<>();
    }

    @Override
    public Set<Progress<VideoConsumingStatus, Long>> getProgressParts() {
        return contentListProgresses;
    }

    @Override
    public void addProgressParts(Progress<VideoConsumingStatus, Long> progressPart) {
        contentListProgresses.add(progressPart);
    }

    @Override
    public float getPercentByKey(VideoConsumingStatus key) {
        return ((float) getProgressByKey(key).getValue() / getTotal())*100;
    }

    @Override
    public boolean isEmpty() {
        return getTotal() == 0;
    }

    @Override
    public Progress<VideoConsumingStatus, Long> getProgressByKey(VideoConsumingStatus key) {
        return contentListProgresses.stream()
                .filter(progress -> progress.getKey() == key)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Long getTotal() {
        Long ans = 0L;
        for(Progress<VideoConsumingStatus, Long> progress : contentListProgresses){
            ans += progress.getValue();
        }
        return ans;
    }
}
