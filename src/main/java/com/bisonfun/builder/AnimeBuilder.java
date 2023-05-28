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
        log.info("Return instance of AnimeBuilder");
        return new AnimeBuilder(id);
    }

    private AnimeBuilder(int id){
        log.info("Instance of JSONUserAniBuilder created");
        log.info("Id: "+id);
        this.id = id;
    }

    public AnimeBuilder addId(int id){
        log.info("Setting id: "+id);
        this.id = id;
        return this;
    }

    public AnimeBuilder addMalId(int malId){
        log.info("Setting MAL id: "+id);
        this.malId = malId;
        return this;
    }

    public AnimeBuilder addPoster(String poster){
        log.info("Setting poster: "+poster);
        this.poster = poster;
        return this;
    }

    public AnimeBuilder addTitle(String title){
        log.info("Setting title: "+title);
        this.title = title;
        return this;
    }

    public AnimeBuilder addType(VideoContentType type){
        log.info("Setting type: "+type);
        this.type = type;
        return this;
    }

    public AnimeBuilder addYear(int year){
        log.info("Setting year: "+year);
        this.year = year;
        return this;
    }

    public Anime build(){
        return new Anime(id, malId, poster, title, type, year);
    }

}
