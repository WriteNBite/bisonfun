package com.bisonfun.serializer;

import com.bisonfun.model.VideoEntertainment;
import com.bisonfun.model.enums.VideoContentStatus;
import com.bisonfun.model.enums.VideoContentType;
import com.google.gson.*;

import java.lang.reflect.Type;

public class VideoEntertainmentSerializer implements JsonSerializer<VideoEntertainment> {
    @Override
    public JsonElement serialize(VideoEntertainment src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());

        String type = src.getType() != null ? src.getType().getString() : VideoContentType.UNKNOWN.getString();
        object.addProperty("type", type);

        object.addProperty("title", src.getTitle());
        object.addProperty("description", src.getDescription());
        object.addProperty("runtime", src.getRuntime());
        object.addProperty("releaseDate", String.valueOf(context.serialize(src.getReleaseDate())));
        object.addProperty("poster", src.getPoster());
        object.addProperty("score", src.getScore());

        JsonArray genres = new JsonArray();
        if(src.getGenres() != null){
            for(String genre : src.getGenres()){
                genres.add(genre);
            }
        }
        object.add("genres", genres);

        String status = src.getStatus() != null ? src.getStatus().name() : VideoContentStatus.RUMORED.name();
        object.addProperty("status", status);

        object.addProperty("anime", src.isAnime());
        object.addProperty("releaseYear", src.getReleaseYear());
        object.addProperty("timeToWatch", src.getTimeToWatch());
        return object;
    }
}
