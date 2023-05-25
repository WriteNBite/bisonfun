package com.bisonfun.web;

import com.bisonfun.domain.enums.MediaListStatus;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.service.AnimeService;
import com.bisonfun.service.UserAnimeService;
import com.bisonfun.service.UserService;
import com.bisonfun.utilities.AniParser;
import com.bisonfun.utilities.JSONParser;
import com.bisonfun.utilities.TooManyAnimeRequestsException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Controller
public class AniListController {
    private final JSONParser jsonParser;
    private final UserService userService;
    private final AniParser aniParser;
    private final AnimeService animeService;
    private final UserAnimeService userAnimeService;

    @Autowired
    public AniListController(JSONParser jsonParser, UserService userService, AniParser aniParser, AnimeService animeService, UserAnimeService userAnimeService) {
        this.jsonParser = jsonParser;
        this.userService = userService;
        this.aniParser = aniParser;
        this.animeService = animeService;
        this.userAnimeService = userAnimeService;
    }

    @GetMapping("/anilist")
    public String aniListRegistration(@RequestParam String code, Principal principal) throws TooManyAnimeRequestsException {
        if(principal==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User user = userService.getUserByUsername(principal.getName());
        if(user == null){
            return "redirect:/login";
        }

        String token;
        //if token is null or not null but token has been expired
        if(user.getAniToken() == null || (user.getAniToken() != null && user.getTokenExpires().after(new Timestamp(Calendar.getInstance().getTime().getTime())))){
            //set tokens
            JSONObject jsonObject = jsonParser.getAniToken(code);

            token = jsonObject.getString("access_token");//get AniList Token
            int secondsAvailable = jsonObject.getInt("expires_in");
            long expiresIn = new Timestamp(Calendar.getInstance().getTime().getTime()).getTime() + secondsAvailable * 1000L;//get expires time in milliseconds

            //set
            user.setAniToken(token);
            user.setTokenExpires(new Timestamp(expiresIn));
            userService.saveUser(user);
        }else{
            token = user.getAniToken();
        }


        JSONObject viewer = jsonParser.getUserByToken(token).getJSONObject("Viewer");
        int viewerId = viewer.getInt("id");

        //processing planned anime list
        List<UserAnime> plannedAnime = aniParser.parseMediaList(viewerId, MediaListStatus.PLANNING);
        animeService.saveAnimeFromUserAnimeList(plannedAnime);
        userAnimeService.saveUserList(user, plannedAnime);
        //processing watching anime list
        List<UserAnime> watchingAnime = aniParser.parseMediaList(viewerId, MediaListStatus.CURRENT);
        animeService.saveAnimeFromUserAnimeList(watchingAnime);
        userAnimeService.saveUserList(user, watchingAnime);
        //processing finished anime list
        List<UserAnime> finishedAnime = aniParser.parseMediaList(viewerId, MediaListStatus.COMPLETED);
        animeService.saveAnimeFromUserAnimeList(finishedAnime);
        userAnimeService.saveUserList(user, finishedAnime);


        String redirectLink = "/users/"+user.getUsername()+"/anime";
        return "redirect:"+redirectLink;
    }
}
