package com.bisonfun.web;

import com.bisonfun.utilities.TooManyAnimeRequestsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TooManyRequestsExceptionHandler {

    @ExceptionHandler(TooManyAnimeRequestsException.class)
    public String toManyAnimeRequestHandle(TooManyAnimeRequestsException exception, Model model){
        int seconds = exception.getSeconds();

        model.addAttribute("time", seconds);
        return "error/429";
    }
}
