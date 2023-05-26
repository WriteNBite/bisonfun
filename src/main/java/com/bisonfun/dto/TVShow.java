package com.bisonfun.dto;

import com.bisonfun.dto.enums.VideoContentStatus;
import com.bisonfun.dto.enums.VideoContentType;

import java.sql.Date;

public class TVShow extends VideoEntertainment{
    protected Date lastAired;
    protected int episodes;

    public TVShow(int id, boolean isAnime, VideoContentType type, String title, String description, int runtime, Date releaseDate, String poster, float score, String[] genres, VideoContentStatus status, Date lastAired, int episodes) {
        super(id, isAnime, type, title, description, runtime, releaseDate, poster, score, genres, status);
        this.lastAired = lastAired;
        this.episodes = episodes;
    }

    public TVShow(){

    }

    public Date getLastAired() {
        return lastAired;
    }

    public void setLastAired(Date lastAired) {
        this.lastAired = lastAired;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }
}
