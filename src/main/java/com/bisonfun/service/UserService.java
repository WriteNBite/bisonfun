package com.bisonfun.service;

import com.bisonfun.entity.User;
import com.bisonfun.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    final
    UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User getUserByUsername(String username){
        log.info("Getting User {}", username);
        User user = userRepo.findByUsername(username);
        log.debug("User: "+user);
        return user;
    }

    public User saveUser(User user){
        log.info("Saving User {}", user.getUsername());
        return userRepo.save(user);
    }

    public boolean existUserByUsernameOrEmail(String username, String email){
        log.info("Check existence of User by Username {} and Email {}", username, email);
        boolean exists = userRepo.existsUserByUsernameOrEmail(username, email);
        log.info("Existence of User with Username {} and Email {}: {}", username, email, exists);
        return exists;
    }
}
