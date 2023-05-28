package com.bisonfun.utilities;

import com.bisonfun.builder.JSONAniBuilder;
import com.bisonfun.builder.JSONUserAniBuilder;
import com.bisonfun.model.AniAnime;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.MediaListStatus;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.UserAnime;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AniParser {
    private final JSONParser parser;

    @Autowired
    public AniParser(JSONParser parser) {
        this.parser = parser;
    }

    //parse anime lists
    public Pagination<VideoEntertainment> parse(String query) throws TooManyAnimeRequestsException {
        return parse(query, 1);
    }
    public Pagination<VideoEntertainment> parse(String query, int page) throws TooManyAnimeRequestsException {
        JSONObject root = parser.getAnimeList(query, page);

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
        log.info("---Anime search list parsing---");
        log.info(query+" "+page);
        for(int i = 0; i < count; i++){
            //New one
            JSONAniBuilder aniBuilder = JSONAniBuilder.getInstance(animeArray.getJSONObject(i));
            VideoEntertainment anime = aniBuilder
                    .addType()
                    .addReleaseDate()
                    .addPoster()
                    .build();

            documents.add(anime);
        }
        return new Pagination<>(page, count, documents, lastPage);
    }
    //parse anime trends
    public List<VideoEntertainment> parseAnimeTrends() throws TooManyAnimeRequestsException {
        JSONArray animeArray;
        try {
            animeArray = parser.getAnimeTrends().getJSONArray("media");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<VideoEntertainment> trends = new ArrayList<>();
        for(int i = 0; i < animeArray.length(); i++){
            JSONAniBuilder aniBuilder = JSONAniBuilder.getInstance(animeArray.getJSONObject(i));
            VideoEntertainment anime = aniBuilder
                    .addType()
                    .addReleaseDate()
                    .addPoster()
                    .build();

            trends.add(anime);
        }
        return trends;
    }
    //parse anime(as VideoEntertainment)
    public VideoEntertainment parseAnimeById(int id) throws ContentNotFoundException, TooManyAnimeRequestsException {
        JSONObject jsonAnime = parser.getAnimeById(id);

        return parseAnime(jsonAnime);
    }
    public VideoEntertainment parseAnimeByName(String name) throws ContentNotFoundException, TooManyAnimeRequestsException {
        JSONObject jsonAnime = parser.getAnimeByName(name);

        return parseAnime(jsonAnime);
    }
    private VideoEntertainment parseAnime(JSONObject jsonAnime){
        JSONAniBuilder aniBuilder = JSONAniBuilder.getInstance(jsonAnime);
        return aniBuilder
                .addType()
                .addDescription()
                .addRuntime()
                .addReleaseDate()
                .addScore()
                .addGenres()
                .addStatus()
                .addPoster().build();
    }
    //parse anime(as AniAnime)
    public AniAnime parseById(int id) throws ContentNotFoundException, TooManyAnimeRequestsException {
        JSONObject root = parser.getAnimeById(id);

        JSONAniBuilder aniBuilder = JSONAniBuilder.getInstance(root);
        return aniBuilder
                .addType()
                .addDescription()
                .addRuntime()
                .addReleaseDate()
                .addScore()
                .addGenres()
                .addStatus()
                .addPoster()
                .addIdMAL()
                .addLastAired()
                .addEpisodes()
                .addStudios()
                .addOtherNames()
                .addRecommendations().build();
    }

    public List<UserAnime> parseMediaList(int userId, MediaListStatus status) throws TooManyAnimeRequestsException {
        int page = 1;
        List<UserAnime> animeList = new ArrayList<>();
        JSONObject root;
        do {
            log.info("Page: "+page+" Status: "+status);
            root = parser.getUserMediaList(userId, page, status);
            log.info(root.toString());

            VideoConsumingStatus consumingStatus = null;
            switch (status){
                case PLANNING: consumingStatus = VideoConsumingStatus.PLANNED; break;
                case CURRENT: consumingStatus = VideoConsumingStatus.WATCHING; break;
                case COMPLETED: consumingStatus = VideoConsumingStatus.COMPLETE; break;
            }
            if(consumingStatus == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            JSONArray mediaList = root.getJSONArray("mediaList");
            for(int i = 0; i < mediaList.length(); i++){
                if(page>50){
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                JSONUserAniBuilder aniBuilder = JSONUserAniBuilder.getInstance(mediaList.getJSONObject(i), consumingStatus);
                aniBuilder.addAnime()
                        .addProgress()
                        .addScore();

                animeList.add(aniBuilder.build());
            }
            page++;
        }while (root.getJSONObject("pageInfo").getBoolean("hasNextPage"));
        return animeList;
    }

}
