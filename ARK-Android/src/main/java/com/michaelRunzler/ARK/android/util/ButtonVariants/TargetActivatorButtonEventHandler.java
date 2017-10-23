package com.michaelRunzler.ARK.android.util.ButtonVariants;

/**
 * Abstract class for handling event interactions from hybrid target activator button instances.
 */
public abstract class TargetActivatorButtonEventHandler
{
    /**
     * Handles an event from a hybrid target activator button press.
     * All actions taken must be effectively self-contained, with the exception of public method
     * calls, since this method cannot take or return arguments, with the exception of the
     * state update values.
     * @param state the current state of the calling button class
     * @return a state update for the calling button class
     */
    public abstract boolean handleEvent(boolean state);
}
