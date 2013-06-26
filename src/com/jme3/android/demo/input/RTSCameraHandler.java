/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.android.demo.input;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nehon
 */
public class RTSCameraHandler extends AbstractControl implements ActionListener, AnalogListener, TouchListener {

    private static final Logger logger = Logger.getLogger(RTSCameraHandler.class.getName());
    private static final String TOUCH = "RTSCameraToouch";
    protected Node ghostTarget = new Node("cameraGhostTarget");
    //protected CameraNode cameraNode;
    protected Node cameraNode;
    protected Camera camera;
    protected float cameraMaxDistance = 30;
    protected float cameraDistance = cameraMaxDistance;
    protected float cameraMinDistance = 3;
    protected float maxVerticalRotation = -1.4f;
    protected float verticalRotation = -0.6f;
    protected float minVerticalRotation = -0.15f;
    protected float zoomValue = 1;
    protected Vector3f targetStartPosition = new Vector3f();
    protected Vector3f targetActualPosition = new Vector3f();
    protected Vector3f targetEndPosition = new Vector3f();
    protected float targetTransitionTime = 0.5f;
    protected float targetMovingProgress = targetTransitionTime;
    //Camera
    protected Vector3f cameraStartPosition = new Vector3f();
    protected Vector3f cameraActualPosition = new Vector3f();
    protected Vector3f cameraEndPosition = new Vector3f();
    protected float cameraTransitionTime = targetTransitionTime * 1.5f;
    protected float cameraMovingProgress = cameraTransitionTime;
    protected float previousScale = 0;
    protected Quaternion rot = new Quaternion();
    protected Quaternion rot2 = new Quaternion();
    protected float horizontalRotation = 0;
    //only for desktop
    protected boolean drag = false;
    private static final String ROTATION_LEFT = "RTSCameraRotationLeft";
    private static final String ROTATION_RIGHT = "RTSCameraRotationRight";
    private static final String ROTATION_UP = "RTSCameraRotationUp";
    private static final String ROTATION_DOWN = "RTSCameraRotationDown";
    private static final String ZOOM_IN = "RTSCameraZoomIn";
    private static final String ZOOM_OUT = "RTSCameraZoomOut";
    private Mode mode = Mode.Rotate;
    private boolean mouseSupportEnabled = true;

    public enum Mode {

        Pan, Rotate
    }

    public RTSCameraHandler(Camera cam, Node parentNode) {
        this.camera = cam;
        cameraNode = new Node("RTSCameraNode");
        ghostTarget.attachChild(cameraNode);
        cameraNode.setLocalTranslation(new Vector3f(0, 0, cameraDistance));
        camera.setLocation(cameraNode.getLocalTranslation());
        camera.setRotation(cameraNode.getLocalRotation());
        parentNode.attachChild(ghostTarget);

        ghostTarget.setLocalRotation(new Quaternion().fromAngleNormalAxis(-0.6f, Vector3f.UNIT_X));
        ghostTarget.addControl(this);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (enabled) {
            if (targetMovingProgress <= targetTransitionTime) {
                targetMovingProgress += tpf;
                FastMath.interpolateLinear(targetMovingProgress / targetTransitionTime, targetStartPosition, targetEndPosition, targetActualPosition);
                ghostTarget.setLocalTranslation(targetActualPosition);
            }

            if (cameraMovingProgress <= cameraTransitionTime) {
                cameraMovingProgress += tpf;
                FastMath.interpolateLinear(cameraMovingProgress / cameraTransitionTime, cameraStartPosition, cameraNode.getWorldTranslation(), cameraActualPosition);
                camera.setLocation(cameraActualPosition);

            } else {
                camera.setLocation(cameraNode.getWorldTranslation());
                camera.setRotation(cameraNode.getWorldRotation());
            }
            // 

            //  logger.warning("rotation x : " + ghostTarget.getWorldRotation().toAngleAxis(Vector3f.UNIT_X));
            camera.lookAt(targetActualPosition, Vector3f.UNIT_Y);
        }
    }

