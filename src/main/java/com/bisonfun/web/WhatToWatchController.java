package com.bisonfun.web;

import com.bisonfun.domain.VideoEntertainment;
import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.entity.*;
import com.bisonfun.service.UserAnimeService;
import com.bisonfun.service.UserMovieService;
import com.bisonfun.service.UserService;
import com.bisonfun.service.UserTvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
public class WhatToWatchController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserAnimeService userAnimeService;
    @Autowired
    private UserMovieService userMovieService;
    @Autowired
    private UserTvService userTvService;

    @GetMapping("/wtw")
    public String getWhatToWatch(){
        return "random_vc";
    }
    @GetMapping("/random")
    public String getRandomVideoContent(
            @RequestParam("movie-planned") Optional<Boolean> moviePlanned, @RequestParam("movie-watching") Optional<Boolean> movieWatching, @RequestParam("movie-complete") Optional<Boolean> movieComplete,
            @RequestParam("tv-planned") Optional<Boolean> tvPlanned, @RequestParam("tv-watching") Optional<Boolean> tvWatching, @RequestParam("tv-complete") Optional<Boolean> tvComplete,
            @RequestParam("anime-planned") Optional<Boolean> animePlanned, @RequestParam("anime-watching") Optional<Boolean> animeWatching, @RequestParam("anime-complete") Optional<Boolean> animeComplete,
            Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<VideoEntertainment> contentList = new ArrayList<>();
        //Movies
        if(moviePlanned.isPresent()){
            contentList.addAll(userMovieService.getContentListByStatus(user.getId(), VideoConsumingStatus.PLANNED));
        }
        if(movieWatching.isPresent()){
            contentList.addAll(userMovieService.getContentListByStatus(user.getId(), VideoConsumingStatus.WATCHING));
        }
        if(movieComplete.isPresent()){
            contentList.addAll(userMovieService.getContentListByStatus(user.getId(), VideoConsumingStatus.COMPLETE));
        }

        //Tv
        if(tvPlanned.isPresent()){
            contentList.addAll(userTvService.getContentListByStatus(user.getId(), VideoConsumingStatus.PLANNED));
        }
        if(tvWatching.isPresent()){
            contentList.addAll(userTvService.getContentListByStatus(user.getId(), VideoConsumingStatus.WATCHING));
        }
        if(tvComplete.isPresent()){
            contentList.addAll(userTvService.getContentListByStatus(user.getId(), VideoConsumingStatus.COMPLETE));
        }

        //Anime
        if(animePlanned.isPresent()){
            contentList.addAll(userAnimeService.getVideoContentListByStatus(user.getId(), VideoConsumingStatus.PLANNED));
        }
        if(animeWatching.isPresent()){
            contentList.addAll(userAnimeService.getVideoContentListByStatus(user.getId(), VideoConsumingStatus.WATCHING));
        }
        if(animeComplete.isPresent()){
            contentList.addAll(userAnimeService.getVideoContentListByStatus(user.getId(), VideoConsumingStatus.COMPLETE));
        }

        if(contentList.isEmpty()){
            String wtwLink = "/wtw";
            return "redirect:"+wtwLink;
        }

        Random random = new Random();
        VideoEntertainment content = contentList.get(random.nextInt(contentList.size()));

        return "redirect:"+contentPage(content);
    }

    private String contentPage(VideoEntertainment content){
        StringBuilder linkBuilder = new StringBuilder("/");
        if(content.isAnime()){
            linkBuilder.append("anime");
        }else{
            linkBuilder.append(content.getType().toString().toLowerCase());
        }
        linkBuilder.append("/")
                .append(content.getId());
        return linkBuilder.toString();
    }

}
