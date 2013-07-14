package com.jme3.android.demo.system;

import com.jme3.android.demo.utils.PhysicsHelpers;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class Scene {
    private static final Logger logger = Logger.getLogger(Scene.class.getName());

    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;

    private String worldFileName;
    private Node world;
    private String sceneNodeName = "Scene";
    private Node scene;
    private String navMeshName = "NavMesh";
    private Geometry navMesh;
    private String groundName = "Ground";
    private Spatial ground;
    private String otherObjectsNodeName = "SceneObjects";
    private Node otherObjects;
    private String mainCharacterName = "Jaime";
    private Node mainCharacter;



    public Scene() {
    }

    public void setWorldFileName(String worldFileName) {
        this.worldFileName = worldFileName;
    }

    public String getWorldFileName() {
        return worldFileName;
    }

    public Node getWorldNode() {
        return world;
    }

    public void setSceneNodeName(String sceneNodeName) {
        this.sceneNodeName = sceneNodeName;
    }

    public Node getSceneNode() {
        return scene;
    }

    public void setGroundNodeName(String groundName) {
        this.groundName = groundName;
    }

    public Spatial getGround() {
        return ground;
    }

    public void setNavMeshName(String navMeshName) {
        this.navMeshName = navMeshName;
    }

    public Geometry getNavMesh() {
        return navMesh;
    }

    public void setOtherObjectsNodeName(String otherObjectsNodeName) {
        this.otherObjectsNodeName = otherObjectsNodeName;
    }

    public Node getOtherObjects() {
        return otherObjects;
    }

    public void setMainCharNodeName(String mainCharacterName) {
        this.mainCharacterName = mainCharacterName;
    }

    public Node getMainCharacter() {
        return mainCharacter;
    }

    public void loadScene(AssetManager assetManager){
        this.assetManager = assetManager;

        // Load main j3o that includes the entire scene.
        world = (Node)assetManager.loadModel(worldFileName);
        if (world == null) {
            throw new IllegalArgumentException("world " + worldFileName + " did not load.");
        }

        // Get the static scene objects
        scene = (Node)world.getChild(sceneNodeName);
        if (scene == null) {
            throw new IllegalArgumentException("scene " + sceneNodeName + " was not found.");
        }

        // Get the NavMesh from the scene node
        navMesh = (Geometry)scene.getChild(navMeshName);
        if (navMesh == null) {
            throw new IllegalArgumentException("navMesh " + navMeshName + " was not found.");
        }
        // Get the Ground from the scene node
        ground = scene.getChild(groundName);
        if (ground == null) {
            throw new IllegalArgumentException("ground " + groundName + " was not found.");
        }

        // Get the node that contains dynamic objects placed around the scene
        otherObjects = (Node)world.getChild(otherObjectsNodeName);
        if (otherObjects == null) {
            logger.log(Level.SEVERE, "otherObjects {0} was not found", otherObjectsNodeName);
            otherObjects = new Node("EmptyOtherObjects");
        }

        // Get the main character node from the world node and create a CharacterHandler
        mainCharacter = (Node)world.getChild(mainCharacterName);
        if (mainCharacter == null) {
            throw new IllegalArgumentException("mainCharacter " + mainCharacterName + " was not found.");
        }

    }

    public void initScenePhysics(PhysicsSpace physicsSpace){
        this.physicsSpace = physicsSpace;

        // create mesh collision shape around static scene
        // NavMesh Geometry has JmePhysicsIgnore UserData so it will not
        //   be included in the collision shape
        PhysicsHelpers.createIndivMeshRigidBodies(physicsSpace, scene, 0f, true);

        // For each child in the otherObjects node, create a physics RigidBodyControl
        PhysicsHelpers.createIndivMeshRigidBodies(physicsSpace, otherObjects, 5f, true);

    }

    public void unloadScene() {
        PhysicsHelpers.clearScene(world, false);
    }

}
