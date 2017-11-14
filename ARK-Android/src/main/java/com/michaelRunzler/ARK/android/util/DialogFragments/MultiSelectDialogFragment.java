package com.michaelRunzler.ARK.android.util.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.michaelRunzler.ARK.android.R;

/**
 * Implements a multiple-option select dialog interface, in which the user may select one of multiple
 * displayed items from a scrollable list.
 */
public class MultiSelectDialogFragment extends DialogFragment
{
    public String title;
    public String[] options;
    public DialogActionEventHandler handler;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        if(handler == null) throw new IllegalArgumentException("Handler class is null!");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title == null ? getActivity().getResources().getString(R.string.default_MultiSelectDialog_title) : title);

        builder.setItems(options == null || options.length <= 0
                ? new String[]{getActivity().getApplicationContext().getResources().getString(R.string.multi_select_dialog_no_items_notice)}
                : options, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.handleEvent(DialogActionEventHandler.ResultID.SUBMITTED, which);
            }
        });

        return builder.show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        handler.handleEvent(DialogActionEventHandler.ResultID.CANCELLED);
        dialog.dismiss();
    }

    /**
     * Sets this object's interface properties. Variables referenced are public, but this method is
     * provided for convenience.
     * @param title the title text to be shown on the dialog window. Null values will default to the
     *              corresponding value specified in strings.xml.
     * @param options the list of options to be used for the displayed list in the dialog window.
     * @param handler a DialogEventHandler class to handle the result of the dialog. The result code
     *                given to the handler class will correspond to the type of response given.
     *                If the user chooses an option, the result code will be SUBMITTED, otherwise, it will
     *                be CANCELLED. Returned value is the index of the chosen item.
     */
    public void setProperties(String title, DialogActionEventHandler handler, String... options)
    {
        this.title = title;
        if(handler == null){
            throw new IllegalArgumentException("Handler must not be null!");
        }
        this.handler = handler;
        this.options = options;
    }
}
