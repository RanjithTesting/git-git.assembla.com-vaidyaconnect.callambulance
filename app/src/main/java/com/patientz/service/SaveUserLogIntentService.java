package com.patientz.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.patientz.VO.UserVO;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SaveUserLogIntentService extends IntentService {
    private static final String TAG = "SaveUserLogIntentService";
    private Intent intent;

    public SaveUserLogIntentService() {
        super("SaveUserLogIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        callSaveUserLogWebservice();
    }

    public void callSaveUserLogWebservice() {
        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
        mRequestQueue.add(getSaveUserLogIntentServiceStringRequest());
    }

    private StringRequest getSaveUserLogIntentServiceStringRequest() {
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.SAVE_USER_LOG;
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    if (response.equalsIgnoreCase("Successfully saved UserLog")) {
                        Log.d(TAG, "Successfully saved UserLog");
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return getParamss();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    private HashMap<String, String> getParamss() {
        HashMap<String, String> params = new HashMap<String, String>();
        UserVO user = AppUtil.getLoggedUser(getApplicationContext());
        params.put("userId", String.valueOf(user.getUserId()));
        params.put("appName", getString(R.string.app_name));
        params.put("loggedInLatitude", intent.getStringExtra("loggedInLatitude"));
        params.put("loggedInLongitude", intent.getStringExtra("loggedInLongitude"));
        params.put("city", intent.getStringExtra("city"));
        params.put("district", intent.getStringExtra("district"));
        params.put("state", intent.getStringExtra(Constant.STATE));
        params.put("pinCode", intent.getStringExtra(Constant.POSTAL_CODE));
        params.put("country", intent.getStringExtra(Constant.COUNTRY));
        params.put("locality", intent.getStringExtra(Constant.LOCALITY));
        params.put("subLocality", intent.getStringExtra(Constant.SUB_LOCALITY));

        return (HashMap<String, String>) checkParams(params);
    }

    private Map<String, String> checkParams(Map<String, String> params) {
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            if (pairs.getValue() == null) {
                params.put(pairs.getKey(), "");
            }
        }
        Log.d(TAG, "PARAMS_SAVE_USER_LOG: " + params);
        return params;
    }
}
