package com.bisonfun.deserializer;

public class MissingMemberException extends Exception{
    public MissingMemberException(String member){
        super(member+" of object is missing");
    }

    public MissingMemberException(String member, String object){
        super(member+" is missing in object: "+object);
    }
}
