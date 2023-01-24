package com.bisonfun.web;

import com.bisonfun.domain.TMDBTVShow;
import com.bisonfun.domain.VideoEntertainment;
import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.entity.*;
import com.bisonfun.service.TvService;
import com.bisonfun.service.UserService;
import com.bisonfun.service.UserTvService;
import com.bisonfun.utilities.*;
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
import java.text.DateFormat;
import java.util.List;

import static java.util.Arrays.asList;

@Controller
public class TvController extends VideoContentController{
    @Autowired
    private AniParser aniParser;
    @Autowired
    private TMDBParser tmdbParser;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTvService userTvService;
    @Autowired
    private TvService tvService;

    @GetMapping("/tv/{id}")
    public String tvPage(Model model, @PathVariable int id, Principal principal) throws JSONException {
        TMDBTVShow show = tmdbParser.parseShowById(id);

        if (show.isAnime()) {
            VideoEntertainment anime;
            try {
                anime = aniParser.parseAnimeByName(show.getTitle());
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

        List<VideoEntertainment> tvRecommendations = tmdbParser.parseTVRecommendations(id);

        String lastAired = show.getLastAired() == null ? null : DateFormat.getDateInstance().format(show.getLastAired());
        String releaseDate = show.getReleaseDate() == null ? "???" : DateFormat.getDateInstance().format(show.getReleaseDate());
        String timeToWatch = show.getTimeToWatch();

        model.addAttribute("content", show);
        model.addAttribute("lastAired", lastAired);
        model.addAttribute("releaseDate", releaseDate);
        model.addAttribute("timeToWatch", timeToWatch);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        User user = userService.getUserByUsername(principal.getName());
        Tv dbTv = tvService.findById(tvId);
        TMDBTVShow tmdbtvShow = tmdbParser.parseShowById(tvId);
        if(dbTv == null){
            dbTv = tvService.addNewMovie(tmdbtvShow);
        }else{
            dbTv = tvService.updating(dbTv, tmdbtvShow);
        }
        UserTvKey userTvKey = new UserTvKey(user.getId(), dbTv.getId());
        userTv.setId(userTvKey);
        userTv.setUser(user);
        userTv.setTv(dbTv);

        UserTv dbUserTv = userTvService.getUserTvById(userTvKey);
        if(userTv.getEpisodes() != dbUserTv.getEpisodes()){
            userTv.setStatus(updateStatus(userTv, tmdbtvShow));
        }else if(userTv.getStatus() != dbUserTv.getStatus()){
            userTv.setEpisodes(updateEpisodes(userTv, tmdbtvShow));
        }
        userTvService.saveUserTv(userTv);


        String tvLink = "/tv/"+tvId;
        return "redirect:" + tvLink;
    }

    @DeleteMapping("/tv/{tvId}")
    public String deleteTvFromList(@PathVariable int tvId, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        userTvService.deleteTvFromUserList(new UserTvKey(user.getId(), tvId));
        String tvLink = "/tv/"+tvId;
        return "redirect:" + tvLink;
    }
}
