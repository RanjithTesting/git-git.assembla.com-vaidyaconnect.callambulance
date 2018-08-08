package com.patientz.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.patientz.VO.BloodyFriendVO;
import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.OrgBranchFacilityVO;
import com.patientz.VO.OrgBranchSpecialityVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.PublicProviderVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.VO.UserSpecialityVO;
import com.patientz.VO.UserUploadedMedia;
import com.patientz.VO.UserVO;
import com.patientz.activity.BuildConfig;
import com.patientz.activity.CallAmbulance;
import com.patientz.activity.R;
import com.patientz.activity.RegistrationActivity;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.fragments.AlertDialogFragment;
import com.patientz.gcm.GcmRegistrationIntentService;
import com.patientz.service.ServiceEmergencyStepsBroadCaster;
import com.patientz.upshot.UpshotEvents;
import com.patientz.upshot.UpshotManager;
import com.patientz.webservices.WebServiceUrls;
import com.squareup.picasso.Picasso;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.patientz.utils.CommonUtils.getSP;


public class AppUtil {
    private static final String TAG = "AppUtil";
    private static MediaPlayer mediaPlayer;

    public static void showToast(Context mContext, String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToastShort(Context mContext, String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 30);
        toast.show();
    }

    public static String getTwoDigitDecimalValue(double doubleValue) {
        return new DecimalFormat("#.#").format(doubleValue);
    }

