package com.bisonfun.config;

import com.bisonfun.deserializer.AniAnimeDeserializer;
import com.bisonfun.deserializer.TmdbMovieDeserializer;
import com.bisonfun.deserializer.TmdbTvShowDeserializer;
import com.bisonfun.model.AniAnime;
import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.serializer.VideoEntertainmentSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {
    @Bean
    public Gson gson(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(TMDBMovie.class, new TmdbMovieDeserializer())
                .registerTypeAdapter(TMDBTVShow.class, new TmdbTvShowDeserializer())
                .registerTypeAdapter(AniAnime.class, new AniAnimeDeserializer())
                .registerTypeAdapter(VideoEntertainment.class, new VideoEntertainmentSerializer())
                .create();
    }
}
