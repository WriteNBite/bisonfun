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
        log.info("Getting user by username");
        User user = userRepo.findByUsername(username);
        log.info("User: "+user);
        return user;
    }

    public User saveUser(User user){
        log.info("Saving\n User: "+user);
        return userRepo.save(user);
    }

    public boolean existUserByUsernameOrEmail(String username, String email){
        log.info("Find user with username: "+username+" and email: "+email);
        boolean exists = userRepo.existsUserByUsernameOrEmail(username, email);
        log.info("Exists: "+exists);
        return exists;
    }
}
