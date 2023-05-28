package com.bisonfun.model.enums;

public enum VideoContentType {
    TV("TV"),
    MOVIE("Movie"),
    UNKNOWN("Unknown");

    private final String string;

    private VideoContentType(String string){
        this.string = string;
    }
    public String getString(){
        return string;
    }
}
