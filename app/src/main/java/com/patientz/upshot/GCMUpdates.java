package com.patientz.upshot;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.brandkinesis.BrandKinesis;
import com.brandkinesis.utils.BKUtilLogger;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.patientz.utils.Log;

public class GCMUpdates extends BroadcastReceiver
{
	private final String PUSH_KEY = "bk";
	private final String ALERT_KEY = "Alert";
	private final String ACTION_KEY = "openStore";
	private final String APPDATA = "appData";
	private final String TITLE = "title";
	private String pushKeyId;
	String bk = "";
	private NotificationManager mNotificationManager = null;
//	private Context mContext;
	private Bundle bundle;


	@Override
	public void onReceive(Context context, Intent intent)

	{
		BKUtilLogger.showErrorLog(BKUtilLogger.BK_DEBUG, "GCMUpdates onReceive " + intent.getExtras());
//		this.mContext = context;
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String messageType = gcm.getMessageType(intent);
		 bundle = intent.getExtras();

		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){}
		else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){}
		else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
		{
			Log.e("push data onReceive", bundle.toString());
			if(bundle.containsKey(ALERT_KEY) && bundle.containsKey("title")){
				String alertValue = bundle.getString(ALERT_KEY);
				bk = bundle.getString("bk");
				String title = bundle.getString(TITLE);

					sendNotification(context, alertValue,title);




			}

		}
	}


	private void sendNotification(final Context context, String msg,String title) {
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
					}
					else {
						BKUtilLogger.showErrorLog(BKUtilLogger.BK_DEBUG,"push notify auth status"+status);
					}
				}
			});
		}


	}

	private void buildBkNotification(final Context context,Bundle bundle){
		BrandKinesis bkInstance1 = BrandKinesis.getBKInstance();
		if(bkInstance1 !=null){
			bkInstance1.buildEnhancedPushNotification(context, bundle,true);
		}
		else{
			BKUtilLogger.showErrorLog(BKUtilLogger.BK_DEBUG,"push notify auth status buildBkNotification");
		}
	}



}
