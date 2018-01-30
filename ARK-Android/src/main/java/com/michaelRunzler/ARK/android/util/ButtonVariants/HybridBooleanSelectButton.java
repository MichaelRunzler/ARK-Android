package com.michaelRunzler.ARK.android.util.ButtonVariants;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.Settings.SettingsManager;

public class HybridBooleanSelectButton extends HybridSettingsButton
{
    private String SET_TEXT;
    private String UNSET_TEXT;
    private int SET_COLOR;
    private int UNSET_COLOR;
    private boolean DEFAULT_STATE;

    private boolean state;

    /**
     * The simplest variant of the {@link HybridSettingsButton}, this allows a user to turn a setting on or off,
     * using a {@code boolean} value to track state internally. Functions as a 'toggle'-type button.
     * @param view the Hybrid Button XML element to link this object to. Cannot be null. The {@link Context}
     *             being used by this element will be used to derive such things as colors,
     *             preset Strings, and element unit IDs. This context is not cached for future use.
     * @param desc The text to show on the button. Setting this to null will default it to 'Button'.
     * @param icon The {@link Drawable} reference to set this button's icon to. Setting this to
     *             {@code null} will default it to the Android 'New Folder' icon.
     * @param initialState the value that the button should store as its initial internal state. Useful
     *                     if the program using the button needs to preset the value to a default,
     *                     previously set value, or something similar.
     *                     This value will also be used as the initial default state.
     * @param settingID the {@link SettingsManager} Setting ID to use for processing output. Setting this to
     *                  {@code null} will cause interaction handling to update only the internal state variable,
     *                  and not the settings index.
     */
    public HybridBooleanSelectButton(@NonNull RelativeLayout view, @Nullable String desc, @Nullable Drawable icon, @Nullable String settingID,
                                     boolean initialState)
    {
        super(view, desc, icon, settingID);

        Context context = view.getContext();
        if(context == null) throw new IllegalArgumentException("Provided view does not have any associated Context!");

        SET_TEXT = context.getResources().getString(R.string.default_boolean_button_true);
        UNSET_TEXT = context.getResources().getString(R.string.default_boolean_button_false);
        SET_COLOR = context.getResources().getColor(R.color.setSettingsButtonTextColor);
        UNSET_COLOR = context.getResources().getColor(R.color.unsetSettingsButtonTextColor);

        this.DEFAULT_STATE = initialState;
        this.state = initialState;

        updateLinkedView();
    }

    /**
     * Handles an interact request for this button object.
     * In this case, the button will toggle its internal state variable and update its appearance
     * to reflect the change.
     * @param manager the {@link SettingsManager} object to deliver the updated state to. If null, this object
     *                will skip result delivery and simply update its internal state.
     */
    @Override
    public void handleInteract(SettingsManager manager)
    {
        this.state = !this.state;

        if(manager != null && super.settingID != null) manager.storeSetting(super.settingID, this.state);

        updateLinkedView();
    }

    /**
     * Gets this button's internal stored state.
     * @return the {@code boolean} that this object is storing as its state
     */
    public boolean getState() {
        return state;
    }

    /**
     * Sets this button's stored state. This determines the button's displayed state, as well as
     * how it handles return values from interact requests.
     * @param state the {@code boolean} that this button should store as its internal state
     */
    public void setState(boolean state) {
        this.state = state;
        updateLinkedView();
    }

    @Override
    public void updateLinkedView()
    {
        super.linkedText.setText(state ? SET_TEXT : UNSET_TEXT);
        super.linkedText.setTextColor(state ? SET_COLOR : UNSET_COLOR);

        super.updateLinkedView();
    }

    /**
     * Sets the default state of this object to the specified {@code boolean} value. By default, {@code false} is used as the
     * default state.
     * @param defaultState the {@code boolean} to use as this object's default state
     */
    public void setDefaultState(boolean defaultState) {
        this.DEFAULT_STATE = defaultState;
        updateLinkedView();
    }

    @Override
    public void loadDefaultState(SettingsManager manager) {
        this.state = DEFAULT_STATE;
        if(manager != null && super.settingID != null) manager.storeSetting(super.settingID, this.state);
    }
}
