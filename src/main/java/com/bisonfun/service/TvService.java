package com.bisonfun.service;

import com.bisonfun.dto.TMDBTVShow;
import com.bisonfun.entity.Tv;
import com.bisonfun.mapper.TvMapper;
import com.bisonfun.repository.TvRepository;
import com.bisonfun.utilities.ContentNotFoundException;
import com.bisonfun.utilities.TMDBParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TvService {
    private final TvRepository tvRepository;
    private final TMDBParser tmdbParser;
    private final TvMapper tvMapper;

    @Autowired
    public TvService(TvRepository tvRepository, TMDBParser tmdbParser, TvMapper tvMapper) {
        this.tvRepository = tvRepository;
        this.tmdbParser = tmdbParser;
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
        TMDBTVShow tmdbtvShow = tmdbParser.parseShowById(tvId);
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
