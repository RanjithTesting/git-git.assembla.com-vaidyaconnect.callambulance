package com.patientz.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.patientz.activity.R;

public class AlertDialogFragment extends DialogFragment {
	
    public static AlertDialogFragment newInstance(String title) {
    	AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getActivity().getString(R.string.app_name))
                .setMessage(title)
                .setPositiveButton(getString(R.string.OK),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	dialog.cancel();
                        }
                    }
                )
                .create();
    }

}
