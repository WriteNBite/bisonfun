package com.bisonfun.utilities;

public class Statics {

    public static final String ANILIST_GRAPHQL = "https://graphql.anilist.co";
    public static final String ANILIST_TOKEN = "https://anilist.co/api/v2/oauth/token";
    public static final String JIKAN_ANIME_SEARCH = "https://api.jikan.moe/v4/anime";
    public static final String TMDB_SEARCH_TV = "https://api.themoviedb.org/3/search/tv";
    public static final String TMDB_SEARCH_MOVIE = "https://api.themoviedb.org/3/search/movie";
    public static final String TMDB_MOVIE = "https://api.themoviedb.org/3/movie/{movie_id}";
    public static final String TMDB_MOVIE_KEYWORDS = "https://api.themoviedb.org/3/movie/{movie_id}/keywords";
    public static final String TMDB_TV = "https://api.themoviedb.org/3/tv/{tv_id}";
    public static final String TMDB_TV_KEYWORDS = "https://api.themoviedb.org/3/tv/{tv_id}/keywords";
    public static final String TMDB_IMAGE = "https://image.tmdb.org/t/p/w500";
    public static final String NO_IMAGE = "https://img.freepik.com/free-vector/flat-design-no-photo-sign-design_23-2149289006.jpg";

    public static final String ANILIST_QUERY_VIEWER = "query{\n" +
            "  Viewer {\n" +
            "    id,\n" +
            "    name,\n" +
            "    \n" +
            "  }\n" +
            "}";

    public static final String ANILIST_QUERY_USER_LIST = "query($page: Int, $userId:Int, $status:MediaListStatus){\n" +
            "  Page(page:$page){\n" +
            "    pageInfo{\n" +
            "      currentPage\n" +
            "      hasNextPage\n" +
            "    }\n" +
            "    mediaList(userId:$userId, type: ANIME, status: $status){\n" +
            "      progress\n" +
            "      score(format:POINT_10)\n" +
            "      user{\n" +
            "        name\n" +
            "        id\n" +
            "      }\n" +
            "      media {\n" +
            "        id\n" +
            "        idMal\n" +
            "        title{\n" +
            "          english\n" +
            "          romaji\n" +
            "        }\n" +
            "        coverImage{\n" +
            "          large\n" +
            "        }\n" +
            "        format\n" +
            "        startDate{\n" +
            "          day\n" +
            "          month\n" +
            "          year\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String ANILIST_QUERY_SEARCH = "query getAnimeList($query: String, $page: Int){\n" +
            "  Page(page: $page, perPage: 20){\n" +
            "    pageInfo{\n" +
            "      perPage\n" +
            "      currentPage\n" +
            "      lastPage\n" +
            "      hasNextPage\n" +
            "    }\n" +
            "    media(search: $query, type: ANIME, isAdult: false){\n" +
            "      id\n" +
            "      idMal\n" +
            "      coverImage{\n" +
            "        large\n" +
            "      }\n" +
            "      title{\n" +
            "        romaji\n" +
            "        english\n" +
            "      }\n" +
            "      format\n" +
            "      startDate{\n" +
            "        day\n" +
            "        month\n" +
            "        year\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String ANILIST_QUERY_ANIME_ID = "query GetAnimeById($id: Int){\n" +
            "  Media(id: $id, type: ANIME){\n" +
            "    id\n" +
            "    idMal\n" +
            "    coverImage{\n" +
            "      extraLarge\n" +
            "    }\n" +
            "    title{\n" +
            "      romaji\n" +
            "      english\n" +
            "    }\n" +
            "    averageScore\n" +
            "    format\n" +
            "    episodes\n" +
            "    duration\n" +
            "    status\n" +
            "    startDate{\n" +
            "      day\n" +
            "      month\n" +
            "      year\n" +
            "    }\n" +
            "    endDate{\n" +
            "      day\n" +
            "      month\n" +
            "      year\n" +
            "    }\n" +
            "    genres\n" +
            "    studios{\n" +
            "      nodes{\n" +
            "        name\n" +
            "      }\n" +
            "    }\n" +
            "    synonyms\n" +
            "    description\n" +
            "  }\n" +
            "}";

    public static final String ANILIST_QUERY_ANIME_NAME = "query getAnimeByName($name: String!){\n" +
            "  Media(search: $name, type: ANIME){\n" +
            "    id\n" +
            "    idMal\n" +
            "    coverImage{\n" +
            "      extraLarge\n" +
            "    }\n" +
            "    title{\n" +
            "      romaji\n" +
            "      english\n" +
            "    }\n" +
            "    averageScore\n" +
            "    format\n" +
            "    episodes\n" +
            "    duration\n" +
            "    status\n" +
            "    startDate{\n" +
            "      day\n" +
            "      month\n" +
            "      year\n" +
            "    }\n" +
            "    endDate{\n" +
            "      day\n" +
            "      month\n" +
            "      year\n" +
            "    }\n" +
            "    genres\n" +
            "    studios{\n" +
            "      nodes{\n" +
            "        name\n" +
            "      }\n" +
            "    }\n" +
            "    synonyms\n" +
            "    description\n" +
            "  }\n" +
            "}";
}
