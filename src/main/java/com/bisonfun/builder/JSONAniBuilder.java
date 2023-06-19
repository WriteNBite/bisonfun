package com.bisonfun.builder;

import com.bisonfun.model.AniAnime;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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
    private VideoEntertainment[] recommendations;

    /**
     * Get instance of builder.
     * @param root JSONObject to work with. Right now needed "Media" JSONObject from Anilist API.
     * @return builder with id and title included.
     */
    public static JSONAniBuilder getInstance(JSONObject root){
        return new JSONAniBuilder(root);
    }

    private JSONAniBuilder(JSONObject root){
        this.root = root;
        addId();
        addTitle();
    }

    /**
     * Add to builder anime id. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addId(){
        id = root.getInt("id");
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
        if(root.isNull("format")){
            type = VideoContentType.UNKNOWN;
        }else {
            type = root.getString("format").equalsIgnoreCase("movie") ? VideoContentType.MOVIE : VideoContentType.TV;
        }
        return this;
    }

    /**
     * Add to builder anime title. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addTitle() {
        if(!root.isNull("title")) {
            JSONObject jsonTitle = root.getJSONObject("title");
            title = jsonTitle.isNull("english") ? jsonTitle.getString("romaji") : jsonTitle.getString("english");
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
        if(!root.isNull("description")){
            this.description = root.getString("description");
        }
        return this;
    }

    /**
     * Add to builder anime runtime(for episode or movie). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addRuntime() {
        this.runtime = root.isNull("duration") ? -1 : root.getInt("duration");
        return this;
    }

    /**
     * Add to builder anime release date. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addReleaseDate() {
        JSONObject jsonReleaseDate = root.getJSONObject("startDate");
        releaseDate = parseDate(jsonReleaseDate);
        return this;
    }

    /**
     * Add to builder anime poster path. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addPoster() {
        JSONObject coverImages = root.getJSONObject("coverImage");
        if(coverImages.has("extraLarge")) {
            this.poster = coverImages.getString("extraLarge");
        }else if(coverImages.has("large")){
            this.poster = coverImages.getString("large");
        }else {
            log.warn("There's no proper image\n"+coverImages);
        }
        return this;
    }

    /**
     * Add to builder anime rating score(format "n.f"). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addScore() {
        this.score = root.isNull("averageScore") ? 0 : root.getInt("averageScore")/10f;
        return this;
    }

    /**
     * Add to builder anime genres. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addGenres() {
        JSONArray JSONGenres = root.getJSONArray("genres");
        String[] genres = new String[JSONGenres.length()];
        for(int i = 0; i < JSONGenres.length(); i++){
            genres[i] = JSONGenres.getString(i);
        }
        this.genres = genres;
        return this;
    }

    /**
     * Add to builder anime status (Finished/Upcoming/Ongoing/Canceled/Paused). Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addStatus() {
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
        }else{
            log.warn("Status wasn't find; Anime id: "+id);
        }
        return this;
    }

    /**
     * Add to builder anime last aired date. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addLastAired() {
        JSONObject jsonLastDate = root.getJSONObject("endDate");
        lastAired = parseDate(jsonLastDate);
        return this;
    }

    /**
     * Add to builder number of anime episodes. Gotten from JSONObject root.
     * @return builder
     */
    @Override
    public JSONAniBuilder addEpisodes() {
        if(root.isNull("episodes")){
            log.warn("Episode count wasn't found; Anime id: "+id);
            episodes = -1;
        }else{
            episodes =  root.getInt("episodes");
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
        }else{
            log.warn("No MAL id found; Anime id: "+id);
        }
        return this;
    }

    /**
     * Add to builder array of studios that was involved in anime development. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addStudios() {
        JSONArray JSONStudios = root.getJSONObject("studios").getJSONArray("nodes");
        String[] studios = new String[JSONStudios.length()];
        for(int i = 0; i < JSONStudios.length(); i++){
            studios[i] = JSONStudios.getJSONObject(i).getString("name");
        }
        this.studios = studios;
        return this;
    }

    /**
     * Add to builder other(official, not official, translated) names of anime. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addOtherNames() {
        JSONArray JSONNames = root.getJSONArray("synonyms");
        String[] names = new String[JSONNames.length()];
        for(int i = 0; i < JSONNames.length(); i++){
            names[i] = JSONNames.getString(i);
        }
        otherNames = names;
        return this;
    }

    /**
     * Add array of recommended anime to builder. Gotten from JSONObject root.
     * @return builder
     */
    public JSONAniBuilder addRecommendations(){
        JSONArray JSONRec = root.getJSONObject("recommendations").getJSONArray("nodes");
        List<VideoEntertainment> recs = new ArrayList<>();
        for(int i = 0; i < JSONRec.length(); i++){
            JSONObject recommendation = JSONRec.getJSONObject(i);
            if(recommendation.isNull("mediaRecommendation")){
                continue;
            }
            JSONAniBuilder aniBuilder = JSONAniBuilder.getInstance(recommendation.getJSONObject("mediaRecommendation"));
            VideoEntertainment rec = aniBuilder
                    .addReleaseDate()
                    .addPoster()
                    .addType()
                    .build();
            recs.add(rec);
        }
        recommendations = recs.toArray(new VideoEntertainment[0]);
        return this;
    }

    /**
     * Create anime object.
     * @return {@link com.bisonfun.model.AniAnime}
     */
    @Override
    public AniAnime build(){
        AniAnime anime = new AniAnime(id, true, type, title, description, runtime, releaseDate, poster, score, genres, status, lastAired, episodes, idMAL, studios, otherNames, recommendations);
        return anime;
    }

    private Date parseDate(JSONObject jsonDate){
        if(jsonDate.isNull("year") || jsonDate.isNull("month") || jsonDate.isNull("day")){
            log.warn("Date is null; Anime id: {}", id);
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
