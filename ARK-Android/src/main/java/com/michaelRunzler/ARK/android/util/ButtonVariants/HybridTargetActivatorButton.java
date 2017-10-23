package com.michaelRunzler.ARK.android.util.ButtonVariants;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.Settings.SettingsManager;

public class HybridTargetActivatorButton extends HybridSettingsButton
{
    private String SET_TEXT;
    private String UNSET_TEXT;
    private int SET_COLOR;
    private int UNSET_COLOR;
    private boolean DEFAULT_STATE;

    private boolean state;
    private TargetActivatorButtonEventHandler handler;

    /**
     * Functions aa sub-variant of the basic BooleanSelect button, but when the user clicks the button,
     * the state is not automatically toggled, and an externally-defined action may be taken as specified
     * by a Handler object, essentially making the button's function customizable. All other functionality
     * is exactly the same as the Boolean Select button.
     * @param view the Hybrid Button XML element to link this object to. Cannot be null. The Context
     *             being used by this element will be used as the Context to derive such things as colors,
     *             preset Strings, and element unit IDs.
     * @param desc The text to show on the button. Setting this to null will default it to 'Button'.
     * @param icon The Android Drawable reference to set this button's icon to. Setting this to
     *             null will default it to the Android 'New Folder' icon.
     * @param initialState the value that the button should store as its initial internal state. Useful
     *                     if the program using the button needs to preset the value to a default,
     *                     previously set value, or something similar.
     *                     This value will also be used as the initial default state.
     * @param settingID the SettingsManager Setting ID to use for processing output. Setting this to
     *                  null will cause interaction handling to update only the internal state variable,
     *                  and not the settings index.
     * @param handler the Event Handler to handle interaction with the button. Null values will simply
     *                cause any interaction to be ignored.
     */
    public HybridTargetActivatorButton(RelativeLayout view, String desc, Drawable icon, String settingID,
                                     boolean initialState, TargetActivatorButtonEventHandler handler)
    {
        super(view, desc, icon, settingID);

        Context context = view.getContext();
        if(context == null) throw new IllegalArgumentException("Provided view does not have any associated Context!");

        SET_TEXT = context.getResources().getString(R.string.default_settings_button_set);
        UNSET_TEXT = context.getResources().getString(R.string.default_settings_button_unset);
        SET_COLOR = context.getResources().getColor(R.color.setSettingsButtonTextColor);
        UNSET_COLOR = context.getResources().getColor(R.color.unsetSettingsButtonTextColor);

        this.DEFAULT_STATE = initialState;
        this.state = initialState;
        this.handler = handler;

        updateLinkedView();
    }

    /**
     * Handles an interact request for this button object.
     * Automatically updates the button's appearance and stored state when finished.
     * Will not take any action if the Handler class provided is null.
     * @param manager the SettingsManager object to deliver the updated state to. If null, this object
     *                will skip result delivery and simply update its internal state.
     */
    @Override
    public void handleInteract(SettingsManager manager)
    {
        if(handler != null){
            state = handler.handleEvent(state);
            if(manager != null && super.settingID != null) manager.storeSetting(super.settingID, this.state);
            updateLinkedView();
        }
    }

    /**
     * Gets this button's internal stored state.
     * @return the boolean that this object is storing as its state
     */
    public boolean getState() {
        return state;
    }

    /**
     * Sets this button's stored state. This determines the button's displayed state, as well as
     * how it handles return values from interact requests.
     * @param state the boolean that this button should store as its internal state
     */
    public void setState(boolean state) {
        this.state = state;
        updateLinkedView();
    }

    /**
     * Gets the current handler for this button.
     * @return the current interaction event handler object
     */
    public TargetActivatorButtonEventHandler getHandler() {
        return handler;
    }

    /**
     * Sets the handler object for this button.
     * @param handler the Event Handler to handle interaction with the button. Null values will simply
     *                cause any interaction to be ignored.
     */
    public void setHandler(TargetActivatorButtonEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void updateLinkedView()
    {
        super.linkedText.setText(state ? SET_TEXT : UNSET_TEXT);
        super.linkedText.setTextColor(state ? SET_COLOR : UNSET_COLOR);

        super.updateLinkedView();
    }

    /**
     * Sets the default state of this object to the specified boolean value. By default, false is used as the
     * default state.
     * @param defaultState the boolean to use as this object's default state
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

