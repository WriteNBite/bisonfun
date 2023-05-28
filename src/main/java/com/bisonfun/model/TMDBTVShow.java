package com.bisonfun.model;

import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;

import java.sql.Date;
import java.util.Arrays;

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

    public String getPosterBySize(int width){
        return this.poster.replaceAll("p/w[\\d]+/", "p/w"+width+"/");
    }

    public int getSeasons() {
        return seasons;
    }

    public void setSeasons(int seasons) {
        this.seasons = seasons;
    }

    public String[] getNetworks() {
        return networks;
    }

    public void setNetworks(String[] networks) {
        this.networks = networks;
    }

    public String[] getStudios() {
        return studios;
    }

    public void setStudios(String[] studios) {
        this.studios = studios;
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
