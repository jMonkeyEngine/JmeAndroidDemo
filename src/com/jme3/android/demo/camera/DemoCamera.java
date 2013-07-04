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
    public void pan(float value, boolean sideways);
    public void setEnabled(boolean enable);
    public boolean supportsPan();
}
