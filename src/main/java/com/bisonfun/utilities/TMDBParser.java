package com.bisonfun.utilities;

import com.bisonfun.builder.JSONMovieBuilder;
import com.bisonfun.builder.JSONTVBuilder;
import com.bisonfun.domain.TMDBMovie;
import com.bisonfun.domain.TMDBTVShow;
import com.bisonfun.domain.VideoEntertainment;
import com.bisonfun.domain.enums.VideoContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TMDBParser {

    @Autowired
    JSONParser parser;

    public TMDBMovie parseMovieById(int id) {

        JSONObject root = parser.getMovieById(id);

        JSONMovieBuilder movieBuilder = JSONMovieBuilder.getInstance(root, parser);

        return movieBuilder.addId()
                .addTitle()
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

        return tvBuilder.addId()
                .addTitle()
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
            VideoEntertainment movie = movieBuilder.addId()
                    .addTitle()
                    .addDescription()
                    .addReleaseDate()
                    .addPoster().build();
            movieList.add(movie);
        }
        return new Pagination<>(page, count, movieList, lastPage);
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
            VideoEntertainment tv = tvBuilder.addId()
                    .addTitle()
                    .addDescription()
                    .addReleaseDate()
                    .addPoster().build();

            documents.add(tv);
        }
        return new Pagination<>(page, count, documents, lastPage);
    }

}
