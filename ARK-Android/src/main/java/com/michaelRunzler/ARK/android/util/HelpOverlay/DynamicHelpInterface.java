package com.michaelRunzler.ARK.android.util.HelpOverlay;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michaelRunzler.ARK.android.R;

import java.util.ArrayList;

/**
 * Allows interaction with a Dynamic Help Overlay layout.
 */
public class DynamicHelpInterface
{
    private int DEFAULT_LABEL_COLOR;
    private String DEFAULT_LABEL_TEXT;

    private ArrayList<HelpOverlayScene> scenes;
    private int sceneCounter;
    private RelativeLayout linkedView;
    private int labelBGColor;
    private View targetView;
    private String labelText;
    private int maxLabelWidth;

    private float manualXPos;
    private float manualYPos;
    private int manualXSize;
    private int manualYSize;

    /**
     * Initializes a new Help Overlay Interface object. This object has all default values and a null
     * target view until otherwise specified.
     * @param linkedView the RelativeLayout View to manage. Must be non-null and an instance of a
     *                   Dynamic Help Overlay layout. The view's Context will be
     *                   used to set default properties for this object.
     * @param scenes the initial list of overlay scene objects to store in this object's internal queue.
     *               If set to null, the initial scene list will be empty.
     */
    public DynamicHelpInterface(RelativeLayout linkedView, ArrayList<HelpOverlayScene> scenes)
    {
        if(linkedView == null) throw new IllegalArgumentException("Linked view must not be null!");

        if(!(linkedView.getChildAt(0) instanceof FrameLayout) || !(linkedView.getChildAt(5) instanceof TextView))
            throw new IllegalArgumentException("Linked view must be an instance of a Dynamic Help Overlay layout!");

        Context context = linkedView.getContext();
        if(context == null) throw new IllegalArgumentException("Linked view's Context must not be null!");

        this.linkedView = linkedView;
        this.targetView = null;

        manualXPos = 0.0f;
        manualYPos = 0.0f;
        manualXSize = 0;
        manualYSize = 0;

        this.DEFAULT_LABEL_COLOR = context.getResources().getColor(R.color.defaultHelpOverlayLabelBGColor);
        this.DEFAULT_LABEL_TEXT = context.getResources().getString(R.string.default_help_overlay_label_text);

        this.labelBGColor = DEFAULT_LABEL_COLOR;
        this.labelText = DEFAULT_LABEL_TEXT;
        this.maxLabelWidth = 0;

        this.scenes = scenes == null ? new ArrayList<HelpOverlayScene>() : scenes;
        sceneCounter = 0;
    }

    /**
     * Adds a scene object to the scene list.
     * @param scene the HelpOverlayScene object to add to the list
     */
    public void addScene(@NonNull HelpOverlayScene scene){
        scenes.add(scene);
    }

    /**
     * Sets the background color of the text label.
     * Setting this to -1 will use the default color set in colors.xml.
     * @param color the resource ID of the color to use as the label background
     */
    public void setLabelColor(int color){
        labelBGColor = color == -1 ? DEFAULT_LABEL_COLOR : color;
    }

    /**
     * Sets this object's label text.
     * Setting this to null will use the default label text set in strings.xml.
     * @param text the text to use for the label on this overlay
     */
    public void setLabelText(String text){
        labelText = text == null ? DEFAULT_LABEL_TEXT : text;
    }

    /**
     * Sets the maximum width of the text label before wrapping around to another line.
     * Values below 0 will be ignored.
     * @param max the maximum width of the label in pixels
     */
    public void setMaxLabelWidth(int max){
        this.maxLabelWidth = max < 0 ? 0 : max;
    }

    /**
     * Sets this object's target view. This view will be 'highlighted' in the overlay.
     * Cannot be the same as the linked view.
     * Setting this to null will use manually set position values, if present.
     * @param target the View to use as this object's highlight target
     */
    public void setTargetView(View target){
        if(target == linkedView) throw new IllegalArgumentException("Target view cannot be the same as the linked view!");
        this.targetView = target;
    }

    /**
     * Sets manual values for the overlay's label/display pair.
     * Any targeted View will override manually set values. Set the targeted View to null to enable manual values.
     * @param xPos the X-position of the upper-left corner of the label/display pair in DIP (density-independent pixels)
     * @param yPos the Y-position of the upper-left corner of the label/display pair in DIP (density-independent pixels)
     * @param width the width of the display window in DIP (density-independent pixels)
     * @param height the height of the display window in DIP (density-independent pixels)
     */
    public void setManualValues(float xPos, float yPos, int width, int height){
        manualXPos = xPos < 0 ? 0 : xPos;
        manualYPos = yPos < 0 ? 0 : yPos;
        manualXSize = width < 0 ? 0 : width;
        manualYSize = height < 0 ? 0 : height;
    }

    /**
     * Gets the current list of stored scenes. Returns a copy, not a direct reference.
     * @return a copy of the current scene list
     */
    public ArrayList<HelpOverlayScene> getScenes(){
        ArrayList<HelpOverlayScene> retV = new ArrayList<>();
        retV.addAll(scenes);
        return retV;
    }

