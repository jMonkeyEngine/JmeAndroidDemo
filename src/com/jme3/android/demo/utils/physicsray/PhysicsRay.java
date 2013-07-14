package com.jme3.android.demo.utils.physicsray;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author iwgeric
 */
public class PhysicsRay {
    private Vector3f startLocation = new Vector3f();
    private Vector3f endLocation = new Vector3f();

    public PhysicsRay(Camera cam, float x, float y) {
        Vector2f click2d = new Vector2f(x, y);
        this.startLocation.set(cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 0f));
        this.endLocation.set(cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f));
    }

    public PhysicsRay(Vector3f startLocation, Vector3f direction, float length) {
        this.startLocation.set(startLocation);
        if (!direction.isUnitVector()) {
            this.endLocation.set(direction.normalize()).multLocal(length).addLocal(startLocation);
        } else {
            this.endLocation.set(direction).multLocal(length).addLocal(startLocation);
        }
    }

    public PhysicsRay(Vector3f startLocation, Vector3f endLocation) {
        this.startLocation.set(startLocation);
        this.endLocation.set(endLocation);
    }

    public Vector3f getStartLocation() {
        return startLocation;
    }

    public Vector3f getEndLocation() {
        return endLocation;
    }

}
