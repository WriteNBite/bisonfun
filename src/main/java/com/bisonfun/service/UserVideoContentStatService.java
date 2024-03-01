package com.bisonfun.service;

import com.bisonfun.dto.ProgressBar;
import com.bisonfun.dto.user.*;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.model.enums.VideoContentCategory;
import com.bisonfun.model.enums.VideoContentType;
import com.bisonfun.repository.UserVideoContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserVideoContentStatService {
    final UserVideoContentRepository userVideoContentRepository;

    @Autowired
    public UserVideoContentStatService(UserVideoContentRepository userVideoContentRepository) {
        this.userVideoContentRepository = userVideoContentRepository;
    }

    public ProgressBar<VideoConsumingStatus, Long> getVideoContentCategoryListProgressBar(int userId, VideoContentCategory category, List<VideoContentType> types){
        ProgressBar<VideoConsumingStatus, Long> progressBar = new UserContentListProgressBar();
        for(VideoConsumingStatus status : VideoConsumingStatus.values()){
            progressBar.addProgressParts(new UserContentListProgress(status, getSizeOfListByStatusAndCategory(userId, status, category, types)));
        }
        return progressBar;
    }

    public ProgressBar<UserContentType, Integer> getEpisodeStatProgressBar(int userId){
        ProgressBar<UserContentType, Integer> progressBar = new UserContentIntStatProgressBar();
        List<VideoContentCategory> categories = new ArrayList<>();
        List<VideoContentType> types = new ArrayList<>();
        //Count movies
        categories.add(VideoContentCategory.MAINSTREAM);
        types.add(VideoContentType.MOVIE);
        int movieSum = userVideoContentRepository.countWatchedEpisodesByUserAndCategoriesAndTypes(userId, categories, types);
        progressBar.addProgressParts(new UserContentIntStatProgress(UserContentType.MOVIE, movieSum));
        //Count series
        types.clear();
        types.add(VideoContentType.TV);
        int tvSum = userVideoContentRepository.countWatchedEpisodesByUserAndCategoriesAndTypes(userId, categories, types);
        progressBar.addProgressParts(new UserContentIntStatProgress(UserContentType.TV, tvSum));
        //Count anime
        categories.clear();
        categories.add(VideoContentCategory.ANIME);
        types.add(VideoContentType.MOVIE);
        int animeSum = userVideoContentRepository.countWatchedEpisodesByUserAndCategoriesAndTypes(userId, categories, types);
        progressBar.addProgressParts(new UserContentIntStatProgress(UserContentType.ANIME, animeSum));

        return progressBar;
    }

    public ProgressBar<UserContentType, Float> getMeanScoreStatProgressBar(int userId){
        ProgressBar<UserContentType, Float> progressBar = new UserContentFloatStatProgressBar();
        List<VideoContentCategory> categories = new ArrayList<>();
        List<VideoContentType> types = new ArrayList<>();
        //Count movies
        categories.add(VideoContentCategory.MAINSTREAM);
        types.add(VideoContentType.MOVIE);
        float movieScore = userVideoContentRepository.getMeanScoreByUserAndCategoriesAndTypes(userId, categories, types);
        progressBar.addProgressParts(new UserContentFloatStatProgress(UserContentType.MOVIE, movieScore));
        //Count tv series
        types.clear();
        types.add(VideoContentType.TV);
        float tvScore = userVideoContentRepository.getMeanScoreByUserAndCategoriesAndTypes(userId, categories, types);
        progressBar.addProgressParts(new UserContentFloatStatProgress(UserContentType.TV, tvScore));
        //Count anime
        categories.clear();
        categories.add(VideoContentCategory.ANIME);
        types.add(VideoContentType.MOVIE);
        float animeScore = userVideoContentRepository.getMeanScoreByUserAndCategoriesAndTypes(userId, categories, types);
        progressBar.addProgressParts(new UserContentFloatStatProgress(UserContentType.ANIME, animeScore));

        return progressBar;
    }

    private Long getSizeOfListByStatusAndCategory(int userId, VideoConsumingStatus status, VideoContentCategory category, List<VideoContentType> types) {
        log.info("Get Size of User {} {} {} List", userId, status, category);
        List<VideoConsumingStatus> statuses = new ArrayList<>();
        statuses.add(status);
        List<VideoContentCategory> categories = new ArrayList<>();
        categories.add(category);

        return userVideoContentRepository.countUserVideoContentByUserIdAndCategoriesAndStatusesAndTypes(userId, categories, statuses, types);
    }
}
