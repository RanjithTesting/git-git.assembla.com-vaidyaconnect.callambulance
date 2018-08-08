package com.patientz.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.RecordSchemaAttributes;
import com.patientz.VO.ResponseVO;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetSchemaKeys {
    String
            url;
    String schemaKeysPreferenceFileName;
    String schemaAttPreferenceFileNameKey;
    String schemaAttPreferenceFileName;
    Context context;
    ResponseVO mResponseVO;
    Exception exc;
    HashMap<String, String> schemaKeys;
    ArrayList<RecordSchemaAttributes> schemaAttrList;
    private String TAG = "GetSchemaKeys";
    private RequestQueue mRequestQueue;

    public GetSchemaKeys(Context context,
                         String labelDictionaryFileName, String schemaAttrPrefFileName,
                         String schemaAttrPrefFileNameKey, String url) {
        schemaKeysPreferenceFileName = labelDictionaryFileName;
        schemaAttPreferenceFileName = schemaAttrPrefFileName;
        schemaAttPreferenceFileNameKey = schemaAttrPrefFileNameKey;
        Log.d(TAG, "schema keys file name" + schemaKeysPreferenceFileName);
        Log.d(TAG, "schema attr file name" + schemaAttPreferenceFileName);
        Log.d(TAG, "schema attr key file name" + schemaAttPreferenceFileNameKey);
        Log.d(TAG, "url" + url);

        this.url = url;
        this.context = context;
        mRequestQueue = AppVolley.getRequestQueue();
        mRequestQueue.add(createSchemaRequest());
    }


    private StringRequest createSchemaRequest() {
        String szServerUrl = WebServiceUrls.serverUrl + url;
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ResponseVO responseVO = new ResponseVO();
                Log.d(TAG, "createSchemaRequest " + response);
                if (response != null) {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<ResponseVO>() {
                    }.getType();
                    try {
                        responseVO = gson.fromJson(
                                response, objectType);
                    } catch (JsonParseException e) {
                        Log.e(TAG, "Exception Parsing Response VO : "
                                + e.getMessage());
                    }
                    mResponseVO = responseVO;
                    extractSchemaMap(mResponseVO);
                    onPostExecute(responseVO.getCode());
                } else {
                    /*AppUtil.showToast(getApplicationContext(),
                            getString(R.string.networkError));*/
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(context, super.getHeaders());
            }
        };
        return mRequest;
    }

    protected void onPostExecute(long response) {
        switch ((int) response) {
            case Constant.RESPONSE_SUCCESS:
                SchemaUtils.storeVersionInSharedPref(context,
                        mResponseVO.getVersion(), schemaKeysPreferenceFileName);
                storeSchemaKeysLocallyInSharedPreference();
                saveSchemaAttributesListLocallyInSharedPreference();
                break;
            default:
                break;
        }

    }

    private void extractSchemaMap(ResponseVO mResponseVO) {
        Log.d("~~~~~~~~~~~~~~~~~", "extractSchemaMap");
        if (mResponseVO != null) {
            Log.d("~~~~~~~~~~~~~~~~~", mResponseVO + "");
            Log.d("~~~~~~~~~~~~~~~~~", mResponseVO.getLabelDictionary() + "");
            if (mResponseVO.getLabelDictionary() != null) {
                Log.d("PVF", "IN extractSchemaMap");
                schemaKeys = mResponseVO.getLabelDictionary();
                Log.d("schemaKeys", schemaKeys + "");
                schemaAttrList = mResponseVO.getSchemaAttributeList();
                Log.d("?????????????<<<<", schemaAttrList + "");
            }

        } else {

        }
    }

    public void storeSchemaKeysLocallyInSharedPreference() {
        Log.d(TAG,
                "storeSchemaKeysLocallyInSharedPreference>>>Check fileName>>"
                        + schemaKeysPreferenceFileName);
        Log.d(TAG,
                "storeSchemaKeysLocallyInSharedPreference>>>Check schemaKeys>>"
                        + schemaKeys.entrySet());
        ComplexPreferences mComplexPreferences = new ComplexPreferences(
                context, schemaKeysPreferenceFileName, Context.MODE_PRIVATE);
        mComplexPreferences.putKey(schemaKeys);

    }

    public void saveSchemaAttributesListLocallyInSharedPreference() {
        Log.d(TAG,
                "saveSchemaAttributesListLocallyInSharedPreference>>>Check schemaAttPreferenceFileName>>"
                        + schemaAttPreferenceFileName);
        Log.d(TAG,
                "saveSchemaAttributesListLocallyInSharedPreference>>>Check schemaAttPreferenceFileNameKey>>"
                        + schemaAttPreferenceFileNameKey);
        Log.d(TAG,
                "saveSchemaAttributesListLocallyInSharedPreference>>>Check schemaAttrList>>"
                        + schemaAttrList.toArray().toString());
        ComplexPreferences mComplexPreferences = new ComplexPreferences(
                context, schemaAttPreferenceFileName, Context.MODE_PRIVATE);
        mComplexPreferences.putObject(schemaAttPreferenceFileNameKey,
                schemaAttrList);
        mComplexPreferences.commit();

    }
}
