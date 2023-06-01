package com.bisonfun.web;

import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    final
    AniListClient aniListClient;
    final
    TmdbClient tmdbClient;

    @Autowired
    public HomeController(AniListClient aniListClient, TmdbClient tmdbClient) {
        this.aniListClient = aniListClient;
        this.tmdbClient = tmdbClient;
    }

    @GetMapping("/")
    public String home(Model model) throws TooManyAnimeRequestsException {
        List<VideoEntertainment> animeTrends = aniListClient.parseAnimeTrends();
        List<VideoEntertainment> movieTrends = tmdbClient.parseMovieTrends();
        List<VideoEntertainment> tvTrends = tmdbClient.parseTVTrends();

        model.addAttribute("animeTrends", animeTrends);
        model.addAttribute("movieTrends", movieTrends);
        model.addAttribute("tvTrends", tvTrends);

        return "main";
    }
}
