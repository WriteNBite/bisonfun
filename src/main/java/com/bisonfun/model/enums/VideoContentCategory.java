package com.bisonfun.model.enums;

public enum VideoContentCategory {
    MAINSTREAM("Mainstream media"),
    ANIME("Anime");


    private final String string;

    VideoContentCategory(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
