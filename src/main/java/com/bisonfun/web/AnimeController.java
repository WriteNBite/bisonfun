package com.bisonfun.web;

import com.bisonfun.domain.AniAnime;
import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.entity.UserAnimeKey;
import com.bisonfun.service.AnimeService;
import com.bisonfun.service.UserAnimeService;
import com.bisonfun.service.UserService;
import com.bisonfun.utilities.AniParser;
import com.bisonfun.utilities.ContentNotFoundException;
import com.bisonfun.utilities.TooManyAnimeRequestsException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

import static java.util.Arrays.asList;

@Controller
public class AnimeController extends VideoContentController {

    private final UserService userService;
    private final AnimeService animeService;
    private final UserAnimeService userAnimeService;
    private final AniParser aniParser;

    @Autowired
    public AnimeController(UserService userService, AnimeService animeService, UserAnimeService userAnimeService, AniParser aniParser) {
        this.userService = userService;
        this.animeService = animeService;
        this.userAnimeService = userAnimeService;
        this.aniParser = aniParser;
    }

    @GetMapping("/anime/{id}")
    public String animePage(@PathVariable int id, Model model, Principal principal) throws JSONException, TooManyAnimeRequestsException {

        AniAnime anime;
        try {
            anime = aniParser.parseById(id);
        } catch (ContentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        UserAnime userAnime;
        if (principal != null) {
            User user = userService.getUserByUsername(principal.getName());
            userAnime = userAnimeService.getUserAnimeById(user.getId(), anime.getId());
        } else {
            userAnime = new UserAnime();
        }

        model.addAttribute("anime", anime);
        model.addAttribute("actions", asList(VideoConsumingStatus.values()));
        model.addAttribute("userAnime", userAnime);


        return "content_anime";
    }

    @PostMapping("/anime/{animeId}")
    public String completeAnime(UserAnime userAnime, @PathVariable int animeId, Principal principal) throws ContentNotFoundException, TooManyAnimeRequestsException {
        if (principal == null) {//if user isn't logged in
            String loginLink = "/login";
            return "redirect:"+loginLink;
        }
        if (userAnime.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = userService.getUserByUsername(principal.getName());
        Anime dbAnime = animeService.findById(animeId);
        AniAnime anime = aniParser.parseById(animeId);
        if (dbAnime == null) {
            dbAnime = animeService.addNewAnime(anime);
        }else{
            dbAnime = animeService.updating(dbAnime, anime);
        }
        //getUserAnimeKey and UserAnime
        UserAnimeKey userAnimeKey = new UserAnimeKey(user.getId(), dbAnime.getId());
        userAnime.setId(userAnimeKey);
        userAnime.setUser(user);
        userAnime.setAnime(dbAnime);

        UserAnime dbUserAnime = userAnimeService.getUserAnimeById(userAnimeKey);
        if(userAnime.getEpisodes() != dbUserAnime.getEpisodes()){//if episode number changed
            userAnime.setStatus(updateStatus(userAnime, anime));
        }else if(userAnime.getStatus() != dbUserAnime.getStatus()){//if status changed
            userAnime.setEpisodes(updateEpisodes(userAnime, anime));
        }
        userAnimeService.saveUserAnime(userAnime);

        String redirectLink = "/anime/"+animeId;
        return "redirect:"+redirectLink;
    }

    @DeleteMapping("/anime/{animeId}")
    public String deleteAnimeFromList(@PathVariable int animeId, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        userAnimeService.deleteAnimeFromUserList(new UserAnimeKey(user.getId(), animeId));
        String redirectLink = "/anime/"+animeId;
        return "redirect:"+redirectLink;
    }
}
