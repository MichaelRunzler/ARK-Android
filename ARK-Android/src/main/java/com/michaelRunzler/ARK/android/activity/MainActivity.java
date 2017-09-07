package com.michaelRunzler.ARK.android.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.SettingsManager;
import com.michaelRunzler.ARK.android.util.SettingsManagerDelegator;

/**
 * Default template class for the ARK Android UI system.
 * Copy and modify this class and its associated XML template files
 * if you wish to make your own version.
 */
public class MainActivity extends AppCompatActivity
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
        ((ProgressBar)findViewById(R.id.splash_progress_bar)).getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        ((ProgressBar)findViewById(R.id.splash_progress_spinner)).getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    /**
     * Add your code for system background initialization here.
     * Use the postASyncProgress method to increment the progress bar on the splash UI.
     */
    private void initializeSystem()
    {
        // Get the global instance of the settings manager object.
        settingsManager = SettingsManagerDelegator.getInstance();

        // Test pseudo-load segment
        final Handler msg = new Handler();
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
                        // Initialize and display main screen.
                        setContentView(R.layout.main_screen);
                        initializeMainUI();
                        setMainUIContentActions();
                    }
                });
            }
        }).start();
    }

    /**
     * Initializes the main screen's UI element settings.
     * Use the setUIContextActions() method for your own custom UI actions.
     */
    private void initializeMainUI()
    {
        addLongClickToast(R.id.main_sidebar_menu_button, R.string.main_sidebar_menu_button_toast, Toast.LENGTH_SHORT);
        addLongClickToast(R.id.main_sidebar_settings_button, R.string.main_sidebar_settings_button_toast, Toast.LENGTH_SHORT);
        addLongClickToast(R.id.main_sidebar_help_button, R.string.main_sidebar_help_button_toast, Toast.LENGTH_SHORT);
        addLongClickToast(R.id.main_sidebar_minimize_button, R.string.main_sidebar_minimize_button_toast, Toast.LENGTH_SHORT);
    }

    /**
     * Use this method to add your own main UI actions through the shortcut methods
     * listed in the Utility/Shortcut Method section.
     */
    private void setMainUIContentActions()
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


    /**
     * Slides out the main menu overlay and animates it.
     * @param view the View that called this method
     */
    public void activateMenuSlideout(View view)
    {
        //todo start
    }

    /**
     * Shows the settings menu and animates the settings icon.
     * @param view the View that called this method
     */
    public void showSettings(View view)
    {
        // Rotate the settings icon forward.
        findViewById(R.id.main_sidebar_settings_button).animate().rotation(359.0f).setDuration(1000).setInterpolator(new AccelerateDecelerateInterpolator());

        // Any further actions are handed off to the SettingsMenuActivity class, and are handled there.
        startActivity(new Intent(this, SettingsMenuActivity.class));

        // Rotate the settings icon back again.
        findViewById(R.id.main_sidebar_settings_button).animate().rotation(0.0f).setDuration(1000).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /**
     * Shows the help overlay and the tutorial messages, if any.
     * @param view the View that called this method
     */
    public void showMainHelp(View view)
    {
        // Add help overlay calls here.
    }

    /**
     * Minimizes or maximizes the sidebar and all associated buttons and attached objects.
     * Also rotates the minimize button, changes to the alternate-color versions of the logo and
     * minimize button, and animates the entire sequence.
     * @param view the View that called this method
     */
    public void showOrHideSidebar(View view)
    {
        final RelativeLayout container = (RelativeLayout)findViewById(R.id.main_sidebar_container);
        ImageButton minimize = (ImageButton)findViewById(R.id.main_sidebar_minimize_button);
        ImageView logo = (ImageView)findViewById(R.id.main_sidebar_logo);

        // Set visibility of the sidebar itself, and the color/rotation of the minimize button,
        // based on its current visibility state.
        if(container.getVisibility() == View.GONE){
            container.setVisibility(View.VISIBLE);
            container.animate().translationX(0.0f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

            minimize.setBackgroundResource(R.drawable.minimize_button);
            addLongClickToast(R.id.main_sidebar_minimize_button, R.string.main_sidebar_minimize_button_toast, Toast.LENGTH_SHORT);
            logo.setImageResource(R.drawable.company_logo_128px_inv);

            minimize.animate().rotation(0.0f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

        }else if(container.getVisibility() == View.VISIBLE){
            container.animate().translationX(-120.0f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

            minimize.setBackgroundResource(R.drawable.minimize_button_inv);
            addLongClickToast(R.id.main_sidebar_minimize_button, R.string.main_sidebar_maximize_button_toast, Toast.LENGTH_SHORT);
            logo.setImageResource(R.drawable.company_logo_128px);

            minimize.animate().rotation(180.0f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

            container.postDelayed(new Runnable() {
                @Override
                public void run() {
                    container.setVisibility(View.GONE);
                }
            }, 505);
        }
    }


}
