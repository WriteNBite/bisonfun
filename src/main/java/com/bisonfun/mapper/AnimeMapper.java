package com.bisonfun.mapper;

import com.bisonfun.dto.AniAnime;
import com.bisonfun.dto.VideoEntertainment;
import com.bisonfun.entity.Anime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Date;
import java.time.LocalDate;

@Mapper
public interface AnimeMapper {
    @Mapping(target = "malId", source = "idMAL")
    @Mapping(target = "year", expression = "java(anime.getReleaseYear())")
    Anime fromModel(AniAnime anime);


    @Mapping(target = "anime", constant = "true")
    @Mapping(target = "releaseDate", source = "year", qualifiedByName = "yearDate")
    VideoEntertainment toVideoEntertainment(Anime anime);

    @Named("yearDate")
    default Date yearDate(int year){
        return Date.valueOf(LocalDate.ofYearDay(year, 1));
    }
}
