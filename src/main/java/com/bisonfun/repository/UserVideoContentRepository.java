package com.bisonfun.repository;

import com.bisonfun.entity.UserVideoContent;
import com.bisonfun.entity.UserVideoContentKey;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.model.enums.VideoContentCategory;
import com.bisonfun.model.enums.VideoContentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVideoContentRepository extends CrudRepository<UserVideoContent, UserVideoContentKey> {

    @Query("SELECT userVideoContent FROM UserVideoContent userVideoContent WHERE userVideoContent.id.userId = ?1 AND userVideoContent.videoContent.category IN ?2 AND userVideoContent.status IN ?3 AND userVideoContent.videoContent.type IN ?4")
    List<UserVideoContent> findUserVideoContentByUserIdAndCategoriesAndStatusesAndTypes(int userId, List<VideoContentCategory> categories, List<VideoConsumingStatus> statuses, List<VideoContentType> types);

    @Query("SELECT COUNT(userVideoContent) FROM UserVideoContent userVideoContent WHERE userVideoContent.id.userId = ?1 AND userVideoContent.videoContent.category IN ?2 AND userVideoContent.status IN ?3 AND userVideoContent.videoContent.type IN ?4")
    Long countUserVideoContentByUserIdAndCategoriesAndStatusesAndTypes(int userId, List<VideoContentCategory> categories, List<VideoConsumingStatus> statuses, List<VideoContentType> types);

    @Query("SELECT SUM(userVideoContent.episodes) FROM UserVideoContent userVideoContent WHERE userVideoContent.id.userId = ?1 AND userVideoContent.videoContent.category IN ?2 AND userVideoContent.videoContent.type IN ?3")
    Integer countWatchedEpisodesByUserAndCategoriesAndTypes(int userId, List<VideoContentCategory> categories, List<VideoContentType> types);

    @Query("SELECT AVG(userVideoContent.score) FROM UserVideoContent userVideoContent WHERE userVideoContent.id.userId = ?1 AND userVideoContent.score > 0 AND userVideoContent.videoContent.category IN ?2 AND userVideoContent.videoContent.type IN ?3")
    Float getMeanScoreByUserAndCategoriesAndTypes(int userId, List<VideoContentCategory> categories, List<VideoContentType> types);
}
