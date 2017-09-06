package com.michaelRunzler.ARK.android.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.michaelRunzler.ARK.android.R;

/**
 * Default template class for the ARK Android UI settings menu.
 * Copy and modify this class and its associated XML template files
 * if you wish to make your own version.
 */
public class SettingsMenuActivity extends AppCompatActivity
{
    //  !!! TEMPLATE FILE - DO NOT MODIFY !!!
    //  READ JAVADOC BEFORE USING

    //
    // SYSTEM INITIALIZATION
    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);

        initializeSettingsUI();
        setSettingsUIContentActions();
    }

    /**
     * Initializes the settings menu's UI element settings.
     */
    private void initializeSettingsUI()
    {

    }

    /**
     * Use this method to add your own settings UI actions through the shortcut methods
     * listed in the Utility/Shortcut Method section.
     */
    private void setSettingsUIContentActions()
    {

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
    public void addLongClickToast(int elementID, final int toastStringID, final int length)
    {
        findViewById(elementID).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), toastStringID, length).show();
                return true;
            }
        });
    }

    /**
     * Updates a progress bar on the main thread from an asynchronous subthread.
     * Must be used to avoid throwing access violations when attempting to do this.
     * @param target the ProgressBar View Object to update
     * @param progress the amount of progress to post to the target
     */
    public void postASyncProgress(final ProgressBar target, final int progress)
    {
        if(target == null) return;
        new Handler().post(new Runnable() {
            public void run() {
                target.incrementProgressBy(progress);
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


}
