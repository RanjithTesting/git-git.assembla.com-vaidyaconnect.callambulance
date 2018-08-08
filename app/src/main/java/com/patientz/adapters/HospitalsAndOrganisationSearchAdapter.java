package com.patientz.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.OrganisationVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.activity.AddEmployerActivity;
import com.patientz.activity.R;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.CreateAlertDialog;
import com.patientz.utils.Log;
import com.patientz.utils.SyncUtil;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by sukesh on 19/4/16.
 */
public class HospitalsAndOrganisationSearchAdapter extends BaseAdapter {

    private static final String TAG = "HospitalsAndOrganisationSearchAdapter";
    Activity context;
    ArrayList<OrganisationVO> orgSearchList;
    long currentPatientID;
    private ProgressDialog dialog;
    private RequestQueue mRequestQueue;
    private long orgId;

    public HospitalsAndOrganisationSearchAdapter(Activity context,
                                                 ArrayList<OrganisationVO> orgSearchList, long currentUserId) {
        this.context = context;
        this.orgSearchList = orgSearchList;
        this.currentPatientID = currentUserId;
        Log.d(">> org list size <<", "" + orgSearchList.size());
        mRequestQueue = AppVolley.getRequestQueue();
    }

    public View getView(final int pos, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView = convertView;
        final Holder orgHolder;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.org_search_row_list, parent,
                    false);
            orgHolder = new Holder();
            orgHolder.docStatus = (TextView) rowView.findViewById(R.id.status);
            orgHolder.orgName = (TextView) rowView
                    .findViewById(R.id.org_name_searched);

            orgHolder.orgType = (TextView) rowView
                    .findViewById(R.id.org_type_searched);

