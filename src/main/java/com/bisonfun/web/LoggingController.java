package com.bisonfun.web;

import com.bisonfun.entity.User;
import com.bisonfun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Optional;

@Controller
public class LoggingController {
    private final UserService userService;

    @Autowired
    public LoggingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam Optional<Boolean> alreadyExist, ModelMap map){
        map.addAttribute("user", new User());
        map.addAttribute("alreadyExist", alreadyExist.orElse(false));
        return "registration";
    }

    @PostMapping("/register")
    public ModelAndView processRegister(User user, ModelMap modelMap){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if (!userService.existUserByUsernameOrEmail(user.getUsername(), user.getEmail())){
            userService.saveUser(user);
            modelMap.addAttribute("successLogin", true);
            String loginLink = "/login";
            return new ModelAndView("redirect:"+loginLink, modelMap);
        }else{
            modelMap.addAttribute("alreadyExist", true);
            String registerLink = "/register";
            return new ModelAndView("redirect:"+registerLink, modelMap);
        }

    }

    @GetMapping("/cabinet")
    public String personalCabinet(Principal principal){
        String username = principal.getName();
        String userLink = "/users/"+username;
        return "redirect:"+userLink;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam Optional<Boolean> successLogin, ModelMap map){
        map.addAttribute("successLogin", successLogin.orElse(false));
        return "login";
    }
}
