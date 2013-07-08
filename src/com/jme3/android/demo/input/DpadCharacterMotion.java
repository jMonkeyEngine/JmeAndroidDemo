package com.jme3.android.demo.input;

import com.jme3.android.demo.Main;
import com.jme3.android.demo.system.CharacterHandler;
import com.jme3.android.demo.utils.SelectablePicture;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class DpadCharacterMotion extends AbstractAppState implements
        InputActionListener, CharacterMotion {
    private static final Logger logger = Logger.getLogger(DpadCharacterMotion.class.getName());

    private AppStateManager stateManager = null;
    private Main app = null;
    private AssetManager assetManager = null;
    private Node guiNode = null;
    private Node rootNode = null;
    private AppSettings appSettings = null;

    private Integer pointerId = null;

    private Node dpadNode = new Node("dpad");
    private SelectablePicture dpadPicture = null;
    private CharacterHandler characterHandler = null;
    private BetterCharacterControl characterControl = null;
    private Vector3f maxVelocity = new Vector3f(2f, 0f, 2f);

    private boolean useCameraRotation = false;
    private Camera camera = null;
    private float[] camAngles = new float[3];
    Quaternion levelCamRotation = new Quaternion();
    private Vector3f dpadVector = new Vector3f();
    private Vector3f walkDirection = new Vector3f();

    public void setCharacterHandler(CharacterHandler characterHandler) {
        this.characterHandler = characterHandler;
        this.characterControl = characterHandler.getCharPhysicsControl();
        logger.log(Level.INFO, "Setting charactercontrol: {0}", characterControl);
    }

    private void initDpad() {
        dpadPicture = new SelectablePicture("Dpad", false);
        dpadPicture.setImage(assetManager, "Interface/Dpad.png", true);
        float width = Math.min(appSettings.getWidth() * 0.5f, appSettings.getHeight() * 0.5f);
        float height = width;
        dpadPicture.setWidth(width);
        dpadPicture.setHeight(height);
        dpadNode.attachChild(dpadPicture);
        dpadNode.setLocalTranslation(0, 0, 0);
        pointerId = null;
        setEnabled(true);
    }

    public void setUseCameraRotation(boolean useCameraRotation) {
        this.useCameraRotation = useCameraRotation;
    }
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.app = (Main)app;
        this.assetManager = app.getAssetManager();
        this.guiNode = this.app.getGuiNode();
        this.rootNode = this.app.getRootNode();
        this.appSettings = app.getContext().getSettings();

        initDpad();
        if (camera == null) {
            camera = this.app.getViewPort().getCamera();
        }

        setCharacterHandler(this.app.getSceneAppState().getMainCharacter());

        super.initialize(stateManager, app);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            if (!guiNode.hasChild(dpadNode)) {
                guiNode.attachChild(dpadNode);
            }
        } else {
            if (guiNode.hasChild(dpadNode)) {
                guiNode.detachChild(dpadNode);
            }
        }
        pointerId = null;
        super.setEnabled(enabled);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (characterHandler.getCharacterMotion() == null || !characterHandler.getCharacterMotion().equals(this)) {
            return;
        }

        Vector3f rotatedDpadVector = new Vector3f(dpadVector);

        if (useCameraRotation && camera == null) {
            throw new IllegalStateException("Camera is null with useCameraRotion");
        } else if (useCameraRotation) {
            // create a quat level with the ground
            camera.getRotation().toAngles(camAngles);
            camAngles[0] = 0f;
            levelCamRotation.fromAngles(camAngles).normalizeLocal();
            // rotate dpad based on camera rotation level with the ground
            levelCamRotation.multLocal(rotatedDpadVector);
        }
        walkDirection.set(maxVelocity.mult(rotatedDpadVector));
//        logger.log(Level.INFO, "Setting WalkDirection: {0}", walkDirection);
        characterControl.setWalkDirection(walkDirection);
        if (walkDirection.lengthSquared() > 0) {
            characterControl.setViewDirection(walkDirection);
        } else {
            characterHandler.setCharacterMotion(null);
        }

    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    public boolean onInputAction(TouchEvent event, float tpf) {
        boolean consumed = false;
        InputHandler.dumpEvent(this.getClass().getName(), event);
        if (isEnabled()) {
            if (characterHandler.getCharacterMotion() != null && !characterHandler.getCharacterMotion().equals(this)) {
                return false;
            }
            switch (event.getType()) {
                case SCALE_MOVE:
                    if (this.pointerId != null) {
                        // block zooming while navigating with dpad
                        // still allows x axis and y axis dragging for camera rotation
                        consumed = true;
                    }
                    break;
                case DOWN:
                    if (this.pointerId == null && checkSelect(event.getX(), event.getY())) {
                        this.pointerId = event.getPointerId();
                        processMotionRequest(true, event.getX(), event.getY(), tpf);
                        characterHandler.setCharacterMotion(this);
                        consumed = true;
                    }
                    break;
                case UP:
                    if (this.pointerId != null && this.pointerId == event.getPointerId()) {
                        processMotionRequest(false, event.getX(), event.getY(), tpf);
                        this.pointerId = null;
                        consumed = true;
                    }
                    break;
                case MOVE:
                    if (this.pointerId != null && this.pointerId == event.getPointerId()) {
                        processMotionRequest(true, event.getX(), event.getY(), tpf);
                        consumed = true;
                    }
                    break;
                case TAP:
                    if (checkSelect(event.getX(), event.getY())) {
                        // block if tap occurred over dpad
                        consumed = true;
                    }
                    break;
                default:
                    break;
            }
        }

        return consumed;
    }

    private boolean checkSelect(float x, float y) {
        if (isEnabled() && dpadPicture != null) {
            return dpadPicture.checkSelect(x, y);
        } else {
            return false;
        }
    }

    private void processMotionRequest(boolean active, float x, float y, float tpf) {
        if (isEnabled()) {
//            logger.log(Level.INFO, "processMotionRequest for x:{0}, y:{1}, tpf: {2}",
//                    new Object[]{x, y, tpf});
            Vector2f locationRatio = dpadPicture.getLocationRatioFromCenter(x, y);
//            logger.log(Level.INFO, "dpad ratio: {0}", locationRatio.toString());
            if (characterControl != null) {
                if (active) {
                    dpadVector.set(-locationRatio.x, 0f, locationRatio.y);
                } else {
                    dpadVector.set(Vector3f.ZERO);
                }
            }
        }
    }

}