            orgHolder.invitationButton = (ImageView) rowView
                    .findViewById(R.id.org_subscribe);
            rowView.setTag(orgHolder);
        } else {
            orgHolder = (Holder) rowView.getTag();
        }
        if (orgSearchList.get(pos).getOrgName() != null) {
            orgHolder.orgName.setText(AppUtil.convertToCamelCase(orgSearchList
                    .get(pos).getOrgName()));
        } else {
            orgHolder.orgName.setText("");
        }
        /*
         * if (orgSearchList.get(pos).getCityofPractice() != null) {
		 * orgHolder.sep1.setVisibility(View.VISIBLE);
		 * orgHolder.docLocation.setText(AppUtil.getCity(orgSearchList.get(pos)
		 * .getCityofPractice())); } else {
		 * orgHolder.sep1.setVisibility(View.INVISIBLE);
		 * orgHolder.docLocation.setText(""); }
		 */

        if (orgSearchList.get(pos).getOrgType() != null) {
            orgHolder.orgType.setText(orgSearchList.get(pos).getOrgType());
        } else {
            orgHolder.orgType.setText("");
        }
        // Log.d(">>>OSA>>>Status", "" + orgSearchList.get(pos).getStatus());
        if (TextUtils.equals("Accepted", orgSearchList.get(pos).getStatus())) {
            Log.d(">>>OSA>>>Status>>>Accepted", orgSearchList.get(pos)
                    .getOrgName() + " : " + orgSearchList.get(pos).getStatus());

        } else {

        }
        if (orgSearchList.get(pos).getStatus() == null) {
            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (TextUtils.equals("pending",
                            orgSearchList.get(pos).getStatus())) {
                        AppUtil.showDialog(context, context
                                .getString(R.string.request_sent));
                    } else {
                        Log.d("message-debug", "selected org id : "
                                + orgSearchList.get(pos).getOrgId());
                         orgId=orgSearchList.get(pos).getOrgId();
                        confirmDialog(pos, orgSearchList.get(pos)
                                .getOrgType());
                    }
                }
            });
        } else {
            orgHolder.invitationButton.setVisibility(View.GONE);
            orgHolder.docStatus.setText(orgSearchList.get(pos).getStatus());

        }
        return rowView;
    }

    public void confirmDialog(final int position, final String orgType) {
        final CreateAlertDialog mAlertDialog = new CreateAlertDialog(context);
        if (TextUtils.equals(Constant.ORG_TYPE_CODE_EMPLOYER,
                AppUtil.getOrgTypeCode(context, orgType))) {
            mAlertDialog.setMessage(context
                    .getString(R.string.add_employer_warning_msg));
        }else {
            mAlertDialog.setMessage(context
                    .getString(R.string.add_org_warning_msg));
        }
        mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.CT_ORGANISATION_ADD_CLICKED,true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_ORGANISATION_ADD_CLICKED);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        if(TextUtils.equals(Constant.ORG_TYPE_CODE_EMPLOYER,
                                AppUtil.getOrgTypeCode(context, orgType)))
                        {
                            dialog.dismiss();
                            Intent mIntent=new Intent(context, AddEmployerActivity.class);
                            mIntent.putExtra("orgId",orgId);
                            context.startActivity(mIntent);
                            context.finish();
                        }else
                        {
                            String param = "?patientId=" + currentPatientID + "&orgId="
                                    + orgSearchList.get(position).getOrgId() + "&lastSyncId="
                                    + AppUtil.getEventLogID(context);
                            showProgress(true);
                            mRequestQueue.add(createAddRequest(param));
                        }
                    }
                });
                mAlertDialog
                        .setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                mAlertDialog.show();

    }

   /* public class DialogListener implements DialogInterface.OnClickListener {
        int position;

        public DialogListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                    // Log.d("TAG", "check error");
                    // getItem(getI)Educational Institute
                   *//* if (TextUtils.equals(orgSearchList.get(position).getOrgType()
                            .trim(), "Employer")) {
                        Bundle mBundle = new Bundle();
                        mBundle.putLong("orgId", orgSearchList.get(position)
                                .getOrgId());
                        AddEmployerTypeOrg mDetails = new AddEmployerTypeOrg();
                        mDetails.setArguments(mBundle);
                        mDetails.show(context.getSupportFragmentManager(),
                                "AddOrgWithAdditionalDetails");
                    } else if (TextUtils.equals(orgSearchList.get(position)
                            .getOrgType().trim(), "Educational Institute")) {
                        Bundle mBundle = new Bundle();
                        mBundle.putLong("orgId", orgSearchList.get(position)
                                .getOrgId());
                        AddEducationalInstituteTypeOrg mDetails = new AddEducationalInstituteTypeOrg();
                        mDetails.setArguments(mBundle);
                        mDetails.show(context.getSupportFragmentManager(),
                                "AddEducationalInstituteTypeOrg");
                    } else {
                        new AddOrg(orgSearchList.get(position).getOrgId())
                                .execute();
                    }*//*
                  *//*  String param = "?patientId=" + currentPatientID + "&orgId="
                            + orgSearchList.get(position).getOrgId() + "&lastSyncId="
                            + AppUtil.getEventLogID(context);
                    showProgress(true);
                    mRequestQueue.add(createAddRequest(param));*//*

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }

    }*/

    public static class Holder {
        ImageView invitationButton;
        public View sep1;
        TextView orgName;
        TextView orgType;
        TextView docLocation;
        TextView docStatus;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return orgSearchList.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return orgSearchList.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private StringRequest createAddRequest(final String params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.sendOrgRequestForPatient + params;

        com.patientz.utils.Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                com.patientz.utils.Log.d(TAG, "RESPONSE \n" + response);
                ResponseVO responseVo = null;
                try {
                    if (response != null) {
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<ResponseVO>() {
                        }.getType();

                        responseVo = gson.fromJson(
                                response, objectType);

                        if (responseVo != null) {
                            responseHandler(responseVo);
                        } else {
                            responseHandler(responseVo);
                        }
                    } else {
                        responseHandler(responseVo);
                    }
                } catch (JsonParseException e) {
                    responseHandler(responseVo);
                }
                showProgress(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                com.patientz.utils.Log.d(TAG, "******************* onErrorResponse ******************* \n");
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(false);
                            LayoutInflater inflater =context.getLayoutInflater();
                            View view = inflater.inflate(R.layout. fragment_server_busy_dialog, null);
                            builder.setView(view);
                            TextView mButton= (TextView)view.findViewById(R.id.bt_done);
                            final AlertDialog mAlertDialog=builder.create();
                            mButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialog.dismiss();
                                }
                            });
                            mAlertDialog.show();
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(context,error);
                }
            }

        }) {
           /* @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }*/

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(context, super.getHeaders());
            }
        };
        /*For slow webservice response un-comment this*/
       /* mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20), 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    private void responseHandler(ResponseVO mResponseVO) {
        int code = (int) mResponseVO.getCode();
        switch (code) {
            case Constant.RESPONSE_SUCCESS:
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.ORGANISATION_ADDED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ORGANISATION_ADDED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                ArrayList<PatientUserVO> mPatientUserVOs = mResponseVO.getPatientUserVO();
                for (PatientUserVO mPatientUserVO : mPatientUserVOs) {
                    try {
                        SyncUtil.saveUserRecord(context, mPatientUserVO, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppUtil.showToast(context,
                        context.getString(R.string.org_added_successfully));
                context.finish();
                break;
            case Constant.RESPONSE_FAILURE:
                AppUtil.showToast(context,
                        context.getString(R.string.failure_msg));
                break;
            case Constant.RESPONSE_NOT_LOGGED_IN:
                AppUtil.showToast(context,
                        context.getString(R.string.not_logged_in_error_msg));
                break;
            case Constant.RESPONSE_SERVER_ERROR:
                AppUtil.showToast(context,
                        context.getString(R.string.server_error_msg));
                break;
            case Constant.TERMINATE:
                break;
            case Constant.RESPONSE_REQUEST_FOR_ORG:
                AppUtil.showToast(context,
                        context.getString(R.string.org_request_sent));
                break;
            default:
                break;
        }
    }

    private void showProgress(boolean value) {
        if (value) {
//            Drawable drawable = context.getResources().getDrawable(R.drawable.progress);
            dialog = new ProgressDialog(context);
            // dialog.setTitle("Logging IN...");
            dialog.setMessage(context.getString(R.string.wait_msg));
            dialog.setIndeterminate(true);
            // dialog.setIndeterminateDrawable(drawable);
            // dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(false);
            dialog.show();
        } else {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

}
