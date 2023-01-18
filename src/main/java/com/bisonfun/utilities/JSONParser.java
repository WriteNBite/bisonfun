package com.bisonfun.utilities;

import com.bisonfun.domain.enums.VideoContentType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class JSONParser {
    @Autowired
    Environment environment;
    @Value("#{environment['bisonfun.tmdb.key']}")
    private String tmdbKey;

    //get anime search list
    public JSONObject getAnimeList(String search, int page) throws TooManyAnimeRequestsException{
        log.info("GetAnimeList "+search+" page "+page);

        String variables = "{\n" +
                "  \"query\": \""+search+"\",\n" +
                "  \"page\": "+page+"\n" +
                "}";


        //get anime list from AniList API
        HttpResponse<String> result = Unirest.post(Anilist.GRAPHQL.link)
                .queryString("query", AnilistQuery.SEARCH.query)
                .queryString("variables", variables)
                .asString();

        log.info(result.getBody());

        if(result.getStatus() == 429){
            String secs = result.getHeaders().getFirst("Retry-After");
            int seconds = Integer.parseInt(secs);
            log.info("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co. will be available after " + seconds + "seconds", seconds);
        }else if(result.getStatus() == 404){
            log.error("Anime weren't found("+search+");");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }else if(result.getStatus() == 400){
            log.error("Something went wrong:\n"+ Anilist.GRAPHQL+"\n"+AnilistQuery.ANIME_BY_ID+"\n"+variables);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

            return new JSONObject(result.getBody()).getJSONObject("data").getJSONObject("Page");
    }
    //get anime by id
    @Cacheable("jsonAnime")
    public JSONObject getAnimeById(int id) throws TooManyAnimeRequestsException, ContentNotFoundException {
        log.info("getAnimeById({})", id);
        String variables = "{\n" +
                "  \"id\": "+id+"\n" +
                "}";

        //get anime from AniList API
        HttpResponse<String> anime = Unirest.post(Anilist.GRAPHQL.link)
                .queryString("query", AnilistQuery.ANIME_BY_ID.query)
                .queryString("variables", variables)
                .asString();

        log.info(anime.getBody());

        if(anime.getStatus() == 429){
            int seconds = Integer.parseInt(anime.getHeaders().getFirst("Retry-After"));
            log.info("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co, will be available after " + seconds + "seconds", seconds);
        }else if(anime.getStatus() == 404){
            throw new ContentNotFoundException("Anime #"+id+" not found");
        }else if(anime.getStatus() == 400){
            log.error("Something went wrong:\n"+ Anilist.GRAPHQL+"\n"+AnilistQuery.ANIME_BY_ID+"\n"+variables);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new JSONObject(anime.getBody()).getJSONObject("data").getJSONObject("Media");
    }
    //get anime by name
    @Cacheable("jsonAnime")
    public JSONObject getAnimeByName(String name) throws TooManyAnimeRequestsException, ContentNotFoundException {
        log.info("getAnimeByName({})", name);
        String variables = "{\n" +
                "  \"name\": \""+name+"\"\n" +
                "}";

        //get anime from AniList API
        HttpResponse<String> anime = Unirest.post(Anilist.GRAPHQL.link)
                .queryString("query", AnilistQuery.ANIME_BY_NAME.query)
                .queryString("variables", variables)
                .asString();

        log.info(anime.getBody());

        if(anime.getStatus() == 429){
            int seconds = Integer.parseInt(anime.getHeaders().getFirst("Retry-After"));
            log.info("delay in {} seconds", seconds);
            // here must be thrown new exception
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co, will be available after " + seconds + "seconds", seconds);
        }else if(anime.getStatus() == 404){
            throw new ContentNotFoundException("Anime '"+name+"' not found");
        }else if(anime.getStatus() == 400){
            log.error("Something went wrong:\n"+ Anilist.GRAPHQL+"\n"+AnilistQuery.ANIME_BY_NAME+"\n"+variables);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new JSONObject(anime.getBody()).getJSONObject("data").getJSONObject("Media");
    }
    //get movie by id
    @Cacheable("jsonMovie")
    public JSONObject getMovieById(int id){
        log.info("getMovieById({})", id);
        //Get JSON of movie by id from TMDB
        HttpResponse<String> result = Unirest.get(TMDB.MOVIE.link)
                .routeParam("movie_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .queryString("language", "en-US")
                .asString();

        log.info(result.getBody());

        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(result.getStatus() == 404){
            log.error("Movie couldn't be found: "+id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return new JSONObject(result.getBody());
    }
    //get show by id
    @Cacheable("jsonShow")
    public JSONObject getShowById(int id){
        log.info("getShowById({})", id);
        // get JSON of tv-show by id from TMDB
        HttpResponse<String> result = Unirest.get(TMDB.TV.link)
                .routeParam("tv_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .queryString("language", "en-US")
                .asString();

        log.info(result.getBody());

        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(result.getStatus() == 404){
            log.error("TV Show couldn't be found: "+id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return new JSONObject(result.getBody());
    }
    //get movie keywords
    @Cacheable("movieKeywords")
    public JSONArray getMovieKeywords(int id){
        log.info("getMovieKeywords({})", id);
        HttpResponse<String> result = Unirest.get(TMDB.KEYWORDS_MOVIE.link)
                .routeParam("movie_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .asString();

        log.info(result.getBody());

        if(result.getStatus() == 401 || result.getStatus() == 404){
            log.info("Something went wrong");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject root = new JSONObject(result.getBody());
        return root.getJSONArray("keywords");
    }
    //get show keywords
    @Cacheable("tvKeywords")
    public JSONArray getShowKeywords(int id){
        log.info("getShowKeywords({})", id);
        HttpResponse<String> result = Unirest.get(TMDB.KEYWORDS_TV.link)
                .routeParam("tv_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .asString();

        log.info(result.getBody());

        if(result.getStatus() == 401 || result.getStatus() == 404){
            log.info("Something went wrong");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject root = new JSONObject(result.getBody());
        return root.getJSONArray("results");
    }
    //get movie list
    public JSONObject getTMDBList(String query, VideoContentType contentType, int page){
        log.info("getTMDBList({}, {}, {})", query, contentType, page);
        //get list of content from TMDB
        HttpResponse<String> result = Unirest.get(contentType == VideoContentType.MOVIE ? TMDB.SEARCH_MOVIE.link : TMDB.SEARCH_TV.link)
                .queryString("api_key", tmdbKey)
                .queryString("language", "en-US")
                .queryString("query", query)
                .queryString("page", page)
                .queryString("include_adult", false)
                .asString();

        log.info(result.getBody());

        if(result.getStatus() == 404){
            log.error("Content couldn't be found: "+query);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }else if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new JSONObject(result.getBody());
    }
    //get Anilist token
    public JSONObject getAniToken(String code){
        log.info("get Anilist Token");
        String body = "{" +
                "\"grant_type\":\"authorization_code\","+
                "\"client_id\":\""+environment.getProperty("bisonfun.anilist.client.id")+"\",\n"+
                "\"client_secret\":\""+environment.getProperty("bisonfun.anilist.client.secret")+"\","+
                "\"redirect_uri\":\""+environment.getProperty("bisonfun.anilist.redirect_uri")+"\","+
                "\"code\":\""+code+"\"}";

        HttpResponse<String> response = Unirest.post(Anilist.TOKEN.link)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body).asString();
        if(response.getStatus() == 400){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new JSONObject(response.getBody());
    }
    //get Anilist user
    public JSONObject getUserByToken(String token){

        log.info("get Anilist User By Token");

        HttpResponse<String> userResponse = Unirest.post(Anilist.GRAPHQL.link)
                .header("Authorization", "Bearer "+token)
                .queryString("query", AnilistQuery.VIEWER.query)
                .asString();
        if(userResponse.getStatus() == 400){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new JSONObject(userResponse.getBody()).getJSONObject("data");
    }

    //get user's mediaList
    public JSONObject getUserMediaList(int userId, int page, String status) throws TooManyAnimeRequestsException {
        log.info("get user's anime list({}, {}, {})", userId, page, status);

        String variables = "{\n" +
                "  \"page\": "+page+",\n" +
                "  \"userId\": "+userId+",\n" +
                "  \"status\": \""+status+"\"\n" +
                "}";

        //get anime list from AniList API
        HttpResponse<String> result = Unirest.post(Anilist.GRAPHQL.link)
                .queryString("query", AnilistQuery.USER_LIST.query)
                .queryString("variables", variables)
                .asString();

        log.info(result.getBody());

        if(result.getStatus() == 429){
            String secs = result.getHeaders().getFirst("Retry-After");
            int seconds = Integer.parseInt(secs);
            log.info("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co. will be available after " + seconds + "seconds", seconds);
        }else if(result.getStatus() == 400){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new JSONObject(result.getBody()).getJSONObject("data").getJSONObject("Page");
    }
}
