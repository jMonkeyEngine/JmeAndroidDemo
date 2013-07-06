package com.jme3.android.demo.system;

import com.jme3.android.demo.Main;
import com.jme3.android.demo.camera.CameraHandler;
import com.jme3.android.demo.input.InputActionListener;
import com.jme3.android.demo.utils.MousePicker;
import com.jme3.android.demo.utils.PhysicsHelpers;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
    private Node worldNode;
    private Node sceneNode;
    private CharacterHandler mainCharacter;
    private Geometry navMesh;
    private Spatial navMeshTargetMarker;
    private CameraHandler cameraHandler;
    private Node groundNode;
    private Node sceneDynamicObjects;

    public SceneAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (Main)app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        this.bulletAppState = this.app.getBulletAppState();

        loadScene();
        initCamera();

        super.initialize(stateManager, app);
    }

    private void loadScene(){
        if (worldNode != null) {
            worldNode.removeFromParent();
        }
        worldNode = (Node)assetManager.loadModel("Scenes/World1.j3o");

        Node mainCharacterNode = (Node)worldNode.getChild("Jaime");
        mainCharacter = new CharacterHandler((Node)mainCharacterNode.getChild(0));
        bulletAppState.getPhysicsSpace().add(mainCharacter.getCharPhysicsControl());

        sceneNode = (Node)worldNode.getChild("Scene");
        // create mesh collision shape around scene
        // NavMesh Geometry has JmePhysicsIgnore UserData so it will not
        //   be included in the collision shape
        CollisionShape sceneColShape = CollisionShapeFactory.createMeshShape(sceneNode);
        RigidBodyControl sceneRigidBodyControl = new RigidBodyControl(sceneColShape, 0f);
        sceneNode.addControl(sceneRigidBodyControl);
        bulletAppState.getPhysicsSpace().add(sceneRigidBodyControl);

        navMesh = (Geometry)sceneNode.getChild("NavMesh");

        navMeshTargetMarker = worldNode.getChild("NavMeshTargetMarker");
        groundNode = (Node)worldNode.getChild("Ground");

        sceneDynamicObjects = (Node)worldNode.getChild("SceneObjects");
        Spatial box = sceneDynamicObjects.getChild("jME_Box");

//        BoundingBox bb = (BoundingBox)box.getWorldBound();
//        BoxCollisionShape colBox = new BoxCollisionShape(bb.getExtent(null));
//        RigidBodyControl phyBox = new RigidBodyControl(colBox, 5f);

        RigidBodyControl phyBox = new RigidBodyControl(5f);

        box.addControl(phyBox);
        bulletAppState.getPhysicsSpace().add(phyBox);

        this.rootNode.attachChild(worldNode);
    }

    private void initCamera() {
        cameraHandler = this.app.getCameraHandler();
        cameraHandler.setTarget(mainCharacter.getModel());
        cameraHandler.init();
    }

    public Node getWorldNode() {
        return worldNode;
    }

    public Node getSceneNode() {
        return sceneNode;
    }

    public Node getGroundNode() {
        return groundNode;
    }

    public Geometry getNavMesh() {
        return navMesh;
    }

    public CharacterHandler getMainCharacter() {
        return mainCharacter;
    }

    public Spatial getNavMeshTargetMarker() {
        return navMeshTargetMarker;
    }

    public Node getSceneDynamicObjects() {
        return sceneDynamicObjects;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            rootNode.attachChild(worldNode);
        } else {
            worldNode.removeFromParent();
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
                Geometry geometry = MousePicker.getClosestFilteredGeometry(
                        worldNode, sceneDynamicObjects, app.getCamera(),
                        event.getX(), event.getY());

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
