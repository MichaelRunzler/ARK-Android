package com.michaelRunzler.ARK.android.util;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages cross-class instancing of the SettingsManager object for cases where multiple classes
 * must share an instance of said object.
 */
public class SettingsManagerDelegator
{
    private static SettingsManager instance = null;
    private static ArrayList<SettingsManager> dynamicInstances = new ArrayList<>();

    /**
     * Gets the available SettingsManager global instance.
     * @return the current global SettingsManager instance
     */
    public static SettingsManager getInstance()
    {
        if(instance == null){
            instance = new SettingsManager();
        }
        return instance;
    }

    /**
     * Voids the current global SettingsManager instance and creates a new one.
     * @return the new global instance
     */
    public static SettingsManager refreshInstance()
    {
        if(instance != null){
            instance = null;
        }
        instance = new SettingsManager();
        return instance;
    }

    /**
     * Sets the current global instance to the specified object.
     * @param newInstance the SettingsManager object to set the global reference to
     */
    public static void setInstance(@NonNull final SettingsManager newInstance) {
        instance = newInstance;
    }

    /**
     * Generates a new dynamic SettingsManager instance with the specified ID, or gets an existing
     * one if one exists.
     * @param ID the ID of the desired SettingsManager global instance to get or create
     * @return the SettingsManager global instance at the specified ID, or a new instance if none existed
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
     * Sets the specified ID in the dynamic instance index to the provided SettingsManager.
     * @param ID the ID of the desired SettingsManager global instance to replace
     * @param newInstance the SettingsManager object to replace the desired ID with
     */
    public static void setDynamicInstance(short ID, SettingsManager newInstance)
    {
        if(ID < 0){
            throw new IllegalArgumentException("ID must be more than 0");
        }

        dynamicInstances.set(ID, newInstance);
    }
}

