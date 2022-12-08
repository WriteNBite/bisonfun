package com.bisonfun.builder;

import com.bisonfun.domain.TVShow;

public interface TVContentBuilder extends VideoContentBuilder {
    TVContentBuilder addLastAired();
    TVContentBuilder addEpisodes();

    @Override
    TVShow build();
}
