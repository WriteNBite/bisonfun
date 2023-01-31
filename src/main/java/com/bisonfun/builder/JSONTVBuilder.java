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

    /**
     * Get instance of builder.
     * @param root JSONObject to work with. Contains info about tv. Right now needed JSONObject from TheMovieDB API.
     * @param jsonParser parser to get additional JSONObject from TheMovieDB API.
     * @return builder with id and title included.
     */
    public static JSONTVBuilder getInstance(JSONObject root, JSONParser jsonParser){
        log.info("Returning Instance of JSONAniBuilder");
        return new JSONTVBuilder(root, jsonParser);
    }

    private JSONTVBuilder(JSONObject root, JSONParser parser){
        log.info("Instance of JSONTVBuilder created");
        log.info("Root: "+root.toString());
        this.root = root;
        this.parser = parser;
        addId();
        addTitle();
    }


    /**
     * Add to builder last aired date. Gotten from JSONObject root.
     * @return builder
     */
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
    /**
     * Add to builder number of episodes. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addEpisodes() {
        log.info("Getting episodes");
        episodes = root.getInt("number_of_episodes");
        log.info("Episodes: "+episodes);
        return this;
    }
    /**
     * Add to builder tv id. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addId() {
        log.info("Getting id");
        id = root.getInt("id");
        log.info("Id: "+id);
        return this;
    }
    /**
     * Add to builder info if tv have anime keyword. Tv keywords gotten from JSONParser.
     * @return builder.
     */
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
    /**
     * Add to builder tv type (always Tv).
     * @return builder
     */
    @Override
    public JSONTVBuilder addType() {
        return this;
    }
    /**
     * Add to builder tv title. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addTitle() {
        log.info("Getting title");
        title = root.getString("name");
        log.info("Title: "+title);
        return this;
    }
    /**
     * Add to builder tv description. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addDescription() {
        log.info("Getting description");
        description = root.getString("overview");
        log.info("Description: "+description);
        return this;
    }
    /**
     * Add to builder tv runtime. Gotten from JSONObject root.
     * @return builder
     */
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
    /**
     * Add to builder tv release date. Gotten from JSONObject root.
     * @return builder
     */
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
    /**
     * Add to builder tv poster path. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addPoster() {
        return addPoster(500);
    }
    /**
     * Add to builder movie poster path. Gotten from JSONObject root.
     * @param maxSize maximum width for poster.
     * @return builder.
     */
    public JSONTVBuilder addPoster(int maxSize){
        log.info("Getting poster");
        if (root.isNull("poster_path")) {
            poster = TMDB.NO_IMAGE.link;
        } else {
            String photoLink = maxSize < 500 ? TMDB.IMAGE_200.link : TMDB.IMAGE_500.link;
            poster = photoLink + root.getString("poster_path");
        }
        log.info("Poster path: "+poster);
        return this;
    }
    /**
     * Add to builder tv score (format "n.f"). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addScore() {
        log.info("Getting score");
        score = Math.round(root.getFloat("vote_average")*10f)/10f;
        log.info("Score: "+score);
        return this;
    }
    /**
     * Add to builder tv genres. Gotten from JSONObject root.
     * @return builder
     */
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
    /**
     * Add to builder tv status (Released/Ongoing/Upcoming/Rumored/Planned/Canceled). Gotten from JSONObject root.
     * @return builder
     */
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
    /**
     * Add to builder number of seasons. Gotten from JSONObject root.
     * @return builder
     */
    public JSONTVBuilder addSeasons(){
        log.info("Getting seasons");
        seasons = root.getInt("number_of_seasons");
        log.info("Seasons: "+seasons);
        return this;
    }
    /**
     * Add to builder studios that were involved in tv development. Gotten from JSONObject root.
     * @return builder
     */
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
    /**
     * Add to builder networks that stream tv. Gotten from JSONObject root.
     * @return builder
     */
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
    /**
     * Create tv object.
     * @return {@link com.bisonfun.domain.TMDBTVShow}
     */
    @Override
    public TMDBTVShow build() {
        log.info("Building TMDB TV class");
        TMDBTVShow tmdbtvShow = new TMDBTVShow(id, isAnime, title, description, runtime, releaseDate, poster, score, genres, status, lastAired, episodes, seasons, networks, studios);
        log.info("TV: "+tmdbtvShow);
        return tmdbtvShow;
    }
}
