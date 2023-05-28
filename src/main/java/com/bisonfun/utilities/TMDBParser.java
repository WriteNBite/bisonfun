package com.bisonfun.utilities;

import com.bisonfun.builder.JSONMovieBuilder;
import com.bisonfun.builder.JSONTVBuilder;
import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoContentType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TMDBParser {

    final
    JSONParser parser;

    @Autowired
    public TMDBParser(JSONParser parser) {
        this.parser = parser;
    }

    public TMDBMovie parseMovieById(int id) {

        JSONObject root = parser.getMovieById(id);

        JSONMovieBuilder movieBuilder = JSONMovieBuilder.getInstance(root, parser);

        return movieBuilder
                .addDescription()
                .addRuntime()
                .addReleaseDate()
                .addPoster()
                .addScore()
                .addGenres()
                .addStatus()
                .addIsAnime()
                .addStudios()
                .addIMDBId().build();
    }

    public TMDBTVShow parseShowById(int id) {
        JSONObject root = parser.getShowById(id);

        JSONTVBuilder tvBuilder = JSONTVBuilder.getInstance(root, parser);

        return tvBuilder
                .addDescription()
                .addRuntime()
                .addReleaseDate()
                .addPoster()
                .addScore()
                .addGenres()
                .addStatus()
                .addIsAnime()
                .addStudios()
                .addNetworks()
                .addSeasons()
                .addLastAired()
                .addEpisodes().build();

    }

    public Pagination<VideoEntertainment> parseMovieList(String query, int page){
        JSONObject root = parser.getTMDBList(query, VideoContentType.MOVIE, page);
        JSONArray data = root.getJSONArray("results");

        int count = data.length();
        //get last pages
        int lastPage = root.getInt("total_pages");

        List<VideoEntertainment> movieList = new ArrayList<>();
        for(int i = 0; i < count; i++){
            JSONMovieBuilder movieBuilder = JSONMovieBuilder.getInstance(data.getJSONObject(i), parser);
            VideoEntertainment movie = movieBuilder
                    .addDescription()
                    .addReleaseDate()
                    .addPoster().build();
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
            JSONMovieBuilder movieBuilder = JSONMovieBuilder.getInstance(movies.getJSONObject(i), parser);
            VideoEntertainment movie = movieBuilder
                    .addDescription()
                    .addReleaseDate()
                    .addPoster().build();
            movieList.add(movie);
        }
        return movieList;
    }
    public List<VideoEntertainment> parseMovieRecommendations(int id){
        JSONArray movies;
        try {
            movies = parser.getMovieRecommendations(id).getJSONArray("results");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<VideoEntertainment> movieList = new ArrayList<>();
        for(int i = 0; i < movies.length(); i++){
            JSONMovieBuilder movieBuilder = JSONMovieBuilder.getInstance(movies.getJSONObject(i), parser);
            VideoEntertainment movie = movieBuilder
                    .addDescription()
                    .addReleaseDate()
                    .addPoster(200).build();
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
            JSONTVBuilder tvBuilder = JSONTVBuilder.getInstance(data.getJSONObject(i), parser);
            VideoEntertainment tv = tvBuilder
                    .addDescription()
                    .addReleaseDate()
                    .addPoster().build();

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
            JSONTVBuilder tvBuilder = JSONTVBuilder.getInstance(tvs.getJSONObject(i), parser);
            VideoEntertainment tv = tvBuilder
                    .addDescription()
                    .addReleaseDate()
                    .addPoster().build();
            tvList.add(tv);
        }
        return tvList;
    }
    public List<VideoEntertainment> parseTVRecommendations(int id){
        JSONArray tvs;
        try {
            tvs = parser.getTvRecommendations(id).getJSONArray("results");
        } catch (NoAccessException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        List<VideoEntertainment> tvList = new ArrayList<>();
        for(int i = 0; i < tvs.length(); i++){
            JSONTVBuilder tvBuilder = JSONTVBuilder.getInstance(tvs.getJSONObject(i), parser);
            VideoEntertainment tv = tvBuilder
                    .addDescription()
                    .addReleaseDate()
                    .addPoster(200).build();
            tvList.add(tv);
        }
        return tvList;
    }

}
