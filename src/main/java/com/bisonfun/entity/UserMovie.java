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

    public UserMovie(UserMovieKey id, VideoConsumingStatus status) {
        this.id = id;
        this.status = status;
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
