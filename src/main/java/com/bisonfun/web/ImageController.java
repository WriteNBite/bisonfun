package com.bisonfun.web;

import com.bisonfun.service.redis.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
public class ImageController {
    final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/images/icon")
    public String getRandomIcon(){
        Random random = new Random();

        List<String> icons = imageService.getIcon();
        return icons.get(random.nextInt(icons.size()));
    }
}
