package com.jme3.android.demo.camera;

import com.jme3.input.ChaseCamera;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class CustomChaseCamera extends ChaseCamera implements DemoCamera {
    private static final Logger logger = Logger.getLogger(CustomChaseCamera.class.getName());

    public CustomChaseCamera(Camera cam, final Spatial target) {
        super(cam, target);
        dragToRotate = true;
        canRotate = false;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    public void enableRotation(boolean enable) {
        canRotate = enable;
    }

    public void hRotate(float value) {
        rotateCamera(value);
    }

    public void vRotate(float value) {
        vRotateCamera(value);
    }

    public void zoom(float value) {
        zoomCamera(value);
        if (value < 0) {
            if (zoomin == false) {
                distanceLerpFactor = 0;
            }
            zoomin = true;
        } else {
            if (zoomin == true) {
                distanceLerpFactor = 0;
            }
            zoomin = false;
        }
    }


}
