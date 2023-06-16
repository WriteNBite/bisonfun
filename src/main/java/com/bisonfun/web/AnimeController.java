package com.bisonfun.web;

import com.bisonfun.model.AniAnime;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.entity.UserAnimeKey;
import com.bisonfun.service.AnimeService;
import com.bisonfun.service.UserAnimeService;
import com.bisonfun.service.UserService;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AnimeController{

    private final UserService userService;
    private final AnimeService animeService;
    private final UserAnimeService userAnimeService;
    private final AniListClient aniListClient;

    @Autowired
    public AnimeController(UserService userService, AnimeService animeService, UserAnimeService userAnimeService, AniListClient aniListClient) {
        this.userService = userService;
        this.animeService = animeService;
        this.userAnimeService = userAnimeService;
        this.aniListClient = aniListClient;
    }

    @GetMapping("/anime/{id}")
    public String animePage(@PathVariable int id, Model model, Principal principal) throws JSONException, TooManyAnimeRequestsException {
        log.info("User {} get Anime {}", principal == null ? "Unknown" : principal.getName(), id);
        AniAnime anime;
        try {
            anime = aniListClient.parseById(id);
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
    public String updateAnimeList(UserAnime userAnime, @PathVariable int animeId, Principal principal) throws ContentNotFoundException, TooManyAnimeRequestsException {
        if (principal == null) {//if user isn't logged in
            String loginLink = "/login";
            return "redirect:"+loginLink;
        }
        if (userAnime.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Update Anime {} in User {} List", animeId, principal.getName());
        Anime dbAnime = animeService.updateAnime(animeId);

        User user = userService.getUserByUsername(principal.getName());
        userAnimeService.createUserAnime(userAnime, user, dbAnime);

        String redirectLink = "/anime/"+animeId;
        return "redirect:"+redirectLink;
    }

    @DeleteMapping("/anime/{animeId}")
    public String deleteAnimeFromList(@PathVariable int animeId, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        log.info("Delete Anime {} from User {} List", animeId, user.getUsername());
        userAnimeService.deleteAnimeFromUserList(new UserAnimeKey(user.getId(), animeId));
        String redirectLink = "/anime/"+animeId;
        return "redirect:"+redirectLink;
    }
}
