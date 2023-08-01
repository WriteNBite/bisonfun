package com.bisonfun.model.enums;

import lombok.Getter;

@Getter
public enum VideoConsumingStatus {
    PLANNED("Planned", 1),
    WATCHING("Watching", 2),
    PAUSED("Paused", 3),
    DROPPED("Dropped", 4),
    COMPLETE("Complete", 5);

    private final String string;
    private final int stage;

    VideoConsumingStatus(String string, int stage){
        this.string = string;
        this.stage = stage;
    }

}
