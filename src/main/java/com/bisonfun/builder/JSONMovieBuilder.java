package com.bisonfun.builder;

import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.client.tmdb.TmdbApiResponse;
import com.bisonfun.client.tmdb.TMDB;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;

@Slf4j
public class JSONMovieBuilder implements  VideoContentBuilder{
    private final JSONObject root;
    private final TmdbApiResponse tmdbApiResponse;

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
     * @param tmdbApiResponse parser to get additional JSONObject from TheMovieDB API.
     * @return builder with id and title included.
     */
    public static JSONMovieBuilder getInstance(JSONObject root, TmdbApiResponse tmdbApiResponse){
        return new JSONMovieBuilder(root, tmdbApiResponse);
    }

    private JSONMovieBuilder(JSONObject root, TmdbApiResponse parser){
        this.root = root;
        this.tmdbApiResponse = parser;
        addId();
        addTitle();
    }

    /**
     * Add to builder movie id. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addId() {
        id = root.getInt("id");
        return this;
    }

    /**
     * Add to builder info if movie have anime keyword. Movie keywords gotten from JSONParser.
     * @return builder.
     */
    @Override
    public JSONMovieBuilder addIsAnime() {
        if(id<=0){ //if id hasn't been set
            addId();
        }
        JSONArray keywords = tmdbApiResponse.getMovieKeywords(id);
        isAnime = false;
        if (keywords != null) { // if keywords are existed
            if (keywords.isEmpty()) {
                return this;
            }
            for (int i = 0; i < keywords.length(); i++) {
                JSONObject keyword = keywords.getJSONObject(i);
                if (keyword.getInt("id") == 210024) {// 210024 - anime keyword
                    isAnime = true;
                    log.info("Movie: "+id+" is Anime");
                    return this;
                }
            }
        }
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
        title = root.getString("title");
        return this;
    }
    /**
     * Add to builder movie description. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addDescription() {
        if(root.isNull("overview")){
           description = "";
        }else{
           description = root.getString("overview");
        }
        return this;
    }
    /**
     * Add to builder movie runtime. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addRuntime() {
        if(root.isNull("runtime")){
            log.warn("Runtime is null; Movie: "+id);
            runtime = -1;
        }else{
            runtime = root.getInt("runtime");
        }
        return this;
    }
    /**
     * Add to builder movie release date. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addReleaseDate() {
        if (root.has("release_date") && !root.isNull("release_date")) {// checks if JSON has "release_date" and it's not null
            String strRelease = root.getString("release_date");
            releaseDate = strRelease.equals("") ? null : Date.valueOf(strRelease);
        } else {
            releaseDate = null;
        }
        return this;
    }
    /**
     * Add to builder movie poster path. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addPoster() {
        return addPoster(500);
    }
    /**
     * Add to builder movie poster path. Gotten from JSONObject root.
     * @param maxSize maximum width for poster.
     * @return builder.
     */
    public JSONMovieBuilder addPoster(int maxSize){
        if (root.isNull("poster_path")) {
            poster = TMDB.NO_IMAGE.link;
        } else {
            String photoLink = maxSize < 500 ? TMDB.IMAGE_200.link : TMDB.IMAGE_500.link;
            poster = photoLink + root.getString("poster_path");
        }
        return this;
    }
    /**
     * Add to builder movie score (format "n.f"). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addScore() {
        score = Math.round(root.getFloat("vote_average")*10f)/10f;
        return this;
    }
    /**
     * Add to builder movie genres. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addGenres() {
        JSONArray JSONGenres = root.getJSONArray("genres");
        genres = new String[JSONGenres.length()];
        for(int i = 0; i < JSONGenres.length(); i++){
            genres[i] = JSONGenres.getJSONObject(i).getString("name");
        }
        return this;
    }
    /**
     * Add to builder movie status (Released/Upcoming/Rumored/Planned/Canceled). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONMovieBuilder addStatus() {
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
        return this;
    }
    /**
     * Create movie object.
     * @return {@link com.bisonfun.model.TMDBMovie}
     */
    @Override
    public TMDBMovie build() {
        TMDBMovie movie = new TMDBMovie(id,imdbId, isAnime, title, description, runtime, releaseDate, poster, score, genres, status, studios);
        return movie;
    }
    /**
     * Add to builder id from IMDB. Gotten from JSONObject root.
     * @return builder
     */
    public JSONMovieBuilder addIMDBId(){
        if(root.isNull("imdb_id")){
            imdbId = null;
            log.warn("There's no imdb id; Movie: "+id);
        }else{
            imdbId = root.getString("imdb_id");
        }
        return this;
    }
    /**
     * Add to builder studios that were involved in movie development. Gotten from JSONObject root.
     * @return builder
     */
    public JSONMovieBuilder addStudios(){
        JSONArray JSONStudios = root.getJSONArray("production_companies");
        studios = new String[JSONStudios.length()];
        for(int i = 0; i < JSONStudios.length(); i++){
            studios[i] = JSONStudios.getJSONObject(i).getString("name");
        }
        return this;
    }
}
