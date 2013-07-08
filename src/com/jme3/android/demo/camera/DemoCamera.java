package com.jme3.android.demo.camera;

/**
 *
 * @author iwgeric
 */
public interface DemoCamera {
    public void setEnabled(boolean enable);
    public void enableRotation(boolean enable);
    public boolean isRotationEnabled();
    public void hRotate(float value);
    public void vRotate(float value);
    public void zoom(float value);
    public void pan(float value, boolean sideways);
    public boolean supportsPan();
    public void autoRotate(float value);
    public void autoZoom(float value);
    public void resetAutoZoom();
}
