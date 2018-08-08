package com.patientz.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.patientz.adapters.AdapterEmergencyType;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.ImageUtil;
import com.patientz.utils.Log;
import com.patientz.utils.MultipartRequestNew;
import com.patientz.webservices.WebServiceUrls;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.patientz.utils.Constant.PERMISSIONS_REQUEST_CODE_STORAGE_REPORT_HAZARDS;


public class ReportRoadHazardActivity extends BaseActivity {

    private static final String TAG = "ReportRoadHazardActivity";
    long millisUntilFinishedd = 5000;
    int selectedType = 1;
    private File pic;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private CountDownTimer countDownTimer;
    private RelativeLayout progressBar;
    private Integer[] mThumbIds = {R.drawable.incident_badroad, R.drawable.incident_badlighting, R.drawable.incident_trafficlights,
            R.drawable.incident_wrongside, R.drawable.incident_waterclog,
            R.drawable.incident_roadblock};
    private String[] mThumbText = {"Bad Road", "Bad Lighting", "No Traffic Lights", "Wrong Side Driving", "Water Clogging",
            "Road Encroachment"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporthazard_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.title_activity_road_hazards_types);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GridView gridView = (GridView) findViewById(R.id.grid_view_emergency_types);
        progressBar = (RelativeLayout) findViewById(R.id.progress_bar);


