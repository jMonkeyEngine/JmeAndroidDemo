package com.jme3.android.demo.camera;

/**
 *
 * @author iwgeric
 */
public interface DemoCamera {
    public void enableRotation(boolean enable);
    public void hRotate(float value);
    public void vRotate(float value);
    public void zoom(float value);
}
