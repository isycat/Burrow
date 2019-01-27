package com.isycat.recog.operation;

import com.github.sarxos.webcam.Webcam;
import com.isycat.servlet.json.JsonRequest;
import com.isycat.servlet.json.JsonResponse;
import com.isycat.servlet.operation.JsonOperation;
import org.apache.commons.codec.binary.Hex;

import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ListCamerasOperation extends JsonOperation<JsonRequest, JsonResponse> {
    @Override
    public JsonResponse handle(final JsonRequest request) {
        return new JsonResponse(200)
                .with("cameras", Webcam.getWebcams()
                        .stream()
                        .map(Webcam::getName)
                        .map(name -> Hex.encodeHexString(name.getBytes(UTF_8)))
                        .collect(Collectors.toList()));
    }

}
