package com.jme3.android.demo.utils;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.UserData;
import com.jme3.scene.control.Control;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class PhysicsHelpers {
    private static final Logger logger = Logger.getLogger(PhysicsHelpers.class.getName());

    public static PhysicsControl getPhysicsControl(Spatial spatial) {
        PhysicsControl physicsControl = spatial.getControl(PhysicsControl.class);

        while (physicsControl == null) {
            if (spatial.getParent() != null) {
                spatial = spatial.getParent();
                physicsControl = spatial.getControl(PhysicsControl.class);
            } else {
                break;
            }
        }

//        logger.log(Level.INFO, "PhysicsControl found on: {0}", spatial);
        return physicsControl;
    }

    public synchronized static void clearSpatial(Spatial spatial, boolean enableLogging) {
        if (enableLogging) {
            logger.log(Level.INFO, "clearSpatial: spatial: {0}", spatial.getName());
        }
        for (int i=0; i<spatial.getNumControls(); i++) {
            Control control = spatial.getControl(i);
            if (control instanceof PhysicsControl) {
                PhysicsControl phyControl = (PhysicsControl)control;
                if (phyControl.getPhysicsSpace() != null) {
                    if (enableLogging) {
                        logger.log(Level.INFO, "Removing control: {0} in PhysicsSpace: {1}",
                                new Object[]{control, phyControl.getPhysicsSpace()});
                    }
                    phyControl.getPhysicsSpace().remove(control);
                } else {
                    if (enableLogging) {
                        logger.log(Level.INFO, "PhysicsSpace was null for control: {0}", control);
                    }
                }
            }
            spatial.removeControl(control);
            if (enableLogging) {
                logger.log(Level.INFO, "clearSpatial: spatial: {0} had a Control removed: {1}",
                        new Object[]{spatial.getName(), control.getClass()});
            }
            control = null;
        }

        if (spatial instanceof Geometry) {
            Geometry geo = ((Geometry)spatial);
            ((Geometry)spatial).setMaterial(null);
        }
        spatial.removeFromParent();
        spatial = null;
    }

    public synchronized static void clearScene(Spatial spatial, final boolean enableLogging) {
        SceneGraphVisitorAdapter v = new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Node node) {
                clearSpatial(node, enableLogging);
            }
            @Override
            public void visit(Geometry geometry) {
                clearSpatial(geometry, enableLogging);
            }

        };

//        spatial.breadthFirstTraversal(v);
        spatial.depthFirstTraversal(v);
    }

    public synchronized static void createIndivMeshRigidBodies(
            final PhysicsSpace physicsSpace, final Spatial spatial, final float mass,
            final boolean enableLogging) {

        SceneGraphVisitorAdapter v = new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Node node) {
                // Skip creating rigid body of Nodes, only do the Geometries
                // Allows bullet to take advantage of broadphase
            }

            @Override
            public void visit(Geometry geometry) {
                Boolean bool = geometry.getUserData(UserData.JME_PHYSICSIGNORE);
                if (bool != null && bool.booleanValue()) {
                    logger.log(Level.INFO, "rigid body skipped for {0}", geometry.getName());
                    return;
                }

                CollisionShape colShape;
                if (mass > 0) {
                    colShape = CollisionShapeFactory.createDynamicMeshShape(geometry);
                } else {
                    colShape = CollisionShapeFactory.createMeshShape(geometry);
                }

                RigidBodyControl rigidBodyControl = new RigidBodyControl(colShape, mass);
                geometry.addControl(rigidBodyControl);
                physicsSpace.add(rigidBodyControl);
                logger.log(Level.INFO, "Added rigid body to {0}", geometry.getName());
                logger.log(Level.INFO, "Created Physics Control: {0}, in PhysicsSpace: {1}",
                        new Object[]{rigidBodyControl, physicsSpace});
            }

        };

//        spatial.breadthFirstTraversal(v);
        spatial.depthFirstTraversal(v);
    }

}
