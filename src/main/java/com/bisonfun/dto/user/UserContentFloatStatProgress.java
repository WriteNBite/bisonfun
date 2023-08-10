package com.bisonfun.dto.user;

import com.bisonfun.dto.Progress;

public class UserContentFloatStatProgress  implements Progress<UserContentType, Float> {
    UserContentType key;
    Float value;

    public UserContentFloatStatProgress(UserContentType key, Float value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public UserContentType getKey() {
        return key;
    }
}
