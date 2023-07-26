package com.bisonfun.deserializer;

import com.bisonfun.model.TMDBTVShow;
import com.bisonfun.model.enums.VideoContentStatus;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TmdbTvShowDeserializer implements JsonDeserializer<TMDBTVShow> {
    private JsonObject root;
    @Override
    public TMDBTVShow deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonObject()){
            throw new UnsupportedOperationException("JsonElement "+json+" is not JsonObject");
        }
        root = json.getAsJsonObject();
        try {
            return new TMDBTVShow(getId(), getIsAnime(), getTitle(), getDescription(), getRuntime(), getReleaseDate(), getPosterPath(), getScore(), getGenres(), getStatus(), getLastAired(), getEpisodes(), getSeasons(), getNetworks(), getStudios());
        } catch (MissingMemberException e) {
            throw new RuntimeException(e);
        }
    }

    private int getId() throws MissingMemberException {
        if(!root.has("id")){
            MissingMemberException exception = new MissingMemberException("Id");
            log.error(exception.getMessage());
            throw exception;
        }
        return root.get("id").getAsInt();
    }

    private Boolean getIsAnime(){
        if(!root.has("keywords")){
            log.warn(new MissingMemberException("Keywords").getMessage());
            return false;
        }
        JsonArray keywords = root.getAsJsonArray("keywords");
        for(JsonElement keyword : keywords){
            if(keyword.getAsJsonObject().get("id").getAsInt() == 210024){// 210024 - anime keyword
                return true;
            }
        }
        return false;
    }

    private String getTitle() throws MissingMemberException {
        if(!root.has("name")){
            MissingMemberException exception = new MissingMemberException("Title");
            log.error(exception.getMessage());
            throw exception;
        }
        JsonElement element = root.get("name");
        if(!element.isJsonPrimitive()){
            throw new UnsupportedOperationException("Title has wrong type");
        }
        return element.getAsString();
    }

    private String getDescription(){
        if(!root.has("overview")){
            log.warn(new MissingMemberException("Overview (Description)").getMessage());
            return "";
        }
        JsonElement element = root.get("overview");
        if(!element.isJsonPrimitive()){
            throw new UnsupportedOperationException("The overview is not primitive");
        }
        return element.getAsString();
    }

    private int getRuntime(){
        if(!root.has("episode_run_time")){
            log.warn(new MissingMemberException("Runtime").getMessage());
            return -1;
        }
        JsonArray element = root.get("episode_run_time").getAsJsonArray();
        if(element.isJsonNull() || element.isEmpty()){
            log.warn("Runtime is null or empty; Tv: "+root);
            return -1;
        }else{
            return element.get(element.size()-1).getAsInt();
        }
    }

    private Date getReleaseDate(){
        if(!root.has("first_air_date")){
            log.warn(new MissingMemberException("Release Date").getMessage());
            return null;
        }
        JsonElement element = root.get("first_air_date");
        if(element.isJsonNull()){
            return null;
        }
        String strRelease = element.getAsString();
        return strRelease.equals("") ? null : Date.valueOf(strRelease);
    }

    private String getPosterPath(){
        if(!root.has("poster_path")){
            log.warn(new MissingMemberException("Poster Path").getMessage());
            return null;
        }
        JsonElement element = root.get("poster_path");
        if(element.isJsonNull()){
            return null;
        }
        return element.getAsString();
    }

    private float getScore(){
        if(!root.has("vote_average")){
            log.warn(new MissingMemberException("Score").getMessage());
            return 0;
        }
        JsonElement element = root.get("vote_average");
        if(element.isJsonNull()){
            return 0;
        }else{
            return Math.round(element.getAsFloat()*10f)/10f;
        }
    }

    private String[] getGenres(){
        if(!root.has("genres")){
            log.warn(new MissingMemberException("Genres").getMessage());
            return new String[0];
        }
        JsonElement element = root.get("genres");
        if(element.isJsonNull()){
            return new String[0];
        }else{
            JsonArray array = element.getAsJsonArray();
            List<String> genres = new ArrayList<>();
            for(JsonElement genre : array){
                genres.add(genre.getAsJsonObject().get("name").getAsString());
            }
            return genres.toArray(new String[0]);
        }
    }

    private VideoContentStatus getStatus(){
        if(!root.has("status")){
            MissingMemberException exception = new MissingMemberException("Status");
            log.warn(exception.getMessage());
            return null;
        }
        String strStatus = root.get("status").getAsString();
        VideoContentStatus status = null;
        switch (strStatus) {
            case "Ended":
                status = VideoContentStatus.RELEASED;
                break;
            case "Returning Series":
                status = VideoContentStatus.ONGOING;
                break;
            case "In Production":
            case "Post Production":
                status = VideoContentStatus.UPCOMING;
                break;
            case "Rumored":
                status = VideoContentStatus.RUMORED;
                break;
            case "Planned":
                status = VideoContentStatus.PLANNED;
                break;
            case "Canceled":
                status = VideoContentStatus.CANCELED;
                break;
        }
        return status;
    }

    private String[] getStudios(){
        if(!root.has("production_companies")){
            log.warn(new MissingMemberException("Production Companies (Studios)").getMessage());
            return new String[0];
        }
        JsonElement element = root.get("production_companies");
        if(element.isJsonNull()){
            return new String[0];
        }
        JsonArray array = element.getAsJsonArray();
        List<String> studios = new ArrayList<>();
        for(JsonElement studio : array){
            studios.add(studio.getAsJsonObject().get("name").getAsString());
        }
        return studios.toArray(new String[0]);
    }

    private int getSeasons(){
        if(!root.has("number_of_seasons")){
            log.warn(new MissingMemberException("Number of Seasons").getMessage());
            return -1;
        }
        JsonElement element = root.get("number_of_seasons");
        if(element.isJsonNull()){
            return -1;
        }else{
            return element.getAsInt();
        }
    }

    private String[] getNetworks(){
        if(!root.has("networks")){
            log.warn(new MissingMemberException("Networks").getMessage());
            return new String[0];
        }
        JsonElement element = root.get("networks");
        if(element.isJsonNull()){
            return new String[0];
        }
        JsonArray array = element.getAsJsonArray();
        List<String> networks = new ArrayList<>();
        for(JsonElement network : array){
            networks.add(network.getAsJsonObject().get("name").getAsString());
        }
        return networks.toArray(new String[0]);
    }

    private Date getLastAired(){
        if(!root.has("last_air_date")){
            log.warn(new MissingMemberException("Last Aired Date").getMessage());
            return null;
        }
        JsonElement element = root.get("last_air_date");
        if(element.isJsonNull()){
            return null;
        }
        String strLast = element.getAsString();
        return strLast.equals("") ? null : Date.valueOf(strLast);
    }

    private int getEpisodes(){
        if(!root.has("number_of_episodes")){
            log.warn(new MissingMemberException("Number of Episodes").getMessage());
            return -1;
        }
        JsonElement element = root.get("number_of_episodes");
        if(element.isJsonNull()){
            return -1;
        }else{
            return element.getAsInt();
        }
    }
}
