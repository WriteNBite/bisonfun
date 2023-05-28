package com.bisonfun.builder;

import com.bisonfun.model.TVShow;

public interface TVContentBuilder extends VideoContentBuilder {
    TVContentBuilder addLastAired();
    TVContentBuilder addEpisodes();

    @Override
    TVShow build();
}
