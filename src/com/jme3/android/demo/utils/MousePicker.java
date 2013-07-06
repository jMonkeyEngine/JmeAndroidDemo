package com.jme3.android.demo.utils;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class MousePicker {
    private static final Logger logger = Logger.getLogger(MousePicker.class.getName());

    public static CollisionResults getCollisionResults(Spatial spatialsToCheck, Camera cam, float x, float y) {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = new Vector2f(x, y);
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        spatialsToCheck.collideWith(ray, results);

        return results;
    }

    public static CollisionResult getClosestResult(Spatial spatialsToCheck, Camera cam, float x, float y) {
        CollisionResults results = getCollisionResults(spatialsToCheck, cam, x, y);

        if (results.size() > 0) {
//            for (CollisionResult result: results) {
//                logger.log(Level.INFO, "distance: {0}, name: {1}",
//                        new Object[]{result.getDistance(), result.getGeometry().getName()});
//            }
            return results.getClosestCollision();
        }
        return null;
    }

    public static Vector3f getContactPoint(Spatial spatialsToCheck, Camera cam, float x, float y) {
        CollisionResult closestResult = getClosestResult(spatialsToCheck, cam, x, y);
        if (closestResult != null) {
            return closestResult.getContactPoint();
        }

        return null;
    }

    public static CollisionResult getFilteredClosestResult(Spatial spatialsToCheck, Spatial targetSpatials, Camera cam, float x, float y) {
        CollisionResult result = getClosestResult(spatialsToCheck, cam, x, y);

        if (result != null) {
            Geometry resultGeomety = result.getGeometry();
            if (targetSpatials instanceof Node) {
                if (((Node)targetSpatials).hasChild(resultGeomety)) {
                    return result;
                }
            } else {
                if (targetSpatials.equals(resultGeomety)) {
                    return result;
                }
            }
        }

        return null;
    }

    public static Geometry getFilteredClosestGeometry(Spatial spatialsToCheck, Spatial targetSpatials, Camera cam, float x, float y) {
        CollisionResult result = getClosestResult(spatialsToCheck, cam, x, y);

        if (result != null) {
            return result.getGeometry();
        }

        return null;
    }

    public static Vector3f getClosestFilteredContactPoint(Spatial spatialsToCheck, Spatial targetSpatials, Camera cam, float x, float y) {
        CollisionResult result = getFilteredClosestResult(spatialsToCheck, targetSpatials, cam, x, y);

        if (result != null) {
            return result.getContactPoint();
        }

        return null;
    }
}
