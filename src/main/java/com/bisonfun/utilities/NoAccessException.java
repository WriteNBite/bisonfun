package com.bisonfun.utilities;

public class NoAccessException extends Exception{
    public NoAccessException(String errorMessage, Throwable throwable){
        super(errorMessage, throwable);
    }
    public NoAccessException(String errorMessage){
        super(errorMessage);
    }
}
