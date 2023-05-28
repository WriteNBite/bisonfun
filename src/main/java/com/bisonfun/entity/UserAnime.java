package com.bisonfun.entity;

import com.bisonfun.model.enums.VideoConsumingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
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

    public UserAnime(UserAnimeKey id, VideoConsumingStatus status) {
        this.id = id;
        this.status = status;
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
