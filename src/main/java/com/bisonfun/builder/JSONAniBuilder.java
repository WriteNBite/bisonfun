package com.bisonfun.builder;

import com.bisonfun.domain.AniAnime;
import com.bisonfun.domain.enums.VideoContentStatus;
import com.bisonfun.domain.enums.VideoContentType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.Arrays;

@Slf4j
public class JSONAniBuilder implements TVContentBuilder {
    private final JSONObject root;

    //values
    private int id;
    private VideoContentType type;
    private String title;
    private String description;
    private int runtime;
    private Date releaseDate;
    private String poster;
    private float score;
    private String[] genres;
    private VideoContentStatus status;
    private Date lastAired;
    private int episodes;
    private int idMAL = -1;
    private String[] studios;
    private String[] otherNames;

    /**
     * Get instance of builder.
     * @param root JSONObject to work with. Right now needed "Media" JSONObject from Anilist API.
     * @return builder
     */
    public static JSONAniBuilder getInstance(JSONObject root){
        log.info("Returning Instance of JSONAniBuilder");
        return new JSONAniBuilder(root);
    }

    private JSONAniBuilder(JSONObject root){
        log.info("Instance of JSONAniBuilder created");
        log.info("Root: "+root.toString());
        this.root = root;
    }

    /**
     * Add to builder anime id. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addId(){
        log.info("Getting id");
        id = root.getInt("id");
        log.info("Id: "+id);
        return this;
    }

    /**
     * Add to builder if it's anime (always true).
     * @return builder
     */
    @Override
    public VideoContentBuilder addIsAnime() {
        return this;
    }

