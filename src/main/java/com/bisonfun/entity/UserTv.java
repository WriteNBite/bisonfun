package com.bisonfun.entity;

import com.bisonfun.dto.enums.VideoConsumingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
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

    public UserTv(UserTvKey id, VideoConsumingStatus status) {
        this.id = id;
        this.status = status;
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
