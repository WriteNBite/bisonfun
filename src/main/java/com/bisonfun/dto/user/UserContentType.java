package com.bisonfun.dto.user;

import lombok.Getter;

@Getter
public enum UserContentType {
    ANIME("Anime"),
    MOVIE("Movie"),
    TV("Tv");

    UserContentType(String string) {
        this.string = string;
    }

    private final String string;
}