    /**
     * Add to builder anime type (Movie/TV/Unknown). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addType() {
        log.info("Getting type");
        if(root.isNull("format")){
            type = VideoContentType.UNKNOWN;
        }else {
            type = root.getString("format").equalsIgnoreCase("movie") ? VideoContentType.MOVIE : VideoContentType.TV;
        }
        log.info("Type: "+type);
        return this;
    }

    /**
     * Add to builder anime title. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addTitle() {
        log.info("Getting title");
        if(!root.isNull("title")) {
            JSONObject jsonTitle = root.getJSONObject("title");
            title = jsonTitle.isNull("english") ? jsonTitle.getString("romaji") : jsonTitle.getString("english");
            log.info("Title: "+title);
        }else{
            log.error("Title wasn't found\n JSONObject: "+root);
            title = null;
        }
        return this;
    }

    /**
     * Add to builder anime description. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addDescription() {
        log.info("Getting description");
        if(!root.isNull("description")){
            this.description = root.getString("description");
            log.info("Desc.: "+description);
        }else{
            log.warn("There's no description");
        }
        return this;
    }

    /**
     * Add to builder anime runtime(for episode or movie). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addRuntime() {
        log.info("Getting runtime");
        this.runtime = root.isNull("duration") ? -1 : root.getInt("duration");
        log.info("Runtime: "+runtime);
        return this;
    }

    /**
     * Add to builder anime release date. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addReleaseDate() {
        log.info("Getting release date");
        JSONObject jsonReleaseDate = root.getJSONObject("startDate");
        releaseDate = parseDate(jsonReleaseDate);
        log.info("Release date: "+releaseDate);
        return this;
    }

    /**
     * Add to builder anime poster path. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addPoster() {
        log.info("Getting poster");
        JSONObject coverImages = root.getJSONObject("coverImage");
        if(coverImages.has("extraLarge")) {
            this.poster = coverImages.getString("extraLarge");
        }else if(coverImages.has("large")){
            this.poster = coverImages.getString("large");
        }else {
            log.warn("There's no proper image\n"+coverImages);
        }
        log.info("Poster: "+poster);
        return this;
    }

    /**
     * Add to builder anime rating score(format "n.f"). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addScore() {
        log.info("Getting score");
        this.score = root.isNull("averageScore") ? 0 : root.getInt("averageScore")/10f;
        log.info("Score: "+score);
        return this;
    }

    /**
     * Add to builder anime genres. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addGenres() {
        log.info("Getting genres");
        JSONArray JSONGenres = root.getJSONArray("genres");
        String[] genres = new String[JSONGenres.length()];
        for(int i = 0; i < JSONGenres.length(); i++){
            genres[i] = JSONGenres.getString(i);
        }
        this.genres = genres;
        log.info("Genres: "+ Arrays.toString(genres));
        return this;
    }

    /**
     * Add to builder anime status (Finished/Upcoming/Ongoing/Canceled/Paused). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addStatus() {
        log.info("Getting status");
        if(!root.isNull("status")){
            String strStatus = root.getString("status");
            switch (strStatus) {
                case "FINISHED":
                    status = VideoContentStatus.RELEASED;
                    break;
                case "NOT_YET_RELEASED":
                    status = VideoContentStatus.UPCOMING;
                    break;
                case "RELEASING":
                    status = VideoContentStatus.ONGOING;
                    break;
                case "CANCELLED":
                    status = VideoContentStatus.CANCELED;
                    break;
                case "HIATUS":
                    status = VideoContentStatus.PAUSED;
                    break;
            }
            log.info("Status: "+status);
        }else{
            log.warn("Status isn't find");
        }
        return this;
    }

    /**
     * Add to builder anime last aired date. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addLastAired() {
        log.info("Getting last aired date");
        JSONObject jsonLastDate = root.getJSONObject("endDate");
        lastAired = parseDate(jsonLastDate);
        log.info("Last date: "+lastAired);
        return this;
    }

    /**
     * Add to builder number of anime episodes. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addEpisodes() {
        log.info("Getting episodes");
        if(root.isNull("episodes")){
            log.warn("Episode count wasn't found");
            episodes = -1;
        }else{
            episodes =  root.getInt("episodes");
            log.info("Episodes: "+episodes);
        }
        return this;
    }

    /**
     * Add to builder anime id from MyAnimeList. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addIdMAL() {
        if(!root.isNull("idMal")) {
            this.idMAL = root.getInt("idMal");
            log.info("MAL id: "+idMAL);
        }else{
            log.warn("No MAL id found");
        }
        return this;
    }

    /**
     * Add to builder array of studios that was involved in anime development. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addStudios() {
        log.info("Getting studios");
        JSONArray JSONStudios = root.getJSONObject("studios").getJSONArray("nodes");
        String[] studios = new String[JSONStudios.length()];
        for(int i = 0; i < JSONStudios.length(); i++){
            studios[i] = JSONStudios.getJSONObject(i).getString("name");
        }
        this.studios = studios;
        log.info("Studios: "+Arrays.toString(studios));
        return this;
    }

    /**
     * Add to builder other(official, not official, translated) names of anime. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addOtherNames() {
        log.info("Getting other names");
        JSONArray JSONNames = root.getJSONArray("synonyms");
        String[] names = new String[JSONNames.length()];
        for(int i = 0; i < JSONNames.length(); i++){
            names[i] = JSONNames.getString(i);
        }
        otherNames = names;
        log.info("Other names: "+Arrays.toString(otherNames));
        return this;
    }

    /**
     * Create anime object.
     * @return {@link com.bisonfun.domain.AniAnime}
     */
    @Override
    public AniAnime build(){
        log.info("Building an AniAnime class");
        AniAnime anime = new AniAnime(id, true, type, title, description, runtime, releaseDate, poster, score, genres, status, lastAired, episodes, idMAL, studios, otherNames);
        log.info("Anime: "+ anime);
        return anime;
    }

    private static Date parseDate(JSONObject jsonDate){
        if(jsonDate.isNull("year") || jsonDate.isNull("month") || jsonDate.isNull("day")){
            log.warn("Date is null");
            return null;
        }
        String date = jsonDate.getInt("year") +
                "-" +
                jsonDate.getInt("month") +
                "-" +
                jsonDate.getInt("day");
        return Date.valueOf(date);
    }
}
