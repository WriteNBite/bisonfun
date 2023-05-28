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
@Table(name = "tv")
@Getter
@Setter
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tv tv = (Tv) o;
        return id == tv.id && year == tv.year && Objects.equals(poster, tv.poster) && title.equals(tv.title) && type == tv.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, poster, title, type, year);
    }
}
