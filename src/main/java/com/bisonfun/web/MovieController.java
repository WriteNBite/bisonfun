package com.bisonfun.web;

import com.bisonfun.domain.*;
import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.domain.enums.VideoContentStatus;
import com.bisonfun.entity.*;
import com.bisonfun.service.MovieService;
import com.bisonfun.service.UserMovieService;
import com.bisonfun.service.UserService;
import com.bisonfun.utilities.*;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.text.DateFormat;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

@Controller
public class MovieController {

    private final AniParser aniParser;
    private final TMDBParser tmdbParser;
    private final UserService userService;
    private final UserMovieService userMovieService;
    private final MovieService movieService;

    @Autowired
    public MovieController(AniParser aniParser, TMDBParser tmdbParser, UserService userService, UserMovieService userMovieService, MovieService movieService) {
        this.aniParser = aniParser;
        this.tmdbParser = tmdbParser;
        this.userService = userService;
        this.userMovieService = userMovieService;
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public String searchProcess(Model model, @RequestParam String query, @RequestParam String type, @RequestParam Optional<Integer> page) throws JSONException, TooManyAnimeRequestsException {
        int actualPage = page.orElse(1);

        Pagination<VideoEntertainment> finderResult;

        model.addAttribute("query", query);
        model.addAttribute("type", type);

        switch (type) {
            case "movie":
                finderResult = tmdbParser.parseMovieList(query, actualPage);
                model.addAttribute("pagination", finderResult);
                return "list_movie";
            case "tv":
                finderResult = tmdbParser.parseTVList(query, actualPage);
                model.addAttribute("pagination", finderResult);
                return "list_tv";
            case "anime":
                finderResult = aniParser.parse(query, actualPage);
                model.addAttribute("pagination", finderResult);
                return "list_anime";
        }
        return "main";
    }

    @GetMapping("/movie/{id}")
    public String moviePage(Model model, @PathVariable int id, Principal principal) throws JSONException {
        TMDBMovie movie = tmdbParser.parseMovieById(id);

        if (movie.isAnime()) {
            VideoEntertainment anime;
            try {
                anime = aniParser.parseAnimeByName(movie.getTitle());
            } catch (ContentNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } catch (TooManyAnimeRequestsException e) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
            }
            String animeLink = "/anime/"+anime.getId();
            return "redirect:" + animeLink;
        }
        UserMovie userMovie;
        if (principal == null) {
            userMovie = new UserMovie();
        } else {
            User user = userService.getUserByUsername(principal.getName());
            userMovie = userMovieService.getUserMovieById(user.getId(), movie.getId());
        }

        String releaseDate = movie.getReleaseDate() == null ? "???" : DateFormat.getDateInstance().format(movie.getReleaseDate());
        String timeToWatch = movie.getTimeToWatch();

        List<VideoEntertainment> movieRecommendations = tmdbParser.parseMovieRecommendations(id);

        model.addAttribute("content", movie);
        model.addAttribute("releaseDate", releaseDate);
        model.addAttribute("timeToWatch", timeToWatch);
        model.addAttribute("recommendations", movieRecommendations);

        model.addAttribute("actions", asList(VideoConsumingStatus.values()));
        model.addAttribute("userMovie", userMovie);

        return "content_movie";
    }

    @PostMapping("/movie/{movieId}")
    public String saveMovieToList(UserMovie userMovie, @PathVariable int movieId, Principal principal) throws ContentNotFoundException {
        if (principal == null) {
            String loginLink = "/login";
            return "redirect:"+loginLink;
        }
        if (userMovie.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        User user = userService.getUserByUsername(principal.getName());
        TMDBMovie movie = tmdbParser.parseMovieById(movieId);
        Movie dbMovie = movieService.findById(movieId);
        if (dbMovie == null) {
            dbMovie = movieService.addNewMovie(movie);
        }else{
            dbMovie = movieService.updating(dbMovie, movie);
        }
        UserMovieKey userMovieKey = new UserMovieKey(user.getId(), dbMovie.getId());
        userMovie.setId(userMovieKey);
        userMovie.setUser(user);
        userMovie.setMovie(dbMovie);

        UserMovie dbUserMovie = userMovieService.getUserMovieById(userMovieKey);
        if(userMovie.getEpisodes() != dbUserMovie.getEpisodes()){
            if(movie.getStatus() == VideoContentStatus.RELEASED && userMovie.getEpisodes() == 1){// if it released and all episodes watched then it completed
                userMovie.setStatus(VideoConsumingStatus.COMPLETE);
            }
        }else if(userMovie.getStatus() != dbUserMovie.getStatus()){
            if(userMovie.getStatus() == VideoConsumingStatus.COMPLETE) {//if complete then make all episodes watched
                userMovie.setEpisodes(1);
            }else if(userMovie.getStatus() == VideoConsumingStatus.PLANNED){// if planned then 0 episodes watched
                userMovie.setEpisodes(0);
            }
        }
        userMovieService.saveUserMovie(userMovie);

        String movieLink = "/movie/"+movieId;
        return "redirect:" + movieLink;
    }

    @DeleteMapping("/movie/{movieId}")
    public String deleteMovieFromList(@PathVariable int movieId, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        userMovieService.deleteMovieFromUserList(new UserMovieKey(user.getId(), movieId));
        String movieLink = "/movie/"+movieId;
        return "redirect:" + movieLink;
    }

}
