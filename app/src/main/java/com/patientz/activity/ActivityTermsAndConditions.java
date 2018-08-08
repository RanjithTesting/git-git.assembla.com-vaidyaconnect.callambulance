package com.patientz.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.HashMap;

public class ActivityTermsAndConditions extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ActivityTermsAndConditions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditons);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_activity_terms_and_conditons);
        setSupportActionBar(toolbar);

        TextView tvAccept = (TextView) findViewById(R.id.tv_accept);
        TextView tvReject = (TextView) findViewById(R.id.tv_reject);
        tvReject.setOnClickListener(this);
        tvAccept.setOnClickListener(this);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_accept:
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.T_AND_C_ACCEPTED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.T_AND_C_ACCEPTED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                Intent intent = new Intent(ActivityTermsAndConditions.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


                break;
            case R.id.tv_reject:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_msg_tnc_reject);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.T_AND_C_REJECTED, true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.T_AND_C_REJECTED);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        AppUtil.deleteUserDetails(getApplicationContext());
                        Intent intent = new Intent(ActivityTermsAndConditions.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }
}