    /**
     * Clears the current internal scene list.
     */
    public void clearSceneList(){
        scenes = new ArrayList<>();
    }

    /**
     * Gets this object's linked overlay layout.
     * @return the RelativeLayout linked to this object
     */
    public RelativeLayout getLinkedView(){
        return linkedView;
    }

    /**
     * Gets the background color of the text label.
     * @return the resource ID of the color being used as the text label BG color
     */
    public int getLabelColor(){
        return labelBGColor;
    }

    /**
     * Gets the current label text.
     * @return the String being used as the display label.
     */
    public String getLabelText(){
        return labelText;
    }

    /**
     * Gets the text label's maximum line width before autowrapping.
     * @return the label's max line width in pixels
     */
    public int getMaxLabelWidth() {
        return maxLabelWidth;
    }

    /**
     * Gets the four manually set position/size values for this object.
     * @return a length 4 float array containing the following values, in this order: x-position, y-position, width, height. All values are in pixels.
     */
    public float[] getManualValues(){
        return new float[]{manualXPos, manualYPos, manualXSize, manualYSize};
    }

    /**
     * Gets this object's currently targeted View.
     * @return the View that is currently being used as this object's target, or null if none is set
     */
    public View getTargetView(){
        return targetView;
    }

    /**
     * Updates the display of the linked overlay view.
     * Display window and associated label are moved to the targeted view or manually set coordinates,
     * whichever has current priority. The label is checked for screen bounds clipping, and moved if
     * necessary.
     */
    public void updateLinkedView()
    {
        // FIXME: textview not sizing properly on first display, not compensating for margins
        // Get the display field and its container from the layout.
        FrameLayout display = (FrameLayout) linkedView.getChildAt(4);
        TextView label = (TextView)linkedView.getChildAt(5);

        label.setWidth(maxLabelWidth);
        label.setText(labelText);
        label.setBackgroundColor(labelBGColor);

        // Calculate element positions:
        if(targetView != null)
        {
            // Make sure the display field is at the target's X-Y coordinates as well.
            display.setX(targetView.getX());
            display.setY(targetView.getY());

            // Size the display field to the same dimensions as the target.
            display.getLayoutParams().width = targetView.getWidth();
            display.getLayoutParams().height = targetView.getHeight();

            // Compensate for any margins the target might have.

        }else
        {
            // Make sure the display field is at the manual coordinates as well.
            display.setX(manualXPos);
            display.setY(manualYPos);

            // Size the display field to the manual dimensions.
            display.getLayoutParams().width = manualXSize;
            display.getLayoutParams().height = manualYSize;
        }

        linkedView.requestLayout();

        adaptiveCorrelation(targetView, label);

        linkedView.invalidate();
        linkedView.requestLayout();
    }

    /**
     * Shows this overlay with its current settings. Calls updateLinkedView() after showing overlay.
     * If the overlay is already showing, no action is taken.
     * @param animation the duration of the animation used when changing visibility in milliseconds.
     *                  Set to <= 0 to disable animation. Animation used is an alpha fade.
     */
    public void showOverlay(int animation)
    {
        if(linkedView.getVisibility() == View.VISIBLE) return;

        if(animation <= 0){
            updateLinkedView();
            linkedView.setVisibility(View.VISIBLE);
            return;
        }

        // Make sure that the linked view's settings are current before showing.
        updateLinkedView();

        // Set the overlay's alpha to 0, set it to visible (although invisible to the user),
        // and then animate it to become visible to the user using a fade.
        linkedView.setAlpha(0.0f);
        linkedView.setVisibility(View.VISIBLE);
        linkedView.animate().alpha(255.0f).setDuration(animation).start();
    }

    /**
     * Hides this overlay. Hidden overlays keep their settings.
     * If the overlay is already hidden, no action is taken.
     * @param animation the duration of the animation used when changing visibility in milliseconds.
     *                  Set to <= 0 to disable animation. Animation used is an alpha fade.
     */
    public void hideOverlay(int animation)
    {
        if(linkedView.getVisibility() == View.GONE) return;

        if(animation <= 0) {
            linkedView.setVisibility(View.GONE);
            return;
        }

        // Animate the overlay to disappear to the user, and post a delayed visibility switch
        // to the overlay so that it disappears entirely once the animation finishes.
        linkedView.animate().alpha(0.0f).setDuration(animation).start();
        linkedView.postDelayed(new Runnable() {
            @Override
            public void run() {
                 linkedView.setVisibility(View.GONE);
            }
        }, animation);
    }

