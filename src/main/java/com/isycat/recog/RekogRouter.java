package com.isycat.recog;

import com.isycat.servlet.ServletRouter;

public class RekogRouter extends ServletRouter {
    public RekogRouter() {
        super(
                new ActivityRoute("ListCameras", new ListCamerasActivity()),
                new ActivityRoute("listcameras", new ListCamerasActivity())
        );
    }
}
