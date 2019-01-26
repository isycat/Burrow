package com.isycat.recog.activity;

import com.google.common.collect.ImmutableList;
import com.isycat.servlet.json.JsonActivity;
import com.isycat.servlet.json.JsonRequest;
import com.isycat.servlet.json.JsonResponse;

public class GetCameraImageActivity extends JsonActivity<JsonRequest, JsonResponse> {
    @Override
    public JsonResponse handle(final JsonRequest request) {
        return new JsonResponse(200)
                .with("cameras", ImmutableList.of("7746bd097d5247c4ab47cee937de66a3"));
    }
}
