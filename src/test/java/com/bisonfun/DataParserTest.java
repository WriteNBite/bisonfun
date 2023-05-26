package com.bisonfun;

import com.bisonfun.dto.VideoEntertainment;
import com.bisonfun.utilities.AniParser;
import com.bisonfun.utilities.ContentNotFoundException;
import com.bisonfun.utilities.TMDBParser;
import com.bisonfun.utilities.TooManyAnimeRequestsException;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DataParserTest {
    @Autowired
    private AniParser aniParser;
    @Autowired
    private TMDBParser tmdbParser;

    @Test
    public void movieTrendsTest(){
        List<VideoEntertainment> movieTrends = tmdbParser.parseMovieTrends();
        assertTrue(movieTrends.size() > 0);
    }
    @Test
    public void tvTrendsTest(){
        List<VideoEntertainment> tvTrends = tmdbParser.parseTVTrends();
        assertTrue(tvTrends.size() > 0);
    }
    @Test
    public void animeTrendsTest() throws TooManyAnimeRequestsException {
        List<VideoEntertainment> animeTrends = aniParser.parseAnimeTrends();
        assertTrue(animeTrends.size() > 0);
    }

    @Test
    public void animeRecommendationsTest() throws ContentNotFoundException, TooManyAnimeRequestsException {
        VideoEntertainment[] animeRecommendations = aniParser.parseById(1).getRecommendations();
        assertTrue(animeRecommendations.length > 0);
    }
    @Test
    public void movieRecommendationsTest(){
        List<VideoEntertainment> movieRecommendations = tmdbParser.parseMovieRecommendations(12160);
        assertTrue(movieRecommendations.size() > 0);
    }
    @Test
    public void tvRecommendationsTest(){
        List<VideoEntertainment> tvRecommendations = tmdbParser.parseTVRecommendations(207863);
        assertTrue(tvRecommendations.size() > 0);
    }
}
