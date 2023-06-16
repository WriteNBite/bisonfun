package com.bisonfun.service;

import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.model.enums.VideoContentType;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.entity.UserAnimeKey;
import com.bisonfun.mapper.AnimeMapper;
import com.bisonfun.repository.UserAnimeRepository;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAnimeService extends UserVideoContentService {
    final
    UserAnimeRepository userAnimeRepo;
    final AniListClient aniListClient;
    final AnimeMapper animeMapper;

    @Autowired
    public UserAnimeService(UserAnimeRepository userAnimeRepo, AniListClient aniListClient, AnimeMapper animeMapper) {
        this.userAnimeRepo = userAnimeRepo;
        this.aniListClient = aniListClient;
        this.animeMapper = animeMapper;
    }

    public long[] getSizeOfLists(int userId){
        return new long[]{
                getSizeOfListByStatus(userId, VideoConsumingStatus.PLANNED),
                getSizeOfListByStatus(userId, VideoConsumingStatus.WATCHING),
                getSizeOfListByStatus(userId, VideoConsumingStatus.COMPLETE)
        };
    }

    public long getSizeOfListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get Size of User {} {} Anime List", userId, status);
        return userAnimeRepo.countUserAnimeByUserIdAndStatus(userId, status);
    }

    public List<UserAnime> getUserAnimeListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get UserAnime {} list by User {}", status, userId);
        return userAnimeRepo.findUserAnimeByUserIdAndStatus(userId, status);
    }

    public List<Anime> getAnimeListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get User {} Anime {} list",userId, status);
        List<Anime> animeList = userAnimeRepo.findUserAnimeByUserIdAndStatus(userId, status).stream().map(UserAnime::getAnime).collect(Collectors.toList());
        log.debug("Anime list: "+animeList);
        return animeList;
    }

    public List<VideoEntertainment> getVideoContentListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get User {} Video Entertainment {} Anime list",userId, status);
        List<Anime> animeList = getAnimeListByStatus(userId, status);
        log.debug("Anime list: "+animeList);
        return animeList.stream().map(animeMapper::toVideoEntertainment).collect(Collectors.toList());
    }

    public List<VideoEntertainment> getVideoContentListByStatusAndType(int userId, VideoConsumingStatus status, VideoContentType type){
        log.info("Get User {} Video Entertainment {} {} Anime list",userId, status, type);
        List<VideoEntertainment> animeList = userAnimeRepo.findUserAnimeByUserIdAndStatusAndType(userId, status, type).stream().map(userAnime -> animeMapper.toVideoEntertainment(userAnime.getAnime())).collect(Collectors.toList());
        log.debug("Anime list: "+animeList);
        return animeList;
    }

    public void createUserAnime(UserAnime userAnime, User user, Anime anime) throws ContentNotFoundException, TooManyAnimeRequestsException {
        //getUserAnimeKey and UserAnime
        UserAnimeKey userAnimeKey = new UserAnimeKey(user.getId(), anime.getId());
        userAnime.setId(userAnimeKey);
        userAnime.setUser(user);
        userAnime.setAnime(anime);

        UserAnime dbUserAnime = getUserAnimeById(userAnimeKey);
        if(userAnime.getEpisodes() != dbUserAnime.getEpisodes()){//if episode number changed
            userAnime.setStatus(updateStatus(userAnime, aniListClient.parseById(anime.getId())));
        }else if(userAnime.getStatus() != dbUserAnime.getStatus()){//if status changed
            userAnime.setEpisodes(updateEpisodes(userAnime, aniListClient.parseById(anime.getId())));
        }
        saveUserAnime(userAnime);
    }

    public UserAnime getUserAnimeById(UserAnimeKey userAnimeId){
        log.info("Get UserAnime by User {} and Anime {} by UserAnimeKey", userAnimeId.getUserId(), userAnimeId.getAnimeId());
        UserAnime userAnime = userAnimeRepo.findById(userAnimeId).orElse(new UserAnime());
        log.debug("UserAnime: "+userAnime);
        return userAnime;
    }

    public UserAnime getUserAnimeById(int userId, int animeId){
        log.info("Get UserAnime by User {} and Anime {}", userId, animeId);
        return getUserAnimeById(new UserAnimeKey(userId, animeId));
    }

    public UserAnime saveUserAnime(UserAnime userAnime){
        log.info("Save Anime {} in User {} list", userAnime.getAnime().getId(), userAnime.getUser().getId());
        return userAnimeRepo.save(userAnime);
    }

    public void deleteAnimeFromUserList(UserAnimeKey userAnimeId){
        log.info("Delete Anime {} from User {} list", userAnimeId.getAnimeId(), userAnimeId.getUserId());
        userAnimeRepo.findById(userAnimeId).ifPresent(userAnimeRepo::delete);
    }

    public void saveUserList(User user, List<UserAnime> animeList){
        for(UserAnime userAnime : animeList){
            UserAnime dbUserAnime = userAnimeRepo.findById(new UserAnimeKey(user.getId(), userAnime.getAnime().getId())).orElse(null);
            if(dbUserAnime != null){
                //set pk
                userAnime.setId(dbUserAnime.getId());
                //set the biggest score
                userAnime.setScore(Math.max(dbUserAnime.getScore(), userAnime.getScore()));
                //set the biggest progress
                userAnime.setEpisodes(Math.max(dbUserAnime.getEpisodes(), userAnime.getEpisodes()));
                //set user
                userAnime.setUser(user);
                //set status with the biggest stage
                userAnime.setStatus(userAnime.getStatus().getStage() > dbUserAnime.getStatus().getStage() ? userAnime.getStatus() : dbUserAnime.getStatus());
            }else{
                //set pk
                userAnime.setId(new UserAnimeKey(user.getId(), userAnime.getAnime().getId()));
                //set user
                userAnime.setUser(user);
            }
            userAnimeRepo.save(userAnime);
        }
    }
}
