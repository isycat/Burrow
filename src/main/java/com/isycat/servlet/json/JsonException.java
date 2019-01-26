package com.isycat.servlet.json;

import com.isycat.servlet.HttpConstants;

public class JsonException extends JsonResponse{

    /**
     * Constructor.
     *   Status code defaults to 500
     *
     * @param message error message (user facing)
     */
    public JsonException(final String message) {
        super(500);
        this.with(HttpConstants.Fields.MESSAGE, message);
    }

    /**
     * Constructor.
     *
     * @param status http status code of the exception.
     * @param message error message (user facing)
     */
    public JsonException(final int status, final String message) {
        super(status);
        this.with(HttpConstants.Fields.MESSAGE, message);
    }

    /**
     * Associates the specified value with the specified key in this object.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return this
     */
    @Override
    public JsonException with(String key, Object value) {
        this.put(key, value);
        return this;
    }
}
