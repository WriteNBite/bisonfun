package com.bisonfun.service;

import com.bisonfun.dto.VideoEntertainment;
import com.bisonfun.dto.enums.VideoConsumingStatus;
import com.bisonfun.dto.enums.VideoContentStatus;
import com.bisonfun.entity.Movie;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserMovie;
import com.bisonfun.entity.UserMovieKey;
import com.bisonfun.mapper.MovieMapper;
import com.bisonfun.repository.UserMovieRepository;
import com.bisonfun.utilities.TMDBParser;
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
    final TMDBParser tmdbParser;
    final MovieMapper movieMapper;

    @Autowired
    public UserMovieService(UserMovieRepository userMovieRepo, UserService userService, TMDBParser tmdbParser, MovieMapper movieMapper) {
        this.userMovieRepo = userMovieRepo;
        this.userService = userService;
        this.tmdbParser = tmdbParser;
        this.movieMapper = movieMapper;
    }

    public int[] getSizeOfLists(int userId){
        return new int[]{
                getUserMovieListByStatus(userId, VideoConsumingStatus.PLANNED).size(),
                getUserMovieListByStatus(userId, VideoConsumingStatus.WATCHING).size(),
                getUserMovieListByStatus(userId, VideoConsumingStatus.COMPLETE).size()
        };
    }

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
        List<Movie> movieList = getMovieListByStatus(userId, status);
        log.info("Movie list: "+ movieList);
        return movieList.stream().map(movieMapper::toVideoEntertainment).collect(Collectors.toList());
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

    public void createUserMovie(UserMovie userMovie, User user, Movie movie){
        UserMovieKey userMovieKey = new UserMovieKey(user.getId(), movie.getId());
        userMovie.setId(userMovieKey);
        userMovie.setUser(user);
        userMovie.setMovie(movie);

        UserMovie dbUserMovie = getUserMovieById(userMovieKey);
        if(userMovie.getEpisodes() != dbUserMovie.getEpisodes()){
            if(tmdbParser.parseMovieById(movie.getId()).getStatus() == VideoContentStatus.RELEASED && userMovie.getEpisodes() == 1){// if it released and all episodes watched then it completed
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
        return getUserMovieById(userService.getUserByUsername(username).getId(), movieId);
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
        userMovieRepo.findById(userMovieId).ifPresent(userMovieRepo::delete);
    }
}
