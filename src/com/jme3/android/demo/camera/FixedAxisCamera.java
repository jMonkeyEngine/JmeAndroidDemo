package com.jme3.android.demo.camera;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.logging.Logger;

/**
 * This Control maintains a reference to a Camera,
 * which will be synched with the position (worldTranslation)
 * of the spatial but will keep the y axis value between a min and max value.
 * @author
 */
public class FixedAxisCamera extends AbstractControl {
    private static final Logger logger = Logger.getLogger(FixedAxisCamera.class.getName());

    private Camera camera = null;
    private Vector3f location = new Vector3f();
    private Quaternion rotation = new Quaternion();
    private Vector3f allowedRotationAxes = new Vector3f(1f, 1f, 1f);
    private float[] angles = new float[3];
    private Vector3f lookAtOffset = new Vector3f();
    private Vector3f locationOffset = new Vector3f();
    private Vector3f tmpLocationOffset = new Vector3f();
    private float minWorldHeight = 0f;
    private float maxWorldHeight = 999999f;
    private float newWorldHeight = 0f;

    /**
     * Constructor used for Serialization.
     */
    public FixedAxisCamera() {
    }

    public FixedAxisCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getMinWorldHeight() {
        return minWorldHeight;
    }
    public void setMinWorldHeight(float minWorldHeight) {
        this.minWorldHeight = minWorldHeight;
    }

    public float getMaxWorldHeight() {
        return maxWorldHeight;
    }
    public void setMaxWorldHeight(float maxWorldHeight) {
        this.maxWorldHeight = maxWorldHeight;
    }

    public Vector3f getLookAtOffset() {
        return lookAtOffset;
    }

    public void setLookAtOffset(Vector3f lookAtOffset) {
        this.lookAtOffset = lookAtOffset;
    }

    public Vector3f getLocationOffset() {
        return locationOffset;
    }

    public void setLocationOffset(Vector3f locationOffset) {
        this.locationOffset = locationOffset;
    }

    public Vector3f getAllowedRotationAxes() {
        return allowedRotationAxes;
    }

    public void setAllowedRotationAxes(Vector3f allowedRotationAxes) {
        this.allowedRotationAxes.set(allowedRotationAxes);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null && camera != null) {

            location.set(spatial.getWorldTranslation());
            rotation.set(spatial.getWorldRotation());

            // clear any rotation around not allowed axes
            rotation.toAngles(angles);
            angles[0] *= allowedRotationAxes.x;
            angles[1] *= allowedRotationAxes.y;
            angles[2] *= allowedRotationAxes.z;
            rotation.fromAngles(angles);

            tmpLocationOffset.set(locationOffset);
            rotation.multLocal(tmpLocationOffset);
            location.addLocal(tmpLocationOffset);

            // world offset to keep camera between min and max world height
            if (location.y < minWorldHeight) {
                location.y = minWorldHeight;
            }
            if (location.y > maxWorldHeight) {
                location.y = maxWorldHeight;
            }

//            logger.log(Level.INFO, "spatial location: {0}, rotation: {1}, new location: {2}",
//                    new Object[]{spatial.getWorldTranslation(), spatial.getWorldRotation(), location});

            camera.setLocation(location);
            camera.lookAt(spatial.getWorldTranslation(), Vector3f.UNIT_Y);

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing to do
    }

    @Override
    public Control cloneForSpatial(Spatial newSpatial) {
        FixedAxisCamera control = new FixedAxisCamera(camera);
        control.setSpatial(newSpatial);
        control.setEnabled(isEnabled());
        control.setLocationOffset(locationOffset);
        control.setLookAtOffset(lookAtOffset);
        control.setMaxWorldHeight(maxWorldHeight);
        control.setMinWorldHeight(minWorldHeight);
        return control;
    }

}