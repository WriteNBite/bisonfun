package com.bisonfun.service;

import com.bisonfun.dto.AniAnime;
import com.bisonfun.entity.Anime;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.repository.AnimeRepository;
import com.bisonfun.utilities.ContentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimeService {
    private final AnimeRepository animeRepository;

    @Autowired
    public AnimeService(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }

    public Anime findById(int animeId){
        return animeRepository.findById(animeId).orElse(null);
    }

    public Anime addNewAnime(AniAnime apiAnime) throws ContentNotFoundException {
        if (apiAnime == null) {
            throw new ContentNotFoundException();
        }
        Anime dbAnime = new Anime(apiAnime.getId(), apiAnime.getIdMAL(), apiAnime.getPoster(), apiAnime.getTitle(), apiAnime.getType(), apiAnime.getReleaseYear());
        return animeRepository.save(dbAnime);
    }

    public Anime updating(Anime dbAnime, AniAnime apiAnime){
        if(dbAnime.update(apiAnime)){
            animeRepository.save(dbAnime);
        }
        return dbAnime;
    }

    public void saveAnimeFromUserAnimeList(List<UserAnime> userAnimeList){
        for(UserAnime userAnime : userAnimeList){
            Anime anime = userAnime.getAnime();
            animeRepository.findById(anime.getId()).ifPresent(dbAnime -> anime.setId(dbAnime.getId()));
            animeRepository.save(anime);
        }
    }
}
