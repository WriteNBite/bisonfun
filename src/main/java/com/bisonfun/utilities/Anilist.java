package com.bisonfun.utilities;

public enum Anilist {
    /**
     * Link to access anilist api through GraphQL
     */
    GRAPHQL("https://graphql.anilist.co"),
    /**
     * Link to get anilist user token
     */
    TOKEN("https://anilist.co/api/v2/oauth/token");

    public String link;

    Anilist(String link){
        this.link = link;
    }
}
