package com.patientz.upshot;

import android.content.Context;
import android.os.Bundle;

import com.brandkinesis.BKProperties;
import com.brandkinesis.BrandKinesis;
import com.brandkinesis.callback.BKAuthCallback;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;


public class AuthenticateUpshot {
    private Context mContext;
    private UpshotAuthCallback mCallback;
    public static BrandKinesis brandKinesisInstance;

    public AuthenticateUpshot(Context context, UpshotAuthCallback callback) {
        mContext = context;
        mCallback = callback;
        authenticateBK();
    }

    public void authenticateBK() {
        Bundle data = new Bundle();
        data.putString(BKProperties.BK_APPLICATION_ID, WebServiceUrls.UPSHOT_APPLICATION_ID);
        data.putString(BKProperties.BK_APPLICATION_OWNER_ID, WebServiceUrls.UPSHOT_APPLICATION_OWNER_ID);
        data.putBoolean(BKProperties.BK_DEMO_RUN, false);
        data.putBoolean(BKProperties.BK_FETCH_LOCATION, false);
        data.putBoolean(BKProperties.BK_ENABLE_DEBUG_LOGS, true);

        BrandKinesis.initialiseBrandKinesis(mContext, data, new BKAuthCallback() {
            @Override
            public void onAuthenticationComplete(boolean status, BrandKinesis brandKinesis) {
                BrandKinesis brandKinesis1 = BrandKinesis.getBKInstance();
                Log.d("AuthenticateUpshot", "brandKinesis1 : " + brandKinesis1 + "\n status : " + status);
                brandKinesisInstance = brandKinesis;
                mCallback.onBKAuthneticationDone(status);
            }
        });
    }

    public static BrandKinesis getBKInstance() {
        return brandKinesisInstance;
    }

}

