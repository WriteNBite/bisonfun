package com.bisonfun.domain;

import com.bisonfun.domain.enums.VideoContentStatus;
import com.bisonfun.domain.enums.VideoContentType;

import java.sql.Date;
import java.util.Arrays;

public class TMDBMovie extends VideoEntertainment{

    protected String imdbId;
    protected String[] studios;

    public TMDBMovie(int id, String imdbId, boolean isAnime, String title, String description, int runtime, Date releaseDate, String poster, float score, String[] genres, VideoContentStatus status, String[] studios) {
        super(id, isAnime, VideoContentType.MOVIE, title, description, runtime, releaseDate, poster, score, genres, status);
        this.imdbId = imdbId;
        this.studios = studios;
    }

    public String getPosterBySize(int width){
        return this.poster.replaceAll("p/w[\\d]+/", "p/w"+width+"/");
    }

    public String[] getStudios() {
        return studios;
    }

    public void setStudios(String[] studios) {
        this.studios = studios;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
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
