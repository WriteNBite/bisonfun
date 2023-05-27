package com.bisonfun.dto.enums;

public enum MALRating {
    G("G - All ages"),
    PG("PG - Children"),
    PG13("PG-13 - Teens 13 or older"),
    R17("R - 17+ (violence & profanity)"),
    R("R+ - Mild Nudity"),
    RX("Rx - Hentai"),
    UNKNOWN("Unknown");


    public final String label;

    private MALRating(String label){
        this.label = label;
    }
}
