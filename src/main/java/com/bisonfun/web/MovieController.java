package com.bisonfun.web;

import com.bisonfun.client.*;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.model.*;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.*;
import com.bisonfun.service.MovieService;
import com.bisonfun.service.UserMovieService;
import com.bisonfun.service.UserService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

@Controller
public class MovieController {

    private final AniListClient aniListClient;
    private final TmdbClient tmdbClient;
    private final UserService userService;
    private final UserMovieService userMovieService;
    private final MovieService movieService;

    @Autowired
    public MovieController(AniListClient aniListClient, TmdbClient tmdbClient, UserService userService, UserMovieService userMovieService, MovieService movieService) {
        this.aniListClient = aniListClient;
        this.tmdbClient = tmdbClient;
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
                finderResult = tmdbClient.parseMovieList(query, actualPage);
                model.addAttribute("pagination", finderResult);
                return "list_movie";
            case "tv":
                finderResult = tmdbClient.parseTVList(query, actualPage);
                model.addAttribute("pagination", finderResult);
                return "list_tv";
            case "anime":
                finderResult = aniListClient.parse(query, actualPage);
                model.addAttribute("pagination", finderResult);
                return "list_anime";
        }
        return "main";
    }

    @GetMapping("/movie/{id}")
    public String moviePage(Model model, @PathVariable int id, Principal principal) throws JSONException {
        TMDBMovie movie = tmdbClient.parseMovieById(id);

        if (movie.isAnime()) {
            VideoEntertainment anime;
            try {
                anime = aniListClient.parseAnimeByName(movie.getTitle());
            } catch (ContentNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } catch (TooManyAnimeRequestsException e) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
            }
            String animeLink = "/anime/"+anime.getId();
            return "redirect:" + animeLink;
        }

        UserMovie userMovie = principal == null ? new UserMovie() : userMovieService.getUserMovieByUsernameAndId(principal.getName(), movie.getId());

        List<VideoEntertainment> movieRecommendations = tmdbClient.parseMovieRecommendations(id);

        model.addAttribute("content", movie);
        model.addAttribute("recommendations", movieRecommendations);

        model.addAttribute("actions", asList(VideoConsumingStatus.values()));
        model.addAttribute("userMovie", userMovie);

        return "content_movie";
    }

    @PostMapping("/movie/{movieId}")
    public String updateMovieList(UserMovie userMovie, @PathVariable int movieId, Principal principal) throws ContentNotFoundException {
        if (principal == null) {
            String loginLink = "/login";
            return "redirect:"+loginLink;
        }
        if (userMovie.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        User user = userService.getUserByUsername(principal.getName());
        Movie dbMovie = movieService.updateMovie(movieId);

        userMovieService.createUserMovie(userMovie, user, dbMovie);

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
