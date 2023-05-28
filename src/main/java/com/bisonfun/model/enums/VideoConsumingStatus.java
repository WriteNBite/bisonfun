package com.bisonfun.model.enums;

public enum VideoConsumingStatus {
    PLANNED("Planned", 1),
    WATCHING("Watching", 2),
    COMPLETE("Complete", 3);

    private final String string;
    private final int stage;

    private VideoConsumingStatus(String string, int stage){
        this.string = string;
        this.stage = stage;
    }
    public String getString(){
        return string;
    }

    public int getStage() {
        return stage;
    }
}
