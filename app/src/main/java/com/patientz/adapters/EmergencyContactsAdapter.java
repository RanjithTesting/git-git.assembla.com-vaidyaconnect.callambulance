package com.patientz.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.activity.R;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.SyncUtil;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EmergencyContactsAdapter extends BaseAdapter {

    private static final String TAG = "EmergencyContactsAdapter";
    ArrayList<EmergencyContactsVO> mEmergencyContactsVO;
    Activity context;
    long currentUserId;
    String contactRole;
    //	AddContact addContact12;
    RequestQueue mRequestQueue;
    ProgressDialog dialog;

    public EmergencyContactsAdapter(Activity context,
                                    ArrayList<EmergencyContactsVO> mEmergencyContactsVO,
                                    long currentUserId, String contactRole) {

        Log.d("mEmergencyContactsVO",
                String.valueOf(mEmergencyContactsVO.size()));
        this.context = context;
        this.mEmergencyContactsVO = mEmergencyContactsVO;
        this.contactRole = contactRole;
        this.currentUserId = currentUserId;
        mRequestQueue = AppVolley.getRequestQueue();
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final Holder ecHolder;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(
                    R.layout.search_emergency_contact_list_row, parent, false);
            ecHolder = new Holder();
            ecHolder.sep1 = (TextView) rowView
                    .findViewById(R.id.search_contact_sep1);
            ecHolder.sep2 = (TextView) rowView
                    .findViewById(R.id.search_contact_sep2);
            ecHolder.ecName = (TextView) rowView
                    .findViewById(R.id.search_emergency_contact_name);
            ecHolder.ecGender = (TextView) rowView
                    .findViewById(R.id.search_emergency_gender);
            ecHolder.ecAge = (TextView) rowView
                    .findViewById(R.id.search_emergency_age);
            ecHolder.ecLocation = (TextView) rowView
                    .findViewById(R.id.search_emergency_loc);
            ecHolder.ecButton2 = (ImageView) rowView
                    .findViewById(R.id.search_emergency_accept);
            ecHolder.ecStatus = (TextView) rowView.findViewById(R.id.status);
            rowView.setTag(ecHolder);
        } else {
            ecHolder = (Holder) rowView.getTag();
        }
        if (mEmergencyContactsVO.get(pos).getGender() == null
                || mEmergencyContactsVO.get(pos).getAge() == null) {
            ecHolder.sep1.setVisibility(View.INVISIBLE);
        } else {
            ecHolder.sep1.setVisibility(View.VISIBLE);
        }
        ecHolder.ecName.setText(AppUtil.convertToCamelCase(mEmergencyContactsVO
                .get(pos).getFirstName())
                + " "
                + AppUtil.convertToCamelCase(mEmergencyContactsVO.get(pos)
                .getLastName()));
        ecHolder.ecGender.setText(mEmergencyContactsVO.get(pos).getGender());
        ecHolder.ecAge.setText(mEmergencyContactsVO.get(pos).getAge());
        ecHolder.ecLocation.setText(AppUtil.getCity(mEmergencyContactsVO.get(
                pos).getCity()));
        //	ecHolder.ecButton2.setBackgroundResource(R.drawable.invite_button);

        // ecHolder.ecButton2.setBackgroundResource(R.drawable.accept_button);
        final int position = pos;
        ecHolder.ecButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ECA>>Status check", mEmergencyContactsVO.get(position)
                        .getStatus() + "");
                if (mEmergencyContactsVO.get(position).getStatus() == null
                        || mEmergencyContactsVO.get(position).getStatus()
                        .equalsIgnoreCase("Rejected")
                        || mEmergencyContactsVO
                        .get(position)
                        .getStatus()
                        .equalsIgnoreCase(
                                context.getString(R.string.deleted))) {
                    Log.d("TAG", "check error");

                    final String[] relationship = context.getResources()
                            .getStringArray(R.array.add_patient_relation_entry);
                    new AlertDialog.Builder(context)
                            .setTitle("Contact Relationship")
                            .setSingleChoiceItems(relationship, 0,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            HashMap<String, Object> upshotData = new HashMap<>();
                                            if(contactRole.equalsIgnoreCase(Constant.CARE_GIVER))
                                            {
                                                upshotData.put(Constant.UpshotEvents.CT_CG_REGISTERED_CONTACT_ADD_CLICKED,true);
                                                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_CG_REGISTERED_CONTACT_ADD_CLICKED);
                                                Log.d(TAG,"upshot data="+upshotData.entrySet());
                                            }else
                                            {
                                                upshotData.put(Constant.UpshotEvents.CT_EC_REGISTERED_CONTACT_ADD_CLICKED,true);
                                                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_EC_REGISTERED_CONTACT_ADD_CLICKED);
                                                Log.d(TAG,"upshot data="+upshotData.entrySet());
                                            }

                                            Log.d("TAG",
                                                    "check selected value: "
                                                            + relationship[whichButton]);
                                            try {
                                                String urlEncode = "?patientId=" + currentUserId
                                                        + "&patientUserProfileId=" + mEmergencyContactsVO.get(
                                                        position)
                                                        .getUserProfileId()
                                                        + "&contactRole="
                                                        + URLEncoder.encode(contactRole, "UTF-8")
                                                        + "&relation=" + relationship[whichButton];
                                                showProgress(true);
                                                mRequestQueue.add(createAddRequest(urlEncode));
                                            } catch (Exception e) {
                                                Log.d(TAG, e.getMessage());
                                            }
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                } else {
                    showStatusMsg(position, ecHolder);
                }
            }
        });

        return rowView;

    }

    public void showStatusMsg(int pos, Holder ecHolder) {
        Log.d("ECA>>showStatus>>Status", mEmergencyContactsVO.get(pos)
                .getStatus());
        if (mEmergencyContactsVO.get(pos).getStatus()
                .equalsIgnoreCase(context.getString(R.string.pending))) {
            AppUtil.showDialog(context,
                    context.getString(R.string.request_sent));
        } else if (mEmergencyContactsVO.get(pos).getStatus()
                .equalsIgnoreCase(context.getString(R.string.accepted))) {
            AppUtil.showDialog(context,
                    context.getString(R.string.searchContactAcceptedMsg) + " "
                            + mEmergencyContactsVO.get(pos).getRole());
        }

    }

    public static class Holder {
        public TextView sep2;
        public TextView sep1;
        TextView ecName;
        TextView ecGender;
        TextView ecAge;
        TextView ecLocation;
        TextView ecStatus;
        ImageView ecButton1;
        ImageView ecButton2;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mEmergencyContactsVO.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mEmergencyContactsVO.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

/*	public class AddContact extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
		long contactId;

		public AddContact(long contactId) {
			this.contactId = contactId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.progress);
			dialog = new ProgressDialog(context);
			// dialog.setTitle("Logging IN...");
			dialog.setMessage(context.getString(R.string.wait_msg));
			dialog.setIndeterminate(true);
			dialog.setIndeterminateDrawable(drawable);
			// dialog.setCanceledOnTouchOutside(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... url) {
			String response = "";
			try {
				String urlEncode = "?patientId=" + currentUserId
						+ "&patientUserProfileId=" + contactId
						+ "&contactRole="
						+ URLEncoder.encode(contactRole, "UTF-8")
						+ "&relation=" + url[0];
				response = HttpUtil.callWebServiceWithParams(
						"patientWebservices/saveCareGiverOrEC", urlEncode,
						context);
				Log.d("response", response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		// return response;

		protected void onPostExecute(String response) {

			dialog.dismiss();

			int invitationResponse = 0;
			try {
				invitationResponse = ResponseHandler.handleResponse(context,
						response);
			} catch (DoctrzException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch (invitationResponse) {
			case 00:
				Toast.makeText(context, "Invitation Sent", Toast.LENGTH_SHORT)
						.show();
				ContactListFragment mContactListFragment = new ContactListFragment();
				AppUtil.switchFragment(context, mContactListFragment);
				break;
			case 500:
				AppUtil.showDialog(
						context,
						context.getResources().getString(
								R.string.not_logged_in_error_msg));
				break;
			*//*
			 * case 1012: AppUtil.showDialog(context, responseVO.getResponse());
			 * break;
			 *//*

			default:
				break;
			}
		}*//*
		 * else { AppUtil.showDialog(context, "Network Error !!"); }
		 *//*

	}*/

    private StringRequest createAddRequest(final String params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.saveCareGiverOrEC + params;

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
                            AppUtil.showErrorCodeDialog(context);
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
                ArrayList<PatientUserVO> mPatientUserVOs = mResponseVO.getPatientUserVO();
                for (PatientUserVO mPatientUserVO : mPatientUserVOs) {
                    try {
                        SyncUtil.saveUserRecord(context, mPatientUserVO, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Intent startIntent = new Intent(context, StickyNotificationForeGroundService.class);
                    startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
                    context.startService(startIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppUtil.showToast(context,
                        context.getString(R.string.patientAddedSuccessfully));
                context.finish();
                break;
            case Constant.RESPONSE_FAILURE:
                AppUtil.showToast(context,
                        context.getString(R.string.failure_msg));
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
                        context.getString(R.string.patientAddedSuccessfully));
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
            //dialog.setIndeterminateDrawable(drawable);
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
