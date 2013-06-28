package com.jme3.android.demo.utils;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.ui.Picture;

/**
 * Represents a picture that can be selected.
 * Provides some additional methods to determine if a touch location is over
 * the picture and provides a location within the picture.
 *
 * @author iwgeric
 */
public class SelectablePicture extends Picture {
    private boolean selected = false;

    public SelectablePicture(String name, boolean flipY) {
        super(name, flipY);
    }
    public SelectablePicture(String name) {
        super(name);
    }
    public SelectablePicture() {
        super();
    }

    /**
     * Returns the height of the picture in pixels
     * @return Height of picture in pixels
     */
    public float getHeight() {
        return getLocalScale().y;
    }

    /**
     * Returns the width of the picture in pixels
     * @return Width of the picture in pixels
     */
    public float getWidth() {
        return getLocalScale().x;
    }
    private Vector2f getPosition() {
        float x = getLocalTranslation().x;
        float y = getLocalTranslation().y;
        return new Vector2f(x, y);
    }
    private Vector2f getCenter() {
        float x = getLocalTranslation().x + (getWidth() / 2f);
        float y = getLocalTranslation().y + (getHeight() / 2f);
        return new Vector2f(x, y);
    }
    private Vector2f getMin() {
        return new Vector2f(getLocalTranslation().x, getLocalTranslation().y);
    }
    private Vector2f getMax() {
        return new Vector2f(getLocalTranslation().x + getWidth(), getLocalTranslation().y + getHeight());
    }

    private void setSelected(boolean selected) {
        this.selected = selected;
    }
    private boolean isSelected() {
        return selected;
    }

    /**
     * Used to determine if a location is over the picture.
     * @param location Screen coordinates in pixels
     * @return true if the location is over the picture
     */
    public boolean checkSelect(Vector2f location) {
        if (location == null) {
            return false;
        }
        return checkSelect(location.x, location.y);
    }

    /**
     * Used to determine if a location is over the picture.
     * @param x Screen location in pixels
     * @param y Screen location in pixels
     * @return true if the location is over the picture
     */
    public boolean checkSelect(float x, float y) {
        Vector2f min = getMin();
        Vector2f max = getMax();

        if (    x > min.x &&
                x < max.x &&
                y > min.y &&
                y < max.y
                ) {
            return true;
        }
        return false;
    }

    /**
     * Provides the percentage of the location from the center of the picture.
     * Positive values mean the location is to the Right or Above the center of the picture.
     * Negative values mean the location is to the Left or Below the center of the picture.
     * @param location Screen location in pixels
     * @return Vector with the location percentage from the center of the picture.
     */
    public Vector2f getLocationRatioFromCenter(Vector2f location) {
        if (location == null) {
            return null;
        }
        return getLocationRatioFromCenter(location.x, location.y);
    }

    /**
     * Provides the percentage of the location from the center of the picture.
     * Positive values mean the location is to the Right or Above the center of the picture.
     * Negative values mean the location is to the Left or Below the center of the picture.
     * @param x Screen location in pixels
     * @param y Screen location in pixels
     * @return Vector with the location percentage from the center of the picture.
     */
    public Vector2f getLocationRatioFromCenter(float x, float y) {
        if (getWidth() == 0f || getHeight() == 0f) {
            return null;
        }

        Vector2f center = getCenter();

        float horizontalRatio = (x - center.x) / (getWidth() / 2f);
        horizontalRatio = FastMath.clamp(horizontalRatio, -1, 1);
        float verticalRatio = (y - center.y) / (getHeight() / 2f);
        verticalRatio = FastMath.clamp(verticalRatio, -1, 1);

        return new Vector2f(horizontalRatio, verticalRatio);
    }

}
