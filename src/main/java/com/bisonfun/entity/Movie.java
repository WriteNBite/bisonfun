package com.bisonfun.entity;

import com.bisonfun.dto.TMDBMovie;
import com.bisonfun.dto.enums.VideoContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
public class Movie implements Serializable {
    @Id
    private int id;

    private String imdbId;
    private String poster;
    private String title;
    private VideoContentType type;
    private int year;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private Set<UserMovie> userMovies = new HashSet<>();

    public Movie(int id, String imdbId, String poster, String title, VideoContentType type, int year) {
        this.id = id;
        this.imdbId = imdbId;
        this.poster = poster;
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public boolean update(TMDBMovie apiMovie){
        boolean posterUpdate = !this.poster.equals(apiMovie.getPoster());
        boolean titleUpdate = !this.title.equals(apiMovie.getTitle());
        boolean yearUpdate = this.year != apiMovie.getReleaseYear();
        if(posterUpdate){ //update content if was something new
            this.setPoster(apiMovie.getPoster());
        }
        if(titleUpdate){
            this.setTitle(apiMovie.getTitle());
        }
        if(yearUpdate){
            this.setYear(apiMovie.getReleaseYear());
        }
        return posterUpdate || titleUpdate || yearUpdate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", imdbId='" + imdbId + '\'' +
                ", poster='" + poster + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", year=" + year +
                '}';
    }
}
