package com.bisonfun.dto.enums;

public enum MediaListStatus {
    PLANNING("PLANNING"),
    CURRENT("CURRENT"),
    COMPLETED("COMPLETED");

    private final String string;

    private MediaListStatus(String string){
        this.string = string;
    }
    public String getString(){
        return string;
    }
}
