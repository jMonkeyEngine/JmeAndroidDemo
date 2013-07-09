package com.jme3.android.demo.camera;

import com.jme3.input.ChaseCamera;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class CustomChaseCamera extends ChaseCamera implements DemoCamera {
    private static final Logger logger = Logger.getLogger(CustomChaseCamera.class.getName());
    private float userZoom = 0f;
    private boolean autoZoomActive = false;
    private boolean autoRotateActive = false;
    private float autoRotateTarget = 0f;
    private float autoRotateCurrent = 0f;

    public CustomChaseCamera(Camera cam, final Spatial target) {
        super(cam, target);
        dragToRotate = true;
        canRotate = false;
        userZoom = targetDistance;
    }

    @Override
    public void setDefaultDistance(float distance) {
        super.setDefaultDistance(distance);
        userZoom = targetDistance;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (!autoZoomActive) {
//            logger.log(Level.INFO, "userZoom: {0}, targetDistance: {1}",
//                    new Object[]{userZoom, targetDistance});
            internalZoom((userZoom-targetDistance)*2f*tpf);
        }

        if (autoRotateActive) {
            if (Math.abs(autoRotateCurrent-autoRotateTarget) < 0.01) {
                autoRotateCurrent = autoRotateTarget = 0f;
                autoRotateActive = false;
            } else {
//                logger.log(Level.INFO, "updating autoRotateCurrent: {0}, autoRotateTarget: {1}",
//                        new Object[]{autoRotateCurrent, autoRotateTarget});
                float value = (autoRotateTarget-autoRotateCurrent)*10f*tpf;
                internalRotate(value);
                autoRotateCurrent += value;
            }
        }

    }

    public void enableRotation(boolean enable) {
        canRotate = enable;
    }

    public boolean isRotationEnabled() {
        return canRotate;
    }

    public void hRotate(float value) {
        rotateCamera(value);
    }

    public void vRotate(float value) {
        vRotateCamera(value);
    }

    public void zoom(float value) {
        internalZoom(value);
        userZoom = targetDistance;
//        logger.log(Level.INFO, "zoom userZoom: {0}, targetDistance: {1}",
//                new Object[]{userZoom, targetDistance});
    }

    public void pan(float value, boolean sideways) {
        // do nothing for chase cam
    }

    public boolean supportsPan() {
        return false;
    }

    public void autoRotate(float value) {
//        logger.log(Level.INFO, "Setting ForceRotate: {0}", value);
        autoRotateActive = true;
        autoRotateCurrent = 0f;
        autoRotateTarget = value;
    }

    public void autoZoom(float value) {
//        boolean prevEnableRotation = activeCamera.isRotationEnabled();
//        activeCamera.enableRotation(true);
        autoZoomActive = true;
        internalZoom(-value);
//        activeCamera.enableRotation(prevEnableRotation);
    }

    public void resetAutoZoom() {
        autoZoomActive = false;
    }

    private void internalZoom(float value) {
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

    private void internalRotate(float value) {
        boolean prevEnableRotation = isRotationEnabled();
        enableRotation(true);
        hRotate(value);
        enableRotation(prevEnableRotation);
    }

    public Camera getCamera() {
        return cam;
    }

}
