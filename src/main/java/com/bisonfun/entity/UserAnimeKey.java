package com.bisonfun.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserAnimeKey implements Serializable {

    public UserAnimeKey(){

    }

    public UserAnimeKey(int userId, int animeId) {
        this.userId = userId;
        this.animeId = animeId;
    }

    @Column(name = "user_id")
    int userId;

    @Column(name = "anime_id")
    int animeId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAnimeId() {
        return animeId;
    }

    public void setAnimeId(int animeId) {
        this.animeId = animeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAnimeKey that = (UserAnimeKey) o;
        return userId == that.userId && animeId == that.animeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, animeId);
    }
}
