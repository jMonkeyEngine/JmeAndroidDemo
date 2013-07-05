package com.jme3.android.demo.utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import java.util.logging.Logger;

public class GeometryUtils {
    private static final Logger logger = Logger.getLogger(GeometryUtils.class.getName());

    public synchronized static void createOriginTriad(String name, Spatial target, float length, float width, AssetManager assetManager) {
        Vector3f start = new Vector3f(target.getLocalTranslation());
        Vector3f end = new Vector3f(start.add(Vector3f.UNIT_XYZ.mult(length)));
        Quaternion localRot = new Quaternion(target.getLocalRotation());
        Vector3f rotatedEnd = localRot.mult(end);

//        Mesh lineX = new Line(start, start.add(Vector3f.UNIT_X.mult(length)));
        Vector3f endX = new Vector3f(start);
        endX.x = rotatedEnd.x;
        Mesh lineX = new Line(start, endX);
        lineX.setLineWidth(width);
        Geometry geoX = new Geometry(name + "X", lineX);
        Material matX = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matX.setColor("Color", ColorRGBA.Red);
        geoX.setMaterial(matX);

//        Mesh lineY = new Line(start, start.add(Vector3f.UNIT_Y.mult(length)));
        Vector3f endY = new Vector3f(start);
        endY.y = rotatedEnd.y;
        Mesh lineY = new Line(start, endY);
        lineY.setLineWidth(width);
        Geometry geoY = new Geometry(name + "Y", lineY);
        Material matY = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matY.setColor("Color", ColorRGBA.Green);
        geoY.setMaterial(matY);

//        Mesh lineZ = new Line(start, start.add(Vector3f.UNIT_Z.mult(length)));
        Vector3f endZ = new Vector3f(start);
        endZ.z = rotatedEnd.z;
        Mesh lineZ = new Line(start, endZ);
        lineZ.setLineWidth(width);
        Geometry geoZ = new Geometry(name + "Z", lineZ);
        Material matZ = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matZ.setColor("Color", ColorRGBA.Blue);
        geoZ.setMaterial(matZ);

        if (target.getParent() != null) {
            target.getParent().attachChild(geoX);
            target.getParent().attachChild(geoY);
            target.getParent().attachChild(geoZ);
        } else {
            if (target instanceof Node) {
                ((Node)target).attachChild(geoX);
                ((Node)target).attachChild(geoY);
                ((Node)target).attachChild(geoZ);
            }

        }

    }

    public synchronized static void createLocationTriad(String name, Vector3f target, Node node, float length, float width, AssetManager assetManager) {
        Vector3f start = target;
        Vector3f end = start.add(Vector3f.UNIT_XYZ.mult(length));
        Quaternion localRot = new Quaternion(0, 0, 0, 1);
        Vector3f rotatedEnd = localRot.mult(end);

//        Mesh lineX = new Line(start, start.add(Vector3f.UNIT_X.mult(length)));
        Vector3f endX = new Vector3f(start);
        endX.x = rotatedEnd.x;
        Mesh lineX = new Line(start, endX);
        lineX.setLineWidth(width);
        Geometry geoX = new Geometry(name + "X", lineX);
        Material matX = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matX.setColor("Color", ColorRGBA.Red);
        geoX.setMaterial(matX);

//        Mesh lineY = new Line(start, start.add(Vector3f.UNIT_Y.mult(length)));
        Vector3f endY = new Vector3f(start);
        endY.y = rotatedEnd.y;
        Mesh lineY = new Line(start, endY);
        lineY.setLineWidth(width);
        Geometry geoY = new Geometry(name + "Y", lineY);
        Material matY = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matY.setColor("Color", ColorRGBA.Green);
        geoY.setMaterial(matY);

//        Mesh lineZ = new Line(start, start.add(Vector3f.UNIT_Z.mult(length)));
        Vector3f endZ = new Vector3f(start);
        endZ.z = rotatedEnd.z;
        Mesh lineZ = new Line(start, endZ);
        lineZ.setLineWidth(width);
        Geometry geoZ = new Geometry(name + "Z", lineZ);
        Material matZ = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matZ.setColor("Color", ColorRGBA.Blue);
        geoZ.setMaterial(matZ);

        ((Node)node).attachChild(geoX);
        ((Node)node).attachChild(geoY);
        ((Node)node).attachChild(geoZ);

    }

}
