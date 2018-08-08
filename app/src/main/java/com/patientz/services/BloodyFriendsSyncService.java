package com.patientz.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.patientz.VO.BloodyFriendVO;
import com.patientz.activity.BloodyFriendsActivity;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BloodyFriendsSyncService extends Service {
    DatabaseHandler dh;
    private static final String TAG = "BloodyFriendsSyncService";
    RequestQueue mRequestQueue;
    boolean updatestatus = true;
    private final IBinder mBinder = new LocalBinder();
    private int counter;
    public static final int NOTIFICATION_ID_EVENT_TYPE_PROMOTION = 4;

    public BloodyFriendsSyncService() {

    }

    @Override
    public void onCreate() {
        //mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        mRequestQueue = AppVolley.getRequestQueue();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BloodyFriendsSyncService getService() {
            return BloodyFriendsSyncService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dh = DatabaseHandler.dbInit(getApplicationContext());
        new Thread() {
            public void run() {
                startUpdateRequests();
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }


    private void startUpdateRequests() {

        Log.e(TAG, "Hi... BloodyFriendsSyncService");
        Log.e(TAG, "updatestatus=" + updatestatus);
        Log.e(TAG, "updatestatus=" + updatestatus);
        Answers.getInstance().logCustom(new CustomEvent("BFSync Initiated"));

        for (; ; ) {
            if (updatestatus == true) {
                updatestatus = false;
                ArrayList<BloodyFriendVO> list = dh.getPhoneContactsInfo();
                Log.e(TAG, "contacts size=" + list.size());

                if (list.size() > 0) {
                    Log.e(TAG + "--->", "running.....");

                    if (AppUtil.isOnline(getApplicationContext())) {
                        mRequestQueue.add(syncContactsFromPhone(list.get(0)));
                    } else {
                    }

                } else {
                    Log.e(TAG + "--->", "break");
                    stopSelf();
                    sendPromotionalNotification(getString(R.string.label_contacts_sync_completed));
                    Log.e(TAG + "--->", "stopped");
                    Answers.getInstance().logCustom(new CustomEvent("BFSync Completed"));
                    break;
                }
            }
        }
    }


    private StringRequest syncContactsFromPhone(final BloodyFriendVO bloodyFriendVO) {

        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.addContact;
        Log.e("url----> ", szServerUrl);
        counter++;
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                counter--;
                Log.d(TAG, "response : " + response);

                try {
                    JSONObject jsonRootObject = new JSONObject(response);
                    String contact = jsonRootObject.getString("contact");
                    Log.d(TAG + "-contact-", contact);

                    if (contact.equals(bloodyFriendVO.getContact())) {
                        Log.d("get_id--->", "" + bloodyFriendVO.get_id());
                        int updatevalue = dh.updatePhoneContactsStatus(bloodyFriendVO.get_id());
                        Log.d(TAG, "updatevalue : " + updatevalue);
                        if (updatevalue == 1) {
                            updatestatus = true;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                counter--;
                Log.d("json--->", "--------" + counter);
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.put("lookUpId", bloodyFriendVO.getLookUpId());
                    jsonobject.put("contactType", bloodyFriendVO.getContactType());
                    jsonobject.put("contact", bloodyFriendVO.getContact());
                    jsonobject.put("contactName", bloodyFriendVO.getContactName());
                    jsonobject.put("contactId", bloodyFriendVO.getContactId());
                    jsonobject.put("userInvited", bloodyFriendVO.isUserInvited());

                    Locale current_language = getResources().getConfiguration().locale;
                    Log.d("current locale--->", current_language.getLanguage());

                    SharedPreferences sharedPreferences = getSharedPreferences(
                            Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
                    String deepLink = sharedPreferences.getString(Constant.DYNAMIC_LINK_2, "");

                    if (current_language.getLanguage().equals("en")) {
                        jsonobject.put("deepLink", deepLink);
                    } else {
                        String send_message1 = getString(R.string.bloodyfriends_invite_message).replace("deepLink", deepLink);
                        String send_message2 = send_message1.replace("friend1", "@friend1");
                        String send_message = send_message2.replace("friend2", "@friend2");
                        jsonobject.put("message", send_message);
                    }

                    //String send_message = "@friend1 and @friend2 are now part of Bloody Friends Network. Do you know who among your personal friends can donate blood to you if needed! You can now build your list of Bloody Friends with one click on /\"Sync/\" button in CallAmbulance App. Click @deepLink to download the app now and Be Safe!";
                    Log.d("json--->", jsonobject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put("contact", jsonobject.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    private void sendPromotionalNotification(String msg) {
        Intent intent;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon(R.drawable.icon_notification);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle("Bloody Friends");
        mBuilder.setContentText(msg);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(bm);
        mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.smallbells));
        //mBuilder.setVibrate(new long[]{100, 200, 300, 400, 500});

        intent = new Intent(this, BloodyFriendsActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        mBuilder.setContentIntent(buildPendingIntent(intent));
        Log.d(TAG, "GCM event  : A2");

        Notification notification = mBuilder.build();
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(NOTIFICATION_ID_EVENT_TYPE_PROMOTION, notification);
    }

    private PendingIntent buildPendingIntent(Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

    }
}
