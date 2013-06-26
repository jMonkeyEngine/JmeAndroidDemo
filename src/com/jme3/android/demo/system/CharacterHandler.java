/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.android.demo.system;

import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author Nehon
 */
public class CharacterHandler {

    private Node model;

    public CharacterHandler(AssetManager assetManager, String path) {

        model = (Node) assetManager.loadModel(path);
        model.getControl(SkeletonControl.class).setHardwareSkinningPreferred(true);
        model.setLocalTranslation(new Vector3f(12.0908f, 0, -12.063316f));
        model.getControl(AnimControl.class).createChannel().setAnim("Idle");
        //    camHandler.lookAt(jaime.getWorldTranslation());
        ((Geometry) model.getChild(0)).setLodLevel(1);
    }

    public Node getModel() {
        return model;
    }
    
    
    
}
