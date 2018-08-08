package com.patientz.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;

import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.Log;

public class FragmentAlertDialogSingleChoice extends DialogFragment {

    private static final String TAG ="FragmentAlertDialogSingleChoice" ;

    public static FragmentAlertDialogSingleChoice newInstance(String title) {
        FragmentAlertDialogSingleChoice frag = new FragmentAlertDialogSingleChoice();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        try {
            DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
            final SimpleCursorAdapter mSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.layout_select_member, mDatabaseHandler.getCursorForAllUsers(), new String[]{"first_name"}, new int[]{R.id.tv_user_name}, 0);
            Log.d(TAG,mDatabaseHandler.getCursorForAllUsers().getCount()+"");
            Bundle mBundle = getArguments();
            mBuilder.setTitle(mBundle.getString("title"));
            mBuilder.setSingleChoiceItems(mSimpleCursorAdapter, 1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Cursor mCursor=mSimpleCursorAdapter.getCursor();
                    Log.d(TAG,mCursor.getString(mCursor
                            .getColumnIndex("first_name")));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBuilder.create();
    }
}