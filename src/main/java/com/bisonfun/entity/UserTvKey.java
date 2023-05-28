package com.bisonfun.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class UserTvKey implements Serializable {
    @Column(name = "user_id")
    int userId;

    @Column(name = "tv_id")
    int tvId;
}
