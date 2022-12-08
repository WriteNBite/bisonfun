package com.bisonfun.builder;

import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.domain.enums.VideoContentType;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.UserAnime;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.Date;

@Slf4j
public class JSONUserAniBuilder {
    private final JSONObject root;
    private final VideoConsumingStatus status;

    private Anime anime;

    private int progress;
    private int score;

    public static JSONUserAniBuilder getInstance(JSONObject root, VideoConsumingStatus status){
        log.info("Return instance of JSONUserAniBuilder");
        return new JSONUserAniBuilder(root, status);
    }

    private JSONUserAniBuilder(JSONObject root, VideoConsumingStatus status) {
        log.info("Instance of JSONUserAniBuilder created");
        log.info("Root: "+root.toString());
        this.root = root;
        this.status = status;
    }

    public JSONUserAniBuilder addProgress(){
        log.info("Getting progress");
        progress = root.getInt("progress");
        log.info("Score: "+score);
        return this;
    }

    public JSONUserAniBuilder addScore(){
        log.info("Getting score");
        score = (int) root.getFloat("score");
        log.info("Score: "+score);
        return this;
    }

    public JSONUserAniBuilder addAnime(){
        JSONObject media = root.getJSONObject("media");
        JSONObject jsonTitle = media.getJSONObject("title");
        AnimeBuilder builder = AnimeBuilder.getInstance(media.getInt("id"));

        //MAL id, poster, title
        builder.addMalId(media.isNull("idMal") ? -1 : media.getInt("idMal"))
                .addPoster(media.getJSONObject("coverImage").getString("large"))
                .addTitle(jsonTitle.isNull("english") ? jsonTitle.getString("romaji") : jsonTitle.getString("english"));

        //Types
        VideoContentType type;
        if(media.isNull("format")){
            type = VideoContentType.UNKNOWN;
        }else {
            type = media.getString("format").equalsIgnoreCase("movie") ? VideoContentType.MOVIE : VideoContentType.TV;
        }
        builder.addType(type);

        //year
        JSONObject jsonReleaseDate = media.getJSONObject("startDate");
        Date releaseDate = parseDate(jsonReleaseDate);
        builder.addYear(releaseDate != null ? releaseDate.toLocalDate().getYear() : -1);

        //anime
        anime = builder.build();
        return this;
    }

    public UserAnime build(){
        UserAnime userAnime = new UserAnime();
        userAnime.setEpisodes(progress);
        userAnime.setAnime(anime);
        userAnime.setScore(score);
        userAnime.setStatus(status);
        return userAnime;
    }

    private static Date parseDate(JSONObject releaseDate){
        if(releaseDate.isNull("year") || releaseDate.isNull("month") || releaseDate.isNull("day")){
            return null;
        }
        String date = releaseDate.getInt("year") +
                "-" +
                releaseDate.getInt("month") +
                "-" +
                releaseDate.getInt("day");
        return Date.valueOf(date);
    }

}
