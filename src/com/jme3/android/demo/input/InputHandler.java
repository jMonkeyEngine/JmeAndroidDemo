package com.jme3.android.demo.input;

import com.jme3.android.demo.input.LocationInputListener.LocationType;
import com.jme3.android.demo.input.ValueInputListener.ValueType;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
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
public class InputHandler extends AbstractAppState implements RawInputListener {
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


    private long lastFrameNanos = 0;
    private float localTPF = 0;
    private Map<LocationType, ArrayList<LocationInputListener>> locationInputListenerMap =
            new EnumMap<LocationType, ArrayList<LocationInputListener>>(LocationType.class);
    private Map<ValueType, ArrayList<ValueInputListener>> valueInputListenerMap =
            new EnumMap<ValueType, ArrayList<ValueInputListener>>(ValueType.class);

    public void addValueInputListener(ValueInputListener listener, ValueType... valueTypes) {
        for (ValueType valueType : valueTypes) {
            ArrayList<ValueInputListener> valueInputListeners = valueInputListenerMap.get(valueType);
            if (valueInputListeners == null) {
                valueInputListeners = new ArrayList<ValueInputListener>();
            }
            if (valueInputListeners.contains(listener)) {
                logger.log(Level.INFO, "{0} alread mapped to {1}",
                        new Object[]{valueType, listener.getClass().getName()});
            } else {
                valueInputListeners.add(listener);
            }
            valueInputListenerMap.put(valueType, valueInputListeners);
        }
    }

    public void addLocationInputListener(LocationInputListener listener, LocationType... locationTypes) {
        for (LocationType locationType : locationTypes) {
            ArrayList<LocationInputListener> locationInputListeners = locationInputListenerMap.get(locationType);
            if (locationInputListeners == null) {
                locationInputListeners = new ArrayList<LocationInputListener>();
            }
            if (locationInputListeners.contains(listener)) {
                logger.log(Level.INFO, "{0} alread mapped to {1}",
                        new Object[]{locationType, listener.getClass().getName()});
            } else {
                locationInputListeners.add(listener);
            }
            locationInputListenerMap.put(locationType, locationInputListeners);
        }
    }

    private void registerInputs() {
        inputManager.setSimulateMouse(false);
        inputManager.addRawInputListener(this);

        // Android touch mapping
        //inputManager.addMapping(TOUCH, new TouchTrigger(TouchInput.ALL));
        //inputManager.addListener(this, TOUCH);

        // Desktop mouse mappings for debug
        //inputManager.addMapping(MOUSE_LEFT_BUTTON,
        //        new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        //inputManager.addMapping(MOUSE_X_AXIS_POS,
        //        new MouseAxisTrigger(MouseInput.AXIS_X, false));
        //inputManager.addMapping(MOUSE_X_AXIS_NEG,
        //        new MouseAxisTrigger(MouseInput.AXIS_X, true));
        //inputManager.addMapping(MOUSE_Y_AXIS_POS,
        //        new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        //inputManager.addMapping(MOUSE_Y_AXIS_NEG,
        //        new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        //inputManager.addMapping(MOUSE_WHEEL_AXIS_POS,
        //        new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        //inputManager.addMapping(MOUSE_WHEEL_AXIS_NEG,
        //        new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        //inputManager.addListener(this, MOUSE_LEFT_BUTTON,
        //        MOUSE_X_AXIS_POS, MOUSE_X_AXIS_NEG,
        //        MOUSE_Y_AXIS_POS, MOUSE_Y_AXIS_NEG,
        //        MOUSE_WHEEL_AXIS_POS, MOUSE_WHEEL_AXIS_NEG
        //        );
    }

    private boolean processLocationInput(LocationType locationType, int pointerId, float locX, float locY, float tpf) {
        boolean consumed = false;
        ArrayList<LocationInputListener> locationInputListeners = locationInputListenerMap.get(locationType);
        if (locationInputListeners != null) {
            for (LocationInputListener listener: locationInputListeners) {
                consumed = listener.onLocation(locationType, pointerId, locX, locY, tpf);
                if (consumed) {
//                    logger.log(Level.INFO, "consumed by: {0}", listener.getClass().getName());
                    break;
                }
            }
        }
        return consumed;
    }

