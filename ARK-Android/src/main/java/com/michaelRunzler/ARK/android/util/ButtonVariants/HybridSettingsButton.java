package com.michaelRunzler.ARK.android.util.ButtonVariants;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.SettingsManager;

/**
 * Determines settings, behavior, and handling for a linked hybrid settings button (instances of the
 * XML layout hybrid_settings_button). Abstract, cannot be instantiated. Use a subclass of this instead,
 * or create one yourself.
 */
public abstract class HybridSettingsButton
{
    protected Drawable DEFAULT_ICON;
    protected String DEFAULT_DESC;

    protected RelativeLayout linkedView;
    protected String desc;
    protected Drawable icon;
    protected String settingID;
    protected Button linkedButton;
    protected TextView linkedText;

    /**
     * Initializes a new linked Hybrid Settings Button object.
     * @param view the Hybrid Button XML element to link this object to. Cannot be null. The Context
     *             being used by this element will be used as the Context to derive such things as colors,
     *             preset Strings, and element unit IDs.
     * @param desc The text to show on the button. Setting this to null will default it to 'Button'.
     * @param icon The Android Drawable reference to set this button's icon to. Setting this to
     *             null will default it to the Android 'New Folder' icon.
     * @param settingID the SettingsManager Setting ID to use for processing output. This is used by
     *                  subclasses to handle output from user interaction. Setting this to null will
     *                  cause interaction handling to update only the internal state variable, and
     *                  not the settings index.
     */
    HybridSettingsButton(RelativeLayout view, String desc, Drawable icon, String settingID)
    {
        // Check that the provided View is actually a proper instance of a hybrid button XML element.
        if(view == null) throw new IllegalArgumentException("Linked view cannot be null");

        if(!(view.getChildAt(0) instanceof FrameLayout)|| !(view.getChildAt(1) instanceof FrameLayout))
            throw new IllegalArgumentException("Linked view must be a Hybrid Settings Button element");

        this.linkedView = view;
        this.settingID = settingID;

        // For ease-of-use, derive the subelements in the linked view from their parent layouts.
        this.linkedButton = (Button)((FrameLayout)view.getChildAt(0)).getChildAt(0);
        this.linkedText = (TextView)((FrameLayout)view.getChildAt(1)).getChildAt(0);

        Context context = view.getContext();

        this.DEFAULT_DESC = context.getResources().getString(R.string.default_button_desc);
        this.DEFAULT_ICON = ContextCompat.getDrawable(context, R.drawable.ic_folder_24dp);

        this.desc = desc == null ? DEFAULT_DESC : desc;
        this.icon = icon == null ? DEFAULT_ICON : icon;
    }

    /**
     * Gets the Hybrid Button XML element that this object is currently linked to.
     * @return the View that this object is linked to
     */
    public RelativeLayout getLinkedView() {
        return linkedView;
    }

    /**
     * Gets this button's description.
     * @return the text currently displayed by this button
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Sets the display text for this button object.
     * Does not update the linked view until the updateLinkedView() method is called.
     * @param desc the text to display on the button.
     *             Providing a null reference will default to the state provided by the Context
     *             that was given as an argument to this object's constructor.
     */
    public void setDescription(String desc) {
        this.desc = desc == null ? DEFAULT_DESC : desc;
    }

    /**
     * Gets this button's icon.
     * @return the Drawable that this object is currently displaying as its icon
     */
    public Drawable getIcon() {
        return this.icon;
    }

    /**
     * Sets the icon for this button object.
     * Does not update the linked view until the updateLinkedView() method is called.
     * @param icon the Drawable to set this button's icon to.
     *             Providing a null reference will default to the state provided by the Context
     *             that was given as an argument to this object's constructor.
     */
    public void setIcon(Drawable icon) {
        this.icon = icon == null ? DEFAULT_ICON : icon;
    }

    /**
     * Updates this object's linked View object with this object's current icon and description.
     */
    public void updateLinkedView()
    {
        linkedButton.setCompoundDrawablesWithIntrinsicBounds(null, icon == null ? DEFAULT_ICON : icon, null, null);
        linkedButton.setText(desc == null ? DEFAULT_DESC : desc);
    }

    /**
     * Handles user interaction for this button type. Implementation is specific to the button subtype
     * implementing this method.
     */
    public abstract void handleInteract(SettingsManager manager);
}
