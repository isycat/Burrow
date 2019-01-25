package com.isycat.servlet.json;

import com.google.gson.Gson;

import java.util.LinkedHashMap;

public class JsonRequest extends LinkedHashMap<String, Object> {
    private static Gson GSON = new Gson();

    public JsonRequest with(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public String toString() {
        return GSON.toJson(this);
    }
}