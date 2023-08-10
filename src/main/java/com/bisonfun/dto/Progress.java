package com.bisonfun.dto;

public interface Progress<K, V extends Number> {
    V getValue();
    K getKey();
}
