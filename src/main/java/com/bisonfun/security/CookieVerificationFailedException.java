package com.bisonfun.security;

public class CookieVerificationFailedException extends RuntimeException{
    public CookieVerificationFailedException(String message){
        super(message);
    }
}
