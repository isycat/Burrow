package com.isycat.servlet.json;

import com.google.gson.Gson;
import com.isycat.servlet.HttpConstants;

import java.util.LinkedHashMap;

public class JsonResponse extends LinkedHashMap<String, Object> {
    private static Gson GSON = new Gson();

    public JsonResponse() {
        this.with(HttpConstants.Fields.STATUS, 200);
    }

    public JsonResponse(final int status) {
        this.with(HttpConstants.Fields.STATUS, status);
    }


    /**
     * Associates the specified value with the specified key in this
     * response object.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return this
     */
    public JsonResponse with(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public String toString() {
        return GSON.toJson(this);
    }
}
