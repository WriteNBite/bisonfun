package com.bisonfun.entity;

import com.bisonfun.dto.enums.VideoContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "anime")
@Getter @Setter @NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anime anime = (Anime) o;
        return id == anime.id && malId == anime.malId && year == anime.year && Objects.equals(poster, anime.poster) && title.equals(anime.title) && type == anime.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, malId, poster, title, type, year);
    }
}
