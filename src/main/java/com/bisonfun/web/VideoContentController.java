package com.bisonfun.web;

import com.bisonfun.domain.TVShow;
import com.bisonfun.domain.enums.VideoConsumingStatus;
import com.bisonfun.domain.enums.VideoContentStatus;
import com.bisonfun.entity.UserTvContent;

public abstract class VideoContentController {
    public static VideoConsumingStatus updateStatus(UserTvContent userTvContent, TVShow tvShow){
        if((userTvContent.getEpisodes() > 0 || userTvContent.getStatus() == VideoConsumingStatus.COMPLETE) && userTvContent.getEpisodes() < tvShow.getEpisodes()){//if less watched episodes as it is make it watching
            return VideoConsumingStatus.WATCHING;
        }else if(tvShow.getStatus() == VideoContentStatus.RELEASED && userTvContent.getEpisodes() > 0 && userTvContent.getEpisodes() == tvShow.getEpisodes()){// if it released and all episodes watched then it completed
            return VideoConsumingStatus.COMPLETE;
        }
        return userTvContent.getStatus();
    }

    public static int updateEpisodes(UserTvContent userTvContent, TVShow tvShow){
        if(userTvContent.getStatus() == VideoConsumingStatus.COMPLETE) {//if complete then make all episodes watched
            return tvShow.getEpisodes();
        }else if(userTvContent.getStatus() == VideoConsumingStatus.PLANNED){// if planned then 0 episodes watched
            return 0;
        }
        return userTvContent.getEpisodes();
    }
}
