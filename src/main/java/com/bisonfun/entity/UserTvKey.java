package com.bisonfun.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserTvKey implements Serializable {
    public UserTvKey(){}

    public UserTvKey(int userId, int tvId) {
        this.userId = userId;
        this.tvId = tvId;
    }

    @Column(name = "user_id")
    int userId;

    @Column(name = "tv_id")
    int tvId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTvId() {
        return tvId;
    }

    public void setTvId(int tvId) {
        this.tvId = tvId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTvKey userTvKey = (UserTvKey) o;
        return userId == userTvKey.userId && tvId == userTvKey.tvId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tvId);
    }
}
