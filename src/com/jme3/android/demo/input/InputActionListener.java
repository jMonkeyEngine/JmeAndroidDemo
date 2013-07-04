package com.jme3.android.demo.input;

import com.jme3.input.event.TouchEvent;

/**
 *
 * @author iwgeric
 */
public interface InputActionListener {
    public boolean onInputAction(TouchEvent event, float tpf);
}
