package com.bisonfun.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Deserializer {

    public static String getAsString(JsonObject root, String jsonElementName){
        JsonElement element = root.get(jsonElementName);
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException(jsonElementName, root.toString()).getMessage());
            return null;
        }
        return element.getAsString();
    }

    public static JsonArray getAsJsonArray(JsonObject root, String jsonArrayName){
        JsonElement element = root.get(jsonArrayName);
        if (element == null || !element.isJsonArray()) {
            log.debug(new MissingMemberException(jsonArrayName, root.toString()).getMessage());
            return new JsonArray();
        }
        return element.getAsJsonArray();
    }

    public static JsonObject getAsJsonObject(JsonObject root, String jsonObjectName){
        JsonElement element = root.get(jsonObjectName);
        if (element == null || !element.isJsonObject()) {
            log.debug(new MissingMemberException(jsonObjectName, root.toString()).getMessage());
            return new JsonObject();
        }
        return element.getAsJsonObject();
    }

    public static float getAsFloat(JsonObject root, String jsonElementName){
        JsonElement element = root.get(jsonElementName);
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException(jsonElementName, root.toString()).getMessage());
            return 0;
        }
        return element.getAsFloat();
    }
    public static int getAsInt(JsonObject root, String jsonElementName){
        JsonElement element = root.get(jsonElementName);
        if (element == null || !element.isJsonPrimitive()) {
            log.debug(new MissingMemberException(jsonElementName, root.toString()).getMessage());
            return 0;
        }
        return element.getAsInt();
    }
}
