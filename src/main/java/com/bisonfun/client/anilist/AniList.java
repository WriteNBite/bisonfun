package com.bisonfun.client.anilist;

public enum AniList {
    /**
     * Link to access anilist api through GraphQL
     */
    GRAPHQL("https://graphql.anilist.co"),
    /**
     * Link to get anilist user token
     */
    TOKEN("https://anilist.co/api/v2/oauth/token");

    public String link;

    AniList(String link){
        this.link = link;
    }
}
