package com.jme3.android.demo.input;

import com.jme3.android.demo.input.InputHandler.InputType;


/**
 * This interface is used to listen to generic input actions from InputHandler.
 * Classes must register themselves to InputHandler using InputHandler.addListener
 * Events are not generated if they are already consumed by the CharacterMotion class.
 *
 * onAction is used for touch events.
 * onAnalog is used for touch dragging.
 *
 * @author iwgeric
 */
public interface InputListener {
    /**
     * onAction is used to receive callbacks for touch events
     * @param inputType see InputHandler.InputType for defined Input Types
     * @param tpf Time per Frame in sec.
     */
    public void onAction(InputType inputType, float tpf);

    /**
     * onAnalog is used to recieve continuous callbacks while an event is happening
     * @param inputType see InputHandler.InputType for defined Input Types
     * @param value value passed through from jME InputManager
     * @param tpf Time per Frame in sec.
     */
    public void onAnalog(InputType inputType, float value, float tpf);
}
