package com.bisonfun.entity;

import com.bisonfun.model.enums.VideoContentCategory;
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
@Table(name = "video_content")
@Getter
@Setter
@NoArgsConstructor
public class VideoContent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String poster;
    private String title;
    private VideoContentCategory category;
    private VideoContentType type;
    private Integer year;
    private String imdbId;
    private Integer tmdbId;
    private Integer malId;
    private Integer aniListId;

    @OneToMany(mappedBy = "videoContent", fetch = FetchType.LAZY)
    private Set<UserVideoContent> userVideoContents = new HashSet<>();

    public VideoContent(long id, String poster, String title, VideoContentCategory category, VideoContentType type, Integer year, String imdbId, Integer tmdbId, Integer malId, Integer aniListId) {
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.category = category;
        this.type = type;
        this.year = year;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;
        this.malId = malId;
        this.aniListId = aniListId;
    }

    @Override
    public String toString() {
        return "VideoContent{" +
                "id=" + id +
                ", poster='" + poster + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", type=" + type +
                ", year=" + year +
                ", imdbId='" + imdbId + '\'' +
                ", tmdbId=" + tmdbId +
                ", malId=" + malId +
                ", aniListId=" + aniListId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoContent that = (VideoContent) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && category == that.category && type == that.type && Objects.equals(year, that.year) && Objects.equals(imdbId, that.imdbId) && Objects.equals(tmdbId, that.tmdbId) && Objects.equals(malId, that.malId) && Objects.equals(aniListId, that.aniListId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, category, type, year, imdbId, tmdbId, malId, aniListId);
    }
}
