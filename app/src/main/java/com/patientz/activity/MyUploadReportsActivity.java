
package com.patientz.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.PatientFileCollection;
import com.patientz.VO.UserInfoVO;
import com.patientz.adapters.MyUploadsAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MyUploadReportsActivity extends BaseActivity {
    private static final String TAG = "MyUploadReportsActivity";
    GridView gridView;
    RequestQueue mRequestQueue;
    ArrayList<PatientFileCollection> patientFileCollections;
    SharedPreferences defaultSharedPreferences;
    TextView  tvName, tvAge, tvGender;
    LinearLayout progressBar;
    MyUploadsAdapter myUploadsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload_reports);

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

        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvGender = (TextView) findViewById(R.id.tv_gender);

        gridView = (GridView) findViewById(R.id.grid_view_emergency_types);
        TextView emptyView = (TextView)findViewById(android.R.id.empty);
        gridView.setEmptyView(emptyView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(TAG, "********* grid item clicked ***********");
                Log.d(TAG, "fileid - " + patientFileCollections.get(position).getFileId());
                Log.d(TAG, "username - " + patientFileCollections.get(position).getCreatedByUsername());
                Log.e(TAG, "content_type - " + patientFileCollections.get(position).getFile().getContentType());

                startActivity(new Intent(MyUploadReportsActivity.this, PDFView.class)
                        .putExtra("fileid", patientFileCollections.get(position).getFileId())
                        .putExtra("moduleType",Constant.IMAGE_MODULE_TYPE_REPORTS)
                        .putExtra("content_type", patientFileCollections.get(position).getFile().getContentType()));
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyUploadReportsActivity.this);
                builder.setMessage(getString(R.string.label_delete_report));
                builder.setTitle("");
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        showProgressBar(true);
                        RequestQueue mRequestQueue= AppVolley.getRequestQueue();
                        mRequestQueue.add(createDeleteReportRequest((PatientFileCollection) gridView.getItemAtPosition(position),position));
                    }
                });
                builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(this);
        try {
            long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(this);
            UserInfoVO mUserInfoVO = mDatabaseHandler.getUserInfo(currentSelectedProfile);
            Log.d(TAG, "NAME=" + mUserInfoVO.getFirstName());

            tvName.setText(mUserInfoVO.getFirstName() + " " + mUserInfoVO.getLastName());
            tvAge.setText(AppUtil.convertToAge(mUserInfoVO.getDateOfBirth(), MyUploadReportsActivity.this));
            tvGender.setText((mUserInfoVO.getGender() == 2 ? getString(R.string.female) : getString(R.string.male)) + "  |  " + mUserInfoVO.getPatientHandle());

        } catch (Exception e) {
            e.printStackTrace();
        }

        mRequestQueue = AppVolley.getRequestQueue();
        showProgressBar(true);
        mRequestQueue.add(getMyUploadReports());
    }
    private StringRequest createDeleteReportRequest(PatientFileCollection mPatientFileCollection, final int position) {
        long patientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        String url= WebServiceUrls.serverUrl + WebServiceUrls.deleteReport +"id="+mPatientFileCollection.getId()+"&patientId="+patientId;
        Log.d(TAG, "url = " + url);
        StringRequest mRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, WebServiceUrls.deleteReport + " response = " + response);
                showProgressBar(false);
                patientFileCollections.remove(position);
                myUploadsAdapter.notifyDataSetChanged();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgressBar(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(MyUploadReportsActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),error);

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }

        };
        return mRequest;
    }
    private StringRequest getMyUploadReports() {

        String url = WebServiceUrls.serverUrl + WebServiceUrls.searchfile + String.valueOf(AppUtil.getCurrentSelectedPatientId(getApplicationContext()));
        Log.d(TAG, "url = " + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, " response = " + response);

                if (response != null && response.length() != 0) {

                    Type collectionType = new TypeToken<Collection<PatientFileCollection>>() {
                    }.getType();
                    patientFileCollections = AppUtil.getGson().fromJson(response, collectionType);

                    Collections.sort(patientFileCollections, new Comparator<PatientFileCollection>() {
                        @Override
                        public int compare(PatientFileCollection t0, PatientFileCollection t1) {
                            if (t0.getDateCreated() == null || t1.getDateCreated() == null)
                                return 0;
                            return t1.getDateCreated().compareTo(t0.getDateCreated());
                        }
                    });

                    Log.e("patientFileCollections", patientFileCollections.size() + "");
                    if (patientFileCollections.size() == 0) {
                    } else {
                        myUploadsAdapter=new MyUploadsAdapter(getApplicationContext(), R.layout.adapter_my_reports, patientFileCollections);
                        gridView.setAdapter(myUploadsAdapter);
                       /* MyUploadsAdapter myUploadsAdapter=new MyUploadsAdapter(getApplicationContext(), R.layout.adapter_my_reports, patientFileCollections);
                        gridView.setAdapter(myUploadsAdapter);
                        myUploadsAdapter.notifyDataSetChanged();*/
                    }
                }
                showProgressBar(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgressBar(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(MyUploadReportsActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),error);

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }

        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fileupload_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                Log.d(TAG, "Add");
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.REPORTS_LIST_ADD_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.REPORTS_LIST_ADD_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                startActivity(new Intent(MyUploadReportsActivity.this, UploadReportsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
            defaultSharedPreferences.edit().putBoolean("file_upload_status", false).commit();
            showProgressBar(true);
            mRequestQueue.add(getMyUploadReports());
        }
    }
}














