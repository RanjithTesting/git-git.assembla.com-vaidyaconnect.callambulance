package com.patientz.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.patientz.VO.PatientUserVO;
import com.patientz.activity.R;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AsyncAlertingEmri;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.ksoap2.serialization.SoapPrimitive;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by sunil on 08/06/16.
 */
public class OutgoingCallReceiver extends BroadcastReceiver  {
    private static final String TAG = "OutgoingCallReceiver";
    TelephonyManager mTM;
    EndCallListener mEndCallListener;
    GoogleApiClient mGoogleApiClient;
    String phone;
    Context context;
    AddressResultReceiver mResultReceiver;
    SharedPreferences sharedPreferences;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming;
    private static String savedNumber;


    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG,"Call Intercepted");
        this.context=context;
        sharedPreferences = getApplicationContext().getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        Log.d(TAG,"CALL_STATE="+sharedPreferences.getInt("CALL_STATE",0));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            Log.d(TAG,"yyyyy");

            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                phone = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.d(TAG, "PHONE_STATE=" + phone);
                if(!TextUtils.isEmpty(phone))
                {
                if (phone.equalsIgnoreCase("108")) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                            Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
                    if (sharedPreferences.getBoolean(Constant.LOGIN_STATUS, false) == true) {
                        Log.d("LOGGED_IN", "LOGGED_IN");
                        if (phone != null) {
                            Log.d(TAG, "IF");
                            notifyEmri();
                        } else {
                            Log.d(TAG, "else");
                            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            int state = 0;
                            Log.d(TAG, "stateStr=" + stateStr);
                            if (stateStr != null) {
                                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                                    state = TelephonyManager.CALL_STATE_IDLE;
                                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                                    state = TelephonyManager.CALL_STATE_RINGING;
                                }
                                onCallStateChanged(context, state, number);
                            }
                        }
                    }
                }
                }
            }


                /*

                sharedPreferences.edit().putInt("CALL_STATE",TelephonyManager.CALL_STATE_IDLE).commit();
                sharedPreferences.edit().putBoolean("EMRI_NOTIFIED",false).commit();
                mTM = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                Log.d(TAG,"stateStr"+stateStr);
                mEndCallListener=new EndCallListener();
                mTM.listen(mEndCallListener, PhoneStateListener.LISTEN_CALL_STATE);*/
            }
    }

    private void notifyEmri() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        mResultReceiver = new AddressResultReceiver(new Handler());
                        if(getCurrentLocation()!=null)
                        {
                            startIntentServiceFetchAddress(getCurrentLocation());
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                savedNumber = number;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    notifyEmri();
                    Toast.makeText(context, "Outgoing Call Started" , Toast.LENGTH_SHORT).show();
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                }
                else if(isIncoming){
                }
                else{
                    Toast.makeText(context, "outgoing " + savedNumber, Toast.LENGTH_SHORT).show();
                }

                break;
        }
        lastState = state;
    }
    public Location getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        return  LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }


    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state)
            {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG,"CALL_STATE_OFFHOOK");
                    sharedPreferences.edit().putInt("CALL_STATE",TelephonyManager.CALL_STATE_OFFHOOK).commit();
                    Log.d(TAG,"CALL_STATE="+sharedPreferences.getInt("CALL_STATE",0));
                    phone=incomingNumber;
                    if (phone != null && !sharedPreferences.getBoolean("EMRI_NOTIFIED",false)) {
                        mGoogleApiClient = new GoogleApiClient.Builder(context)
                                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                    @Override
                                    public void onConnected(@Nullable Bundle bundle) {
                                        mResultReceiver = new AddressResultReceiver(new Handler());
                                        if(getCurrentLocation()!=null)
                                        {
                                            startIntentServiceFetchAddress(getCurrentLocation());
                                        }
                                    }

                                    @Override
                                    public void onConnectionSuspended(int i) {

                                    }
                                })
                                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                    @Override
                                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                                    }
                                })
                                .addApi(LocationServices.API)
                                .build();
                        mGoogleApiClient.connect();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG,"CALL_STATE_IDLE");
                    Log.d(TAG,"CALL_STATE="+sharedPreferences.getInt("CALL_STATE",0));

                    if(sharedPreferences.getInt("CALL_STATE",0)==TelephonyManager.CALL_STATE_OFFHOOK)
                    {
                        Log.d(TAG,"dimmm");
                        sharedPreferences.edit().putInt("CALL_STATE",TelephonyManager.CALL_STATE_IDLE).commit();
                        sharedPreferences.edit().putBoolean("EMRI_NOTIFIED",true).commit();
                    }
                    break;
            }

           Log.d(TAG,"INCOMING PNO="+incomingNumber);

        }
    }

    protected void startIntentServiceFetchAddress(Location mLastLocation) {
        Intent intent = new Intent(getApplicationContext(), IntentServiceFetchAddress.class);
        intent.putExtra(getApplicationContext().getString(R.string.package_name) + "." + Constant.RECEIVER, mResultReceiver);
        intent.putExtra(getApplicationContext().getString(R.string.package_name) + "." + Constant.LOCATION_DATA_EXTRA, mLastLocation);
        getApplicationContext().startService(intent);
    }
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            Log.d(TAG, "ON RECEIVE CALLED");
            try {
                if (resultData != null) {
                    String address=resultData.getString(getApplicationContext().getString(R.string.package_name) + "." + Constant.FORMATTED_ADDRESS, "");
                    Log.d(TAG, "F Address=" + address);
                    for(int i=0;i<=10;i++)
                    {
                        if (getCurrentLocation() != null) {
                            Log.d("PHONE NUMBER=", phone + "");
                            PatientUserVO mPatientUserVO=AppUtil.getLoggedPatientVO(getApplicationContext());
                                    AsyncAlertingEmri alertingEmriAsync = new AsyncAlertingEmri(
                                            getApplicationContext(), getCurrentLocation(), mPatientUserVO.getEmergencyToken(),address,mPatientUserVO.getPatientId()) {
                                        @Override
                                        protected void onPostExecute(SoapPrimitive result) {
                                            super.onPostExecute(result);
                                            if (result != null) {
                                                int resultCode = Integer.parseInt(result.toString());
                                                switch (resultCode) {
                                                    case Constant.RESPONSE_SUCCESS_EMRI:
                                                        Log.d(TAG,"EMRI NOTIFIED SUCCESSFULLY");
                                                        break;
                                                    default:

                                                        break;
                                                }
                                            }
                                        }
                                    };
                                    alertingEmriAsync.execute();
                            break;
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}




