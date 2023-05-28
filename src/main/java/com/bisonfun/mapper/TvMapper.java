package com.bisonfun.mapper;

import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.entity.Tv;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Date;
import java.time.LocalDate;

@Mapper
public interface TvMapper {
    @Mapping(target = "year", expression = "java(tv.getReleaseYear())")
    Tv fromModel(TMDBTVShow tv);

    @Mapping(target = "anime", constant = "false")
    @Mapping(target = "releaseDate", source = "year", qualifiedByName = "yearDate")
    VideoEntertainment toVideoEntertainment(Tv tv);

    @Named("yearDate")
    default Date yearDate(int year){
        return Date.valueOf(LocalDate.ofYearDay(year, 1));
    }
}
