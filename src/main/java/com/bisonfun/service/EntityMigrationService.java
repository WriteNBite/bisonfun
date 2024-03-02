package com.bisonfun.service;

import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.Movie;
import com.bisonfun.entity.Tv;
import com.bisonfun.entity.VideoContent;
import com.bisonfun.mapper.VideoContentMapper;
import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.repository.AnimeRepository;
import com.bisonfun.repository.MovieRepository;
import com.bisonfun.repository.TvRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EntityMigrationService {
    private final AnimeRepository animeRepository;
    private final MovieRepository movieRepository;
    private final TvRepository tvRepository;
    private final VideoContentService videoContentService;
    private final VideoContentMapper videoContentMapper;
    private final TmdbClient tmdbClient;

    @Autowired
    public EntityMigrationService(AnimeRepository animeRepository, MovieRepository movieRepository, TvRepository tvRepository, VideoContentService videoContentService, VideoContentMapper videoContentMapper, TmdbClient tmdbClient) {
        this.animeRepository = animeRepository;
        this.movieRepository = movieRepository;
        this.tvRepository = tvRepository;
        this.videoContentService = videoContentService;
        this.videoContentMapper = videoContentMapper;
        this.tmdbClient = tmdbClient;
    }

    public void migrateOldEntityToVideoContent() {
        List<Anime> animeList = getTotalAnimeList();
        for (Anime anime : animeList){
            VideoContent videoContent = videoContentMapper.fromAnime(anime);
            if(videoContentService.getVideoContentByVideoContent(videoContent).isEmpty()){
                VideoEntertainment videoEntertainment = anime.getYear() > 0 ? tmdbClient.parseTmdbContentByName(anime.getTitle(), anime.getType(), anime.getYear()) : null;
                if(videoEntertainment != null){
                    Integer tmdbId = null;
                    String imdbId = null;
                    switch (videoEntertainment.getType()){
                        case TV:
                            TMDBTVShow tmdbtvShow = tmdbClient.parseShowById(videoEntertainment.getId());
                            tmdbId = tmdbtvShow.getId();
                            imdbId = tmdbtvShow.getImdbId();
                            break;
                        case MOVIE:
                            TMDBMovie tmdbMovie = tmdbClient.parseMovieById(videoEntertainment.getId());
                            tmdbId = tmdbMovie.getId();
                            imdbId = tmdbMovie.getImdbId();
                            break;
                    }
                    videoContent.setImdbId(imdbId);
                    videoContent.setTmdbId(tmdbId);
                }
                try {
                    videoContentService.addNewVideoContent(videoContent);
                } catch (ContentNotFoundException e) {
                    log.error(e.getMessage());
                }
            }
        }

        List<Movie> movieList = getTotalMovieList();
        for(Movie movie : movieList){
            VideoContent videoContent = videoContentMapper.fromMovie(movie);
            if(videoContentService.getVideoContentByVideoContent(videoContent).isEmpty()){
                try {
                    videoContentService.addNewVideoContent(videoContent);
                } catch (ContentNotFoundException e) {
                    log.error(e.getMessage());
                }
            }
        }

        List<Tv> tvList = getTotalTvList();
        for (Tv tv : tvList){
            VideoContent videoContent = videoContentMapper.fromTv(tv);
            if(videoContentService.getVideoContentByVideoContent(videoContent).isEmpty()){
                TMDBTVShow tmdbtvShow = tmdbClient.parseShowById(tv.getId());
                videoContent.setImdbId(tmdbtvShow.getImdbId());
                try {
                    videoContentService.addNewVideoContent(videoContent);
                } catch (ContentNotFoundException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private List<Anime> getTotalAnimeList(){
        List<Anime> animeList = new ArrayList<>();
        animeRepository.findAll().forEach(animeList::add);
        return animeList;
    }

    private List<Movie> getTotalMovieList(){
        List<Movie> movieList = new ArrayList<>();
        movieRepository.findAll().forEach(movieList::add);
        return movieList;
    }

    private List<Tv> getTotalTvList(){
        List<Tv> tvList = new ArrayList<>();
        tvRepository.findAll().forEach(tvList::add);
        return tvList;
    }
}
