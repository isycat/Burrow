package com.isycat.recog;

import com.isycat.recog.activity.GetCameraImageActivity;
import com.isycat.recog.activity.ListCamerasActivity;
import com.isycat.servlet.activity.ActivityRoute;
import com.isycat.servlet.Router;

public class RekogRouter extends Router {
    public RekogRouter() {
        super(
                new ActivityRoute("/ListCameras", ListCamerasActivity::new),
                new ActivityRoute("/listcameras", ListCamerasActivity::new),
                new ActivityRoute("/GetCamera/{cameraId}", GetCameraImageActivity::new)
        );
    }
}
