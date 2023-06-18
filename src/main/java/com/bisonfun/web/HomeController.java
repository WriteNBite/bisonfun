package com.bisonfun.web;

import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import com.bisonfun.service.redis.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Controller
public class HomeController {

    final
    AniListClient aniListClient;
    final
    TmdbClient tmdbClient;
    final
    ImageService imageService;

    @Autowired
    public HomeController(AniListClient aniListClient, TmdbClient tmdbClient, ImageService imageService) {
        this.aniListClient = aniListClient;
        this.tmdbClient = tmdbClient;
        this.imageService = imageService;
    }

    @GetMapping("/")
    public String home(Model model) throws TooManyAnimeRequestsException {
        Random random = new Random();

        List<String> backgrounds = imageService.getMainPageBackground();

        List<VideoEntertainment> animeTrends = aniListClient.parseAnimeTrends();
        List<VideoEntertainment> movieTrends = tmdbClient.parseMovieTrends();
        List<VideoEntertainment> tvTrends = tmdbClient.parseTVTrends();

        model.addAttribute("animeTrends", animeTrends);
        model.addAttribute("movieTrends", movieTrends);
        model.addAttribute("tvTrends", tvTrends);
        model.addAttribute("background", backgrounds.get(random.nextInt(backgrounds.size())));

        return "main";
    }
}
