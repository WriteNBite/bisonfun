package com.bisonfun.service;

import com.bisonfun.domain.TMDBTVShow;
import com.bisonfun.entity.Tv;
import com.bisonfun.repository.TvRepository;
import com.bisonfun.utilities.ContentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TvService {
    private final TvRepository tvRepository;

    @Autowired
    public TvService(TvRepository tvRepository) {
        this.tvRepository = tvRepository;
    }

    public Tv findById(int tvId){
        return tvRepository.findById(tvId).orElse(null);
    }

    public Tv addNewMovie(TMDBTVShow apiTv) throws ContentNotFoundException {
        if(apiTv == null){
            throw new ContentNotFoundException();
        }
        Tv dbTv = new Tv(apiTv.getId(), apiTv.getPoster(), apiTv.getTitle(), apiTv.getType(), apiTv.getReleaseYear());
        return tvRepository.save(dbTv);
    }

    public Tv updating(Tv dbTv, TMDBTVShow apiTv){
        if(dbTv.update(apiTv)){
            tvRepository.save(dbTv);
        }
        return dbTv;
    }
}
