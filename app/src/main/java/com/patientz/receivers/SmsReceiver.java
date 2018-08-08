package com.patientz.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import com.patientz.utils.Log;

public class SmsReceiver extends BroadcastReceiver {
    private static final String ACTION_MSG_RECEIVED ="ACTION_MSG_RECEIVED" ;
    private String TAG = "SmsReceiver";

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Recieved intent with action  : " + intent.getAction());
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    //   for (int i = 0; i < pdusObj .length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[0]);
                    String message = currentMessage.getDisplayMessageBody();
                    sendUpdateRefershRequest(context, message);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendUpdateRefershRequest(Context context, String msg) {
        Intent otpIntent = new Intent(ACTION_MSG_RECEIVED);
        otpIntent.putExtra("messageReceived", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(otpIntent);
    }
}
