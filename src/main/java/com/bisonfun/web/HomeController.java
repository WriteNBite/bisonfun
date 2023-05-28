package com.bisonfun.web;

import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.utilities.AniParser;
import com.bisonfun.utilities.TMDBParser;
import com.bisonfun.utilities.TooManyAnimeRequestsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    final
    AniParser aniParser;
    final
    TMDBParser tmdbParser;

    @Autowired
    public HomeController(AniParser aniParser, TMDBParser tmdbParser) {
        this.aniParser = aniParser;
        this.tmdbParser = tmdbParser;
    }

    @GetMapping("/")
    public String home(Model model) throws TooManyAnimeRequestsException {
        List<VideoEntertainment> animeTrends = aniParser.parseAnimeTrends();
        List<VideoEntertainment> movieTrends = tmdbParser.parseMovieTrends();
        List<VideoEntertainment> tvTrends = tmdbParser.parseTVTrends();

        model.addAttribute("animeTrends", animeTrends);
        model.addAttribute("movieTrends", movieTrends);
        model.addAttribute("tvTrends", tvTrends);

        return "main";
    }
}
