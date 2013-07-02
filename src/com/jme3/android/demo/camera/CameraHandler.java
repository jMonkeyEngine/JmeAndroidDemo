package com.jme3.android.demo.camera;

import com.jme3.android.demo.input.LocationInputListener;
import com.jme3.android.demo.input.ValueInputListener;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import java.util.logging.Logger;

public class CameraHandler implements LocationInputListener, ValueInputListener {
    private static final Logger logger = Logger.getLogger(CameraHandler.class.getName());

    public enum CameraMode {
        CHASE,
//        NODE,
//        FIXEDAXIS,
//        FLYCAM
    }
    private CameraMode camMode = CameraMode.CHASE;
    private Camera cam = null;
    private DemoCamera activeCamera;
    private Node node = null;
    private Vector3f lookAtOffset = new Vector3f();
    private CameraNode nodeCam = null;
    private Node fixedAxisCamNode = null;
    private CustomFlyByCamera customFlyCam = null;
    private CustomChaseCamera customChaseCam = null;
    private float moveSpeed = 1f;
    private float rotateSpeed = 1f;
    private float zoomSpeed = 1f;

    public CameraHandler(Camera cam) {
        this.cam = cam;
    }

    public void setCameraMode(CameraMode cameraMode, Node node) {
        this.camMode = cameraMode;
        this.node = node;
    }

    public CameraMode getCameraMode() {
        return camMode;
    }

    public Camera getCamera() {
        return cam;
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
        switch (camMode) {
            case CHASE:
                customChaseCam.setEnabled(enable);
                break;
//            case FIXEDAXIS:
//                break;
//            case FLYCAM:
//                customFlyCam.setEnabled(enable);
//                break;
//            case NODE:
//                nodeCam.setEnabled(enable);
//                break;
            default:
                break;
        }
    }


    public void init() {
        switch (camMode) {
            case CHASE:
                customChaseCam = new CustomChaseCamera(cam, node);
                customChaseCam.setLookAtOffset(lookAtOffset);
                //customChaseCam.setDragToRotate(true);
                customChaseCam.setDefaultDistance(10f);
                customChaseCam.setMaxDistance(50f);
                customChaseCam.setMinDistance(2f);
                customChaseCam.setDefaultVerticalRotation(FastMath.QUARTER_PI);
                customChaseCam.setMaxVerticalRotation(FastMath.HALF_PI - 0.1f);
                customChaseCam.setMinVerticalRotation(0f);
                customChaseCam.setDefaultHorizontalRotation(-FastMath.HALF_PI);
                customChaseCam.setSmoothMotion(false);
                customChaseCam.setRotationSensitivity(3f);
                customChaseCam.setUpVector(Vector3f.UNIT_Y);
                customChaseCam.setRotationSensitivity(rotateSpeed);
                customChaseCam.setZoomSensitivity(zoomSpeed);
//                customChaseCam.setChasingSensitivity(0.7f);
//                customChaseCam.setTrailingEnabled(false);
                activeCamera = customChaseCam;
                break;

//            case NODE:
//                //create the camera Node
//                nodeCam = new CameraNode("Camera Node", cam);
//                //This mode means that camera copies the movements of the target:
//                nodeCam.setControlDir(ControlDirection.SpatialToCamera);
//                //Attach the camNode to the target:
//                node.attachChild(nodeCam);
//                //Move camNode
//                nodeCam.setLocalTranslation(new Vector3f(0f, 5f, -10f));
//                //Rotate the camNode to look at the target:
//                nodeCam.lookAt(node.getLocalTranslation().add(lookAtOffset), Vector3f.UNIT_Y);
////                nodeCam.lookAt(lookAt.add(lookAtOffset), Vector3f.UNIT_Y);
//
//                activeCamera = nodeCam;
//                break;
//
//            case FIXEDAXIS:
//                //create the camera Node
//                fixedAxisCamNode = new Node("Fixed Axis Camera Node");
//
//                //create and configure the control
//                FixedAxisCamera control = new FixedAxisCamera(cam);
////                control.setLocationOffset(new Vector3f(0f, 35f, 35f));
//                control.setLocationOffset(new Vector3f(0f, 3f, -7f));
//                control.setLookAtOffset(Vector3f.ZERO);
////                control.setMinWorldHeight(35f);
////                control.setMinWorldHeight(-9999f);
//                control.setMinWorldHeight(2.5f);
//                control.setAllowedRotationAxes(new Vector3f(0f, 1f, 0f));
//
//                //attach the control to the new node
//                fixedAxisCamNode.addControl(control);
//
//                //Attach the camNode to the target:
//                node.attachChild(fixedAxisCamNode);
//
//                activeCamera = fixedAxisCamNode;
//                break;
//
//            case FLYCAM:
//                logger.log(Level.INFO, "Creating new CustomFlyCam");
//                FlyCamAppState flyCamState = app.getStateManager().getState(FlyCamAppState.class);
//                if (flyCamState != null) {
//                    app.getStateManager().detach(flyCamState);
//                }
//                customFlyCam = new CustomFlyByCamera(cam, inputManager);
//                customFlyCam.setDragToRotate(true);
//                customFlyCam.setMoveSpeed(moveSpeed);
//                customFlyCam.setRotationSpeed(rotateSpeed);
//                customFlyCam.setZoomSpeed(zoomSpeed);
//                customFlyCam.registerWithInput(inputManager);
//                cam.setLocation(new Vector3f(62f, 32f, 6f));
//                cam.lookAtDirection(new Vector3f(-1f, -0.5f, 0f), Vector3f.UNIT_Y);
//
//                activeCamera = customFlyCam;
//                break;

            default:
                break;
        }

    }

    public boolean onLocation(LocationType locationType, int pointerId, float locX, float locY, float tpf) {
        boolean consumed = false;
        switch (locationType) {
            case DOWN:
                if (activeCamera != null) {
                    activeCamera.enableRotation(true);
                    consumed = true;
                }
                break;
            case UP:
                if (activeCamera != null) {
                    activeCamera.enableRotation(false);
                    consumed = true;
                }
                break;
            default:
                break;
        }
        return consumed;
    }

    public boolean onValue(ValueType valueType, int pointerId, float value, float tpf) {
        boolean consumed = false;
        switch (valueType) {
            case X_AXIS_DRAG:
                if (activeCamera != null) {
                    activeCamera.hRotate(value/1024);
                    consumed = true;
                }
                break;
            case Y_AXIS_DRAG:
                if (activeCamera != null) {
                    activeCamera.vRotate(value/1024);
                    consumed = true;
                }
                break;
            case PINCH:
                if (activeCamera != null) {
                    activeCamera.zoom(value/1024);
                    consumed = true;
                }
                break;
            default:
                break;
        }
        return consumed;
    }

}
