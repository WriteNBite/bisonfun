package com.bisonfun.client.anilist;

import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.NoAccessException;
import com.bisonfun.client.Pagination;
import com.bisonfun.entity.User;
import com.bisonfun.model.AniAnime;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.MediaListStatus;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Component
public class AniListClient {
    private final AniListApiResponse aniListApiResponse;
    private final UserService userService;
    private final Gson gson;

    @Autowired
    public AniListClient(AniListApiResponse aniListApiResponse, UserService userService, Gson gson) {
        this.aniListApiResponse = aniListApiResponse;
        this.userService = userService;
        this.gson = gson;
    }

    //parse anime lists
    public Pagination<VideoEntertainment> parse(String query) throws TooManyAnimeRequestsException {
        return parse(query, 1);
    }
    public Pagination<VideoEntertainment> parse(String query, int page) throws TooManyAnimeRequestsException {
        log.info("Parsing video entertainment anime:\nquery:{}\npage{}", query, page);
        JSONObject root = aniListApiResponse.getAnimeList(query, page);

        // get page info
        JSONObject pageInfo = root.getJSONObject("pageInfo");

        //get anime list
        JSONArray animeArray = root.getJSONArray("media");

        //get number of content
        int count = animeArray.length();
        //get last page
        int lastPage = pageInfo.getInt("lastPage");

        //parsing Anime List
        List<VideoEntertainment> documents = new ArrayList<>();
        log.debug("---Anime search list parsing---");
        log.debug(query+" "+page);
        for(int i = 0; i < count; i++){
            VideoEntertainment anime = gson.fromJson(String.valueOf(animeArray.getJSONObject(i)), AniAnime.class);
            documents.add(anime);
        }
        return new Pagination<>(page, count, documents, lastPage);
    }
    //parse anime trends
    public List<VideoEntertainment> parseAnimeTrends() throws TooManyAnimeRequestsException {
        JSONArray animeArray;
        try {
            animeArray = aniListApiResponse.getAnimeTrends().getJSONArray("media");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<VideoEntertainment> trends = new ArrayList<>();
        for(int i = 0; i < animeArray.length(); i++){
            VideoEntertainment anime = gson.fromJson(String.valueOf(animeArray.getJSONObject(i)), AniAnime.class);
            trends.add(anime);
        }
        return trends;
    }
    //parse anime(as VideoEntertainment)
    public VideoEntertainment parseAnimeById(int id) throws ContentNotFoundException, TooManyAnimeRequestsException {
        JSONObject jsonAnime = aniListApiResponse.getAnimeById(id);

        return parseAnime(jsonAnime);
    }
    public VideoEntertainment parseAnimeByName(String name) throws ContentNotFoundException, TooManyAnimeRequestsException {
        JSONObject jsonAnime = aniListApiResponse.getAnimeByName(name);

        return parseAnime(jsonAnime);
    }
    private VideoEntertainment parseAnime(JSONObject jsonAnime){
        return gson.fromJson(String.valueOf(jsonAnime), AniAnime.class);
    }
    //parse anime(as AniAnime)
    public AniAnime parseById(int id) throws ContentNotFoundException, TooManyAnimeRequestsException {
        JSONObject root = aniListApiResponse.getAnimeById(id);

        return gson.fromJson(String.valueOf(root), AniAnime.class);
    }

    public String getAniListToken(User user, String code){
        //if token is null or not null but token has been expired
        if(user.getAniToken() == null || user.getTokenExpires().after(new Timestamp(Calendar.getInstance().getTime().getTime()))){
            //set tokens
            JSONObject jsonObject = aniListApiResponse.getAniToken(code);

            String token = jsonObject.getString("access_token");//get AniList Token
            int secondsAvailable = jsonObject.getInt("expires_in");
            long expiresIn = new Timestamp(Calendar.getInstance().getTime().getTime()).getTime() + secondsAvailable * 1000L;//get expires time in milliseconds

            //set
            user.setAniToken(token);
            user.setTokenExpires(new Timestamp(expiresIn));
            userService.saveUser(user);
        }
        return user.getAniToken();
    }
    public int getViewerId(String token){
        JSONObject viewer = aniListApiResponse.getUserByToken(token).getJSONObject("Viewer");
        return viewer.getInt("id");
    }

    public List<UserAnime> parseMediaList(int userId, MediaListStatus status) throws TooManyAnimeRequestsException {
        int page = 1;
        List<UserAnime> animeList = new ArrayList<>();
        JSONObject root;
        log.info("Parsing user {} {} media list", userId, status);
        do {
            root = aniListApiResponse.getUserMediaList(userId, page, status);
            log.debug(root.toString());

            JSONArray mediaList = root.getJSONArray("mediaList");
            for(int i = 0; i < mediaList.length(); i++){
                if(page>50){
                    log.error("Something went wrong; Page is bigger than 50");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                animeList.add(gson.fromJson(String.valueOf(mediaList.getJSONObject(i)), UserAnime.class));
            }
            page++;
        }while (root.getJSONObject("pageInfo").getBoolean("hasNextPage"));
        return animeList;
    }

}
