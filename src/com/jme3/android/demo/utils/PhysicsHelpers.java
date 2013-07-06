package com.jme3.android.demo.utils;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.scene.Spatial;
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
}
