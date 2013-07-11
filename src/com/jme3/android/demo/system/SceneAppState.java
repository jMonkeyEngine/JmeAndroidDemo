package com.jme3.android.demo.system;

import com.jme3.android.demo.Main;
import com.jme3.android.demo.input.InputActionListener;
import com.jme3.android.demo.shadows.CheapShadowRenderer;
import com.jme3.android.demo.utils.PhysicsHelpers;
import com.jme3.android.demo.utils.PickingHelpers;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nehon / iwgeric
 */
public class SceneAppState extends AbstractAppState implements InputActionListener {
    private static final Logger logger = Logger.getLogger(SceneAppState.class.getName());

    private Main app;
    private BulletAppState bulletAppState;
    private AssetManager assetManager;
    private Node rootNode;
//    private IntMap<Scene> scenes = new IntMap<Scene>();
    private Map<String, Scene> scenes = new HashMap<String, Scene>();

    private Scene curScene;
    private boolean sceneLoaded = false;

    private CharacterHandler characterHandler;




    public SceneAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (Main)app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        this.bulletAppState = this.app.getBulletAppState();

        CheapShadowRenderer shadows = new CheapShadowRenderer(assetManager);
        app.getViewPort().addProcessor(shadows);
        rootNode.attachChild(shadows.getShadowNode());

        super.initialize(stateManager, app);
    }

    public void addScene(Scene scene) {
        scenes.put(scene.getWorldFileName(), scene);
    }

    public void loadScene(String worldFileName){
        sceneLoaded = false;
        if (curScene != null) {
            unloadCurScene();
        }

        curScene = scenes.get(worldFileName);
        if (curScene == null) {
            throw new IllegalArgumentException("Scene " + worldFileName + " does not exist");
        }
        curScene.loadScene(assetManager);
        curScene.initScenePhysics(bulletAppState.getPhysicsSpace());

        characterHandler = new CharacterHandler(
                (Node)curScene.getMainCharacter().getChild(0),
                bulletAppState.getPhysicsSpace());

        this.rootNode.attachChild(curScene.getWorldNode());

        sceneLoaded = true;

    }

    public void unloadCurScene() {
        curScene.unloadScene();

        sceneLoaded = false;
    }

    public Node getWorldNode() {
        if (curScene != null) {
            return curScene.getWorldNode();
        }
        return null;
    }

    public Node getSceneNode() {
        if (curScene != null) {
            return curScene.getSceneNode();
        }
        return null;
    }

    public Spatial getGroundNode() {
        if (curScene != null) {
            return curScene.getGround();
        }
        return null;
    }

    public Geometry getNavMesh() {
        if (curScene != null) {
            return curScene.getNavMesh();
        }
        return null;
    }

    public Node getOtherObjects() {
        if (curScene != null) {
            return curScene.getOtherObjects();
        }
        return null;
    }

    public CharacterHandler getMainCharacter() {
        if (characterHandler != null) {
            return characterHandler;
        }
        return null;
    }

    public boolean isLoaded() {
        return sceneLoaded;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            rootNode.attachChild(curScene.getWorldNode());
        } else {
            curScene.getWorldNode().removeFromParent();
        }
        if (bulletAppState != null) {
            bulletAppState.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    public boolean onInputAction(TouchEvent event, float tpf) {
        boolean consumed = false;

        if (!isEnabled()) {
            return false;
        }

        switch (event.getType()) {
            case TAP:
                Ray ray = PickingHelpers.getCameraRayForward(app.getCamera(), event.getX(), event.getY());
                Geometry geometry = PickingHelpers.getClosestFilteredGeometry(
                        curScene.getWorldNode(), curScene.getOtherObjects(), ray);

                if (geometry != null) {
                    logger.log(Level.INFO, "Dynamic Object Selected: {0}", geometry.getName());
                    PhysicsControl physicsControl = PhysicsHelpers.getPhysicsControl(geometry);
                    if (physicsControl != null) {
//                        logger.log(Level.INFO, "PhysicsControl found: {0}", physicsControl.getClass().getName());
                        if (physicsControl instanceof RigidBodyControl) {
                            RigidBodyControl rigidBodyControl = (RigidBodyControl)physicsControl;
                            Vector3f force = rigidBodyControl.getGravity().negate().mult(rigidBodyControl.getMass());
                            force.multLocal(0.5f);
//                            logger.log(Level.INFO, "gravity: {0}, mass: {1}, force: {2}",
//                                    new Object[]{rigidBodyControl.getGravity(), rigidBodyControl.getMass(), force});
                            ((RigidBodyControl)physicsControl).applyImpulse(force, Vector3f.ZERO);
                            consumed = true;
                        }
                    } else {
                        logger.log(Level.INFO, "Geometry and all parents do not have a PhysicsControl");
                    }
                }
                break;
            default:
                break;
        }
        return consumed;
    }

}
