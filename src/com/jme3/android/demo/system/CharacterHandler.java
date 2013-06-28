/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.android.demo.system;

import com.jme3.android.demo.control.CharacterAnimControl;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nehon
 */
public class CharacterHandler {

    private Node model;
    BetterCharacterControl charPhysicsControl;
    CharacterAnimControl charAnimControl;


    public CharacterHandler(AssetManager assetManager, String path) {

        model = (Node) assetManager.loadModel(path);
        model.getControl(SkeletonControl.class).setHardwareSkinningPreferred(true);
        model.setLocalTranslation(new Vector3f(12.0908f, 0, -12.063316f));
        model.getControl(AnimControl.class).createChannel().setAnim("Idle");
        //    camHandler.lookAt(jaime.getWorldTranslation());
        ((Geometry) model.getChild(0)).setLodLevel(1);

        initPhysicsControl();
        initAnimControl();
    }

    private void initPhysicsControl() {
        if (model.getWorldBound() instanceof BoundingBox) {
            BoundingBox bb = (BoundingBox)model.getWorldBound();
            float radius = Math.max(bb.getXExtent(), bb.getZExtent());
            float height = bb.getYExtent();
            height = Math.max(height, radius*2.5f);
            charPhysicsControl = new BetterCharacterControl(radius, height, 50f);
            model.addControl(charPhysicsControl);
        } else {
            Logger.getLogger(CharacterHandler.class.getName()).log(Level.INFO,
                    "WorldBound is not a BoundingBox, Character Control not created.");
        }
    }

    private void initAnimControl() {
        charAnimControl = new CharacterAnimControl(charPhysicsControl);
        model.addControl(charAnimControl);
    }

    public Node getModel() {
        return model;
    }

    public BetterCharacterControl getCharPhysicsControl() {
        return charPhysicsControl;
    }

    public CharacterAnimControl getCharAnimControl() {
        return charAnimControl;
    }


}
