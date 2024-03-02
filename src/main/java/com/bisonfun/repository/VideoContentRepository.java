package com.bisonfun.repository;

import com.bisonfun.entity.VideoContent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoContentRepository extends CrudRepository<VideoContent, Long> {
    @Query("select v from VideoContent v where v.aniListId = ?1")
    Optional<VideoContent> findByAniListId(int anilistId);

    @Query("select v from VideoContent v where v.tmdbId = ?1 and v.type = 'Movie'")
    Optional<VideoContent> findByTmdbMovieId(int tmdbMovieId);

    @Query("select v from VideoContent v where v.tmdbId = ?1 and v.type = 'TV'")
    Optional<VideoContent> findByTmdbTvId(int tmdbTvId);

}
