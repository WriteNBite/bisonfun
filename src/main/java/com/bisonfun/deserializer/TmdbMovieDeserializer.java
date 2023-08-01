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
        if (!json.isJsonObject()) {
            throw new UnsupportedOperationException("JsonElement " + json + " is not JsonObject");
        }
        root = json.getAsJsonObject();

        try {
            return new TMDBMovie(getId(), getImdbId(), getIsAnime(), getTitle(), getDescription(), getRuntime(), getReleaseDate(), getPosterPath(), getScore(), getGenres(), getStatus(), getStudios());
        } catch (MissingMemberException e) {
            throw new RuntimeException(e);
        }
    }

    private int getId() throws MissingMemberException {
        if (!root.has("id")) {
            MissingMemberException exception = new MissingMemberException("Id");
            log.error(exception.getMessage());
            throw exception;
        }
        return root.get("id").getAsInt();
    }

    private Boolean getIsAnime() {
        JsonArray keywords = Deserializer.getAsJsonArray(root, "keywords");
        for (JsonElement keyword : keywords) {
            if (keyword.getAsJsonObject().get("id").getAsInt() == 210024) {// 210024 - anime keyword
                return true;
            }
        }
        return false;
    }

    private String getTitle() throws MissingMemberException {
        JsonElement element = root.get("title");
        if (element == null || !element.isJsonPrimitive()) {
            MissingMemberException exception = new MissingMemberException("Title");
            log.error(exception.getMessage());
            throw exception;
        }
        return element.getAsString();
    }

    private String getDescription() {
        return Deserializer.getAsString(root ,"overview");
    }

    private int getRuntime() {
        JsonElement element = root.get("runtime");
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException("Runtime").getMessage());
            return -1;
        }
        return element.getAsInt();
    }

    private Date getReleaseDate() {
        String strRelease = Deserializer.getAsString(root, "release_date");
        if(strRelease == null){
            strRelease = "";
        }
        return strRelease.equals("") ? null : Date.valueOf(strRelease);
    }

    private String getPosterPath() {
        return Deserializer.getAsString(root, "poster_path");
    }

    private float getScore() {
        return Math.round(Deserializer.getAsFloat(root, "vote_average") * 10f) / 10f;
    }

    private String[] getGenres() {
        JsonArray array = Deserializer.getAsJsonArray(root, "genres");
        return getAsList(array).toArray(new String[0]);
    }

    private VideoContentStatus getStatus() {
        String strStatus = Deserializer.getAsString(root, "status");
        if(strStatus == null){
            strStatus = "";
        }
        VideoContentStatus status;
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
            default:
                status = null;
                break;
        }
        return status;
    }

    private String getImdbId() {
        return Deserializer.getAsString(root, "imdb_id");
    }

    private String[] getStudios() {
        JsonArray array = Deserializer.getAsJsonArray(root, "production_companies");
        return getAsList(array).toArray(new String[0]);
    }

    private List<String> getAsList(JsonArray jsonArray){
        List<String> list = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            if(element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                list.add(Deserializer.getAsString(jsonObject, "name"));
            }
        }
        return list;
    }
}
