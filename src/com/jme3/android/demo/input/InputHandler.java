package com.jme3.android.demo.input;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class InputHandler extends AbstractAppState implements
        AnalogListener, ActionListener, TouchListener, RawInputListener {
    private static final Logger logger = Logger.getLogger(InputHandler.class.getName());
    // Android only input mappings
    private static final String TOUCH = "InputHandler_Touch";
    // Desktop only input mappings
    private static final String MOUSE_LEFT_BUTTON = "InputHandler_MouseLeftButton";
    private static final String MOUSE_X_AXIS_POS = "InputHandler_MouseXAxisPos";
    private static final String MOUSE_X_AXIS_NEG = "InputHandler_MouseXAxisNeg";
    private static final String MOUSE_Y_AXIS_POS = "InputHandler_MouseYAxisPos";
    private static final String MOUSE_Y_AXIS_NEG = "InputHandler_MouseYAxisNeg";
    private static final String MOUSE_WHEEL_AXIS_POS = "InputHandler_MouseWheelAxisPos";
    private static final String MOUSE_WHEEL_AXIS_NEG = "InputHandler_MouseWheelAxisNeg";

    private AppStateManager stateManager = null;
    private InputManager inputManager = null;

    private Integer motionPointerId = null;
    private CharacterMotion characterMotion = null;
    private long lastFrameNanos = 0;
    private float localTPF = 0;
    private Map<InputType, ArrayList<InputListener>> inputListenerMap = new EnumMap<InputType, ArrayList<InputListener>>(InputType.class);


    public enum InputType {
        DOWN,
        UP,
        TAP,
        X_POS,
        X_NEG,
        Y_POS,
        Y_NEG,
        ZOOM_IN,
        ZOOM_OUT
    }

    public void addListener(InputListener listener, InputType... inputTypes) {
        for (InputType inputType : inputTypes) {
            ArrayList<InputListener> inputListeners = inputListenerMap.get(inputType);
            if (inputListeners == null) {
                inputListeners = new ArrayList<InputListener>();
            }
            if (inputListeners.contains(listener)) {
                logger.log(Level.INFO, "{0} alread mapped to {1}",
                        new Object[]{inputType, listener.getClass().getName()});
            } else {
                inputListeners.add(listener);
            }
            inputListenerMap.put(inputType, inputListeners);
        }
    }

    private void registerInputs() {
//        inputManager.setSimulateMouse(false);

        // Android touch mapping
        inputManager.addMapping(TOUCH, new TouchTrigger(TouchInput.ALL));
        inputManager.addListener(this, TOUCH);

        // Desktop mouse mappings for debug
        inputManager.addMapping(MOUSE_LEFT_BUTTON,
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(MOUSE_X_AXIS_POS,
                new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(MOUSE_X_AXIS_NEG,
                new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(MOUSE_Y_AXIS_POS,
                new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(MOUSE_Y_AXIS_NEG,
                new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(MOUSE_WHEEL_AXIS_POS,
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(MOUSE_WHEEL_AXIS_NEG,
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addListener(this, MOUSE_LEFT_BUTTON,
                MOUSE_X_AXIS_POS, MOUSE_X_AXIS_NEG,
                MOUSE_Y_AXIS_POS, MOUSE_Y_AXIS_NEG,
                MOUSE_WHEEL_AXIS_POS, MOUSE_WHEEL_AXIS_NEG
                );

        inputManager.addRawInputListener(this);
    }

    public void setCharacterMotion(CharacterMotion characterMotion) {
        this.characterMotion = characterMotion;
    }

    private boolean checkCharacterMotion(float x, float y) {
        if (characterMotion != null) {
            return characterMotion.checkSelect(x, y);
        } else {
            return false;
        }
    }

    private void processCharacterMotion(boolean active, float x, float y, float tpf) {
        if (characterMotion != null) {
            characterMotion.processMotionRequest(active, x, y, tpf);
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.inputManager = app.getInputManager();

        motionPointerId = null;

        registerInputs();

        super.initialize(stateManager, app);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    public void cleanup() {
        inputManager.removeListener(this);
        inputManager.deleteMapping(TOUCH);
        inputManager.deleteMapping(MOUSE_LEFT_BUTTON);
        inputManager.deleteMapping(MOUSE_X_AXIS_POS);
        inputManager.deleteMapping(MOUSE_X_AXIS_NEG);
        inputManager.deleteMapping(MOUSE_Y_AXIS_POS);
        inputManager.deleteMapping(MOUSE_Y_AXIS_NEG);
        inputManager.deleteMapping(MOUSE_WHEEL_AXIS_POS);
        inputManager.deleteMapping(MOUSE_WHEEL_AXIS_NEG);

        Set<Entry<InputType, ArrayList<InputListener>>> entries = inputListenerMap.entrySet();
        for (Entry<InputType, ArrayList<InputListener>> entry: entries) {
            ArrayList<InputListener> listeners = entry.getValue();
            if (listeners != null) {
                listeners.clear();
            }
        }
        inputListenerMap.clear();

        super.cleanup();
    }

    public void onAnalog(String mapping, float value, float tpf) {
    }

    public void onAction(String mapping, boolean isPressed, float tpf) {
    }

    public void onTouch(String mapping, TouchEvent event, float tpf) {
    }

    public void beginInput() {
        long time = System.nanoTime();
        localTPF = (time - lastFrameNanos) / 1000000000.0f;  // calculated tpf in sec
        lastFrameNanos = time;
    }

    public void endInput() {
    }

    public void onJoyAxisEvent(JoyAxisEvent jae) {
    }

    public void onJoyButtonEvent(JoyButtonEvent jbe) {
    }

    public void onMouseMotionEvent(MouseMotionEvent mme) {
        if (motionPointerId != null) {
//            logger.log(Level.INFO, "onMouseMotionEvent: {0}", mme.toString());
            processCharacterMotion(true, (float)mme.getX(), (float)mme.getY(), localTPF);
            mme.setConsumed();
        }
    }

    public void onMouseButtonEvent(MouseButtonEvent mbe) {
//        logger.log(Level.INFO, "onMouseButtonEvent: {0}", mbe.toString());
        if (mbe.getButtonIndex() == MouseInput.BUTTON_LEFT) {
            if (mbe.isPressed()) {
                if (checkCharacterMotion((float)mbe.getX(), (float)mbe.getY())) {
                    motionPointerId = MouseInput.BUTTON_LEFT;
                    processCharacterMotion(true, (float)mbe.getX(), (float)mbe.getY(), localTPF);
                    mbe.setConsumed();
                }
            } else {
                if (motionPointerId != null) {
                    processCharacterMotion(false, (float)mbe.getX(), (float)mbe.getY(), localTPF);
                    motionPointerId = null;
                    mbe.setConsumed();
                }
            }
        }
    }

    public void onKeyEvent(KeyInputEvent kie) {
    }

    public void onTouchEvent(TouchEvent te) {
    }

}
