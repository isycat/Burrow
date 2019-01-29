package com.isycat.burrow.json;

import com.isycat.burrow.error.Structure;

import java.util.Map;

public class JsonRequest extends Structure {
    private Map<String, String> headers;

    public Map<String, String> getHeaders() {
        return headers;
    }
}
