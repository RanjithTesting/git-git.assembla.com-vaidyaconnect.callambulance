package com.patientz.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.patientz.VO.BloodyFriendVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.adapters.BloodyFriendsListAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.fragments.BloodRequestConfirmationDialog;
import com.patientz.utils.AppUtil;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.DatePickerFragment;
import com.patientz.utils.DividerItemDecoration;
import com.patientz.utils.Log;
import com.patientz.utils.TimePickerFragment;
import com.patientz.utils.Validator;

import java.util.ArrayList;

public class BloodyFriendsListActivity extends BaseActivity  implements View.OnClickListener,BloodRequestConfirmationDialog.ButtonsClickCallBack {
    private static final String TAG = "BloodBuddiesActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    LinearLayout ll_sendrequest;
    TextView select_bloodgroupcontacts;
    // TextView tv_hint;
    SharedPreferences spnfs;
    SharedPreferences.Editor spedit;
    DatabaseHandler dh;
    RequestQueue mRequestQueue;
    public static ArrayList<BloodyFriendVO> selcontactslist;
    CheckBox check_select_all;
    TextView tv_contacts_count, tv_name, tv_bloodgroup, tv_bloodgrouphint;
    private static final int REQUEST_READ_CONTACTS = 0;

    private TextView tvName;
    private TextView tvBloodgroup;
    private LinearLayout tvAddMoreBf,btSendBloodRequest;
    private void findViews() {
        tvName = (TextView)findViewById( R.id.tv_name);
        tvBloodgroup = (TextView)findViewById( R.id.tv_bloodgroup );
        tvAddMoreBf = (LinearLayout)findViewById( R.id.tv_add_more_bf );
        btSendBloodRequest = (LinearLayout)findViewById( R.id.bt_send_blood_request );
        mRecyclerView = (RecyclerView)findViewById( R.id.recycler_view );
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        tvAddMoreBf.setOnClickListener(this);
        btSendBloodRequest.setOnClickListener(this);
        tv_bloodgrouphint = (TextView) findViewById(R.id.tv_bloodgrouphint);
        tv_bloodgrouphint.setOnClickListener(this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloody_friends_list_screen_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.my_bloody_friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        if (CommonUtils.getSP(this).getString("bloodGroup", "").length() > 0) {
            tv_bloodgrouphint.setVisibility(View.GONE);
        } else {
            tv_bloodgrouphint.setVisibility(View.VISIBLE);
        }

        UserInfoVO mUserInfoVO = null;
        try {
            long currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(this);
            mUserInfoVO = dh.getUserInfo(currentSelectedProfile);
            tvName.setText(mUserInfoVO.getFirstName() + " " + mUserInfoVO.getLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CommonUtils.getSP(this).getString("bloodGroup", "").length() > 0) {
            tvBloodgroup.setText(CommonUtils.getSP(this).getString("bloodGroup", "") + " | " + mUserInfoVO.getPatientHandle());
        }

        ArrayList<BloodyFriendVO> bloodyFriendVOs= dh.getPhoneContactsInfoShow(CommonUtils.getSP(this).getString("bloodGroup", "").trim());

        BloodyFriendsListAdapter mAdapter = new BloodyFriendsListAdapter(getApplicationContext(), bloodyFriendVOs);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.tv_add_more_bf:
                startActivity(new Intent(BloodyFriendsListActivity.this, BloodyFriendsAdditionActivity.class));
                break;
            case R.id.bt_send_blood_request:
                sendBloodRequest();
                break;
            case R.id.tv_bloodgrouphint:
                Intent mIntent = new Intent(this, ActivityEditProfile.class);
                mIntent.putExtra("key", "edit");
                startActivity(mIntent);

                break;
        }
        }


    private void sendBloodRequest() {
        final Dialog dialogpopup = new Dialog(BloodyFriendsListActivity.this);
        dialogpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogpopup.setContentView(R.layout.blood_request_details_screen_layout);
        dialogpopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_blood_group = (TextView) dialogpopup.findViewById(R.id.tv_blood_group);
        final EditText editName = (EditText) dialogpopup.findViewById(R.id.edit_name);
        final Spinner spinnerCountrycodes = (Spinner) dialogpopup.findViewById(R.id.spinner_countrycodes);
        final EditText editPhno = (EditText) dialogpopup.findViewById(R.id.edit_phno);
        final Spinner spinnerNoofunits = (Spinner) dialogpopup.findViewById(R.id.spinner_noofunits);
        EditText editmessage = (EditText) dialogpopup.findViewById(R.id.editmessage);
        final TextView textDate = (TextView) dialogpopup.findViewById(R.id.text_date);
        ImageView imageDate = (ImageView) dialogpopup.findViewById(R.id.image_date);
        final TextView textTime = (TextView) dialogpopup.findViewById(R.id.text_time);
        ImageView imgTime = (ImageView) dialogpopup.findViewById(R.id.img_time);
        final EditText editLocation = (EditText) dialogpopup.findViewById(R.id.edit_location);
        final EditText editPurpose = (EditText) dialogpopup.findViewById(R.id.edit_purpose);
        TextView tvAcceptButton = (TextView) dialogpopup.findViewById(R.id.tv_accept_button);
        TextView tvCancelButton = (TextView) dialogpopup.findViewById(R.id.tv_cancel_button);

        if (CommonUtils.getSP(this).getString("bloodGroup", "").trim().length() != 0) {
            tv_blood_group.setText(getString(R.string.reqfor) + " " + CommonUtils.getSP(this).getString("bloodGroup", "").toString() + " " + getString(R.string.blood));
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

                AlertDialog.Builder builder = new AlertDialog.Builder(BloodyFriendsListActivity.this);
                final ArrayList<BloodyFriendVO> selcontactslist = dh.getPhoneContactsInfoShow(CommonUtils.getSP(getApplicationContext()).getString("bloodGroup", ""));
                if (selcontactslist.size() <= 1) {
                    builder.setMessage("You are about to request for blood " + CommonUtils.getSP(BloodyFriendsListActivity.this).getString("bloodGroup", "").trim().trim() + " to " + selcontactslist.size() + " contact via SMS. charges apply as per your carrier.");
                } else {
                    builder.setMessage("You are about to request for blood " + CommonUtils.getSP(BloodyFriendsListActivity.this).getString("bloodGroup", "").trim().trim() + " to " + selcontactslist.size() + " contacts via SMS. charges apply as per your carrier.");
                }
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        long currentSelectedUserId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                        String sender_name = "";

                        if (AppUtil.checkLoginDetails(BloodyFriendsListActivity.this)) {
                            try {
                                PatientUserVO mPatientUserVO = dh.getProfile(currentSelectedUserId);
                                if (mPatientUserVO != null && !mPatientUserVO.equals("")) {
                                    sender_name = mPatientUserVO.getFirstName();
                                } else {
                                    sender_name = editPhno.getText().toString().trim();
                                }
                            } catch (Exception e) {
                                sender_name = editPhno.getText().toString().trim();
                            }
                        } else {
                            AppUtil.callLoginScreen(getApplicationContext());
                            finish();
                        }

                        String send_req_msg = "Hi, " + editName.getText().toString().trim()
                                + " is in need of " + spinnerNoofunits.getSelectedItem().toString().trim()
                                + " units of " + CommonUtils.getSP(BloodyFriendsListActivity.this).getString("bloodGroup", "").trim()
                                + " blood for a " + editPurpose.getText().toString().trim()
                                + " at " + editLocation.getText().toString().trim()
                                + " at " + textTime.getText().toString().trim()
                                + " on " + textDate.getText().toString().trim()
                                + ". Please help save a life. Contact " + editPhno.getText().toString().trim()
                                + ".\nRegards,\n" + sender_name;

Log.d(TAG,"SMS="+send_req_msg);
                        for (int i = 0; i < selcontactslist.size(); i++) {
                            Log.d(TAG,"SMS SENT");
                            AppUtil.sendSMS(BloodyFriendsListActivity.this, selcontactslist.get(i).getContact(), send_req_msg);
                        }

                        dialog.cancel();
                        Log.e("selcontactslist", "--> " + selcontactslist.size() + " & send_req_msg " + send_req_msg);
                        dialogpopup.dismiss();
                        BloodRequestConfirmationDialog mConfirmationDialog=new BloodRequestConfirmationDialog();
                        mConfirmationDialog.show(getSupportFragmentManager(),"");
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

dialogpopup.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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

    }

    @Override
    public void button1Clicked() {
        startActivity(new Intent(BloodyFriendsListActivity.this, BloodyFriendsLandingActivity.class));

    }
}