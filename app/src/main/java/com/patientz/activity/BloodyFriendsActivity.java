package com.patientz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.BloodyFriendVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.adapters.AdapterPhoneContacts;
import com.patientz.databases.DatabaseHandler;
import com.patientz.service.IntentServiceFetchContacts;
import com.patientz.services.BloodyFriendsSyncService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.DatePickerFragment;
import com.patientz.utils.DividerItemDecoration;
import com.patientz.utils.Log;
import com.patientz.utils.TimePickerFragment;
import com.patientz.utils.Validator;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BloodyFriendsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BloodBuddiesActivity";
    ContactsResultReceiver contactResultReceiver;
    ProgressDialog progressDialog = null;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView button_sendrequest;
    Spinner select_bloodgroupcontacts;
    TextView tv_hint;
    SharedPreferences spnfs;
    SharedPreferences.Editor spedit;
    DatabaseHandler dh;
    RequestQueue mRequestQueue;
    public static ArrayList<BloodyFriendVO> selcontactslist;
    CheckBox check_select_all;
    TextView tv_contacts_count, tv_name, tv_bloodgroup, tv_bloodgrouphint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodfriends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.label_bloodbuddies);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dh = DatabaseHandler.dbInit(BloodyFriendsActivity.this);

        selcontactslist = new ArrayList<BloodyFriendVO>();

        Log.e("selcontactslist", "--> " + selcontactslist.size());

        spnfs = getSharedPreferences("blood_request_details", 4);
        spedit = spnfs.edit();

        mRequestQueue = AppVolley.getRequestQueue();

        tv_bloodgroup = (TextView) findViewById(R.id.tv_bloodgroup);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_bloodgrouphint = (TextView) findViewById(R.id.tv_bloodgrouphint);
        tv_contacts_count = (TextView) findViewById(R.id.tv_contacts_count);
        select_bloodgroupcontacts = (Spinner) findViewById(R.id.select_bloodgroupcontacts);
        button_sendrequest = (TextView) findViewById(R.id.button_sendrequest);
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        check_select_all = (CheckBox) findViewById(R.id.check_select_all);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));

        button_sendrequest.setOnClickListener(this);
        tv_bloodgrouphint.setOnClickListener(this);

        button_sendrequest.setVisibility(View.INVISIBLE);


        select_bloodgroupcontacts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                check_select_all.setChecked(false);
                setContactsData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        check_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setContactsData();
            }
        });

        if (AppUtil.isOnline(getApplicationContext())) {
            Log.d(TAG, "ONCREATE IS CALLED");
            mRequestQueue.add(getBloodyFriendsContacts());
        } else {
            AppUtil.showToast(getApplicationContext(), getString(R.string.toast_no_internet));
        }

    }

    private StringRequest getBloodyFriendsContacts() {
        String url = WebServiceUrls.serverUrl + WebServiceUrls.getSyncedContacts;
        Log.d(TAG, " url = " + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, " response = " + response);

                if (response != null && response.length() != 0) {

                    Type collectionType = new TypeToken<Collection<BloodyFriendVO>>() {
                    }.getType();
                    ArrayList<BloodyFriendVO> contacts = AppUtil.getGson().fromJson(response, collectionType);

                    if (contacts.size() != 0) {
                        tv_hint.setVisibility(View.GONE);
                        try {
                            if (dh.getPhoneContactsInfoCount() > 0) {
                                Log.d(TAG, " update = " + dh.getPhoneContactsInfoCount());
                                dh.updatePhoneContactsDetails(contacts);
                            } else {
                                Log.d(TAG, " insert = " + dh.getPhoneContactsInfoCount());
                                dh.insertPhoneContactsInfoDUPLICATE(contacts);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setContactsData();
                    } else {
                        tv_hint.setVisibility(View.VISIBLE);
                        AppUtil.showToast(getApplicationContext(), getString(R.string.networkError));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ERROR MESSAGE=" + error.getMessage());
                int responsecode = -2;
                try {
                    if(error.networkResponse!=null)
                    {
                        responsecode = error.networkResponse.statusCode;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Error responsecode - " + responsecode);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    private void setContactsData() {

        if (dh.getPhoneContactsInfo().size() > 0 && dh.getPhoneContactsInfoDUPLICATE().size() != 0) {
            AppUtil.showToast(BloodyFriendsActivity.this, "Sync in progress.This will take a few minutes. We will notify you once the process is completed.");
        }

        selcontactslist = new ArrayList<BloodyFriendVO>();
        spedit.clear().commit();

        try {

            ArrayList<BloodyFriendVO> contacts_list = dh.getPhoneContactsInfoShow(select_bloodgroupcontacts.getSelectedItem().toString().trim());
            Log.d(TAG, "contacts_list - " + contacts_list.size());

            //if (contacts_list.size() > 0) {
            if (contacts_list.size() > 0 && !select_bloodgroupcontacts.getSelectedItem().toString().trim().contains("Select")) {
                tv_hint.setVisibility(View.GONE);
                check_select_all.setVisibility(View.VISIBLE);
                tv_contacts_count.setVisibility(View.VISIBLE);
                button_sendrequest.setVisibility(View.VISIBLE);

                if (contacts_list.size() == 1) {
                    tv_contacts_count.setText(contacts_list.size() + " Contact Available");
                } else {
                    tv_contacts_count.setText(contacts_list.size() + " Contacts Available");
                }
            } else {
                tv_hint.setVisibility(View.VISIBLE);
                if (select_bloodgroupcontacts.getSelectedItemPosition() != 0) {
                    tv_hint.setText(getString(R.string.nocontactfound) + " " + select_bloodgroupcontacts.getSelectedItem() + " " + getString(R.string.bloodgroup));
                } else {
                    tv_hint.setText(getString(R.string.label_sync_hint));
                }
                tv_contacts_count.setVisibility(View.GONE);
                button_sendrequest.setVisibility(View.GONE);
                check_select_all.setVisibility(View.GONE);
            }


            Log.d(TAG, "@@@ - " + check_select_all.isChecked());

            AdapterPhoneContacts mAdapter = new AdapterPhoneContacts(getApplicationContext(), contacts_list, check_select_all.isChecked());
            mRecyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startIntentServiceFetchContacts() {  //==]=-O9IF
        progressDialog = CommonUtils.showProgressDialogWithCustomMsg(BloodyFriendsActivity.this,getString(R.string.reading_contacts_msg));
        Intent intent = new Intent(BloodyFriendsActivity.this, IntentServiceFetchContacts.class);
        intent.putExtra(getString(R.string.package_name) + "." + Constant.CONTACTS_RECEIVER, contactResultReceiver);
        startService(intent);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tv_bloodgrouphint) {

            Intent mIntent = new Intent(this, ActivityEditProfile.class);
            mIntent.putExtra("key", "edit");
            startActivity(mIntent);

        } else if (v.getId() == R.id.button_sendrequest) {

            final Dialog dialogpopup = new Dialog(BloodyFriendsActivity.this);
            dialogpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogpopup.setContentView(R.layout.dialog_blood_friends_send_request);
            dialogpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final TextView tv_blood_group = (TextView) dialogpopup.findViewById(R.id.tv_blood_group);
            final EditText editName = (EditText) dialogpopup.findViewById(R.id.edit_name);
            final Spinner spinnerCountrycodes = (Spinner) dialogpopup.findViewById(R.id.spinner_countrycodes);
            final EditText editPhno = (EditText) dialogpopup.findViewById(R.id.edit_phno);
            final Spinner spinnerNoofunits = (Spinner) dialogpopup.findViewById(R.id.spinner_noofunits);
            EditText editmessage = (EditText) dialogpopup.findViewById(R.id.editmessage);
            final EditText textDate = (EditText) dialogpopup.findViewById(R.id.text_date);
            ImageView imageDate = (ImageView) dialogpopup.findViewById(R.id.image_date);
            final EditText textTime = (EditText) dialogpopup.findViewById(R.id.text_time);
            ImageView imgTime = (ImageView) dialogpopup.findViewById(R.id.img_time);
            final EditText editLocation = (EditText) dialogpopup.findViewById(R.id.edit_location);
            final EditText editPurpose = (EditText) dialogpopup.findViewById(R.id.edit_purpose);
            TextView tvAcceptButton = (TextView) dialogpopup.findViewById(R.id.tv_accept_button);
            TextView tvCancelButton = (TextView) dialogpopup.findViewById(R.id.tv_cancel_button);

            if (select_bloodgroupcontacts.getSelectedItemPosition() != 0) {
                tv_blood_group.setText(getString(R.string.reqfor) + " " + select_bloodgroupcontacts.getSelectedItem().toString() + " " + getString(R.string.blood));
            }

            imgTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment picker = new TimePickerFragment(textTime);
                    picker.show(getFragmentManager(), "timePicker");
                }
            });
            imageDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment picker = new DatePickerFragment(textDate);
                    picker.show(getFragmentManager(), "datePicker");
                }
            });

            tvAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(BloodyFriendsActivity.this);
                    if (selcontactslist.size() <= 1) {
                        builder.setMessage("You are about to request for blood " + select_bloodgroupcontacts.getSelectedItem().toString().trim() + " to " + selcontactslist.size() + " contact via SMS. charges apply as per your carrier.");
                    } else {
                        builder.setMessage("You are about to request for blood " + select_bloodgroupcontacts.getSelectedItem().toString().trim() + " to " + selcontactslist.size() + " contacts via SMS. charges apply as per your carrier.");
                    }
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            long currentSelectedUserId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                            String sender_name = "";

                            if (AppUtil.checkLoginDetails(BloodyFriendsActivity.this)) {
                                DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                                try {
                                    PatientUserVO mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedUserId);
                                    if (mPatientUserVO != null && !mPatientUserVO.equals("")) {
                                        sender_name = mPatientUserVO.getFirstName();
                                    } else {
                                        sender_name = editPhno.getText().toString().trim();
                                    }
                                } catch (Exception e) {
                                    sender_name = editPhno.getText().toString().trim();
                                }
                            }

                            String send_req_msg = "Hi, " + editName.getText().toString().trim()
                                    + " is in need of " + spinnerNoofunits.getSelectedItem().toString().trim()
                                    + " units of " + select_bloodgroupcontacts.getSelectedItem().toString().trim()
                                    + " blood for a " + editPurpose.getText().toString().trim()
                                    + " at " + editLocation.getText().toString().trim()
                                    + " at " + textTime.getText().toString().trim()
                                    + " on " + textDate.getText().toString().trim()
                                    + ". Please help save a life. Contact " + editPhno.getText().toString().trim()
                                    + ".\nRegards,\n" + sender_name;


                            for (int i = 0; i < selcontactslist.size(); i++) {
                                AppUtil.sendSMS(BloodyFriendsActivity.this, selcontactslist.get(i).getContact(), send_req_msg);
                            }

                            dialog.cancel();
                            Log.e("selcontactslist", "--> " + selcontactslist.size() + " & send_req_msg " + send_req_msg);
                            dialogpopup.dismiss();
                            check_select_all.setChecked(false);
                            AppUtil.showToast(getApplicationContext(),"Sms sent to contacts");
                            setContactsData();
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });


                    ArrayList<View> errorProneEdittext = new ArrayList<View>();
                    Boolean result = true;

                    if (editName.getText().toString().trim().length() == 0) {
                        editName.setError(getString(R.string.fname_mandatory_error_msg));
                        errorProneEdittext.add(editName);
                        result = false;
                    } else {
                        if (editName.getText().toString().trim().length() < 3 || editName.getText().toString().trim().length() > 25) {
                            editName.setError(getString(R.string.fname_length_error_msg));
                            errorProneEdittext.add(editName);
                            result = false;
                        }
                    }

                    if (editPhno.getText().toString().trim().matches(Validator.phoneValidator) == false) {
                        editPhno.setError(getString(R.string.alt_phoneno_invalid));
                        errorProneEdittext.add(editPhno);
                        result = false;
                    }
                    if ((editPhno.getText().toString().trim().length() < 10 || editPhno.getText().toString().trim().length() > 13)) {
                        editPhno.setError(getString(R.string.phoneno_length_error_msg));
                        errorProneEdittext.add(editPhno);
                        result = false;
                    }

                    if (textDate.getText().toString().trim().length() == 0) {
                        textDate.setError("Please enter required date");
                        errorProneEdittext.add(textDate);
                        result = false;
                    }

                    if (textTime.getText().toString().trim().length() == 0) {
                        textTime.setError("Please enter required time");
                        errorProneEdittext.add(textTime);
                        result = false;
                    }

                    if (editLocation.getText().toString().trim().length() == 0) {
                        editLocation.setError("Please enter where should the donor come");
                        errorProneEdittext.add(editLocation);
                        result = false;
                    }

                    if (editPurpose.getText().toString().trim().length() == 0) {
                        editPurpose.setError("Please enter purpose of blood");
                        errorProneEdittext.add(editPurpose);
                        result = false;
                    }

                    if (result == true) {
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
            tvCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogpopup.dismiss();
                }
            });


            if (selcontactslist.size() > 0) {
                dialogpopup.show();
            } else {
                AppUtil.showToast(BloodyFriendsActivity.this, getString(R.string.plz_sel_contacts));
            }

        }
    }

    class ContactsResultReceiver extends ResultReceiver {

        public ContactsResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            Log.d(TAG, "ON RECEIVE CALLED - " + resultCode);

            if (resultCode == 567) {
                progressDialog.dismiss();
                if (AppUtil.isOnline(getApplicationContext())) {
                    if (dh.getPhoneContactsInfo().size() > 0) {
                        Log.e("getPhoneContactsInfo", "--> " + dh.getPhoneContactsInfo().size());
                        startService(new Intent(BloodyFriendsActivity.this, BloodyFriendsSyncService.class));
                        AppUtil.showToast(BloodyFriendsActivity.this, "Sync in progress.This will take a few minutes. We will notify you once the process is completed.");
                    }else
                    {
                        AppUtil.showToast(BloodyFriendsActivity.this, "Your contacts had already been synced");
                    }
                } else {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_no_internet));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bloody_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_sync2:

                if (AppUtil.isOnline(getApplicationContext())) {
                    Log.d(TAG, "ONCREATE IS CALLED");
                    mRequestQueue.add(getBloodyFriendsContacts());
                } else {
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_no_internet));
                }

                break;

            case R.id.menu_sync1:

                if (dh.getPhoneContactsInfo().size() > 0) {
                    AppUtil.showToast(BloodyFriendsActivity.this, "Sync in progress.This will take a few minutes. We will notify you once the process is completed.");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.dialog_sync_confirmation);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            contactResultReceiver = new ContactsResultReceiver(new Handler());
                            startIntentServiceFetchContacts();

                    /*Upshot event capture*/

                            HashMap<String, Object> bkDATA = new HashMap<>();
                            bkDATA.put("BFSync", "Yes");
                            Log.d("BFSync bk data", bkDATA.toString());
                            UpshotEvents.createCustomEvent(bkDATA, 11);

                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            HashMap<String, Object> bkDATA = new HashMap<>();
                            bkDATA.put("BFSync", "No");
                            Log.d("BFSync bk data", bkDATA.toString());
                            UpshotEvents.createCustomEvent(bkDATA, 11);
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(this);
        UserInfoVO mUserInfoVO = null;
        try {
            long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(this);
            mUserInfoVO = mDatabaseHandler.getUserInfo(currentSelectedProfile);
            Log.d(TAG, "NAME=" + mUserInfoVO.getFirstName());

            tv_name.setText(mUserInfoVO.getFirstName() + " " + mUserInfoVO.getLastName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (CommonUtils.getSP(this).getString("bloodGroup", "").length() > 0) {
            for (int i = 0; i < select_bloodgroupcontacts.getCount(); i++) {
                if (select_bloodgroupcontacts.getItemAtPosition(i).equals(CommonUtils.getSP(this).getString("bloodGroup", ""))) {
                    select_bloodgroupcontacts.setSelection(i);
                    tv_bloodgroup.setText(CommonUtils.getSP(this).getString("bloodGroup", "") + (!TextUtils.isEmpty(mUserInfoVO.getPatientHandle())?(" | "+mUserInfoVO.getPatientHandle()):""));
                    tv_bloodgroup.setVisibility(View.VISIBLE);
                    tv_bloodgrouphint.setVisibility(View.GONE);
                }
            }
        } else {
            tv_bloodgroup.setVisibility(View.GONE);
            tv_bloodgrouphint.setVisibility(View.VISIBLE);
        }
    }
}
