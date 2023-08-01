package com.bisonfun.deserializer;

import com.bisonfun.entity.Anime;
import com.bisonfun.entity.UserAnime;
import com.bisonfun.mapper.AnimeMapper;
import com.bisonfun.mapper.VideoConsumingStatusMapper;
import com.bisonfun.model.AniAnime;
import com.bisonfun.model.enums.MediaListStatus;
import com.bisonfun.model.enums.VideoConsumingStatus;
import com.google.gson.*;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Type;

public class UserAnimeDeserializer implements JsonDeserializer<UserAnime> {
    private JsonObject root;
    @Override
    public UserAnime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonObject()){
            throw new UnsupportedOperationException("JsonElement "+json+" is not JsonObject");
        }
        root = json.getAsJsonObject();
        UserAnime userAnime = new UserAnime();
        userAnime.setAnime(getAnime(context));
        userAnime.setEpisodes(getProgress());
        userAnime.setScore(getScore());
        userAnime.setStatus(getStatus());
        return userAnime;
    }

    private int getProgress(){
        return Deserializer.getAsInt(root, "progress");
    }

    private int getScore(){
        return (int) Deserializer.getAsFloat(root, "score");
    }

    private VideoConsumingStatus getStatus(){
        String status = Deserializer.getAsString(root, "status");
        VideoConsumingStatusMapper mapper = Mappers.getMapper(VideoConsumingStatusMapper.class);
        MediaListStatus mediaListStatus = MediaListStatus.valueOf(status);
        return mapper.fromMediaListStatus(mediaListStatus);
    }

    private Anime getAnime(JsonDeserializationContext context){
        JsonObject media = Deserializer.getAsJsonObject(root, "media");
        AniAnime aniAnime = context.deserialize(media, AniAnime.class);
        AnimeMapper mapper = Mappers.getMapper(AnimeMapper.class);
        return mapper.fromModel(aniAnime);
    }
}
