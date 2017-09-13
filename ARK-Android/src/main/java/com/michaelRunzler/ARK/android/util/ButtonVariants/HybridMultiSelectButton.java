package com.michaelRunzler.ARK.android.util.ButtonVariants;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.SettingsManager;

public class HybridMultiSelectButton extends HybridSettingsButton
{
    private String[] DEFAULT_OPTIONS;
    private int SET_COLOR;
    private int DEFAULT_COLOR;
    private int DEFAULT_STATE;

    private String[] options;
    private int state;

    /**
     * A variant of the hybrid button, this allows a user to select a file from the Android filesystem,
     * and displays whether or not said value has been set yet.
     * @param view the Hybrid Button XML element to link this object to. Cannot be null. The Context
     *             being used by this element will be used as the Context to derive such things as colors,
     *             preset Strings, and element unit IDs. This context is not cached for future use.
     * @param desc The text to show on the button. Setting this to null will default it to 'Button'.
     * @param icon The Android Drawable reference to set this button's icon to. Setting this to
     *             null will default it to the Android 'New Folder' icon.
     * @param settingID the SettingsManager Setting ID to use for processing output. Setting this to
     *                  null will cause interaction handling to update only the internal state variable,
     *                  and not the settings index.
     * @param initialState the index within the provided String array to use as this object's initial state.
     *                     This value will also be used as the initial default state.
     * @param options a list of option text segments that the button cycles through when interacted with.
     *                Index 0 of this array will be used as the default state of this button.
     *                Setting this to null will default it to the array specified in arrays.xml.
     */
    public HybridMultiSelectButton(RelativeLayout view, String desc, Drawable icon, String settingID,
                                   Integer initialState, String... options)
    {
        super(view, desc, icon, settingID);

        Context context = view.getContext();

        DEFAULT_OPTIONS = context.getResources().getStringArray(R.array.default_settings_multiSelect_button_options);

        SET_COLOR = context.getResources().getColor(R.color.setSettingsButtonTextColor);
        DEFAULT_COLOR = context.getResources().getColor(R.color.defaultSettingsButtonTextColor);

        this.options = (options == null || options.length == 0) ? DEFAULT_OPTIONS : options;
        this.state = initialState == null ? 0 : initialState;
        DEFAULT_STATE = state;

        updateLinkedView();
    }

    /**
     * Handles an interact request for this button object.
     * In this case, the button will cycle between its various options in turn and update the
     * SettingsManager with the index of the current state after changes have been applied.
     * @param manager the SettingsManager object to deliver the result to. If null, this object
     *                will skip result delivery and simply update its internal state.
     */
    @Override
    public void handleInteract(SettingsManager manager)
    {
        if(state < options.length - 1) state ++;
        else state = 0;

        if(manager != null && super.settingID != null) manager.storeSetting(super.settingID, this.state);

        updateLinkedView();
    }

    /**
     * Gets the current state of this button.
     * @return the index of the currently selected display string
     */
    public int getState()
    {
        return state;
    }

    /**
     * Manually sets the state of this button. No changes to the actual button will occur until
     * updateLinkedView() is called.
     * @param state the index of the desired display string. Values below 0 or above the length of the
     *              state index will result in an exception.
     */
    public void setState(int state) {
        if(state < 0 || state >= options.length) throw new IllegalArgumentException("Provided state index is outside of valid range");
        this.state = state;
    }

    /**
     * Gets the current state index. Returned object is a copy of the index, not a reference.
     * @return the current display string index
     */
    public String[] getStateIndex() {
        return options.clone();
    }

    /**
     * Updates the state index to the specified values. Providing a null value or a zero-length array
     * will reset the state index to its default values, specified in strings.xml.
     * @param options a String array or multiple strings representing the desired selection of options to display
     */
    public void setStateIndex(String... options) {
        this.options = (options == null || options.length == 0) ? DEFAULT_OPTIONS : options;
    }

    @Override
    public void updateLinkedView()
    {
        super.linkedText.setTextColor(state == 0 ? DEFAULT_COLOR : SET_COLOR);
        super.linkedText.setText(options[state]);

        super.updateLinkedView();
    }

    /**
     * Sets the default state of this object to the specified integer. By default, 0 is used as the
     * default state.
     * @param defaultState the integer to use as this object's default state
     */
    public void setDefaultState(Integer defaultState) {
        this.DEFAULT_STATE = defaultState == null ? 0 : defaultState;
    }

    @Override
    public void loadDefaultState(SettingsManager manager) {
        this.state = DEFAULT_STATE;
        if(manager != null && super.settingID != null) manager.storeSetting(super.settingID, this.state);
    }
}
