package com.bisonfun.model;

import com.bisonfun.client.tmdb.TMDB;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.Arrays;

@Getter
@Setter
public class TMDBMovie extends VideoEntertainment{

    protected String imdbId;
    protected String[] studios;

    public TMDBMovie(int id, String imdbId, boolean isAnime, String title, String description, int runtime, Date releaseDate, String poster, float score, String[] genres, VideoContentStatus status, String[] studios) {
        super(id, isAnime, VideoContentType.MOVIE, title, description, runtime, releaseDate, poster, score, genres, status);
        this.imdbId = imdbId;
        this.studios = studios;
    }

    @Override
    public String getPoster() {
        return getPoster(500);
    }
    public String getPoster(int maxSize) {
        if(poster == null){
            return TMDB.NO_IMAGE.link;
        }
        String link = maxSize < 500 ? TMDB.IMAGE_200.link : TMDB.IMAGE_500.link;
        return link + poster;
    }

    @Override
    public String toString() {
        return "TMDBMovie{" +
                "imdbId='" + imdbId + '\'' +
                ", studios=" + Arrays.toString(studios) +
                ", id=" + id +
                ", isAnime=" + isAnime +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", runtime=" + runtime +
                ", releaseDate=" + releaseDate +
                ", poster='" + poster + '\'' +
                ", score=" + score +
                ", genres=" + Arrays.toString(genres) +
                ", status=" + status +
                '}';
    }
}
