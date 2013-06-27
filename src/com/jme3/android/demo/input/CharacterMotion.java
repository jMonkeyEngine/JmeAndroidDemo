package com.jme3.android.demo.input;

/**
 * CharacterMotion interface allows a class to register with InputHandler to
 * receive character motion input events
 *
 * @author iwgeric
 */
public interface CharacterMotion {
    /**
     * Checks the implementation to see if the touch event and future touch
     * events with the same pointer id are to be used to move the character.<br>
     * If true is return, this event and future events with the same pointer id
     * are provided via processMotionRequest until the finger is lifted.
     * Input events are consumed so they won't be sent to other generic listeners.
     *
     * @param x Screen x location in pixels
     * @param y Screen y location in pixels
     * @return true if the event is to be used for character motion
     */
    public boolean checkSelect(float x, float y);

    /**
     * Provides the x and y screen coordinates of the touch event so the
     * character motion implementation can move the character.
     * @param x Screen x location in pixels
     * @param y Screen y location in pixels
     * @param tpf Time per Frame in sec.
     */
    public void processMotionRequest(float x, float y, float tpf);
}
