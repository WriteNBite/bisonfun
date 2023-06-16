package com.bisonfun.service;

import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.entity.Movie;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserMovie;
import com.bisonfun.entity.UserMovieKey;
import com.bisonfun.mapper.MovieMapper;
import com.bisonfun.repository.UserMovieRepository;
import com.bisonfun.client.tmdb.TmdbClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserMovieService {
    final
    UserMovieRepository userMovieRepo;
    final UserService userService;
    final TmdbClient tmdbClient;
    final MovieMapper movieMapper;

    @Autowired
    public UserMovieService(UserMovieRepository userMovieRepo, UserService userService, TmdbClient tmdbClient, MovieMapper movieMapper) {
        this.userMovieRepo = userMovieRepo;
        this.userService = userService;
        this.tmdbClient = tmdbClient;
        this.movieMapper = movieMapper;
    }

    public long[] getSizeOfLists(int userId){
        return new long[]{
                getSizeOfListByStatus(userId, VideoConsumingStatus.PLANNED),
                getSizeOfListByStatus(userId, VideoConsumingStatus.WATCHING),
                getSizeOfListByStatus(userId, VideoConsumingStatus.COMPLETE)
        };
    }
    public long getSizeOfListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get Size of User {} {} Movie List", userId, status);
        return userMovieRepo.countUserMovieByUserIdAndStatus(userId, status);
    }

    public List<Movie> getMovieListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get User {} Movie {} list",userId, status);
        List<Movie> movieList = new ArrayList<>();
        for(UserMovie userMovie : userMovieRepo.findUserMovieByUserIdAndStatus(userId, status)){
            movieList.add(userMovie.getMovie());
        }
        log.debug("Movie list: "+ movieList);
        return movieList;
    }

    public List<VideoEntertainment> getContentListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get User {} Video Entertainment {} Movie list",userId, status);
        List<Movie> movieList = getMovieListByStatus(userId, status);
        log.debug("Movie list: "+ movieList);
        return movieList.stream().map(movieMapper::toVideoEntertainment).collect(Collectors.toList());
    }

    public List<UserMovie> getUserMovieListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get UserMovie {} list by User {}", status, userId);
        return userMovieRepo.findUserMovieByUserIdAndStatus(userId, status);
    }

    public UserMovie getUserMovieById(UserMovieKey userMovieId){
        log.info("Get UserMovie by User {} and Movie {} by UserMovieKey", userMovieId.getUserId(), userMovieId.getMovieId());
        UserMovie userMovie = userMovieRepo.findById(userMovieId).orElse(new UserMovie());
        log.debug("UserMovie: "+userMovie);
        return userMovie;
    }

    public void createUserMovie(UserMovie userMovie, User user, Movie movie){
        UserMovieKey userMovieKey = new UserMovieKey(user.getId(), movie.getId());
        userMovie.setId(userMovieKey);
        userMovie.setUser(user);
        userMovie.setMovie(movie);

        UserMovie dbUserMovie = getUserMovieById(userMovieKey);
        if(userMovie.getEpisodes() != dbUserMovie.getEpisodes()){
            if(tmdbClient.parseMovieById(movie.getId()).getStatus() == VideoContentStatus.RELEASED && userMovie.getEpisodes() == 1){// if it released and all episodes watched then it completed
                userMovie.setStatus(VideoConsumingStatus.COMPLETE);
            }
        }else if(userMovie.getStatus() != dbUserMovie.getStatus()){
            if(userMovie.getStatus() == VideoConsumingStatus.COMPLETE) {//if complete then make all episodes watched
                userMovie.setEpisodes(1);
            }else if(userMovie.getStatus() == VideoConsumingStatus.PLANNED){// if planned then 0 episodes watched
                userMovie.setEpisodes(0);
            }
        }
        saveUserMovie(userMovie);
    }

    public UserMovie getUserMovieByUsernameAndId(String username, int movieId){
        log.info("Get UserMovie by User {} and Movie {}", username, movieId);
        return getUserMovieById(userService.getUserByUsername(username).getId(), movieId);
    }

    public UserMovie getUserMovieById(int userId, int movieId){
        log.info("Get UserMovie by User {} and Movie {}", userId, movieId);
        return getUserMovieById(new UserMovieKey(userId, movieId));
    }

    public UserMovie saveUserMovie(UserMovie userMovie){
        log.info("Save Movie {} in User {} list", userMovie.getMovie().getId(), userMovie.getUser().getId());
        return userMovieRepo.save(userMovie);
    }

    public void deleteMovieFromUserList(UserMovieKey userMovieId){
        log.info("Delete Movie {} from User {} list", userMovieId.getMovieId(), userMovieId.getUserId());
        userMovieRepo.findById(userMovieId).ifPresent(userMovieRepo::delete);
    }
}
