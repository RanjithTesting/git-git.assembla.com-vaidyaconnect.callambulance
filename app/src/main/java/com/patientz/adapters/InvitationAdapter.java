package com.patientz.adapters;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class InvitationAdapter extends BaseAdapter {
    private final String TAG = "InvitationAdapter";

    Activity context;
    ArrayList<PatientUserVO> mUserVOs;
    ProgressDialog dialog;
    PatientUserVO invitationByPatientVO;
    ViewGroup parentView;
    private long patientId;
    String status = "";
    String acceptReject = "";
    Holder holder;
    private ListView mListView;
    private LinearLayout mLoaderStatusView;
    int pos;
    private long acceptedOrRejectedPatientId;
    RequestQueue mRequestQueue = AppVolley.getRequestQueue();

    public InvitationAdapter(Activity context,
                             ArrayList<PatientUserVO> mUserVOs, ListView mListView, LinearLayout parentProgressBar, long patientId) {
        this.context = context;
        this.mUserVOs = mUserVOs;// TODO Auto-generated constructor stub
        this.mListView = mListView;
        mLoaderStatusView = parentProgressBar;
        this.patientId = patientId;

    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        View rowView = convertView;
        this.pos = pos;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.invitation_row, parent, false);

            holder = new Holder();
            //
            holder.accept_button = (ImageView) rowView
                    .findViewById(R.id.iv_accept);
            holder.reject_button = (ImageView) rowView
                    .findViewById(R.id.iv_reject);
            //
            holder.name = (TextView) rowView.findViewById(R.id.tv_name);
            holder.ivPic = (ImageView) rowView.findViewById(R.id.iv_pic);
            holder.role = (TextView) rowView.findViewById(R.id.tv_role);

            holder.relationship = (TextView) rowView
                    .findViewById(R.id.tv_relationship);

            rowView.setTag(holder);
            // TODO Auto-generated method stub
        } else {
            holder = (Holder) rowView.getTag();
        }
        invitationByPatientVO = mUserVOs.get(pos);
        setUserPic(holder.ivPic);

        holder.name.setText(AppUtil.convertToCamelCase(mUserVOs.get(pos)
                .getFirstName())
                + " "
                + AppUtil.convertToCamelCase(mUserVOs.get(pos).getLastName()));
        holder.role.setText(mUserVOs.get(pos).getRole());
        holder.relationship.setText(mUserVOs.get(pos).getRelationship()!=null?mUserVOs.get(pos).getRelationship():"");
        holder.accept_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "Reject button clicked");
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.CT_INVITATION_ACCEPTED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_INVITATION_ACCEPTED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                status = "Accepted";
                if (holder.role.getText().toString()
                        .equalsIgnoreCase(context.getString(R.string.filter_emergency_contact))) {
                    acceptReject = context.getString(R.string.accept_ec_invitation_warning_msg);
                } else if (holder.role.getText().toString()
                        .equalsIgnoreCase(context.getString(R.string.fab_care_giver_title))) {
                    acceptReject = context.getString(R.string.accept_cg_invitation_warning_msg);
                } else {

                }
                acceptRejectInvitation(pos, "Accepting Invitation");
            }
        });
        holder.reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "Reject button clicked");
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.CT_INVITATION_REJECTED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_INVITATION_REJECTED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                status = context.getString(R.string.rejected);
                acceptReject = context.getString(R.string.rejectInvitation);
                acceptRejectInvitation(pos, "Rejecting Invitation");
            }
        });
        return rowView;
    }

    private void setUserPic(ImageView ivPic) {
        long picFileId = mUserVOs.get(pos).getPicId();
        String url = WebServiceUrls.getPatientAttachment + picFileId+"&patientId="+invitationByPatientVO.getPatientId()+"&moduleType="+ Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
        Log.d(TAG, "Url=" + url);
        Picasso.with(context).load(url).into(ivPic);
    }

    public void acceptRejectInvitation(final int pos, final String progressBarText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        if (AppUtil.isOnline(context)) {
            alertDialogBuilder.setMessage(acceptReject);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(context.getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            acceptedOrRejectedPatientId = mUserVOs.get(pos).getUserProfileId();
                            showProgress(true);
                            ((TextView) mLoaderStatusView.getChildAt(1)).setText(progressBarText);
                            //getSessionAndCallUpdate(context);
                            mRequestQueue.add(createInvitationWebservice());
                        }
                    });
            alertDialogBuilder.setNegativeButton(context.getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            alertDialogBuilder.setMessage(context.getString(R.string.offlineMode));
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setNegativeButton(context.getString(R.string.OK),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }


    private StringRequest createInvitationWebservice() {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.acceptRejectPatientAccessRequest;

        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE \n" + response);
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

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                AppUtil.showToast(context, context.getString(R.string.toast_adapter_failed_invitation));
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(context);
                            break;
                    }
                }else
                {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {
                           AppUtil.showToast(getApplicationContext(), context.getString(R.string.connection_error));
                    }
                }

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                long patientAccessId = invitationByPatientVO.getPatientAccessId();
                Log.d(TAG, "PATIENT ACCESS ID=" + String
                        .valueOf(patientAccessId));
                Log.d(TAG, "lastSyncId=" + String.valueOf(AppUtil.getEventLogID(context)));
                Log.d(TAG, "status=" + status);
                params.put("patientId", String
                        .valueOf(patientId));
                params.put("patientAccessId", String
                        .valueOf(patientAccessId));
                params.put("lastSyncId", String.valueOf(AppUtil.getEventLogID(context)));
                params.put("status", status);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(context, super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {
        showProgress(false);

        //dialog.dismiss();
        if (mResponseVO != null)
            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_CODE_SUCCESS:
                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "acceptedOrRejectedPatientId=" + acceptedOrRejectedPatientId);
                    DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(context);

                    if(status.equalsIgnoreCase("Accepted"))
                    {
                        for (PatientUserVO mPatientUserVO : mResponseVO.getPatientUserVO()) {
                            Log.d("USER_NAME=",mPatientUserVO.getFirstName());
                            ArrayList<PatientUserVO> mPatientUserVOS= new ArrayList<>();
                            mPatientUserVOS.add(mPatientUserVO);
                            try {
                                if (patientId == mPatientUserVO.getPatientId()) {
                                    mDatabaseHandler.insertUsers(mPatientUserVOS);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else
                    {
                        mDatabaseHandler.deletePatient(acceptedOrRejectedPatientId);
                    }
                    mUserVOs.remove(pos);
                    notifyDataSetChanged();
                  /*  Intent service = new Intent(context, CallAmbulanceSyncService.class);
                    context.startService(service);*/
                    break;
                case Constant.RESPONSE_CODE_INVITATION_STATU_UPDATE_FAILED:
                    Toast.makeText(context,
                            context.getString(R.string.unableToAcceptInvite), Toast.LENGTH_SHORT).show();
                    break;
                case Constant.RESPONSE_NOT_LOGGED_IN:
                    Toast.makeText(context,
                            context.getString(R.string.not_logged_in_error_msg), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        else {
            AppUtil.showToast(context, context.getString(R.string.toast_adapter_failed_invitation));

        }
    }


    public static class Holder {
        TextView name;
        TextView role;
        TextView status;
        ImageView ivPic;
        TextView relationship;
        ImageView accept_button;
        ImageView reject_button;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mUserVOs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mUserVOs.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoaderStatusView.setVisibility(View.VISIBLE);
            mLoaderStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoaderStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mListView.setVisibility(View.VISIBLE);
            mListView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mListView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}


