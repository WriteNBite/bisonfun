package com.bisonfun.entity;

import com.bisonfun.dto.TMDBTVShow;
import com.bisonfun.dto.enums.VideoContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
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
