package com.bisonfun.builder;

import com.bisonfun.domain.TMDBTVShow;
import com.bisonfun.domain.enums.VideoContentStatus;
import com.bisonfun.utilities.JSONParser;
import com.bisonfun.utilities.TMDB;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.Arrays;

@Slf4j
public class JSONTVBuilder implements TVContentBuilder{
    private final JSONObject root;
    private final JSONParser parser;

    private int id;
    private boolean isAnime;
    private String title;
    private String description;
    private int runtime = -1;
    private Date releaseDate;
    private String poster;
    private float score;
    private String[] genres;
    private VideoContentStatus status;
    private Date lastAired;
    private int episodes = -1;
    private int seasons = -1;
    private String[] studios;
    private String[] networks;

    public static JSONTVBuilder getInstance(JSONObject root, JSONParser jsonParser){
        log.info("Returning Instance of JSONAniBuilder");
        return new JSONTVBuilder(root, jsonParser);
    }

    private JSONTVBuilder(JSONObject root, JSONParser parser){
        log.info("Instance of JSONTVBuilder created");
        log.info("Root: "+root.toString());
        this.root = root;
        this.parser = parser;
    }

    @Override
    public JSONTVBuilder addLastAired() {
        log.info("Getting last aired");
        if (root.has("last_air_date") && !root.isNull("last_air_date")) {
            String strLast = root.getString("last_air_date");
            lastAired = strLast.equals("") ? null : Date.valueOf(strLast);
        } else {
            lastAired = null;
        }
        log.info("Last aired: "+lastAired);
        return this;
    }

    @Override
    public JSONTVBuilder addEpisodes() {
        log.info("Getting episodes");
        episodes = root.getInt("number_of_episodes");
        log.info("Episodes: "+episodes);
        return this;
    }

    @Override
    public JSONTVBuilder addId() {
        log.info("Getting id");
        id = root.getInt("id");
        log.info("Id: "+id);
        return this;
    }

    @Override
    public JSONTVBuilder addIsAnime() {
        log.info("Check if anime");
        if(id<=0){ //if id hasn't been set
            addId();
        }
        JSONArray keywords = parser.getShowKeywords(id);
        if (keywords != null) { // if keywords are existed
            if (keywords.isEmpty()) {
                isAnime = false;
            }else {
                for (int i = 0; i < keywords.length(); i++) {
                    JSONObject keyword = keywords.getJSONObject(i);
                    if (keyword.getInt("id") == 210024) {// 210024 - anime keyword
                        isAnime = true;
                    }
                }
            }
        }else {
            isAnime = false;
        }
        log.info("IsAnime: "+ isAnime);
        return this;
    }

    @Override
    public JSONTVBuilder addType() {
        return this;
    }

    @Override
    public JSONTVBuilder addTitle() {
        log.info("Getting title");
        title = root.getString("name");
        log.info("Title: "+title);
        return this;
    }

    @Override
    public JSONTVBuilder addDescription() {
        log.info("Getting description");
        description = root.getString("overview");
        log.info("Description: "+description);
        return this;
    }

    @Override
    public JSONTVBuilder addRuntime() {
        log.info("Getting runtime");JSONArray runtimes = root.getJSONArray("episode_run_time");
        if(runtimes.isEmpty()){
            runtime = -1;
        }else {
            runtime = runtimes.getInt(runtimes.length() - 1);
        }
        log.info("Runtime: "+runtime);
        return this;
    }

    @Override
    public JSONTVBuilder addReleaseDate() {
        log.info("Getting release date");
        if (root.has("first_air_date") && !root.isNull("first_air_date")) {
            String strRelease = root.getString("first_air_date");
            releaseDate = strRelease.equals("") ? null : Date.valueOf(strRelease);
        } else {
            releaseDate = null;
        }
        log.info("Release Date: "+releaseDate);
        return this;
    }

    @Override
    public JSONTVBuilder addPoster() {
        log.info("Getting poster");
        if (root.isNull("poster_path")) {
            poster = TMDB.NO_IMAGE.link;
        } else {
            poster = TMDB.IMAGE.link + root.getString("poster_path");
        }
        log.info("Poster path: "+poster);
        return this;
    }

    @Override
    public JSONTVBuilder addScore() {
        log.info("Getting score");
        score = Math.round(root.getFloat("vote_average")*100f)/100f;
        log.info("Score: "+score);
        return this;
    }

    @Override
    public JSONTVBuilder addGenres() {
        log.info("Getting genres");
        JSONArray JSONGenres = root.getJSONArray("genres");
        genres = new String[JSONGenres.length()];
        for(int i = 0; i < JSONGenres.length(); i++){
            genres[i] = JSONGenres.getJSONObject(i).getString("name");
        }
        log.info("Genres: "+ Arrays.toString(genres));
        return this;
    }

    @Override
    public JSONTVBuilder addStatus() {
        log.info("Getting status");
        String strStatus = root.getString("status");
        switch (strStatus) {
            case "Ended":
                status = VideoContentStatus.RELEASED;
                break;
            case "Returning Series":
                status = VideoContentStatus.ONGOING;
                break;
            case "In Production":
            case "Post Production":
                status = VideoContentStatus.UPCOMING;
                break;
            case "Rumored":
                status = VideoContentStatus.RUMORED;
                break;
            case "Planned":
                status = VideoContentStatus.PLANNED;
                break;
            case "Canceled":
                status = VideoContentStatus.CANCELED;
                break;
        }
        log.info("Status: "+status);
        return this;
    }

    public JSONTVBuilder addSeasons(){
        log.info("Getting seasons");
        seasons = root.getInt("number_of_seasons");
        log.info("Seasons: "+seasons);
        return this;
    }

    public JSONTVBuilder addStudios(){
        log.info("Getting studios");
        JSONArray JSONStudios = root.getJSONArray("production_companies");
        studios = new String[JSONStudios.length()];
        for(int i = 0; i < JSONStudios.length(); i++){
            studios[i] = JSONStudios.getJSONObject(i).getString("name");
        }
        log.info("Studios: "+ Arrays.toString(studios));
        return this;
    }

    public JSONTVBuilder addNetworks(){
        log.info("Getting networks");
        JSONArray JSONNetworks = root.getJSONArray("networks");
        networks = new String[JSONNetworks.length()];
        for(int i = 0; i < JSONNetworks.length(); i++){
            networks[i] = JSONNetworks.getJSONObject(i).getString("name");
        }
        log.info("Networks: "+ Arrays.toString(networks));
        return this;
    }

    @Override
    public TMDBTVShow build() {
        log.info("Building TMDB Movie class");
        TMDBTVShow tmdbtvShow = new TMDBTVShow(id, isAnime, title, description, runtime, releaseDate, poster, score, genres, status, lastAired, episodes, seasons, networks, studios);
        log.info("Movie: "+tmdbtvShow);
        return tmdbtvShow;
    }
}
