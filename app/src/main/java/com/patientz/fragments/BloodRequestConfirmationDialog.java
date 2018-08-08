package com.patientz.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.patientz.activity.R;

public class BloodRequestConfirmationDialog extends DialogFragment
{
    ButtonsClickCallBack mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout. fragment_blood_request_confirmation_dialog, null);
        builder.setView(view);
        TextView mButton= (TextView)view.findViewById(R.id.bt_done);
        final AlertDialog mAlertDialog=builder.create();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                mCallback.button1Clicked();
            }
        });
        return builder.create();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (BloodRequestConfirmationDialog.ButtonsClickCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    public interface ButtonsClickCallBack
    {
        void button1Clicked();
    }
}
