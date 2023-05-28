package com.bisonfun.entity;

import com.bisonfun.model.enums.VideoContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id && year == movie.year && Objects.equals(imdbId, movie.imdbId) && Objects.equals(poster, movie.poster) && title.equals(movie.title) && type == movie.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imdbId, poster, title, type, year);
    }
}
