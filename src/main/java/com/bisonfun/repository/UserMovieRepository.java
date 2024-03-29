package com.bisonfun.repository;

import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.UserMovie;
import com.bisonfun.entity.UserMovieKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMovieRepository extends CrudRepository<UserMovie, UserMovieKey> {
    @Query("SELECT userMovie FROM UserMovie userMovie WHERE userMovie.id.userId = ?1 AND userMovie.status = ?2")
    List<UserMovie> findUserMovieByUserIdAndStatus(int userId, VideoConsumingStatus status);
    Long countUserMovieByUserIdAndStatus(int userId, VideoConsumingStatus status);
    @Query("SELECT SUM(userMovie.episodes) FROM UserMovie userMovie WHERE userMovie.id.userId = ?1")
    Integer countWatchedEpisodesByUser(int userId);
    @Query("SELECT AVG(userMovie.score) FROM UserMovie userMovie WHERE userMovie.score > 0 AND userMovie.id.userId = ?1")
    Float getMeanScoreByUser(int userId);
}
