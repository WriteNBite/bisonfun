package com.bisonfun.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ImageService {
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ImageService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<String> getAuthPageBackground(){
        return new ArrayList<>(Objects.requireNonNull(redisTemplate.opsForSet().members("auth-pages-background")));
    }

    public List<String> getMainPageBackground(){
        return new ArrayList<>(Objects.requireNonNull(redisTemplate.opsForSet().members("main-page-background")));
    }

    public List<String> getIcon(){
        return new ArrayList<>(Objects.requireNonNull(redisTemplate.opsForSet().members("icons")));
    }
}
