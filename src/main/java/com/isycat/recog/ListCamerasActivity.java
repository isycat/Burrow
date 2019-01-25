package com.isycat.recog;

import com.google.common.collect.ImmutableList;
import com.isycat.servlet.json.JsonActivity;
import com.isycat.servlet.json.JsonResponse;

import javax.servlet.http.HttpServletRequest;

public class ListCamerasActivity extends JsonActivity {
    @Override
    public JsonResponse handle(final HttpServletRequest request) {
        return new JsonResponse(200)
                .with("cameras", ImmutableList.of("7746bd097d5247c4ab47cee937de66a3"));
    }
}
