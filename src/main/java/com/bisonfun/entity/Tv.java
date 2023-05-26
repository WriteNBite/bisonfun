package com.bisonfun.entity;

import com.bisonfun.dto.TMDBTVShow;
import com.bisonfun.dto.enums.VideoContentType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tv")
public class Tv implements Serializable {
    @Id
    private int id;

    private String poster;
    private String title;
    private VideoContentType type;
    private int year;

    @OneToMany(mappedBy = "tv", fetch = FetchType.LAZY)
    private Set<UserTv> userTvs = new HashSet<>();

    public Tv(int id, String poster, String title, VideoContentType type, int year) {
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public Tv() {
    }

    public boolean update(TMDBTVShow apiTv){
        boolean posterUpdate = !this.poster.equals(apiTv.getPoster());
        boolean titleUpdate = !this.title.equals(apiTv.getTitle());
        boolean yearUpdate = this.year != apiTv.getReleaseYear();
        if(posterUpdate){ //update content if was something new
            this.setPoster(apiTv.getPoster());
        }
        if(titleUpdate){
            this.setTitle(apiTv.getTitle());
        }
        if(yearUpdate){
            this.setYear(apiTv.getReleaseYear());
        }
        return posterUpdate || titleUpdate || yearUpdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Set<UserTv> getUserTvs() {
        return userTvs;
    }

    public void setUserTvs(Set<UserTv> userTvs) {
        this.userTvs = userTvs;
    }

    @Override
    public String toString() {
        return "Tv{" +
                "id=" + id +
                ", poster='" + poster + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", year=" + year +
                '}';
    }
}
