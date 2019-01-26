package com.isycat.recog.activity;

import com.isycat.servlet.activity.ImageActivity;
import com.isycat.servlet.json.JsonRequest;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class GetCameraImageActivity extends ImageActivity<GetCameraImageActivity.GetCameraImageRequest> {
    public GetCameraImageActivity() {
        super("jpeg");
    }

    @Override
    public RenderedImage handle(final GetCameraImageRequest request) throws IOException {
        // todo: make sure servlet handles 4xx/5xx from exception types
        // i.e. customer facing exception types that don't get buried as 500s
        // then this can use file.exists() and 404
        return ImageIO.read(new File("e:\\" + request.getCameraId() + ".jpg"));
    }

    public static class GetCameraImageRequest extends JsonRequest {
        public String getCameraId() {
            return Optional.ofNullable(this.get("cameraId"))
                    .map(Object::toString)
                    // don't eat that file system
                    // todo: DESTROY this; it's too close to tech horror
                    .filter(id -> id.matches("[A-Za-z0-9]{1,32}"))
                    .orElse("");
        }
    }
}
