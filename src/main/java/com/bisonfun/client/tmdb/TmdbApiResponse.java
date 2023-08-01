package com.bisonfun.client.tmdb;

import com.bisonfun.client.NoAccessException;
import com.bisonfun.model.enums.VideoContentType;
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
public class TmdbApiResponse {
    final
    Environment environment;
    @Value("#{environment['bisonfun.tmdb.key']}")
    private String tmdbKey;

    @Autowired
    public TmdbApiResponse(Environment environment) {
        this.environment = environment;
    }

    /**
     * Get movie by id. Cacheable as "jsonMovie".
     * @param id identification number from TheMovieDB database.
     * @return JSONObject with info about movie (id, title, description, etc.).
     */
    @Cacheable("jsonMovie")
    public JSONObject getMovieById(int id){
        log.info("Get Movie: {}", id);
        //Get JSON of movie by id from TMDB
        HttpResponse<String> result = Unirest.get(TMDB.MOVIE.link)
                .routeParam("movie_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .queryString("language", "en-US")
                .asString();

        log.debug(result.getBody());

        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(result.getStatus() == 404){
            log.error("Movie couldn't be found: "+id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return new JSONObject(result.getBody()).put("keywords", getMovieKeywords(id));
    }
    /**
     * Get tv by id. Cacheable as "jsonShow".
     * @param id identification number from TheMovieDB database.
     * @return JSONObject with info about tv (id, title, description, etc.).
     */
    @Cacheable("jsonShow")
    public JSONObject getShowById(int id){
        log.info("Get TV: {}", id);
        // get JSON of tv-show by id from TMDB
        HttpResponse<String> result = Unirest.get(TMDB.TV.link)
                .routeParam("tv_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .queryString("language", "en-US")
                .asString();

        log.debug(result.getBody());

        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(result.getStatus() == 404){
            log.error("TV Show couldn't be found: "+id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return new JSONObject(result.getBody()).put("keywords", getShowKeywords(id));
    }
    /**
     * Get movie keywords by movie id. Cacheable as "movieKeywords".
     * @param id movie identification number from TheMovieDB database.
     * @return JSONArray of keywords connected to movie with specific id.
     */
    @Cacheable("movieKeywords")
    public JSONArray getMovieKeywords(int id){
        log.info("Get Keywords of Movie: {}", id);
        HttpResponse<String> result = Unirest.get(TMDB.KEYWORDS_MOVIE.link)
                .routeParam("movie_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .asString();

        log.debug(result.getBody());

        if(result.getStatus() == 401 || result.getStatus() == 404){
            log.error("Something went wrong, got status {} in getMovieKeywords", result.getStatus());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject root = new JSONObject(result.getBody());
        return root.getJSONArray("keywords");
    }
    /**
     * Get tv keywords by tv id. Cacheable as "tvKeywords".
     * @param id tv identification number from TheMovieDB database.
     * @return JSONArray of keywords connected to tv with specific id.
     */
    @Cacheable("tvKeywords")
    public JSONArray getShowKeywords(int id){
        log.info("Get Keywords of TV Show: {}", id);
        HttpResponse<String> result = Unirest.get(TMDB.KEYWORDS_TV.link)
                .routeParam("tv_id", Integer.toString(id))
                .queryString("api_key", tmdbKey)
                .asString();

        log.debug(result.getBody());

        if(result.getStatus() == 401 || result.getStatus() == 404){
            log.error("Something went wrong, got status {} in getMovieKeywords", result.getStatus());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject root = new JSONObject(result.getBody());
        return root.getJSONArray("results");
    }
    /**
     * Get movie\tv list by search query.
     * @param query string query to find movie\tv.
     * @param contentType (Movie or TV).
     * @param page current page of list.
     * @return JSONObject with page info(current page, etc.) and JSONArray with movie\tv JSONObjects.
     */
    public JSONObject getTMDBList(String query, VideoContentType contentType, int page){
        log.info("Get list of {} by \"{}\" Page: {}", contentType, query, page);
        //get list of content from TMDB
        HttpResponse<String> result = Unirest.get(contentType == VideoContentType.MOVIE ? TMDB.SEARCH_MOVIE.link : TMDB.SEARCH_TV.link)
                .queryString("api_key", tmdbKey)
                .queryString("language", "en-US")
                .queryString("query", query)
                .queryString("page", page)
                .queryString("include_adult", false)
                .asString();

        log.debug(result.getBody());

        if(result.getStatus() == 404){
            log.error("Content couldn't be found: "+query);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }else if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new JSONObject(result.getBody());
    }
    /**
     * Get list of movie trends. Cacheable as "movieTrends".
     * @return JSONObject with page info(current page, etc.) and JSONArray with movie JSONObjects.
     * @throws NoAccessException if app can't access to TheMovieDB API.
     */
    @Cacheable("movieTrends")
    public JSONObject getMovieTrends() throws NoAccessException {
        log.info("Get movie trends");
        HttpResponse<String> result;
        try {
            result = Unirest.get(TMDB.TRENDS_MOVIE.link)
                    .queryString("api_key", tmdbKey)
                    .asString();
        }catch (Exception e){
            log.error("Caught exception {}", e.getMessage());
            throw new NoAccessException("Can't access to TheMovieDB");
        }
        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new NoAccessException("Can't access to TheMovieDB (wrong API key)");
        }
        return new JSONObject(result.getBody());
    }
    /**
     * Get list of tv trends. Cacheable as "tvTrends".
     * @return JSONObject with page info(current page, etc.) and JSONArray with tv JSONObjects.
     * @throws NoAccessException if app can't access to TheMovieDB API.
     */
    @Cacheable("tvTrends")
    public JSONObject getTvTrends() throws NoAccessException {
        log.info("Get tv trends");
        HttpResponse<String> result;
        try {
            result = Unirest.get(TMDB.TRENDS_TV.link)
                    .queryString("api_key", tmdbKey)
                    .asString();
        }catch (Exception e){
            log.error("Caught exception {}", e.getMessage());
            throw new NoAccessException("Can't access to TheMovieDB");
        }
        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new NoAccessException("Can't access to TheMovieDB (wrong API key)");
        }
        return new JSONObject(result.getBody());
    }
    /**
     * Get recommendations based on movie. Cacheable as "movieRec"
     * @param id movie identification number from TheMovieDB API.
     * @return JSONObject with page info (current page, etc.) and JSONArray with movie JSONObject.
     * @throws NoAccessException if app can't access to TheMovieDB API.
     */
    @Cacheable("movieRec")
    public JSONObject getMovieRecommendations(int id) throws NoAccessException {
        log.info("Get movie recommendations by {} movie", id);

        HttpResponse<String> result = Unirest.get(TMDB.RECOMMENDATIONS_MOVIE.link)
                .routeParam("movie_id", String.valueOf(id))
                .queryString("api_key", tmdbKey)
                .asString();

        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new NoAccessException("Can't access to TheMovieDB (wrong API key)");
        }
        return new JSONObject(result.getBody());
    }
    /**
     * Get recommendations based on tv show. Cacheable as "tvRec"
     * @param id tv show identification number from TheMovieDB API.
     * @return JSONObject with page info (current page, etc.) and JSONArray with tv JSONObject.
     * @throws NoAccessException if app can't access to TheMovieDB API.
     */
    @Cacheable("tvRec")
    public JSONObject getTvRecommendations(int id) throws NoAccessException {
        log.info("Get tv recommendations by {} tv show", id);

        HttpResponse<String> result = Unirest.get(TMDB.RECOMMENDATIONS_TV.link)
                .routeParam("tv_id", String.valueOf(id))
                .queryString("api_key", tmdbKey)
                .asString();

        if(result.getStatus() == 401){
            log.error("Invalid API key (TMDB API key)");
            throw new NoAccessException("Can't access to TheMovieDB (wrong API key)");
        }
        return new JSONObject(result.getBody());
    }
}