        TextView tvEmergencyTypeCounter = (TextView) findViewById(R.id.tv_emergency_type_counter);
        gridView.setAdapter(new AdapterEmergencyType(getApplicationContext(), R.layout.adapter_safety_hazards_type, mThumbIds, mThumbText));
        //x AlphaAnimation mAlphaAnimation=new AlphaAnimation();xx
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(TAG, "********* grid item clicked ***********");
                String hazardType = (String) parent.getAdapter().getItem(position);
                selectedType = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportRoadHazardActivity.this);
                builder.setTitle(hazardType);
                builder.setMessage(R.string.dialog_msg_take_hazard_pic);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.ROAD_HAZARDS_SUBMIT, true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ROAD_HAZARDS_SUBMIT);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());

                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        if (ActivityCompat.checkSelfPermission(ReportRoadHazardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            dispatchTakePictureIntent();
                        } else {
                            ActivityCompat.requestPermissions(
                                    ReportRoadHazardActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSIONS_REQUEST_CODE_STORAGE_REPORT_HAZARDS);
                        }

                    }
                });
                builder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.ROAD_HAZARDS_SUBMIT, true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ROAD_HAZARDS_SUBMIT);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        showProgressBar(true);
                        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                        mRequestQueue.add(callSaveSafetyHazardWebserviceWithoutImage());

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                HashMap<String, Object> bkDATA = new HashMap<>();
                bkDATA.put("RoadHazard", position);
                Log.d("RoadHazard bk data", bkDATA.toString());
                UpshotEvents.createCustomEvent(bkDATA, 12);
            }
        });
        // defaultSettingEmergencyTypeAsAccident(tvEmergencyTypeCounter);
    }

    public void showProgressBar(boolean show) {
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);

    }

    private String createFileRequest() {

        boolean isExternalStorageWriteable = ImageUtil
                .isExternalStorageWriteable();
        String filePath = "";
        File[] mFiles;
        String STORAGE_PATH = getString(R.string.uploadPath);
        if (isExternalStorageWriteable) {
            mFiles = ContextCompat.getExternalFilesDirs(
                    getApplicationContext(), STORAGE_PATH);
            if (mFiles.length == 2) {
                File mFile = new File(mFiles[0], "safety_road_hazard");
                if (mFile.exists()) {
                    filePath = mFiles[0].getPath() + File.separator + "safety_road_hazard.jpg";
                } else {
                    filePath = mFiles[1].getPath() + File.separator + "safety_road_hazard.jpg";
                }
            } else {
                filePath = mFiles[0].getPath() + File.separator + "safety_road_hazard.jpg";
                Log.d(TAG, "Upload file path=" + filePath);
            }
        }
        return filePath;
    }

    public StringRequest callSaveSafetyHazardWebserviceWithoutImage() {
        Log.d(TAG, "createSessionVerifyRequest");
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.webservice_safety_road_hazard;
        Log.d(TAG, "szServerUrl - " + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "login response : " + response);
                if (response.equalsIgnoreCase("Success")) {
                    showProgressBar(false);
                    AppUtil.showToast(getApplicationContext(), getString(R.string.thank_reg_success));
                    finish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("error1 - ", "" + android.os.Build.VERSION.SDK_INT);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(ReportRoadHazardActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),error);
                }
            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences mSharedPreferences = getApplicationContext().getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
                params.put("imageContent", "");
                params.put("longitude", mSharedPreferences.getString("lon", "0"));
                params.put("latitude", mSharedPreferences.getString("lat", "0"));
                params.put("hazardType", String.valueOf(selectedType + 1));
                params.put("status", "");
                Log.d(TAG,"params="+params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(ReportRoadHazardActivity.this, super.getHeaders());
            }
        };
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    public MultipartRequestNew callSaveSafetyHazardWebservice(String filePath) {
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.webservice_safety_road_hazard;
        SharedPreferences mSharedPreferences = getApplicationContext().getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
        MultipartRequestNew multipartRequest = new MultipartRequestNew(getApplicationContext(),
                mSharedPreferences.getString("lat", "0"), mSharedPreferences.getString("lon", "0"), String.valueOf(selectedType + 1), "", filePath,
                szServerUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                handleFileUploadResponse(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(ReportRoadHazardActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),error);

                }
            }
        });
        return multipartRequest;
    }

    private void handleFileUploadResponse(String response) {

        if (!TextUtils.isEmpty(response)) {
            Log.d(TAG, "RESPONSE=" + response);
            if (response.equalsIgnoreCase("Success")) {
                showProgressBar(false);
                AppUtil.showToast(getApplicationContext(), getString(R.string.thank_reg_success));
                finish();
            }
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "********* onRestoreInstanceState ***********");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "********* onSaveInstanceState ***********");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_kill:
                Log.d(TAG, "Killing Activity");
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "********* onDestroy ***********");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            pic = createImageFile();
            if (pic != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(pic));
                startActivityForResult(takePictureIntent,
                        REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        File file = null;
        boolean isExternalStorageWriteable = ImageUtil
                .isExternalStorageWriteable();
        if (isExternalStorageWriteable) {
            file = new File(Environment.getExternalStorageDirectory(), "image_" + String.valueOf(System.currentTimeMillis()) + ".png");

        } else {
            // phone might be in unmount state/removed sd card/storage is read
            // only
            AppUtil.showToast(getApplicationContext(), getString(R.string.storage_med_not_avail));
            finish();
        }

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE
                    && resultCode == RESULT_OK) {
                if (pic != null) {
                    String imageUri = pic.getPath();
                    if (imageUri != null) {
                        String compressedFileName = ImageUtil.compressImage(
                                getApplicationContext(), imageUri);
                        Log.d(TAG, "Compressed image file name : " + compressedFileName);
                        File file = new File(compressedFileName);
                        Log.d(TAG,
                                "original file size : "
                                        + (float) pic.length()
                                        / (1024 * 1024) + " MB");
                        Log.d(TAG, "Compressed file size : " + file.length() / (1024)
                                + " KB");
                        Log.d(TAG, "Percentage compression : "
                                + (float) (pic.length() - file.length())
                                / pic.length());
                        if (file != null) {
                            pic = file;
                            showProgressBar(true);
                            RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                            MultipartRequestNew multipartRequest = callSaveSafetyHazardWebservice(pic.getPath());
                            mRequestQueue.add(multipartRequest);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_STORAGE_REPORT_HAZARDS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(ReportRoadHazardActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(ReportRoadHazardActivity.this);
                } else {
                }
                break;
        }
    }
}
