package com.patientz.activity;

import android.app.AlertDialog;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.patientz.VO.BloodyFriendVO;
import com.patientz.adapters.AdapterPhoneContactsNew;
import com.patientz.databases.DatabaseHandler;
import com.patientz.service.IntentServiceFetchContacts;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BloodyFriendsAdditionActivity extends BaseActivity {
    private static final String TAG = "BloodyFriendsAdditionActivity";
    ContactsResultReceiver contactResultReceiver;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    DatabaseHandler dh;
    TextView text_count;
    private LinearLayout button_syncnow;
    AdapterPhoneContactsNew mAdapter;
    RequestQueue mRequestQueue;
    EditText editSearch;
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodfriends_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.label_add_bf);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dh = DatabaseHandler.dbInit(BloodyFriendsAdditionActivity.this);
        mRequestQueue = AppVolley.getRequestQueue();

        editSearch = (EditText) findViewById(R.id.edit_search);
        text_count = (TextView) findViewById(R.id.text_count);
        button_syncnow = (LinearLayout) findViewById(R.id.button_syncnow);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //if (dh.getPhoneContactsInfoCount() == 0) {
        startIntentServiceFetchContacts();
//        } else {
//            ArrayList<BloodyFriendVO> bloodyFriendVOArrayList = dh.getAllPhoneContacts();
//            Log.e("getPhoneContactsInfo333", "--> " + dh.getAllPhoneContacts().size());
//            mAdapter = new AdapterPhoneContactsNew(getApplicationContext(), bloodyFriendVOArrayList, text_count);
//            mRecyclerView.setAdapter(mAdapter);
//        }

        /*text_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<BloodyFriendVO> selcontactslist = dh.getSelected_5_SyncPhoneContacts();

                if (selcontactslist.size() > 0) {

                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_sel_sync_contacts_list);

                    ImageView text_close = (ImageView) dialog.findViewById(R.id.text_close);
                    RecyclerView recycler_view = (RecyclerView) dialog.findViewById(R.id.recycler_view);

                    recycler_view.setHasFixedSize(true);
                    RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                    recycler_view.setLayoutManager(mLayoutManager1);
                    recycler_view.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));

                    AdapterPhoneContactsNew mAdapter1 = new AdapterPhoneContactsNew(getApplicationContext(), selcontactslist, text_count);
                    recycler_view.setAdapter(mAdapter1);

                    text_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else {
                    Toast.makeText(BloodyFriendsAdditionActivity.this, R.string.label_sync_hint_sync5, Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        editSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editSearch.getText().toString().toLowerCase(Locale.getDefault()).trim();
                ArrayList<BloodyFriendVO> bloodyFriendVOArrayList = dh.getAllPhoneContactsSearch(text);
                mAdapter = new AdapterPhoneContactsNew(getApplicationContext(), bloodyFriendVOArrayList, text_count);
                mRecyclerView.setAdapter(mAdapter);
                //mAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


        button_syncnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.ADD_BLOODY_FRIENDS_CLICKED, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ADD_BLOODY_FRIENDS_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                final ArrayList<BloodyFriendVO> selcontactslist = dh.getSelected_5_SyncPhoneContacts();

                Log.e("selcontact - > ", "" + selcontactslist.size());

                if (selcontactslist.size() > 0) {

                    if (selcontactslist.size() == 5) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(BloodyFriendsAdditionActivity.this);
                        builder.setMessage(getResources().getString(R.string.label_sync_hint_dialog));
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                for (int i = 0; i < selcontactslist.size(); i++) {
                                    Log.e("selcontact - > ", selcontactslist.get(i).getContact());
                                    if (AppUtil.isOnline(getApplicationContext())) {
                                        mRequestQueue.add(syncContactsFromPhone(selcontactslist.get(i)));
                                    }else
                                    {
                                        AppUtil.showToast(getApplicationContext(),getString(R.string.offlineMode));
                                    }
                                }
                                Log.e("finish...", "finish...");
                                getSharedPreferences("sync_data", 4).edit().clear().commit();
                                mRequestQueue.add(getBloodyFriendsContacts());
                                dialog.cancel();

                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        AppUtil.showToast(BloodyFriendsAdditionActivity.this, getString(R.string.label_sync_hint_sync5));
                    }
                } else {
                    AppUtil.showToast(BloodyFriendsAdditionActivity.this, getString(R.string.plz_sel_contacts));
                }
            }
        });


    }

    protected void startIntentServiceFetchContacts() {
        if (progressDialog == null) {
            progressDialog = CommonUtils.showProgressDialog(BloodyFriendsAdditionActivity.this);
        }
        getSharedPreferences("sync_data", 4).edit().clear().commit();
        contactResultReceiver = new ContactsResultReceiver(new Handler());
        Intent intent = new Intent(BloodyFriendsAdditionActivity.this, IntentServiceFetchContacts.class);
        intent.putExtra(getString(R.string.package_name) + "." + Constant.CONTACTS_RECEIVER, contactResultReceiver);
        startService(intent);
    }


    class ContactsResultReceiver extends ResultReceiver {

        public ContactsResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            Log.d(TAG, "ON RECEIVE CALLED - " + resultCode);

            if (resultCode == 567) {

                if (AppUtil.isOnline(getApplicationContext())) {
                    if (dh.getPhoneContactsInfo().size() > 0) {

                        ArrayList<? extends BloodyFriendVO> mBloodyFriendVOs = resultData.getParcelableArrayList(getString(R.string.package_name) + "." + Constant.RESULT_CONTACTS_KEY);

                        Log.e("getPhoneContactsInfo111", "--> " + mBloodyFriendVOs.size());
                        Log.e("getPhoneContactsInfo222", "--> " + dh.getPhoneContactsInfo().size());

                        text_count.setText(dh.getSelected_5_SyncPhoneContacts().size() + " Selected");
                        mRequestQueue.add(getBloodyFriendsContactsFirst());

                    }
                } else {
                    CommonUtils.dismissProgressDialog(progressDialog);
                    AppUtil.showToast(getApplicationContext(), getString(R.string.network_error));
                }

            }
        }
    }

    private StringRequest syncContactsFromPhone(final BloodyFriendVO bloodyFriendVO) {

        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.addContact;
        Log.e("url----> ", szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response : " + response);

                try {
                    JSONObject jsonRootObject = new JSONObject(response);
                    String contact = jsonRootObject.getString("contact");
                    Log.d(TAG + "-contact-", contact);

                    if (contact.equals(bloodyFriendVO.getContact())) {
                        Log.d("get_id--->", "" + bloodyFriendVO.get_id());
                        int updatevalue = dh.updatePhoneContactsStatus(bloodyFriendVO.get_id());
                        getSharedPreferences("invited_status", 4).edit().putBoolean(bloodyFriendVO.getContact(),true).commit();
                        Log.d(TAG, "updatevalue : " + updatevalue);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error--->", "--------" + error.getMessage());
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AlertDialog.Builder builder = new AlertDialog.Builder(BloodyFriendsAdditionActivity.this);
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
                                }
                            });
                            mAlertDialog.show();
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

                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.put("lookUpId", bloodyFriendVO.getLookUpId());
                    jsonobject.put("contactType", bloodyFriendVO.getContactType());
                    jsonobject.put("contact", bloodyFriendVO.getContact());
                    jsonobject.put("contactName", bloodyFriendVO.getContactName());
                    jsonobject.put("contactId", bloodyFriendVO.getContactId());
                    jsonobject.put("userInvited", true);

                    Locale current_language = getResources().getConfiguration().locale;
                    Log.d("current locale--->", current_language.getLanguage());

                    SharedPreferences sharedPreferences = getSharedPreferences(Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
                    String deepLink = sharedPreferences.getString(Constant.DYNAMIC_LINK_2, "");

                    if (current_language.getLanguage().equals("en")) {
                        jsonobject.put("deepLink", deepLink);
                    } else {
                        String send_message1 = getString(R.string.bloodyfriends_invite_message).replace("deepLink", deepLink);
                        String send_message2 = send_message1.replace("friend1", "@friend1");
                        String send_message = send_message2.replace("friend2", "@friend2");
                        jsonobject.put("message", send_message);
                    }

                    //String send_message = "@friend1 and @friend2 are now part of Bloody Friends Network. Do you know who among your personal friends can donate blood to you if needed! You can now build your list of Bloody Friends with one click on /\"Sync/\" button in CallAmbulance App. Click @deepLink to download the app now and Be Safe!";
                    Log.d("json--->", jsonobject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put("contact", jsonobject.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bloody_contacts_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_reset:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

                        if (contacts.size() != 0) {
                            if (dh.getPhoneContactsInfoCount() > 0) {
                                Log.d(TAG, " update = " + dh.getPhoneContactsInfoCount());
                                dh.updatePhoneContactsDetails(contacts);
                            } else {
                                Log.d(TAG, " insert = " + dh.getPhoneContactsInfoCount());
                                dh.insertPhoneContactsInfoDUPLICATE(contacts);
                            }
//                            setContactsData();
                        } else {
                            AppUtil.showToast(getApplicationContext(), getString(R.string.networkError));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppUtil.showToast(BloodyFriendsAdditionActivity.this, getString(R.string.server_error));
                    }
                }

                final ArrayList<BloodyFriendVO> selcontactslist = dh.getSelected_5_SyncPhoneContacts();

                SharedPreferences sharedPreferences = getSharedPreferences(Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
                String deepLink = sharedPreferences.getString(Constant.DYNAMIC_LINK_2, "");

                String send_sms = "I want to know ur blood group.Download CallAmbulance @ " + deepLink + " to know if we can donate blood to each other in an emergency";
                Log.d(TAG, "send_sms=" + send_sms);

                for (int i = 0; i < selcontactslist.size(); i++) {
                    //  if (!dh.checkInvitedStatus(selcontactslist.get(i).get_id())) {
                    //  Log.e("Not Exist - > ", selcontactslist.get(i).getContact());
                    AppUtil.sendSMS(BloodyFriendsAdditionActivity.this, selcontactslist.get(i).getContact(), send_sms);
//                    } else {
//                        Log.e("Exist - > ", selcontactslist.get(i).getContact());
//                    }
                }

                ArrayList<BloodyFriendVO> bloodyFriendVOArrayList = dh.getAllPhoneContacts();
                mAdapter = new AdapterPhoneContactsNew(getApplicationContext(), bloodyFriendVOArrayList, text_count);
                mRecyclerView.setAdapter(mAdapter);

//                if (dh.getPhoneContactsInfo().size() > 0) {
//                    startService(new Intent(BloodyFriendsAdditionActivity.this, BloodyFriendsSyncService.class));
//                }

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AlertDialog.Builder builder = new AlertDialog.Builder(BloodyFriendsAdditionActivity.this);
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
                                }
                            });
                            mAlertDialog.show();
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

    private StringRequest getBloodyFriendsContactsFirst() {

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

                        if (contacts.size() != 0) {
                            if (dh.getPhoneContactsInfoCount() > 0) {
                                Log.d(TAG, " update = " + dh.getPhoneContactsInfoCount());
                                dh.updatePhoneContactsDetails(contacts);
                            } else {
                                Log.d(TAG, " insert = " + dh.getPhoneContactsInfoCount());
                                dh.insertPhoneContactsInfoDUPLICATE(contacts);
                            }
//                            setContactsData();
                        } else {
                            AppUtil.showToast(getApplicationContext(), getString(R.string.networkError));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppUtil.showToast(BloodyFriendsAdditionActivity.this, getString(R.string.server_error));
                    }
                }

                ArrayList<BloodyFriendVO> bloodyFriendVOArrayList = dh.getAllPhoneContacts();
                mAdapter = new AdapterPhoneContactsNew(getApplicationContext(), bloodyFriendVOArrayList, text_count);
                mRecyclerView.setAdapter(mAdapter);

                CommonUtils.dismissProgressDialog(progressDialog);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtils.dismissProgressDialog(progressDialog);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AlertDialog.Builder builder = new AlertDialog.Builder(BloodyFriendsAdditionActivity.this);
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
                                }
                            });
                            mAlertDialog.show();
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
}
