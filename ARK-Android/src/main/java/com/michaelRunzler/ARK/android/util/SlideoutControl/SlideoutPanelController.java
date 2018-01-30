package com.michaelRunzler.ARK.android.util.SlideoutControl;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.michaelRunzler.ARK.android.R;

/**
 * Allows interaction with a Modular Slideout Panel layout.
 */
public class SlideoutPanelController
{
    private RelativeLayout linkedView;
    private int animTime;

    private int DEFAULT_BG_COLOR;

    /**
     * Sets up a new instance of this object.
     * @param linkedView a {@link RelativeLayout} representing the desired linked slideout panel layout.
     *                   This view's {@link Context} will be used to set default properties for this object.
     * @param width the desired width for the slideout panel in pixels. Setting this to {@code 0} or less will
     *              use the currently set value in the linked {@link View}.
     * @param BGColor the background color for the linked slideout. Setting this to {@code 0} or less will use
     *                the corresponding default color value specified in the {@code colors.xml} file.
     * @param animTime the duration in milliseconds of all animations that will fire when extending
     *                 or retracting the panel. Setting this to {@code 0} or less will default to a value of
     *                 250ms.
     */
    public SlideoutPanelController(RelativeLayout linkedView, int width, int BGColor, int animTime)
    {
        if(linkedView == null) throw new IllegalArgumentException("Linked view must not be null!");

        Context context = linkedView.getContext();
        if(context == null) throw new IllegalArgumentException("Linked view's Context must not be null!");

        this.linkedView = linkedView;

        linkedView.getLayoutParams().width = width <= 0 ? linkedView.getWidth() : width;
        DEFAULT_BG_COLOR = context.getResources().getColor(R.color.mainSlideoutBackground);
        linkedView.setBackgroundColor(BGColor <= 0 ? DEFAULT_BG_COLOR : BGColor);
        this.animTime = animTime <= 0 ? 250 : animTime;
    }

    /**
     * Switches the visibility state of the overlay, running all associated animations.
     */
    public void switchState()
    {
        Handler handler = new Handler();

        if(linkedView.getVisibility() == View.VISIBLE)
        {
            linkedView.animate().translationX(-1.0f * (linkedView.getWidth() * linkedView.getScaleX())).setDuration(animTime).start();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    linkedView.setVisibility(View.GONE);
                }
            }, animTime);
        }else{
            linkedView.setVisibility(View.VISIBLE);
            linkedView.animate().translationX(0).setDuration(animTime).start();
        }
    }

    /**
     * Switches the visibility state of the overlay, skipping all animations that would normally run.
     */
    public void switchStateNoAnim()
    {
        int cachedAnimTime = animTime;
        animTime = 0;

        switchState();

        animTime = cachedAnimTime;
    }

    /**
     * Sets the background color of the slideout panel.
     * Setting this to {@code -1} will use the default color set in{@code colors.xml}.
     * @param color the resource ID of the color to use as the background
     */
    public void setBGColor(int color){
        linkedView.setBackgroundColor(color <= 0 ? DEFAULT_BG_COLOR : color);
    }

    /**
     * Sets the desired width of the slideout panel.
     * Values below {@code 0} will be ignored.
     * @param width the width of the panel in pixels
     */
    public void setWidth(int width){
        linkedView.getLayoutParams().width = width <= 0 ? linkedView.getWidth() : width;
    }

    /**
     * Gets the current background color of this slideout panel.
     * @return the resource ID of the current background color. The returned value will directly
     * reflect the actual color, as the value is sourced from the linked view itself.
     */
    public int getBGColor() {
        return ((ColorDrawable)linkedView.getBackground()).getColor();
    }

    /**
     * Gets the current animation delay time.
     * @return the animation delay time in milliseconds
     */
    public int getAnimTime(){
        return animTime;
    }

    /**
     * Gets the current width of this slideout panel.
     * @return the width in pixels. The returned value will directly reflect the actual width,
     * as the value is sourced from the linked view itself.
     */
    public int getWidth(){
        return linkedView.getWidth();
    }
}
