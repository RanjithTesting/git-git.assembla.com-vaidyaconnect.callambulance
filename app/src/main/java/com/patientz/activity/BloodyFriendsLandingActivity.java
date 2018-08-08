package com.patientz.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.BloodyFriendVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

public class BloodyFriendsLandingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BloodBuddiesActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    LinearLayout ll_sendrequest;
    SharedPreferences spnfs;
    SharedPreferences.Editor spedit;
    DatabaseHandler dh;
    RequestQueue mRequestQueue;
    public static ArrayList<BloodyFriendVO> selcontactslist;
    TextView tv_name, tv_bloodgroup, tv_bloodgrouphint;
    private static final int REQUEST_READ_CONTACTS = 0;
    private TextView tvAddBloodyFriends;
    private LinearLayout btAddBloodyFriends;
    public static final int REQUEST_CODE_GET_SYNCED_CONTACTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodfriends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.label_bloodbuddies);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dh = DatabaseHandler.dbInit(BloodyFriendsLandingActivity.this);
        selcontactslist = new ArrayList<BloodyFriendVO>();
        spnfs = getSharedPreferences("blood_request_details", 4);
        spedit = spnfs.edit();
        tv_bloodgroup = (TextView) findViewById(R.id.tv_bloodgroup);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_bloodgrouphint = (TextView) findViewById(R.id.tv_bloodgrouphint);
        tvAddBloodyFriends = (TextView) findViewById(R.id.tv_add_bloody_friends);
        btAddBloodyFriends = (LinearLayout) findViewById(R.id.bt_add_bloody_friends);
        btAddBloodyFriends.setOnClickListener(this);
        tv_bloodgrouphint.setOnClickListener(this);
        mRequestQueue=AppVolley.getRequestQueue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRequestQueue.add(getBloodyFriendsContacts());
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
            tv_bloodgroup.setText(CommonUtils.getSP(this).getString("bloodGroup", "") + " | " + mUserInfoVO.getPatientHandle());
            tv_bloodgroup.setVisibility(View.VISIBLE);
            tv_bloodgrouphint.setVisibility(View.GONE);
        } else {
            tv_bloodgroup.setVisibility(View.GONE);
            tv_bloodgrouphint.setVisibility(View.VISIBLE);
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
                if (!TextUtils.isEmpty(response)) {
                    try {
                        Type collectionType = new TypeToken<Collection<BloodyFriendVO>>() {
                        }.getType();
                        ArrayList<BloodyFriendVO> contacts = AppUtil.getGson().fromJson(response, collectionType);
                        Log.d("RESPONSE=",AppUtil.convertToJsonString(contacts));

                        if (contacts.size() != 0 ) {

                            if (dh.getPhoneContactsInfoCount() > 0) {
                                Log.d(TAG,"updating contacts");
                                dh.updatePhoneContactsDetails(contacts);
                            } else {
                                Log.d(TAG,"inserting contacts");
                                dh.insertPhoneContactsInfoDUPLICATE(contacts);
                            }
                            ArrayList<BloodyFriendVO> contacts_list = dh.getPhoneContactsInfoShow(CommonUtils.getSP(getApplicationContext()).getString("bloodGroup", ""));
                            Log.d(TAG,"contacts_list size="+contacts_list.size());
                            if(response.contains(CommonUtils.getSP(getApplicationContext()).getString("bloodGroup", "")))
                            {
                                tvAddBloodyFriends.setText(CommonUtils.getSP(getApplicationContext()).getString("bloodGroup", "")+" Blood "+contacts_list.size()+" \ncontacts available");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppUtil.showToast(BloodyFriendsLandingActivity.this, getString(R.string.server_error));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(BloodyFriendsLandingActivity.this);
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getApplicationContext(),error);
                }            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        return mRequest;
    }

    private boolean mayRequestContacts() {
        Log.d(TAG,"mayRequestContacts");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Log.d(TAG,"IN shouldShowRequestPermissionRationale");
            Snackbar.make(ll_sendrequest, getString(R.string.permission_rationale), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.OK), new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt_add_bloody_friends:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                    HashMap<String, Object> upshotData = new HashMap<>();
                    upshotData.put(Constant.UpshotEvents.BLOODY_FRIENDS_LANDING_PAGE_ADD_CLICKED,true);
                    UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.BLOODY_FRIENDS_LANDING_PAGE_ADD_CLICKED);
                    Log.d(TAG,"upshot data="+upshotData.entrySet());
                    callNextScreen();
                } else {
                    ActivityCompat.requestPermissions(
                            this, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                            Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_BLOODY_FRIENDS);
                }
                break;
            case R.id.tv_bloodgrouphint:
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.BLOODY_FRIENDS_LANDING_PAGE_UPDATE_BLOOD_GROUP_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.BLOODY_FRIENDS_LANDING_PAGE_UPDATE_BLOOD_GROUP_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                Intent mIntent = new Intent(this, ActivityEditProfile.class);
                mIntent.putExtra("key", "edit");
                startActivity(mIntent);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_BLOODY_FRIENDS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "PERMISSION GRANTED");
                    Intent aboutActivity = new Intent(this, BloodyFriendsLandingActivity.class);
                    startActivity(aboutActivity);
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(BloodyFriendsLandingActivity.this,
                        android.Manifest.permission.GET_ACCOUNTS)) {
                    Log.d(TAG, "PERMISSION NOT GRANTED/NEVER ASK CLICKED");
                    AppUtil.showToast(getApplicationContext(), getString(R.string.toast_switch_on_permissions));
                    AppUtil.launchSettingPermissionScreen(BloodyFriendsLandingActivity.this);
                } else {
                    //showDialogWithMoreSpecificPermissionInfo(getString(R.string.permission_contacts_msg), new String[]{Manifest.permission.GET_ACCOUNTS}, Constant.PERMISSIONS_REQUEST_CODE_CONTACTS_BLOODY_FRIENDS);
                }
                break;
        }
    }

    public void callNextScreen()
    {
        ArrayList<BloodyFriendVO> contacts_list = dh.getPhoneContactsInfoShow(CommonUtils.getSP(getApplicationContext()).getString("bloodGroup", ""));
        Log.d(TAG, "BloodyFriendsCount=" + contacts_list.size());
        if (contacts_list.size() > 0) {
            startActivity(new Intent(BloodyFriendsLandingActivity.this, BloodyFriendsListActivity.class));
        } else {
            startActivityForResult(new Intent(BloodyFriendsLandingActivity.this, BloodyFriendsAdditionActivity.class),REQUEST_CODE_GET_SYNCED_CONTACTS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_SYNCED_CONTACTS) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    private void launchSettingPermissionScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.permission_grant_msg);
        builder.setTitle("");
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //   AppUtil.showDialog(this,getString(R.string.permission_grant_msg));
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
