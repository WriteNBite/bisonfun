package com.bisonfun.web;

import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Controller
public class HomeController {

    final
    AniListClient aniListClient;
    final
    TmdbClient tmdbClient;
    final
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public HomeController(AniListClient aniListClient, TmdbClient tmdbClient, RedisTemplate<String, String> redisTemplate) {
        this.aniListClient = aniListClient;
        this.tmdbClient = tmdbClient;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/")
    public String home(Model model) throws TooManyAnimeRequestsException {
        Random random = new Random();

        List<String> backgrounds = new ArrayList<>(Objects.requireNonNull(redisTemplate.opsForSet().members("main-page-background")));

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
