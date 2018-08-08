package com.patientz.utils;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by sunil on 17/08/16.
 */
public class MultipartRequestNew extends Request<String> {

    // PRAMS : fileName,checkSum, fileContent, recordId, fileType
    private static final String TAG = "MultipartRequestNew";
    private HttpEntity mHttpEntity;
    private Response.Listener mListener;
    private long recordId;
    private int fileType;
    private String lat, lon, status, hazardType, url, filePath;
    Context mContext;


    public MultipartRequestNew(Context mContext, String lat, String lon, String hazardType, String status, String filePath, String url,
                               Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        Log.d(TAG, "LAT=" + lat + " LON=" + lon);
        Log.d(TAG, "HAZARD TYPE=" + hazardType);
        Log.d(TAG, "Status=" + status);
        Log.d(TAG, "URL=" + url);
        Log.d(TAG, "filePath=" + filePath);
        mListener = listener;
        this.lat = lat;
        this.lon = lon;
        this.hazardType = hazardType;
        this.status = status;
        this.url = url;
        this.mContext = mContext;
        this.filePath = filePath;

        mHttpEntity = buildMultipartEntity(new File(filePath));

    }


    private HttpEntity buildMultipartEntity(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        Log.d(TAG,"buildMultipartEntity");
        //String fileName = file.getName();
        FileBody fileBody = new FileBody(file);
        builder.addPart("imageContent", fileBody);
        builder.addTextBody("longitude", lon);
        builder.addTextBody("latitude", lat);
        builder.addTextBody("hazardType", hazardType);
        builder.addTextBody("status", "");
        Log.d(TAG, "REQUEST JSON=" + builder.toString());
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        Log.d(TAG,"getBodyContentType");

        if (mHttpEntity != null)
            return mHttpEntity.getContentType().getValue();
        else
            return "";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        Log.d(TAG,"getBody");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (mHttpEntity != null)
                mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }finally {
            return bos.toByteArray();
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Log.d(TAG,"getHeaders");
        return AppUtil.addHeadersForApp(mContext, super.getHeaders());
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String str = "";
        try {
            str = new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response.success(str, getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
        Log.d(TAG,"deliverResponse");

    }
}
