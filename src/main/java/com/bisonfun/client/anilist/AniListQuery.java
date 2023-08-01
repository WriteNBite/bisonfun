package com.bisonfun.client.anilist;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;

@Getter
public enum AniListQuery {
    /**
     * Query to get logged-in user(viewer)'s name and id from Anilist.co
     **/
    VIEWER("graphql/anilist/viewer.graphql"),
    /**
     * Query to get user's anime lists based on user id, media status and page of list.
     */
    USER_LIST("graphql/anilist/user_list.graphql"),
    /**
     * Query to get anime list based on search query and page of list
     */
    SEARCH("graphql/anilist/search.graphql"),
    /**
     * Query to get anime by id from Anilist.co
     */
    ANIME_BY_ID("graphql/anilist/anime_by_id.graphql"),
    /**
     * Query to get anime by name from Anilist.co
     */
    ANIME_BY_NAME("graphql/anilist/anime_by_name.graphql"),
    /**
     * Query to get trending anime
     */
    ANIME_TRENDING("graphql/anilist/anime_trending.graphql");

    @SneakyThrows
    AniListQuery(String classpath){
        Resource resource = new ClassPathResource(classpath);
        File file = resource.getFile();
        query = new String(Files.readAllBytes(file.toPath()));

    }
    private final String query;

}