    public static void showToastLong(Context mContext, String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 30);
        toast.show();
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double theta = lng1 - lng2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        Log.d("APPROXIMATE_DISTANCE=", Double.parseDouble(AppUtil.getTwoDigitDecimalValue(dist)) + "");
        return Double.parseDouble(AppUtil.getTwoDigitDecimalValue(dist));

/*
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        float result=dist/1000;
        Log.d("APPROXIMATE_DISTANCE=",AppUtil.getTwoDigitDecimalValue(result)+"");
        return Float.parseFloat(AppUtil.getTwoDigitDecimalValue(result));*/
    }

    public static void showDialog(Activity activity, String msg) {
        AlertDialogFragment newFragment = AlertDialogFragment.newInstance(msg);
        newFragment.show(activity.getFragmentManager(), "dialog");
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Log.i(TAG, "Connected, Connection type: " + netInfo.getTypeName());
            return true;
        } else {
            Log.i(TAG, "Not Connected");
            return false;
        }
    }

    public static boolean isMyServiceRunning(Context mContext, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static boolean isGpsEnabled(Activity activity) {

        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            buildAlertMessageNoGps(activity);

            return false;
        }


        return true;
    }

    public static boolean isGpsEnabled2(Activity activity) {

        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }

    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private static void buildAlertMessageNoGps(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static long getEventLogID(Context mContext) {
        DatabaseHandler dh = DatabaseHandler.dbInit(mContext);
        long eventID = 0;
        try {
            eventID = dh.getLatestEventLogID(mContext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return eventID;
    }

    public static String getCity(String cityOfPractice) {
        if (cityOfPractice != null) {
            String[] array = cityOfPractice.split(",");
            String city = array[0];
            return city;
        }
        return "";
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    public static boolean isValidMobileNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    public static void downloadImage(final Context context, String imageUrl, final ImageView image, final String fileName) {
        String url = WebServiceUrls.serverUrl + imageUrl;
        Log.d(TAG, "URL=" + url);
        Log.d(TAG, "image=" + image);
        RequestQueue rq = Volley.newRequestQueue(context);
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                Log.d(TAG, "bitmap=" + bitmap);
                Log.d(TAG, "image view=" + image);
                image.setImageBitmap(bitmap); // set image to view
                        /* Storing downloaded image locally */
                        /*
                         * String extStorageDirectory = Environment
						 * .getExternalStorageDirectory().toString();
						 */
                final String STORAGE_PATH = context
                        .getResources().getString(
                                R.string.profileImagePath);
                File cnxDir = new File(Environment
                        .getExternalStorageDirectory(), STORAGE_PATH);
                if (!cnxDir.exists()) {
                    Log.d(TAG, "creating directory");
                    cnxDir.mkdirs();
                }
                File file = new File(cnxDir, fileName);
                Log.d(TAG, "FILE STORED URI =" + file.getAbsolutePath());
                //FILE STORED URI =/storage/emulated/0/Doctrz4MeDemo/Images/T_ShirtDesign-CallAmbulance-White.jpg

                try {
                    OutputStream outStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String filePath = Environment.getExternalStorageDirectory()
                        .getPath()
                        + context.getResources().getString(
                        R.string.profileImagePath) + fileName;
                Log.d(TAG, "FILE path =" + filePath);
                File f = new File(filePath);
                Log.d(TAG, "FILE URI =" + f.getAbsolutePath());
                Picasso.with(context).load(f).transform(new CircleTransform()).into(image);
            }
        }, 0, 0, null, null);
        rq.add(ir);

    }

    public static boolean checkIfFileExists(Context mContext, String fileName) {
        String localImagePath = mContext
                .getResources().getString(
                        R.string.profileImagePath);
        if (fileName != null) {
            File mFile = new File(Environment
                    .getExternalStorageDirectory() + localImagePath, fileName);
            Log.d(TAG, "FILE URI=" + mFile.getName() + "    " + mFile.getAbsolutePath());
            if (mFile.exists()) {
                Log.d(TAG, "FILE EXISTS");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static ArrayList<BloodyFriendVO> readPhoneContacts(Context cntx) {//This Context parameter is nothing but your Activity class's Context


        ArrayList<BloodyFriendVO> contactslist = new ArrayList<BloodyFriendVO>();
        DatabaseHandler dh = DatabaseHandler.dbInit(cntx);

        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list

        Log.e("-----contactsCount------", " " + contactsCount);
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String lookupid = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

//                Log.e("-----Id------", " " + id);
//                Log.e("DISPLAY_NAME", " " + contactName);

               /* Cursor emailCur = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);

                while (emailCur.moveToNext()) {
                    // This would allow you get several email addresses
                    // if the email addresses were stored in an array
                    String email = emailCur.getString(
                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                    String emailType = emailCur.getString(
//                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                    if (email != null && !TextUtils.isEmpty(email) && email.trim().length() > 10 && email.contains("@")) {
                        Log.e("Email", email + " - " + id + " : Id ");
                        BloodyFriendVO phvo = new BloodyFriendVO();
                        phvo.setContactId(Integer.parseInt(id));
                        phvo.setContact(email.trim());
                        phvo.setContactName(contactName);
                        phvo.setContactType(2);
                        phvo.setLookUpId(lookupid);
                        phvo.setUserInvited(false);
                        contactslist.add(phvo);
                    }
                }
                emailCur.close();*/

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    //the below cursor will give you details for multiple contacts
                    Cursor pCursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                    while (pCursor.moveToNext()) {
                        int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        //String isStarred 		= pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
                        String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //you will get all phone numbers according to it's type as below switch case.
                        //Logs.e will print the phone number along with the name in DDMS. you can use these details where ever you want.
                        switch (phoneType) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:

                                if (!TextUtils.isEmpty(phoneNo) && phoneNo != null && phoneNo.trim().length() >= 10) {
                                    Log.e(contactName + ": TYPE_MOBILE - " + id + " : Id ", " " + phoneNo);
                                    BloodyFriendVO phvo = new BloodyFriendVO();
                                    phvo.setContactId(Integer.parseInt(id));
                                    phvo.setContact(getE164FormatPhoneNumber(phoneNo.trim()));
                                    phvo.setContactName(contactName);
                                    phvo.setContactType(1);
                                    phvo.setLookUpId(lookupid);
                                    phvo.setUserInvited(cntx.getSharedPreferences("invited_status", 4).getBoolean(phoneNo.trim(), false));
                                    contactslist.add(phvo);
                                }

                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:

                                if (!TextUtils.isEmpty(phoneNo) && phoneNo != null && phoneNo.trim().length() >= 10) {
                                    Log.e(contactName + ": TYPE_HOME - " + id + " : Id ", " " + phoneNo);
                                    BloodyFriendVO phvo = new BloodyFriendVO();
                                    phvo.setContactId(Integer.parseInt(id));
                                    phvo.setContact(getE164FormatPhoneNumber(phoneNo.trim()));
                                    phvo.setContactName(contactName);
                                    phvo.setContactType(1);
                                    phvo.setLookUpId(lookupid);
                                    phvo.setUserInvited(cntx.getSharedPreferences("invited_status", 4).getBoolean(phoneNo.trim(), false));
                                    contactslist.add(phvo);
                                }

                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:

                                if (!TextUtils.isEmpty(phoneNo) && phoneNo != null && phoneNo.trim().length() >= 10) {
                                    Log.e(contactName + ": TYPE_WORK - " + id + " : Id ", " " + phoneNo);
                                    BloodyFriendVO phvo = new BloodyFriendVO();
                                    phvo.setContactId(Integer.parseInt(id));
                                    phvo.setContact(getE164FormatPhoneNumber(phoneNo.trim()));
                                    phvo.setContactName(contactName);
                                    phvo.setContactType(1);
                                    phvo.setLookUpId(lookupid);
                                    phvo.setUserInvited(cntx.getSharedPreferences("invited_status", 4).getBoolean(phoneNo.trim(), false));
                                    contactslist.add(phvo);
                                }

                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:

                                if (!TextUtils.isEmpty(phoneNo) && phoneNo != null && phoneNo.trim().length() >= 10) {
                                    Log.e(contactName + ": TYPE_WORK_MOBILE - " + id + " : Id ", " " + phoneNo);
                                    BloodyFriendVO phvo = new BloodyFriendVO();
                                    phvo.setContactId(Integer.parseInt(id));
                                    phvo.setContact(getE164FormatPhoneNumber(phoneNo.trim()));
                                    phvo.setContactName(contactName);
                                    phvo.setContactType(1);
                                    phvo.setLookUpId(lookupid);
                                    phvo.setUserInvited(cntx.getSharedPreferences("invited_status", 4).getBoolean(phoneNo.trim(), false));
                                    contactslist.add(phvo);
                                }
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:

                                if (!TextUtils.isEmpty(phoneNo) && phoneNo != null && phoneNo.trim().length() >= 10) {
                                    Log.e(contactName + ": TYPE_OTHER - " + id + " : Id ", " " + phoneNo);
                                    BloodyFriendVO phvo = new BloodyFriendVO();
                                    phvo.setContactId(Integer.parseInt(id));
                                    phvo.setContact(getE164FormatPhoneNumber(phoneNo.trim()));
                                    phvo.setContactName(contactName);
                                    phvo.setContactType(1);
                                    phvo.setLookUpId(lookupid);
                                    phvo.setUserInvited(cntx.getSharedPreferences("invited_status", 4).getBoolean(phoneNo.trim(), false));
                                    contactslist.add(phvo);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    pCursor.close();
                }
            }
            cursor.close();
        }
        if (contactslist.size() > 0) {
            try {
                dh.insertPhoneContactsInfo2(contactslist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contactslist;
    }

    public static Map<String, String> addHeadersForApp(Context context, Map<String, String> oldParams) {
        Map<String, String> params = new HashMap<String, String>();
        params.putAll(oldParams);
//Log.e("AUTHORIZATION-",getSP(CallAmbulance.getAppContext()).getString(Constant.TOKEN_TYPE, "") + " "
//        + getSP(CallAmbulance.getAppContext()).getString(Constant.ACCESS_TOKEN, ""));
        params.put(Constant.AUTHORIZATION,
                getSP(CallAmbulance.getAppContext()).getString(Constant.TOKEN_TYPE, "") + " "
                        + getSP(CallAmbulance.getAppContext()).getString(Constant.ACCESS_TOKEN, ""));
        params.put("mydevice", "android");
        params.put("app", "callambulance");
        params.put("lastSyncId", "" + 0);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        params.put("IMEI", telephonyManager.getDeviceId());
        params.put("OS-OSversion", "Android-" + android.os.Build.VERSION.RELEASE);
        params.put("Model", android.os.Build.MODEL);
        params.put("Manufacturer", android.os.Build.MANUFACTURER);
        SharedPreferences mSharedPreferences = context.getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        if (mSharedPreferences != null) {
            params.put("loggedInLatitude", mSharedPreferences.getString("lat", "0"));
            params.put("loggedInLongitude", mSharedPreferences.getString("lon", "0"));
        }
        Log.d(TAG, "header params=" + params);
        return params;
    }


    public static String getDate() {

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());

        System.out.println("current date - " + formattedDate);

        return formattedDate;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static boolean checkLoginDetails(Activity context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        Log.d("Check login details ", "LOGIN_STATUS : " + sharedPreferences.getBoolean(Constant.LOGIN_STATUS, false));
        if (sharedPreferences.getBoolean(Constant.LOGIN_STATUS, false) == true) {
            return true;
        } else {
            return false;
        }
    }

    public static void deleteUserDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        sharedPreferences.edit().clear().commit();
        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(context);
        defaultSp.edit().clear().commit();
        SharedPreferences sp = context.getSharedPreferences(
                Constant.COMMON_SP_FILE, MODE_PRIVATE);
        sp.edit().clear().commit();
        DatabaseHandler dh = DatabaseHandler.dbInit(context);
        //clearPendingInsurancesSpForAllUsers(dh,context);
        dh.deleteDatabaseAndClearStaticVariables(context);
        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(context);
        databaseHandlerAssetHelper.deleteDatabaseAndClearStaticVariables(context);
        context.getSharedPreferences("sync_data", 4).edit().clear().commit();
        context.getSharedPreferences("invited_status", 4).edit().clear().commit();
        final SharedPreferences prefs = AppUtil.getGcmPreferences(context);
        prefs.edit().clear().commit();
        Intent intent = new Intent(context, ServiceEmergencyStepsBroadCaster.class);
        context.stopService(intent);
    }

    private static void clearPendingInsurancesSpForAllUsers(DatabaseHandler dh, Context context) {
        try {
            ArrayList<PatientUserVO> mPatientUserVOS = dh.getAllUser();
            for (PatientUserVO patientUserVO : mPatientUserVOS) {
                final SharedPreferences mSharedPreferences = context.getSharedPreferences(String.valueOf(patientUserVO.getPatientId()), Context.MODE_PRIVATE);
                Log.d("patientId=" + patientUserVO.getPatientId(), "contains patientId=" + mSharedPreferences.contains("patientId"));
                if (mSharedPreferences.contains("patient_id")) {
                    mSharedPreferences.edit().clear().commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean stopEmergencyService(Context mContext) {
        return mContext.stopService(new Intent(mContext,
                ServiceEmergencyStepsBroadCaster.class));
    }

    public static String convertToJsonString(Object object) {
        if (object != null) {
            Gson gson = new Gson();
            gson = new GsonBuilder().setDateFormat("E, MMM dd, yyyy").create();
            String toSendJson = gson.toJson(object, object.getClass());
            Log.d(TAG, "PARSED_JSON=" + toSendJson);
            return toSendJson;
        } else {
            return "";
        }
    }


    public static void sendSMS(Context context, String number, String message) {
        Log.d(TAG, "sms : " + message + " & number : " + number + " & length : " + message.length());
        String SENT = "SMS_SENT1";
        String DELIVERED = "SMS_DELIVERED1";
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT).putExtra("number", number), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED).putExtra("time", new Date().toString()), PendingIntent.FLAG_UPDATE_CURRENT);
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> messageParts = sms.divideMessage(message);
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(messageParts.size());
        sentIntents.add(sentPI);
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>(messageParts.size());
        deliveryIntents.add(deliveredPI);
        sms.sendMultipartTextMessage(number, null, messageParts, sentIntents,
                deliveryIntents);
    }


    public static StringBuilder convertToCamelCase(String string) {
        StringBuilder ret = new StringBuilder();
        if (!TextUtils.isEmpty(string)) {
            ret.append(string.substring(0, 1).toUpperCase());
            ret.append(string.substring(1).toLowerCase());
        }
        return ret;
    }

    public static String convertToAge(Date dob, Context context) {
        int cutOfMonthsToDisplayAge = context.getResources().getInteger(
                R.integer.ageInMonths);

        if (dob != null) {
            Calendar c = Calendar.getInstance();
            Calendar d = Calendar.getInstance();
            d.setTime(dob);
            // Date date=dob;
            int ageInMonths;
            int dob_year = d.get(Calendar.YEAR);
            int dob_month = d.get(Calendar.MONTH) + 1;
            int dob_day = d.get(Calendar.DAY_OF_MONTH);
            int c_year = c.get(Calendar.YEAR);
            int c_month = c.get(Calendar.MONTH) + 1;
            int c_day = c.get(Calendar.DAY_OF_MONTH);
            int r_year = c_year - dob_year;
            int r_month;
            if (c_month < dob_month) {
                // if current month value is less than dob month
                // subtract current year by 1
                //
                Log.d("1", "1");
                r_year = r_year - 1;
                r_month = 12 + c_month - dob_month;
            } else {
                Log.d("2", "2");
                Log.d("c_month", c_month + "");
                Log.d("dob_month", dob_month + "");

                r_month = c_month - dob_month;
                Log.d("r_month", r_month + "");
            }
            if (c_day < dob_day) {
                Log.d("3", "3");
                r_month = r_month - 1;
            }
            if (r_month < 0) {
                Log.d("4", "4");
                r_month = r_month + 12;
                r_year = r_year - 1;
            }
            ageInMonths = (r_year * 12) + r_month;

            if (ageInMonths >= cutOfMonthsToDisplayAge) {
                Log.d("5", "5");
                return r_year + "Y";
            } else {
                Log.d("6", "6");
                return ageInMonths + "M";
            }
        }
        return "";
    }

    public static int getAge(Date dob, Context context) {
        int cutOfMonthsToDisplayAge = context.getResources().getInteger(
                R.integer.ageInMonths);

        if (dob != null) {
            Calendar c = Calendar.getInstance();
            Calendar d = Calendar.getInstance();
            d.setTime(dob);
            // Date date=dob;
            int ageInMonths;
            int dob_year = d.get(Calendar.YEAR);
            int dob_month = d.get(Calendar.MONTH) + 1;
            int dob_day = d.get(Calendar.DAY_OF_MONTH);
            int c_year = c.get(Calendar.YEAR);
            int c_month = c.get(Calendar.MONTH) + 1;
            int c_day = c.get(Calendar.DAY_OF_MONTH);
            int r_year = c_year - dob_year;
            int r_month;
            if (c_month < dob_month) {
                // if current month value is less than dob month
                // subtract current year by 1
                //
                Log.d("1", "1");
                r_year = r_year - 1;
                r_month = 12 + c_month - dob_month;
            } else {
                Log.d("2", "2");
                Log.d("c_month", c_month + "");
                Log.d("dob_month", dob_month + "");

                r_month = c_month - dob_month;
                Log.d("r_month", r_month + "");
            }
            if (c_day < dob_day) {
                Log.d("3", "3");
                r_month = r_month - 1;
            }
            if (r_month < 0) {
                Log.d("4", "4");
                r_month = r_month + 12;
                r_year = r_year - 1;
            }
            ageInMonths = (r_year * 12) + r_month;

            if (ageInMonths >= cutOfMonthsToDisplayAge) {
                Log.d("5", "5");
                return r_year;
            }
        }
        return 0;
    }


    public static void downloadImage(final Activity activity,
                                     String imageUrl, final ImageView image, final String fileName) {
        String url = WebServiceUrls.serverUrl + imageUrl;
        Log.d(TAG, "url");
        RequestQueue rq = Volley.newRequestQueue(activity);

        ImageRequest ir = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d(TAG, "bitmap=" + bitmap);
                        if (bitmap != null) {
                            //image.setImageBitmap(bitmap); // set image to view
                        /* Storing downloaded image locally */
                        /*
                         * String extStorageDirectory = Environment
						 * .getExternalStorageDirectory().toString();
						 */
                            Log.d(TAG, "bitmap=" + bitmap);
                            final String STORAGE_PATH = activity
                                    .getResources().getString(
                                            R.string.profileImagePath);
                            File cnxDir = new File(Environment
                                    .getExternalStorageDirectory(), STORAGE_PATH);
                            if (!cnxDir.exists()) {
                                try {
                                    cnxDir.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            File file = new File(cnxDir, fileName);
                            try {
                                OutputStream outStream = new FileOutputStream(file);

                                bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                        outStream);
                                outStream.flush();
                                outStream.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            }
                        }
                    }
                }, 0, 0, null, null);
        rq.add(ir);
        String localPath = Environment.getExternalStorageDirectory()
                .getPath()
                + activity.getResources().getString(
                R.string.profileImagePath);
        final File f = new File(localPath + fileName);
        final String path = CommonUtils.decodeFile(localPath, image.getWidth(), image.getHeight());
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Picasso.with(activity).load(path).transform(new CircleTransform()).into(image);
            }
        }, 2000);
    }

    public static String truncateString(String str, int maxLen) {
        if (str != null && str.trim().length() > 0) {
            if (str.length() < maxLen) {
                return str;
            } else {
                return (str.substring(0, maxLen - 2) + "..");
            }
        } else {
            return null;
        }
    }

    public static PatientUserVO getLoggedPatientVO(Context mContext) {
        DatabaseHandler dh = DatabaseHandler.dbInit(mContext);
        PatientUserVO mUserVO = new PatientUserVO();
        try {
            mUserVO = dh.getProfile("Self");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mUserVO;
    }

    public static PatientUserVO getPatientVO(long patientId, Context mContext) {
        DatabaseHandler dh = DatabaseHandler.dbInit(mContext);
        PatientUserVO mUserVO = new PatientUserVO();
        try {
            mUserVO = dh.getProfile(patientId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mUserVO;
    }

    public static UserInfoVO getUserInfoVO(long patientId, Context mContext) {
        DatabaseHandler dh = DatabaseHandler.dbInit(mContext);
        try {
            return dh.getUserInfo(patientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static void setSponsoredByLogos(Context mContext, ImageView mImageView, long preferredOrgBranchId, ProgressBar progressBar) {
        //Picasso.with(mContext).placeholder(R.drawable.progress_circle_animation).into(mImageView);

        boolean isFileExists = false;
        String fileName;
        try {
            fileName = "img_" + preferredOrgBranchId;
            isFileExists = checkIfFileExists(mContext,
                    fileName);

            Log.d(TAG, "File Exists ???" + isFileExists);

            if (!isFileExists) {

                Log.d(TAG, "Pic Id???" + preferredOrgBranchId);

                mImageView.setImageBitmap(null);
                downloadImage(mContext, orgLogoId, fileName, mImageView, null);

               *//* RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                UserInfoVO mUserInfoVO = null;
                long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(mContext);
                DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(mContext);
                mUserInfoVO = mDatabaseHandler.getUserInfo(currentSelectedProfile);
                mRequestQueue.add(createPreferredOrgBranchRequest(mUserInfoVO.getPatientId(), mContext, fileName, mImageView));*//*
                // call webservice to download the image if exists

            } else {
                //progressBar.setVisibility(View.GONE);
                final String STORAGE_PATH = mContext
                        .getResources().getString(
                                R.string.profileImagePath);
                File cnxDir = new File(Environment
                        .getExternalStorageDirectory(), STORAGE_PATH);
                if (!cnxDir.exists()) {
                    cnxDir.mkdirs();
                }
                File file = new File(cnxDir, fileName);
                Picasso.with(mContext).load(file).resize(100, 100).into(mImageView);

            }
        } catch (Exception e) {
            //progressBar.setVisibility(View.GONE);
            mImageView.setImageResource(R.drawable.dashboard_emri);
        }
    }*/


    public static StringRequest createPreferredOrgBranchRequest(final long patientId, final Context mContext, final String fileName, final ImageView mImageView) {
        Log.d(TAG, "createPreferredOrgBranchRequest");
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.GET_PREFERRED_ORG_BRANCH + patientId;
        Log.d(TAG, "szServerUrl - " + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, " response : " + response);
                if (response != null) {
                    Log.d(TAG, "Got webservice response");
                    try {
                        JSONObject mJsonObj = new JSONObject(response.toString());
                        Log.d(TAG, "$$$->" + mJsonObj.has("organization") + "" + mJsonObj.has("orgLogo"));
                        if (mJsonObj.has("organization") && mJsonObj.getJSONObject("organization").has("orgLogo")) {
                            long orgLogoId = mJsonObj.getJSONObject("organization").getJSONObject("orgLogo").getLong("id");
                            Log.d(TAG, "$$$->" + orgLogoId);
                            downloadImage(mContext, orgLogoId, fileName, mImageView, null);
                        } else {
                            Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(mImageView);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(mImageView);

                    }
                } else {
                    Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(mImageView);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(mImageView);
                Log.i("error1 - ", "" + Build.VERSION.SDK_INT);
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                } else if (volleyError instanceof AuthFailureError) {
                } else {
                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(mContext, super.getHeaders());
            }
        };
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    public static void downloadImage(final Context mContext, final long fileId, final String fileName, final ImageView imageView, final ProgressBar progressBar) {
        long patientId = getCurrentSelectedPatientId(mContext);

        String imageUrl = WebServiceUrls.getPatientAttachment + fileId + "&patientId=" + patientId + "&moduleType=" + Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
        String url = WebServiceUrls.serverUrl + imageUrl;
        Log.d(TAG, "Image Url???" + url);
        RequestQueue rq = Volley.newRequestQueue(mContext);
        ImageRequest ir = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if (bitmap != null) {
                            final String STORAGE_PATH = mContext
                                    .getResources().getString(
                                            R.string.profileImagePath);
                            File cnxDir = new File(Environment
                                    .getExternalStorageDirectory(), STORAGE_PATH);
                            if (!cnxDir.exists()) {
                                Log.d(TAG, "creating directory");
                                cnxDir.mkdirs();
                            }
                            File file = new File(cnxDir, fileName);

                            try {
                                OutputStream outStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50,
                                        outStream);
                                outStream.flush();
                                outStream.close();
                                Picasso.with(mContext).load(file).resize(130, 130).into(imageView);
                            } catch (Exception e) {
                                Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(imageView);
                            }
                        } else {
                            Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(imageView);
                        }
                    }
                }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(imageView);

            }
        });
        rq.add(ir);
    }

    /* public static Bitmap decodeSampledBitmapFromResource(Resources res, OutputStream outputStream,
                                                          int reqWidth, int reqHeight) {

         // First decode with inJustDecodeBounds=true to check dimensions
         final BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeStream(new ByteArrayOutputStream(outputStream.));

         // Calculate inSampleSize
         options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

         // Decode bitmap with inSampleSize set
         options.inJustDecodeBounds = false;
         return BitmapFactory.decodeResource(res, resId, options);
     }
     public static int calculateInSampleSize(
             BitmapFactory.Options options, int reqWidth, int reqHeight) {
         // Raw height and width of image
         final int height = options.outHeight;
         final int width = options.outWidth;
         int inSampleSize = 1;

         if (height > reqHeight || width > reqWidth) {

             final int halfHeight = height / 2;
             final int halfWidth = width / 2;

             // Calculate the largest inSampleSize value that is a power of 2 and keeps both
             // height and width larger than the requested height and width.
             while ((halfHeight / inSampleSize) > reqHeight
                     && (halfWidth / inSampleSize) > reqWidth) {
                 inSampleSize *= 2;
             }
         }

         return inSampleSize;
     }*/
    public static void setSpinnerValues(Activity context, Spinner spinner,
                                        int arrayValues) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, arrayValues, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public static LinkedHashMap<String, String> getMap(Context context, String json) {
        Gson gson = new Gson();
        Type entityType = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();
        return gson.fromJson(json, entityType);
    }

    public static UserUploadedMedia convertJsonToGson(String target) {
        Gson gson = new Gson();
        gson = new GsonBuilder().setDateFormat("E, MMM dd, yyyy").create();

        Type entityType = new TypeToken<UserUploadedMedia>() {
        }.getType();
        return gson.fromJson(target, entityType);
    }

    public static String getPhNoWithoutIsdCode(String phNoWithIsdCode) {
        String phoneNumber = "";
        Log.d(TAG, "PHONE NO=" + phNoWithIsdCode);
        if (!TextUtils.isEmpty(phNoWithIsdCode)) {
            String[] separate = phNoWithIsdCode.split("-");
            if (separate.length == 2) {
                phoneNumber = separate[1];
            } else {
                phoneNumber = "";
            }
        }
        return phoneNumber;
    }

    public static String getOrgTypeCode(Activity mContext, String orgType) {
        String orgTypeCode = "";
        try {
            String[] orgTypeNames = mContext.getResources().getStringArray(
                    R.array.array_orgtype);
            String[] orgTypeCodes = mContext.getResources().getStringArray(
                    R.array.array_orgtype_id);
            for (int i = 0; i < orgTypeNames.length; i++) {
                if (orgType.equals(orgTypeNames[i])) {
                    orgTypeCode = orgTypeCodes[i];
                    break;
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return orgTypeCode;
    }

    public static void call(Context mContext, String phoneNumber) {
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            mContext.startActivity(callIntent);
        } else {
            AppUtil.showToast(mContext, mContext.getString(R.string.msg_no_call_permission));
        }
    }

    public static void share(Context mContext, String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }

    public static long getCurrentSelectedPatientId(Context mContext) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        long currentSelectedPatientId = defaultSharedPreferences.getLong("current_selected_user", 0);
        if (currentSelectedPatientId == 0) {
            PatientUserVO patientUserVO = AppUtil.getLoggedPatientVO(mContext);
            return patientUserVO.getPatientId();
        }
        return currentSelectedPatientId;
    }

    public static long getCurrentSelectedUserId(Context mContext) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        long currentSelectedPatientId = defaultSharedPreferences.getLong("current_selected_user", 0);
        Log.d(TAG, "Querying for patient id = " + currentSelectedPatientId);
        if (currentSelectedPatientId == 0) {
            PatientUserVO patientUserVO = AppUtil.getLoggedPatientVO(mContext);
            return patientUserVO.getUserProfileId();
        } else {
            PatientUserVO patientUserVO = AppUtil.getPatientVO(currentSelectedPatientId, mContext);
            return patientUserVO.getUserProfileId();
        }
    }


    public static Gson getGson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonTimeZoneAdapter()).create();
        return new Gson();
    }

    public static SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GcmRegistrationIntentService.class.getSimpleName(),
                MODE_PRIVATE);
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void setDialerScreenDialogFlag(Context applicationContext, boolean flag) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        defaultSharedPreferences.edit().putBoolean("show_dialer_screen_dialog", flag).commit();
        //AppUtil.showToast(applicationContext,"SHOW POP UP="+flag);
    }

    public static boolean showDialerScreen(Context applicationContext) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        Log.d(TAG, "SHOW 108 DIALOG=" + defaultSharedPreferences.getBoolean("show_dialer_screen_dialog", true));
        return defaultSharedPreferences.getBoolean("show_dialer_screen_dialog", true);
    }

    public static boolean checkInsuranceStatus(InsuranceVO insuranceVO) {
        boolean isActive = false;
        if (insuranceVO.getInsPolicyEndDate() != null) {
            Date insuranceDate = insuranceVO.getInsPolicyEndDate();
            Calendar calendar = Calendar.getInstance();
            if (insuranceDate.getTime() >= calendar.getTimeInMillis()) {
                isActive = true;
            }
        }
        return isActive;
    }

    public static boolean requestPermissions(Activity activity, String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity, new String[]{android.Manifest.permission.READ_SMS},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPhoneNumberValid(String phoneNo, String countryShortName) {
        boolean isPhoneNoValid = false;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber indiaNumberProto = phoneUtil.parse(phoneNo, countryShortName);
            isPhoneNoValid = phoneUtil.isValidNumber(indiaNumberProto);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return isPhoneNoValid;
    }

    public static PublicProviderVO getPublicEmergencyProvider(Context applicationContext) {
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(applicationContext);
        PublicProviderVO mPublicProviderVO;
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        Log.d(TAG, "getPublicEmergencyProvider>>COUNTRY=" + mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, ""));
        Log.d(TAG, "getPublicEmergencyProvider>>STATE=" + mSharedPreferences.getString(Constant.STATE_SHORT_NAME, ""));

        mPublicProviderVO = mDatabaseHandler.getPublicProvider(mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, ""), mSharedPreferences.getString(Constant.STATE_SHORT_NAME, ""), mSharedPreferences.getInt("selectedEmergencyType", 0));
        if (mPublicProviderVO == null) {
            mPublicProviderVO = mDatabaseHandler.getPublicProvider(mSharedPreferences.getString(Constant.COUNTRY_SHORT_NAME, ""), "", mSharedPreferences.getInt("selectedEmergencyType", 0));
        }

        if (mPublicProviderVO == null) {
            mPublicProviderVO = new PublicProviderVO();
            switch (mSharedPreferences.getInt("selectedEmergencyType", 0)) {
                case Constant.EMERGENCTY_TYPE_FIRE:
                    mPublicProviderVO.setDisplayName("101");
                    mPublicProviderVO.setEmergencyPhoneNo("101");
                    break;
                case Constant.EMERGENCTY_TYPE_POLICE:
                    mPublicProviderVO.setDisplayName("100");
                    mPublicProviderVO.setEmergencyPhoneNo("100");
                    break;
                default:
                    mPublicProviderVO.setDisplayName("108");
                    mPublicProviderVO.setEmergencyPhoneNo("108");
                    break;

            }
        }
        return mPublicProviderVO;
    }

    public static double getDistanceToInKms(Context context, double currentLocationLat, double currentLocationLon, double destLat, double destLon) {
        double distanceInKms;
        float[] result = new float[1];
        Location.distanceBetween(currentLocationLat, currentLocationLon, destLat, destLon, result);
        distanceInKms = result[0] / 1000;
        return distanceInKms;
    }

    public static String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path,
                    DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap,
                    DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            // Store to tmp file

            File file = new File(Environment.getExternalStorageDirectory(), "image_" + String.valueOf(System.currentTimeMillis()) + ".png");

            strMyImagePath = file.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }

        return strMyImagePath;

    }


    public static void storeUserVO(Context context, UserVO user) {
        String userDetails = "";
        if (user != null) {
            userDetails = convertToJsonString(user);
        }
        Log.d(TAG, "user details a : " + userDetails);
        SharedPreferences sp = getSP(context);
        sp.edit().putString("userDetails", userDetails).commit();
    }


    public static UserVO getLoggedUser(Context context) {
        Gson gson = new GsonBuilder().setDateFormat("E, MMM dd, yyyy").create();
        Type entityType = new TypeToken<UserVO>() {
        }.getType();
        UserVO user = new UserVO();
        try {
            Log.d(TAG, "user details b : " + getSP(context).getString("userDetails", ""));
            user = gson.fromJson(getSP(context).getString("userDetails", ""), entityType);
        } catch (Exception e) {
            Log.d(TAG, "Exception parsing : " + e.getMessage());
        }
        return user;
    }

    public static ArrayList<UserSpecialityVO> getSpecialities(Context context, String jsonSpecialities) {
        ArrayList<UserSpecialityVO> mUserSpecialityVOs = new ArrayList<>();
        if (jsonSpecialities != null) {
            Gson gson = new Gson();
            Type objectType = new TypeToken<ArrayList<UserSpecialityVO>>() {
            }.getType();
            mUserSpecialityVOs = gson.fromJson(
                    jsonSpecialities, objectType);
        }
        return mUserSpecialityVOs;
    }

    public static ArrayList<OrgBranchSpecialityVO> getOrgBranchSpecialities(Context context, String jsonSpecialities) {
        ArrayList<OrgBranchSpecialityVO> mUserSpecialityVOs = new ArrayList<>();
        if (jsonSpecialities != null) {
            Gson gson = new Gson();
            Type objectType = new TypeToken<ArrayList<OrgBranchSpecialityVO>>() {
            }.getType();
            mUserSpecialityVOs = gson.fromJson(
                    jsonSpecialities, objectType);
        }
        return mUserSpecialityVOs;
    }

    public static ArrayList<OrgBranchFacilityVO> getOrgBranchFacilities(Context context, String jsonSpecialities) {
        ArrayList<OrgBranchFacilityVO> mOrgBranchFacilityVOs = new ArrayList<>();
        if (jsonSpecialities != null) {
            Gson gson = new Gson();
            Type objectType = new TypeToken<ArrayList<OrgBranchFacilityVO>>() {
            }.getType();
            mOrgBranchFacilityVOs = gson.fromJson(
                    jsonSpecialities, objectType);
        }
        return mOrgBranchFacilityVOs;
    }


    public static void updateUserInfoUpshot(Context context) {
        /*UpshotManager user creations*/
        PatientUserVO selfProfile = AppUtil.getLoggedPatientVO(context);
        UserInfoVO userInfoVO = AppUtil.getUserInfoVO(selfProfile.getPatientId(), context);
        if (userInfoVO != null) {
            UpshotManager.setUserDetails(context, userInfoVO);
        }
        /*App language*/
        HashMap<String, Object> bkDATA = new HashMap<>();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = defaultSharedPreferences.getString("sel_language", "en");
        bkDATA.put("app_language", lang);
        Log.d("app_language", bkDATA.toString());
        UpshotEvents.createCustomEvent(bkDATA, 15);
    }

    public static void sendCampaignDetails(final Context context, String eventtype) {
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        String deepLink = sharedPreferences.getString(Constant.INCOMING_DYNAMIC_LINK, "");
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(deepLink);
        sanitizer.parseUrl(deepLink);
        params.put("dynamicLink", "" + deepLink);
        params.put("appPackageName", "" + BuildConfig.APPLICATION_ID);
        params.put("campaignName", "" + sanitizer.getValue("utm_campaign"));
        params.put("campaignSource", "" + sanitizer.getValue("utm_source"));
        params.put("campaignMedium", "" + sanitizer.getValue("utm_medium"));
        params.put("campaignId", "123321");
        params.put("referralCode", "" + sanitizer.getValue("referralCode"));
        params.put("registrationId", "" + sharedPreferences.getString(Constant.K_REG_ID, ""));
        params.put("event", "" + eventtype);
        storeDeviceIdAsTimeStamp(sharedPreferences, params);
        Log.d(TAG, "campaign details : " + params);
        if (!TextUtils.isEmpty(deepLink))
            AppVolley.getRequestQueue().add(createMarketingCampaign(context, params));
    }


    private static void storeDeviceIdAsTimeStamp(SharedPreferences sharedPreferences, Map<String, String> params) {
        if (TextUtils.isEmpty(sharedPreferences.getString(Constant.SP_KEY_TIME_STAMP, null))) {
            sharedPreferences.edit().putString(Constant.SP_KEY_TIME_STAMP, String.valueOf(System.currentTimeMillis())).commit();
        }
        params.put("deviceId", sharedPreferences.getString(Constant.SP_KEY_TIME_STAMP, "0"));
    }


    public static StringRequest createMarketingCampaign(final Context context, final Map<String, String> params) {
        Log.d(TAG, "createMarketingCampaign");

        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.marketingCampaign;
        Log.d(TAG, "createMarketingCampaign - " + szServerUrl);

        final StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Marketing Campaign response : " + response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.d(TAG, "Json response : " + volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

        };
        return mRequest;
    }

    public static void callLoginScreen(Context applicationContext) {
        Intent mainIntent = new Intent(applicationContext, RegistrationActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(mainIntent);
    }

    public static void startMapIntentActivity(Context context, Uri uri) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);

    }

    public static void showCustomDurationToast(Activity mActivity, String message) {
        final Toast mToastToShow = Toast.makeText(mActivity, message, Toast.LENGTH_LONG);
        CountDownTimer toastCountDown = new CountDownTimer(5000, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };
        mToastToShow.show();
        toastCountDown.start();
    }

    public static Dialog showProgressDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog_layout);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        return dialog;
    }

    public static void launchSettingPermissionScreen(final Activity activity) {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }


    /*public static void buildNotification(Context mContext,String title,String messageBody,int notificationId,Intent intent)
    {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.icon_notification);
        mBuilder.setPriority(Notification.PRIORITY_MIN);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        //mBuilder.setVisibility(Notification.VISIBILITY_SECRET);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(messageBody);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       *//* int smallIconId = mContext.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
        if (smallIconId != 0) {
            mBuilder.build().contentView.setViewVisibility(smallIconId, View.INVISIBLE);
        }*//*
        mBuilder.setSound(uri);
        if(intent!=null)
        {
            mBuilder.setContentIntent(buildPendingIntent(mContext,intent));
        }
        mNotificationManager.notify(notificationId, mBuilder.build());
        return mBuilder.build();
    }*/
    private static PendingIntent buildPendingIntent(Context mContext, Intent intent) {
        Log.d(TAG, "buildPendingIntent");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        return resultPendingIntent;
    }

    public static void removeStickyNotification(Context mContext, int notificationId) {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    public static void showErrorCodeDialog(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_server_busy_dialog, null);
        builder.setView(view);
        TextView mButton = (Button) view.findViewById(R.id.bt_done);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.show();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "dismiss");
                Log.d(TAG, "mAlertDialog showing=" + mAlertDialog.isShowing());
                mAlertDialog.dismiss();
            }
        });
    }

    public static HttpGet CustomHttpGet(String url, Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        HttpGet customHttpGet = new HttpGet(url);
        customHttpGet.addHeader("Manufacturer", android.os.Build.MANUFACTURER);
        customHttpGet.addHeader("Model", android.os.Build.MODEL);

        customHttpGet.addHeader("mydevice", "android");
        customHttpGet.addHeader("app", "nutrition");
        customHttpGet.addHeader("lastSyncId", "0");
        customHttpGet.addHeader("OS-OSversion", "Android-"
                + android.os.Build.VERSION.RELEASE);
        customHttpGet.addHeader("IMEI", telephonyManager.getDeviceId());
        customHttpGet.addHeader("Carrier",
                telephonyManager.getSimOperatorName());
        return customHttpGet;

    }

    public static void showErrorDialog(Context context, VolleyError error) {
        if (error instanceof TimeoutError || error instanceof ParseError || error instanceof ServerError) {
            AppUtil.showToast(context, context.getString(R.string.connection_error));
        } else if (error instanceof AuthFailureError) {
            AppUtil.showToast(context, context.getString(R.string.auth_error));
        } else if (error instanceof NetworkError || error instanceof NoConnectionError) {
            if (!AppUtil.isOnline(context)) {
                AppUtil.showToast(context, context.getString(R.string.network_error));
            } else {
                AppUtil.showToast(context, context.getString(R.string.connection_error));
            }
        } else if (error.getMessage() != null) {
            if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found")) {
                AppUtil.showToast(context, context.getString(R.string.auth_error));
            }
        }
    }

    public static int getEmergencyPreparednessStatus(Context mContext) throws Exception {
        int value = 0;
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(mContext);
        long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(mContext);
        UserInfoVO mInfoVO = mDatabaseHandler.getUserInfo(currentSelectedProfile);
        ArrayList<InsuranceVO> mInsuranceVOs = mDatabaseHandler.getAllInsurances(currentSelectedProfile);
        HealthRecordVO emergencyHealthRecord = mDatabaseHandler.getUserHealthRecord(Constant.RECORD_TYPE_EHR, currentSelectedProfile);
        ArrayList<EmergencyContactsVO> ecList = mDatabaseHandler.getEmergencyContacts(currentSelectedProfile, Constant.EMERGENCY_CONTACT);
        ArrayList<EmergencyContactsVO> careTeamList = mDatabaseHandler.getEmergencyContacts(currentSelectedProfile, Constant.CARE_GIVER);
        if (emergencyHealthRecord.getHealthRecord() != null && !emergencyHealthRecord.getHealthRecord().equals("") && emergencyHealthRecord.getHealthRecord().size() != 0) {
            Log.d(TAG, "EMHR EXISTS");
            value = value + 20;
        }
        if (!TextUtils.isEmpty(mInfoVO.getBloodGroup())) {
            Log.d(TAG, "BLOOD GROUP EXISTS");
            value = value + 20;
        }
        if (mInsuranceVOs.size() > 0) {
            Log.d(TAG, "INSURANCE EXISTS");
            value = value + 20;
        }
        if (mInfoVO.getPreferredOrgBranchId() != 0) {
            Log.d(TAG, "PREFERRED AP EXISTS");
            value = value + 20;
        }
        if (ecList.size() > 0 || careTeamList.size() > 0) {
            value = value + 20;
        }
        Log.d(TAG, "PREPAREDNESS_PROGRESS_VALUE=" + value);
        return value;
    }

    public static String getFormattedAmount(String insPolicyCoverage) {
        Log.d("insPolicyCoverage", insPolicyCoverage);
        Integer mInteger = new Integer(insPolicyCoverage);
        DecimalFormat myFormatter = new DecimalFormat("#,##,###");
        return myFormatter.format(mInteger);
    }

    public static String getE164FormatPhoneNumber(String phoneNo) {
        Log.d(TAG, "getE164FormatPhoneNumber for: " + phoneNo);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String formattedPhoneNo = null;
            Phonenumber.PhoneNumber indiaNumberProto = phoneUtil.parse(phoneNo, "IN");
            boolean isValid = phoneUtil.isValidNumber(indiaNumberProto); // returns true
            if (isValid) {
                formattedPhoneNo = phoneUtil.format(indiaNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                Log.d(TAG, "getE164FormatPhoneNumber formattedPhoneNo: " + formattedPhoneNo);
            }
            return formattedPhoneNo;
        } catch (NumberParseException e) {
            Log.d("getE164FormatPhoneNumber NumberParseException was thrown: ", e.toString());
        }
        return "";
    }

    public static void sendThisEventToUpshot(String event, int eventId) {
        HashMap<String, Object> upshotData = new HashMap<>();
        upshotData.put(event, true);
        UpshotEvents.createCustomEvent(upshotData, eventId);
        Log.d(TAG, "upshot data=" + upshotData.entrySet());
    }


    public static boolean isCurrentTimeBetweenGivenTimes(String fromTime, String toTime) {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Calendar currentTimeCal = Calendar.getInstance();
            Calendar fromTimeCal = Calendar.getInstance();
            Calendar endTimeCal = Calendar.getInstance();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("hh:mm a");

            Date s1 = mSimpleDateFormat.parse(fromTime);
            fromTimeCal.set(Calendar.HOUR_OF_DAY, s1.getHours());
            fromTimeCal.set(Calendar.MINUTE, s1.getMinutes());

            Date e1 = mSimpleDateFormat.parse(toTime);
            endTimeCal.set(Calendar.HOUR_OF_DAY, e1.getHours());
            endTimeCal.set(Calendar.MINUTE, e1.getMinutes());

            Log.d("current_time=", s.format(currentTimeCal.getTime()));
            Log.d("startTime=", s.format(fromTimeCal.getTime()));
            Log.d("endtime=", s.format(endTimeCal.getTime()));

            if (currentTimeCal.after(fromTimeCal) && currentTimeCal.before(endTimeCal)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {

        }
        return false;
    }
}
