package com.jme3.android.demo.utils;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
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
                phyControl.setEnabled(false);
                if (phyControl.getPhysicsSpace() != null) {
                    phyControl.getPhysicsSpace().remove(control);
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


}
