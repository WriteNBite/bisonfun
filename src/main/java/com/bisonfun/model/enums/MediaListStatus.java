package com.bisonfun.model.enums;

import lombok.Getter;

@Getter
public enum MediaListStatus {
    PLANNING("PLANNING"),
    CURRENT("CURRENT"),
    COMPLETED("COMPLETED"),
    PAUSED("PAUSED"),
    DROPPED("DROPPED");

    private final String string;

    MediaListStatus(String string){
        this.string = string;
    }
}
