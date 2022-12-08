package com.bisonfun.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserMovieKey implements Serializable {

    public UserMovieKey(){}

    public UserMovieKey(int userId, int movieId){
        this.userId = userId;
        this.movieId = movieId;
    }

    @Column(name = "user_id")
    int userId;

    @Column(name = "movie_id")
    int movieId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserMovieKey that = (UserMovieKey) o;
        return userId == that.userId && movieId == that.movieId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, movieId);
    }
}
