package com.patientz.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import com.patientz.utils.Log;

public class OTPSMSReceiver extends BroadcastReceiver {
    private static final String ACTION_OTP_RECEIVED = "OTPONRECEIVE";
    private String TAG = "OTPSMSReceiver";

    public OTPSMSReceiver() {
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
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber.substring(3);//removing first 3 letters
                    String message = currentMessage.getDisplayMessageBody();
                    Log.d(TAG, "Recieved message : " + message);
                    Log.d(TAG, "Recieved message from : " + senderNum);
                    if (senderNum.equals("ABLNCE") || senderNum.equals("DOCTRZ") || senderNum.equals("NUTRIF")) {
                        Log.d(TAG, "Recieved message from doctrz : " + message);
                        sendUpdateRefershRequest(context, message);
                    }
                }
                //  }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendUpdateRefershRequest(Context context, String msg) {
        Intent otpIntent = new Intent(ACTION_OTP_RECEIVED);
        otpIntent.putExtra("OTP", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(otpIntent);
    }
}
