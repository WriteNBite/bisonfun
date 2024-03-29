package com.bisonfun.model;

import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;

import java.sql.Date;
import java.util.Arrays;

public class AniAnime extends TVShow{
    protected int idMAL;
    protected String[] studios;
    protected String[] otherNames;
    protected VideoEntertainment[] recommendations;

    public AniAnime() {
    }

    public AniAnime(int id, boolean isAnime, VideoContentType type, String title, String description, int runtime, Date releaseDate, String poster, float score, String[] genres, VideoContentStatus status, Date lastAired, int episodes, int idMAL, String[] studios, String[] otherNames, VideoEntertainment[] recommendations) {
        super(id, isAnime, type, title, description, runtime, releaseDate, poster, score, genres, status, lastAired, episodes);
        this.idMAL = idMAL;
        this.studios = studios;
        this.otherNames = otherNames;
        this.recommendations = recommendations;
    }

    public AniAnime(VideoEntertainment videoEntertainment, int idMAL, Date lastAired, int episodes, String[] studios, String[] otherNames, VideoEntertainment[] recommendations){
        super(videoEntertainment.getId(), videoEntertainment.isAnime(), videoEntertainment.getType(), videoEntertainment.getTitle(), videoEntertainment.getDescription(), videoEntertainment.getRuntime(), videoEntertainment.getReleaseDate(), videoEntertainment.getPoster(), videoEntertainment.getScore(), videoEntertainment.getGenres(), videoEntertainment.getStatus(), lastAired, episodes);
        this.idMAL = idMAL;
        this.studios = studios;
        this.otherNames = otherNames;
        this.recommendations = recommendations;
    }

    public String[] getStudios() {
        return studios;
    }

    public void setStudios(String[] studios) {
        this.studios = studios;
    }

    public String[] getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String[] otherNames) {
        this.otherNames = otherNames;
    }

    public int getIdMAL() {
        return idMAL;
    }

    public void setIdMAL(int idMAL) {
        this.idMAL = idMAL;
    }

    public VideoEntertainment[] getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(VideoEntertainment[] recommendations) {
        this.recommendations = recommendations;
    }

    @Override
    public String toString() {
        return "AniAnime{" +
                "idMAL=" + idMAL +
                ", studios=" + Arrays.toString(studios) +
                ", otherNames=" + Arrays.toString(otherNames) +
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
