package com.isycat.burrow.json;

import com.isycat.burrow.error.Structure;

public class JsonResponse extends Structure {
    private final int status;

    public JsonResponse() {
        this.status = 200;
    }

    public JsonResponse(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
