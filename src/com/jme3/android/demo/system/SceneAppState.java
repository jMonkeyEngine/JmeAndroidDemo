/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.android.demo.system;

import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author Nehon
 */
public class SceneAppState extends AbstractAppState {

    private ViewPort viewPort;
    private Node rootNode;

    public SceneAppState(ViewPort viewPort, AssetManager assetManager) {
        this.viewPort = viewPort;       
        loadScene(assetManager);
        viewPort.attachScene(rootNode);
    }
 
    
    private void loadScene(AssetManager assetManager){
        rootNode = (Node)assetManager.loadModel("Scenes/Scene.j3o");        
        //ground = scene.getChild("Ground");    
        //  scene.updateModelBound();
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
        return rootNode;
    }
    
    
}
