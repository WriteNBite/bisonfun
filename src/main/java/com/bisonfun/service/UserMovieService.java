package com.bisonfun.service;

import com.bisonfun.domain.VideoEntertainment;
import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.entity.Movie;
import com.bisonfun.entity.UserMovie;
import com.bisonfun.entity.UserMovieKey;
import com.bisonfun.repository.UserMovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserMovieService {
    @Autowired
    UserMovieRepository userMovieRepo;

    public List<Movie> getMovieListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get movie list\nUser id: "+userId+"\nStatus: "+status);
        List<Movie> movieList = new ArrayList<>();
        for(UserMovie userMovie : userMovieRepo.findUserMovieByUserIdAndStatus(userId, status)){
            movieList.add(userMovie.getMovie());
        }
        log.info("Movie list: "+ movieList);
        return movieList;
    }

    public List<VideoEntertainment> getContentListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get movie list\nUser id: "+userId+"\nStatus: "+status);
        List<VideoEntertainment> movieList = new ArrayList<>();
        for(UserMovie userMovie : userMovieRepo.findUserMovieByUserIdAndStatus(userId, status)){
            Movie movie = userMovie.getMovie();
            movieList.add(new VideoEntertainment(movie.getId(), false, movie.getType(), movie.getTitle(), null, null, movie.getPoster()));
        }
        log.info("Movie list: "+ movieList);
        return movieList;
    }

    public List<UserMovie> getUserMovieListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get userMovie list\nUser id: "+userId+"\nStatus: "+status);
        return userMovieRepo.findUserMovieByUserIdAndStatus(userId, status);
    }

    public UserMovie getUserMovieById(UserMovieKey userMovieId){
        log.info("Getting userMovie by user movie id");
        UserMovie userMovie = userMovieRepo.findById(userMovieId).orElse(new UserMovie());
        log.info("UserMovie: "+userMovie);
        return userMovie;
    }

    public UserMovie getUserMovieById(int userId, int movieId){
        log.info("User id: "+userId+"\nMovie id: "+movieId);
        return getUserMovieById(new UserMovieKey(userId, movieId));
    }

    public UserMovie saveUserMovie(UserMovie userMovie){
        log.info("Saving \nUserMovie: "+userMovie);
        return userMovieRepo.save(userMovie);
    }

    public void deleteMovieFromUserList(UserMovieKey userMovieId){
        log.info("Deleting movie from user list");
        userMovieRepo.findById(userMovieId).ifPresent(userMovie -> userMovieRepo.delete(userMovie));
    }
}
