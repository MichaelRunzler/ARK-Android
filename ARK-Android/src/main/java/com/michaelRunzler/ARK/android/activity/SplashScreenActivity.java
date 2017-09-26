package com.michaelRunzler.ARK.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.Settings.SettingsManager;
import com.michaelRunzler.ARK.android.util.Settings.SettingsManagerDelegator;

/**
 * Default template class for the ARK Android UI settings screen.
 * Copy and modify this class and its associated XML template files
 * if you wish to make your own version.
 */
public class SplashScreenActivity extends AppCompatActivity
{
    //  !!! TEMPLATE FILE - DO NOT MODIFY !!!
    //  READ JAVADOC BEFORE USING

    //
    // SYSTEM INITIALIZATION
    //

    private SettingsManager settingsManager;

    /**
     * Called when the Android System starts this app. Add any method calls to be run during initialization here.
     * @param savedInstanceState the saved app instance state from the Android System Process Manager,
     *                           if any. This is dealt with by the system - do not modify.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize and display splash screen.
        setContentView(R.layout.splash_screen);
        initializeSplashUI();

        // Initialize main program elements, display main menu after load is completed.
        initializeSystem();
    }

    /**
     * Initializes the splash screen's UI element settings.
     */
    private void initializeSplashUI()
    {
        // Lock screen orientation to current to avoid reloads while system initialization is in progress.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ((ProgressBar)findViewById(R.id.splash_progress_bar)).getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        ((ProgressBar)findViewById(R.id.splash_progress_spinner)).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    /**
     * Add your code for system background initialization here.
     * Use the postASyncProgress method to increment the progress bar on the splash UI.
     */
    private void initializeSystem()
    {
        // Get the global instance of the settings manager object.
        settingsManager = SettingsManagerDelegator.getMainInstance();

        //todo temporary config placeholder
        settingsManager.setDefaultSetting("menuToolbarSize", 1);
        settingsManager.loadDefault("menuToolbarSize");

        // Test pseudo-load segment
        final Handler msg = new Handler();
        final Context context = this.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ProgressBar splashProgress = (ProgressBar)findViewById(R.id.splash_progress_bar);
                for(int i = 0; i < 100; i++){
                    splashProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            splashProgress.incrementProgressBy(1);
                        }
                    });

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                msg.post(new Runnable() {
                    public void run() {
                        // Initialize and display main screen, exiting this activity in the process.
                        finish();
                        startActivity(new Intent(context, MainActivity.class));
                    }
                });
            }
        }).start();
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

    /**
     * Updates a progress bar on the main thread from an asynchronous subthread.
     * Must be used to avoid throwing access violations when attempting to do this.
     * @param target the ProgressBar View Object to update
     * @param progress the amount of progress to post to the target
     */
    private void postASyncProgress(final ProgressBar target, final int progress)
    {
        if(target == null) return;
        target.post(new Runnable() {
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
