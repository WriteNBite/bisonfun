package com.bisonfun.service;

import com.bisonfun.dto.VideoEntertainment;
import com.bisonfun.dto.enums.VideoConsumingStatus;
import com.bisonfun.entity.Tv;
import com.bisonfun.entity.UserTv;
import com.bisonfun.entity.UserTvKey;
import com.bisonfun.repository.UserTvRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserTvService {
    final
    UserTvRepository userTvRepo;

    @Autowired
    public UserTvService(UserTvRepository userTvRepo) {
        this.userTvRepo = userTvRepo;
    }

    public List<Tv> getTvListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get tv list\nUser id: "+userId+"\nStatus: "+status);
        List<Tv> tvList = new ArrayList<>();
        for(UserTv userTv : userTvRepo.findUserTvByUserIdAndStatus(userId, status)){
            tvList.add(userTv.getTv());
        }
        log.info("Tv list: "+tvList);
        return tvList;
    }

    public List<VideoEntertainment> getContentListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get tv list\nUser id: "+userId+"\nStatus: "+status);
        List<VideoEntertainment> tvList = new ArrayList<>();
        for(UserTv userTv : userTvRepo.findUserTvByUserIdAndStatus(userId, status)){
            Tv tv = userTv.getTv();
            tvList.add(new VideoEntertainment(tv.getId(), false, tv.getType(), tv.getTitle(), null, tv.getYear() > 0 ? Date.valueOf(tv.getYear()+"-1-1") : null, tv.getPoster()));
        }
        log.info("Tv list: "+tvList);
        return tvList;
    }

    public List<UserTv> getUserTvListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get userTv list\nUser id: "+userId+"\nStatus: "+status);
        return userTvRepo.findUserTvByUserIdAndStatus(userId, status);
    }

    public UserTv getUserTvById(UserTvKey userTvId){
        log.info("Getting userTv by user tv id");
        UserTv userTv = userTvRepo.findById(userTvId).orElse(new UserTv());
        log.info("UserTv: "+userTv);
        return userTv;
    }

    public UserTv getUserTvById(int userId, int tvId){
        log.info("User id: "+userId+"\nTv id: "+tvId);
        return getUserTvById(new UserTvKey(userId, tvId));
    }

    public UserTv saveUserTv(UserTv userTv){
        log.info("Saving \nUserTv: "+userTv);
        return userTvRepo.save(userTv);
    }

    public void deleteTvFromUserList(UserTvKey userTvId){
         log.info("Deleting tv from user list");
         userTvRepo.findById(userTvId).ifPresent(userTv -> userTvRepo.delete(userTv));
    }
}
