package com.bisonfun.entity;

import com.bisonfun.domain.enums.VideoConsumingStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class UserTv implements UserTvContent{
    @EmbeddedId
    UserTvKey id;

    @ManyToOne
    @OnDelete(action= OnDeleteAction.CASCADE)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("tvId")
    @JoinColumn(name = "tv_id")
    Tv tv;

    VideoConsumingStatus status;

    @Column(columnDefinition = "integer default 0")
    int episodes;
    @Column(columnDefinition = "integer default 0")
    int score;

    public UserTv() {
    }

    public UserTv(UserTvKey id, VideoConsumingStatus status) {
        this.id = id;
        this.status = status;
    }

    public UserTvKey getId() {
        return id;
    }

    public void setId(UserTvKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tv getTv() {
        return tv;
    }

    public void setTv(Tv tv) {
        this.tv = tv;
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
        return "UserTv{" +
                "id=" + id +
                ", user=" + user +
                ", tv=" + tv +
                ", status=" + status +
                ", episodes=" + episodes +
                ", score=" + score +
                '}';
    }
}
