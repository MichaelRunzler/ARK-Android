package com.michaelRunzler.ARK.android.util.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.michaelRunzler.ARK.android.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Implements a file selection dialog interface, in which a user may select a file from a displayed
 * list, as parsed from the Android filesystem manager, and filtered by a set of filter arguments.
 * Includes multi-level tree and multiple extension/name wildcard argument support.
 */
public class FileSelectDialogFragment extends DialogFragment
{
    private File src;
    private DialogActionEventHandler handler;
    private String[] extFilters;
    private String title;

    private String[] fileList;
    private File[] fileReferenceList;
    private ArrayList<File> hierarchyChain;

    private final String NO_ITEMS_TEXT = "NO FILES FOUND";

    private int hierarchyID = 0;
    private boolean firstRun = true;
    private boolean firstRunCached = firstRun;
    private AlertDialog dialog;

    AlertDialog.Builder builder;

    @Override
    public void onResume()
    {
        final AlertDialog alertDialog;

        // Check if this is being run from the Android system or from a direct call. Allocate dialog reference as such.
        if(firstRunCached) {
            super.onResume();
             alertDialog = (AlertDialog) getDialog();
        }else{
            alertDialog = dialog;
        }

        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the current view level is below the source level, go up one level and redisplay.
                // Otherwise, do nothing and return.
                if(hierarchyID > 0){
                    hierarchyID --;
                    src = hierarchyChain.get(hierarchyID);
                    //parseFileList();
                    //((BaseAdapter)alertDialog.getListView().getAdapter()).notifyDataSetChanged(); //todo not updating view properly
                    onCreateDialog(null);
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position >= 0)
                {
                    String[] list = getCurrentFileList();

                    if(list[position].equals(NO_ITEMS_TEXT)){
                        return;
                    }

                    // If the selected file is a directory, recursively update the file list and redisplay.
                    if(getCurrentFileRefList()[position].isDirectory()){
                        src = getCurrentFileRefList()[position];
                        //parseFileList();
                        //((BaseAdapter)alertDialog.getListView().getAdapter()).notifyDataSetChanged(); //todo not updating view properly
                        hierarchyID ++;
                        onCreateDialog(null);
                        alertDialog.dismiss();
                    }else { // If the selected item is a file, give the value to the handler and dismiss the dialog.
                        handler.handleEvent(DialogActionEventHandler.ResultID.SUBMITTED, getCurrentFileRefList()[position]);
                        alertDialog.dismiss();
                    }
                }
            }
        });

        /*
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.handleEvent(DialogActionEventHandler.ResultID.CANCELLED);
            }
        });
        */
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        handler.handleEvent(DialogActionEventHandler.ResultID.CANCELLED);
        dialog.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Used for caching the state of the firstRun flag for later processing stages
        firstRunCached = firstRun;

        parseFileList();

        // Check if this is the first run cycle. If so, set up normally. If not, bypass initial setup and only update the internal file list.
        if(firstRun)
        {
            builder = new AlertDialog.Builder(getActivity());
            hierarchyChain = new ArrayList<>();

            // Set title.
            builder.setTitle(title == null || title.length() == 0 ? getActivity().getResources().getString(R.string.default_FileDialog_title) : title);

            hierarchyID = 0;

            // Set up the folder level up button.
            builder.setNeutralButton(getActivity().getResources().getString(R.string.default_FileDialog_up_button_text), null);

            firstRun = false;
        }

        // Add the current source file to the hierarchy chain.
        if(hierarchyID == hierarchyChain.size() || hierarchyChain.get(hierarchyID) != src) {
            hierarchyChain.add(hierarchyID, src);
        }

        // Set up the file list.
        builder.setItems(fileList, null);

        // Store a copy of the dialog to a local variable to allow calling of the onResume method outside of normal flow
        dialog = builder.create();
        dialog.show();
        if(!firstRunCached) onResume();
        return dialog;
    }

    /**
     * Loads the current list of files from the working directory, and stores them to the fileList
     * instance variable.
     */
    private void parseFileList()
    {
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename)
            {
                File sel = new File(dir, filename);

                boolean containsType = false;
                if(extFilters.length == 0 || sel.isDirectory()){
                    containsType = true;
                }else{
                    for(String t : extFilters){
                        if(t.trim().equals("*.*") || filename.contains(t.replace("*", ""))){
                            containsType = true;
                        }
                    }
                }

                return containsType;
            }

        };
        fileList = src.list(filter);
        fileReferenceList = src.listFiles(filter);

        // If the list is empty, add an entry indicating as such.
        if(fileList == null || fileList.length <= 0) {
            fileList = new String[]{NO_ITEMS_TEXT};
            fileReferenceList = new File[0];
        }else{
            // Prepend all directory entries with a [Dir] label.
            for(int i = 0; i < fileList.length; i++){
                String l = fileList[i];

                if(fileReferenceList[i].isDirectory()){
                    fileList[i] = "[DIR] " + l;
                }
            }
        }
    }

    /**
     * Sets this object's interface properties.
     * @param src the base directory from which the file chooser should expand its directory and file tree.
     *            If an object is provided that is null, nonexistent, not a directory, or does not possess
     *            valid read permissions, an IllegalArgumentException will be thrown.
     * @param title the title text to be shown on the dialog window. Null values will default to the
     *              corresponding value specified in strings.xml.
     * @param handler a DialogEventHandler class to handle the result of the dialog. The result code
     *                given to the handler class will correspond to the type of response given.
     *                If the user chooses a file, the result code will be SUBMITTED, otherwise, it will
     *                be CANCELLED.
     * @param extFilters zero or more Strings (or a String[]) that represent extension filters, formatted
     *                  as "<i>filename</i>.<i>extension</i>". Asterisks can be used as wildcards. If none
     *                  are provided, or all provided filters are zero-length or equal to "*.*", all
     *                  files will be shown regardless of extension or filename. Directories are not affected
     *                  by these filters.
     */
    public void setProperties(File src, String title, DialogActionEventHandler handler, String... extFilters)
    {
        if(src == null || !src.exists() || src.isFile() || !src.canRead()){
            throw new IllegalArgumentException("Provided File is not a valid directory for file listing!");
        }else{
            this.src = src;
        }

        this.title = title;

        if(handler == null){
            throw new IllegalArgumentException("Handler must not be null!");
        }
        this.handler = handler;

        if(extFilters.length <= 0){
            this.extFilters = new String[0];
        }else{
            boolean nonZero = false;
            for(String s : extFilters){
                if(s.length() > 0 || !s.trim().equals("*.*")){
                    nonZero = true;
                    break;
                }
            }

            this.extFilters = nonZero ? extFilters : new String[0];
        }
    }

    /**
     * Internal accessor method used by inner anonymous classes to access the current copy of the file
     * list array without duplicate final variable creation.
     * @return the current version of the file list
     */
    private String[] getCurrentFileList(){
        return fileList;
    }

    /**
     * Internal accessor method used by inner anonymous classes to access the current copy of the file
     * reference list array without duplicate final variable creation.
     * @return the current version of the file reference list
     */
    private File[] getCurrentFileRefList(){
        return fileReferenceList;
    }

    public File getSrc() {
        return src;
    }

    public String[] getExtFilters() {
        return extFilters;
    }

    public String getTitle() {
        return title;
    }
}
