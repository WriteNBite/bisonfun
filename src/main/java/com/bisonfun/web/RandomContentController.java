package com.bisonfun.web;

import com.bisonfun.dto.VideoEntertainment;
import com.bisonfun.dto.enums.VideoConsumingStatus;
import com.bisonfun.dto.enums.VideoContentType;
import com.bisonfun.entity.User;
import com.bisonfun.service.UserAnimeService;
import com.bisonfun.service.UserMovieService;
import com.bisonfun.service.UserService;
import com.bisonfun.service.UserTvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class RandomContentController {
    final
    UserAnimeService userAnimeService;
    final
    UserMovieService userMovieService;
    final
    UserTvService userTvService;
    final
    UserService userService;

    @Autowired
    public RandomContentController(UserAnimeService userAnimeService, UserMovieService userMovieService, UserTvService userTvService, UserService userService) {
        this.userAnimeService = userAnimeService;
        this.userMovieService = userMovieService;
        this.userTvService = userTvService;
        this.userService = userService;
    }

    @GetMapping(value = "/random/video", produces = "application/json")
    public VideoEntertainment getRandomVideoContent(
            @RequestParam String username,
            @RequestParam Optional<String> anime,
            @RequestParam Optional<String> movie,
            @RequestParam Optional<String> tv
    ){

        User user = userService.getUserByUsername(username);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<VideoEntertainment> contentList = new ArrayList<>();

        if(anime.isPresent()){
            Pattern pattern = Pattern.compile("((planned|watching|complete),){1,3}(all|movie|tv)");
            Matcher matcher = pattern.matcher(anime.get());
            if(matcher.matches()){
                String[] arguments = matcher.group().split(",");
                String type = arguments[arguments.length-1];
                if(type.equalsIgnoreCase("all")){
                    for(int i = 0; i < arguments.length-1; i++){
                        contentList.addAll(userAnimeService.getVideoContentListByStatus(user.getId(), VideoConsumingStatus.valueOf(arguments[i].toUpperCase())));
                    }
                }else{
                    for(int i = 0; i < arguments.length-1; i++){
                        contentList.addAll(userAnimeService.getVideoContentListByStatusAndType(user.getId(), VideoConsumingStatus.valueOf(arguments[i].toUpperCase()), VideoContentType.valueOf(type.toUpperCase())));
                    }
                }
            }
        }
        Pattern pattern = Pattern.compile("((planned|watching|complete),){0,2}(planned|watching|complete)");
        if(movie.isPresent() && pattern.matcher(movie.get()).matches()){
            String[] arguments = movie.get().split(",");
            for(String argument : arguments){
                contentList.addAll(userMovieService.getContentListByStatus(user.getId(), VideoConsumingStatus.valueOf(argument.toUpperCase())));
            }
        }
        if(tv.isPresent() && pattern.matcher(tv.get()).matches()){
            String[] arguments = tv.get().split(",");
            for(String argument : arguments){
                contentList.addAll(userTvService.getContentListByStatus(user.getId(), VideoConsumingStatus.valueOf(argument.toUpperCase())));
            }
        }

        if(contentList.isEmpty()){
            return null;
        }

        Random random = new Random();
        VideoEntertainment content = contentList.get(random.nextInt(contentList.size()));

        return content;
    }
}
