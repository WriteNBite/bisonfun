package com.bisonfun.entity;

import com.bisonfun.dto.AniAnime;
import com.bisonfun.dto.enums.VideoContentType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "anime")
public class Anime implements Serializable {
    @Id
    private int id;

    private int malId;
    private String poster;
    private String title;
    private VideoContentType type;
    private int year;

    @OneToMany(mappedBy = "anime", fetch = FetchType.LAZY)
    private Set<UserAnime> userAnimes = new HashSet<>();

    public Anime(int id, int malId, String poster, String title, VideoContentType type, int year) {
        this.id = id;
        this.malId = malId;
        this.poster = poster;
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public Anime() {
    }

    public boolean update(AniAnime apiAnime){
        boolean posterUpdate = !this.poster.equals(apiAnime.getPoster());
        boolean titleUpdate = !this.title.equals(apiAnime.getTitle());
        boolean yearUpdate = this.year != apiAnime.getReleaseYear();
        if(posterUpdate){ //update content if was something new
            this.setPoster(apiAnime.getPoster());
        }
        if(titleUpdate){
            this.setTitle(apiAnime.getTitle());
        }
        if(yearUpdate){
            this.setYear(apiAnime.getReleaseYear());
        }
        return posterUpdate || titleUpdate || yearUpdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMalId() {
        return malId;
    }

    public void setMalId(int malId) {
        this.malId = malId;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VideoContentType getType() {
        return type;
    }

    public void setType(VideoContentType type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Set<UserAnime> getUserAnimes() {
        return userAnimes;
    }

    public void setUserAnimes(Set<UserAnime> userAnimes) {
        this.userAnimes = userAnimes;
    }

    @Override
    public String toString() {
        return "Anime{" +
                "id=" + id +
                ", malId=" + malId +
                ", poster='" + poster + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", year=" + year +
                '}';
    }
}
