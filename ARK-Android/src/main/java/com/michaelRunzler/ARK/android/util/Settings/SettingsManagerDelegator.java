package com.michaelRunzler.ARK.android.util.Settings;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Manages cross-class instancing of the {@link SettingsManager} object for cases where multiple classes
 * must share an instance of said object.
 * Also manages a multi-object dynamic instance index, for multiple concurrent global instances.
 */
public class SettingsManagerDelegator
{
    private static SettingsManager instance = null;
    private static ArrayList<SettingsManager> dynamicInstances = new ArrayList<>();

    /**
     * Gets the available {@link SettingsManager} global instance.
     * @return the current global {@link SettingsManager} instance
     */
    public static SettingsManager getMainInstance()
    {
        if(instance == null){
            instance = new SettingsManager();
        }
        return instance;
    }

    /**
     * Voids the current global {@link SettingsManager} instance and creates a new one.
     * @return the new global instance
     */
    public static SettingsManager refreshMainInstance()
    {
        if(instance != null){
            instance = null;
        }
        instance = new SettingsManager();
        return instance;
    }

    /**
     * Sets the current global instance to the specified object.
     * @param newInstance the {@link SettingsManager} object to set the global reference to
     */
    public static void setMainInstance(@NonNull final SettingsManager newInstance) {
        instance = newInstance;
    }

    /**
     * Generates a new dynamic {@link SettingsManager} instance with the specified ID, or gets an existing
     * one if one exists.
     * @param ID the ID of the desired {@link SettingsManager} global instance to get or create
     * @return the {@link SettingsManager} global instance at the specified ID, or a new instance if none existed
     */
    public static SettingsManager getDynamicInstance(short ID)
    {
        if(ID < 0){
            throw new IllegalArgumentException("ID must be more than 0");
        }

        if(dynamicInstances.get((int)ID) == null){
            dynamicInstances.set((int)ID, new SettingsManager());
        }

        return dynamicInstances.get((int)ID);
    }

    /**
     * Sets the specified ID in the dynamic instance index to the provided {@link SettingsManager}.
     * @param ID the ID of the desired {@link SettingsManager} global instance to replace
     * @param newInstance the {@link SettingsManager} object to replace the desired ID with
     */
    public static void setDynamicInstance(short ID, SettingsManager newInstance)
    {
        if(ID < 0){
            throw new IllegalArgumentException("ID must be more than 0");
        }

        dynamicInstances.set(ID, newInstance);
    }
}