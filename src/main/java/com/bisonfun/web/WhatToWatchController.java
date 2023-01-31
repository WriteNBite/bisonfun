package com.bisonfun.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class WhatToWatchController {

    @GetMapping("/wtw")
    public String getWhatToWatch(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "random_vc";
    }
}
