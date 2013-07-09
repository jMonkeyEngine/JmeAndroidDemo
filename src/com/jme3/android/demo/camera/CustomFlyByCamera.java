package com.jme3.android.demo.camera;

import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class CustomFlyByCamera extends FlyByCamera implements DemoCamera {
    private static final Logger logger = Logger.getLogger(CustomFlyByCamera.class.getName());

    public CustomFlyByCamera(Camera cam){
        super(cam);
    }

    public void enableRotation(boolean enable) {
        if (!enabled) {
            return;
        }
        canRotate = enable;
    }

    public boolean isRotationEnabled() {
        return canRotate;
    }

    public void hRotate(float value) {
        if (enabled) {
           rotateCamera(value, initialUpVec);
        }
    }

    public void vRotate(float value) {
        if (enabled) {
            rotateCamera(-value * (invertY ? -1 : 1), cam.getLeft());
        }
    }

    public void zoom(float value) {
        if (enabled) {
            zoomCamera(value);
        }
    }

    public void pan(float value, boolean sideways) {
        if (enabled) {
            moveCamera(value, sideways);
            if (!sideways) {
                riseCamera(value);
            }
        }
    }

    public boolean supportsPan() {
        return true;
    }

    public void autoRotate(float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void autoZoom(float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetAutoZoom() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Camera getCamera() {
        return cam;
    }

}
