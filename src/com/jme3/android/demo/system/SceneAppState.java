package com.jme3.android.demo.system;

import com.jme3.android.demo.Main;
import com.jme3.android.demo.input.InputActionListener;
import com.jme3.android.demo.shadows.CheapShadowRenderer;
import com.jme3.android.demo.utils.physicsray.PhysicsRay;
import com.jme3.android.demo.utils.physicsray.PhysicsRayHelpers;
import com.jme3.android.demo.utils.physicsray.PhysicsRayResult;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Map;
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

        // preloadScene is done here to send the Android Bitmap textures
        //   to OpenGL and then recycle the Android Bitmap images
        // This helps remove game hesitations when bringing a texture into
        //   view for the first time since the image is already loaded to OpenGL
        app.getRenderManager().preloadScene(curScene.getWorldNode());

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
                PhysicsRay phyRay = PhysicsRayHelpers.getPhysicsRayForward(app.getCamera(), event.getX(), event.getY());
                PhysicsRayResult result = PhysicsRayHelpers.getClosestResult(bulletAppState.getPhysicsSpace(), phyRay);
//                logger.log(Level.INFO, "CLOSEST OBJECT: Spatial: {0}, CollisionObject: {1}",
//                        new Object[]{
//                            result.getSpatial().getName(),
//                            result.getCollisionObject().toString()
//                        });
                    if (result.getCollisionObject() instanceof RigidBodyControl) {
                        RigidBodyControl rigidBodyControl = (RigidBodyControl)result.getCollisionObject();
                        if (rigidBodyControl.getMass() > 0f) { // don't try to move static objects
                            Vector3f force = rigidBodyControl.getGravity().negate().mult(rigidBodyControl.getMass());
                            force.multLocal(0.5f);
//                            logger.log(Level.INFO, "gravity: {0}, mass: {1}, force: {2}",
//                                    new Object[]{rigidBodyControl.getGravity(), rigidBodyControl.getMass(), force});
                            rigidBodyControl.applyImpulse(force, Vector3f.ZERO);
                            consumed = true;
                        }
                    }

                break;
            default:
                break;
        }
        return consumed;
    }

}
