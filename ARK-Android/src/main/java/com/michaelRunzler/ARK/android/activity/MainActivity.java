package com.michaelRunzler.ARK.android.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.HelpOverlay.DynamicHelpInterface;
import com.michaelRunzler.ARK.android.util.HelpOverlay.HelpOverlayScene;
import com.michaelRunzler.ARK.android.util.Settings.SettingsManager;
import com.michaelRunzler.ARK.android.util.Settings.SettingsManagerDelegator;

import java.util.ArrayList;

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
    private DynamicHelpInterface tutorial;

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

        // Wait a few dozen ms for the UI to finish init, then resize the toolbar and check animation states.
        findViewById(R.id.main_sidebar_container).postDelayed(new Runnable() {
            @Override
            public void run()
            {
                onActivityResult(REQUEST_ID_SETTINGS, 0, null);

                // Set the temporary flag on the settings in the manager that are used for caching.
                settingsManager.setTemporary("topElementDist", true);
                settingsManager.setTemporary("lowElementDist", true);
                settingsManager.setTemporary("menuSidebarAnimState", true);

                LinearLayout menuButtonContainer = (LinearLayout)findViewById(R.id.main_sidebar_menu_button);
                ImageView mid = (ImageView)menuButtonContainer.getChildAt(1);

                // Check to see what the animation state on the menu slideout should be. Correct it if it is incorrect.
                if(mid.getVisibility() == View.VISIBLE && settingsManager.getSetting("topElementDist") != null
                        && (Float)settingsManager.getSetting("topElementDist") != 0.0f) {
                    activateMenuSlideout(null);
                }else{
                    // Set the slideout to its default state if there is no correction necessary.
                    RelativeLayout slideout = (RelativeLayout)findViewById(R.id.main_slideout_panel);
                    slideout.animate().translationX(-1.0f * (slideout.getWidth() * slideout.getScaleX())).setDuration(0).start();
                    slideout.setVisibility(View.GONE);
                }

                RelativeLayout sidebarContainer = (RelativeLayout)findViewById(R.id.main_sidebar_container);
                ImageButton minimize = (ImageButton)findViewById(R.id.main_sidebar_minimize_button);
                ImageView logo = (ImageView)findViewById(R.id.main_sidebar_logo);

                // Check to see what the animation state on the menu toolbar should be. Correct it if it is incorrect.
                if(settingsManager.getSetting("menuSidebarAnimState") != null
                        && (Boolean)settingsManager.getSetting("menuSidebarAnimState"))
                {
                    minimize.setRotation(180.0f);
                    minimize.setBackgroundResource(R.drawable.minimize_button_inv);
                    addLongClickToast(R.id.main_sidebar_minimize_button, R.string.main_sidebar_maximize_button_toast, Toast.LENGTH_SHORT);
                    logo.setImageResource(R.drawable.company_logo_128px);
                    sidebarContainer.setVisibility(View.GONE);
                    sidebarContainer.setTranslationX((-1.0f * ((sidebarContainer.getWidth() - (sidebarContainer.getWidth() * sidebarContainer.getScaleX())) / 2) - sidebarContainer.getWidth()));
                }
            }
        }, 50);

        // Set up main help interface object.
        ArrayList<HelpOverlayScene> scenes = new ArrayList<>();

        scenes.add(new HelpOverlayScene("This is the main sidebar.\nMost primary functions are located here.", findViewById(R.id.main_sidebar_container), 400, -1));
        scenes.add(new HelpOverlayScene("Use this to access the settings menu.", findViewById(R.id.main_sidebar_settings_button), 200, -1));
        scenes.add(new HelpOverlayScene("Use this to access this tutorial.", findViewById(R.id.main_sidebar_help_button), 200, -1));
        scenes.add(new HelpOverlayScene("This opens the main menu panel, which contains additional functions.", findViewById(R.id.main_sidebar_menu_button), 300, -1));

        tutorial = new DynamicHelpInterface((RelativeLayout)findViewById(R.id.main_help_overlay), scenes);
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
            // Rotate the settings icon back again.
            findViewById(R.id.main_sidebar_settings_button).animate().rotation(0.0f).setDuration(500).setInterpolator(new LinearInterpolator());
        }
    }

    /**
     * Automatically scales the menu toolbar to the specified size.
     * Will autohandle any added buttons in the toolbar.
     * @param multiplier the size multiplier for the toolbar and all of its icons
     */
    private void autoSizeMenuToolbar(float multiplier)
    {
        RelativeLayout sidebar = (RelativeLayout)findViewById(R.id.main_sidebar_container);
        ImageView logo = (ImageView)findViewById(R.id.main_sidebar_logo);
        ImageButton minimize = (ImageButton)findViewById(R.id.main_sidebar_minimize_button);

        LinearLayout menu = (LinearLayout)findViewById(R.id.main_sidebar_menu_button);

        RelativeLayout slideout = (RelativeLayout)findViewById(R.id.main_slideout_panel);

        // For each item in the sidebar (excluding the menu button), adjust its Y-scale and vertical position
        // to compensate for the adjustment to the sidebar scaling, maintaining separation ratio.
        for(int i = 1; i < sidebar.getChildCount(); i++)
        {
            View v = sidebar.getChildAt(i);

            v.setScaleY(multiplier);
            v.setTranslationY(-1.0f * ((v.getWidth() - (v.getWidth() * multiplier)) * i));
        }

        // Scale all toolbar icons and containers to the appropriate size
        menu.setScaleY(multiplier);
        minimize.setScaleY(multiplier);
        logo.setScaleY(multiplier);

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

        // Set minimize button to maintain the same separation ratio regardless of size.
        minimize.setTranslationY((minimize.getHeight() - (minimize.getHeight() * multiplier)) / 2);

        // Set the margins of the menu slideout to match the toolbar's new size, then invalidate and
        // redraw the entire slideout.
        ViewGroup.MarginLayoutParams slideoutParams = (ViewGroup.MarginLayoutParams)slideout.getLayoutParams();
        slideoutParams.setMargins((int)(sidebar.getWidth() * sidebar.getScaleX()), 0, 0, 0);
        slideout.invalidate();
        slideout.requestLayout();

        // Check to see if the minimize button is in the GONE state. If it is, fire a zero-length animation
        // in order to ensure that it animates correctly on the next cycle, since its state will have
        // been reset by the change to its Y-translation earlier in the method.
        if(minimize.getVisibility() == View.GONE)
            minimize.animate().translationXBy(-1.0f * (sidebar.getWidth() * sidebar.getScaleX())).setDuration(0).start();
    }


    //
    // UI ELEMENT ACTION LISTENERS
    //


    /**
     * Slides out the main menu overlay and animates it. Caches its state to the Settings Manager,
     * and as such can react to Activity restarts.
     * @param view the View that called this method. If it is null, the method will assume that it is
     *             being called from an internal source, and will shortcut animation. This argument
     *             is not used for anything else, so if you wish to call it internally <i>without</i>
     *             skipping animation, pass it any non-null View, and it will work fine.
     */
    public void activateMenuSlideout(View view)
    {
        // If this is being called from an internal method with a null argument, change state without animating.
        final int ANIM_STAGE_DELAY = view == null ? 0 : 250;

        final LinearLayout container = (LinearLayout)findViewById(R.id.main_sidebar_menu_button);
        final ImageView top = (ImageView)container.getChildAt(0);
        final ImageView mid = (ImageView)container.getChildAt(1);
        final ImageView low = (ImageView)container.getChildAt(2);

        final ImageButton minimize = (ImageButton)findViewById(R.id.main_sidebar_minimize_button);
        final RelativeLayout slideout = (RelativeLayout)findViewById(R.id.main_slideout_panel);
        final RelativeLayout sidebar = (RelativeLayout)findViewById(R.id.main_sidebar_container);

        // Disable the slideout menu button to prevent spamming of the slideout animation.
        container.setEnabled(false);

        Handler handler = new Handler();

        // The animation has not fired.
        if(mid.getVisibility() == View.VISIBLE)
        {
            // Store the distance that the elements are going to translate for future use, and update the animation state.
            settingsManager.storeSetting("topElementDist", (top.getY() - mid.getY()));
            settingsManager.storeSetting("lowElementDist", (low.getY() - mid.getY()));

            // Disable sidebar minimize button before animating to make sure that the user cannot
            // minimize the sidebar during the animation, since this renders the application unusable.
            minimize.setEnabled(false);

            // Pull all three elements on top of each other.
            top.animate().y(mid.getY()).setDuration(ANIM_STAGE_DELAY).start();
            low.animate().y(mid.getY()).setDuration(ANIM_STAGE_DELAY).start();

            // Hide the middle element and rotate the top and bottom elements 45 degrees in opposite
            // directions (forming an X) once the first animation set is done.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mid.setVisibility(View.INVISIBLE);
                    top.animate().rotation(45.0f).setDuration(ANIM_STAGE_DELAY).start();
                    low.animate().rotation(-45.0f).setDuration(ANIM_STAGE_DELAY).start();
                }
            }, ANIM_STAGE_DELAY);

            // Hide the menu bar minimize button.
            minimize.animate().translationXBy(-1.0f * (sidebar.getWidth() * sidebar.getScaleX())).setDuration(ANIM_STAGE_DELAY).start();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    minimize.setVisibility(View.GONE);
                }
            }, ANIM_STAGE_DELAY);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Re-enable the slideout button since the animation is now finished.
                    container.setEnabled(true);
                }
            }, ANIM_STAGE_DELAY * 2);

            // Show the menu slideout.
            slideout.setVisibility(View.VISIBLE);
            slideout.animate().translationX(0).setDuration(ANIM_STAGE_DELAY).start();
        }else // The animation has fired, and the elements should be returned to their default state.
        {
            // Rotate the top and bottom elements back to their original position.
            top.animate().rotation(0.0f).setDuration(ANIM_STAGE_DELAY).start();
            low.animate().rotation(0.0f).setDuration(ANIM_STAGE_DELAY).start();

            // Store the distance values pulled from the settings manager so that we can delete them straight away.
            final float topElementDist = (Float)settingsManager.getSetting("topElementDist");
            final float lowElementDist = (Float)settingsManager.getSetting("lowElementDist");

            // Reveal the middle element and push the elements back apart once the first animation set is done.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mid.setVisibility(View.VISIBLE);
                    top.animate().translationYBy(topElementDist).setDuration(ANIM_STAGE_DELAY).start();
                    low.animate().translationYBy(lowElementDist).setDuration(ANIM_STAGE_DELAY).start();
                }
            }, ANIM_STAGE_DELAY);

            // Show and re-enable the menu bar minimize button.
            minimize.animate().translationXBy(sidebar.getWidth() * sidebar.getScaleX()).setDuration(ANIM_STAGE_DELAY).start();
            minimize.setVisibility(View.VISIBLE);
            minimize.setEnabled(true);

            // Hide the menu slideout.
            slideout.animate().translationX(-1.0f * (slideout.getWidth() * slideout.getScaleX())).setDuration(ANIM_STAGE_DELAY).start();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    slideout.setVisibility(View.GONE);
                }
            }, ANIM_STAGE_DELAY);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Re-enable the slideout button since the animation is now finished.
                    container.setEnabled(true);
                }
            }, ANIM_STAGE_DELAY * 2);

            // Set cached settings in the settings manager to their default state. We could remove them,
            // but this is more efficient.
            settingsManager.storeSetting("topElementDist", 0.0f);
            settingsManager.storeSetting("lowElementDist", 0.0f);
        }
    }

    /**
     * Shows the settings menu and animates the settings icon.
     * @param view the View that called this method
     */
    public void showSettings(View view)
    {
        // Rotate the settings icon forward.
        findViewById(R.id.main_sidebar_settings_button).animate().rotation(359.0f).setDuration(500).setInterpolator(new LinearInterpolator());

        // Any further actions are handed off to the SettingsMenuActivity class, and are handled there.
        startActivityForResult(new Intent(this, SettingsMenuActivity.class), REQUEST_ID_SETTINGS);
    }

    /**
     * Shows the help overlay and the tutorial messages, if any.
     * @param view the View that called this method
     */
    public void showMainHelp(View view) {
        progressHelpOverlay(tutorial.getLinkedView());
    }

    /**
     * Minimizes or maximizes the sidebar and all associated buttons and attached objects.
     * Also rotates the minimize button, changes to the alternate-color versions of the logo and
     * minimize button, and animates the entire sequence. Caches its state to the Settings Manager,
     * and as such can react to Activity restarts.
     * @param view the View that called this method
     */
    public void showOrHideSidebar(View view)
    {
        final RelativeLayout container = (RelativeLayout)findViewById(R.id.main_sidebar_container);
        LinearLayout menuButton = (LinearLayout)findViewById(R.id.main_sidebar_menu_button);
        final ImageButton minimize = (ImageButton)findViewById(R.id.main_sidebar_minimize_button);
        ImageView logo = (ImageView)findViewById(R.id.main_sidebar_logo);

        // Disable the minimize button to prevent spamming of the minimize function.
        minimize.setEnabled(false);

        // Set visibility of the sidebar itself, and the color/rotation of the minimize button,
        // based on its current visibility state.
        if(container.getVisibility() == View.VISIBLE)
        {
            settingsManager.storeSetting("menuSidebarAnimState", true);

            // Disable slideout menu button before animating to make sure that the user cannot
            // maximize the slideout during the animation, since this renders the application unusable.
            menuButton.setEnabled(false);

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

                    // Re-enable the minimize button since the animation is now finished.
                    minimize.setEnabled(true);
                }
            }, 505);
        }else if(container.getVisibility() == View.GONE)
        {
            settingsManager.storeSetting("menuSidebarAnimState", false);

            // Re-enable the slideout menu button.
            menuButton.setEnabled(true);

            container.setVisibility(View.VISIBLE);
            container.animate().translationX(-1.0f * ((container.getWidth() - (container.getWidth() * container.getScaleX())) / 2)).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

            minimize.setBackgroundResource(R.drawable.minimize_button);
            addLongClickToast(R.id.main_sidebar_minimize_button, R.string.main_sidebar_minimize_button_toast, Toast.LENGTH_SHORT);
            logo.setImageResource(R.drawable.company_logo_128px_inv);

            minimize.animate().rotation(0.0f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());

            container.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Re-enable the minimize button since the animation is now finished.
                    minimize.setEnabled(true);
                }
            }, 500);
        }
    }

    /**
     * Advances the help overlay to the next stage of the tutorial. Dynamic - no need to modify.
     */
    public void progressHelpOverlay(View view) {
        if(view instanceof RelativeLayout && ((RelativeLayout) view).getChildAt(5) instanceof TextView)
            tutorial.showNextScene(0);
    }
}