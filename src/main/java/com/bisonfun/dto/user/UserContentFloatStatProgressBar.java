package com.bisonfun.dto.user;

import com.bisonfun.dto.Progress;
import com.bisonfun.dto.ProgressBar;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserContentFloatStatProgressBar  implements ProgressBar<UserContentType, Float> {
    Set<Progress<UserContentType, Float>> userContentFloatStatProgresses;

    public UserContentFloatStatProgressBar() {
        this.userContentFloatStatProgresses = new LinkedHashSet<>();
    }

    @Override
    public Set<Progress<UserContentType, Float>> getProgressParts() {
        return userContentFloatStatProgresses;
    }

    @Override
    public void addProgressParts(Progress<UserContentType, Float> progressPart) {
        userContentFloatStatProgresses.add(progressPart);
    }

    @Override
    public float getPercentByKey(UserContentType key) {
        return (getProgressByKey(key).getValue() / getTotal())*100;
    }

    @Override
    public boolean isEmpty() {
        return getTotal() == 0;
    }

    @Override
    public Progress<UserContentType, Float> getProgressByKey(UserContentType key) {
        return userContentFloatStatProgresses.stream()
                .filter(progress -> progress.getKey() == key)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Float getTotal() {
        float ans = 0;
        for (Progress<UserContentType, Float> progress : userContentFloatStatProgresses){
            ans += progress.getValue();
        }
        return ans;
    }
}
