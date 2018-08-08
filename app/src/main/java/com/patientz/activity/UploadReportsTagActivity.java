package com.patientz.activity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.patientz.VO.FileUploadImagesVO;
import com.patientz.adapters.AdapterEmergencyType;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppHelper;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.VolleyMultipartRequest;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadReportsTagActivity extends BaseActivity {
    private static final String TAG = "UploadReportsActivity";
    LinearLayout progressBar;
    GridView gridView;
    EditText edit_comment;
    DatabaseHandler dh;
    private Integer[] mThumbIds = {R.drawable.report_receipt, R.drawable.reports_prescription, R.drawable.reports_labreport,
            R.drawable.reports_dischargesummary, R.drawable.reports_doctornotes, R.drawable.reports_insurance, R.drawable.reports_xray, R.drawable.reports_other};
    private String[] mThumbText = {"Receipt", "Prescription", "Lab Report", "Discharge Summary", "Doctor Notes", "Insurance", "X-Ray", "Other"};
    Button bt_signup;
    SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tag_reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_upload_reports);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        dh = DatabaseHandler.dbInit(UploadReportsTagActivity.this);

        gridView = (GridView) findViewById(R.id.grid_view_emergency_types);
        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        bt_signup = (Button) findViewById(R.id.bt_signup);
        edit_comment = (EditText) findViewById(R.id.edit_comment);

        gridView.setAdapter(new AdapterEmergencyType(getApplicationContext(), R.layout.adapter_report_tag_type, mThumbIds, mThumbText));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(TAG, "********* grid item clicked ***********");
                defaultSharedPreferences.edit().putInt("file_upload_tag_id", position).commit();
                defaultSharedPreferences.edit().putString("file_upload_tag_text", mThumbText[position]).commit();
                gridView.setAdapter(new AdapterEmergencyType(getApplicationContext(), R.layout.adapter_report_tag_type, mThumbIds, mThumbText));
            }
        });

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.REPORTS_UPLOAD_SUBMIT_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.REPORTS_UPLOAD_SUBMIT_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                if (defaultSharedPreferences.getString("file_upload_tag_text", "").length() == 0 && defaultSharedPreferences.getInt("file_upload_tag_id", -1) == -1) {
                    Toast.makeText(UploadReportsTagActivity.this, getString(R.string.plz_sel_tag), Toast.LENGTH_SHORT).show();
                }  else {
                    try {
                        showProgressBar(true);
                        bt_signup.setClickable(false);
                        ArrayList<FileUploadImagesVO> uploadimages = dh.getFileUploadImagesToServer();
                        Log.e("---->", String.valueOf(uploadimages.size()));
                        if (uploadimages.size() == 1) {
                            Log.e("---->", "uploadSingleFile");
                            uploadSingleFile(uploadimages);
                        } else if (uploadimages.size() > 1) {
                            Log.e("---->", "getFileUploadImages");
                            uploadMultipleFiles(uploadimages);
                        } else {
                            Log.e("---->", "No files to upload");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void uploadMultipleFiles(final ArrayList<FileUploadImagesVO> uploadimages) {

        String url = WebServiceUrls.serverUrl + WebServiceUrls.uploadAll;
        Log.e("url - > ", url);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(NetworkResponse networkResponse) {

                String response = new String(networkResponse.data);
                Log.i("response->", response);
                try {
                    JSONArray result = new JSONArray(response);
                    String fileId = result.getJSONObject(0).getString("fileId");
                    Log.i("fileId->", fileId);
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put(Constant.UpshotEvents.MULTIPLE_REPORTS_UPLOADED, true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.MULTIPLE_REPORTS_UPLOADED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    clearData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgressBar(false);
                bt_signup.setClickable(true);
                NetworkResponse networkResponse = volleyError.networkResponse;
                int statuscode = -2;

                if (networkResponse != null) {
                    statuscode = networkResponse.statusCode;
                }
                Log.e("statuscode: - ", "" + statuscode);
                if (statuscode == Constant.RESPONSE_NOT_LOGGED_IN) {
                    AppUtil.showToast(UploadReportsTagActivity.this, getString(R.string.offlineMode));
                } else {
                    if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError || volleyError instanceof NetworkError) {
                        AppUtil.showToast(UploadReportsTagActivity.this, getString(R.string.connection_error));
                    } else if (volleyError instanceof AuthFailureError) {
                        AppUtil.showToast(UploadReportsTagActivity.this, getString(R.string.offlineMode));
                    } else {
                        AppUtil.showToast(UploadReportsTagActivity.this, getString(R.string.server_error));
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patientId", String.valueOf(AppUtil.getCurrentSelectedPatientId(getApplicationContext())));
                params.put("tags", defaultSharedPreferences.getString("file_upload_tag_text", ""));
                params.put("remarks", edit_comment.getText().toString().trim());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                if (uploadimages.size() > 0) {
                    for (int i = 0; i < uploadimages.size(); i++) {
                        if (uploadimages.get(i).getImage_path().length() > 0) {
                            Log.e("imagepath - > ", uploadimages.get(i).getImage_path() + "");
                            params.put("multiFileAttachment." + (i + 1), new DataPart("multiFileAttachment." + (i + 1), AppHelper.getFileDataFromDrawable(getBaseContext(), Drawable.createFromPath(uploadimages.get(i).getImage_path())), "image/jpeg"));
                        }
                    }
                }
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, 0,
                0));

        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
        mRequestQueue.add(multipartRequest);

    }

    private void clearData() {
        Log.i("clear data->", "clear data->");
        if (dh.getFileUploadImagesToServer().size() > 0) {
            dh.deleteFileUploadImages();
        }
        defaultSharedPreferences.edit().putInt("file_upload_tag_id", -1).commit();
        defaultSharedPreferences.edit().putString("file_upload_tag_text", "").commit();
        defaultSharedPreferences.edit().putBoolean("file_upload_status", true).commit();
        edit_comment.setText("");
        gridView.setAdapter(new AdapterEmergencyType(getApplicationContext(), R.layout.adapter_report_tag_type, mThumbIds, mThumbText));
        showProgressBar(false);
        bt_signup.setClickable(true);
        finish();
    }

    private void uploadSingleFile(final ArrayList<FileUploadImagesVO> uploadimages) {

        String url = WebServiceUrls.serverUrl + WebServiceUrls.createfile;
        Log.e("url - > ", url);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(NetworkResponse networkResponse) {

                String response = new String(networkResponse.data);
                Log.i("response->", response);
                try {
                    JSONObject result = new JSONObject(response);
                    String fileId = result.getString("fileId");
                    Log.i("fileId->", fileId);
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put(Constant.UpshotEvents.SINGLE_REPORT_UPLOADED, true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.SINGLE_REPORT_UPLOADED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    clearData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgressBar(false);
                bt_signup.setClickable(true);
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(UploadReportsTagActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),volleyError);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patientId", String.valueOf(AppUtil.getCurrentSelectedPatientId(getApplicationContext())));
                params.put("tags", defaultSharedPreferences.getString("file_upload_tag_text", ""));
                params.put("remarks", edit_comment.getText().toString().trim());
                Log.d("PARAMS=",params.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                if (uploadimages.size() > 0) {
                    Log.e("imagepath - > ", uploadimages.get(0).getImage_path() + "");
                    params.put("file", new DataPart("file", AppHelper.getFileDataFromDrawable(getBaseContext(), Drawable.createFromPath(uploadimages.get(0).getImage_path())), "image/jpeg"));
                }

                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, 0,
                0));

        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
        mRequestQueue.add(multipartRequest);
    }

    public void showProgressBar(boolean show) {
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (defaultSharedPreferences.getBoolean("file_upload_status", false) == true) {
            Log.e("file_upload_status", "-> " + defaultSharedPreferences.getBoolean("file_upload_status", false));
            finish();
        }
    }


}
