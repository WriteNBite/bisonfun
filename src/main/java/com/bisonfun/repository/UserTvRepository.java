package com.bisonfun.repository;

import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.UserTv;
import com.bisonfun.entity.UserTvKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserTvRepository extends CrudRepository<UserTv, UserTvKey> {
    @Query("SELECT userTv FROM UserTv userTv WHERE userTv.id.userId = ?1 AND userTv.status = ?2")
    List<UserTv> findUserTvByUserIdAndStatus(int userId, VideoConsumingStatus status);
    Long countUserTvByUserIdAndStatus(int userId, VideoConsumingStatus status);

    @Query("SELECT SUM(userTv.episodes) FROM UserTv userTv WHERE userTv.id.userId = ?1")
    Integer countWatchedEpisodesByUser(int userId);
    @Query("SELECT AVG(userTv.score) FROM UserTv userTv WHERE userTv.score > 0 AND userTv.id.userId = ?1")
    Float getMeanScoreByUser(int userId);
}
