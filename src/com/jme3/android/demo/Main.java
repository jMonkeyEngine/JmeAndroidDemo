package com.jme3.android.demo;

import com.jme3.android.demo.input.RTSCameraHandler;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.TempVars;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {
  
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    private Node scene ;
    private Spatial ground ;

    @Override
    public void simpleInitApp() {
//             System.err.println("Count "+MemoryUtils.getDirectMemoryCount());
//         System.err.println("Total "+MemoryUtils.getDirectMemoryTotalCapacity());
//         System.err.println("Usage "+MemoryUtils.getDirectMemoryUsage());
  
       scene = (Node)assetManager.loadModel("Scenes/Scene.j3o");        
        flyCam.setMoveSpeed(50);
       // setDisplayStatView(false);
        //flyCam.setEnabled(true);
        
       
        ground = scene.getChild("Ground");
        cam.setLocation(new Vector3f(5.0244403f, 4.2122016f, -30.357338f));
        cam.setRotation(new Quaternion(0.112140924f, 0.15460506f, -0.01766564f, 0.98143244f));
        
        RTSCameraHandler camHandler = new RTSCameraHandler(cam, rootNode);
        camHandler.registerInputs(inputManager);

        Node jaime = (Node) assetManager.loadModel("Models/Jaime/JaimeOptimized.j3o");
        jaime.getControl(SkeletonControl.class).setHardwareSkinningPreferred(true);
        jaime.setLocalTranslation(new Vector3f(12.0908f, 0, -12.063316f));
        
        rootNode.attachChild(jaime);
         jaime.getControl(AnimControl.class).createChannel().setAnim("Idle");
            camHandler.lookAt(jaime.getWorldTranslation());
        ((Geometry)jaime.getChild(0)).setLodLevel(1);
        
        
        
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(1, -1, 1));
        rootNode.addLight(dl);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.5f));       
        rootNode.addLight(al);
        scene.updateModelBound();
//        System.out.println(scene.getWorldBound());
//        Spatial sky = scene.getChild("Sky");
//        sky.removeFromParent();
//        GeometryBatchFactory.optimize(scene);
//        scene.attachChild(sky);
//        
         rootNode.attachChild(scene);
        
    
//         System.err.println("Count "+MemoryUtils.getDirectMemoryCount());
//         System.err.println("Total "+MemoryUtils.getDirectMemoryTotalCapacity());
//         System.err.println("Usage "+MemoryUtils.getDirectMemoryUsage());
      
        
        inputManager.addListener(new TouchListener() {

            public void onTouch(String name, TouchEvent event, float tpf) {
                if(event.getType()==TouchEvent.Type.DOUBLETAP ){
                    stats = !stats;
                    setDisplayStatView(stats);
                }
            }
        }, "touch");
        inputManager.addMapping("touch", new TouchTrigger(TouchInput.ALL));
    } 
    boolean stats = true;

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }


    
    private Vector3f pick(float x, float y) {
        TempVars vars = TempVars.get();
        Vector2f v2 = vars.vect2d;
        v2.set(x, y);
        Vector3f origin = cam.getWorldCoordinates(v2, 0.0f, vars.vect1);
        Vector3f direction = cam.getWorldCoordinates(v2, 0.3f, vars.vect2);
        direction.subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        Vector3f contactPoint = null;
        ground.collideWith(ray, results);

        
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            contactPoint = closest.getContactPoint();         
        }

        vars.release();
        return contactPoint;

    }

   
}


