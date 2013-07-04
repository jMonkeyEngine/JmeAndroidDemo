package com.jme3.android.demo.input;

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
import com.jme3.input.event.TouchEvent.Type;
import com.jme3.util.IntMap;
import java.util.ArrayList;
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
    private ArrayList<InputActionListener> inputActionListeners = new ArrayList<InputActionListener>();

    private IntMap<Long> mouseDownButtons = new IntMap<Long>();
    private long singleTapMaxTime = (long)(0.25 * 1000000000); // 0.25sec in nanoseconds

    public void addInputActionListener(InputActionListener listener) {
        if (inputActionListeners.contains(listener)) {
            logger.log(Level.INFO, "InputActionListener {0} already added.",
                    new Object[]{listener.getClass().getName()});
        } else {
            inputActionListeners.add(listener);
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

    public static void dumpEvent(String from, TouchEvent event) {
//        logger.log(Level.INFO, "******** Event Dump from: {0} ********", from);
//        logger.log(Level.INFO, "pointerid: {0}, type: {1}, x: {2}, y: {3}, dx: {4}, dy: {5}, scalespan: {6}, dscalespan: {7}",
//                new Object[]{event.getPointerId(), event.getType(), event.getX(), event.getY(), event.getDeltaX(), event.getDeltaY(), event.getScaleSpan(), event.getDeltaScaleSpan()});
    }

    private boolean processInputAction(TouchEvent event, float tpf) {
        boolean consumed = false;
        if (inputActionListeners != null) {
            for (InputActionListener listener: inputActionListeners) {
                consumed = listener.onInputAction(event, tpf);
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

        inputActionListeners.clear();

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
        TouchEvent event;
        if (mme.getDeltaWheel() != 0) {
            event = new TouchEvent(Type.SCALE_MOVE, mme.getX(), mme.getY(), mme.getDX(), mme.getDY());
            event.setPointerId(MouseInput.BUTTON_LEFT);
            event.setScaleSpan(mme.getWheel());
            event.setDeltaScaleSpan(mme.getDeltaWheel());
            dumpEvent(this.getClass().getName() + ": onMouseMotionEvent", event);
            processInputAction(event, localTPF);
        }
        event = new TouchEvent(Type.MOVE, mme.getX(), mme.getY(), mme.getDX(), mme.getDY());
        event.setPointerId(MouseInput.BUTTON_LEFT);
        event.setScaleSpan(mme.getWheel());
        event.setDeltaScaleSpan(mme.getDeltaWheel());
        dumpEvent(this.getClass().getName() + ": onMouseMotionEvent", event);
        processInputAction(event, localTPF);

    }

    public void onMouseButtonEvent(MouseButtonEvent mbe) {
//        logger.log(Level.INFO, "onMouseButtonEvent buttonIndex: {0}, isPressed: {1}, locX: {2}, locY: {3}, localTPF: {4}",
//                new Object[]{mbe.getButtonIndex(), mbe.isPressed(), mbe.getX(), mbe.getY(), localTPF});
        long time = System.nanoTime();
        long downTimeElapsed = 999999999;
        Long mouseDownTime = mouseDownButtons.get(mbe.getButtonIndex());
        boolean consumed = false;

        if (mouseDownTime == null) {
            mouseDownButtons.put(mbe.getButtonIndex(), time);
        }

        Type type;
        if (mbe.isPressed()) {
            type = Type.DOWN;
            mouseDownButtons.put(mbe.getButtonIndex(), time);
        } else {
            type = Type.UP;
            downTimeElapsed = (time - mouseDownTime.longValue());// / 1000000000.0;
        }
        TouchEvent event;
        event = new TouchEvent(type, mbe.getX(), mbe.getY(), 0, 0);
        event.setPointerId(mbe.getButtonIndex());
        event.setScaleSpan(0);
        event.setDeltaScaleSpan(0);
        dumpEvent(this.getClass().getName() + ": onMouseButtonEvent", event);
        processInputAction(event, localTPF);

        if (downTimeElapsed <= singleTapMaxTime) {
            event = new TouchEvent(Type.TAP, mbe.getX(), mbe.getY(), 0, 0);
            event.setPointerId(mbe.getButtonIndex());
            event.setScaleSpan(0);
            event.setDeltaScaleSpan(0);
            dumpEvent(this.getClass().getName() + ": onMouseButtonEvent", event);
            processInputAction(event, localTPF);
        }
    }

    public void onKeyEvent(KeyInputEvent kie) {
    }

    public void onTouchEvent(TouchEvent te) {
//        logger.log(Level.INFO, "onTouchEvent pointerId: {0}, type: {1}, x: {2}, y: {3}, scale: {4}, dx: {5}, dy: {6}, dScale: {7}",
//                new Object[]{te.getPointerId(), te.getType(), te.getX(), te.getY(), te.getScaleSpan(), te.getDeltaX(), te.getDeltaY(), te.getDeltaScaleSpan()});
        boolean consumed = processInputAction(te, localTPF);
        if (consumed) {
            te.setConsumed();
        }
    }

}
