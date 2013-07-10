package com.jme3.android.demo;

import com.jme3.android.demo.camera.CameraHandler;
import com.jme3.android.demo.input.DpadCharacterMotion;
import com.jme3.android.demo.input.InputActionListener;
import com.jme3.android.demo.input.InputHandler;
import com.jme3.android.demo.input.NavMeshCharacterMotion;
import com.jme3.android.demo.system.SceneAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;

/**
 * Main application for JME3.0 android demo
 *
 * @author nehon
 */
public class Main extends SimpleApplication implements InputActionListener {

    private boolean stats = true;
    private SceneAppState sceneAppState;
    private BulletAppState bulletAppState = new BulletAppState();
    private InputHandler inputHandler = new InputHandler();
    private CameraHandler cameraHandler = new CameraHandler();
    private FilterPostProcessor fpp;
    private DepthOfFieldFilter dofFilter;
    private ParticleEmitter fire;

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

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public CameraHandler getCameraHandler() {
        return cameraHandler;
    }

    @Override
    public void simpleInitApp() {
        stateManager.detach(stateManager.getState(FlyCamAppState.class));

//        fpp = new FilterPostProcessor(assetManager);
//        //     fpp.setNumSamples(4);
//
//        dofFilter = new DepthOfFieldFilter();
//        dofFilter.setFocusDistance(0);
//        dofFilter.setFocusRange(100);
//        dofFilter.setBlurScale(1.0f);
//        fpp.addFilter(dofFilter);
//        viewPort.addProcessor(fpp);

//        fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
//        Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
//        mat_red.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
//        fire.setMaterial(mat_red);
//        fire.setImagesX(2); fire.setImagesY(2); // 2x2 texture animation
//        fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
//        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
//        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0,2,0));
//        fire.setStartSize(1.5f);
//        fire.setEndSize(0.1f);
//        fire.setGravity(0,0,0);
//        fire.setLowLife(0.5f);
//        fire.setHighLife(3f);
//        fire.getParticleInfluencer().setVelocityVariation(0.3f);
//        fire.setLocalTranslation(10.4140625f, 0.2f, 31.622173f);
//        rootNode.attachChild(fire);


        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        // sceneAppState attached first to make sure initialize runs before
        //    other app state initialize methods
        sceneAppState = new SceneAppState();
        stateManager.attach(sceneAppState);

        DpadCharacterMotion dpadCharacterMotion = new DpadCharacterMotion();
        dpadCharacterMotion.setCamera(cam);
        dpadCharacterMotion.setUseCameraRotation(true);
        stateManager.attach(dpadCharacterMotion);

        NavMeshCharacterMotion navMeshCharacterMotion = new NavMeshCharacterMotion();
        stateManager.attach(navMeshCharacterMotion);


        cameraHandler.setCamera(cam);
        cameraHandler.setCameraMode(CameraHandler.CameraMode.CHASE);

        // Kept assigning input listener in Main to control the order.
        // inputs are sent to the classes based on order added
        // each input class can consume the input to prevent remaining classes
        // from getting the input event

        /* dpad character motion always has first priority over input events */
        inputHandler.addInputActionListener(dpadCharacterMotion);

        // sceneAppState allows for selecting dynamic objects in the scene
        inputHandler.addInputActionListener(sceneAppState);

        inputHandler.addInputActionListener(this);

        // NavMesh navigation should be after sceneAppState to allow for object
        //   picking to take priority over moving the character to a new location
        inputHandler.addInputActionListener(navMeshCharacterMotion);

        /* camera control should always be last to collect events not handled elsewhere */
        inputHandler.addInputActionListener(cameraHandler);

        // attach inputHandler last so that all the other appstates get initialized first
        stateManager.attach(inputHandler);

    }

    public boolean onInputAction(TouchEvent event, float tpf) {
        boolean consumed = false;
        switch (event.getType()) {
            case TAP:
//                dofFilter.setEnabled(!dofFilter.isEnabled());
//                fire.setEnabled(!fire.isEnabled());
                consumed = false;  // no need to block others
                break;
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
