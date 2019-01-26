package com.isycat.recog;

import com.isycat.recog.operation.GetCameraImageOperation;
import com.isycat.recog.operation.ListCamerasOperation;
import com.isycat.servlet.operation.OperationRoute;
import com.isycat.servlet.Router;

public class RekogRouter extends Router {
    public RekogRouter() {
        super(
                new OperationRoute("/ListCameras", ListCamerasOperation::new),
                new OperationRoute("/listcameras", ListCamerasOperation::new),
                new OperationRoute("/GetCamera/{cameraId}", GetCameraImageOperation::new)
        );
    }
}
