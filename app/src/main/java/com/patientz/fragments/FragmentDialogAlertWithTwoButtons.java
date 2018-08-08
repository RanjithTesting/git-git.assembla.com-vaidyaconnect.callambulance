package com.patientz.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class FragmentDialogAlertWithTwoButtons extends DialogFragment {
    ButtonsClickCallBack mCallback;
    public static FragmentDialogAlertWithTwoButtons newInstance(String button2Text, String button1Text, String title,String msg) {
    	FragmentDialogAlertWithTwoButtons frag = new FragmentDialogAlertWithTwoButtons();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("button1_text", button1Text);
        args.putString("button2_text", button2Text);
        args.putString("msg", msg);
        args.putString("dialog_type","dual_button");


        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ButtonsClickCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       Bundle mBundle= getArguments();
        return new AlertDialog.Builder(getActivity())
                .setTitle(mBundle.getString("title"))
                .setMessage(mBundle.getString("msg"))
                .setPositiveButton(mBundle.getString("button1_text"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                  mCallback.button1Clicked();
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton(mBundle.getString("button2_text"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mCallback.button2Clicked();
                                dialog.dismiss();

                            }
                        }
                )
                .create();
    }
    public interface ButtonsClickCallBack
    {
        void button1Clicked();
        void button2Clicked();
    }

}
