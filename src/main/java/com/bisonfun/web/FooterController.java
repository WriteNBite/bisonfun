package com.bisonfun.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FooterController {
    @GetMapping("/about")
    public String aboutUs(){
        return "about";
    }
    @GetMapping("/terms")
    public String termsOfUse(){
        return "terms_of_use";
    }
    @GetMapping("/privacy")
    public String privacyPolicy(){
        return "privacy_policy";
    }
}
