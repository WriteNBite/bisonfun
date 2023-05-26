package com.bisonfun.entity;

import com.bisonfun.dto.enums.VideoConsumingStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class UserAnime implements UserTvContent {
    @EmbeddedId
    UserAnimeKey id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @MapsId("animeId")
    @JoinColumn(name = "anime_id")
    Anime anime;

    VideoConsumingStatus status;

    @Column(columnDefinition = "integer default 0")
    int episodes;
    @Column(columnDefinition = "integer default 0")
    int score;

    public UserAnime() {
    }

    public UserAnime(UserAnimeKey id, VideoConsumingStatus status) {
        this.id = id;
        this.status = status;
    }

    public UserAnimeKey getId() {
        return id;
    }

    public void setId(UserAnimeKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
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
        return "UserAnime{" +
                "id=" + id +
                ", user=" + user +
                ", anime=" + anime +
                ", status=" + status +
                ", episodes=" + episodes +
                ", score=" + score +
                '}';
    }
}
