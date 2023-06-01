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

import java.sql.Date;
import java.util.ArrayList;
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

    public int[] getSizeOfLists(int userId){
        return new int[]{
                getUserAnimeListByStatus(userId, VideoConsumingStatus.PLANNED).size(),
                getUserAnimeListByStatus(userId, VideoConsumingStatus.WATCHING).size(),
                getUserAnimeListByStatus(userId, VideoConsumingStatus.COMPLETE).size()
        };
    }

    public List<UserAnime> getUserAnimeListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get userAnime list\nUser id: "+userId+"\nStatus: "+status);
        return userAnimeRepo.findUserAnimeByUserIdAndStatus(userId, status);
    }

    public List<Anime> getAnimeListByStatus(int userId, VideoConsumingStatus status){
        log.info("Getting "+status+" anime list\nUser id: "+userId);
        List<Anime> animeList = new ArrayList<>();
        for(UserAnime userAnime : userAnimeRepo.findUserAnimeByUserIdAndStatus(userId, status)){
            animeList.add(userAnime.getAnime());
        }
        log.info("Anime list: "+animeList);
        return animeList;
    }

    public List<VideoEntertainment> getVideoContentListByStatus(int userId, VideoConsumingStatus status){
        log.info("Getting "+status+" anime list\nUser id: "+userId);
        List<Anime> animeList = getAnimeListByStatus(userId, status);
        log.info("Anime list: "+animeList);
        return animeList.stream().map(animeMapper::toVideoEntertainment).collect(Collectors.toList());
    }

    public List<VideoEntertainment> getVideoContentListByStatusAndType(int userId, VideoConsumingStatus status, VideoContentType type){
        log.info("Getting "+status+" anime list\nUser id: "+userId);
        List<VideoEntertainment> animeList = new ArrayList<>();
        for(UserAnime userAnime : userAnimeRepo.findUserAnimeByUserIdAndStatusAndType(userId, status, type)){
            Anime anime = userAnime.getAnime();
            animeList.add(new VideoEntertainment(anime.getId(), true, anime.getType(), anime.getTitle(), null, anime.getYear() > 0 ? Date.valueOf(anime.getYear()+"-1-1") : null, anime.getPoster()));
        }
        log.info("Anime list: "+animeList);
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
        log.info("Getting userAnime");
        UserAnime userAnime = userAnimeRepo.findById(userAnimeId).orElse(new UserAnime());
        log.info("UserAnime: "+userAnime);
        return userAnime;
    }

    public UserAnime getUserAnimeById(int userId, int animeId){
        log.info("Get userAnime \nUser id: "+userId+"\nAnime id: "+animeId);
        return getUserAnimeById(new UserAnimeKey(userId, animeId));
    }

    public UserAnime saveUserAnime(UserAnime userAnime){
        log.info("Saving userAnime");
        return userAnimeRepo.save(userAnime);
    }

    public void deleteAnimeFromUserList(UserAnimeKey userAnimeId){
        log.info("Deleting anime from user list");
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
