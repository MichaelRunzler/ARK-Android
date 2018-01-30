package com.michaelRunzler.ARK.android.util;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Provides common cross-class utilities for classes in this app package.
 */
public class StaticUtils
{
    /**
     * Gets the inherited (actual) scale of a {@link View}. This scale differs from that returned by
     * {@link View#getScaleX()} and {@link View#getScaleY()} in that it takes into account scale factors
     * that are inherited from its parents, if it has any. Inherited scale includes, for example, scale
     * factors set on container-type layouts (such as {@link android.widget.RelativeLayout} and similar)
     * that automatically apply to children as well. Inherited scale is found by iterating through the
     * provided {@link View}'s family tree, checking each parent's scale and factoring it with the scale
     * factors of all below it.
     * @param v the {@link View} to check scaling on
     * @return a 2-length {@link Float} array containing the provided {@link View}'s inherited scale factors
     * in the following order: x-scale, y-scale.
     */
    public static float[] getInheritedScale(@NonNull View v)
    {
        if(v.getParent() == null || !(v.getParent() instanceof View)){
            return new float[]{v.getScaleX(), v.getScaleY()};
        }

        float[] coords = new float[2];

        coords[0] = v.getScaleX();
        coords[1] = v.getScaleY();

        // Iterate through the View's family tree, getting each parent View's scale and calculating
        // it into the total scale factor.
        boolean done = false;
        View current = (View)v.getParent();
        while(!done)
        {
            coords[0] = coords[0] * current.getScaleX();
            coords[1] = coords[1] * current.getScaleY();

            // Check that we have not reached the top of the tree. If we have, set the done flag.
            if(current.getParent() != null && current.getParent() instanceof View){
                current = (View)current.getParent();
            }else{
                done = true;
            }
        }

        return coords;
    }

    /**
     * Gets the inherited X scale of a {@link View}. Equivalent to calling {@code {@link StaticUtils#getInheritedScale(View)}[0]}.
     * @param v the {@link View} to check scaling on
     * @return the inherited X scale of the provided {@link View}
     */
    public static float getInheritedScaleX(@NonNull View v){
        return getInheritedScale(v)[0];
    }

    /**
     * Gets the inherited X scale of a {@link View}. Equivalent to calling {@code {@link StaticUtils#getInheritedScale(View)}[1]}.
     * @param v the {@link View} to check scaling on
     * @return the inherited X scale of the provided {@link View}
     */
    public static float getInheritedScaleY(@NonNull View v){
        return getInheritedScale(v)[1];
    }
}
