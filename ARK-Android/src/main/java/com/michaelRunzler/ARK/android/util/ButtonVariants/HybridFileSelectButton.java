package com.michaelRunzler.ARK.android.util.ButtonVariants;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.DialogFragments.DialogActionEventHandler;
import com.michaelRunzler.ARK.android.util.DialogFragments.FileSelectDialogFragment;
import com.michaelRunzler.ARK.android.util.Settings.SettingsManager;

import java.io.File;

public class HybridFileSelectButton extends HybridSettingsButton
{
    private String DEFAULT_REQUEST_TEXT;
    private String SET_TEXT;
    private String UNSET_TEXT;
    private int SET_COLOR;
    private int UNSET_COLOR;
    private File DEFAULT_STATE;

    private String requestText;
    private File state;
    private File rootDir;

    /**
     * A variant of the {@link HybridSettingsButton}, this allows a user to select a file from the Android filesystem,
     * and displays whether or not said value has been set yet.
     * @param view the Hybrid Button XML element to link this object to. Cannot be {@code null}. The {@link Context}
     *             being used by this element will be used to derive such things as colors,
     *             preset Strings, and element unit IDs. This context is not cached for future use.
     * @param desc The text to show on the button. Setting this to {@code null} will default it to 'Button'.
     * @param icon The {@link Drawable} reference to set this button's icon to. Setting this to
     *             null will default it to the Android 'New Folder' icon.
     * @param requestText the text that the button should display in the prompt for file selection
     * @param initialState the {@link File} that the button should store as its initial internal state. Useful
     *                     if the program using the button needs to preset the value to a default,
     *                     previously set value, or something similar.
     *                     This value will also be used as the initial default state.
     * @param settingID the {@link SettingsManager} Setting ID to use for processing output. Setting this to
     *                  {@code null} will cause interaction handling to update only the internal state variable,
     *                  and not the settings index.
     * @param rootDir the root directory to use when loading the file list. {@code null} or invalid values will result in
     *                an {@link IllegalArgumentException}.
     */
    public HybridFileSelectButton(@NonNull RelativeLayout view, @Nullable String desc, @Nullable Drawable icon, @Nullable String settingID,
                                  String requestText, @Nullable File initialState, @NonNull File rootDir)
    {
        super(view, desc, icon, settingID);

        Context context = view.getContext();
        if(context == null) throw new IllegalArgumentException("Provided view does not have any associated Context!");

        DEFAULT_REQUEST_TEXT = context.getResources().getString(R.string.default_settings_fileSelect_button_request);
        SET_TEXT = context.getResources().getString(R.string.default_settings_button_set);
        UNSET_TEXT = context.getResources().getString(R.string.default_settings_button_unset);
        SET_COLOR = context.getResources().getColor(R.color.setSettingsButtonTextColor);
        UNSET_COLOR = context.getResources().getColor(R.color.unsetSettingsButtonTextColor);

        this.DEFAULT_STATE = initialState;
        this.requestText = requestText == null ? DEFAULT_REQUEST_TEXT : requestText;
        this.state = initialState;
        if(rootDir == null || !rootDir.exists() || rootDir.isFile()){
            throw new IllegalArgumentException("Supplied root directory File is invalid");
        }else{
            this.rootDir = rootDir;
        }

        updateLinkedView();
    }

    /**
     * Handles an interact request for this button object.
     * In this case, the button will display a dialog asking the user to select a file in the
     * Android filesystem, with prompt text specified in this object's internal variables.
     * Automatically updates the button's appearance and stored state when finished.
     * @param manager the {@link SettingsManager} object to deliver the result file to. If {@code null}, this object
     *                will skip result delivery and simply update its internal state.
     */
    @Override
    public void handleInteract(final SettingsManager manager)
    {
        FileSelectDialogFragment dialog = new FileSelectDialogFragment();

        final String settingID = super.settingID;
        DialogActionEventHandler handler = new DialogActionEventHandler() {
            @Override
            public void handleEvent(ResultID resultID, Object... result) {
                if(resultID == ResultID.SUBMITTED) {
                    if (manager != null && settingID != null)
                        manager.storeSetting(settingID, result[0]);

                    setState((File)result[0]);
                    updateLinkedView();
                }
            }
        };

        dialog.setProperties(rootDir, requestText, handler);
        dialog.show(((Activity)linkedView.getContext()).getFragmentManager(), "settingsButton:" + this.toString() + ":FileSelectDialog");
    }

    /**
     * Gets this button's internal stored state.
     * @return the {@link File} that this object is storing as its state
     */
    public File getState() {
        return state;
    }

    /**
     * Sets this button's stored state. This determines the button's displayed state, as well as
     * how it handles return values from interact requests.
     * @param state the {@link File} that this button should store as its internal state
     */
    public void setState(File state) {
        this.state = state;
        updateLinkedView();
    }

    /**
     * Gets the current root directory used by this button's file select mechanism.
     * @return a {@link File} representing the current root directory
     */
    public File getRootDir(){
        return rootDir;
    }

    /**
     * Sets this button's file select root directory.
     * @param rootDir the root directory to use when loading the file list. {@code null} or invalid values will result in
     *                an exception.
     */
    public void setRootDir(File rootDir){
        if(rootDir == null || !rootDir.exists() || rootDir.isFile()){
            throw new IllegalArgumentException("Supplied root directory File is invalid");
        }else{
            this.rootDir = rootDir;
        }
    }

    /**
     * Gets the text that this button is currently using for file select prompting.
     * @return the text that displays when this object is requested to handle file interaction
     */
    public String getRequestText() {
        return requestText;
    }

    /**
     * Sets the text that this button displays in its file select dialog when it is requested to handle
     * interaction.
     * @param requestText the text that this object should use for file select interaction handling.
     *                    Setting this to {@code null} will use the default text for this object subtype, specified
     *                    in {@code strings.xml}.
     */
    public void setRequestText(String requestText) {
        this.requestText = requestText == null ? DEFAULT_REQUEST_TEXT : requestText;
    }

    @Override
    public void updateLinkedView()
    {
        super.linkedText.setText(state == DEFAULT_STATE ? UNSET_TEXT : SET_TEXT);
        super.linkedText.setTextColor(state == DEFAULT_STATE ? UNSET_COLOR : SET_COLOR);

        super.updateLinkedView();
    }

    /**
     * Sets the default state of this object to the specified {@link File}. By default, {@code null} is used as the
     * default state.
     * @param defaultState the {@link File} to use as this object's default state
     */
    public void setDefaultState(File defaultState) {
        this.DEFAULT_STATE = defaultState;
        updateLinkedView();
    }

    @Override
    public void loadDefaultState(SettingsManager manager) {
        this.state = DEFAULT_STATE;
        if(manager != null && super.settingID != null) manager.storeSetting(super.settingID, this.state);
    }
}
