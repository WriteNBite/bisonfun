package com.bisonfun.client.anilist;

public enum AnilistQuery {

    /**
     * Query to get logged-in user(viewer)'s name and id from Anilist.co
     **/
    VIEWER(
            "query{\n" +
                    "  Viewer {\n" +
                    "    id,\n" +
                    "    name,\n" +
                    "    \n" +
                    "  }\n" +
                    "}"
    ),
    /**
     * Query to get user's anime lists based on user id, media status and page of list.
     */
    USER_LIST(
            "query($page: Int, $userId:Int, $status:MediaListStatus){\n" +
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
                    "}"
    ),
    /**
     * Query to get anime list based on search query and page of list
     */
    SEARCH(
            "query getAnimeList($query: String, $page: Int){\n" +
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
                    "}"
    ),
    /**
     * Query to get anime by id from Anilist.co
     */
    ANIME_BY_ID(
            "query GetAnimeById($id: Int){\n" +
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
                    "recommendations {\n" +
                    "      nodes {\n" +
                    "        mediaRecommendation {\n" +
                    "          id\n" +
                    "          coverImage {\n" +
                    "            large\n" +
                    "          }\n" +
                    "          title {\n" +
                    "            romaji\n" +
                    "            english\n" +
                    "          }\n" +
                    "          format\n" +
                    "startDate {\n" +
                    "      day\n" +
                    "      month\n" +
                    "      year\n" +
                    "    }" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
    ),
    /**
     * Query to get anime by name from Anilist.co
     */
    ANIME_BY_NAME(
            "query getAnimeByName($name: String!){\n" +
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
                    "recommendations {\n" +
                    "      nodes {\n" +
                    "        mediaRecommendation {\n" +
                    "          id\n" +
                    "          coverImage {\n" +
                    "            large\n" +
                    "          }\n" +
                    "          title {\n" +
                    "            romaji\n" +
                    "            english\n" +
                    "          }\n" +
                    "          format\n" +
                    "startDate {\n" +
                    "      day\n" +
                    "      month\n" +
                    "      year\n" +
                    "    }" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
    ),
    /**
     * Query to get trending anime
     */
    ANIME_TRENDING(
            "query trends{\n" +
                    "  Page(perPage: 25) {\n" +
                    "    pageInfo{\n" +
                    "      perPage\n" +
                    "    }\n" +
                    "    media(type: ANIME, isAdult: false, sort: TRENDING_DESC) {\n" +
                    "      id\n" +
                    "      title {\n" +
                    "        romaji\n" +
                    "        english\n" +
                    "      }\n" +
                    "      coverImage{\n" +
                    "        large\n" +
                    "      }\n" +
                    "      format\n" +
                    "      startDate{\n" +
                    "        day\n" +
                    "        month\n" +
                    "        year\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
    );

    public String query;

    AnilistQuery(String query) {
        this.query = query;
    }
}
