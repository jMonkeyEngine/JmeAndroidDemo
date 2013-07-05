package com.jme3.android.demo.system;

import com.jme3.android.demo.control.CharacterAnimControl;
import com.jme3.android.demo.input.CharacterMotion;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
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
    private static final Logger logger = Logger.getLogger(CharacterHandler.class.getName());

    private Node characterNode;
    private BetterCharacterControl charPhysicsControl;
    private CharacterAnimControl charAnimControl;
    private CharacterMotion motionControl;


    public CharacterHandler(Node characterNode) {
        this.characterNode = characterNode;

        characterNode.getControl(SkeletonControl.class).setHardwareSkinningPreferred(true);
        characterNode.getControl(AnimControl.class).createChannel().setAnim("Idle");
        ((Geometry) characterNode.getChild(0)).setLodLevel(1);

        initPhysicsControl();
        initAnimControl();
    }

    private void initPhysicsControl() {
        if (characterNode.getWorldBound() instanceof BoundingBox) {
            BoundingBox bb = (BoundingBox)characterNode.getWorldBound();
            float radius = Math.max(bb.getXExtent(), bb.getZExtent());
            float height = bb.getYExtent();
            height = Math.max(height, radius*2.5f);
            charPhysicsControl = new BetterCharacterControl(radius, height, 50f);
            charPhysicsControl.setViewDirection(characterNode.getWorldRotation().mult(Vector3f.UNIT_Z));
            characterNode.addControl(charPhysicsControl);
        } else {
            Logger.getLogger(CharacterHandler.class.getName()).log(Level.INFO,
                    "WorldBound is not a BoundingBox, Character Control not created.");
        }
    }

    private void initAnimControl() {
        charAnimControl = new CharacterAnimControl(charPhysicsControl);
        characterNode.addControl(charAnimControl);
    }

    public Node getModel() {
        return characterNode;
    }

    public BetterCharacterControl getCharPhysicsControl() {
        return charPhysicsControl;
    }

    public CharacterAnimControl getCharAnimControl() {
        return charAnimControl;
    }

    public void setCharacterMotion(CharacterMotion characterMotion) {
        if (characterMotion == null) {
//            logger.log(Level.INFO, "Setting CharacterMotion to null");
        } else {
//            logger.log(Level.INFO, "Setting CharacterMotion: {0}", characterMotion.getClass().getName());
        }
        this.motionControl = characterMotion;
    }

    public CharacterMotion getCharacterMotion() {
        return motionControl;
    }

}
