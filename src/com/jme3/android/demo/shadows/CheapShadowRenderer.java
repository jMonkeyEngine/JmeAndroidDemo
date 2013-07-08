/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.android.demo.shadows;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.FrameBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Nehon
 */
public class CheapShadowRenderer implements SceneProcessor {

    private ViewPort vp;
    private Map<Geometry, Geometry> shadows = new ConcurrentHashMap<Geometry, Geometry>();
    private List<Geometry> shadowPool = new ArrayList<Geometry>();
    private Material shadowMaterial; 
    private Node shadowNode = new Node("Shadows");
    private Mesh q;

    public CheapShadowRenderer(AssetManager assetManager) {
        shadowMaterial = assetManager.loadMaterial("Materials/Shadow/IndivShadow.j3m");
        createQuad();
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        this.vp = vp;
        
    }

    public void reshape(ViewPort vp, int w, int h) {
    }

    public boolean isInitialized() {
        return vp != null;
    }

    public void preFrame(float tpf) {
    }

    public void postQueue(RenderQueue rq) {

        shadowNode.detachAllChildren();

        GeometryList list2 = rq.getShadowQueueContent(RenderQueue.ShadowMode.Cast);
        for (int i = 0; i < list2.size(); i++) {
            Geometry g = list2.get(i);
            Geometry shadow = getShadow(g);          
            shadowNode.attachChild(shadow);
            shadow.setLocalTranslation(g.getWorldTranslation());          
            
            float size = Math.min(g.getModelBound().getVolume() * g.getWorldScale().x, 10) / 6f;           
            ColorRGBA c = (ColorRGBA) shadow.getMaterial().getParam("Color").getValue();
            c.a = FastMath.clamp((0.9f - (g.getWorldBound().getCenter().y) * 0.08f) * size, 0.0f, 1.0f);
           
            shadow.setLocalScale(getMaxExtent(g) * 2f);             
            shadow.getMaterial().setColor("Color", c);
        }
        for (Geometry geom : shadows.keySet()) {

            Geometry shadow = shadows.get(geom);
            if (shadow.getParent() == null) {
                shadowPool.add(shadow);
                shadows.remove(geom);
            }
        }
    }

    private Geometry getShadow(Geometry geom) {
        Geometry shadow = shadows.get(geom);
        if (shadow == null) {

            if (shadowPool.size() > 0) {
                shadow = shadowPool.get(0);
                shadowPool.remove(0);
            } else {
                shadow = createShadow(geom, shadow);
            }
            shadow.setName(geom.getName() + "Shadow");
            shadows.put(geom, shadow);
        }

        return shadow;
    }

    private float getMaxExtent(Geometry g) {
        BoundingBox bbox = ((BoundingBox) g.getWorldBound());
        return Math.max(bbox.getZExtent(), Math.max(bbox.getXExtent(), bbox.getYExtent()));
    }
    
    //int cpt = 0;
    private Geometry createShadow(Geometry g, Geometry shadow) {
        //cpt++;
        shadow = new Geometry(g.getName() + "Shadow", q);
        shadow.setMaterial(shadowMaterial.clone());
        shadow.getMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        shadow.setQueueBucket(RenderQueue.Bucket.Transparent);
        shadow.setLocalScale(2f);
        //System.out.println("Created "+cpt+" shadows so far");
        return shadow;
    }

    final public void createQuad() {

        q = new Mesh();
        q.setBuffer(Type.Position, 3, new float[]{-0.5f, 0, -0.5f,
                    0.5f, 0, -0.5f,
                    0.5f, 0, 0.5f,
                    -0.5f, 0, 0.5f
                });

        q.setBuffer(Type.TexCoord, 2, new float[]{0, 0,
                    1, 0,
                    1, 1,
                    0, 1});

        q.setBuffer(Type.Normal, 3, new float[]{0, 1, 0,
                    0, 1, 0,
                    0, 1, 0,
                    0, 1, 0});

        q.setBuffer(Type.Index, 3, new short[]{0, 2, 1,
                    0, 3, 2});

        q.updateBound();
    }

  
    public Node getShadowNode() {
        return shadowNode;
    }

    public void postFrame(FrameBuffer out) {
    }

    public void cleanup() {
    }

    public Material getGroundMaterial() {
        return shadowMaterial;
    }
}
