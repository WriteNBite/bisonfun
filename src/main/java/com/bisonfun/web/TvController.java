package com.bisonfun.web;

import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.*;
import com.bisonfun.service.TvService;
import com.bisonfun.service.UserService;
import com.bisonfun.service.UserTvService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static java.util.Arrays.asList;

@Controller
@Slf4j
public class TvController{
    private final AniListClient aniListClient;
    private final TmdbClient tmdbClient;
    private final UserService userService;
    private final UserTvService userTvService;
    private final TvService tvService;

    @Autowired
    public TvController(AniListClient aniListClient, TmdbClient tmdbClient, UserService userService, UserTvService userTvService, TvService tvService) {
        this.aniListClient = aniListClient;
        this.tmdbClient = tmdbClient;
        this.userService = userService;
        this.userTvService = userTvService;
        this.tvService = tvService;
    }

    @GetMapping("/tv/{id}")
    public String tvPage(Model model, @PathVariable int id, Principal principal) throws JSONException {
        log.info("User {} get TV {}", principal == null ? "Unknown" : principal.getName(), id);
        TMDBTVShow show = tmdbClient.parseShowById(id);

        if (show.isAnime()) {
            VideoEntertainment anime;
            try {
                anime = aniListClient.parseAnimeByName(show.getTitle());
            }   catch (TooManyAnimeRequestsException e) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
            } catch (ContentNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            String animeLink = "/anime/"+anime.getId();
            return "redirect:" + animeLink;
        }

        UserTv userTv;
        if (principal == null) {
            userTv = new UserTv();
        }else {
            User user = userService.getUserByUsername(principal.getName());
            userTv = userTvService.getUserTvById(user.getId(), show.getId());
        }

        List<VideoEntertainment> tvRecommendations = tmdbClient.parseTVRecommendations(id);

        model.addAttribute("content", show);
        model.addAttribute("recommendations", tvRecommendations);

        model.addAttribute("actions", asList(VideoConsumingStatus.values()));
        model.addAttribute("userTv", userTv);

        return "content_tv";
    }

    @PostMapping("/tv/{tvId}")
    public String saveTvToList(UserTv userTv, @PathVariable int tvId, Principal principal) throws ContentNotFoundException {
        if(principal == null){
            String loginLink = "/login";
            return "redirect:"+loginLink;
        }
        if(userTv.getStatus() == null){
            log.warn("UserTv is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        log.info("Update TV {} in User {} List", tvId, principal.getName());
        User user = userService.getUserByUsername(principal.getName());
        Tv dbTv = tvService.updateTv(tvId);

        userTvService.createUserTv(userTv, user, dbTv);


        String tvLink = "/tv/"+tvId;
        return "redirect:" + tvLink;
    }

    @DeleteMapping("/tv/{tvId}")
    public String deleteTvFromList(@PathVariable int tvId, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        log.info("Delete TV {} from User {} List", tvId, user.getUsername());
        userTvService.deleteTvFromUserList(new UserTvKey(user.getId(), tvId));
        String tvLink = "/tv/"+tvId;
        return "redirect:" + tvLink;
    }
}
