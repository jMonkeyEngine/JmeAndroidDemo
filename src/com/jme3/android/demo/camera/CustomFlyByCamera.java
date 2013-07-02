package com.jme3.android.demo.camera;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.Camera;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class CustomFlyByCamera extends FlyByCamera {
    private static final Logger logger = Logger.getLogger(CustomFlyByCamera.class.getName());

    public CustomFlyByCamera(Camera cam, InputManager inputManager){
        super(cam);
        inputManager.addMapping("FLYCAM_ZoomIn", new KeyTrigger(KeyInput.KEY_EQUALS));
        inputManager.addMapping("FLYCAM_ZoomOut", new KeyTrigger(KeyInput.KEY_MINUS));
    }

    @Override
    protected void mapJoystick( Joystick joystick ) {
        /*
        leave empty to remove android orientation
        sensors from controling the camera
        */
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
//        logger.log(Level.INFO, "onAction name: {0}, value: {1}, tpf: {2}, enabled: {3}",
//                new Object[]{name, value, tpf, isEnabled()});
        super.onAction(name, value, tpf);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
//        logger.log(Level.INFO, "onAnalog name: {0}, value: {1}, tpf: {2}, enabled: {3}",
//                new Object[]{name, value, tpf, isEnabled()});
        super.onAnalog(name, value, tpf);
//        logger.log(Level.INFO, "cam location: {0}, direction: {1}",
//                new Object[]{cam.getLocation(), cam.getDirection()});

    }

}
