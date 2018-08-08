package com.patientz.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.InsuranceUpload;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;


public class InsuranceDecider extends BaseFragment {
    DatabaseHandler mDatabaseHandler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view= inflater.inflate(R.layout.insurance_decider, container, false);
        return view;
    }

    public StringRequest getAccidentCoverageList(final long currentSelectedPatientId) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.insuranceUploadList+"?patientId="+currentSelectedPatientId;
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response=",response);
                if(response!=null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            Gson gson = new Gson();
                            Type objectType = new TypeToken<ArrayList<InsuranceUpload>>() {
                            }.getType();
                            ArrayList<InsuranceUpload> insuranceUploadArrayList = gson.fromJson(response, objectType);
                            Log.d("insuranceUploadArrayList=",insuranceUploadArrayList.size()+"");

                            for (InsuranceUpload insuranceVO : insuranceUploadArrayList) {
                                mDatabaseHandler=DatabaseHandler.dbInit(getActivity());
                                mDatabaseHandler.insertUserUploadInsurance(insuranceVO);
                                FragmentTransaction transaction = getFragmentManager()
                                        .beginTransaction();
                                transaction.replace(((ViewGroup)getView().getParent()).getId(), new InsuranceFragment());
                                transaction.commit();
                            }

                        }
                    } catch (Exception e) {
                        Log.d("e=",e.getMessage()+"");
                        AppUtil.showToast(getActivity(),"Failed to load accident insurances.Please try again");
                        getActivity().finish();
                    }
                }else
                {
                    AppUtil.showToast(getActivity(),"Failed to load accident insurances.Please try");
                    getActivity().finish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppUtil.showToast(getActivity(),"Failed to load accident insurances.Please ");
                getActivity().finish();
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getActivity(), super.getHeaders());
            }
        };
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000,2,2));
        return mRequest;
    }
    @Override
    public void onResume() {
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        RequestQueue mRequestQueue= AppVolley.getRequestQueue();
        mRequestQueue.add(getAccidentCoverageList(currentSelectedPatientId));

        super.onResume();
    }

}
