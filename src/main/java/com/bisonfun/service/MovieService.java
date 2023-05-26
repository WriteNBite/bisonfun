package com.bisonfun.service;

import com.bisonfun.dto.TMDBMovie;
import com.bisonfun.entity.Movie;
import com.bisonfun.repository.MovieRepository;
import com.bisonfun.utilities.ContentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie findById(int movieId){
        return movieRepository.findById(movieId).orElse(null);
    }

    public Movie addNewMovie(TMDBMovie apiMovie) throws ContentNotFoundException {
        if(apiMovie == null){
            throw new ContentNotFoundException();
        }
        Movie dbMovie = new Movie(apiMovie.getId(), apiMovie.getImdbId(), apiMovie.getPoster(), apiMovie.getTitle(), apiMovie.getType(), apiMovie.getReleaseYear());
        return movieRepository.save(dbMovie);
    }

    public Movie updating(Movie dbMovie, TMDBMovie apiMovie){
        if(dbMovie.update(apiMovie)){
            movieRepository.save(dbMovie);
        }
        return dbMovie;
    }
}
