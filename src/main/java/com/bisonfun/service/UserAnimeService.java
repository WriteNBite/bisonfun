package com.bisonfun.service;

import com.bisonfun.domain.VideoEntertainment;
import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.domain.enums.VideoContentType;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.entity.UserAnimeKey;
import com.bisonfun.repository.UserAnimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserAnimeService {
    final
    UserAnimeRepository userAnimeRepo;

    @Autowired
    public UserAnimeService(UserAnimeRepository userAnimeRepo) {
        this.userAnimeRepo = userAnimeRepo;
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
        List<VideoEntertainment> animeList = new ArrayList<>();
        for(UserAnime userAnime : userAnimeRepo.findUserAnimeByUserIdAndStatus(userId, status)){
            Anime anime = userAnime.getAnime();
            animeList.add(new VideoEntertainment(anime.getId(), true, anime.getType(), anime.getTitle(), null, anime.getYear() > 0 ? Date.valueOf(anime.getYear()+"-1-1") : null, anime.getPoster()));
        }
        log.info("Anime list: "+animeList);
        return animeList;
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
        userAnimeRepo.findById(userAnimeId).ifPresent(userAnime -> userAnimeRepo.delete(userAnime));
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
