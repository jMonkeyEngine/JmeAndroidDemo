package com.jme3.android.demo;

import com.jme3.android.demo.camera.CameraHandler;
import com.jme3.android.demo.input.DpadCharacterMotion;
import com.jme3.android.demo.input.InputHandler;
import com.jme3.android.demo.input.LocationInputListener.LocationType;
import com.jme3.android.demo.input.ValueInputListener.ValueType;
import com.jme3.android.demo.system.CharacterHandler;
import com.jme3.android.demo.system.SceneAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;

/**
 * Main application for JME3.0 android demo
 *
 * @author nehon
 */
public class Main extends SimpleApplication {

    private boolean stats = true;
    private SceneAppState sceneAppState;
    private BulletAppState bulletAppState = new BulletAppState();
    private float totalTime = 0f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public SceneAppState getSceneAppState() {
        return sceneAppState;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    @Override
    public void simpleInitApp() {
       // flyCam.setMoveSpeed(50);
        stateManager.detach(stateManager.getState(FlyCamAppState.class));

        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        sceneAppState = new SceneAppState();
        stateManager.attach(sceneAppState);

        CharacterHandler jaime = new CharacterHandler(assetManager, "Models/Jaime/JaimeOptimized.j3o");
        sceneAppState.addMainCharacter(jaime);
        bulletAppState.getPhysicsSpace().add(jaime.getCharPhysicsControl());

        DpadCharacterMotion dpadCharacterMotion = new DpadCharacterMotion();
        stateManager.attach(dpadCharacterMotion);
        dpadCharacterMotion.setCharacterControl(jaime.getCharPhysicsControl());

        CameraHandler cameraHandler = new CameraHandler(cam);
        cameraHandler.setCameraMode(CameraHandler.CameraMode.CHASE, jaime.getModel());
        cameraHandler.init();

        // Kept assigning input listener in Main to control the order.
        // inputs are sent to the classes based on order added
        // each input class can consume the input to prevent remaining classes
        // from getting the input event
        InputHandler inputHandler = new InputHandler();
        inputHandler.addLocationInputListener(dpadCharacterMotion,
                LocationType.DOWN, LocationType.UP, LocationType.MOVE);
        inputHandler.addValueInputListener(dpadCharacterMotion,
                ValueType.PINCH);

        // TODO: Add sceneAppState input listener here when ready to be able to detect
        // object picking in the scene before the events get sent to the camera class

        inputHandler.addLocationInputListener(cameraHandler,
                LocationType.DOWN, LocationType.UP);
        inputHandler.addValueInputListener(cameraHandler,
                ValueType.X_AXIS_DRAG, ValueType.Y_AXIS_DRAG, ValueType.PINCH);
        stateManager.attach(inputHandler);

        // TODO: convert to InputHandler listener
        inputManager.addListener(new TouchListener() {
            public void onTouch(String name, TouchEvent event, float tpf) {
                if (event.getType() == TouchEvent.Type.DOUBLETAP) {
                    stats = !stats;
                    setDisplayStatView(stats);
                }
            }
        }, "touch");
        inputManager.addMapping("touch", new TouchTrigger(TouchInput.ALL));
    }

    @Override
    public void simpleUpdate(float tpf) {
//        totalTime += tpf;
//        if (totalTime > 30) {
//            sceneAppState.setEnabled(true);
//        } else if (totalTime > 20) {
//            sceneAppState.setEnabled(false);
//        }
    }




//
//    private Vector3f pick(float x, float y) {
//        TempVars vars = TempVars.get();
//        Vector2f v2 = vars.vect2d;
//        v2.set(x, y);
//        Vector3f origin = cam.getWorldCoordinates(v2, 0.0f, vars.vect1);
//        Vector3f direction = cam.getWorldCoordinates(v2, 0.3f, vars.vect2);
//        direction.subtractLocal(origin).normalizeLocal();
//
//        Ray ray = new Ray(origin, direction);
//        CollisionResults results = new CollisionResults();
//        Vector3f contactPoint = null;
//        ground.collideWith(ray, results);
//
//
//        if (results.size() > 0) {
//            CollisionResult closest = results.getClosestCollision();
//            contactPoint = closest.getContactPoint();
//        }
//
//        vars.release();
//        return contactPoint;
//
//    }
}
