package com.michaelRunzler.ARK.android.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.ButtonVariants.HybridFileSelectButton;
import com.michaelRunzler.ARK.android.util.ButtonVariants.HybridMultiSelectButton;
import com.michaelRunzler.ARK.android.util.ButtonVariants.HybridSettingsButton;
import com.michaelRunzler.ARK.android.util.DialogFragments.DialogActionEventHandler;
import com.michaelRunzler.ARK.android.util.DialogFragments.YNDialogFragment;
import com.michaelRunzler.ARK.android.util.SettingsManager;
import com.michaelRunzler.ARK.android.util.SettingsManagerDelegator;

import java.io.File;
import java.util.HashMap;

/**
 * Default template class for the ARK Android UI settings menu.
 * Copy and modify this class and its associated XML template files
 * if you wish to make your own version.
 */
public class SettingsMenuActivity extends AppCompatActivity
{
    //  !!! TEMPLATE FILE - DO NOT MODIFY !!!
    //  READ JAVADOC BEFORE USING

    HashMap<View, HybridSettingsButton> buttonIndex;
    private SettingsManager settingsManager;

    //
    // SYSTEM INITIALIZATION
    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        buttonIndex = new HashMap<>();
        settingsManager = SettingsManagerDelegator.getMainInstance();

        // Check to see if the SettingsManager object that we just pulled has a stored cache.
        // If it does, it's likely that this Activity is reloading from an orientation change or other
        // such automatic event, so we can just load, assuming that the stored copy is valid.
        // If not, store a cached copy of current settings. We'll use them if the user decides to
        // exit without saving settings.
        if(settingsManager.getCache() == null){
            settingsManager.fillCache();
        }

        setContentView(R.layout.settings_menu);

