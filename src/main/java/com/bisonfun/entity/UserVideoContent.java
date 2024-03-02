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
public class UserVideoContent {
    @EmbeddedId
    UserVideoContentKey id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("videoContentId")
    @JoinColumn(name = "video_content_id")
    VideoContent videoContent;

    VideoConsumingStatus status;

    @Column(columnDefinition = "integer default 0")
    int episodes;
    @Column(columnDefinition = "integer default 0")
    int score;

    public UserVideoContent(UserVideoContentKey id, VideoConsumingStatus status) {
        this.id = id;
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserVideoContent{" +
                "id=" + id +
                ", user=" + user +
                ", videoContent=" + videoContent +
                ", status=" + status +
                ", episodes=" + episodes +
                ", score=" + score +
                '}';
    }
}
