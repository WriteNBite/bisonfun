package com.bisonfun.domain;

import com.bisonfun.domain.enums.VideoContentStatus;
import com.bisonfun.domain.enums.VideoContentType;

import java.sql.Date;
import java.time.LocalDate;

public class VideoEntertainment {
    protected int id;
    protected boolean isAnime;
    protected VideoContentType type;
    protected String title;
    protected String description;
    protected int runtime;
    protected Date releaseDate;
    protected String poster;
    protected float score;
    protected String[] genres;
    protected VideoContentStatus status;

    public VideoEntertainment(int id, boolean isAnime, VideoContentType type, String title, String description, int runtime, Date releaseDate, String poster, float score, String[] genres, VideoContentStatus status) {
        this.id = id;
        this.isAnime = isAnime;
        this.type = type;
        this.title = title;
        this.description = description;
        this.runtime = runtime;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.score = score;
        this.genres = genres;
        this.status = status;
    }

    public VideoEntertainment(int id, boolean isAnime, VideoContentType type, String title, String description, Date releaseDate, String poster) {
        this.id = id;
        this.isAnime = isAnime;
        this.type = type;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.poster = poster;
    }

    public VideoEntertainment(){

    }

    public String getTimeToWatch(){
        return (runtime >= 60 ? (runtime / 60 + " hr. ") : "") +
                (runtime % 60 > 0 ? (runtime % 60 + " min.") : "") +
                (runtime < 0 ? "Unknown" : "");
    }
    public int getReleaseYear(){
        if(releaseDate == null){
            return -1;
        }
        LocalDate localDate = releaseDate.toLocalDate();

        return localDate.getYear();
    }

    public boolean isAnime() {
        return isAnime;
    }

    public void setAnime(boolean anime) {
        isAnime = anime;
    }

    public VideoContentType getType() {
        return type;
    }

    public void setType(VideoContentType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public VideoContentStatus getStatus() {
        return status;
    }

    public void setStatus(VideoContentStatus status) {
        this.status = status;
    }
}
