package com.jme3.android.demo;

import com.jme3.android.demo.input.DpadCharacterMotion;
import com.jme3.android.demo.input.InputHandler;
import com.jme3.android.demo.input.RTSCameraHandler;
import com.jme3.android.demo.system.CharacterHandler;
import com.jme3.android.demo.system.SceneAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;

/**
 * Main application for JME3.0 android demo
 *
 * @author nehon
 */
public class Main extends SimpleApplication {

    private boolean stats = true;
    private BulletAppState bulletAppState = new BulletAppState();

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       // flyCam.setMoveSpeed(50);
        stateManager.detach(stateManager.getState(FlyCamAppState.class));

        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        SceneAppState sceneAppState = new SceneAppState(viewPort, assetManager, bulletAppState);
        stateManager.attach(sceneAppState);

        RTSCameraHandler camHandler = new RTSCameraHandler(cam, sceneAppState.getScene());
        camHandler.registerInputs(inputManager);

        CharacterHandler jaime = new CharacterHandler(assetManager, "Models/Jaime/JaimeOptimized.j3o");
        sceneAppState.addMainCharacter(jaime);
        bulletAppState.getPhysicsSpace().add(jaime.getCharPhysicsControl());

        camHandler.lookAt(jaime.getModel().getWorldTranslation());

        DpadCharacterMotion dpadCharacterMotion = new DpadCharacterMotion();
        stateManager.attach(dpadCharacterMotion);

        InputHandler inputHandler = new InputHandler();
        inputHandler.setCharacterMotion(dpadCharacterMotion);
        stateManager.attach(inputHandler);

        dpadCharacterMotion.setCharacterControl(jaime.getCharPhysicsControl());

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
