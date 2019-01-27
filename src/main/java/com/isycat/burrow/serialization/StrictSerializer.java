package com.isycat.burrow.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StrictSerializer {
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static <ObjectType> String serialize(final ObjectType object) {
        return GSON.toJson(object);
    }
}
