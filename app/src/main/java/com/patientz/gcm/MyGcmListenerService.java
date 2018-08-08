package com.patientz.gcm;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brandkinesis.BrandKinesis;
import com.brandkinesis.utils.BKUtilLogger;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.GcmMessageVO;
import com.patientz.activity.ActivityCurrentEmergencies;
import com.patientz.activity.MainActivity;
import com.patientz.activity.MyUploadReportsActivity;
import com.patientz.activity.ProfileActivity;
import com.patientz.activity.R;
import com.patientz.service.NotificationEventsListenerService;
import com.patientz.upshot.AuthenticateUpshot;
import com.patientz.upshot.UpshotAuthCallback;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID_EVENT_TYPE_PROMOTION = 3;
    private final String ALERT_KEY = "Alert";
    private final String TITLE = "title";
    private static RequestQueue mRequestQueue;
    private String CHANNEL_ID = "callambulance";
    private CharSequence name = "callambulance";
    private static NotificationCompat.Builder mBuilder;
    private int importance = NotificationManager.IMPORTANCE_HIGH;

    @Override
    public void onMessageReceived(String from, Bundle extras) {
        super.onMessageReceived(from, extras);
        mRequestQueue = AppVolley.getRequestQueue();
        try {
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            Log.d(TAG, "Received: " + extras.toString());
            if (extras.containsKey(ALERT_KEY) && extras.containsKey("title")) {
                String alertValue = extras.getString(ALERT_KEY);
                String bk = extras.getString("bk");
                String title = extras.getString(TITLE);
                sendBKNotification(getApplicationContext(), alertValue, title, extras);
            } else {
                String msg = extras.getString("GCMmessage");
                if (!TextUtils.isEmpty(msg)) {
                    Gson gson = AppUtil.getGson();
                    Type objectType = new TypeToken<GcmMessageVO>() {
                    }.getType();
                    final GcmMessageVO gcmMessageVO = gson.fromJson(msg, objectType);
                    if (gcmMessageVO != null) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            public void run() {
                                if (gcmMessageVO.getGcId() != 0)
                                    mRequestQueue.add(saveGCMCommunicationLogRequest(getApplicationContext(), gcmMessageVO.getGcId(), Constant.EVENT_GCM_RECEIVED));
                            }
                        }, 3000);

                        Log.d(TAG, "GCM event type : " + gcmMessageVO.getEventType());
                        if (TextUtils.equals(gcmMessageVO.getEventType(), getString(R.string.nt_event_type_promotion))) {
                            Log.d(TAG, "GCM event  : A");
                            sendPromotionalNotification(gcmMessageVO);
                        } else if (TextUtils.equals(gcmMessageVO.getEventType(), getString(R.string.nt_event_type_emergency))) {
                            sendPromotionalNotification(gcmMessageVO);
                            Log.d(TAG, "GCM event  : B");
                        } else if (TextUtils.equals(gcmMessageVO.getEventType(), getString(R.string.nt_event_report))) {
                            sendPromotionalNotification(gcmMessageVO);
                            Log.d(TAG, "GCM event  : C");
                        } else if (TextUtils.equals(gcmMessageVO.getEventType(), Constant.GCM_EVENT_TYPE_AMBULANCE_REQUEST_STATUS)) {
                            sendPromotionalNotification(gcmMessageVO);
                            Log.d(TAG, "GCM event  : D");
                        } else {
                            sendPromotionalNotification(gcmMessageVO);
                        }
                    }
                }
            }
        } catch (
                JsonParseException e
                )

        {
            Log.e(TAG, "Exception Parsing Response VO : "
                    + e.getMessage());
        }

        Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
    }

    private static StringRequest saveGCMCommunicationLogRequest(final Context applicationContext, long gcId, String eventType) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constant.GCM_COMMUNICATION_ID, String.valueOf(gcId));
        params.put(Constant.EVENT, eventType);
        Log.d("params.toString() : ", params.toString());
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

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendPromotionalNotification(GcmMessageVO gcmMessageVO) {

        if (!TextUtils.isEmpty(gcmMessageVO.getImageUrl()) && AppUtil.isOnline(getApplicationContext())) {
            downloadImageAndShowNotification(gcmMessageVO);
        } else {
            sendPromotionalNotification(gcmMessageVO, null);
        }
    }

    private void downloadImageAndShowNotification(final GcmMessageVO gcmMessageVO) {
        String url = gcmMessageVO.getImageUrl();
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        ImageRequest ir = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        sendPromotionalNotification(gcmMessageVO, bitmap);
                    }
                }, 512, 256, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "GCM event  : failed to download image");
                /*If image download fails don't show the notification*/
                sendPromotionalNotification(gcmMessageVO, null);
            }
        });
        rq.add(ir);
    }

    private void sendPromotionalNotification(GcmMessageVO gcmMessageVO, Bitmap bitmap) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent intent;
        mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.icon_notification);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle(gcmMessageVO.getTitle());
        mBuilder.setContentText(gcmMessageVO.getTextShort());
        mBuilder.setChannelId(CHANNEL_ID);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(bm);

        if (bitmap != null) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigLargeIcon(bm);
            bigPictureStyle.bigPicture(bitmap);
            bigPictureStyle.setBigContentTitle(gcmMessageVO.getTitle());
            bigPictureStyle.setSummaryText(gcmMessageVO.getTextLong());
            mBuilder.setStyle(bigPictureStyle);
        } else if (!TextUtils.isEmpty(gcmMessageVO.getTextLong())) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(gcmMessageVO.getTitle());
            bigTextStyle.setSummaryText(gcmMessageVO.getTextShort());
            bigTextStyle.bigText(gcmMessageVO.getTextLong());
            bigTextStyle.setBuilder(mBuilder);
            mBuilder.setStyle(bigTextStyle);
        }
        if (!TextUtils.isEmpty(gcmMessageVO.getOnClickUrl()) && !TextUtils.equals(gcmMessageVO.getEventType(), Constant.GCM_EVENT_TYPE_AMBULANCE_REQUEST_STATUS) && URLUtil.isValidUrl(gcmMessageVO.getOnClickUrl()) && !gcmMessageVO.getTextLong().contains("Revoked") && !gcmMessageVO.getTextLong().contains("Deactivate")) {
            Log.d(TAG, "event3");
            mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ambulance_siren_two), AudioManager.STREAM_ALARM);
        }
        Intent mIntent1 = new Intent(getApplicationContext(), NotificationEventsListenerService.class);
        mIntent1.putExtra("gcmMessageVO", gcmMessageVO);
        mBuilder.setContentIntent(PendingIntent.getService(getApplicationContext(), 0, mIntent1, PendingIntent.FLAG_UPDATE_CURRENT));
        mNotificationManager.notify(NOTIFICATION_ID_EVENT_TYPE_PROMOTION, mBuilder.build());
    }

    private void sendBKNotification(final Context context, String msg, String title, final Bundle bundle) {
        Log.e("push sendNotification", bundle.toString());
        final BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if (bkInstance != null) {
            buildBkNotification(context, bundle);
        } else {
            new AuthenticateUpshot(context, new UpshotAuthCallback() {
                @Override
                public void onBKAuthneticationDone(boolean status) {
                    if (status) {
                        buildBkNotification(context, bundle);
                    } else {
                        BKUtilLogger.showErrorLog(BKUtilLogger.BK_DEBUG, "push notify auth status" + status);
                    }
                }
            });
        }
    }

    private void buildBkNotification(final Context context, Bundle bundle) {
        BrandKinesis bkInstance1 = BrandKinesis.getBKInstance();
        bundle.putInt(BrandKinesis.BK_LOLLIPOP_NOTIFICATION_ICON, R.drawable.icon_notification);
        if (bkInstance1 != null) {
            boolean allowPushNotification = true; // true:allows push notification even in foreground
            bkInstance1.buildEnhancedPushNotification(context, bundle, allowPushNotification);
        } else {
            BKUtilLogger.showErrorLog(BKUtilLogger.BK_DEBUG, "push notify auth status buildBkNotification");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class OnClickPendingIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "OnClickPendingIntentReceiver>>called");

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            GcmMessageVO gcmMessageVO = (GcmMessageVO) intent.getSerializableExtra("gcmMessageVO");
            if (gcmMessageVO.getGcId() != 0) {
                mRequestQueue = AppVolley.getRequestQueue();
                mRequestQueue.add(saveGCMCommunicationLogRequest(context, gcmMessageVO.getGcId(), Constant.EVENT_GCM_CLICKED));
            }
            if (gcmMessageVO.getEventType().equalsIgnoreCase(Constant.GCM_EVENT_TYPE_AMBULANCE_REQUEST_STATUS)) {
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                stackBuilder.addParentStack(MainActivity.class);
            } else if (gcmMessageVO.getTextLong().contains("Revoked")) {
                Log.d(TAG, "event2");
                intent = new Intent(context, MainActivity.class);
                stackBuilder.addParentStack(MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else if (!TextUtils.isEmpty(gcmMessageVO.getOnClickUrl()) && URLUtil.isValidUrl(gcmMessageVO.getOnClickUrl()) && !gcmMessageVO.getTextLong().contains("Revoked") && !gcmMessageVO.getTextLong().contains("Deactivate")) {
                Log.d(TAG, "event3");
                mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ambulance_siren_two), AudioManager.STREAM_ALARM);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gcmMessageVO.getOnClickUrl()));
                stackBuilder.addParentStack(MainActivity.class);
            } else if (gcmMessageVO.getEventType().equalsIgnoreCase(Constant.GCM_EVENT_TYPE_INSURANCE_ISSUED)) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
                Log.d(TAG, "EVENT_TYPEE=" + gcmMessageVO.getEventType());
                intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("goTo", Constant.INSURANCE_VIEW_SCREEN);
                intent.putExtra("refresh", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                stackBuilder.addParentStack(ProfileActivity.class);
                //startActivity(intent);
            } else if (gcmMessageVO.getEventType().contains(context.getString(R.string.nt_event_type_emergency))) {
                intent = new Intent(context, ActivityCurrentEmergencies.class);
                stackBuilder.addParentStack(ActivityCurrentEmergencies.class);
            } else if (gcmMessageVO.getEventType().contains(context.getString(R.string.nt_event_report))) {
                intent = new Intent(context, MyUploadReportsActivity.class);
                stackBuilder.addParentStack(MyUploadReportsActivity.class);
            } else {
                Log.d(TAG, "event4");
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                stackBuilder.addParentStack(MainActivity.class);
            }
            TaskStackBuilder.create(context)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(intent)
                    // Navigate up to the closest parent
                    .startActivities();
        }
    }
}
