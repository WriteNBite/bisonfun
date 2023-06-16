package com.bisonfun.client.anilist;

import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.NoAccessException;
import com.bisonfun.model.enums.MediaListStatus;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class AniListApiResponse {

    final
    Environment environment;

    @Autowired
    public AniListApiResponse(Environment environment) {
        this.environment = environment;
    }

    /**
     * Get anime list by search query.
     * @param search string query to get list.
     * @param page current page of list
     * @return "Page" JSONObject which have page info(number, count, etc.) and JSONArray of anime "Media" JSONObjects.
     * @throws TooManyAnimeRequestsException if Anilist.co have got more requests than limit from app's account.
     * */
    public JSONObject getAnimeList(String search, int page) throws TooManyAnimeRequestsException{
        log.info("Get list of Anime by \"{}\" Page: {}", search, page);

        String variables = "{\n" +
                "  \"query\": \""+search+"\",\n" +
                "  \"page\": "+page+"\n" +
                "}";


        //get anime list from AniList API
        HttpResponse<String> result = Unirest.post(AniList.GRAPHQL.link)
                .queryString("query", AnilistQuery.SEARCH.query)
                .queryString("variables", variables)
                .asString();

        log.debug(result.getBody());

        if(result.getStatus() == 429){
            String secs = result.getHeaders().getFirst("Retry-After");
            int seconds = Integer.parseInt(secs);
            log.warn("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co. will be available after " + seconds + "seconds", seconds);
        }else if(result.getStatus() == 404){
            log.error("Anime weren't found("+search+");");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }else if(result.getStatus() == 400){
            log.error("Something went wrong:\n"+ AniList.GRAPHQL+"\n"+AnilistQuery.ANIME_BY_ID+"\n"+variables);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new JSONObject(result.getBody()).getJSONObject("data").getJSONObject("Page");
    }
    /**
     * Get list of anime trends. Cacheable as "animeTrends".
     * @return "Page" JSONObject which have page info(number, count, etc.) and JSONArray of anime "Media" JSONObjects.
     * @throws TooManyAnimeRequestsException if Anilist.co have got more requests than limit from app's account.
     * @throws NoAccessException if app can't access to Anilist.co API.
     */
    @Cacheable("animeTrends")
    public JSONObject getAnimeTrends() throws TooManyAnimeRequestsException, NoAccessException {
        log.info("Get anime trends");
        HttpResponse<String> result;
        try {
            result = Unirest.post(AniList.GRAPHQL.link)
                    .queryString("query", AnilistQuery.ANIME_TRENDING.query)
                    .asString();
        }catch (Exception e){
            throw new NoAccessException("No Access to Anilist.co");
        }
        log.debug(result.getBody());

        if(result.getStatus() == 429){
            String secs = result.getHeaders().getFirst("Retry-After");
            int seconds = Integer.parseInt(secs);
            log.warn("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co. will be available after " + seconds + "seconds", seconds);
        }else if(result.getStatus() == 400){
            log.error("Something went wrong:\n"+ AniList.GRAPHQL+"\n"+AnilistQuery.ANIME_BY_ID+"\n");
            throw new NoAccessException("No Access to Anilist.co");
        }

        return new JSONObject(result.getBody()).getJSONObject("data").getJSONObject("Page");
    }
    /**
     * Get anime by id. Cacheable as "jsonAnime".
     * @param id identification number from Anilist database.
     * @return "Media" JSONObject with info about anime (id, title, description, etc.).
     * @throws TooManyAnimeRequestsException if Anilist.co have got more requests than limit from app's account.
     * @throws ContentNotFoundException if there's no such anime in database.
     */
    @Cacheable("jsonAnime")
    public JSONObject getAnimeById(int id) throws TooManyAnimeRequestsException, ContentNotFoundException {
        log.info("Get Anime: {}", id);
        String variables = "{\n" +
                "  \"id\": "+id+"\n" +
                "}";

        //get anime from AniList API
        HttpResponse<String> anime = Unirest.post(AniList.GRAPHQL.link)
                .queryString("query", AnilistQuery.ANIME_BY_ID.query)
                .queryString("variables", variables)
                .asString();

        log.debug(anime.getBody());

        if(anime.getStatus() == 429){
            int seconds = Integer.parseInt(anime.getHeaders().getFirst("Retry-After"));
            log.warn("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co, will be available after " + seconds + "seconds", seconds);
        }else if(anime.getStatus() == 404){
            log.error("Anime {} not found", id);
            throw new ContentNotFoundException("Anime #"+id+" not found");
        }else if(anime.getStatus() == 400){
            log.error("Something went wrong:\n"+ AniList.GRAPHQL+"\n"+AnilistQuery.ANIME_BY_ID+"\n"+variables);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new JSONObject(anime.getBody()).getJSONObject("data").getJSONObject("Media");
    }
    /**
     * Get anime by name. Cacheable as "jsonAnime".
     * @param name title from Anilist database
     * @return "Media" JSONObject with info about anime (id, title, description, etc.).
     * @throws TooManyAnimeRequestsException if Anilist.co have got more requests than limit from app's account.
     * @throws ContentNotFoundException if there's no such anime in database.
     */
    @Cacheable("jsonAnime")
    public JSONObject getAnimeByName(String name) throws TooManyAnimeRequestsException, ContentNotFoundException {
        log.info("Get Anime: {}", name);
        String variables = "{\n" +
                "  \"name\": \""+name+"\"\n" +
                "}";

        //get anime from AniList API
        HttpResponse<String> anime = Unirest.post(AniList.GRAPHQL.link)
                .queryString("query", AnilistQuery.ANIME_BY_NAME.query)
                .queryString("variables", variables)
                .asString();

        log.debug(anime.getBody());

        if(anime.getStatus() == 429){
            int seconds = Integer.parseInt(anime.getHeaders().getFirst("Retry-After"));
            log.warn("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co, will be available after " + seconds + "seconds", seconds);
        }else if(anime.getStatus() == 404){
            log.error("Anime \"{}\" not found", name);
            throw new ContentNotFoundException("Anime '"+name+"' not found");
        }else if(anime.getStatus() == 400){
            log.error("Something went wrong:\n"+ AniList.GRAPHQL+"\n"+AnilistQuery.ANIME_BY_NAME+"\n"+variables);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new JSONObject(anime.getBody()).getJSONObject("data").getJSONObject("Media");
    }
    /**
     * Get token from Anilist.co by code.
     * @param code granted from authorised Anilist user.
     * @return JSONObject with token to get user info.
     */
    public JSONObject getAniToken(String code){
        log.info("Get Anilist Token");
        String body = "{" +
                "\"grant_type\":\"authorization_code\","+
                "\"client_id\":\""+environment.getProperty("bisonfun.anilist.client.id")+"\",\n"+
                "\"client_secret\":\""+environment.getProperty("bisonfun.anilist.client.secret")+"\","+
                "\"redirect_uri\":\""+environment.getProperty("bisonfun.anilist.redirect_uri")+"\","+
                "\"code\":\""+code+"\"}";

        HttpResponse<String> response = Unirest.post(AniList.TOKEN.link)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body).asString();
        if(response.getStatus() == 400){
            log.error("Something went wrong: {}", response.getBody());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new JSONObject(response.getBody());
    }
    /**
     * Get Anilist user info by token.
     * @param token granted from Anilist by code from logged Anilist user.
     * @return JSONObject with Anilist user info (id, name).
     */
    public JSONObject getUserByToken(String token){

        log.info("Get Anilist User By Token");

        HttpResponse<String> userResponse = Unirest.post(AniList.GRAPHQL.link)
                .header("Authorization", "Bearer "+token)
                .queryString("query", AnilistQuery.VIEWER.query)
                .asString();
        if(userResponse.getStatus() == 400){
            log.error("Something went wrong: {}", userResponse.getBody());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new JSONObject(userResponse.getBody()).getJSONObject("data");
    }
    /**
     * Get Anilist user lists by user's id.
     * @param userId identification number of Anilist user.
     * @param page page of the user's list
     * @param status status of the user's list
     * @return "Page" JSONObject which have page info(number, count, etc.) and JSONArray of anime "Media" JSONObjects.
     * @throws TooManyAnimeRequestsException if Anilist.co have got more requests than limit from app's account.
     */
    public JSONObject getUserMediaList(int userId, int page, MediaListStatus status) throws TooManyAnimeRequestsException {
        log.info("Get User {} {} anime list. Page:{}", userId, status, page);

        String variables = "{\n" +
                "  \"page\": "+page+",\n" +
                "  \"userId\": "+userId+",\n" +
                "  \"status\": \""+status.getString()+"\"\n" +
                "}";

        //get anime list from AniList API
        HttpResponse<String> result = Unirest.post(AniList.GRAPHQL.link)
                .queryString("query", AnilistQuery.USER_LIST.query)
                .queryString("variables", variables)
                .asString();

        log.debug(result.getBody());

        if(result.getStatus() == 429){
            String secs = result.getHeaders().getFirst("Retry-After");
            int seconds = Integer.parseInt(secs);
            log.warn("delay in {} seconds", seconds);
            throw new TooManyAnimeRequestsException("Too many requests to Anilist.co. will be available after " + seconds + "seconds", seconds);
        }else if(result.getStatus() == 400){
            log.error("Something went wrong: {}", result.getBody());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new JSONObject(result.getBody()).getJSONObject("data").getJSONObject("Page");
    }
}
