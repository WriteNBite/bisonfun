package com.bisonfun.service;

import com.bisonfun.model.TMDBMovie;
import com.bisonfun.entity.Movie;
import com.bisonfun.mapper.MovieMapper;
import com.bisonfun.repository.MovieRepository;
import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.tmdb.TmdbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final TmdbClient tmdbClient;

    @Autowired
    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper, TmdbClient tmdbClient) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.tmdbClient = tmdbClient;
    }

    public Movie updateMovie(int movieId) throws ContentNotFoundException {
        TMDBMovie movie = tmdbClient.parseMovieById(movieId);
        Movie dbMovie = findById(movieId);
        if (dbMovie == null) {
            dbMovie = addNewMovie(movieMapper.fromModel(movie));
        }else{
            dbMovie = updateMovie(dbMovie, movieMapper.fromModel(movie));
        }
        return dbMovie;
    }

    public Movie findById(int movieId){
        return movieRepository.findById(movieId).orElse(null);
    }

    public Movie addNewMovie(Movie movie) throws ContentNotFoundException {
        if(movie == null){
            throw new ContentNotFoundException();
        }
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Movie dbMovie, Movie apiMovie){
        if(dbMovie.equals(apiMovie)){
            return dbMovie;
        }else {
            return movieRepository.save(apiMovie);
        }
    }
}
