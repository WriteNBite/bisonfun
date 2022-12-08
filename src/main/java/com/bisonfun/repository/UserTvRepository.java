package com.bisonfun.repository;

import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.entity.UserTv;
import com.bisonfun.entity.UserTvKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserTvRepository extends CrudRepository<UserTv, UserTvKey> {
    @Query("SELECT userTv FROM UserTv userTv WHERE userTv.id.userId = ?1 AND userTv.status = ?2")
    List<UserTv> findUserTvByUserIdAndStatus(int userId, VideoConsumingStatus status);
}
