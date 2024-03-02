package com.bisonfun.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class UserVideoContentKey implements Serializable {
    @Column(name = "user_id")
    int userId;

    @Column(name = "video_content_id")
    long videoContentId;
}
