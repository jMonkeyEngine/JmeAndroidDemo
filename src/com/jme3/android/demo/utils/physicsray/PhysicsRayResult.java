package com.jme3.android.demo.utils.physicsray;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * A <code>PhysicsRayResult</code> represents a single collision instance
 * between a {@link PhysicsRay} and a {@link PhysicsCollisionObject}.
 * A collision can result at the front of the collison object or at the back
 * of the collision object or both.
 *
 * @author iwgeric
 */
public class PhysicsRayResult implements Comparable<PhysicsRayResult> {

    private Spatial spatial;
    private Vector3f contactPoint;
    private Vector3f contactNormal;
    private float distance;
    private PhysicsCollisionObject collisionObject;

    public PhysicsRayResult(PhysicsRay ray, PhysicsRayTestResult testResult) {
        this.collisionObject = testResult.getCollisionObject();
        Object object = collisionObject.getUserObject();
        if (object instanceof Spatial) {
            this.spatial = (Spatial)object;
        } else {
            this.spatial = null;
        }

        Vector3f rayVector = ray.getEndLocation().subtract(ray.getStartLocation());
        float rayLength = rayVector.length();
        distance = rayLength * testResult.getHitFraction();
        Vector3f rayDirection = rayVector.normalize();

        contactPoint = rayDirection.clone().multLocal(distance).addLocal(ray.getStartLocation());
        contactNormal = testResult.getHitNormalLocal();

    }

    public int compareTo(PhysicsRayResult other) {
        return Float.compare(distance, other.distance);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PhysicsRayResult){
            return ((PhysicsRayResult)obj).compareTo(this) == 0;
        }
        return super.equals(obj);
    }

    public Vector3f getContactPoint() {
        return contactPoint;
    }

    public Vector3f getContactNormal() {
        return contactNormal;
    }

    public float getDistance() {
        return distance;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public PhysicsCollisionObject getCollisionObject() {
        return collisionObject;
    }

}
