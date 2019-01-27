package com.isycat.recog.operation;

import com.github.sarxos.webcam.Webcam;
import com.isycat.servlet.json.JsonRequest;
import com.isycat.servlet.operation.ImageOperation;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GetCameraImageOperation extends ImageOperation<GetCameraImageOperation.GetCameraImageRequest> {
    public GetCameraImageOperation() {
        super("jpeg");
    }

    @Override
    public RenderedImage handle(final GetCameraImageRequest request) throws IOException {
        // todo: make sure servlet handles 4xx/5xx from exception types
        // i.e. customer facing exception types that don't get buried as 500s
        // then this can use file.exists() and 404
        final String cameraId = request.getCameraId();
        final Webcam webcam;
        if ("camtest".equals(cameraId)) {
            webcam = Webcam.getDefault();
        } else {
            try {
                webcam = Webcam.getWebcamByName(new String(Hex.decodeHex(cameraId.toCharArray()), UTF_8));
            } catch (final DecoderException e) {
                e.printStackTrace();
                throw new RuntimeException("Nope!");
            }
        }
        if (!webcam.isOpen()) {
            webcam.open();
        }
        return webcam.getImage();
//        return ImageIO.read(new File("e:\\" + cameraId + ".jpg"));
    }

    public static class GetCameraImageRequest extends JsonRequest {
        public String getCameraId() {
            return Optional.ofNullable(this.get("cameraId"))
                    .map(Object::toString)
                    // don't eat that file system
                    // todo: DESTROY this; it's way too close to tech horror
                    .filter(id -> id.matches("[A-Za-z0-9]{1,64}"))
                    .orElseThrow(() -> new RuntimeException("Invalid parameter value for 'cameraId'"));
        }
    }
}
