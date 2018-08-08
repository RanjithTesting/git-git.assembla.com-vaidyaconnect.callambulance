package com.patientz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MultipartRequest extends Request<String> {

    // PRAMS : fileName,checkSum, fileContent, recordId, fileType

    public static final String fileContent = "fileContent";
    private String filePath;
    private static final String TAG = "MultipartRequest";
    private HttpEntity mHttpEntity;
    private Response.Listener mListener;
    private long patientId;
    private int fileType;
    private String hazardType, status, lat, lon, url;
    private Context mContext;

    public MultipartRequest(Context mContext, long patientId, String filePath, String url,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        Log.d(TAG, "PATIENT ID=" + patientId);
        Log.d(TAG, "FILE PATH=" + filePath);
        Log.d(TAG, "URL=" + url);

        mListener = listener;
        this.patientId = patientId;
        this.mContext = mContext;
        this.filePath = filePath;
        mHttpEntity = buildMultipartEntity(new File(filePath));

    }

    public MultipartRequest(Context mContext, String lat, String lon, String hazardType, String status, Bitmap bitmap, String url,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        Log.d(TAG, "LAT=" + lat + " LON=" + lon);
        Log.d(TAG, "HAZARD TYPE=" + hazardType);
        Log.d(TAG, "Status=" + status);
        Log.d(TAG, "URL=" + url);
        mListener = listener;
        this.lat = lat;
        this.mContext = mContext;
        this.lon = lon;
        this.hazardType = hazardType;
        this.status = status;
        this.url = url;
        mHttpEntity = buildMultipartEntity(bitmap);
    }

    private HttpEntity buildMultipartEntity(Bitmap bitmap) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        int imageQualityLevel = 90; // for good quality images
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       /* bitmap.compress(Bitmap.CompressFormat.JPEG,
                imageQualityLevel, baos);
        bitmap.recycle();*/
        byte[] ba = baos.toByteArray();
        ByteArrayBody bab = new ByteArrayBody(ba, null);
        builder.addTextBody("hazardType", hazardType);
        builder.addTextBody("latitude", lat);
        builder.addTextBody("longitude", lon);
        builder.addTextBody("status", status);
        builder.addPart(fileContent, bab);
        try {
            baos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        baos = null;
        return builder.build();
    }

    private HttpEntity buildMultipartEntity(String filePath) {
        File file = new File(filePath);
        //   Log.d("MultipartRequest", "file name : "+file.getAbsolutePath()+"\n file size : "+file.length());
        return buildMultipartEntity(file);
    }

    private HttpEntity buildMultipartEntity(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        String fileName = file.getName();
        Log.d(TAG, "FILE NAME=" + fileName);
        if (!TextUtils.isEmpty(filePath)) {
            int imageQualityLevel = 90; // for good quality images
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(bitmap!=null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG,
                        imageQualityLevel, baos);
                bitmap.recycle();
            }
            byte[] ba = baos.toByteArray();
            // base64 = EncodeFile.byteArrayToBase64(ba);
            ByteArrayBody bab = new ByteArrayBody(ba, filePath);
            builder.addTextBody("patientId", String.valueOf(patientId));

            builder.addPart(fileContent, bab);
            builder.addTextBody("lastSyncId", String
                    .valueOf(AppUtil.getEventLogID(mContext)));
            try {
                baos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            baos = null;


        }
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String str = "";
        try {
            str = new String(response.data, "UTF-8");
            Log.d(TAG, "RESPONSE $$ =" + str);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response.success(str, getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
        Log.d(TAG, "deliverResponse");

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
    }
}
