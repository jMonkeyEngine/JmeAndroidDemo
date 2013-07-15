package com.jme3.android.demo.control;

import com.jme3.android.demo.camera.DemoCamera;
import com.jme3.android.demo.utils.physicsray.PhysicsRay;
import com.jme3.android.demo.utils.physicsray.PhysicsRayHelpers;
import com.jme3.android.demo.utils.physicsray.PhysicsRayResult;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.logging.Logger;

/**
 * KeepSpatialVisibleControl automatically rotates/zooms the camera to try to keep
 * the target spatial in view.  This is done by creating 2 PhysicsRays between
 * the spatial and the camera.  If only 1 ray is blocked, the camera rotates.
 * When both rays are blocked, the camera zooms in until the rays aren't blocked
 * anymore.
 *
 * @author iwgeric
 */
public class KeepSpatialVisibleControl extends AbstractControl {
    private static final Logger logger = Logger.getLogger(KeepSpatialVisibleControl.class.getName());

    private DemoCamera demoCamera = null;
    private Node objects = null;
    private Vector3f spatialLocation = new Vector3f();
    private Vector3f spatialOffset = new Vector3f();
    private Vector3f targetToCamera = new Vector3f();
    private Vector3f targetToCameraDirection = new Vector3f();
    private Vector3f leftLocation = new Vector3f();
    private Vector3f leftEnd = new Vector3f();
    private Vector3f targetToLeft = new Vector3f();
    private Vector3f rightLocation = new Vector3f();
    private Vector3f rightEnd = new Vector3f();
    private Vector3f targetToRight = new Vector3f();
    private PhysicsSpace physicsSpace = null;

    public KeepSpatialVisibleControl() {
    }

    public KeepSpatialVisibleControl(DemoCamera demoCamera) {
        this.demoCamera = demoCamera;
    }

    public KeepSpatialVisibleControl(DemoCamera demoCamera, Node objects) {
        this.demoCamera = demoCamera;
        this.objects = objects;
    }

    public void setCamera(DemoCamera demoCamera) {
        this.demoCamera = demoCamera;
    }

    public void setObjects(Node objects) {
        this.objects = objects;
    }

    public void setSpatialOffset(Vector3f offset) {
        spatialOffset.set(offset);
    }

    public void setPhysicsSpace(PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
    }

    public void keepTargetVisible(float tpf) {
        float rayOffset = 0.75f;

        float leftDistToCollision = 9999f;
        float rightDistToCollision = 9999f;
        boolean okToResetZoom = true;
        Camera camera = demoCamera.getCamera();

        spatialLocation.set(spatial.getWorldTranslation()).addLocal(spatialOffset);
        targetToCamera.set(camera.getLocation()).subtractLocal(spatialLocation);
        targetToCameraDirection.set(targetToCamera).normalizeLocal();
        float distToCamera = (camera.getLocation().subtract(spatialLocation)).length();

        // ray to left of camera
        leftLocation.set(camera.getLeft()).multLocal(rayOffset).addLocal(spatialLocation);
        leftEnd.set(camera.getLeft()).multLocal(rayOffset).addLocal(camera.getLocation());
        targetToLeft.set(leftEnd).subtractLocal(spatialLocation);
        float leftAngle = targetToCameraDirection.angleBetween(targetToLeft.normalize());

        PhysicsRay rayLeft = new PhysicsRay(leftLocation, leftEnd);
        PhysicsRayResult leftClosestResult =
                PhysicsRayHelpers.getClosestExcludedResult(physicsSpace, spatial, rayLeft);

        if (leftClosestResult != null) {
            okToResetZoom = false;
            leftDistToCollision = leftClosestResult.getDistance();
        }

        // ray to right of camera
        rightLocation.set(camera.getLeft()).negateLocal().multLocal(rayOffset).addLocal(spatialLocation);
        rightEnd.set(camera.getLeft()).negateLocal().multLocal(rayOffset).addLocal(camera.getLocation());
        targetToRight.set(rightEnd).subtractLocal(spatialLocation);
        float rightAngle = targetToCameraDirection.angleBetween(targetToRight.normalize());

        PhysicsRay rayRight = new PhysicsRay(rightLocation, rightEnd);
        PhysicsRayResult rightClosestResult =
                PhysicsRayHelpers.getClosestExcludedResult(physicsSpace, spatial, rayRight);

        if (rightClosestResult != null) {
            okToResetZoom = false;
            rightDistToCollision = rightClosestResult.getDistance();
        }


        if (leftDistToCollision < distToCamera && rightDistToCollision < distToCamera) {
            // do zoom
            okToResetZoom = false;
            demoCamera.autoZoom(Math.min(leftDistToCollision, rightDistToCollision)*tpf);
        } else if (leftDistToCollision < distToCamera) {
            // rotate right
//            logger.log(Level.INFO, "leftAngle: {0}", leftAngle);
            demoCamera.autoRotate(-1f * leftAngle/2);
        } else if (rightDistToCollision < distToCamera) {
            // rotate left
//            logger.log(Level.INFO, "rightAngle: {0}", rightAngle);
            demoCamera.autoRotate(1f * rightAngle/2);
        } else {
//            logger.log(Level.INFO, "CLEAR!!");
        }

        if (okToResetZoom) {
            demoCamera.resetAutoZoom();
        }
    }


    @Override
    protected void controlUpdate(float tpf) {
        if (demoCamera != null && objects != null && spatial != null && enabled) {
            keepTargetVisible(tpf);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
