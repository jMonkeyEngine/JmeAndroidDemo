package com.jme3.android.demo.input;

import com.jme3.android.demo.Main;
import com.jme3.android.demo.utils.SelectablePicture;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class DpadCharacterMotion extends AbstractAppState implements CharacterMotion {
    private static final Logger logger = Logger.getLogger(DpadCharacterMotion.class.getName());

    private AppStateManager stateManager = null;
    private Main app = null;
    private AssetManager assetManager = null;
    private Node guiNode = null;
    private Node rootNode = null;
    private AppSettings appSettings = null;

    private Node dpadNode = new Node("dpad");
    private SelectablePicture dpadPicture = null;
    private BetterCharacterControl characterControl = null;
    private float maxVelocity = 1f;

    public void setCharacterControl(BetterCharacterControl characterControl) {
        this.characterControl = characterControl;
    }

    private void initDpad() {
        dpadPicture = new SelectablePicture("Dpad", false);
        dpadPicture.setImage(assetManager, "Interface/Dpad.png", true);
        dpadPicture.setWidth(256f);
        dpadPicture.setHeight(256f);
        dpadNode.attachChild(dpadPicture);
        dpadNode.setLocalTranslation(0, 0, 0);
        setEnabled(true);
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
        super.setEnabled(enabled);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    public boolean checkSelect(float x, float y) {
        if (isEnabled() && dpadPicture != null) {
            return dpadPicture.checkSelect(x, y);
        } else {
            return false;
        }
    }

    public void processMotionRequest(boolean active, float x, float y, float tpf) {
        if (isEnabled()) {
            logger.log(Level.INFO, "processMotionRequest for x:{0}, y:{1}, tpf: {2}",
                    new Object[]{x, y, tpf});
            Vector2f locationRatio = dpadPicture.getLocationRatioFromCenter(x, y);
            logger.log(Level.INFO, "dpad ratio: {0}", locationRatio.toString());
            if (characterControl != null) {
                if (active) {
                    Vector3f walk = new Vector3f(maxVelocity,0,maxVelocity);
                    walk.z *= locationRatio.y;
                    walk.x *= -locationRatio.x;
                    characterControl.setWalkDirection(walk);
                    characterControl.setViewDirection(walk);
                } else {
                    characterControl.setWalkDirection(Vector3f.ZERO);
                }
            }
        }
    }

}
