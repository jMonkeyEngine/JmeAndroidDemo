package com.jme3.android.demo.camera;

import com.jme3.android.demo.control.KeepSpatialVisibleControl;
import com.jme3.android.demo.input.InputActionListener;
import com.jme3.android.demo.input.InputHandler;
import com.jme3.android.demo.utils.PickingHelpers;
import com.jme3.collision.CollisionResult;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CameraHandler implements InputActionListener {
    private static final Logger logger = Logger.getLogger(CameraHandler.class.getName());

    public enum CameraMode {
        CHASE,
        FLYCAM
    }
    private CameraMode camMode = CameraMode.CHASE;
    private Camera cam = null;
    private DemoCamera activeCamera;
    private Spatial target = null;
    private Vector3f lookAtOffset = new Vector3f();
    private CustomFlyByCamera customFlyCam = null;
    private CustomChaseCamera customChaseCam = null;
    private KeepSpatialVisibleControl keepCharVisControl = null;
    private float moveSpeed = 10f;
    private float rotateSpeed = 1f;
    private float zoomSpeed = 5f;
    private boolean panMode = false;
    private Integer pointerId = null;

    /**
     * Using this constructor you must call setCamera() before init()
     */
    public CameraHandler() {
    }

    public CameraHandler(Camera cam) {
        this.cam = cam;
    }

    public void setCamera(Camera cam) {
        this.cam = cam;
    }

    public void setCameraMode(CameraMode cameraMode, Spatial target) {
        this.camMode = cameraMode;
        this.target = target;
    }

    public void setCameraMode(CameraMode cameraMode) {
        this.camMode = cameraMode;
    }

    public CameraMode getCameraMode() {
        return camMode;
    }

    public Camera getCamera() {
        return cam;
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }

    public void setLookAtOffset(Vector3f lookAtOffset) {
        this.lookAtOffset.set(lookAtOffset);
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public void setZoomSpeed(float zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }

    public float getZoomSpeed() {
        return zoomSpeed;
    }

    public void setEnable(boolean enable) {
        if (activeCamera != null) {
            activeCamera.setEnabled(enable);
        }
    }

    public void enableKeepCharVisible(Node objects) {
        if (keepCharVisControl != null && keepCharVisControl.getSpatial() != null) {
            keepCharVisControl.getSpatial().removeControl(KeepSpatialVisibleControl.class);
        }
        keepCharVisControl = new KeepSpatialVisibleControl();
        keepCharVisControl.setObjects(objects);
        keepCharVisControl.setCamera(activeCamera);
        keepCharVisControl.setSpatialOffset(lookAtOffset);
        target.addControl(keepCharVisControl);
    }

    public void init() {
        switch (camMode) {
            case CHASE:
                customChaseCam = new CustomChaseCamera(cam, target);
                customChaseCam.setLookAtOffset(lookAtOffset);
                //customChaseCam.setDragToRotate(true);
                customChaseCam.setDefaultDistance(10f);
                customChaseCam.setMaxDistance(50f);
                customChaseCam.setMinDistance(2f);
                customChaseCam.setDefaultVerticalRotation(FastMath.QUARTER_PI);
                customChaseCam.setMaxVerticalRotation(FastMath.HALF_PI - 0.1f);
                customChaseCam.setMinVerticalRotation(0f);
                customChaseCam.setDefaultHorizontalRotation(FastMath.HALF_PI);
                customChaseCam.setSmoothMotion(false);
                customChaseCam.setRotationSensitivity(3f);
                customChaseCam.setUpVector(Vector3f.UNIT_Y);
                customChaseCam.setRotationSensitivity(rotateSpeed);
                customChaseCam.setZoomSensitivity(zoomSpeed);
//                customChaseCam.setChasingSensitivity(0.7f);
//                customChaseCam.setTrailingEnabled(false);
                activeCamera = customChaseCam;
                break;

            case FLYCAM:
                customFlyCam = new CustomFlyByCamera(cam);
                customFlyCam.setDragToRotate(true);
                customFlyCam.setMoveSpeed(moveSpeed);
                customFlyCam.setRotationSpeed(rotateSpeed);
                customFlyCam.setZoomSpeed(zoomSpeed*5);
                cam.setLocation(target.getWorldTranslation().add(0f, 10f, -10f));
                cam.lookAtDirection(target.getWorldTranslation().subtract(cam.getLocation()), Vector3f.UNIT_Y);

                activeCamera = customFlyCam;
                break;

            default:
                break;
        }

    }

    public boolean onInputAction(TouchEvent event, float tpf) {
        InputHandler.dumpEvent(this.getClass().getName(), event);
        boolean consumed = false;
        switch (event.getType()) {
            case DOWN:
                if (activeCamera != null && this.pointerId == null) {
                    activeCamera.enableRotation(true);
                    this.pointerId = event.getPointerId();
                    consumed = true;
                }
                break;
            case UP:
                if (activeCamera != null && this.pointerId != null && this.pointerId == event.getPointerId()) {
                    activeCamera.enableRotation(false);
                    panMode = false;
                    this.pointerId = null;
                    consumed = true;
                }
                break;
            case TAP:
                if (activeCamera != null && (this.pointerId == null || this.pointerId == event.getPointerId())) {
                    if (activeCamera.supportsPan()) {
                        panMode = true;
                        consumed = true;
                    }
                }
            case MOVE:
                if (activeCamera != null && this.pointerId != null && this.pointerId == event.getPointerId()) {
                    if (panMode) {
                        activeCamera.pan(event.getDeltaX()/1024, true);
                        activeCamera.pan(event.getDeltaY()/1024, false);
                    } else {
                        activeCamera.hRotate(event.getDeltaX()/1024);
                        activeCamera.vRotate(event.getDeltaY()/1024);
                    }
                    consumed = true;
                }
                break;
            case SCALE_MOVE:
                if (activeCamera != null) {
                    activeCamera.zoom(event.getDeltaScaleSpan()/1024);
                    consumed = true;
                }
                break;
            default:
                break;
        }
        return consumed;
    }

}
