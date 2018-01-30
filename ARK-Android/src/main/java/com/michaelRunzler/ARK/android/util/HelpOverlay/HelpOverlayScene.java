package com.michaelRunzler.ARK.android.util.HelpOverlay;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Stores configuration data for individual 'scenes' in the {@link DynamicHelpInterface}.
 * All fields are read-only, and cannot be changed after initialization. Container-type object.
 */
public class HelpOverlayScene
{
    private float x;
    private float y;
    private int width;
    private int height;
    private int maxLabelWidth;
    private View target;
    private String label;
    private boolean usingViewTarget;
    private int BGColorID;

    /**
     * Initializes a new scene object with the specified coordinates and dimensions.
     * @param label the text for the scene label
     * @param x the absolute X-coordinate in pixels. Must be >=0 and <= maximum screen width.
     * @param y the absolute Y-coordinate in pixels. Must be >=0 and <= maximum screen height.
     * @param width the width of the display field for the scene. Must be {@code >= 0}.
     * @param height the height of the display field for the scene. Must be {@code >= 0}.
     * @param labelBGColorID the color ID to use as the label background color. Set to {@code -1} to use default.
     */
    public HelpOverlayScene(@NonNull String label, float x, float y, int width, int height, int maxLabelWidth, int labelBGColorID){
        this.label = label;
        DisplayMetrics display  = Resources.getSystem().getDisplayMetrics();

        // Check dimensions and coordinates for validity.
        if(x < 0 || x > display.widthPixels || y < 0 || y > display.heightPixels || width < 0 || height < 0)
            throw new IllegalArgumentException("One or more invalid coordinate/dimension arguments!");

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.target = null;
        this.usingViewTarget = false;
        this.BGColorID = labelBGColorID;
        this.maxLabelWidth = maxLabelWidth;
    }

    /**
     * Initializes a new scene object with the specified target {@link View}.
     * @param label the text for the scene label
     * @param target the {@link View} object to be used as the source for display field dimensions and coordinates
     * @param maxLabelWidth the maximum horizontal width of the label before wrapping around to another line
     * @param labelBGColorID the color ID to use as the label background color. Set to {@code -1} to use default.
     */
    public HelpOverlayScene(@NonNull String label, @NonNull View target, int maxLabelWidth, int labelBGColorID){
        this.label = label;
        this.target = target;
        this.usingViewTarget = true;
        this.BGColorID = labelBGColorID;
        this.maxLabelWidth = maxLabelWidth;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public View getTarget() {
        return target;
    }

    public String getLabel() {
        return label;
    }

    public int getBGColorID() {
        return BGColorID;
    }

    public int getMaxLabelWidth(){
        return maxLabelWidth;
    }

    /**
     * This value is {@code true} if the object was initialized using the view-based constructor, {@code false} if
     * it was initialized using the coordinate-based constructor.
     */
    public boolean isUsingViewTarget() {
        return usingViewTarget;
    }
}
