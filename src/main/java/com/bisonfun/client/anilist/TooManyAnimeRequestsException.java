package com.bisonfun.client.anilist;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "Too many requests to Anilist.co")
public class TooManyAnimeRequestsException extends Exception{
    private int seconds;

    public TooManyAnimeRequestsException(String errorMessage, Throwable throwable, int seconds){
        super(errorMessage, throwable);
        this.seconds = seconds;
    }
    public TooManyAnimeRequestsException(String errorMessage, int seconds){
        super(errorMessage);
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
