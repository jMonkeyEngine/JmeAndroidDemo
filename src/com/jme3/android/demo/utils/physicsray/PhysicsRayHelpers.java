package com.jme3.android.demo.utils.physicsray;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class PhysicsRayHelpers {
    private static final Logger logger = Logger.getLogger(PhysicsRayHelpers.class.getName());

    /**
     * Creates a PhysicsRay from the screen location defined by x and y (in pixels)
     * @param cam Camera to use to get the 3D location from the screen location
     * @param x Screen location x value in Pixels
     * @param y Screen location y value in Pixels
     * @return PhysicsRay from the from Near Plane to Far Plane based on the x and y screen location.
     */
    public static PhysicsRay getPhysicsRayForward(Camera cam, float x, float y) {
        PhysicsRay ray = new PhysicsRay(cam, x, y);
        return ray;
    }

    /**
     * Creates a CollisionResults for all contacts within the ray provided
     * @param physicsSpace PhysicsSpace to check
     * @param ray PhysicsRay to use define the start and end of the check
     * @return CollisionResults containing all contacts within the ray provided
     */
    public static PhysicsRayResults getCollisionResults(PhysicsSpace physicsSpace, PhysicsRay ray) {
        PhysicsRayResults results = new PhysicsRayResults();

        List<PhysicsRayTestResult> testResults = physicsSpace.rayTest(ray.getStartLocation(), ray.getEndLocation());
        if (testResults.size() > 0) {
            for (PhysicsRayTestResult testResult: testResults) {
                PhysicsRayResult result = new PhysicsRayResult(ray, testResult);
                results.addResult(result);
            }
        }
        return results;
    }

    /**
     * Finds the closest contact
     * @param physicsSpace PhysicsSpace to check
     * @param ray PhysicsRay to use define the start and end of the check
     * @return Returns the closest result
     */
    public static PhysicsRayResult getClosestResult(PhysicsSpace physicsSpace, PhysicsRay ray) {
        PhysicsRayResults results = getCollisionResults(physicsSpace, ray);
        PhysicsRayResult closestResult = results.getClosestResult();

        return closestResult;
    }

    /**
     * Returns the closest result containing the spatials provided.  This method
     * returns the closest spatial provided even if there is a non-provided spatial
     * closer
     * @param physicsSpace PhysicsSpace to check
     * @param targetSpatials Node or Geometry containing the spatials interested in.
     * @param ray PhysicsRay to use define the start and end of the check
     * @return Returns the closest result matching the spatials provided even if there
     * is a different spatial closer than the spatials provided.
     */
    public static PhysicsRayResult getClosestTargetResult(PhysicsSpace physicsSpace, Spatial targetSpatials, PhysicsRay ray) {
        PhysicsRayResults results = getCollisionResults(physicsSpace, ray);

        if (results.size() > 0) {
            for (Iterator<PhysicsRayResult> it = results.iterator(); it.hasNext();) {
                PhysicsRayResult result = it.next();
                if (result.getSpatial() == null) {
                    logger.log(Level.INFO, "result spatial is null");
                    continue;
                } else {
                    logger.log(Level.INFO, "spatial found: {0}", result.getSpatial().getName());
                    Spatial resultSpatial = result.getSpatial();
                    if (resultSpatial instanceof Node) {
                        if (((Node)resultSpatial).hasChild(targetSpatials)) {
                            return result;
                        } else {
                            logger.log(Level.INFO, "Node {0} does not contain {1}",
                                    new Object[]{resultSpatial.getName(), targetSpatials.getName()});
                        }
                    } else {
                        if (resultSpatial.equals(targetSpatials)) {
                            return result;
                        } else {
                            logger.log(Level.INFO, "Geometry {0} is not {1}",
                                    new Object[]{resultSpatial.getName(), targetSpatials.getName()});
                        }
                    }
                }
            }
        } else {
            logger.log(Level.INFO, "No results found.");
        }
        return null;
    }

    /**
     * Returns the contact point in world coordinates from getClosestTargetResult
     * @param physicsSpace PhysicsSpace to check
     * @param targetSpatials Node or Geometry containing the spatials interested in.
     * @param ray PhysicsRay to use define the start and end of the check
     * @return Contact point Vector3f in world coordinates
     */
    public static Vector3f getClosestTargetContactPoint(PhysicsSpace physicsSpace, Spatial targetSpatials, PhysicsRay ray) {
        PhysicsRayResult result = getClosestTargetResult(physicsSpace, targetSpatials, ray);

        if (result != null) {
            return result.getContactPoint();
        }

        return null;
    }

    /**
     * Returns the closest result if it is one of the spatials provided
     * If the closest spatial is not part of the spatials provided, null is returned.
     * This means that something else is closer than spatials provided.
     * @param physicsSpace PhysicsSpace to check
     * @param targetSpatials Node or Geometry containing the spatials interested in.
     * @param ray PhysicsRay to use define the start and end of the check
     * @return Closest result if it contains one of the spatials provided. Null otherwise.
     */
    public static PhysicsRayResult getClosestFilteredResult(PhysicsSpace physicsSpace, Spatial targetSpatials, PhysicsRay ray) {
        PhysicsRayResult result = getClosestResult(physicsSpace, ray);

        if (result != null) {
            Spatial resultSpatial = result.getSpatial();
            if (targetSpatials instanceof Node) {
                if (((Node)targetSpatials).hasChild(resultSpatial)) {
                    return result;
                }
            } else {
                if (targetSpatials.equals(resultSpatial)) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Returns the contact point in world coordinates from getClosestFilteredResult
     * @param physicsSpace PhysicsSpace to check
     * @param targetSpatials Node or Geometry containing the spatials interested in.
     * @param ray PhysicsRay to use define the start and end of the check
     * @return Contact point Vector3f in world coordinates
     */
    public static Vector3f getClosestFilteredContactPoint(PhysicsSpace physicsSpace, Spatial targetSpatials, PhysicsRay ray) {
        PhysicsRayResult result = getClosestFilteredResult(physicsSpace, targetSpatials, ray);

        if (result != null) {
            return result.getContactPoint();
        }

        return null;
    }

    /**
     * Returns the closest contact result excluding the spatials provided.  If the
     * closest result is one of the spatials provided, it is skipped and the next
     * closest contact is returned.
     * @param physicsSpace PhysicsSpace to check
     * @param targetSpatials Node or Geometry containing the spatials interested in.
     * @param ray PhysicsRay to use define the start and end of the check
     * @return Closest result excluding any of the spatials provided.
     */
    public static PhysicsRayResult getClosestExcludedResult(PhysicsSpace physicsSpace, Spatial targetSpatials, PhysicsRay ray) {
        PhysicsRayResults results = getCollisionResults(physicsSpace, ray);
        PhysicsRayResult closestResult = null;

        if (results.size() > 0) {
            for (PhysicsRayResult result: results) {
                if (targetSpatials instanceof Node) {
                    if (((Node)targetSpatials).hasChild(result.getSpatial())) {
                        continue;
                    } else {
                        closestResult = result;
//                        logger.log(Level.INFO, "result: {0}, dist: {1}",
//                                new Object[]{result.getGeometry().getName(), result.getDistance()});
                        break;
                    }
                } else {
                    if (targetSpatials.equals(result.getSpatial())) {
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
