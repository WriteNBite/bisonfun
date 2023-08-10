package com.bisonfun.dto.user;

import com.bisonfun.dto.Progress;
import com.bisonfun.dto.ProgressBar;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserContentIntStatProgressBar implements ProgressBar<UserContentType, Integer> {
    Set<Progress<UserContentType, Integer>> userContentIntStatProgresses;

    public UserContentIntStatProgressBar() {
        this.userContentIntStatProgresses = new LinkedHashSet<>();
    }

    @Override
    public Set<Progress<UserContentType, Integer>> getProgressParts() {
        return userContentIntStatProgresses;
    }

    @Override
    public void addProgressParts(Progress<UserContentType, Integer> progressPart) {
        userContentIntStatProgresses.add(progressPart);
    }

    @Override
    public float getPercentByKey(UserContentType key) {
        return ((float) getProgressByKey(key).getValue() / getTotal())*100;
    }

    @Override
    public boolean isEmpty() {
        return getTotal() == 0;
    }

    @Override
    public Progress<UserContentType, Integer> getProgressByKey(UserContentType key) {
        return userContentIntStatProgresses.stream()
                .filter(progress -> progress.getKey() == key)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Integer getTotal() {
        int ans = 0;
        for (Progress<UserContentType, Integer> progress : userContentIntStatProgresses){
            ans += progress.getValue();
        }
        return ans;
    }
}
