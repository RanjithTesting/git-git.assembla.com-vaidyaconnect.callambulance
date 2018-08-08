package com.patientz.upshot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.brandkinesis.BrandKinesis;
import com.brandkinesis.pushnotifications.BKPushListener;
import com.brandkinesis.utils.BKUtilLogger;
import com.patientz.activity.MainActivity;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 */
public class BKPushAction extends BroadcastReceiver {
    private static final String TAG ="BKPushAction" ;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e("Bundle BKPushAction", "" + intent.getExtras());

        String action = "";
        String appData = "";
        String deepLink = "";
        String bk = "";
        final Bundle bundle = intent.getExtras();
        if (intent.getExtras() != null)
        {
            action = intent.getStringExtra("actionData");
            appData = intent.getStringExtra("appData");
            bk = intent.getStringExtra("bk");
        }
        Log.d("BKPushAction","app data : "+appData);
        Log.d("BKPushAction","action : "+action);

        JSONObject object = null;
        String type = "";
        String url = "";
        String custEventParam = "";



        try
        {
            if(appData != null && !(appData.isEmpty()))
            {
                object = new JSONObject(appData);
                if(object.has("deepLink"))
                {
                    deepLink = object.optString("deepLink");
                } else {
                    deepLink = object.optString("deepLinkURL");
                }
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(deepLink);
                sanitizer.parseUrl(deepLink);
                type = sanitizer.getValue("page");
                url = sanitizer.getValue("url");
            }
            if(action != null && !(action.isEmpty()))
            {
                UrlQuerySanitizer sanitizer1 = new UrlQuerySanitizer(action);
                sanitizer1.parseUrl(action);
                type = sanitizer1.getValue("page");
                url = sanitizer1.getValue("url");
                custEventParam = sanitizer1.getValue("custEventParam");
            }
            if(type == null)
            {
                BKUtilLogger.showErrorLog(BKUtilLogger.BK_DEBUG,"type is null");
                type = "";
            }
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.CUSTOM_EVENT_NOTIFICATION,custEventParam);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CUSTOM_EVENT_NOTIFICATION);
            Log.d(TAG,"upshot data="+upshotData.entrySet());
            Log.d("BKPushAction","type push deep link : "+type);
            if(Tag.getActivityFromTag(type)!=null){
                Intent intent1 = new Intent(context, Tag.getActivityFromTag(type));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent1);
            }else if(TextUtils.equals("web",type)
                    &&!TextUtils.isEmpty(url)
                    && URLUtil.isValidUrl(url)){
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent2);
            }
            else {
                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent1);
            }
            final BrandKinesis bkInstance = BrandKinesis.getBKInstance();

            if(bkInstance!=null)
            {
                bkInstance.handlePushNotification(context, bundle, new BKPushListener() {
                    @Override
                    public void onPushReceived(boolean status) {
                    }
                });
            } else {
                new AuthenticateUpshot(context, new UpshotAuthCallback() {
                    @Override
                    public void onBKAuthneticationDone(boolean status) {
                        if(status)
                        {
                            BrandKinesis bkInstance1 = BrandKinesis.getBKInstance();
                            if(bkInstance1 != null)
                            {
                                bkInstance1.handlePushNotification(context, bundle, new BKPushListener() {
                                    @Override
                                    public void onPushReceived(boolean status) {

                                    }
                                });
                            }
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}





