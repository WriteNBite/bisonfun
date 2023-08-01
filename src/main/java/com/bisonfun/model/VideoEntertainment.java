package com.bisonfun.model;

import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class VideoEntertainment {
    protected int id;
    @SerializedName("anime")
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
}
