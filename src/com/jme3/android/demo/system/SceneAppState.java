/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.android.demo.system;

import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nehon
 */
public class SceneAppState extends AbstractAppState {

    private ViewPort viewPort;
    private Node rootNode = new Node("Root");
    private Node sceneNode;
    private BulletAppState bulletAppState;

    public SceneAppState(ViewPort viewPort, AssetManager assetManager, BulletAppState bulletAppState) {
        this.viewPort = viewPort;
        this.bulletAppState = bulletAppState;
        loadScene(assetManager);
        viewPort.attachScene(rootNode);
    }


    private void loadScene(AssetManager assetManager){
        sceneNode = (Node)assetManager.loadModel("Scenes/Scene.j3o");
        rootNode.attachChild(sceneNode);
        //ground = scene.getChild("Ground");
        //  scene.updateModelBound();

        CollisionShape sceneColShape = CollisionShapeFactory.createMeshShape(sceneNode);
        RigidBodyControl sceneRigidBodyControl = new RigidBodyControl(sceneColShape, 0f);
        sceneNode.addControl(sceneRigidBodyControl);
        bulletAppState.getPhysicsSpace().add(sceneRigidBodyControl);
    }

    public void addMainCharacter(CharacterHandler character){
        rootNode.attachChild(character.getModel());
    }

    @Override
    public void update(float tpf) {
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        viewPort.setEnabled(enabled);
    }

    public Node getScene() {
        return sceneNode;
    }


}
