package com.bisonfun.client.tmdb;

import com.bisonfun.client.NoAccessException;
import com.bisonfun.client.Pagination;
import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoContentType;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TmdbClient {

    final
    TmdbApiResponse parser;
    final Gson gson;

    @Autowired
    public TmdbClient(TmdbApiResponse parser, Gson gson) {
        this.parser = parser;
        this.gson = gson;
    }

    public TMDBMovie parseMovieById(int id) {

        JSONObject root = parser.getMovieById(id);

        return gson.fromJson(String.valueOf(root), TMDBMovie.class);
    }

    public TMDBTVShow parseShowById(int id) {
        JSONObject root = parser.getShowById(id);

        return gson.fromJson(String.valueOf(root), TMDBTVShow.class);

    }

    public Pagination<VideoEntertainment> parseMovieList(String query, int page){
        JSONObject root = parser.getTMDBList(query, VideoContentType.MOVIE, page);
        JSONArray data = root.getJSONArray("results");

        int count = data.length();
        //get last pages
        int lastPage = root.getInt("total_pages");

        List<VideoEntertainment> movieList = new ArrayList<>();
        for(int i = 0; i < count; i++){
            VideoEntertainment movie = gson.fromJson(String.valueOf(data.getJSONObject(i)), TMDBMovie.class);
            movieList.add(movie);
        }
        return new Pagination<>(page, count, movieList, lastPage);
    }
    public List<VideoEntertainment> parseMovieTrends(){
        JSONArray movies;
        try {
            movies = parser.getMovieTrends().getJSONArray("results");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<VideoEntertainment> movieList = new ArrayList<>();
        for(int i = 0; i < movies.length(); i++){
            VideoEntertainment movie = gson.fromJson(movies.getJSONObject(i).toString(), TMDBMovie.class);
            movieList.add(movie);
        }
        return movieList;
    }
    public List<TMDBMovie> parseMovieRecommendations(int id){
        JSONArray movies;
        try {
            movies = parser.getMovieRecommendations(id).getJSONArray("results");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<TMDBMovie> movieList = new ArrayList<>();
        for(int i = 0; i < movies.length(); i++){
            TMDBMovie movie = gson.fromJson(String.valueOf(movies.getJSONObject(i)), TMDBMovie.class);
            movieList.add(movie);
        }
        return movieList;
    }
    public Pagination<VideoEntertainment> parseTVList(String query, int page) {

        JSONObject root = parser.getTMDBList(query, VideoContentType.TV, page);
        JSONArray data = root.getJSONArray("results");

        int count = data.length();
        //get last pages
        int lastPage = root.getInt("total_pages");

        //make list of VideoEntertainments
        List<VideoEntertainment> documents = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            VideoEntertainment tv = gson.fromJson(String.valueOf(data.getJSONObject(i)), TMDBTVShow.class);
            documents.add(tv);
        }
        return new Pagination<>(page, count, documents, lastPage);
    }
    public List<VideoEntertainment> parseTVTrends(){
        JSONArray tvs;
        try {
            tvs = parser.getTvTrends().getJSONArray("results");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<VideoEntertainment> tvList = new ArrayList<>();
        for(int i = 0; i < tvs.length(); i++){
            VideoEntertainment tv = gson.fromJson(String.valueOf(tvs.getJSONObject(i)), TMDBTVShow.class);
            tvList.add(tv);
        }
        return tvList;
    }
    public List<TMDBTVShow> parseTVRecommendations(int id){
        JSONArray tvs;
        try {
            tvs = parser.getTvRecommendations(id).getJSONArray("results");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<TMDBTVShow> tvList = new ArrayList<>();
        for(int i = 0; i < tvs.length(); i++){
            TMDBTVShow tv = gson.fromJson(String.valueOf(tvs.getJSONObject(i)), TMDBTVShow.class);
            tvList.add(tv);
        }
        return tvList;
    }

}