    /**
     * Shows the next scene in the scene queue. If the overlay is currently hidden, the overlay counter is
     * reset to zero and the overlay is shown. If the currently displaying scene is the last in
     * the queue, the overlay will be hidden. If the scene queue is empty or null, no action will be taken.
     * @param animation if the overlay must be hidden or shown, the duration of the animation used in milliseconds.
     *                  Set to <= 0 to disable animation. Animation used is an alpha fade.
     */
    public void showNextScene(int animation)
    {
        if(scenes == null || scenes.size() == 0) return;

        // If the current scene is the last in the queue, hide overlay, reset counter, and return.
        if(sceneCounter == scenes.size()){
            sceneCounter = 0;
            hideOverlay(animation);
            return;
        }

        if(this.linkedView.getVisibility() == View.GONE) sceneCounter = 0;

        // Set internal values from scene object.
        HelpOverlayScene scene = scenes.get(sceneCounter);

        this.labelText = scene.getLabel();
        this.labelBGColor = scene.getBGColorID() == -1 ? DEFAULT_LABEL_COLOR : scene.getBGColorID();

        if(scene.isUsingViewTarget()){
            this.targetView = scene.getTarget();
        }else{
            this.manualXPos = scene.getX();
            this.manualYPos = scene.getY();
            this.manualXSize = scene.getWidth();
            this.manualYSize = scene.getHeight();
        }

        this.maxLabelWidth = scene.getMaxLabelWidth();

        sceneCounter ++;

        if(this.linkedView.getVisibility() == View.GONE) showOverlay(animation);
        else updateLinkedView();
    }

    /**
     * Checks to see if a View is occluded by any part of the screen border.
     * @param v the View on which to check occlusion
     * @return true if the view is partially or totally occluded, false if not
     */
    private boolean isOccluded(@NonNull View v)
    {
        float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        return v.getY() + v.getHeight() > screenHeight || v.getY() < 0
                || v.getX() + v.getWidth() > screenWidth || v.getX() < 0;
    }

    /**
     * Checks the alignment of a view to a target. Rotates the view's alignment axis to avoid
     * occlusion by the edge of the screen. Defaults to negative-Y alignment with X-axis centering.
     * @param t the target View to align to
     * @param v the View to align to the target
     */
    private static void adaptiveCorrelation(@NonNull View t, @NonNull View v)
    {
        int tX = (int)t.getX();
        int tY = (int)t.getY();
        int tW = t.getWidth();
        int tH = t.getHeight();

        int vW = v.getWidth();
        int vH = v.getHeight();

        int dW = Resources.getSystem().getDisplayMetrics().widthPixels; // correct
        int dH = Resources.getSystem().getDisplayMetrics().heightPixels; // correct

        int rX = 0;
        int rY = 0;

        // WARNING: State engine with large amounts of undocumented algebraic expressions ahead.
        // If this sort of thing makes you have assembly language flashbacks, just assume that it works and move on.

        boolean complete = false;
        // If the target is a vertically-biased parallelogram, try Y-align first instead of X-align.
        // If the target is a cube or a horizontally-biased parallelogram, try X-align first.
        boolean mode_isY = tH > tW;
        boolean altAlign = false;
        boolean defAlign = false;
        int check = 0;

        do{
            if(!mode_isY && !defAlign)
            {
                // try positive-X align first
                if(tX + tW + vW < dW){
                    rX = tX + tW;
                    rY = (tY + (tH / 2)) - (vH / 2);
                    complete = true;
                }else if(tX - tW > 0){ // if that failed, try negative-X align instead
                    rX = tX - vW;
                    rY = (tY + (tH / 2)) - (vH / 2);
                    complete = true;
                }else{ // couldn't do either X-align, try Y-align instead
                    // check if the other alignment has already been tried. If so, set the default flag.
                    // If not, tell the loop to try the other method and set the try flag.
                    if(altAlign) defAlign = true;
                    mode_isY = true;
                    altAlign = true;
                }
            }else if(mode_isY && !defAlign)
            {
                // try negative-Y align first
                if(tY - vH > 0){
                    rY = tY - vH;
                    rX = (tX + (tW / 2)) - (vW / 2);
                    complete = true;
                }else if(tY + tH + vH < dH){ // if that failed, try positive-Y align instead
                    rY = tY + tH;
                    rX = (tX + (tW / 2)) - (vW / 2);
                    complete = true;
                }else{ // couldn't do either X, try X-align instead
                    // check if the other alignment has already been tried. If so, set the default flag.
                    // If not, tell the loop to try the other method and set the try flag.
                    if(altAlign) defAlign = true;
                    mode_isY = false;
                    altAlign = true;
                }
            }else{
                // couldn't do either alignment, so default to negative-Y.
                rY = tY - vH;
                rX = (tX + (tW / 2)) - (vW / 2);
                complete = true;
            }

            // Check if the engine has run longer than it should (theoretical limit should be 4 state transitions).
            // If it has run longer, set the result values to -0x7FFFFFF (a result that should not be possible), and break.
            if(check > 4){
                rX = -1 * 0x7FFFFFF;
                rY = -1 * 0x7FFFFFF;
                break;
            }else{
                check ++;
            }

        }while(!complete);

        if(rX == (-1 * 0x7FFFFFF) || rY == (-1 * 0x7FFFFFF)){
            throw new IllegalStateException("Position state engine terminated: no valid position");
        }

        v.setX(rX);
        v.setY(rY);
    }
}
