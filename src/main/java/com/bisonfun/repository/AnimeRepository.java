package com.bisonfun.repository;

import com.bisonfun.entity.Anime;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimeRepository extends CrudRepository<Anime, Integer> {
}
