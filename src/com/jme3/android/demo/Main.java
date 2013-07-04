package com.jme3.android.demo;

import com.jme3.android.demo.camera.CameraHandler;
import com.jme3.android.demo.input.DpadCharacterMotion;
import com.jme3.android.demo.input.InputActionListener;
import com.jme3.android.demo.input.InputHandler;
import com.jme3.android.demo.system.CharacterHandler;
import com.jme3.android.demo.system.SceneAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.event.TouchEvent;

/**
 * Main application for JME3.0 android demo
 *
 * @author nehon
 */
public class Main extends SimpleApplication implements InputActionListener {

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
        dpadCharacterMotion.setCharacterControl(jaime.getCharPhysicsControl());
        dpadCharacterMotion.setCamera(cam);
        dpadCharacterMotion.setUseCameraRotation(true);
        stateManager.attach(dpadCharacterMotion);

        CameraHandler cameraHandler = new CameraHandler(cam);
        cameraHandler.setCameraMode(CameraHandler.CameraMode.CHASE, jaime.getModel());
        cameraHandler.init();

        InputHandler inputHandler = new InputHandler();
        // Kept assigning input listener in Main to control the order.
        // inputs are sent to the classes based on order added
        // each input class can consume the input to prevent remaining classes
        // from getting the input event

        /* character motion always has first priority over input events */
        inputHandler.addInputActionListener(dpadCharacterMotion);

        // TODO: Add sceneAppState input listener here when ready to be able to detect
        // object picking in the scene before the events get sent to the camera class

        inputHandler.addInputActionListener(this);

        /* camera control should always be last to collect events not handled elsewhere */
        inputHandler.addInputActionListener(cameraHandler);
        stateManager.attach(inputHandler);

    }

    public boolean onInputAction(TouchEvent event, float tpf) {
        boolean consumed = false;
        switch (event.getType()) {
            case DOUBLETAP:
                stats = !stats;
                setDisplayStatView(stats);
                consumed = false;  // no need to block others
                break;
            default:
                break;
        }
        return consumed;
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
