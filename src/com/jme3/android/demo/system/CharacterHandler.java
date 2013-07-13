package com.jme3.android.demo.system;

import com.jme3.android.demo.control.CharacterAnimControl;
import com.jme3.android.demo.input.CharacterMotion;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
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
    private PhysicsSpace physicsSpace;
    private Vector3f lookAtOffset = new Vector3f();


    public CharacterHandler(Node characterNode, PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
        this.characterNode = characterNode;

        characterNode.setShadowMode(RenderQueue.ShadowMode.Cast);
        characterNode.getControl(SkeletonControl.class).setHardwareSkinningPreferred(true);
        characterNode.getControl(AnimControl.class).createChannel().setAnim("Idle");
        ((Geometry) characterNode.getChild(0)).setLodLevel(1);

        initPhysicsControl();
        initAnimControl();
    }

    private void initPhysicsControl() {
        if (characterNode.getWorldBound() instanceof BoundingBox) {
            BoundingBox bb = (BoundingBox)characterNode.getWorldBound();
            lookAtOffset.set(Vector3f.UNIT_Y.mult(bb.getYExtent()*2f));
            float radius = Math.max(bb.getXExtent(), bb.getZExtent());
            float height = bb.getYExtent();
            height = Math.max(height, radius*2.5f);
            float mass = 50f;
            charPhysicsControl = new BetterCharacterControl(radius, height, mass);
            charPhysicsControl.setViewDirection(characterNode.getWorldRotation().mult(Vector3f.UNIT_Z));
            charPhysicsControl.setJumpForce(Vector3f.UNIT_Y.mult(mass*2f));
            characterNode.addControl(charPhysicsControl);
            physicsSpace.add(charPhysicsControl);
            logger.log(Level.SEVERE, "Added mainCharacter {0} PhysicsControl", characterNode.getName());
            logger.log(Level.INFO, "Height: {0}, radius: {1}, mass: {2}",
                    new Object[]{height, radius, mass});
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

    public Vector3f getLookAtOffset() {
        return lookAtOffset;
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
