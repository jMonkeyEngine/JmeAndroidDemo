package com.jme3.android.demo.input;


/**
 *
 * @author iwgeric
 */
public interface ValueInputListener {
    public enum ValueType {
        X_AXIS_DRAG,
        Y_AXIS_DRAG,
        PINCH,
    }

    public boolean onValue(ValueType valueType, int pointerId, float value, float tpf);
}
