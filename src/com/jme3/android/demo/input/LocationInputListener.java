package com.jme3.android.demo.input;


/**
 *
 * @author iwgeric
 */
public interface LocationInputListener {
    public enum LocationType {
        DOWN,
        UP,
        TAP,
        DOUBLETAP,
        MOVE,
    }

    public boolean onLocation(LocationType locationType, int pointerId, float locX, float locY, float tpf);

}
