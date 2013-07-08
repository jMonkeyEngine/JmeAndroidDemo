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
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class PickingHelpers {
    private static final Logger logger = Logger.getLogger(PickingHelpers.class.getName());

    public static Ray getCameraRayForward(Camera cam, float x, float y) {
        Vector2f click2d = new Vector2f(x, y);
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        return new Ray(click3d, dir);
    }

    public static CollisionResults getCollisionResults(Spatial spatialsToCheck, Ray ray) {
        CollisionResults results = new CollisionResults();
        spatialsToCheck.collideWith(ray, results);

        return results;
    }

    public static CollisionResult getClosestResult(Spatial spatialsToCheck, Ray ray) {
        CollisionResults results = getCollisionResults(spatialsToCheck, ray);

//        if (results.size() > 0) {
//            for (CollisionResult result: results) {
//                logger.log(Level.INFO, "distance: {0}, name: {1}",
//                        new Object[]{result.getDistance(), result.getGeometry().getName()});
//            }
//        }
        return results.getClosestCollision();
    }

    public static Vector3f getClosestContactPoint(Spatial spatialsToCheck, Ray ray) {
        CollisionResult closestResult = getClosestResult(spatialsToCheck, ray);
        if (closestResult != null) {
            return closestResult.getContactPoint();
        }

        return null;
    }

    public static CollisionResult getClosestFilteredResult(Spatial spatialsToCheck, Spatial targetSpatials, Ray ray) {
        CollisionResult result = getClosestResult(spatialsToCheck, ray);

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

    public static Geometry getClosestFilteredGeometry(Spatial spatialsToCheck, Spatial targetSpatials, Ray ray) {
        CollisionResult result = getClosestFilteredResult(spatialsToCheck, targetSpatials, ray);

        if (result != null) {
            return result.getGeometry();
        }

        return null;
    }

    public static Vector3f getClosestFilteredContactPoint(Spatial spatialsToCheck, Spatial targetSpatials, Ray ray) {
        CollisionResult result = getClosestFilteredResult(spatialsToCheck, targetSpatials, ray);

        if (result != null) {
            return result.getContactPoint();
        }

        return null;
    }

    public static CollisionResult getClosestExcludedResult(Spatial spatialsToCheck, Spatial targetSpatials, Ray ray) {
        CollisionResults results = getCollisionResults(spatialsToCheck, ray);
        CollisionResult closestResult = null;

        spatialsToCheck.collideWith(ray, results);
        if (results.size() > 0) {
            for (CollisionResult result: results) {
                if (targetSpatials instanceof Node) {
                    if (((Node)targetSpatials).hasChild(result.getGeometry())) {
                        continue;
                    } else {
                        closestResult = result;
//                        logger.log(Level.INFO, "result: {0}, dist: {1}",
//                                new Object[]{result.getGeometry().getName(), result.getDistance()});
                        break;
                    }
                } else {
                    if (targetSpatials.equals(result.getGeometry())) {
                        continue;
                    } else {
                        closestResult = result;
//                        logger.log(Level.INFO, "result: {0}, dist: {1}",
//                                new Object[]{result.getGeometry().getName(), result.getDistance()});
                        break;
                    }
                }
            }
        }

        return closestResult;
    }


}
