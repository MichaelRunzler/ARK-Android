package com.michaelRunzler.ARK.android.util.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.michaelRunzler.ARK.android.R;
import com.michaelRunzler.ARK.android.util.DialogFragments.DialogActionEventHandler.ResultID;

/**
 * Implements a basic Yes/No dialog interface with a customizable layout. All internal variables are
 * public, convenience access methods are included. Delegates result status to a provided handler
 * object. Returned result codes will be {@link ResultID#POSITIVE} or {@link ResultID#NEGATIVE}, depending on user response.
 */
public class YNDialogFragment extends DialogFragment
{
    public String message;
    public String yesButtonText;
    public String noButtonText;
    public int layoutID;
    private DialogActionEventHandler handler;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        if(handler == null) throw new IllegalArgumentException("Handler class is null!");
        if(message == null || message.isEmpty()) message = getResources().getString(R.string.default_YNDialog_message);
        if(yesButtonText == null || yesButtonText.isEmpty()) yesButtonText = getResources().getString(R.string.default_YNDialog_yes_text);
        if(noButtonText == null || noButtonText.isEmpty()) noButtonText = getResources().getString(R.string.default_YNDialog_no_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if(layoutID != -1) builder.setView(inflater.inflate(getResources().getLayout(layoutID), null));

        builder.setMessage(message);
        builder.setPositiveButton(yesButtonText, (dialog, which) -> {
            handler.handleEvent(ResultID.POSITIVE);
            dialog.dismiss();
        });

        builder.setNegativeButton(noButtonText, (dialog, which) -> {
            handler.handleEvent(ResultID.NEGATIVE);
            dialog.dismiss();
        });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        handler.handleEvent(ResultID.NEGATIVE);
        dialog.dismiss();
    }

    /**
     * Sets this object's interface properties. Variables referenced are public, but this method is
     * provided for convenience. Providing a {@code null} value for one or more of these arguments will reset
     * the corresponding variable to its default setting, specified in the {@code strings.xml} file.
     * @param message the message (could be considered the 'title' of the dialog) to display
     * @param yesButtonText the text to display on the button that returns {@code true}
     * @param noButtonText the text to display on the button that returns {@code false}
     * @param layoutID the Android Layout ID to use for a custom layout in the shown dialog. Set the
     *                 ID to {@code -1} to use the default layout.
     * @param handler a {@link DialogActionEventHandler} class to handle the result of the dialog. The result code
     *                given to the handler class will correspond to the type of response given - {@link ResultID#POSITIVE} or {@link ResultID#NEGATIVE}.
     */
    public void setProperties(String message, String yesButtonText, String noButtonText, int layoutID, DialogActionEventHandler handler)
    {
        this.message = message;
        this.yesButtonText = yesButtonText;
        this.noButtonText = noButtonText;
        this.layoutID = layoutID;
        if(handler == null){
            throw new IllegalArgumentException("Handler must not be null!");
        }
        this.handler = handler;
    }
}
