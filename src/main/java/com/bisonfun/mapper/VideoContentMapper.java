package com.bisonfun.mapper;

import com.bisonfun.entity.Anime;
import com.bisonfun.entity.Movie;
import com.bisonfun.entity.Tv;
import com.bisonfun.entity.VideoContent;
import com.bisonfun.model.AniAnime;
import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoContentCategory;
import com.bisonfun.model.enums.VideoContentType;
import com.sun.istack.NotNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Date;
import java.time.LocalDate;

@Mapper
public interface VideoContentMapper {

    @Mapping(target = "id", expression = "java(nullId())")
    @Mapping(target = "poster", expression = "java(posterPath(anime, movie, tv))")
    @Mapping(target = "title", expression = "java(contentTitle(anime, movie, tv))")
    @Mapping(target = "category", expression = "java(contentCategory(anime))")
    @Mapping(target = "type", expression = "java(contentType(anime, movie, tv))")
    @Mapping(target = "year", expression = "java(contentYear(anime, movie, tv))")
    @Mapping(target = "imdbId", expression = "java(contentImdbId(movie, tv))")
    @Mapping(target = "tmdbId", expression = "java(contentTmdbId(movie, tv))")
    @Mapping(target = "malId", expression = "java(contentMalId(anime))")
    @Mapping(target = "aniListId", expression = "java(contentAniListId(anime))")
    VideoContent fromModels(@Nullable AniAnime anime, @Nullable TMDBMovie movie, @Nullable TMDBTVShow tv);

    default VideoContent fromMovieModels(@Nullable AniAnime anime, @Nullable TMDBMovie movie){
        return fromModels(anime, movie, null);
    }
    default VideoContent fromTvModels(@Nullable AniAnime anime, @Nullable TMDBTVShow tv){
        return fromModels(anime, null, tv);
    }
    default VideoContent fromAniAnime(@NotNull AniAnime anime){
        return fromModels(anime, null, null);
    }
    default VideoContent fromTmdbMovie(@NotNull TMDBMovie movie){
        return fromMovieModels(null, movie);
    }
    default VideoContent fromTmdbTv(@NotNull TMDBTVShow tv){
        return fromTvModels(null, tv);
    }

    @Mapping(target = "id", expression = "java(nullId())")
    @Mapping(target = "aniListId", source = "id")
    @Mapping(target = "category", constant = "ANIME")
    @Mapping(target = "year", expression = "java(parseYear(anime.getYear()))")
    VideoContent fromAnime(Anime anime);

    @Mapping(target = "id", expression = "java(nullId())")
    @Mapping(target = "tmdbId", source = "id")
    @Mapping(target = "category", constant = "MAINSTREAM")
    @Mapping(target = "year", expression = "java(parseYear(movie.getYear()))")
    VideoContent fromMovie(Movie movie);

    @Mapping(target = "id", expression = "java(nullId())")
    @Mapping(target = "tmdbId", source = "id")
    @Mapping(target = "category", constant = "MAINSTREAM")
    @Mapping(target = "year", expression = "java(parseYear(tv.getYear()))")
    VideoContent fromTv(Tv tv);

    @Mapping(target = "releaseDate", source = "year", qualifiedByName = "yearDate")
    @Mapping(target = "anime", source = "category", qualifiedByName = "isAnime")
    VideoEntertainment toVideoEntertainment(VideoContent videoContent);

    @Named("yearDate")
    default Date yearDate(int year) {
        return Date.valueOf(LocalDate.ofYearDay(year, 1));
    }

    @Named("isAnime")
    default boolean isAnime(VideoContentCategory category) {
        return category == VideoContentCategory.ANIME;
    }

    default String posterPath(VideoEntertainment... videoEntertainments) {
        String posterPath = null;
        for (VideoEntertainment videoEntertainment : videoEntertainments) {
            if (posterPath == null && videoEntertainment != null) {
                posterPath = videoEntertainment.getPoster();
            }
        }
        return posterPath;
    }

    default String contentTitle(AniAnime anime, TMDBMovie movie, TMDBTVShow tv) {
        if (anime != null) {
            return anime.getTitle();
        } else if (movie != null) {
            return movie.getTitle();
        } else if (tv != null) {
            return tv.getTitle();
        } else {
            throw new IllegalArgumentException("AniAnime, TMDBMovie and TMDBTVShow are null");
        }
    }

    default VideoContentCategory contentCategory(AniAnime anime) {
        if (anime != null) {
            return VideoContentCategory.ANIME;
        } else {
            return VideoContentCategory.MAINSTREAM;
        }
    }

    default VideoContentType contentType(VideoEntertainment... videoEntertainments) {
        VideoContentType type = VideoContentType.UNKNOWN;
        for (VideoEntertainment videoEntertainment : videoEntertainments) {
            if (type == VideoContentType.UNKNOWN && videoEntertainment != null) {
                type = videoEntertainment.getType();
            }
        }
        return type;
    }

    default Integer contentYear(VideoEntertainment... videoEntertainments) {
        Integer year = null;
        for(VideoEntertainment videoEntertainment : videoEntertainments){
            if(year == null && videoEntertainment != null){
                year = parseYear(videoEntertainment.getReleaseYear());
            }
        }
        return year;
    }

    default Integer parseYear(int year){
        return year > 0 ? year : null;
    }

    default String contentImdbId(TMDBMovie movie, TMDBTVShow tv){
        String id = null;
        if(movie != null){
            id = movie.getImdbId();
        }else if(tv != null){
            id = tv.getImdbId();
        }
        return id;
    }

    default Integer contentTmdbId(TMDBMovie movie, TMDBTVShow tv){
        Integer id = null;
        if(movie != null){
            id = movie.getId();
        }else if(tv != null){
            id = tv.getId();
        }
        return id;
    }

    default Integer contentMalId(AniAnime anime){
        Integer id = null;
        if(anime != null && anime.getIdMAL() > 0){
            id = anime.getIdMAL();
        }
        return id;
    }

    default Integer contentAniListId(AniAnime anime){
        Integer id = null;
        if(anime != null){
            id = anime.getId();
        }
        return id;
    }

    default Long nullId(){
        return null;
    }
}
