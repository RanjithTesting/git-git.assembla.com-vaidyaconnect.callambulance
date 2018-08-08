package com.patientz.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.SMSMessageVO;
import com.patientz.VO.WebVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmergencyUtil {

    private final static String TAG = "EmergencyUtil";
    public static final String LOCATION_UPDATE = "location";

    String ACTION_STARTED = "STARTED";
    String ACTION_UPDATE = "UPDATE";
    String ACTION_STOPPED = "STOPPED";
    String ACTION_SMS_SENT = "sent";
    String ACTION_SMS_DELIVERD = "deliverd";
    Context mContext;

    public EmergencyUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static String prepareSMSForServer(Context applicationContext, long pId, long uId, String status,
                                             Location location, int revokeCode, Context context, String emergencyNumber) {
        Log.d(TAG, "E_STATUS=" + status);

        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(applicationContext);
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(pId);
       /* SharedPreferences sp = context
                .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        String emergencyNumber = sp.getString("emergencyNumber", context.getString(R.string.ambulanceNo));*/
       /* if (!emergencyNumber.equalsIgnoreCase(context.getString(R.string.ambulanceNo))) {
            emergencyNumber = "+91-"+emergencyNumber;
        }*/
        int bodyAreasorIssues = defaultSharedPreferences.getInt("selectedEmergencyType", 7);// infected body area
        // as defined in
        // array.
        int emriEmergencyInvoke = defaultSharedPreferences.getInt(Constant.RESPONSE_EMRI_EMERGENCY, 0); // response code : EMRI webservice location passed or not
        Log.d(TAG, "prepareSMSForServer>>Call going to>>" + emergencyNumber);
        SMSMessageVO messageVO = new SMSMessageVO();
        messageVO.setE(getEnvironmentForServerSMS(context));
        messageVO.setpId(pId);
        messageVO.setuId(uId);
        messageVO.setEp(emergencyNumber);
        if (emergencyNumber.equalsIgnoreCase("108")) // bId=0 when emergency raised to 108 orelse bId=org branchId of the hospital selected
        {
            messageVO.setbId(0);
        } else {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            Log.e("bid-->1", "" + mSharedPreferences.getLong("bId", 0));
            if (mSharedPreferences.getLong("bId", 0) != 0) {
                messageVO.setbId(mSharedPreferences.getLong("bId", 0));
                mSharedPreferences.edit().putLong("bId", 0).commit();
                Log.e("bid-->2", "" + mSharedPreferences.getLong("bId", 0));
            } else {
                messageVO.setbId(preferredOrgBranchId);
            }
        }
        Log.d(TAG, "bId=" + messageVO.getbId());
        messageVO.setEt(String.valueOf(bodyAreasorIssues));
        messageVO.setCe(String.valueOf(emriEmergencyInvoke));

        // messageVO.setiP(getLocalIpAddress());
        if (location != null) {
            messageVO.setgLn(location.getLongitude());
            messageVO.setgLt(location.getLatitude());
            if (location.getAccuracy() != 0.0)
                messageVO.setLacc(String.valueOf((int) location.getAccuracy()));
            // if(location.getProvider()!=null)
            // messageVO.setLpd(location.getProvider());
        } else {
            messageVO.setgLn(0.0);
            messageVO.setgLt(0.0);
            messageVO.setLacc(String.valueOf(0));
        }
        messageVO.setDt(new Date().getTime());
        messageVO.setSt(status); // status invoke or revoke
        messageVO.setCd(String.valueOf(revokeCode));

        Gson gson = new Gson();
        gson = new GsonBuilder().create();

        Type typeOfObjectVO = new TypeToken<SMSMessageVO>() {
        }.getType();

        String jsonSMS = gson.toJson(messageVO, typeOfObjectVO);

        return jsonSMS;
    }

    public static String prepareWebVOForServer(Context applicationContext, long pId, long uId,
                                               String status, Location location, int revokeCode, Context context, String emergencyNumber) {
        Log.d(TAG, "E_STATUS=" + status);
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        int bodyAreasorIssues = defaultSharedPreferences.getInt("selectedEmergencyType", 7);// infected body area
        // as defined in
        // array.
        int emriEmergencyInvoke = defaultSharedPreferences.getInt(Constant.RESPONSE_EMRI_EMERGENCY, 0);
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(applicationContext);
        long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(pId);

        WebVO webVO = new WebVO();
        SMSMessageVO messageVO = new SMSMessageVO();
        messageVO.setE(getEnvironmentForServerSMS(context));
        messageVO.setpId(pId);
        messageVO.setuId(uId);
        messageVO.setEp(emergencyNumber);
        if (emergencyNumber.equalsIgnoreCase("108")) // bId=0 when emergency raised to 108 orelse bId=org branchId of the hospital selected
        {
            messageVO.setbId(0);
        } else {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            Log.e("bid-->1", "" + mSharedPreferences.getLong("bId", 0));
            if (mSharedPreferences.getLong("bId", 0) != 0) {
                messageVO.setbId(mSharedPreferences.getLong("bId", 0));
                mSharedPreferences.edit().putLong("bId", 0).commit();
                Log.e("bid-->2", "" + mSharedPreferences.getLong("bId", 0));
            } else {
                messageVO.setbId(preferredOrgBranchId);
            }
        }
        messageVO.setEt(String.valueOf(bodyAreasorIssues));
        messageVO.setCe(String.valueOf(emriEmergencyInvoke));
        // messageVO.setiP(getLocalIpAddress());
        if (location != null) {
            messageVO.setgLn(location.getLongitude());
            messageVO.setgLt(location.getLatitude());
            if (location.getAccuracy() != 0.0)
                messageVO.setLacc(String.valueOf((int) location.getAccuracy()));
            // if(location.getProvider()!=null)
            // messageVO.setLpd(location.getProvider());
        } else {
            messageVO.setgLn(0.0);
            messageVO.setgLt(0.0);
            messageVO.setLacc(String.valueOf(0));
        }
        messageVO.setDt(new Date().getTime());
        messageVO.setSt(status);
        messageVO.setCd(String.valueOf(revokeCode));
        webVO.setsVO(messageVO);
        //	String[] mobileNumber = new String[1];

        DatabaseHandler dh = DatabaseHandler.dbInit(context);
        // mobileNumber[0]
        try {
            ContentValues mValues = dh.getDeviceDetails();
            if (mValues.get("mobile_no") != null) {
                String mobileNumberInDB = (String) mValues.get("mobile_no");
                if (!TextUtils.isEmpty(mobileNumberInDB)) {
                    String[] mobileNumber = mobileNumberInDB.split(",");
                    webVO.seteIPh(mobileNumber);
                    Log.d(TAG, "mobile numbers: " + mobileNumber.toString());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Gson gson = new Gson();
        gson = new GsonBuilder().create();
        Type typeOfObjectVO = new TypeToken<WebVO>() {
        }.getType();

        String jsonSMS = gson.toJson(webVO, typeOfObjectVO);

        return jsonSMS;
    }

    public static String getEnvironmentForServerSMS(Context context) {
        String env = context.getString(R.string.environment).trim();
        // Log.d(tag, msg)
        String envCode = "";
        if (env.equalsIgnoreCase("DEMO")) {
            envCode = "DM";
        } else if (env.equalsIgnoreCase("QA")) {
            envCode = "QA";
        } else if (env.equalsIgnoreCase("Stage")) {
            envCode = "SG";
        } else if (env.equalsIgnoreCase("PREPROD")) {
            envCode = "PP";
        } else if (env.equalsIgnoreCase("PROD")) {
            envCode = "PR";
        } else if (env.equalsIgnoreCase("Beta")) {
            envCode = "PR";
        } else if (env.equalsIgnoreCase("DEV")) {
            envCode = "DV";
        }
        Log.d(TAG, "environment: " + env + "  environment_code+" + envCode);
        return envCode;
    }

    public static String prepareInvokeSMSForContactsWithoutToken(
            String username, Location location, Context mContext) {
        String sms = "";
        if (location != null) {
            sms = username + " is in emergency with following location: ";
            Address mAddress = null;
            if (AppUtil.isOnline(mContext))
                mAddress = getAddress(location, mContext);
            if (mAddress != null)
                sms += AppUtil.truncateString(mAddress.getAddressLine(0), 25)
                        + "  ";
            String mapUrl = "http://maps.google.com/maps?q="
                    + location.getLatitude() + "," + location.getLongitude()
                    + "&z=18";
            sms += mapUrl;
        } else {
            sms = username
                    + " has invoked emergency. Location was unknow at the time of invoke.";
        }
        return sms;
    }

    public static String prepareInvokeSMSForContactWithToken(String username,
                                                             String tokenUrl, Context mContext) {
        if (TextUtils.isEmpty(tokenUrl)) {
            return "";
        } else {
            return username
                    + " is in emergency. Please use the following url to track emergency. "
                    + tokenUrl;
        }
    }

    public String prepareRevokeSMSForContacts(String username, Location location) {
        String sms = "Emergency for " + username
                + " has been revoked with following last known location: ";
        if (location != null) {
            Address mAddress = null;
            if (AppUtil.isOnline(mContext))
                mAddress = getAddress(location, mContext);
            if (mAddress != null)
                sms += AppUtil.truncateString(mAddress.getAddressLine(0), 25)
                        + "  ";
            String mapUrl = "http://maps.google.com/maps?q="
                    + location.getLatitude() + "," + location.getLongitude()
                    + "&z=18";
            sms += mapUrl;
        }
        return sms;
    }


    public static Address getAddress(Location location, Context mContext) {

        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0)
                return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ....Get Device IP Address.... */
    /*public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress
                            .getHostAddress())) {
                        // String ip =
                        // Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.d(TAG,
                                "IP address: " + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }*/


}
