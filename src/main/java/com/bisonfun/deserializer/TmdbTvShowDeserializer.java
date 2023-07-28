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
        if (!json.isJsonObject()) {
            throw new UnsupportedOperationException("JsonElement " + json + " is not JsonObject");
        }
        root = json.getAsJsonObject();
        try {
            return new TMDBTVShow(getId(), getIsAnime(), getTitle(), getDescription(), getRuntime(), getReleaseDate(), getPosterPath(), getScore(), getGenres(), getStatus(), getLastAired(), getEpisodes(), getSeasons(), getNetworks(), getStudios());
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
        JsonElement element = root.get("name");
        if (element == null || !element.isJsonPrimitive()) {
            MissingMemberException exception = new MissingMemberException("Title");
            log.error(exception.getMessage());
            throw exception;
        }
        return element.getAsString();
    }

    private String getDescription() {
        return Deserializer.getAsString(root, "overview");
    }

    private int getRuntime() {
        JsonElement element = root.get("episode_run_time");
        if (element == null || !element.isJsonArray()) {
            log.debug(new MissingMemberException("Runtime").getMessage());
            return -1;
        }
        JsonArray array = element.getAsJsonArray();
        if (array.isEmpty()) {
            log.debug("Runtime is empty; Tv: " + root);
            return -1;
        } else {
            return array.get(array.size() - 1).getAsInt();
        }
    }

    private Date getReleaseDate() {
        String strRelease = Deserializer.getAsString(root, "first_air_date");
        if (strRelease == null) {
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
        List<String> genres = new ArrayList<>();
        for (JsonElement genre : array) {
            genres.add(genre.getAsJsonObject().get("name").getAsString());
        }
        return genres.toArray(new String[0]);
    }

    private VideoContentStatus getStatus() {
        String strStatus = Deserializer.getAsString(root, "status");
        if(strStatus == null){
            strStatus = "";
        }
        VideoContentStatus status;
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
            default:
                status = null;
                break;
        }
        return status;
    }

    private String[] getStudios() {
        JsonArray array = Deserializer.getAsJsonArray(root, "production_companies");
        List<String> studios = new ArrayList<>();
        for (JsonElement studio : array) {
            studios.add(studio.getAsJsonObject().get("name").getAsString());
        }
        return studios.toArray(new String[0]);
    }

    private int getSeasons() {
        JsonElement element = root.get("number_of_seasons");
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException("Number of Seasons").getMessage());
            return -1;
        }
        return element.getAsInt();
    }

    private String[] getNetworks() {
        JsonArray array = Deserializer.getAsJsonArray(root, "networks");
        List<String> networks = new ArrayList<>();
        for (JsonElement network : array) {
            networks.add(network.getAsJsonObject().get("name").getAsString());
        }
        return networks.toArray(new String[0]);
    }

    private Date getLastAired() {
        String strLast = Deserializer.getAsString(root, "last_air_date");
        if(strLast == null){
            strLast = "";
        }
        return strLast.equals("") ? null : Date.valueOf(strLast);
    }

    private int getEpisodes() {
        JsonElement element = root.get("number_of_episodes");
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException("Number of Episodes").getMessage());
            return -1;
        }
        return element.getAsInt();
    }
}
