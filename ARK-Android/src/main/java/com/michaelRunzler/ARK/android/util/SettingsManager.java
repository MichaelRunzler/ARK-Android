package com.michaelRunzler.ARK.android.util;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Stores and manages application configuration settings. Also manages read/write to/from the config
 * file stored on disk. Use the SettingsManagerDelegator class for cross-class instancing.
 */
public class SettingsManager
{
    private HashMap<String,Object> storage;
    private File target;

    /**
     * Constructs a new instance of this object with an empty internal registry and null file target.
     */
    public SettingsManager() {
        storage = new HashMap<>();
        target = null;
    }

    /**
     * Constructs a new instance of this object with an empty internal registry and the specified file target.
     * @param target a File representing the desired target configuration file.
     */
    public SettingsManager(File target){
        storage = new HashMap<>();
        this.target = target;
    }

    /**
     * Gets a setting value from the stored list.
     * @param key the key to search for in the settings index
     * @return the value corresponding to the provided key, or null if the key does not exist in the index
     */
    public Object getSetting(@NonNull String key) {
        return storage.containsKey(key) ? storage.get(key) : null;
    }

    /**
     * Gets multiple settings values from the stored index.
     * Automatically culls the output array, removing corresponding entries for any keys
     * that do not exist in the stored index.
     * @param keys one or multiple Strings (or a String array) that represent keys in the stored settings index
     * @return an Object array containing a culled list of settings from the internal index, or null if no keys existed in the index
     */
    public Object[] getMultipleSettingsCulled(@NonNull String... keys)
    {
        if(keys.length == 0) throw new IllegalArgumentException("Provide one or more key arguments");

        String[] validKeys = new String[keys.length];
        System.arraycopy(keys, 0, validKeys, 0, keys.length);

        // Check to see how many given keys actually exist in the index,
        // and copy them over to a temporary array if they do.
        int validKeyCount = keys.length;
        int validityCounter = 0;

        for(String k : keys)
        {
            if(storage.containsKey(k)){
                validKeys[validityCounter] = k;
                validityCounter ++;
            }else{
                validKeyCount --;
            }
        }

        // Return a null array if no keys matched.
        if(validKeyCount == 0) return null;

        // Iterate through the array of valid keys, find the value of each one, and copy the
        // results over to the output array.
        Object[] retV = new Object[validKeyCount];
        for(int i = 0; i < validKeyCount; i++) {
            retV[i] = storage.get(validKeys[i]);
        }

        return retV;
    }

    /**
     * Gets multiple values from the stored index.
     * Returns an array of the exact same length as the input array, even if some values were not found.
     * @param keys one or multiple Strings (or a String array) that represent keys in the stored settings index
     * @return an Object array containing a list of settings from the internal index. Keys that were not found
     * in the index have their corresponding indices in the output array set to null.
     */
    public Object[] getMultipleSettings(@NonNull String... keys)
    {
        if(keys.length == 0) throw new IllegalArgumentException("Provide one or more key arguments");

        Object[] retV = new Object[keys.length];
        for(int i = 0; i < keys.length; i++) {
            retV[i] = storage.containsKey(keys[i]) ? storage.get(keys[i]) : null;
        }

        return retV;
    }

    /**
     * Sets a specified key in the internal index to the specified value.
     * If no entry for that key exists, it will be created.
     * Provided object must implement Serializable to allow reading from/writing to config files.
     * @param key the key to search for in the index, or the new key that will be created, if that key does not exist
     * @param value the value to set the existing or new key to
     * @return true if the specified key existed already, false if otherwise
     */
    public boolean storeSetting(@NonNull String key, Object value)
    {
        if(key.length() == 0) throw new IllegalArgumentException("Key cannot be zero-length");
        if(!(value instanceof Serializable)) throw new IllegalArgumentException("Object must be serializable");

        boolean retV = false;
        if(storage.containsKey(key)) {
            storage.remove(key);
            retV = true;
        }

        storage.put(key, value);
        return retV;
    }

