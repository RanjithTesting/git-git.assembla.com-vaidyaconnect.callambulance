package com.patientz.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.patientz.VO.GcmMessageVO;
import com.patientz.activity.ActivityCurrentEmergencies;
import com.patientz.activity.MainActivity;
import com.patientz.activity.MyUploadReportsActivity;
import com.patientz.activity.ProfileActivity;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.webservices.WebServiceUrls;

import java.util.HashMap;
import java.util.Map;

public class NotificationEventsListenerService extends JobIntentService {
    public static final int NOTIFICATION_ID_EVENT_TYPE_PROMOTION = 3;
    private static String TAG = "NotificationEventsListenerService";

    public NotificationEventsListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                stopSelf();
            }
        }, 5000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent != null) {
            com.patientz.utils.Log.d(TAG, "OnClickPendingIntentReceiver>>called");

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            GcmMessageVO gcmMessageVO = (GcmMessageVO) intent.getSerializableExtra("gcmMessageVO");
            if (gcmMessageVO.getGcId() != 0) {
                RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                mRequestQueue.add(saveGCMCommunicationLogRequest(getApplicationContext(), gcmMessageVO.getGcId(), Constant.EVENT_GCM_CLICKED));
            }
            if (gcmMessageVO.getEventType().equalsIgnoreCase(Constant.GCM_EVENT_TYPE_AMBULANCE_REQUEST_STATUS)) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                stackBuilder.addParentStack(MainActivity.class);
            } else if (gcmMessageVO.getTextLong().contains("Revoked")) {
                com.patientz.utils.Log.d(TAG, "event2");
                intent = new Intent(getApplicationContext(), MainActivity.class);
                stackBuilder.addParentStack(MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else if (!TextUtils.isEmpty(gcmMessageVO.getOnClickUrl()) && URLUtil.isValidUrl(gcmMessageVO.getOnClickUrl()) && !gcmMessageVO.getTextLong().contains("Revoked") && !gcmMessageVO.getTextLong().contains("Deactivate")) {
                com.patientz.utils.Log.d(TAG, "event3");
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gcmMessageVO.getOnClickUrl()));
                stackBuilder.addParentStack(MainActivity.class);
            } else if (gcmMessageVO.getEventType().equalsIgnoreCase(Constant.GCM_EVENT_TYPE_INSURANCE_ISSUED)) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
                com.patientz.utils.Log.d(TAG, "EVENT_TYPEE=" + gcmMessageVO.getEventType());
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("goTo", Constant.INSURANCE_VIEW_SCREEN);
                intent.putExtra("refresh", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                stackBuilder.addParentStack(ProfileActivity.class);
                //startActivity(intent);
            } else if (gcmMessageVO.getEventType().contains(getApplicationContext().getString(R.string.nt_event_type_emergency))) {
                intent = new Intent(getApplicationContext(), ActivityCurrentEmergencies.class);
                stackBuilder.addParentStack(ActivityCurrentEmergencies.class);
            } else if (gcmMessageVO.getEventType().contains(getApplicationContext().getString(R.string.nt_event_report))) {
                intent = new Intent(getApplicationContext(), MyUploadReportsActivity.class);
                stackBuilder.addParentStack(MyUploadReportsActivity.class);
            } else {
                com.patientz.utils.Log.d(TAG, "event4");
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                stackBuilder.addParentStack(MainActivity.class);
            }
            TaskStackBuilder.create(getApplicationContext())
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(intent)
                    // Navigate up to the closest parent
                    .startActivities();
        }
    }


    private static StringRequest saveGCMCommunicationLogRequest(final Context applicationContext, long gcId, String eventType) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constant.GCM_COMMUNICATION_ID, String.valueOf(gcId));
        params.put(Constant.EVENT, eventType);
        com.patientz.utils.Log.d("params.toString() : ", params.toString());
        final StringRequest mRequest = new StringRequest(Request.Method.POST,
                WebServiceUrls.serverUrl + WebServiceUrls.logGCMCommunication, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //android.util.Log.d(TAG,"response="+response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(applicationContext, super.getHeaders());
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
}


