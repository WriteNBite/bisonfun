package com.bisonfun.builder;

import com.bisonfun.model.enums.VideoContentType;
import com.bisonfun.entity.Anime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnimeBuilder {
    private int id;
    private int malId;
    private String poster;
    private String title;
    private VideoContentType type;
    private int year;

    public static AnimeBuilder getInstance(int id){
        return new AnimeBuilder(id);
    }

    private AnimeBuilder(int id){
        this.id = id;
    }

    public AnimeBuilder addId(int id){
        this.id = id;
        return this;
    }

    public AnimeBuilder addMalId(int malId){
        this.malId = malId;
        return this;
    }

    public AnimeBuilder addPoster(String poster){
        this.poster = poster;
        return this;
    }

    public AnimeBuilder addTitle(String title){
        this.title = title;
        return this;
    }

    public AnimeBuilder addType(VideoContentType type){
        this.type = type;
        return this;
    }

    public AnimeBuilder addYear(int year){
        this.year = year;
        return this;
    }

    public Anime build(){
        return new Anime(id, malId, poster, title, type, year);
    }

}
