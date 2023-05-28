package com.bisonfun.mapper;

import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Date;
import java.time.LocalDate;

@Mapper
public interface MovieMapper {

    @Mapping(target = "year", expression = "java(movie.getReleaseYear())")
    Movie fromModel(TMDBMovie movie);

    @Mapping(target = "anime", constant = "false")
    @Mapping(target = "releaseDate", source = "year", qualifiedByName = "yearDate")
    VideoEntertainment toVideoEntertainment(Movie movie);

    @Named("yearDate")
    default Date yearDate(int year){
        return Date.valueOf(LocalDate.ofYearDay(year, 1));
    }
}
