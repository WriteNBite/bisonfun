package com.bisonfun.entity;

import com.bisonfun.dto.enums.VideoConsumingStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class UserMovie {
    @EmbeddedId
    UserMovieKey id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    Movie movie;

    VideoConsumingStatus status;

    @Column(columnDefinition = "integer default 0")
    int episodes;
    @Column(columnDefinition = "integer default 0")
    int score;

    public UserMovie(){}

    public UserMovie(UserMovieKey id, VideoConsumingStatus status) {
        this.id = id;
        this.status = status;
    }

    public UserMovieKey getId() {
        return id;
    }

    public void setId(UserMovieKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public VideoConsumingStatus getStatus() {
        return status;
    }

    public void setStatus(VideoConsumingStatus status) {
        this.status = status;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "UserMovie{" +
                "id=" + id +
                ", user=" + user +
                ", movie=" + movie +
                ", status=" + status +
                ", episodes=" + episodes +
                ", score=" + score +
                '}';
    }
}
