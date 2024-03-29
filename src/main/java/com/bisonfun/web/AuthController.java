package com.bisonfun.web;

import com.bisonfun.entity.User;
import com.bisonfun.service.UserService;
import com.bisonfun.service.redis.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.*;

@Controller
@Slf4j
public class AuthController {
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public AuthController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam Optional<Boolean> alreadyExist, ModelMap map){
        Random random = new Random();
        List<String> backgrounds = imageService.getAuthPageBackground();
        map.addAttribute("background", backgrounds.get(random.nextInt(backgrounds.size())));
        map.addAttribute("user", new User());
        map.addAttribute("alreadyExist", alreadyExist.orElse(false));
        return "registration";
    }

    @PostMapping("/register")
    public ModelAndView processRegister(User user, ModelMap modelMap){
        log.warn("Attempt to register {} account with {}", user.getUsername(), user.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if (!userService.existUserByUsernameOrEmail(user.getUsername(), user.getEmail())){
            log.info("{} registered successfully", user.getUsername());
            userService.saveUser(user);
            modelMap.addAttribute("successLogin", true);
            String loginLink = "/login";
            return new ModelAndView("redirect:"+loginLink, modelMap);
        }else{
            log.warn("Account with {} and {} already exist. Registration failed", user.getUsername(), user.getEmail());
            modelMap.addAttribute("alreadyExist", true);
            String registerLink = "/register";
            return new ModelAndView("redirect:"+registerLink, modelMap);
        }

    }

    @GetMapping("/cabinet")
    public String personalCabinet(Principal principal){
        String username = principal.getName();
        log.info("{} enter into his personal page", username);
        String userLink = "/users/"+username;
        return "redirect:"+userLink;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam Optional<Boolean> successLogin, ModelMap map){
        Random random = new Random();
        List<String> background = imageService.getAuthPageBackground();
        map.addAttribute("background", background.get(random.nextInt(background.size())));
        map.addAttribute("successLogin", successLogin.orElse(false));
        return "login";
    }
}
