package com.bisonfun.service;

import com.bisonfun.dto.ProgressBar;
import com.bisonfun.dto.user.*;
import com.bisonfun.repository.UserAnimeRepository;
import com.bisonfun.repository.UserMovieRepository;
import com.bisonfun.repository.UserTvRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserStatService {
    final UserAnimeRepository userAnimeRepository;
    final UserTvRepository userTvRepository;
    final UserMovieRepository userMovieRepository;

    @Autowired
    public UserStatService(UserAnimeRepository userAnimeRepository, UserTvRepository userTvRepository, UserMovieRepository userMovieRepository) {
        this.userAnimeRepository = userAnimeRepository;
        this.userTvRepository = userTvRepository;
        this.userMovieRepository = userMovieRepository;
    }

    public ProgressBar<UserContentType, Integer> getEpisodeStatProgressBar(int userId){
        ProgressBar<UserContentType, Integer> progressBar = new UserContentIntStatProgressBar();
        Integer animeSum = userAnimeRepository.countWatchedEpisodesByUser(userId);
        progressBar.addProgressParts(new UserContentIntStatProgress(UserContentType.ANIME, animeSum != null ? animeSum : 0));
        Integer movieSum = userMovieRepository.countWatchedEpisodesByUser(userId);
        progressBar.addProgressParts(new UserContentIntStatProgress(UserContentType.MOVIE, movieSum != null ? movieSum : 0));
        Integer tvSum = userTvRepository.countWatchedEpisodesByUser(userId);
        progressBar.addProgressParts(new UserContentIntStatProgress(UserContentType.TV, tvSum != null ? tvSum : 0));
        return progressBar;
    }

    public ProgressBar<UserContentType, Float> getMeanScoreStatProgressBar(int userId){
        ProgressBar<UserContentType, Float> progressBar = new UserContentFloatStatProgressBar();
        Float animeScore = userAnimeRepository.getMeanScoreByUser(userId);
        progressBar.addProgressParts(new UserContentFloatStatProgress(UserContentType.ANIME, animeScore != null ? animeScore : 0));
        Float movieScore = userMovieRepository.getMeanScoreByUser(userId);
        progressBar.addProgressParts(new UserContentFloatStatProgress(UserContentType.MOVIE, movieScore != null ? movieScore : 0));
        Float tvScore = userTvRepository.getMeanScoreByUser(userId);
        progressBar.addProgressParts(new UserContentFloatStatProgress(UserContentType.TV, tvScore != null ? tvScore : 0));
        return progressBar;
    }
}
