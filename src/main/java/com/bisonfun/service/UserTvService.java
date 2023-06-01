package com.bisonfun.service;

import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.bisonfun.entity.Tv;
import com.bisonfun.entity.User;
import com.bisonfun.entity.UserTv;
import com.bisonfun.entity.UserTvKey;
import com.bisonfun.mapper.TvMapper;
import com.bisonfun.repository.UserTvRepository;
import com.bisonfun.client.tmdb.TmdbClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserTvService extends UserVideoContentService {
    final
    UserTvRepository userTvRepo;
    final TmdbClient tmdbClient;
    final TvMapper tvMapper;

    @Autowired
    public UserTvService(UserTvRepository userTvRepo, TmdbClient tmdbClient, TvMapper tvMapper) {
        this.userTvRepo = userTvRepo;
        this.tmdbClient = tmdbClient;
        this.tvMapper = tvMapper;
    }

    public int[] getSizeOfLists(int userId){
        return new int[]{
                getUserTvListByStatus(userId, VideoConsumingStatus.PLANNED).size(),
                getUserTvListByStatus(userId, VideoConsumingStatus.WATCHING).size(),
                getUserTvListByStatus(userId, VideoConsumingStatus.COMPLETE).size()
        };
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
        List<Tv> tvList = getTvListByStatus(userId, status);
        log.info("Tv list: "+tvList);
        return tvList.stream().map(tvMapper::toVideoEntertainment).collect(Collectors.toList());
    }

    public List<UserTv> getUserTvListByStatus(int userId, VideoConsumingStatus status){
        log.info("Get userTv list\nUser id: "+userId+"\nStatus: "+status);
        return userTvRepo.findUserTvByUserIdAndStatus(userId, status);
    }

    public void createUserTv(UserTv userTv, User user, Tv tv){
        UserTvKey userTvKey = new UserTvKey(user.getId(), tv.getId());
        userTv.setId(userTvKey);
        userTv.setUser(user);
        userTv.setTv(tv);

        UserTv dbUserTv = getUserTvById(userTvKey);
        TMDBTVShow tmdbtvShow = tmdbClient.parseShowById(tv.getId());
        if(userTv.getEpisodes() != dbUserTv.getEpisodes()){
            userTv.setStatus(updateStatus(userTv, tmdbtvShow));
        }else if(userTv.getStatus() != dbUserTv.getStatus()){
            userTv.setEpisodes(updateEpisodes(userTv, tmdbtvShow));
        }
        saveUserTv(userTv);
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
         userTvRepo.findById(userTvId).ifPresent(userTvRepo::delete);
    }
}
