package com.bisonfun;

import com.bisonfun.entity.VideoContent;
import com.bisonfun.mapper.VideoContentMapper;
import com.bisonfun.model.AniAnime;
import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoContentCategory;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class VideoContentMapperTest {
    @Autowired
    private VideoContentMapper videoContentMapper;

    private AniAnime animeMovie;
    private AniAnime animeTv;
    private TMDBMovie movie;
    private TMDBTVShow tv;

    @Before
    public void init(){
        animeMovie = new AniAnime(
                1,
                true,
                VideoContentType.MOVIE,
                "Example Title",
                "Example Description",
                120,
                null,
                null,
                8.5f,
                new String[]{"Action", "Adventure"},
                VideoContentStatus.PLANNED,
                null,
                10,
                12345,
                new String[]{"Studio 1", "Studio 2"},
                new String[]{"Other Name 1", "Other Name 2"},
                new VideoEntertainment[0]
        );
        animeTv = new AniAnime(
                2,
                true,
                VideoContentType.TV,
                "Example Title",
                "Example Description",
                122,
                null,
                null,
                8.5f,
                new String[]{"Action", "Adventure"},
                VideoContentStatus.PLANNED,
                null,
                10,
                2334,
                new String[]{"Studio 1", "Studio 2"},
                new String[]{"Other Name 1", "Other Name 2"},
                new VideoEntertainment[0]
        );
        movie = new TMDBMovie(
                1,
                "tt1234567",
                false,
                "Example Movie Title",
                "Example Movie Description",
                120,
                null,
                "movie-poster.jpg",
                8.0f,
                new String[]{"Action", "Adventure"},
                VideoContentStatus.PLANNED,
                new String[]{"Studio 1", "Studio 2"}
        );
        tv = new TMDBTVShow(
                1,
                false,
                "Example TV Show Title",
                "Example TV Show Description",
                30,
                null,
                "tv-show-poster.jpg",
                8.0f,
                new String[]{"Drama", "Thriller"},
                VideoContentStatus.PLANNED,
                null,
                10,
                5,
                new String[]{"Network 1", "Network 2"},
                new String[]{"Studio 1", "Studio 2"},
                "tt2345678"
        );
    }

    @Test
    public void fromVideoContentToVideoEntertainmentTest(){
        VideoContent videoContent = new VideoContent(25, "poster", "Testing title", VideoContentCategory.ANIME, VideoContentType.TV, 2022, "tt9018736", 45, 1, 1);
         VideoEntertainment videoEntertainment = videoContentMapper.toVideoEntertainment(videoContent);

         assertEquals("Id should be equal", (long)videoContent.getId(), videoEntertainment.getId());
         assertEquals("Poster should be equal", videoContent.getPoster(), videoEntertainment.getPoster());
         assertEquals("Title should be equal", videoContent.getTitle(), videoEntertainment.getTitle());
         assertTrue("isAnime should be true", videoEntertainment.isAnime());
         assertEquals("Type should be equal", videoContent.getType(), videoEntertainment.getType());
         assertEquals("Year should be equal", (int)videoContent.getYear(), videoEntertainment.getReleaseYear());
    }

    @Test
    public void fromAniAnimeToVideoContentTest(){
        VideoContent videoContent = videoContentMapper.fromAniAnime(animeMovie);

        assertNull("Id should be null", videoContent.getId());
        assertEquals("Poster should be equal", videoContent.getPoster(), animeMovie.getPoster());
        assertEquals("Title should be equal", videoContent.getTitle(), animeMovie.getTitle());
        assertEquals("Category should be anime", videoContent.getCategory(), VideoContentCategory.ANIME);
        assertEquals("Type should be equal", videoContent.getType(), animeMovie.getType());
        if(animeMovie.getReleaseYear() > 0) {
            assertEquals("Year should be equal", (int) videoContent.getYear(), animeMovie.getReleaseYear());
        }else{
            assertNull("Year should be null", videoContent.getYear());
        }
        assertNull("Imdb id should be null", videoContent.getImdbId());
        assertNull("Tmdb id should be null", videoContent.getTmdbId());
        assertEquals("MAL Id should be equal", (int)videoContent.getMalId(), animeMovie.getIdMAL());
        assertEquals("Anilist Id should be equal", (int)videoContent.getAniListId(), animeMovie.getId());
    }

    @Test
    public void fromTmdbMovieToVideoContentTest(){
        VideoContent videoContent = videoContentMapper.fromTmdbMovie(movie);

        assertNull("Id should be null", videoContent.getId());
        assertEquals("Poster should be equal", videoContent.getPoster(), movie.getPoster());
        assertEquals("Title should be equal", videoContent.getTitle(), movie.getTitle());
        assertEquals("Category should be mainstream", videoContent.getCategory(), VideoContentCategory.MAINSTREAM);
        assertEquals("Type should be equal", videoContent.getType(), movie.getType());
        if(movie.getReleaseYear() > 0) {
            assertEquals("Year should be equal", (int) videoContent.getYear(), movie.getReleaseYear());
        }else{
            assertNull("Year should be null", videoContent.getYear());
        }
        assertNull("Mal id should be null", videoContent.getMalId());
        assertNull("Anilist id should be null", videoContent.getAniListId());
        assertEquals("Imdb Id should be equal", videoContent.getImdbId(), movie.getImdbId());
        assertEquals("TMDB Id should be equal", (int)videoContent.getTmdbId(), movie.getId());
    }

    @Test
    public void fromAnimeAndMovieToVideoContentTest(){
        VideoContent videoContent = videoContentMapper.fromMovieModels(animeMovie, movie);

        assertNull("Id should be null", videoContent.getId());
        assertEquals("Poster should be equal to movie", videoContent.getPoster(), movie.getPoster());
        assertEquals("Title should be equal to anime", videoContent.getTitle(), animeMovie.getTitle());
        assertEquals("Category should be anime", videoContent.getCategory(), VideoContentCategory.ANIME);
        assertEquals("Type should be Movie", videoContent.getType(), VideoContentType.MOVIE);
        if(animeMovie.getReleaseYear() > 0) {
            assertEquals("Year should be equal to anime", (int) videoContent.getYear(), animeMovie.getReleaseYear());
        } else if (movie.getReleaseYear() > 0) {
            assertEquals("Year should be equal to movie", (int) videoContent.getYear(), movie.getReleaseYear());
        } else{
            assertNull("Year should be null", videoContent.getYear());
        }
        assertEquals("Mal id should be equal", (int)videoContent.getMalId(), animeMovie.getIdMAL());
        assertEquals("Anilist id should be equal", (int)videoContent.getAniListId(), animeMovie.getId());
        assertEquals("Imdb Id should be equal", videoContent.getImdbId(), movie.getImdbId());
        assertEquals("TMDB Id should be equal", (int)videoContent.getTmdbId(), movie.getId());
    }

    @Test
    public void fromTmdbTvToVideoContentTest(){
        VideoContent videoContent = videoContentMapper.fromTmdbTv(tv);

        assertNull("Id should be null", videoContent.getId());
        assertEquals("Poster should be equal", videoContent.getPoster(), tv.getPoster());
        assertEquals("Title should be equal", videoContent.getTitle(), tv.getTitle());
        assertEquals("Category should be mainstream", videoContent.getCategory(), VideoContentCategory.MAINSTREAM);
        assertEquals("Type should be equal", videoContent.getType(), tv.getType());
        if(tv.getReleaseYear() > 0) {
            assertEquals("Year should be equal", (int) videoContent.getYear(), tv.getReleaseYear());
        }else{
            assertNull("Year should be null", videoContent.getYear());
        }
        assertNull("Mal id should be null", videoContent.getMalId());
        assertNull("Anilist id should be null", videoContent.getAniListId());
        assertEquals("IMDB Id should be equal", videoContent.getImdbId(), tv.getImdbId());
        assertEquals("TMDB Id should be equal", (int)videoContent.getTmdbId(), tv.getId());
    }

    @Test
    public void fromAnimeAndTvToVideoContentTest(){
        VideoContent videoContent = videoContentMapper.fromTvModels(animeTv, tv);

        assertNull("Id should be null", videoContent.getId());
        assertEquals("Poster should be equal to tv", videoContent.getPoster(), tv.getPoster());
        assertEquals("Title should be equal to anime", videoContent.getTitle(), animeTv.getTitle());
        assertEquals("Category should be anime", videoContent.getCategory(), VideoContentCategory.ANIME);
        assertEquals("Type should be Tv", videoContent.getType(), VideoContentType.TV);
        if(animeTv.getReleaseYear() > 0) {
            assertEquals("Year should be equal to anime", (int) videoContent.getYear(), animeTv.getReleaseYear());
        } else if (tv.getReleaseYear() > 0) {
            assertEquals("Year should be equal to tv", (int) videoContent.getYear(), tv.getReleaseYear());
        } else{
            assertNull("Year should be null", videoContent.getYear());
        }
        assertEquals("Mal id should be equal", (int)videoContent.getMalId(), animeTv.getIdMAL());
        assertEquals("Anilist id should be equal", (int)videoContent.getAniListId(), animeTv.getId());
        assertEquals("IMDB Id should be equal", videoContent.getImdbId(), tv.getImdbId());
        assertEquals("TMDB Id should be equal", (int)videoContent.getTmdbId(), tv.getId());
    }
}
