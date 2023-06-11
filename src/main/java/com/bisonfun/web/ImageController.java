package com.bisonfun.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@RestController
public class ImageController {
    final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ImageController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/images/icon")
    public String getRandomIcon(){
        Random random = new Random();

        List<String> icons = new ArrayList<>(Objects.requireNonNull(redisTemplate.opsForSet().members("icons")));
        return icons.get(random.nextInt(icons.size()));
    }
}
