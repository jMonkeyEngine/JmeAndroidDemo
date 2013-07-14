package com.jme3.android.demo;

import com.jme3.android.demo.camera.CameraHandler;
import com.jme3.android.demo.input.DpadCharacterMotion;
import com.jme3.android.demo.input.InputActionListener;
import com.jme3.android.demo.input.InputHandler;
import com.jme3.android.demo.input.NavMeshCharacterMotion;
import com.jme3.android.demo.system.Scene;
import com.jme3.android.demo.system.SceneAppState;
import com.jme3.android.demo.utils.OtherUtils;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.input.event.TouchEvent;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FadeFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application for JME3.0 android demo
 *
 * @author nehon
 */
public class Main extends SimpleApplication implements InputActionListener {
    private final static Logger logger = Logger.getLogger(Main.class.getName());

    private boolean stats = true;
    private SceneAppState sceneAppState;
    private BulletAppState bulletAppState = new BulletAppState();
    private InputHandler inputHandler = new InputHandler();
    private CameraHandler cameraHandler = new CameraHandler();
    private DpadCharacterMotion dpadCharacterMotion;
    private NavMeshCharacterMotion navMeshCharacterMotion;

    private FilterPostProcessor fpp;
    private DepthOfFieldFilter dofFilter;
    private ParticleEmitter fire;
    private FadeFilter fadeFilter;

    private float totalTime = 0f;
    private boolean sceneNeedsLoading = false;
    private boolean startFadeIn = false;
    private float fadeDelay = -1f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    @Override
    public void simpleInitApp() {
        stateManager.detach(stateManager.getState(FlyCamAppState.class));

        fpp = new FilterPostProcessor(assetManager);
        //     fpp.setNumSamples(4);
        viewPort.addProcessor(fpp);

        fadeFilter = new FadeFilter(3f);
        fadeFilter.setValue(0);
        fadeFilter.setEnabled(true);
        fpp.addFilter(fadeFilter);


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


        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        // sceneAppState attached first to make sure initialize runs before
        //    other app state initialize methods
        sceneAppState = new SceneAppState();
        stateManager.attach(sceneAppState);

        Scene newScene;
        // Repeat for each scene
        // Call setxxx methods to change the node/geometry names of the children of the scene
        newScene = new Scene();
        newScene.setWorldFileName("Scenes/World1.j3o");
        sceneAppState.addScene(newScene);

        dpadCharacterMotion = new DpadCharacterMotion();
        dpadCharacterMotion.setCamera(cam);
        dpadCharacterMotion.setUseCameraRotation(true);
        stateManager.attach(dpadCharacterMotion);

        navMeshCharacterMotion = new NavMeshCharacterMotion();
        stateManager.attach(navMeshCharacterMotion);

        cameraHandler = new CameraHandler();
        cameraHandler.setCamera(cam);

        inputHandler = new InputHandler();
        stateManager.attach(inputHandler);

        // Kept assigning input listeners in Main to control the order.
        // Inputs are sent to the classes based on order added
        // Each listener can consume the input to prevent remaining listeners
        //   from getting the input event

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

        sceneNeedsLoading = true;

    }

    // normally this method would be called from the UIF when the user hits
    //   a "play" type button and passing the name of the world j3o file to load.
    private void loadScene(String worldFileName) {
        sceneAppState.loadScene(worldFileName);

        dpadCharacterMotion.setCharacterHandler(sceneAppState.getMainCharacter());

        navMeshCharacterMotion.setCharacterHandler(sceneAppState.getMainCharacter());
        navMeshCharacterMotion.setNavMesh(sceneAppState.getNavMesh());
        navMeshCharacterMotion.setWorldNode(sceneAppState.getWorldNode());
        navMeshCharacterMotion.setGroundNode(sceneAppState.getGroundNode());

        cameraHandler.setCameraMode(CameraHandler.CameraMode.CHASE);
        cameraHandler.setTarget(sceneAppState.getMainCharacter().getModel());
        cameraHandler.setLookAtOffset(sceneAppState.getMainCharacter().getLookAtOffset());
        cameraHandler.init();
        cameraHandler.enableKeepCharVisible(bulletAppState.getPhysicsSpace(), sceneAppState.getWorldNode());
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
        totalTime += tpf;

        // This section of code repeats after 30 seconds
        // Used for debug only
        if (totalTime > 30) {
            totalTime = 0f;
            // uncomment this code to automatically load the scene after 30sec
//            if (!sceneAppState.isLoaded()) {
//                sceneNeedsLoading = true;
//            }

            // uncomment this code to automatically enable and attach the scene after 30sec
//            sceneAppState.setEnabled(true);

            // uncomment this code to fade in the scene after 30sec
//            fadeFilter.fadeIn();


        } else if (totalTime > 20) {
            // uncomment this code to automatically unload the scene after 20sec
//            if (sceneAppState.isLoaded()) {
//                sceneAppState.unloadCurScene();
//            }

            // uncomment this code to automatically disable and detatch the scene after 20sec
//            sceneAppState.setEnabled(false);

            // uncomment this code to fade out the scene after 20sec
//            fadeFilter.fadeOut();

        }

        if (startFadeIn) {
            // need a slight delay due to first frame having a high fps which
            //   causes the fadeFilter to be too bright on the first frame
            if (fadeDelay < 0) {
                fadeFilter.fadeIn();
                startFadeIn = false;
            }
            fadeDelay -= tpf;
        }

        if (sceneNeedsLoading) {
//            OtherUtils.printMemoryUsed("Before Load Scene: ");
            loadScene("Scenes/World1.j3o");
            sceneNeedsLoading = false;
            startFadeIn = true;
            fadeDelay = 0.01f;
//            OtherUtils.printMemoryUsed("After Load Scene: ");
        }

    }

    @Override
    public void reshape(int w, int h){
        super.reshape(w, h);

        // TODO: add code here to adjust guiNode objects
        // dpadCharacterMotion.reshape(w, h);

        // TODO: add code here to adjust frustum based on landscape or portrait
        // maybe use cam.setFrustumPerspective, but we'll have to initially
        // create the initial frustum so we can adjust the foy for each orientation
    }


}
