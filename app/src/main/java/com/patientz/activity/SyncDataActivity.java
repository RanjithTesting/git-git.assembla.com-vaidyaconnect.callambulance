package com.patientz.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.SyncUtil;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class SyncDataActivity extends BaseActivity {
    private static final String TAG = "SyncDataActivity";
    private TextView loadingText;
    private RequestQueue mRequestQueue;
    private int count = 1;
    private int totalPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        loadingText = (TextView) findViewById(R.id.loading_text);
        mRequestQueue = AppVolley.getRequestQueue();

        //Downloading all users details here and saving into db
        DatabaseHandler dh = DatabaseHandler.dbInit(getApplicationContext());
        ArrayList<PatientUserVO> allUsers = new ArrayList<>();
        try {
            allUsers = dh.getAllUser();
            totalPatients = allUsers.size();
            mRequestQueue.add(createProfileDetailsRequest(33));//patientUserVO.getPatientId()));
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }


    private StringRequest createProfileDetailsRequest(final long patientID) {
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.patientCompleteRecord + patientID;
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ResponseVO responseVO = new ResponseVO();
                Log.d(TAG, "patientCompleteRecord response " + response);
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
                    if (responseVO != null) {
                        if (responseVO.getCode() == Constant.RESPONSE_SUCCESS) {
                            try {
                                for (PatientUserVO patientUserVO : responseVO.getPatientUserVO()) {
                                    //  SyncUtil.updateAllPatientInformations(getApplicationContext(),responseVO.getPatientUserVO(),"qqqq");
                                    SyncUtil.saveUserRecord(getApplicationContext(), patientUserVO, null);
                                    Intent intent = new Intent(SyncDataActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        AppUtil.showToast(getApplicationContext(),
                                getString(R.string.networkError));
                    }
                } else {
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.networkError));
                }
                managaCounts();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                managaCounts();
                AlertDialog.Builder builder = new AlertDialog.Builder(SyncDataActivity.this);
                builder.setCancelable(false);
                LayoutInflater inflater =getLayoutInflater();
                View view = inflater.inflate(R.layout. fragment_server_busy_dialog, null);
                builder.setView(view);
                TextView mButton= (TextView)view.findViewById(R.id.bt_done);
                final AlertDialog mAlertDialog=builder.create();
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                        finish();
                    }
                });
                mAlertDialog.show();
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        return mRequest;
    }

    private void managaCounts() {
        if (count == totalPatients) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            count++;
        }
    }


    /*Down*/
}
