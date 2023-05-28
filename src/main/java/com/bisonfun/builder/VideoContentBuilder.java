package com.bisonfun.builder;

import com.bisonfun.model.VideoEntertainment;

public interface VideoContentBuilder {
    VideoContentBuilder addId();
    VideoContentBuilder addIsAnime();
    VideoContentBuilder addType();
    VideoContentBuilder addTitle();
    VideoContentBuilder addDescription();
    VideoContentBuilder addRuntime();
    VideoContentBuilder addReleaseDate();
    VideoContentBuilder addPoster();
    VideoContentBuilder addScore();
    VideoContentBuilder addGenres();
    VideoContentBuilder addStatus();
    VideoEntertainment build();
}
