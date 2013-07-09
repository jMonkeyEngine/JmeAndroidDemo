package com.jme3.android.demo.control;

import com.jme3.android.demo.camera.DemoCamera;
import com.jme3.android.demo.utils.PickingHelpers;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class KeepSpatialVisibleControl extends AbstractControl {
    private static final Logger logger = Logger.getLogger(KeepSpatialVisibleControl.class.getName());

    private DemoCamera demoCamera = null;
    private Node objects = null;
    private Vector3f spatialOffset = new Vector3f();

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

    public void keepTargetVisible(float tpf) {
        float rayOffset = 0.75f;

        float leftDistToCollision = 9999f;
        float rightDistToCollision = 9999f;
        boolean okToResetZoom = true;
        String leftGeometry = "null";
        String rightGeometry = "null";
        Camera camera = demoCamera.getCamera();
        Vector3f spatialLocation = spatial.getWorldTranslation().add(spatialOffset);

        Vector3f targetToCamera = camera.getLocation().subtract(spatialLocation);
        Vector3f targetToCameraDirection = targetToCamera.normalize();
        float distToCamera = targetToCamera.length();

        // ray to left of camera
        Vector3f leftLocation = new Vector3f(camera.getLeft()).multLocal(rayOffset).addLocal(spatialLocation);
        Vector3f leftEnd = new Vector3f(camera.getLeft()).multLocal(rayOffset).addLocal(camera.getLocation());
        Vector3f leftVector = new Vector3f(leftEnd.subtract(leftLocation));
        Vector3f targetToLeft = leftEnd.subtract(spatialLocation);
        float leftAngle = targetToCameraDirection.angleBetween(targetToLeft.normalize());

        Ray rayLeft = new Ray(leftLocation, leftVector.normalize());
        rayLeft.setLimit(leftVector.length() + rayOffset);

        CollisionResult leftClosestResult = PickingHelpers.getClosestExcludedResult(objects, spatial, rayLeft);
        if (leftClosestResult != null) {
            okToResetZoom = false;
            leftDistToCollision = leftClosestResult.getDistance();
            leftGeometry = leftClosestResult.getGeometry().getName();
        }

        // ray to right of camera
        Vector3f rightLocation = new Vector3f(camera.getLeft()).negateLocal().multLocal(rayOffset).addLocal(spatialLocation);
        Vector3f rightEnd = new Vector3f(camera.getLeft()).negateLocal().multLocal(rayOffset).addLocal(camera.getLocation());
        Vector3f rightVector = new Vector3f(rightEnd.subtract(rightLocation));
        Vector3f targetToRight = rightEnd.subtract(spatialLocation);
        float rightAngle = targetToCameraDirection.angleBetween(targetToRight.normalize());

        Ray rayRight = new Ray(rightLocation, rightVector.normalize());
        rayRight.setLimit(rightVector.length() + rayOffset);

        CollisionResult rightClosestResult = PickingHelpers.getClosestExcludedResult(objects, spatial, rayRight);
        if (rightClosestResult != null) {
            okToResetZoom = false;
            rightDistToCollision = rightClosestResult.getDistance();
            rightGeometry = rightClosestResult.getGeometry().getName();
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
