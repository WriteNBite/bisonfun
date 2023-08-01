package com.bisonfun.deserializer;

import com.bisonfun.model.AniAnime;
import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AniAnimeDeserializer implements JsonDeserializer<AniAnime> {
    private JsonObject root;

    @Override
    public AniAnime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new UnsupportedOperationException("JsonElement " + json + " is not JsonObject");
        }
        root = json.getAsJsonObject();
        try {
            return new AniAnime(getId(), true, getType(), getTitle(), getDescription(), getRuntime(), getReleaseDate(), getPosterPath(), getScore(), getGenres(), getStatus(), getLastAired(), getEpisodes(), getIdMal(), getStudios(), getOtherNames(), getRecommendations(context));
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

    private VideoContentType getType() {
        String format = Deserializer.getAsString(root, "format");
        if(format == null){
            return VideoContentType.UNKNOWN;
        }
        return format.equalsIgnoreCase("movie") ? VideoContentType.MOVIE : VideoContentType.TV;
    }

    private String getTitle() throws MissingMemberException {
        JsonElement element = root.get("title");
        if (element == null || element.isJsonNull()) {
            MissingMemberException exception = new MissingMemberException("Title");
            log.error(exception.getMessage());
            throw exception;
        }
        JsonObject jsonTitle = element.getAsJsonObject();
        return jsonTitle.get("english").isJsonNull() ? jsonTitle.get("romaji").getAsString() : jsonTitle.get("english").getAsString();
    }

    private String getDescription() {
        String description = Deserializer.getAsString(root, "description");
        return description == null ? "" : description;
    }

    private int getRuntime() {
        if (!root.has("duration") || !root.get("duration").isJsonPrimitive()) {
            log.debug(new MissingMemberException("Runtime").getMessage());
            return -1;
        }
        JsonElement element = root.get("duration");
        return element.getAsInt();
    }

    private Date getReleaseDate() {
        if (!root.has("startDate") || !root.get("startDate").isJsonObject()) {
            log.debug(new MissingMemberException("Release Date").getMessage());
            return null;
        }
        JsonObject element = root.get("startDate").getAsJsonObject();
        return parseDate(element);
    }

    private String getPosterPath() {
        JsonElement element = root.get("coverImage");
        if (element == null || !element.isJsonObject()) {
            log.debug(new MissingMemberException("Poster Path").getMessage());
            return null;
        }
        if (element.getAsJsonObject().has("extraLarge")) {
            return element.getAsJsonObject().get("extraLarge").getAsString();
        } else if (element.getAsJsonObject().has("large")) {
            return element.getAsJsonObject().get("large").getAsString();
        } else {
            log.warn("There's no proper image\n" + element);
            return null;
        }
    }

    private float getScore() {
        if (!root.has("averageScore") || !root.get("averageScore").isJsonPrimitive()) {
            log.debug(new MissingMemberException("Score").getMessage());
            return 0;
        }
        JsonElement element = root.get("averageScore");
        return element.getAsInt() / 10f;
    }

    private String[] getGenres() {
        JsonArray array = Deserializer.getAsJsonArray(root, "genres");
        List<String> genres = new ArrayList<>();
        for (JsonElement genre : array) {
            genres.add(genre.getAsString());
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
            case "FINISHED":
                status = VideoContentStatus.RELEASED;
                break;
            case "RELEASING":
                status = VideoContentStatus.ONGOING;
                break;
            case "NOT_YET_RELEASED":
                status = VideoContentStatus.UPCOMING;
                break;
            case "HIATUS":
                status = VideoContentStatus.PAUSED;
                break;
            case "CANCELLED":
                status = VideoContentStatus.CANCELED;
                break;
            default:
                status = null;
                break;
        }
        return status;
    }

    private String[] getStudios() {
        JsonElement element = root.get("studios");
        if (element == null || !element.isJsonObject()) {
            log.debug(new MissingMemberException("Studios").getMessage());
            return new String[0];
        }
        JsonArray array = Deserializer.getAsJsonArray(element.getAsJsonObject(), "nodes");
        List<String> studios = new ArrayList<>();
        for (JsonElement studio : array) {
            studios.add(studio.getAsJsonObject().get("name").getAsString());
        }
        return studios.toArray(new String[0]);
    }

    private Date getLastAired() {
        JsonElement element = root.get("endDate");
        if (element == null || !element.isJsonObject()) {
            log.debug(new MissingMemberException("Last Aired Date").getMessage());
            return null;
        }
        JsonObject lastAired = element.getAsJsonObject();
        return parseDate(lastAired);
    }

    private int getEpisodes() {
        JsonElement element = root.get("episodes");
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException("Number of Episodes").getMessage());
            return -1;
        }
        return element.getAsInt();
    }

    private int getIdMal() {
        JsonElement element = root.get("idMal");
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException("MAL Id").getMessage());
            return -1;
        }
        return element.getAsInt();
    }

    private String[] getOtherNames() {
        JsonArray array = Deserializer.getAsJsonArray(root, "synonyms");
        List<String> genres = new ArrayList<>();
        for (JsonElement genre : array) {
            genres.add(genre.getAsString());
        }
        return genres.toArray(new String[0]);
    }

    private VideoEntertainment[] getRecommendations(JsonDeserializationContext context) {
        JsonElement element = root.get("recommendations");
        if (element == null || !element.isJsonObject()) {
            log.debug(new MissingMemberException("Recommendations").getMessage());
            return new VideoEntertainment[0];
        }
        JsonArray array = Deserializer.getAsJsonArray(element.getAsJsonObject(), "nodes");
        List<VideoEntertainment> recs = new ArrayList<>();
        for (JsonElement rec : array) {
            JsonObject recommendation = rec.getAsJsonObject().get("mediaRecommendation").getAsJsonObject();
            recs.add(context.deserialize(recommendation, AniAnime.class));
        }
        return recs.toArray(new VideoEntertainment[0]);
    }

    private Date parseDate(JsonObject jsonDate) {
        if ((!jsonDate.has("year") || jsonDate.get("year").isJsonNull()) || (!jsonDate.has("month") || jsonDate.get("month").isJsonNull()) || (!jsonDate.has("day") || jsonDate.get("day").isJsonNull())) {
            log.warn("Date is null; " + jsonDate);
            return null;
        }
        String date = jsonDate.get("year").getAsString() +
                "-" +
                jsonDate.get("month").getAsString() +
                "-" +
                jsonDate.get("day").getAsString();
        return Date.valueOf(date);
    }
}
