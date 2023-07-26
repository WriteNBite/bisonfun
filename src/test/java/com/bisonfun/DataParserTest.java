package com.bisonfun;

import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DataParserTest {
    @Autowired
    private AniListClient aniListClient;
    @Autowired
    private TmdbClient tmdbClient;

    @Test
    public void movieTrendsTest(){
        List<VideoEntertainment> movieTrends = tmdbClient.parseMovieTrends();
        assertTrue(movieTrends.size() > 0);
    }
    @Test
    public void tvTrendsTest(){
        List<VideoEntertainment> tvTrends = tmdbClient.parseTVTrends();
        assertTrue(tvTrends.size() > 0);
    }
    @Test
    public void animeTrendsTest() throws TooManyAnimeRequestsException {
        List<VideoEntertainment> animeTrends = aniListClient.parseAnimeTrends();
        assertTrue(animeTrends.size() > 0);
    }

    @Test
    public void animeRecommendationsTest() throws ContentNotFoundException, TooManyAnimeRequestsException {
        VideoEntertainment[] animeRecommendations = aniListClient.parseById(1).getRecommendations();
        assertTrue(animeRecommendations.length > 0);
    }
    @Test
    public void movieRecommendationsTest(){
        List<TMDBMovie> movieRecommendations = tmdbClient.parseMovieRecommendations(12160);
        assertTrue(movieRecommendations.size() > 0);
    }
    @Test
    public void tvRecommendationsTest(){
        List<VideoEntertainment> tvRecommendations = tmdbClient.parseTVRecommendations(207863);
        assertTrue(tvRecommendations.size() > 0);
    }
}