    private boolean processValueInput(ValueType valueType, int pointerId, float value, float tpf) {
        boolean consumed = false;
        ArrayList<ValueInputListener> valueInputListeners = valueInputListenerMap.get(valueType);
        if (valueInputListeners != null) {
            for (ValueInputListener listener: valueInputListeners) {
                consumed = listener.onValue(valueType, pointerId, value, tpf);
                if (consumed) {
//                    logger.log(Level.INFO, "consumed by: {0}", listener.getClass().getName());
                    break;
                }
            }
        }
        return consumed;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.inputManager = app.getInputManager();

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
        //inputManager.removeListener(this);
        //inputManager.deleteMapping(TOUCH);
        //inputManager.deleteMapping(MOUSE_LEFT_BUTTON);
        //inputManager.deleteMapping(MOUSE_X_AXIS_POS);
        //inputManager.deleteMapping(MOUSE_X_AXIS_NEG);
        //inputManager.deleteMapping(MOUSE_Y_AXIS_POS);
        //inputManager.deleteMapping(MOUSE_Y_AXIS_NEG);
        //inputManager.deleteMapping(MOUSE_WHEEL_AXIS_POS);
        //inputManager.deleteMapping(MOUSE_WHEEL_AXIS_NEG);

        inputManager.removeRawInputListener(this);

        Set<Entry<ValueType, ArrayList<ValueInputListener>>> entries = valueInputListenerMap.entrySet();
        for (Entry<ValueType, ArrayList<ValueInputListener>> entry: entries) {
            ArrayList<ValueInputListener> listeners = entry.getValue();
            if (listeners != null) {
                listeners.clear();
            }
        }
        valueInputListenerMap.clear();

        Set<Entry<LocationType, ArrayList<LocationInputListener>>> entriesRaw = locationInputListenerMap.entrySet();
        for (Entry<LocationType, ArrayList<LocationInputListener>> entry: entriesRaw) {
            ArrayList<LocationInputListener> listeners = entry.getValue();
            if (listeners != null) {
                listeners.clear();
            }
        }
        locationInputListenerMap.clear();

        super.cleanup();
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
        boolean consumed = processLocationInput(LocationType.MOVE, MouseInput.BUTTON_LEFT, (float)mme.getX(), (float)mme.getY(), localTPF);
        if (consumed) {
            mme.setConsumed();
        } else {
            if (mme.getDX() != 0) {
                processValueInput(ValueType.X_AXIS_DRAG, MouseInput.BUTTON_LEFT, (float)mme.getDX(), localTPF);
            }

            if (mme.getDY() != 0) {
                processValueInput(ValueType.Y_AXIS_DRAG, MouseInput.BUTTON_LEFT, (float)mme.getDY(), localTPF);
            }

            if (mme.getDeltaWheel() != 0) {
                processValueInput(ValueType.PINCH, MouseInput.BUTTON_MIDDLE, (float)mme.getDeltaWheel(), localTPF);
            }
        }
    }

    public void onMouseButtonEvent(MouseButtonEvent mbe) {
//        logger.log(Level.INFO, "onMouseButtonEvent buttonIndex: {0}, isPressed: {1}, locX: {2}, locY: {3}, localTPF: {4}",
//                new Object[]{mbe.getButtonIndex(), mbe.isPressed(), mbe.getX(), mbe.getY(), localTPF});
        LocationType locationType;
        if (mbe.isPressed()) {
            locationType = LocationType.DOWN;
        } else {
            locationType = LocationType.UP;
        }
        boolean consumed = processLocationInput(locationType, mbe.getButtonIndex(), (float)mbe.getX(), (float)mbe.getY(), localTPF);
        if (consumed) {
            mbe.setConsumed();
        }
    }

    public void onKeyEvent(KeyInputEvent kie) {
    }

    public void onTouchEvent(TouchEvent te) {
//        logger.log(Level.INFO, "onTouchEvent pointerId: {0}, type: {1}, x: {2}, y: {3}",
//                new Object[]{te.getPointerId(), te.getType(), te.getX(), te.getY()});
        boolean consumed = false;
        switch (te.getType()) {
            case DOWN:
                consumed = processLocationInput(LocationType.DOWN, te.getPointerId(), te.getX(), te.getY(), localTPF);
                if (consumed) {
                    te.setConsumed();
                }
                break;
            case UP:
                consumed = processLocationInput(LocationType.UP, te.getPointerId(), te.getX(), te.getY(), localTPF);
                if (consumed) {
                    te.setConsumed();
                }
                break;
            case MOVE:
                consumed = processLocationInput(LocationType.MOVE, te.getPointerId(), te.getX(), te.getY(), localTPF);
                if (consumed) {
                    te.setConsumed();
                } else {
                    processValueInput(ValueType.X_AXIS_DRAG, MouseInput.BUTTON_LEFT, te.getDeltaX(), localTPF);
                    processValueInput(ValueType.Y_AXIS_DRAG, MouseInput.BUTTON_LEFT, te.getDeltaY(), localTPF);
                }
                break;
            case SCALE_MOVE:
                processValueInput(ValueType.PINCH, te.getPointerId(), te.getDeltaScaleSpan(), localTPF);
            default:
                break;
        }
    }

}
