package com.bisonfun.deserializer;

public class MissingMemberException extends Exception{
    public MissingMemberException(String member){
        super(member+" of object is missing");
    }
}
