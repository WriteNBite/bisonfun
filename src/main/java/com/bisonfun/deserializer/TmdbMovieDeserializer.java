package com.bisonfun.deserializer;

import com.bisonfun.model.TMDBMovie;
import com.bisonfun.model.enums.VideoContentStatus;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TmdbMovieDeserializer implements JsonDeserializer<TMDBMovie> {

    private JsonObject root;

    @Override
    public TMDBMovie deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonObject()){
            throw new UnsupportedOperationException("JsonElement "+json+" is not JsonObject");
        }
        root = json.getAsJsonObject();

        try {
            return new TMDBMovie(getId(), getImdbId(), getIsAnime(), getTitle(), getDescription(), getRuntime(), getReleaseDate(), getPosterPath(), getScore(), getGenres(), getStatus(), getStudios());
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
            log.error(new MissingMemberException("Keywords").getMessage());
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
        if(!root.has("title")){
            MissingMemberException exception = new MissingMemberException("Title");
            log.error(exception.getMessage());
            throw exception;
        }
        JsonElement element = root.get("title");
        if(!element.isJsonPrimitive()){
            throw new UnsupportedOperationException("Title has wrong type");
        }
        return element.getAsString();
    }

    private String getDescription(){
        if(!root.has("overview")){
            log.error(new MissingMemberException("Overview (Description)").getMessage());
            return "";
        }
        JsonElement element = root.get("overview");
        if(!element.isJsonPrimitive()){
            throw new UnsupportedOperationException("The overview is not primitive");
        }
        return element.getAsString();
    }

    private int getRuntime(){
        if(!root.has("runtime")){
            log.error(new MissingMemberException("Runtime").getMessage());
            return -1;
        }
        JsonElement element = root.get("runtime");
        if(element.isJsonNull()){
            return -1;
        }else{
            return element.getAsInt();
        }
    }

    private Date getReleaseDate(){
        if(!root.has("release_date")){
            log.error(new MissingMemberException("Release Date").getMessage());
            return null;
        }
        JsonElement element = root.get("release_date");
        if(element.isJsonNull()){
            return null;
        }
        String strRelease = element.getAsString();
        return strRelease.equals("") ? null : Date.valueOf(strRelease);
    }

    private String getPosterPath(){
        if(!root.has("poster_path")){
            log.error(new MissingMemberException("Poster Path").getMessage());
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
            log.error(new MissingMemberException("Score").getMessage());
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
            log.error(new MissingMemberException("Genres").getMessage());
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
            log.error(exception.getMessage());
            return null;
        }
        String strStatus = root.get("status").getAsString();
        VideoContentStatus status = null;
        switch (strStatus) {
            case "Released":
                status = VideoContentStatus.RELEASED;
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

    public String getImdbId(){
        if(!root.has("imdb_id")){
            log.error(new MissingMemberException("IMDB ID").getMessage());
            return null;
        }
        JsonElement element = root.get("imdb_id");
        if(element.isJsonNull()){
            log.warn("There's no imdb id; Movie: "+root);
            return null;
        }else{
            return element.getAsString();
        }
    }

    public String[] getStudios(){
        if(!root.has("production_companies")){
            log.error(new MissingMemberException("Production Companies (Studios)").getMessage());
            return new String[0];
        }
        JsonElement element =  root.get("production_companies");
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
}
