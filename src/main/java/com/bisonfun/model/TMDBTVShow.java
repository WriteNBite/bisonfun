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
public class TMDBTVShow extends TVShow{

    protected int seasons;
    protected String[] studios;
    protected String[] networks;

    public TMDBTVShow(int id, boolean isAnime, String title, String description, int runtime, Date releaseDate, String poster, float score, String[] genres, VideoContentStatus status, Date lastAired, int episodes, int seasons, String[] networks, String[] studios) {
        super(id, isAnime, VideoContentType.TV, title, description, runtime, releaseDate, poster, score, genres, status, lastAired, episodes);
        this.seasons = seasons;
        this.networks = networks;
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
        return "TMDBTVShow{" +
                "seasons=" + seasons +
                ", studios=" + Arrays.toString(studios) +
                ", networks=" + Arrays.toString(networks) +
                ", lastAired=" + lastAired +
                ", episodes=" + episodes +
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
