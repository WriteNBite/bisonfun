package com.bisonfun.service;

import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.entity.Tv;
import com.bisonfun.mapper.TvMapper;
import com.bisonfun.repository.TvRepository;
import com.bisonfun.client.ContentNotFoundException;
import com.bisonfun.client.tmdb.TmdbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TvService {
    private final TvRepository tvRepository;
    private final TmdbClient tmdbClient;
    private final TvMapper tvMapper;

    @Autowired
    public TvService(TvRepository tvRepository, TmdbClient tmdbClient, TvMapper tvMapper) {
        this.tvRepository = tvRepository;
        this.tmdbClient = tmdbClient;
        this.tvMapper = tvMapper;
    }

    public Tv findById(int tvId){
        return tvRepository.findById(tvId).orElse(null);
    }

    public Tv addNewTv(Tv tv) throws ContentNotFoundException {
        if(tv == null){
            throw new ContentNotFoundException();
        }
        return tvRepository.save(tv);
    }

    public Tv updateTv(int tvId) throws ContentNotFoundException {
        Tv dbTv = findById(tvId);
        TMDBTVShow tmdbtvShow = tmdbClient.parseShowById(tvId);
        if(dbTv == null){
            dbTv = addNewTv(tvMapper.fromModel(tmdbtvShow));
        }else{
            dbTv = updateTv(dbTv, tvMapper.fromModel(tmdbtvShow));
        }
        return dbTv;
    }

    public Tv updateTv(Tv dbTv, Tv apiTv){
        if(dbTv.equals(apiTv)){
            return dbTv;
        }else {
            return tvRepository.save(apiTv);
        }
    }
}