    /**
     * Stores multiple settings in keypair format.
     * Keys that exist in the index will be set to the provided value, and keys that do not will be created.
     * Provided objects must implement Serializable to allow reading from/writing to config files.
     * Objects that do not implement Serializable will throw an IllegalArgumentException.
     * @param settings a HashMap containing the list of settings and keys to store
     */
    public void storeMultipleSettings(@NonNull HashMap<String, Object> settings)
    {
        if(settings.size() == 0) throw new IllegalArgumentException("Input HashMap cannot be null");

        for(String key : settings.keySet())
        {
            Object value = settings.get(key);
            if(!(value instanceof Serializable)) throw new IllegalArgumentException("Object must be serializable");

            if(storage.containsKey(key)) storage.remove(key);
            storage.put(key, value);
        }
    }

    /**
     * Stores multiple settings in keypair format.
     * Keys that exist in the index will be set to the provided value, and keys that do not will be created.
     * Only objects that implement Serializable will be added to the index, all others will be ignored.
     * Make sure to check the index after completion to see if any objects were skipped.
     * @param settings a HashMap containing the list of settings and keys to store
     */
    public void storeMultipleSettingsIgnoreNonSerializable(@NonNull HashMap<String, Object> settings)
    {
        if(settings.size() == 0) throw new IllegalArgumentException("Input HashMap cannot be null");

        for(String key : settings.keySet()) {
            if(storage.containsKey(key)) storage.remove(key);
            storage.put(key, settings.get(key));
        }
    }

    /**
     * Clears the internal settings index completely.
     */
    public void clearStorage() {
        storage = new HashMap<>();
    }

    /**
     * Attempts to write the currently stored settings index to the set config file.
     * @throws IOException if an error occurred during the write or serialization process
     */
    public void writeStoredConfigToFile() throws IOException
    {
        // Check to make sure the current target is valid.
        if(target == null || target.isDirectory()) throw new IllegalArgumentException("Config target is invalid");

        // If the target is valid, make sure a file exists at the specified location.
        if(target.exists()){
            if(!target.delete()) throw new IOException("Unable to delete current config file");
        }else{
            if(!target.getParentFile().mkdirs()) throw new IOException("Unable to create necessary config directory path");
        }

        if(!target.createNewFile()) throw new IOException("Unable to create config file");

        // Make sure we can write to the file we just created.
        if(!target.canRead() || !target.canWrite()) throw new IOException("No access rights for written config file");

        // Now that we are sure that the file is ready for writing, write the index to it
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(target));
        os.writeObject(storage);
        os.flush();
        os.close();
    }

    /**
     * Attempts to read a settings index from the currently set config file.
     * If the read succeeds, the currently stored index of this object will be replaced by the read
     * settings, so make sure to store the current index somewhere else if you wish to retain it.
     * @throws IOException if an error occurred during the read or interpretation process
     */
    public void loadConfigFromFile() throws IOException
    {
        // Check to make sure the current target is valid.
        if(target == null || target.isDirectory()) throw new IllegalArgumentException("Config target is invalid");

        // If the target is valid, make sure a file exists at the specified location.
        if(!target.exists() || (target.exists() & !target.getName().contains(".cfg")))
            throw new IOException("No valid config file exists at the specified location");

        // Target is valid. Try to load from it.
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(target));
        Object buffer;
        try{
            buffer = is.readObject();
        } catch(ClassNotFoundException | IOException e){
            throw new IOException("Unable to load from config. Cause: " +
                    (e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage()));
        }

        // Check that the file actually contained a valid HashMap.
        if(!(buffer instanceof HashMap)) throw new IOException("Read succeeded, but the file did not contain valid settings");

        // Store the retrieved data.
        // Not an unchecked cast, but IntelliJ seems to think so...
        storage = (HashMap<String,Object>)buffer;
    }

    /**
     * Gets the current target config file for this object.
     * @return a copy of the File that this object is currently managing
     */
    public File getTarget() {
        return new File(target.getParent(), target.getName());
    }

    /**
     * Sets the config file target of this object.
     * @param target a File representing the target configuration file that this object should manage
     */
    public void setTarget(File target) {
        this.target = target;
    }

    /**
     * Dumps this object's currently stored settings index.
     * The returned value is a copy of the stored index, not a direct reference.
     * @return a copy of the currently stored settings index
     */
    public HashMap<String, Object> getSettings()
    {
        HashMap<String, Object> retV = new HashMap<>();
        retV.putAll(storage);
        return retV;
    }
}
