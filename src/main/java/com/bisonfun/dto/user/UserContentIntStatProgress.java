package com.bisonfun.dto.user;

import com.bisonfun.dto.Progress;

public class UserContentIntStatProgress implements Progress<UserContentType, Integer> {
    UserContentType key;
    Integer value;

    public UserContentIntStatProgress(UserContentType key, Integer value) {
        this.value = value;
        this.key = key;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public UserContentType getKey() {
        return key;
    }
}
