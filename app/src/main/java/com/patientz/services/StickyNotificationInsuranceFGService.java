package com.patientz.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.patientz.VO.InsuranceVO;
import com.patientz.activity.ProfileActivity;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

public class StickyNotificationInsuranceFGService extends Service {
    private static final String TAG ="StickyNotificationForeGroundService" ;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (!intent.getAction().equals(Constant.ACTION.STOP)) {
                DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                InsuranceVO mInsuranceVO = mDatabaseHandler.getPurchasedInsurances(currentSelectedPatientId);
                if (mInsuranceVO != null) {
                    NotificationManager mNotificationManager = (NotificationManager)
                            getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setSmallIcon(R.drawable.icon_notification);
                    mBuilder.setPriority(Notification.PRIORITY_MIN);
                    mBuilder.setAutoCancel(false);
                    mBuilder.setOngoing(true);
                    mBuilder.setContentTitle("Group Emergency Accident Insurance Policy");
                    mBuilder.setContentText("Verify your Email,Mobile Number and Add Aadhar No. to complete the insurance purchase request");

                    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                    bigTextStyle.bigText("Verify your Email,Mobile Number and Add Aadhar No. to complete the insurance purchase request");
                    bigTextStyle.setBuilder(mBuilder);
                    mBuilder.setStyle(bigTextStyle);
                    Log.d(TAG, "INSURANCE INCOMPLETE");
                    Intent mIntent = new Intent(this, ProfileActivity.class);
                    mIntent.putExtra("goTo",Constant.INSURANCE_VIEW_SCREEN);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addParentStack(ProfileActivity.class);
                    stackBuilder.addNextIntent(mIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    Log.d(TAG, "INSURANCE INCOMPLETE");
                    mNotificationManager.notify(Constant.NOTIFICATION_ID.STICKY_NOTIFICATION_INSURANCE_FOREGROUND_SERVICE, mBuilder.build());
                } else {
                    NotificationManager mNotificationManager = (NotificationManager)
                            getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(Constant.NOTIFICATION_ID.STICKY_NOTIFICATION_INSURANCE_FOREGROUND_SERVICE);
                    stopSelf();
                    stopForeground(true);
                }
            }else
            {
                NotificationManager mNotificationManager = (NotificationManager)
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(Constant.NOTIFICATION_ID.STICKY_NOTIFICATION_INSURANCE_FOREGROUND_SERVICE);
                stopSelf();
                stopForeground(true);
            }
        }
        return START_STICKY;
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
