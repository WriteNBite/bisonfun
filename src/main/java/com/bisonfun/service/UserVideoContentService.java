package com.bisonfun.service;

import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.entity.*;
import com.bisonfun.mapper.VideoContentMapper;
import com.bisonfun.model.TVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.model.enums.VideoContentCategory;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;
import com.bisonfun.repository.UserVideoContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserVideoContentService  {
    final UserVideoContentRepository userVideoContentRepository;
    final AniListClient aniListClient;
    final TmdbClient tmdbClient;
    final VideoContentMapper videoContentMapper;

    @Autowired
    public UserVideoContentService(UserVideoContentRepository userVideoContentRepository, AniListClient aniListClient, TmdbClient tmdbClient, VideoContentMapper videoContentMapper) {
        this.userVideoContentRepository = userVideoContentRepository;
        this.aniListClient = aniListClient;
        this.tmdbClient = tmdbClient;
        this.videoContentMapper = videoContentMapper;
    }

    public List<UserVideoContent> getUserVideoContentListByStatusAndCategoryAndTypes(int userId, VideoConsumingStatus status, VideoContentCategory category, List<VideoContentType> types){
        log.info("Get User{} {} {} list by User {}", category, types, status, userId);
        List<VideoConsumingStatus> statuses = new ArrayList<>();
        statuses.add(status);
        List<VideoContentCategory> categories = new ArrayList<>();
        categories.add(category);
        return userVideoContentRepository.findUserVideoContentByUserIdAndCategoriesAndStatusesAndTypes(userId, categories, statuses, types);
    }

    public List<VideoContent> getVideoContentListByStatusAndCategoryAndTypes(int userId, VideoConsumingStatus status, VideoContentCategory category, List<VideoContentType> types){
        log.info("Get User {} {} {} {} list", userId, category, types, status);
        List<VideoConsumingStatus> statuses = new ArrayList<>();
        statuses.add(status);
        List<VideoContentCategory> categories = new ArrayList<>();
        categories.add(category);
        List<VideoContent> videoContentList = userVideoContentRepository.findUserVideoContentByUserIdAndCategoriesAndStatusesAndTypes(userId, categories, statuses, types)
                .stream()
                .map(UserVideoContent::getVideoContent)
                .collect(Collectors.toList());
        log.debug("Video Content List: "+videoContentList);
        return videoContentList;
    }

    public List<VideoEntertainment> getVideoEntertainmentListByStatusAndCategoryAndTypes(int userId, VideoConsumingStatus status, VideoContentCategory category, List<VideoContentType> types){
        log.info("Get User {} Video Entertainment {} {} {} list", userId, category, types, status);
        List<VideoContent> videoContentList = getVideoContentListByStatusAndCategoryAndTypes(userId, status, category, types);
        log.debug("Video Entertainment list: "+videoContentList);
        return videoContentList.stream()
                .map(videoContentMapper::toVideoEntertainment)
                .collect(Collectors.toList());
    }

    public void updateUserVideoContent(UserVideoContent userVideoContent, User user, VideoContent videoContent) throws ContentNotFoundException, TooManyAnimeRequestsException {
        UserVideoContentKey userVideoContentKey = new UserVideoContentKey(user.getId(), videoContent.getId());
        userVideoContent.setId(userVideoContentKey);
        userVideoContent.setUser(user);
        userVideoContent.setVideoContent(videoContent);

        Optional<UserVideoContent> dbUserVideoContent = getUserVideoContentById(userVideoContentKey);
        if (dbUserVideoContent.isPresent()) {
            if (userVideoContent.getEpisodes() != dbUserVideoContent.get().getEpisodes()) {//if episode number changed
                userVideoContent.setStatus(updateStatus(userVideoContent));
            } else if (userVideoContent.getStatus() != dbUserVideoContent.get().getStatus()) {//if status changed
                userVideoContent.setEpisodes(updateEpisodes(userVideoContent));
            }
        }
        saveUserVideoContent(userVideoContent);
    }

    private VideoConsumingStatus updateStatus(UserVideoContent userTvContent) throws ContentNotFoundException, TooManyAnimeRequestsException {
        TVShow tvShow = getTvShow(userTvContent.getVideoContent());
        if((userTvContent.getEpisodes() > 0 || userTvContent.getStatus() == VideoConsumingStatus.COMPLETE) && userTvContent.getEpisodes() < tvShow.getEpisodes()){//if less watched episodes as it is make it watching
            return VideoConsumingStatus.WATCHING;
        }else if(tvShow.getStatus() == VideoContentStatus.RELEASED && userTvContent.getEpisodes() > 0 && userTvContent.getEpisodes() == tvShow.getEpisodes()){// if it released and all episodes watched then it completed
            return VideoConsumingStatus.COMPLETE;
        }
        return userTvContent.getStatus();
    }

    private int updateEpisodes(UserVideoContent userTvContent) throws ContentNotFoundException, TooManyAnimeRequestsException {
        TVShow tvShow = getTvShow(userTvContent.getVideoContent());
        if(userTvContent.getStatus() == VideoConsumingStatus.COMPLETE) {//if complete then make all episodes watched
            return tvShow.getEpisodes();
        }else if(userTvContent.getStatus() == VideoConsumingStatus.PLANNED){// if planned then 0 episodes watched
            return 0;
        }
        return userTvContent.getEpisodes();
    }

    private TVShow getTvShow(VideoContent videoContent) throws ContentNotFoundException, TooManyAnimeRequestsException {
        TVShow tvShow;
        switch (videoContent.getCategory()){
            case MAINSTREAM: tvShow = tmdbClient.parseShowById(videoContent.getTmdbId()); break;
            case ANIME: tvShow = aniListClient.parseById(videoContent.getAniListId()); break;
            default: throw new IllegalArgumentException();
        }
        return tvShow;
    }

    private Optional<UserVideoContent> getUserVideoContentById(UserVideoContentKey userVideoContentKey) {
        log.info("Get User Video Content by User {} and VideoContent {} by UserVideoContentKey", userVideoContentKey.getUserId(), userVideoContentKey.getVideoContentId());
        Optional<UserVideoContent> userVideoContent = userVideoContentRepository.findById(userVideoContentKey);
        log.debug("UserVideoContent: "+userVideoContent);
        return userVideoContent;
    }

    public Optional<UserVideoContent> getUserVideoContentById(int userId, long videoContentId){
        log.info("Get User Video Content by User {} and Video Content {}", userId, videoContentId);
        return getUserVideoContentById(new UserVideoContentKey(userId, videoContentId));
    }

    private UserVideoContent saveUserVideoContent(UserVideoContent userVideoContent) {
        log.info("Save Video Content {} in User {} list", userVideoContent.getVideoContent().getId(), userVideoContent.getUser().getId());
        return userVideoContentRepository.save(userVideoContent);
    }

    public void deleteVideoContentFromUserList(UserVideoContentKey userVideoContentKey){
        log.info("Delete Video Content {} from User {} list", userVideoContentKey.getVideoContentId(), userVideoContentKey.getUserId());
        userVideoContentRepository.findById(userVideoContentKey).ifPresent(userVideoContentRepository::delete);
    }

    public void saveUserList(User user, List<UserVideoContent> videoContentList){
        for(UserVideoContent userVideoContent : videoContentList){
            UserVideoContent dbUserVideoContent = userVideoContentRepository.findById(new UserVideoContentKey(user.getId(), userVideoContent.getVideoContent().getId())).orElse(null);
            if(dbUserVideoContent != null){
                //set pk
                userVideoContent.setId(dbUserVideoContent.getId());
                //set the biggest score
                userVideoContent.setScore(Math.max(dbUserVideoContent.getScore(), userVideoContent.getScore()));

                // set db progress and status if it has bigger progress
                if(dbUserVideoContent.getEpisodes() > userVideoContent.getEpisodes()){
                    userVideoContent.setEpisodes(dbUserVideoContent.getEpisodes());
                    userVideoContent.setScore(dbUserVideoContent.getScore());
                }else if (dbUserVideoContent.getEpisodes() == userVideoContent.getEpisodes()){
                    userVideoContent.setStatus(userVideoContent.getStatus().getStage() > dbUserVideoContent.getStatus().getStage() ? userVideoContent.getStatus() : dbUserVideoContent.getStatus());
                }
            }else {
                //set pk
                userVideoContent.setId(new UserVideoContentKey(user.getId(), userVideoContent.getVideoContent().getId()));
            }
            //set user
            userVideoContent.setUser(user);
            userVideoContentRepository.save(userVideoContent);
        }
    }
}