        initializeSettingsUI();
        setSettingsUIContentActions();
    }

    /**
     * Initializes the settings menu's UI element settings.
     */
    private void initializeSettingsUI()
    {
        addLongClickToast(R.id.settings_title_bar_back_button, R.string.settings_title_bar_back_button_toast, Toast.LENGTH_SHORT);
        addLongClickToast(R.id.settings_title_bar_save_button, R.string.settings_title_bar_save_button_toast, Toast.LENGTH_SHORT);
        addLongClickToast(R.id.settings_title_bar_reset_button, R.string.settings_title_bar_reset_button_toast, Toast.LENGTH_SHORT);
    }

    /**
     * Use this method to add your own settings UI actions through the shortcut methods
     * listed in the Utility/Shortcut Method section.
     */
    private void setSettingsUIContentActions()
    {
        // TODO remove test buttons when done
        String button1ID = "menuToolbarSize";
        String button2ID = "testID2";
        String button3ID = "testID3";

        String[] sizes = new String[]{"Small", "Medium", "Large"};
        RelativeLayout button1 = (RelativeLayout)findViewById(R.id.settings_test_button_1);
        HybridMultiSelectButton testButton1 = new HybridMultiSelectButton(button1, "Menu Toolbar Size", null, button1ID, (Integer)settingsManager.getSetting(button1ID), sizes);

        RelativeLayout button2 = (RelativeLayout)findViewById(R.id.settings_test_button_2);
        HybridFileSelectButton testButton2 = new HybridFileSelectButton(button2, "File Select Test 2", null, button2ID, null, (File)settingsManager.getSetting(button2ID));

        RelativeLayout button3 = (RelativeLayout)findViewById(R.id.settings_test_button_3);
        HybridFileSelectButton testButton3 = new HybridFileSelectButton(button3, "File Select Test", null, button3ID, null, (File)settingsManager.getSetting(button3ID));

        testButton1.setDefaultState((Integer)settingsManager.getDefaultSetting(button1ID));
        testButton2.setDefaultState((File)settingsManager.getDefaultSetting(button2ID));
        testButton3.setDefaultState((File)settingsManager.getDefaultSetting(button3ID));

        buttonIndex.put(button1, testButton1);
        buttonIndex.put(button2, testButton2);
        buttonIndex.put(button3, testButton3);
    }


    //
    // UTILITY / SHORTCUT METHODS
    //


    /**
     * Adds a Toast notification to a UI element capable of reading longPress inputs.
     * @param elementID the Android Element ID of the UI element to add the toast to
     * @param toastStringID the Android String ID of the XML String value to display on the added toast
     * @param length the length the toast should display when called. Use the constants in the Toast class for length
     *               unless a custom length is desired
     */
    private void addLongClickToast(int elementID, final int toastStringID, final int length)
    {
        findViewById(elementID).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), toastStringID, length).show();
                return true;
            }
        });
    }


    //
    //DELEGATION METHODS
    //


    // this section is empty in template classes


    //
    // UI ELEMENT ACTION LISTENERS
    //

    public void settingsBack(View view)
    {
        // Check if anything has changed from the stored settings copy. If they haven't, skip the confirmation dialog.
        boolean identical = true;
        HashMap<String, Object> cache = settingsManager.getCache();
        HashMap<String, Object> live = settingsManager.getSettings();

        // If the two maps are mapped to the same object, OR both caches are NOT null and their sizes are the same,
        // (so in other words, the two are not obviously different in some way), proceed to test them for sameness.
        if(cache == live || ((cache != null && live != null) && cache.size() == live.size())){
            // Iterate through the key set of the live map, testing to make sure that each key is also
            // possessed by the cached map, and that all objects referenced by the keys are identical.
            // If any key violates this convention, flag as such and break the loop.
            for(String k : live.keySet()){
                if(!cache.containsKey(k) || live.get(k) != cache.get(k)){
                    identical = false;
                    break;
                }
            }
        }else{
            // The two are obviously different in some way, even without testing their contents, so we can assume that something was changed.
            identical = false;
        }

        if(identical){
            // The user went back before changing anything, so the cache and live copies are the exact same.
            // Clear the cache to free up memory before cleaning up the activity.
            settingsManager.clearCache();
            Toast.makeText(getApplicationContext(), R.string.settings_cancel_no_changes_notice_toast, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        YNDialogFragment query = new YNDialogFragment();

        query.setProperties(getString(R.string.settings_cancel_notice_query), getString(R.string.settings_cancel_notice_continue), null, -1, new DialogActionEventHandler() {
            @Override
            public void handleEvent(ResultID resultID, Object... result)
            {
                // Check that the user chose the 'yes' option. If not, exit without doing anything.
                if(resultID != ResultID.POSITIVE) return;

                // The user cancelled current changes, so we tell the SettingsManager to push its stored cache from
                // earlier to the main index before returning to the menu.
                settingsManager.commitCache();
                finish();
            }
        });

        query.show(getFragmentManager(), "settingsCancelNotice");
    }

    public void settingsReset(View view)
    {
        YNDialogFragment query = new YNDialogFragment();

        query.setProperties(getString(R.string.settings_reset_notice_query), null, null, -1, new DialogActionEventHandler() {
            @Override
            public void handleEvent(ResultID resultID, Object... result)
            {
                // Check that the user chose the 'yes' option. If not, exit without doing anything.
                if(resultID != ResultID.POSITIVE) return;

                // Tell each button to load from its default state, notify the SettingsManager, and update its
                // linked View object.
                for (HybridSettingsButton s : buttonIndex.values()) {
                    s.loadDefaultState(settingsManager);
                    s.updateLinkedView();
                }

                Toast.makeText(getApplicationContext(), R.string.settings_reset_notice_toast, Toast.LENGTH_LONG).show();
            }
        });

        query.show(getFragmentManager(), "settingsResetNotice");
    }

    public void settingsApply(View view)
    {
        // The user accepted the modified settings (if there were any), so we tell the SettingsManager
        // to drop its stored index copy, since we don't need it anymore.
        settingsManager.clearCache();
        Toast.makeText(getApplicationContext(), R.string.settings_apply_notice_toast, Toast.LENGTH_LONG).show();
        finish();
    }

    // DYNAMIC - NO NEED TO MODIFY
    public void settingsButton(View view)
    {
        // Check to make sure that the View calling this method is a valid Button member of a Hybrid Settings Button element.
        if(!(view instanceof Button)) return;

        // Yes, this LOOKS redundant, but remember: this element is nested inside of TWO layouts, and this makes things simpler.
        FrameLayout parent = (FrameLayout)view.getParent();
        if(!(parent.getParent() instanceof RelativeLayout)) return;
        if(!(buttonIndex.containsKey(parent.getParent()))) return;

        // Tell the linked hybrid button object to execute its interaction handling routine.
        buttonIndex.get(parent.getParent()).handleInteract(settingsManager);
    }
}
