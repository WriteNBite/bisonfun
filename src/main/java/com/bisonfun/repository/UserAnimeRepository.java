package com.bisonfun.repository;

import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.model.enums.VideoContentType;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.entity.UserAnimeKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnimeRepository extends CrudRepository<UserAnime, UserAnimeKey> {
    @Query("SELECT userAnime FROM UserAnime userAnime WHERE userAnime.id.userId = ?1 AND userAnime.status = ?2")
    List<UserAnime> findUserAnimeByUserIdAndStatus(int userId, VideoConsumingStatus status);
    @Query("SELECT userAnime FROM UserAnime userAnime WHERE userAnime.id.userId = ?1 AND userAnime.status = ?2 AND userAnime.anime.type = ?3")
    List<UserAnime> findUserAnimeByUserIdAndStatusAndType(int userId, VideoConsumingStatus status, VideoContentType type);
    Long countUserAnimeByUserIdAndStatus(int userId, VideoConsumingStatus status);
    @Query("SELECT SUM(userAnime.episodes) FROM UserAnime userAnime WHERE userAnime.id.userId = ?1")
    Integer countWatchedEpisodesByUser(int userId);
    @Query("SELECT AVG(userAnime.score) FROM UserAnime userAnime WHERE userAnime.score > 0 AND userAnime.id.userId = ?1")
    Float getMeanScoreByUser(int userId);
}
