package com.bisonfun.web;

import com.bisonfun.model.enums.MediaListStatus;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.service.AnimeService;
import com.bisonfun.service.UserAnimeService;
import com.bisonfun.service.UserService;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
public class AniListController {
    private final UserService userService;
    private final AniListClient aniListClient;
    private final AnimeService animeService;
    private final UserAnimeService userAnimeService;

    @Autowired
    public AniListController(UserService userService, AniListClient aniListClient, AnimeService animeService, UserAnimeService userAnimeService) {
        this.userService = userService;
        this.aniListClient = aniListClient;
        this.animeService = animeService;
        this.userAnimeService = userAnimeService;
    }

    @GetMapping("/anilist")
    public String aniListRegistration(@RequestParam String code, Principal principal) throws TooManyAnimeRequestsException {
        if(principal==null){
            log.error("Non-authorised user trying to use aniList registration");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User user = userService.getUserByUsername(principal.getName());
        if(user == null){
            log.error("User is null");
            return "redirect:/login";
        }

        String token = aniListClient.getAniListToken(user, code);

        int viewerId = aniListClient.getViewerId(token);

        for(MediaListStatus status : MediaListStatus.values()){
            List<UserAnime> userAnimeList = aniListClient.parseMediaList(viewerId, status);
            animeService.saveAnimeFromUserAnimeList(userAnimeList);
            userAnimeService.saveUserList(user, userAnimeList);
        }

        String redirectLink = "/users/"+user.getUsername()+"/anime";
        return "redirect:"+redirectLink;
    }
}
