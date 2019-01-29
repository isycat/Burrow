package com.isycat.burrow.serialization;

import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StrictSerializer {
    private static Gson CLEAN_GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeHierarchyAdapter(HasMetadata.class, new MetadataSerializer())
            .create();

    public static <ObjectType> String serialize(final ObjectType object) {
        return GSON.toJson(object, object.getClass());
    }

    public static <ObjectType> ObjectType deserialize(final String jsonString, final Class<ObjectType> clazz) {
        return GSON.fromJson(jsonString, clazz);
    }

    private static class MetadataSerializer implements JsonSerializer<HasMetadata>, JsonDeserializer<HasMetadata> {
        @Override
        public JsonElement serialize(final HasMetadata src,
                                     final Type typeOfSrc,
                                     final JsonSerializationContext context) {
            final JsonObject jsonObject = (JsonObject) CLEAN_GSON.toJsonTree(src, typeOfSrc);
            src.getMetadata().forEach((k, v) -> {
                if (!jsonObject.has(k)) {
                    jsonObject.add(k, CLEAN_GSON.toJsonTree(v, v.getClass()));
                }
            });
            return jsonObject;
        }

        @Override
        public HasMetadata deserialize(final JsonElement json,
                                       final Type typeOfT,
                                       final JsonDeserializationContext context) throws JsonParseException {
            final Set<String> fieldNames = Arrays.stream(((Class) typeOfT).getFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());
            final JsonObject jsonObject = (JsonObject) json;
            final HasMetadata deserialized = CLEAN_GSON.fromJson(jsonObject, typeOfT);
            deserialized.setMetadata(jsonObject.entrySet()
                    .stream()
                    .filter(e -> !fieldNames.contains(e.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> CLEAN_GSON.fromJson(e.getValue(), Object.class))));
            return deserialized;
        }
    }
}
