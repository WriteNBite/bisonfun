package com.bisonfun.service;

import com.bisonfun.model.AniAnime;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.mapper.AnimeMapper;
import com.bisonfun.repository.AnimeRepository;
import com.bisonfun.client.anilist.AniListClient;
import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.anilist.TooManyAnimeRequestsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimeService {
    private final AnimeRepository animeRepository;
    private final AnimeMapper animeMapper;
    private final AniListClient aniListClient;

    @Autowired
    public AnimeService(AnimeRepository animeRepository, AnimeMapper animeMapper, AniListClient aniListClient) {
        this.animeRepository = animeRepository;
        this.animeMapper = animeMapper;
        this.aniListClient = aniListClient;
    }

    public Anime updateAnime(int animeId) throws ContentNotFoundException, TooManyAnimeRequestsException {
        Anime dbAnime = findById(animeId);
        AniAnime apiAnime = aniListClient.parseById(animeId);
        if (dbAnime == null) {
            dbAnime = addNewAnime(animeMapper.fromModel(apiAnime));
        }else{
            dbAnime = updateAnime(dbAnime, animeMapper.fromModel(apiAnime));
        }
        return dbAnime;
    }

    public Anime findById(int animeId){
        return animeRepository.findById(animeId).orElse(null);
    }

    public Anime addNewAnime(Anime anime) throws ContentNotFoundException {
        if (anime == null) {
            throw new ContentNotFoundException();
        }
        return animeRepository.save(anime);
    }

    public Anime updateAnime(Anime dbAnime, Anime apiAnime){
        if(dbAnime.equals(apiAnime)){
            return dbAnime;
        }else {
            return animeRepository.save(apiAnime);
        }
    }

    public void saveAnimeFromUserAnimeList(List<UserAnime> userAnimeList){
        for(UserAnime userAnime : userAnimeList){
            Anime anime = userAnime.getAnime();
            animeRepository.findById(anime.getId()).ifPresent(dbAnime -> anime.setId(dbAnime.getId()));
            animeRepository.save(anime);
        }
    }
}
