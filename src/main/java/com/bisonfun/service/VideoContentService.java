package com.bisonfun.service;

import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import com.bisonfun.client.tmdb.TmdbClient;
import com.bisonfun.entity.UserVideoContent;
import com.bisonfun.entity.VideoContent;
import com.bisonfun.mapper.VideoContentMapper;
import com.bisonfun.model.AniAnime;
import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.enums.VideoContentCategory;
import com.bisonfun.model.enums.VideoContentType;
import com.bisonfun.repository.VideoContentRepository;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoContentService {
    private final VideoContentRepository videoContentRepository;
    private final AniListClient aniListClient;
    private final TmdbClient tmdbClient;
    private final VideoContentMapper videoContentMapper;

    @Autowired
    public VideoContentService(VideoContentRepository videoContentRepository, AniListClient aniListClient, TmdbClient tmdbClient, VideoContentMapper videoContentMapper) {
        this.videoContentRepository = videoContentRepository;
        this.aniListClient = aniListClient;
        this.tmdbClient = tmdbClient;
        this.videoContentMapper = videoContentMapper;
    }

    public Optional<VideoContent> updateContent(VideoContent updatedVideoContent){
        Optional<VideoContent> existingVideoContent = getVideoContentByVideoContent(updatedVideoContent);
        if(existingVideoContent.isPresent()){
            VideoContent videoContent = existingVideoContent.get();

            //Update poster if it's not empty String or null
            if(updatedVideoContent.getPoster() != null && !updatedVideoContent.getPoster().trim().isEmpty()){
                videoContent.setPoster(updatedVideoContent.getPoster());
            }
            //Update category if it's more specified (i.e. Mainstream updates to Anime but Anime not updates to Mainstream)
            if(videoContent.getCategory() == VideoContentCategory.MAINSTREAM){
                videoContent.setCategory(updatedVideoContent.getCategory());
            }
            //Update type if old type is Unknown
            if(videoContent.getType() == VideoContentType.UNKNOWN){
                videoContent.setType(updatedVideoContent.getType());
            }
            //Update year if old doesn't have one
            if(videoContent.getYear() <= 0){
                videoContent.setYear(updatedVideoContent.getYear());
            }
            //Update imdb id if old doesn't have one, or it's empty
            if(videoContent.getImdbId() == null || videoContent.getImdbId().trim().isEmpty()){
                videoContent.setImdbId(updatedVideoContent.getImdbId());
            }
            //Update tmdb id if old doesn't have one
            if(videoContent.getTmdbId() == null || videoContent.getTmdbId() <= 0){
                videoContent.setTmdbId(updatedVideoContent.getTmdbId());
            }
            //Update mal id if old doesn't have one
            if(videoContent.getMalId() == null || videoContent.getMalId() <= 0){
                videoContent.setMalId(updatedVideoContent.getMalId());
            }
            //Update anilist id if old doesn't have one
            if(videoContent.getAniListId() == null || videoContent.getAniListId() <= 0){
                videoContent.setAniListId(updatedVideoContent.getAniListId());
            }

            return Optional.of(videoContentRepository.save(videoContent));
        }else{
            return Optional.empty();
        }
    }

    public Optional<VideoContent> getVideoContentByVideoContent(@NonNull VideoContent videoContent){
        if(videoContent.getId() != null){
            return videoContentRepository.findById(videoContent.getId());
        } else if (videoContent.getAniListId() != null) {
            return videoContentRepository.findByAniListId(videoContent.getAniListId());
        } else if (videoContent.getTmdbId() != null) {
            switch (videoContent.getType()){
                case MOVIE: return videoContentRepository.findByTmdbMovieId(videoContent.getTmdbId());
                case TV: return videoContentRepository.findByTmdbTvId(videoContent.getTmdbId());
            }
        }
        return Optional.empty();
    }

    public VideoContent createVideoContent(int aniListId, int tmdbId, VideoContentType type) throws ContentNotFoundException, TooManyAnimeRequestsException {
        if(type == VideoContentType.MOVIE){
            return createMovieVideoContent(aniListId, tmdbId);
        } else if (type == VideoContentType.TV) {
            return createTvVideoContent(aniListId, tmdbId);
        }else {
            return videoContentMapper.fromAniAnime(aniListClient.parseById(aniListId));
        }
    }

    public VideoContent createMovieVideoContent(int aniListId, int tmdbId) throws ContentNotFoundException, TooManyAnimeRequestsException {
        AniAnime apiAnime = aniListClient.parseById(aniListId);
        TMDBMovie tmdbMovie = tmdbClient.parseMovieById(tmdbId);

        return videoContentMapper.fromMovieModels(apiAnime, tmdbMovie);
    }
    public VideoContent createTvVideoContent(int aniListId, int tmdbId) throws ContentNotFoundException, TooManyAnimeRequestsException {
        AniAnime apiAnime = aniListClient.parseById(aniListId);
        TMDBTVShow tv = tmdbClient.parseShowById(tmdbId);

        return videoContentMapper.fromTvModels(apiAnime, tv);
    }

    public void saveVideoContentFromUserVideoContentList(List<UserVideoContent> userVideoContentList){
        for(UserVideoContent userVideoContent : userVideoContentList){
            VideoContent videoContent = userVideoContent.getVideoContent();
            videoContentRepository.findById(videoContent.getId()).ifPresent(dbContent -> videoContent.setId(dbContent.getId()));
            videoContentRepository.save(videoContent);
        }
    }

    public VideoContent addNewVideoContent(VideoContent videoContent) throws ContentNotFoundException {
        if(videoContent == null){
            throw new ContentNotFoundException();
        }
        return videoContentRepository.save(videoContent);
    }

    public VideoContent findByContentId(long contentId){
        return videoContentRepository.findById(contentId).orElse(null);
    }

}
