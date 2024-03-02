package com.bisonfun.service;

import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import com.bisonfun.entity.*;
import com.bisonfun.repository.UserAnimeRepository;
import com.bisonfun.repository.UserMovieRepository;
import com.bisonfun.repository.UserTvRepository;
import com.bisonfun.repository.VideoContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserListsMigrationService {
    private final UserAnimeRepository userAnimeRepository;
    private final UserMovieRepository userMovieRepository;
    private final UserTvRepository userTvRepository;
    private final UserVideoContentService userVideoContentService;
    private final VideoContentRepository videoContentRepository;

    @Autowired
    public UserListsMigrationService(UserAnimeRepository userAnimeRepository, UserMovieRepository userMovieRepository, UserTvRepository userTvRepository, UserVideoContentService userVideoContentService, VideoContentRepository videoContentRepository) {
        this.userAnimeRepository = userAnimeRepository;
        this.userMovieRepository = userMovieRepository;
        this.userTvRepository = userTvRepository;
        this.userVideoContentService = userVideoContentService;
        this.videoContentRepository = videoContentRepository;
    }

    public void migrateOldUserListsToUserVideoContent(){
        List<UserAnime> userAnimeList = getTotalUserAnimeLists();
        for(UserAnime userAnime : userAnimeList){
            Optional<VideoContent> videoContent = videoContentRepository.findByAniListId(userAnime.getId().getAnimeId());
            if(videoContent.isEmpty()){
                log.error(new ContentNotFoundException().getMessage());
                continue;
            }
            UserVideoContent userVideoContent = new UserVideoContent();
            userVideoContent.setScore(userAnime.getScore());
            userVideoContent.setEpisodes(userAnime.getEpisodes());
            userVideoContent.setStatus(userAnime.getStatus());
            try {
                userVideoContentService.updateUserVideoContent(userVideoContent, userAnime.getUser(), videoContent.get());
            } catch (ContentNotFoundException e) {
                log.error(e.getMessage());
            } catch (TooManyAnimeRequestsException e) {
                throw new RuntimeException(e);
            }
        }

        List<UserMovie> userMovieList = getTotalUserMovieLists();
        for (UserMovie userMovie : userMovieList){
            Optional<VideoContent> videoContent = videoContentRepository.findByTmdbMovieId(userMovie.getId().getMovieId());
            if(videoContent.isEmpty()){
                log.error(new ContentNotFoundException().getMessage());
                continue;
            }
            UserVideoContent userVideoContent = new UserVideoContent();
            userVideoContent.setScore(userMovie.getScore());
            userVideoContent.setEpisodes(userMovie.getEpisodes());
            userVideoContent.setStatus(userMovie.getStatus());
            try {
                userVideoContentService.updateUserVideoContent(userVideoContent, userMovie.getUser(), videoContent.get());
            } catch (ContentNotFoundException e) {
                log.error(e.getMessage());
            } catch (TooManyAnimeRequestsException e) {
                throw new RuntimeException(e);
            }
        }

        List<UserTv> userTvList = getTotalUserTvLists();
        for (UserTv userTv : userTvList){
            Optional<VideoContent> videoContent = videoContentRepository.findByTmdbTvId(userTv.getId().getTvId());
            if(videoContent.isEmpty()){
                log.error(new ContentNotFoundException().getMessage());
                continue;
            }
            UserVideoContent userVideoContent = new UserVideoContent();
            userVideoContent.setScore(userTv.getScore());
            userVideoContent.setEpisodes(userTv.getEpisodes());
            userVideoContent.setStatus(userTv.getStatus());
            try {
                userVideoContentService.updateUserVideoContent(userVideoContent, userTv.getUser(), videoContent.get());
            } catch (ContentNotFoundException e) {
                log.error(e.getMessage());
            } catch (TooManyAnimeRequestsException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private List<UserAnime> getTotalUserAnimeLists(){
        List<UserAnime> userAnimeList = new ArrayList<>();
        userAnimeRepository.findAll().forEach(userAnimeList::add);
        return userAnimeList;
    }

    private List<UserMovie> getTotalUserMovieLists(){
        List<UserMovie> userMovieList = new ArrayList<>();
        userMovieRepository.findAll().forEach(userMovieList::add);
        return userMovieList;
    }

    private List<UserTv> getTotalUserTvLists(){
        List<UserTv> userTvList = new ArrayList<>();
        userTvRepository.findAll().forEach(userTvList::add);
        return userTvList;
    }
}
