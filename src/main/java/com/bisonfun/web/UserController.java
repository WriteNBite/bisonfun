package com.bisonfun.web;

import com.bisonfun.dto.ProgressBar;
import com.bisonfun.dto.user.UserContentType;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.*;
import com.bisonfun.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;

@Controller
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserAnimeService userAnimeService;
    private final UserMovieService userMovieService;
    private final UserTvService userTvService;
    private final Environment environment;
    private final UserStatService userStatService;

    @Autowired
    public UserController(UserService userService, UserAnimeService userAnimeService, UserMovieService userMovieService, UserTvService userTvService, Environment environment, UserStatService userStatService) {
        this.userService = userService;
        this.userAnimeService = userAnimeService;
        this.userMovieService = userMovieService;
        this.userTvService = userTvService;
        this.environment = environment;
        this.userStatService = userStatService;
    }

    @GetMapping("/users/{username}")
    public String getUsernamePage(Model model, @PathVariable String username){
        User user = userService.getUserByUsername(username);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("User {} page is visited", user.getUsername());
        model.addAttribute("login", user.getUsername());

        int userId = user.getId();

        ProgressBar<VideoConsumingStatus, Long> animeListProgressBar = userAnimeService.getListProgressBar(userId);
        model.addAttribute("animeListProgressBar", animeListProgressBar);
        ProgressBar<VideoConsumingStatus, Long> movieListProgressBar = userMovieService.getListProgressBar(userId);
        model.addAttribute("movieListProgressBar", movieListProgressBar);
        ProgressBar<VideoConsumingStatus, Long> tvListProgressBar = userTvService.getListProgressBar(userId);
        model.addAttribute("tvListProgressBar", tvListProgressBar);

        ProgressBar<UserContentType, Integer> episodeStatProgressBar = userStatService.getEpisodeStatProgressBar(userId);
        model.addAttribute("episodeProgressBar", episodeStatProgressBar);
        ProgressBar<UserContentType, Float> meanScoreProgressBar = userStatService.getMeanScoreStatProgressBar(userId);
        model.addAttribute("meanScoreProgressBar", meanScoreProgressBar);

        return "user";
    }

    @GetMapping("/users/{username}/anime")
    public String getUserAnimeList(Model model, @PathVariable String username, Principal principal){
        User user = userService.getUserByUsername(username);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("User {} anime list page is visited", user.getUsername());
        model.addAttribute("login", user.getUsername());

        //UserLists
        Map<VideoConsumingStatus, List<UserAnime>> userLists = new LinkedHashMap<>();
        boolean empty = true;
        for(VideoConsumingStatus status : VideoConsumingStatus.values()){
            List<UserAnime> userAnimeList = userAnimeService.getUserAnimeListByStatus(user.getId(), status);
            userLists.put(status, userAnimeList);
            empty = empty && userAnimeList.isEmpty();
        }
        model.addAttribute("userLists", userLists);
        model.addAttribute("isEmpty", empty);

        //Principal
        boolean loggedList;
        if(principal != null){
            loggedList = principal.getName().equalsIgnoreCase(username);
        }else{
            loggedList = false;
        }
        String aniListLink = "https://anilist.co/api/v2/oauth/authorize?client_id="+environment.getProperty("bisonfun.anilist.client.id")+"&redirect_uri="+environment.getProperty("bisonfun.anilist.redirect_uri")+"&response_type=code";
        model.addAttribute("anilist", aniListLink);
        model.addAttribute("loggedList", loggedList);


        return "user_anime_list";
    }

    @GetMapping("users/{username}/movie")
    public String getUserMovieList(Model model, @PathVariable String username){
        User user = userService.getUserByUsername(username);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("User {} movie list page is visited", user.getUsername());
        model.addAttribute("login", user.getUsername());

        //UserLists
        Map<VideoConsumingStatus, List<UserMovie>> userLists = new LinkedHashMap<>();
        boolean empty = true;
        for(VideoConsumingStatus status : VideoConsumingStatus.values()){
            List<UserMovie> userMovieList = userMovieService.getUserMovieListByStatus(user.getId(), status);
            userLists.put(status, userMovieList);
            empty = empty && userMovieList.isEmpty();
        }
        model.addAttribute("userLists", userLists);
        model.addAttribute("isEmpty", empty);

        return "user_movie_list";
    }

    @GetMapping("users/{username}/tv")
    public String getUserTvList(Model model, @PathVariable String username){
        User user = userService.getUserByUsername(username);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("User {} tv list page is visited", user.getUsername());
        model.addAttribute("login", user.getUsername());

        //UserLists
        Map<VideoConsumingStatus, List<UserTv>> userLists = new LinkedHashMap<>();
        boolean empty = true;
        for(VideoConsumingStatus status : VideoConsumingStatus.values()){
            List<UserTv> userTvList = userTvService.getUserTvListByStatus(user.getId(), status);
            userLists.put(status, userTvList);
            empty = empty && userTvList.isEmpty();
        }
        model.addAttribute("userLists", userLists);
        model.addAttribute("isEmpty", empty);

        return "user_tv_list";
    }

}
