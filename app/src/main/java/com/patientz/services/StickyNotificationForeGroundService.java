package com.patientz.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.activity.ActivityEHRUpdate;
import com.patientz.activity.ActivityEditInsurance;
import com.patientz.activity.ActivityEditProfile;
import com.patientz.activity.ContactsActivity;
import com.patientz.activity.MainActivity;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class StickyNotificationForeGroundService extends Service {
    private static final String TAG = "StickyNotificationForeGroundService";
    private static final String CHANNEL_ID ="sticky_notification_channel" ;
    private CharSequence name = "callambulance";
    private RemoteViews remoteViews;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int value = 0;
            if (intent.getAction().equals(Constant.ACTION.EMERGENCY_PREPAREDNESS)) {
                remoteViews = new RemoteViews(getPackageName(), R.layout.emergency_preparedness_custom_notification);
                Log.d(TAG, "EMERGENCY_PREPAREDNESS");
                DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                HashMap<String, Object> upshotData = new HashMap<>();
                long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                try {
                    UserInfoVO mInfoVO = mDatabaseHandler.getUserInfo(currentSelectedProfile);
                    ArrayList<InsuranceVO> mInsuranceVOs = mDatabaseHandler.getAllInsurances(currentSelectedProfile);
                    HealthRecordVO emergencyHealthRecord = mDatabaseHandler.getUserHealthRecord(Constant.RECORD_TYPE_EHR, currentSelectedProfile);
                    ArrayList<EmergencyContactsVO> ecList = mDatabaseHandler.getEmergencyContacts(currentSelectedProfile, Constant.EMERGENCY_CONTACT);
                    ArrayList<EmergencyContactsVO> careTeamList = mDatabaseHandler.getEmergencyContacts(currentSelectedProfile, Constant.CARE_GIVER);
                    Intent resultIntent = null;
                    if (ecList.size() == 0 && careTeamList.size() == 0) {
                        resultIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                        resultIntent.putExtra("source", getApplicationContext().getString(R.string.lable_emergency_prepardness));
                    }else if
                    (TextUtils.isEmpty(mInfoVO.getBloodGroup())) {
                        resultIntent = new Intent(getApplicationContext(), ActivityEditProfile.class);
                        resultIntent.putExtra("key", "edit");
                    }else
                    if (emergencyHealthRecord.getHealthRecord() == null && emergencyHealthRecord.getHealthRecord().equals("") && emergencyHealthRecord.getHealthRecord().size() == 0) {
                        resultIntent = new Intent(getApplicationContext(), ActivityEHRUpdate.class);
                        resultIntent.putExtra("key", "edit");
                    }else
                    if (mInfoVO.getPreferredOrgBranchId() == 0) {
                        resultIntent = new Intent(getApplicationContext(),ActivityEHRUpdate.class);
                        resultIntent.putExtra("key", "edit");
                        resultIntent.putExtra(Constant.FOCUS_PREFERRED_HOSPITAL, true);
                    }else
                    if (mInsuranceVOs.size() == 0) {
                        resultIntent= new Intent(getApplicationContext(), ActivityEditInsurance.class);
                    }


                    if (ecList.size() > 0 || careTeamList.size() > 0) {
                        value = value + 20;
                        upshotData.put("emergencyContact_exists", true);
                        remoteViews.setImageViewResource(R.id.icon1, R.drawable.em_contact_notification);
                    }
                    if (!TextUtils.isEmpty(mInfoVO.getBloodGroup())) {
                        Log.d(TAG, "BLOOD GROUP EXISTS");
                        value = value + 20;
                        upshotData.put("bloodGroup_exists", true);
                        Log.d(TAG, "BLOOD GROUP INCOMPLETE");
                        remoteViews.setImageViewResource(R.id.icon2, R.drawable.bloodgrp_notification);
                    }
                    if (emergencyHealthRecord.getHealthRecord() != null && !emergencyHealthRecord.getHealthRecord().equals("") && emergencyHealthRecord.getHealthRecord().size() != 0) {
                        Log.d(TAG, "EMHR EXISTS");
                        value = value + 20;
                        upshotData.put("healthRecord_exists", true);
                        Log.d(TAG, "EMHR INCOMPLETE");
                        remoteViews.setImageViewResource(R.id.icon3, R.drawable.emhr_notification);
                    }
                    if (mInfoVO.getPreferredOrgBranchId() != 0) {
                        Log.d(TAG, "PREFERRED AP EXISTS");
                        value = value + 20;
                        upshotData.put("preferredOrgBranch_exists", true);
                        Log.d(TAG, "PREFERRED AP INCOMPLETE");
                        remoteViews.setImageViewResource(R.id.icon4, R.drawable.em_provider_notification);
                    }
                    if (mInsuranceVOs.size() > 0) {
                        Log.d(TAG, "INSURANCE EXISTS");
                        value = value + 20;
                        upshotData.put("insurance_exists", true);
                        remoteViews.setImageViewResource(R.id.icon5, R.drawable.insurance_notification);
                    }
                    if(value==100)
                    {
                        Log.d(TAG, "REMOVING STICKY NOTIFICATION");
                        NotificationManager mNotificationManager = (NotificationManager)
                                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancel(Constant.NOTIFICATION_ID.STICKY_NOTIFICATION_FOREGROUND_SERVICE);
                        stopSelf();
                        stopForeground(true);
                    }else
                    {
                        createNotification(getApplicationContext(),resultIntent,value,ContactsActivity.class);
                    }
                    upshotData.put("patientId", currentSelectedProfile);
                    upshotData.put("emergencyPreparednessProgress", value);
                    Log.d(TAG, "PREPAREDNESS_PROGRESS_VALUE=" + value);
                    Log.d(TAG, "emergency_preparedness_upshot_values=" + upshotData.entrySet());
                    UpshotEvents.createCustomEvent(upshotData, 18);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
               /* Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                createNotification(getApplicationContext(),startIntent, 6,value, MainActivity.class);*/
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                createNotification(getApplicationContext(), "CallAmbulance", "Please complete registration process",startIntent,MainActivity.class);
            }
        }
        return START_STICKY;
    }
    public  void createNotification(Context mContext, String title, String messageBody, Intent intent, Class<?> cls)
    {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        @SuppressLint("WrongConstant")
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_MIN);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setShowBadge(false);
        mNotificationManager.createNotificationChannel(mChannel);
    }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.icon_notification);
        mBuilder.setPriority(Notification.PRIORITY_MIN);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(messageBody);
        Log.d(TAG, "INSURANCE INCOMPLETE");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(cls);
        if(cls.equals(ActivityEditInsurance.class))
        {
            Log.d(TAG,"setting notification non sticky");
            mBuilder.setAutoCancel(false);
            mBuilder.setOngoing(false);
            //mBuilder.setDeleteIntent();
        }
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        //mBuilder.setDeleteIntent();
        mNotificationManager.notify(Constant.NOTIFICATION_ID.STICKY_NOTIFICATION_FOREGROUND_SERVICE, mBuilder.build());
    }
    public void createNotification(Context mContext, Intent intent, int value, Class<?> cls) {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_MIN);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notification)
                .setOngoing(true)
                .setTicker("Ambulance")
                .setContent(remoteViews)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_MIN)
                .setOngoing(true);

        remoteViews.setTextViewText(R.id.tv_percentage_complete,+value+"%");


        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = remoteViews;
        }
        Log.d(TAG, "INSURANCE INCOMPLETE");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(cls);
        if (cls.equals(ActivityEditInsurance.class)) {
            Log.d(TAG, "setting notification non sticky");
            builder.setAutoCancel(false);
            builder.setOngoing(false);
        }
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.root_layout,resultPendingIntent);
        //mBuilder.setDeleteIntent();
        mNotificationManager.notify(Constant.NOTIFICATION_ID.STICKY_NOTIFICATION_FOREGROUND_SERVICE, notification);
    }


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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}