    public void lookAt(Vector3f position) {

        targetMovingProgress = 0;
        targetEndPosition.set(position);
        targetEndPosition.y = 1.0f;
        targetStartPosition.set(ghostTarget.getLocalTranslation());

        cameraMovingProgress = 0;
        cameraStartPosition.set(camera.getLocation());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        RTSCameraHandler rch;
        try {
            rch = (RTSCameraHandler) super.clone();
            spatial.addControl(rch);
            return rch;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(RTSCameraHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void registerInputs(InputManager inputManager) {
        inputManager.addListener(this, TOUCH);
        inputManager.addMapping(TOUCH, new TouchTrigger(TouchInput.ALL));

        //only for desktop
        inputManager.addMapping(ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping(ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(ROTATION_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(ROTATION_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(ROTATION_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(ROTATION_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("down", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addMapping("togglePan", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        inputManager.addListener(this, ZOOM_IN, ZOOM_OUT, ROTATION_LEFT, ROTATION_RIGHT, ROTATION_UP, ROTATION_DOWN, "down", "togglePan");

    }
    Vector3f tmpVect = new Vector3f();

    //only for desktop
    public void onAction(String name, boolean isPressed, float tpf) {
        if (enabled && mouseSupportEnabled) {
            if (name.equals("down")) {
                drag = isPressed;
            }
            if (name.equals("togglePan")) {
                drag = isPressed;
                if (isPressed) {
                    setMode(Mode.Pan);
                    tmpVect.set(ghostTarget.getLocalTranslation());
                } else {
                    setMode(Mode.Rotate);
                }
            }
        }

    }

    //only for desktop
    public void onAnalog(String name, float value, float tpf) {
        if (enabled && mouseSupportEnabled) {
            boolean rotate = false;
            boolean pan = false;
            if (name.equals(ZOOM_IN)) {
                cameraDistance = FastMath.clamp(cameraDistance + value, cameraMinDistance, cameraMaxDistance);
                cameraNode.setLocalTranslation(new Vector3f(0, 0, cameraDistance));
            }
            if (name.equals(ZOOM_OUT)) {
                cameraDistance = FastMath.clamp(cameraDistance - value, cameraMinDistance, cameraMaxDistance);
                cameraNode.setLocalTranslation(new Vector3f(0, 0, cameraDistance));
            }
            if (mode == Mode.Rotate) {
                if (name.equals(ROTATION_RIGHT) && drag) {
                    horizontalRotation -= value;
                    rotate = true;
                }
                if (name.equals(ROTATION_LEFT) && drag) {
                    horizontalRotation += value;
                    rotate = true;
                }
                if (name.equals(ROTATION_UP) && drag) {
                    verticalRotation -= value;
                    if (verticalRotation < maxVerticalRotation) {
                        verticalRotation += value;
                    }
                    rotate = true;
                }


                if (name.equals(ROTATION_DOWN) && drag) {
                    verticalRotation += value;
                    if (verticalRotation > minVerticalRotation) {
                        verticalRotation -= value;
                    }
                    rotate = true;
                }

                if (rotate && drag) {
                    rot.fromAngleNormalAxis(verticalRotation, Vector3f.UNIT_X);
                    rot2.fromAngleNormalAxis(horizontalRotation, Vector3f.UNIT_Y);
                    rot2.multLocal(rot);
                    ghostTarget.setLocalRotation(rot2);
                }
            } else {
                float speed = 20;
                TempVars vars = TempVars.get();
                Vector3f dir = vars.vect1;
                if (name.equals(ROTATION_RIGHT) && drag) {
                    dir.set(camera.getLeft()).normalizeLocal();
                    tmpVect.addLocal(dir.multLocal(value * speed));
                    pan = true;
                }
                if (name.equals(ROTATION_LEFT) && drag) {
                    dir.set(camera.getLeft()).normalizeLocal();
                    tmpVect.subtractLocal(dir.multLocal(value * speed));
                    pan = true;
                }
                if (name.equals(ROTATION_UP) && drag) {
                    dir.set(camera.getDirection());
                    dir.y = 0;
                    dir.normalizeLocal();
                    tmpVect.addLocal(dir.multLocal(value * speed));
                    pan = true;
                }
                if (name.equals(ROTATION_DOWN) && drag) {
                    dir.set(camera.getDirection());
                    dir.y = 0;
                    dir.normalizeLocal();
                    tmpVect.subtractLocal(dir.multLocal(value * speed));
                    pan = true;
                }
                if (pan && drag) {
                    tmpVect.y = ghostTarget.getLocalTranslation().y;
                    lookAt(tmpVect);
                }
                vars.release();
            }
        }
    }
    private int nbDown = 0;

    public void onTouch(String name, TouchEvent event, float tpf) {
        if (enabled) {
//            System.out.println(event.getType());
            mouseSupportEnabled = false;
            if (event.getType() == TouchEvent.Type.DOWN) {
                nbDown++;

                //        System.out.println("nbDown Down" + nbDown);
                if (nbDown > 1) {
                    tmpVect.set(ghostTarget.getLocalTranslation());
                    setMode(Mode.Pan);
                }
            }
            if (event.getType() == TouchEvent.Type.UP) {
                nbDown--;
                if (nbDown < 0) {
                    nbDown = 0;
                }
                //  System.out.println("nbDown UP " + nbDown);
                setMode(Mode.Rotate);
            }
            if (event.getType() == TouchEvent.Type.SCALE_MOVE) {
                float scale = (previousScale - event.getScaleSpan()) * 0.05f;
                previousScale = event.getScaleSpan();

                cameraDistance = FastMath.clamp(cameraDistance + scale, cameraMinDistance, cameraMaxDistance);
                cameraNode.setLocalTranslation(new Vector3f(0, 0, cameraDistance));
            }
            if (event.getType() == TouchEvent.Type.SCALE_START) {
                previousScale = event.getScaleSpan();
            }
            if (mode == Mode.Rotate) {
                if (event.getType() == TouchEvent.Type.SCROLL) {

                    float xrot = -event.getDeltaY() * 0.005f;
                    verticalRotation += xrot;
                    horizontalRotation += event.getDeltaX() * 0.005f;
                    if (verticalRotation > minVerticalRotation || verticalRotation < maxVerticalRotation) {
                        verticalRotation -= xrot;
                    }
                    rot.fromAngleNormalAxis(verticalRotation, Vector3f.UNIT_X);
                    rot2.fromAngleNormalAxis(horizontalRotation, Vector3f.UNIT_Y);
                    rot2.multLocal(rot);
                    ghostTarget.setLocalRotation(rot2);

                }
            } else {
                if (event.getType() == TouchEvent.Type.SCROLL) {
                    //System.out.println("Scroll");
                    float speed = 0.001f * cameraDistance;
                    TempVars vars = TempVars.get();
                    Vector3f dir = vars.vect1;
                    float xDelta = event.getDeltaX();
                    float yDelta = event.getDeltaY();


                    dir.set(camera.getLeft()).normalizeLocal();
                    tmpVect.subtractLocal(dir.multLocal(xDelta * speed));

                    dir.set(camera.getDirection());
                    dir.y = 0;
                    dir.normalizeLocal();
                    tmpVect.addLocal(dir.multLocal(yDelta * speed));


                    tmpVect.y = ghostTarget.getLocalTranslation().y;
                    // System.out.println(tmpVect);
                    lookAt(tmpVect);

                    vars.release();
                }

            }


        }


    }

    private void outputEvent(TouchEvent event) {
        logger.log(Level.WARNING, "event : {0}\nx : {1}\ny : {2}\ndeltaX : {3}\ndeltaY : {4}\npressure : {5}\nscale factor : {6}\nscale span : {7}\n----------------", new Object[]{event.getType(), event.getX(), event.getY(), event.getDeltaX(), event.getDeltaY(), event.getPressure(), event.getScaleFactor(), event.getScaleSpan()});

    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        if (mode != this.mode) {
            this.mode = mode;
            if (mode == Mode.Pan) {
                targetTransitionTime = 0;
                cameraTransitionTime = 0;
            } else {
                targetTransitionTime = 0.5f;
                cameraTransitionTime = targetTransitionTime * 1.5f;
            }
        }
    }
}
