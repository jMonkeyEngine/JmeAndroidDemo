package com.jme3.android.demo.system;

import com.jme3.android.demo.Main;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nehon / iwgeric
 */
public class SceneAppState extends AbstractAppState {
    private static final Logger logger = Logger.getLogger(SceneAppState.class.getName());

    private Main app;
    private BulletAppState bulletAppState;
    private AssetManager assetManager;
    private Node rootNode;
    private Node worldNode = new Node("World");
    private Node sceneNode;
    private CharacterHandler mainCharacter;

    public SceneAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (Main)app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        this.bulletAppState = this.app.getBulletAppState();

        this.rootNode.attachChild(worldNode);
        loadScene();

        super.initialize(stateManager, app);
    }

    private void loadScene(){
        sceneNode = (Node)assetManager.loadModel("Scenes/Scene.j3o");
        worldNode.attachChild(sceneNode);
        //ground = scene.getChild("Ground");
        //  scene.updateModelBound();

        CollisionShape sceneColShape = CollisionShapeFactory.createMeshShape(sceneNode);
        RigidBodyControl sceneRigidBodyControl = new RigidBodyControl(sceneColShape, 0f);
        sceneNode.addControl(sceneRigidBodyControl);
        bulletAppState.getPhysicsSpace().add(sceneRigidBodyControl);

    }

    public void addMainCharacter(CharacterHandler mainCharacter){
        if (mainCharacter != null) {
            mainCharacter.getModel().removeFromParent();
        }
        this.mainCharacter = mainCharacter;
        worldNode.attachChild(mainCharacter.getModel());
    }

    public Node getScene() {
        return sceneNode;
    }



    @Override
    public void update(float tpf) {
    }

    @Override
    public void setEnabled(boolean enabled) {
//        if (enabled && isEnabled()) {
//            return;
//        }
//        if (!enabled && !isEnabled()) {
//            return;
//        }
//        logger.log(Level.INFO, "setEnabled: {0}", enabled);
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

}
