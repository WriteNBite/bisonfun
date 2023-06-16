package com.bisonfun.builder;

import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.client.tmdb.TmdbApiResponse;
import com.bisonfun.client.tmdb.TMDB;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;

@Slf4j
public class JSONTVBuilder implements TVContentBuilder{
    private final JSONObject root;
    private final TmdbApiResponse parser;

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
     * @param tmdbApiResponse parser to get additional JSONObject from TheMovieDB API.
     * @return builder with id and title included.
     */
    public static JSONTVBuilder getInstance(JSONObject root, TmdbApiResponse tmdbApiResponse){
        return new JSONTVBuilder(root, tmdbApiResponse);
    }

    private JSONTVBuilder(JSONObject root, TmdbApiResponse parser){
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
        if (root.has("last_air_date") && !root.isNull("last_air_date")) {
            String strLast = root.getString("last_air_date");
            lastAired = strLast.equals("") ? null : Date.valueOf(strLast);
        } else {
            lastAired = null;
        }
        return this;
    }
    /**
     * Add to builder number of episodes. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addEpisodes() {
        episodes = root.getInt("number_of_episodes");
        return this;
    }
    /**
     * Add to builder tv id. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addId() {
        id = root.getInt("id");
        return this;
    }
    /**
     * Add to builder info if tv have anime keyword. Tv keywords gotten from JSONParser.
     * @return builder.
     */
    @Override
    public JSONTVBuilder addIsAnime() {
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
                        log.info("TV: "+id+" is Anime");
                    }
                }
            }
        }else {
            isAnime = false;
        }
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
        title = root.getString("name");
        return this;
    }
    /**
     * Add to builder tv description. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addDescription() {
        description = root.getString("overview");
        return this;
    }
    /**
     * Add to builder tv runtime. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addRuntime() {
        JSONArray runtimes = root.getJSONArray("episode_run_time");
        if(runtimes.isEmpty()){
            runtime = -1;
            log.warn("Runtime is null; TV: "+id);
        }else {
            runtime = runtimes.getInt(runtimes.length() - 1);
        }
        return this;
    }
    /**
     * Add to builder tv release date. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addReleaseDate() {
        if (root.has("first_air_date") && !root.isNull("first_air_date")) {
            String strRelease = root.getString("first_air_date");
            releaseDate = strRelease.equals("") ? null : Date.valueOf(strRelease);
        } else {
            releaseDate = null;
        }
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
        if (root.isNull("poster_path")) {
            poster = TMDB.NO_IMAGE.link;
        } else {
            String photoLink = maxSize < 500 ? TMDB.IMAGE_200.link : TMDB.IMAGE_500.link;
            poster = photoLink + root.getString("poster_path");
        }
        return this;
    }
    /**
     * Add to builder tv score (format "n.f"). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addScore() {
        score = Math.round(root.getFloat("vote_average")*10f)/10f;
        return this;
    }
    /**
     * Add to builder tv genres. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addGenres() {
        JSONArray JSONGenres = root.getJSONArray("genres");
        genres = new String[JSONGenres.length()];
        for(int i = 0; i < JSONGenres.length(); i++){
            genres[i] = JSONGenres.getJSONObject(i).getString("name");
        }
        return this;
    }
    /**
     * Add to builder tv status (Released/Ongoing/Upcoming/Rumored/Planned/Canceled). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONTVBuilder addStatus() {
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
        return this;
    }
    /**
     * Add to builder number of seasons. Gotten from JSONObject root.
     * @return builder
     */
    public JSONTVBuilder addSeasons(){
        seasons = root.getInt("number_of_seasons");
        return this;
    }
    /**
     * Add to builder studios that were involved in tv development. Gotten from JSONObject root.
     * @return builder
     */
    public JSONTVBuilder addStudios(){
        JSONArray JSONStudios = root.getJSONArray("production_companies");
        studios = new String[JSONStudios.length()];
        for(int i = 0; i < JSONStudios.length(); i++){
            studios[i] = JSONStudios.getJSONObject(i).getString("name");
        }
        return this;
    }
    /**
     * Add to builder networks that stream tv. Gotten from JSONObject root.
     * @return builder
     */
    public JSONTVBuilder addNetworks(){
        JSONArray JSONNetworks = root.getJSONArray("networks");
        networks = new String[JSONNetworks.length()];
        for(int i = 0; i < JSONNetworks.length(); i++){
            networks[i] = JSONNetworks.getJSONObject(i).getString("name");
        }
        return this;
    }
    /**
     * Create tv object.
     * @return {@link com.bisonfun.model.TMDBTVShow}
     */
    @Override
    public TMDBTVShow build() {
        TMDBTVShow tmdbtvShow = new TMDBTVShow(id, isAnime, title, description, runtime, releaseDate, poster, score, genres, status, lastAired, episodes, seasons, networks, studios);
        return tmdbtvShow;
    }
}
