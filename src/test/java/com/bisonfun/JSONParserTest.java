package com.bisonfun;

import com.bisonfun.utilities.ContentNotFoundException;
import com.bisonfun.utilities.JSONParser;
import com.bisonfun.utilities.TooManyAnimeRequestsException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JSONParserTest {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private JSONParser jsonParser;

    public static final String JSON_ANIME = "jsonAnime";
    public static final String JSON_MOVIE = "jsonMovie";
    public static final String JSON_SHOW = "jsonShow";

    private Optional<JSONObject> getCachedJSONObject(String cacheName, int id){
        return Optional.ofNullable(cacheManager.getCache(cacheName)).map(c -> c.get(id, JSONObject.class));
    }
    private Optional<JSONObject> getCachedJSONObject(String cacheName, String name){
        return Optional.ofNullable(cacheManager.getCache(cacheName)).map(c -> c.get(name, JSONObject.class));
    }

    @Test
    public void animeParseCachingTest(){
        Optional<JSONObject> jsonAnime = Optional.empty();
        try {
            jsonAnime = Optional.ofNullable(jsonParser.getAnimeById(450));
        } catch (TooManyAnimeRequestsException | ContentNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(jsonAnime, getCachedJSONObject(JSON_ANIME, 450));
    }

    @Test
    public void movieParseCachingTest(){
        Optional<JSONObject> jsonMovie = Optional.ofNullable(jsonParser.getMovieById(361743));

        assertEquals(jsonMovie, getCachedJSONObject(JSON_MOVIE, 361743));
    }

    @Test
    public void showParseCachingTest(){
        Optional<JSONObject> jsonShow = Optional.ofNullable(jsonParser.getShowById(76479));

        assertEquals(jsonShow, getCachedJSONObject(JSON_SHOW, 76479));
    }

    @Test
    public void cachingByDifferentValuesSameVariableTest(){
        Optional<JSONObject> jsonAnimeId = Optional.empty();
        Optional<JSONObject> jsonAnimeName = Optional.empty();
        try {
            jsonAnimeId = Optional.ofNullable(jsonParser.getAnimeById(450));
            jsonAnimeName = Optional.ofNullable(jsonParser.getAnimeByName("Naruto"));
            jsonParser.getAnimeById(450);
            jsonParser.getAnimeByName("Naruto");
        } catch (TooManyAnimeRequestsException | ContentNotFoundException e) {
            e.printStackTrace();
        }
        Optional<JSONObject> jsonCachedAnimeId = getCachedJSONObject(JSON_ANIME, 450);
        Optional<JSONObject> jsonCachedAnimeName = getCachedJSONObject(JSON_ANIME, "Naruto");
        assertEquals(jsonAnimeId, jsonCachedAnimeId);
        assertEquals(jsonAnimeName, jsonCachedAnimeName);
    }
}
