package com.michaelRunzler.ARK.android.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.SettingsManager;
import com.michaelRunzler.ARK.android.util.SettingsManagerDelegator;

/**
 * Default template class for the ARK Android UI main screen.
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

    private final int REQUEST_ID_SETTINGS = 1000;
    private final float MENU_BAR_SIZE_SMALL = 0.7f;
    private final float MENU_BAR_SIZE_MEDIUM = 0.85f;
    private final float MENU_BAR_SIZE_LARGE = 1.0f;

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

        settingsManager = SettingsManagerDelegator.getMainInstance();

        // Initialize and display main screen.
        setContentView(R.layout.main_screen);
        initializeMainUI();
        setMainUIContentActions();

        // Allow orientation changes again.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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

        // Wait a few dozen ms for the UI to finish init, then resize the toolbar.
        findViewById(R.id.main_sidebar_container).postDelayed(new Runnable() {
            @Override
            public void run() {
                onActivityResult(REQUEST_ID_SETTINGS, 0, null);
            }
        }, 50);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_ID_SETTINGS)
        {
            float multiplier = 0.0f;
            switch ((Integer)settingsManager.getSetting("menuToolbarSize"))
            {
                case 0:
                    multiplier = MENU_BAR_SIZE_SMALL;
                    break;
                case 1:
                    multiplier = MENU_BAR_SIZE_MEDIUM;
                    break;
                case 2:
                    multiplier = MENU_BAR_SIZE_LARGE;
                    break;
            }
            autoSizeMenuToolbar(multiplier);
        }
    }

    /**
     * Automatically scales the menu toolbar to the specified size.
     * @param multiplier the size multiplier for the toolbar and all of its icons
     */
    private void autoSizeMenuToolbar(float multiplier)
    {
        RelativeLayout sidebar = (RelativeLayout)findViewById(R.id.main_sidebar_container);
        ImageView logo = (ImageView)findViewById(R.id.main_sidebar_logo);
        ImageButton minimize = (ImageButton)findViewById(R.id.main_sidebar_minimize_button);
        ImageButton settings = (ImageButton)findViewById(R.id.main_sidebar_settings_button);
        ImageButton help = (ImageButton)findViewById(R.id.main_sidebar_help_button);

        // Scale all toolbar icons and containers to the appropriate size
        findViewById(R.id.main_sidebar_settings_button).setScaleY(multiplier);
        findViewById(R.id.main_sidebar_help_button).setScaleY(multiplier);
        findViewById(R.id.main_sidebar_menu_button).setScaleY(multiplier);
        findViewById(R.id.main_sidebar_minimize_button).setScaleY(multiplier);
        findViewById(R.id.main_sidebar_logo).setScaleY(multiplier);

        sidebar.setScaleX(multiplier);
        minimize.setScaleX(multiplier);
        minimize.setScaleY(multiplier);
        logo.setScaleX(multiplier);
        logo.setScaleY(multiplier);

        // Set sidebar elements to hug the left side of the screen regardless of scale.
        // The formula for doing so is as follows:
        // -1 * ([x - {x * y}] / 2)
        // where x is the maximum horizontal size of the object, and y is the decimal multiplier
        // denoting the current horizontal size of the object. This yields the offset necessary to
        // align the left edge of the object to the exact same position regardless of scale.
        sidebar.setTranslationX(-1.0f * ((sidebar.getWidth() - (sidebar.getWidth() * multiplier)) / 2));
        minimize.setTranslationX(sidebar.getTranslationX());
        logo.setTranslationX(sidebar.getTranslationX());

        minimize.setTranslationY((minimize.getHeight() - (minimize.getHeight() * multiplier)) / 2);
        settings.setTranslationY(-1.0f * ((settings.getHeight() - (settings.getHeight() * multiplier)) / 2));
        help.setTranslationY(-1.0f * ((help.getHeight() - (help.getHeight() * multiplier))));
    }


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
        startActivityForResult(new Intent(this, SettingsMenuActivity.class), REQUEST_ID_SETTINGS);

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
            container.animate().translationX(-1.0f * ((container.getWidth() - (container.getWidth() * container.getScaleX())) / 2)).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

            minimize.setBackgroundResource(R.drawable.minimize_button);
            addLongClickToast(R.id.main_sidebar_minimize_button, R.string.main_sidebar_minimize_button_toast, Toast.LENGTH_SHORT);
            logo.setImageResource(R.drawable.company_logo_128px_inv);

            minimize.animate().rotation(0.0f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

        }else if(container.getVisibility() == View.VISIBLE){
            // HERE THERE BE DRAGONS
            // ...OF THE ALGEBRA KIND
            container.animate().translationX((-1.0f * ((container.getWidth() - (container.getWidth() * container.getScaleX())) / 2) - container.getWidth())).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

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