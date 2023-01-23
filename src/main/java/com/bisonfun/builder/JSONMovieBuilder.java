package com.bisonfun.builder;

import com.bisonfun.domain.TMDBMovie;
import com.bisonfun.domain.enums.VideoContentStatus;
import com.bisonfun.utilities.JSONParser;
import com.bisonfun.utilities.TMDB;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.Arrays;

@Slf4j
public class JSONMovieBuilder implements  VideoContentBuilder{
    private final JSONObject root;
    private final JSONParser jsonParser;

    private int id = -1;
    private boolean isAnime;
    private String title;
    private String description;
    private int runtime = -1;
    private Date releaseDate;
    private String poster;
    private float score;
    private String[] genres;
    private VideoContentStatus status;
    private String imdbId;
    private String[] studios;

    /**
     * Get instance of builder.
     * @param root JSONObject to work with. Contains info about movie. Right now needed JSONObject from TheMovieDB API.
     * @param jsonParser parser to get additional JSONObject from TheMovieDB API.
     * @return builder.
     */
    public static JSONMovieBuilder getInstance(JSONObject root, JSONParser jsonParser){
        log.info("Returning Instance of JSONAniBuilder");
        return new JSONMovieBuilder(root, jsonParser);
    }

    private JSONMovieBuilder(JSONObject root, JSONParser parser){
        log.info("Instance of JSONMovieBuilder created");
        log.info("Root: "+root.toString());
        this.root = root;
        this.jsonParser = parser;
    }

    /**
     * Add to builder movie id. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addId() {
        log.info("Getting id");
        id = root.getInt("id");
        log.info("Id: "+id);
        return this;
    }

    /**
     * Add to builder info if movie have anime keyword. Movie keywords gotten from JSONParser.
     * @return builder.
     */
    @Override
    public JSONMovieBuilder addIsAnime() {
        log.info("Check if anime");
        if(id<=0){ //if id hasn't been set
            addId();
        }
        JSONArray keywords = jsonParser.getMovieKeywords(id);
        if (keywords != null) { // if keywords are existed
            if (keywords.isEmpty()) {
                isAnime = false;
                log.info("IsAnime: "+ false);
                return this;
            }
            for (int i = 0; i < keywords.length(); i++) {
                JSONObject keyword = keywords.getJSONObject(i);
                if (keyword.getInt("id") == 210024) {// 210024 - anime keyword
                    isAnime = true;
                    log.info("IsAnime: "+ true);
                    return this;
                }
            }
        }
        isAnime = false;
        log.info("IsAnime: "+ false);
        return this;
    }
    /**
     * Add to builder movie type (always Movie).
     * @return builder
     */
    @Override
    public JSONMovieBuilder addType() {
        return this;
    }
    /**
     * Add to builder movie title. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addTitle() {
        log.info("Getting title");
        title = root.getString("title");
        log.info("Title: "+title);
        return this;
    }
    /**
     * Add to builder movie description. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addDescription() {
        log.info("Getting description");
        if(root.isNull("overview")){
           log.warn("Overview is null");
           description = "";
        }else{
           description = root.getString("overview");
            log.info("Description: "+description);
        }
        return this;
    }
    /**
     * Add to builder movie runtime. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addRuntime() {
        log.info("Getting runtime");
        if(root.isNull("runtime")){
            log.warn("Runtime is null");
            runtime = -1;
        }else{
            runtime = root.getInt("runtime");
            log.info("Runtime: "+runtime);
        }
        return this;
    }
    /**
     * Add to builder movie release date. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addReleaseDate() {
        log.info("Getting release date");
        if (root.has("release_date") && !root.isNull("release_date")) {// checks if JSON has "release_date" and it's not null
            String strRelease = root.getString("release_date");
            releaseDate = strRelease.equals("") ? null : Date.valueOf(strRelease);
        } else {
            releaseDate = null;
        }
        log.info("Release date: "+releaseDate);
        return this;
    }
    /**
     * Add to builder movie poster path. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addPoster() {
        log.info("Getting poster");
        if (root.isNull("poster_path")) {
            poster = TMDB.NO_IMAGE.link;
        } else {
            poster = TMDB.IMAGE.link + root.getString("poster_path");
        }
        log.info("Poster path: "+poster);
        return this;
    }
    /**
     * Add to builder movie score (format "n.f"). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addScore() {
        log.info("Getting score");
        score = Math.round(root.getFloat("vote_average")*10f)/10f;
        log.info("Score: "+score);
        return this;
    }
    /**
     * Add to builder movie genres. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addGenres() {
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
     * Add to builder movie status (Released/Upcoming/Rumored/Planned/Canceled). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addStatus() {
        log.info("Getting status");
        String strStatus = root.getString("status");
        switch (strStatus) {
            case "Released":
                status = VideoContentStatus.RELEASED;
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
     * Create movie object.
     * @return {@link com.bisonfun.domain.TMDBMovie}
     */
    @Override
    public TMDBMovie build() {
        log.info("Building TMDB Movie class");
        TMDBMovie movie = new TMDBMovie(id,imdbId, isAnime, title, description, runtime, releaseDate, poster, score, genres, status, studios);
        log.info("Movie: "+movie);
        return movie;
    }
    /**
     * Add to builder id from IMDB. Gotten from JSONObject root.
     * @return builder
     */
    public JSONMovieBuilder addIMDBId(){
        log.info("Getting IMDB id");
        if(root.isNull("imdb_id")){
            imdbId = null;
            log.warn("There's no imdb id");
        }else{
            imdbId = root.getString("imdb_id");
            log.info("IMDB id: "+imdbId);
        }
        return this;
    }
    /**
     * Add to builder studios that were involved in movie development. Gotten from JSONObject root.
     * @return builder
     */
    public JSONMovieBuilder addStudios(){
        log.info("Getting studios");
        JSONArray JSONStudios = root.getJSONArray("production_companies");
        studios = new String[JSONStudios.length()];
        for(int i = 0; i < JSONStudios.length(); i++){
            studios[i] = JSONStudios.getJSONObject(i).getString("name");
        }
        log.info("Studios: "+ Arrays.toString(studios));
        return this;
    }
}
