package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.OrganisationVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.upshot.UpshotManager;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.BadgeDrawable;
import com.patientz.utils.CircleTransform;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactsActivity extends BaseActivity implements View.OnClickListener{
    public final static int REQUEST_CODE_ADD_CONTACT = 786;
    private static final String TAG = "ContactsActivity";
    private RecyclerView mCareGiverRecyclerView, mEmergencyContactRecyclerView, mHospitalsRecyclerView, doctorsRecyclerView, recyclerViewOrgTypeSchool;
    private RecyclerView.Adapter mAdapterCareGiver, mAdapterEmergencyContact, mAdapterHospitals, mAdapterDoctors;
    private RecyclerView.LayoutManager mLayoutManager, mLayoutManager2, mLayoutManager3, mLayoutManager4;
    private Boolean isFabOpen = false;
    private TextView titleCareGiver, titleEmergencyContact, titleNotifyingDoctor, favCareGiverIcon, fabECIcon, fabHospitalsIcon;
    private View mCareTeamView;
    private View mLoaderStatusView;
    private LoadDataAsync loadDataAsync;
    private FloatingActionsMenu floatingActionButton;
    private DatabaseHandler mDatabaseHandler;
    long currentSelectedPatientId;

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
        Log.d(TAG, "count=" + count);

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(String.valueOf(count));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_v2);
         mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_contacts);
        setSupportActionBar(toolbar);
        currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());


        mCareTeamView = findViewById(R.id.careTeam);
        mLoaderStatusView = findViewById(R.id.loading_status);
        titleCareGiver = (TextView) findViewById(R.id.title_care_giver);
        titleCareGiver.setOnClickListener(this);
        titleEmergencyContact = (TextView) findViewById(R.id.title_emergency_contact);
        titleEmergencyContact.setOnClickListener(this);
        titleNotifyingDoctor = (TextView) findViewById(R.id.tv_my_doctors);
        titleNotifyingDoctor.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCareGiverRecyclerView = (RecyclerView) findViewById(R.id.care_giver_recycler_view);
        mEmergencyContactRecyclerView = (RecyclerView) findViewById(R.id.emergency_contact_recycler_view);
        mHospitalsRecyclerView = (RecyclerView) findViewById(R.id.hospital_recycler_view);
        doctorsRecyclerView = (RecyclerView) findViewById(R.id.doctors_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mCareGiverRecyclerView.setHasFixedSize(true);
        mEmergencyContactRecyclerView.setHasFixedSize(true);
        mHospitalsRecyclerView.setHasFixedSize(true);
        doctorsRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager3 = new LinearLayoutManager(this);
        mLayoutManager4 = new LinearLayoutManager(this);

        mCareGiverRecyclerView.setLayoutManager(mLayoutManager);
        mEmergencyContactRecyclerView.setLayoutManager(mLayoutManager2);
        mHospitalsRecyclerView.setLayoutManager(mLayoutManager3);
        doctorsRecyclerView.setLayoutManager(mLayoutManager4);
        registerForContextMenu(mCareGiverRecyclerView);
        registerForContextMenu(mEmergencyContactRecyclerView);
        registerForContextMenu(mHospitalsRecyclerView);
        registerForContextMenu(doctorsRecyclerView);


        // specify an adapter (see also next example)
        floatingButtonUI();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_item_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);

    }

    private void floatingButtonUI() {
        floatingActionButton = (FloatingActionsMenu) findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.VISIBLE);
        com.getbase.floatingactionbutton.FloatingActionButton careGiver = new com.getbase.floatingactionbutton.FloatingActionButton(this);
        careGiver.setTitle(getString(R.string.fab_care_giver_title));
        careGiver.setId(R.id.fab3);
        careGiver.setOnClickListener(this);
        careGiver.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_NORMAL);
        careGiver.setColorNormal(getResources().getColor(R.color.colorFAB3));
        careGiver.setIconDrawable(getResources().getDrawable(R.drawable.plus_caregiver));
        floatingActionButton.addButton(careGiver);
        com.getbase.floatingactionbutton.FloatingActionButton ec = new com.getbase.floatingactionbutton.FloatingActionButton(this);
        ec.setTitle(getString(R.string.filter_emergency_contact));
        ec.setId(R.id.fab2);
        ec.setOnClickListener(this);
        ec.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_NORMAL);
        ec.setColorNormal(getResources().getColor(R.color.colorFAB2));
        ec.setIconDrawable(getResources().getDrawable(R.drawable.plus_emcontact));
        floatingActionButton.addButton(ec);
        com.getbase.floatingactionbutton.FloatingActionButton hospitals = new com.getbase.floatingactionbutton.FloatingActionButton(this);
        hospitals.setTitle(getString(R.string.title_hospitals));
        hospitals.setId(R.id.fab1);
        hospitals.setOnClickListener(this);
        hospitals.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_NORMAL);
        hospitals.setColorNormal(getResources().getColor(R.color.colorFAB1));
        hospitals.setIconDrawable(getResources().getDrawable(R.drawable.plus_hospitals));
        floatingActionButton.addButton(hospitals);
        com.getbase.floatingactionbutton.FloatingActionButton notifyDoctor = new com.getbase.floatingactionbutton.FloatingActionButton(this);
        notifyDoctor.setTitle(getString(R.string.title_notify_doctor));
        notifyDoctor.setId(R.id.fab4);
        notifyDoctor.setOnClickListener(this);
        notifyDoctor.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_NORMAL);
        notifyDoctor.setColorNormal(getResources().getColor(R.color.colorFAB1));
        notifyDoctor.setIconDrawable(getResources().getDrawable(R.drawable.plus_hospitals));
        floatingActionButton.addButton(notifyDoctor);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        HashMap<String, Object> upshotData = new HashMap<>();

        switch (id) {
           /* case R.id.fab:
                animateFAB();
                break;*/
            case R.id.fab4:
                floatingActionButton.collapseImmediately();
                upshotData.put(Constant.UpshotEvents.CT_MY_DOCTORS_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_MY_DOCTORS_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                try {
                    ArrayList<OrganisationVO> orgsList = mDatabaseHandler.getAllHospitalsAndClinic(currentSelectedPatientId);
                    if (orgsList.size() > 0) {
                        Intent intentNotifyDoctor = new Intent(this, NotifyDoctorActivity.class);
                        startActivity(intentNotifyDoctor);
                    } else {
                        AppUtil.showToast(getApplicationContext(), getString(R.string.label_no_hospitals_set));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fab3:
                upshotData.put(Constant.UpshotEvents.CT_CG_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_CG_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                floatingActionButton.collapseImmediately();
                Intent intent = new Intent(this, SearchCareGiver.class);
                startActivity(intent);
                break;
            case R.id.fab2:
                upshotData.put(Constant.UpshotEvents.CT_EC_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_EC_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                floatingActionButton.collapseImmediately();
                Intent intent2 = new Intent(this, SearchEmergencyContact.class);
                startActivity(intent2);
                break;
            case R.id.fab1:
                upshotData.put(Constant.UpshotEvents.CT_ORGANISATION_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_ORGANISATION_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                floatingActionButton.collapseImmediately();
                Intent intent3 = new Intent(this, SearchHospitalsAndOrganisationsActivity.class);
                startActivity(intent3);
                break;

            case R.id.title_care_giver:
                AppUtil.showToast(getApplicationContext(), getString(R.string.help_care_giver_msg));

                break;
            case R.id.title_emergency_contact:
                AppUtil.showToast(getApplicationContext(), getString(R.string.help_ec_msg));

                break;
            case R.id.title_hospitals:
                AppUtil.showToast(getApplicationContext(), getString(R.string.help_hospital_msg));

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent=getIntent();
        try {
            ArrayList<EmergencyContactsVO> ecList = mDatabaseHandler.getEmergencyContacts(currentSelectedPatientId, Constant.EMERGENCY_CONTACT);
            ArrayList<EmergencyContactsVO> careTeamList = mDatabaseHandler.getEmergencyContacts(currentSelectedPatientId, Constant.CARE_GIVER);
            if (ecList.size()==0 && careTeamList.size()== 0) {
                if(mIntent!=null) {
                    if (mIntent.getStringExtra("source") != null) {
                        if (mIntent.getStringExtra("source").equalsIgnoreCase(getString(R.string.lable_emergency_prepardness))) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
                            builder.setMessage(R.string.emergency_preparedness_dialog_msg);
                            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadDataAsync = new LoadDataAsync();
        loadDataAsync.execute();
    }

    private StringRequest createDeleteContactRequest(final long patientAccessId, final long orgId, final int position, final ArrayList<EmergencyContactsVO> mEmergencyContactsVOs, final ArrayList<OrganisationVO> orgsList) {
        String url=null;
        Log.d(TAG,"ORG ID="+orgId);
        if(orgId==0)
        {
             url= WebServiceUrls.serverUrl + WebServiceUrls.deleteContacts +"patientAccessId="+patientAccessId+"&patientId="+currentSelectedPatientId;
        }else
        {
            url= WebServiceUrls.serverUrl + WebServiceUrls.deleteContacts +"patientId="+currentSelectedPatientId+"&orgId="+orgId;
        }
        Log.d(TAG, "url = " + url);
        StringRequest mRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, WebServiceUrls.deleteContacts + " response = " + response);
                    if (orgId == 0) {
                        mDatabaseHandler.deleteContact(patientAccessId);
                            if (mEmergencyContactsVOs.get(position).getRole().equals(Constant.EMERGENCY_CONTACT)) {
                                mEmergencyContactsVOs.remove(position);
                                mAdapterEmergencyContact.notifyDataSetChanged();
                            } else if (mEmergencyContactsVOs.get(position).getRole().equals(Constant.CARE_GIVER)) {
                                mEmergencyContactsVOs.remove(position);
                                mAdapterCareGiver.notifyDataSetChanged();
                            } else {
                                mEmergencyContactsVOs.remove(position);
                                mAdapterDoctors.notifyDataSetChanged();
                            }
                    } else {
                        mDatabaseHandler.deleteOrganisation(orgId);
                        orgsList.remove(position);
                        mAdapterHospitals.notifyDataSetChanged();
                    }
                    AppUtil.showToast(getApplicationContext(), response);
                showProgress(false);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                AppUtil.showToast(ContactsActivity.this, getString(R.string.failed_to_delete_contact));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }

        };
        return mRequest;
    }

    public void setCallButton(EmergencyContactsVO contactsVO,
                              MyAdapter.ViewHolder holder) {
        final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(contactsVO.getPhoneNumber())) {
            nameValuePairs.add(new BasicNameValuePair("Mobile: ", contactsVO
                    .getPhoneNumber()));
        }
        if (nameValuePairs.isEmpty() == false) {
            int i = 0;
            CharSequence[] cs = new String[nameValuePairs.size()];
            while (i != nameValuePairs.size()) {
                cs[i] = nameValuePairs.get(i).getName()
                        + nameValuePairs.get(i).getValue();
                i++;
            }
            final CharSequence[] items = cs;
            holder.callImageView.setVisibility(View.VISIBLE);
            holder.callImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            ContactsActivity.this);
                    builder.setTitle("Call Number: ");
                    builder.setItems(items,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int item) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            nameValuePairs.get(item).getValue(),
                                            Toast.LENGTH_SHORT).show();
                                    try {
                                        String phonee = "tel:"
                                                + nameValuePairs.get(item)
                                                .getValue();
                                        Intent intent = new Intent(
                                                Intent.ACTION_CALL, Uri
                                                .parse(phonee));
                                        ContactsActivity.this.startActivity(intent);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).show();
                }
            });

        } else {
            holder.callImageView.setVisibility(View.INVISIBLE);
        }

    }

    private void setOrgImage(OrganisationVO organisationVO, ImageView imageView) {
        boolean isFileExists = false;
        if (organisationVO != null) {
            isFileExists = AppUtil.checkIfFileExists(getApplicationContext(),
                    organisationVO.getOrgName());
            Log.d("ContactsActivity", "File Exists ???" + isFileExists);

            if (!isFileExists) {
                if (organisationVO.getOrgLogoId() > 0 && AppUtil.isOnline(getApplicationContext())) {
                    // call webservice to download the image if exists
                    String imageUrl = WebServiceUrls.getPatientAttachment + organisationVO.getOrgLogoId()+"&patientId="+currentSelectedPatientId+"&moduleType="+Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
                    AppUtil.downloadImage(getApplicationContext(), imageUrl,
                            imageView, organisationVO.getOrgName());
                } else {
                    // Picasso.with(this).load(R.drawable.app_icon).transform(new CircleTransform()).into(imageView);
                }
            } else {
                String localPath = Environment.getExternalStorageDirectory()
                        .getPath()
                        + getResources().getString(
                        R.string.profileImagePath);
                File f = new File(localPath + organisationVO.getOrgName());
                Picasso.with(this).load(f).transform(new CircleTransform()).into(imageView);
            }
        }
    }

    private void setProfileImage(PatientUserVO mPatientUserVO, ImageView imageView) {
        boolean isFileExists = false;
        if (mPatientUserVO != null) {
            isFileExists = AppUtil.checkIfFileExists(getApplicationContext(),
                    mPatientUserVO.getPicName());
            Log.d("ContactsActivity", "File Exists ???" + isFileExists);

            if (!isFileExists) {
                if (mPatientUserVO.getPicId() > 0 && AppUtil.isOnline(getApplicationContext())) {
                    // call webservice to download the image if exists
                    String imageUrl = WebServiceUrls.getPatientAttachment + mPatientUserVO.getPicId()+"&patientId="+mPatientUserVO.getPatientId()+"&moduleType="+Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
                    AppUtil.downloadImage(getApplicationContext(), imageUrl,
                            imageView, mPatientUserVO.getPicName());
                } else {
                    Picasso.with(this).load(R.drawable.default_profile).transform(new CircleTransform()).into(imageView);
                }
            } else {
                String localPath = Environment.getExternalStorageDirectory()
                        .getPath()
                        + getResources().getString(
                        R.string.profileImagePath);
                File f = new File(localPath + mPatientUserVO.getPicName());
                Picasso.with(this).load(f).transform(new CircleTransform()).into(imageView);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "requestCode : " + requestCode + " , result code : " + resultCode);
        if (requestCode == REQUEST_CODE_ADD_CONTACT) {
            if (resultCode == RESULT_OK) {
                Log.d("onActivityResult", "Contact selected  : " + data.getDataString());
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String phoneNo = cursor.getString(phoneIndex);
                String name = cursor.getString(nameIndex);

                Log.d("onActivityResult", "ZZZ number : " + phoneNo + " , name : " + name);
                Intent intent = new Intent(this, AddNewContactActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("number", phoneNo);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Shows the progress UI and hides the list.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
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

            mCareTeamView.setVisibility(View.VISIBLE);
            mCareTeamView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mCareTeamView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCareTeamView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.careteam_menu, menu);
        MenuItem itemCart = menu.findItem(R.id.badge);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        int invitationCount = 0;
        try {
            ArrayList<PatientUserVO> invitations = getInvitations();
            if (invitations.size() > 0) {
                invitationCount = invitations.size();
            }

        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION=" + e.getMessage());
        }
        setBadgeCount(this, icon, invitationCount);
        return true;
    }

    public ArrayList<PatientUserVO> getInvitations() throws Exception {
        long currentSelectedUserId = AppUtil.getCurrentSelectedUserId(getApplicationContext());
        Log.d(TAG, "Querying for user id = " + currentSelectedUserId);
        ArrayList<PatientUserVO> invitationList = mDatabaseHandler.getAllInvitationsForUser(currentSelectedUserId);
        Log.d(TAG, "NO OF INVITATIONS=" + invitationList.size());
        return invitationList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.badge) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.CT_INVITATION_ICON_CLICKED,true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_INVITATION_ICON_CLICKED);
            Log.d(TAG,"upshot data="+upshotData.entrySet());
            startActivity(new Intent(ContactsActivity.this, ActivityInvitation.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadDataAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            PatientUserVO patientUserVO = null;
            try {
                patientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
                ArrayList<EmergencyContactsVO> contactsVOs = null;
                try {
                    contactsVOs = mDatabaseHandler.getAllContacts(patientUserVO.getPatientId(), "Care Giver");
                    ArrayList<EmergencyContactsVO> emergencyContactsList=mDatabaseHandler.getAllContacts(patientUserVO.getPatientId(), "Emergency Contact");
                    HashMap<String, Object> CGbkData = new HashMap<>();
                    HashMap<String, Object> ECbkData = new HashMap<>();
                    CGbkData.put("CareGivers", contactsVOs.size() == 0 ? "No" : "Yes");
                    ECbkData.put("EmergencyCOntacts", emergencyContactsList.size() == 0 ? "No" : "Yes");
                    UpshotManager.createCareGiversCustomEvent(CGbkData);
                    UpshotManager.createEmergencyContactsCustomEvent(ECbkData);
                    mAdapterCareGiver = new MyAdapter(getApplicationContext(), contactsVOs);
                    mAdapterEmergencyContact = new MyAdapter(getApplicationContext(),emergencyContactsList);
                    mAdapterDoctors = new MyAdapter(getApplicationContext(), mDatabaseHandler.getAllContacts(patientUserVO.getPatientId(), Constant.NOTIFYING_DOCTOR));
                    mAdapterHospitals = new MyHospitalAdapter(getApplicationContext(), mDatabaseHandler.getAllOrgs(patientUserVO.getPatientId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCareGiverRecyclerView.setAdapter(mAdapterCareGiver);
            mEmergencyContactRecyclerView.setAdapter(mAdapterEmergencyContact);
            mHospitalsRecyclerView.setAdapter(mAdapterHospitals);
            doctorsRecyclerView.setAdapter(mAdapterDoctors);

            showProgress(false);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<EmergencyContactsVO> contactsVOs;
        private Context context;

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, ArrayList<EmergencyContactsVO> contactsVOs) {
            this.contactsVOs = contactsVOs;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.contacts_row_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final EmergencyContactsVO emergencyContactsVO = contactsVOs.get(position);
            holder.mTextFirstLine.setText(AppUtil.convertToCamelCase(emergencyContactsVO.getFirstName()) + " "
                    + AppUtil.convertToCamelCase(emergencyContactsVO.getLastName()));
            if (emergencyContactsVO.getStatus().equalsIgnoreCase("Pending")) {
                holder.mTextSecondLine.setText(emergencyContactsVO.getRelationship() + " Request " + emergencyContactsVO.getStatus());
            } else {
                holder.mTextSecondLine.setText(emergencyContactsVO.getRelationship());
            }
            PatientUserVO patientUserVO = AppUtil.getPatientVO(emergencyContactsVO.getUserProfileId(), context);

            if (emergencyContactsVO.getRelationship().equalsIgnoreCase(Constant.NOTIFYING_DOCTOR)) {
                if(emergencyContactsVO.getSpecialities()!=null)
                {
                    if(emergencyContactsVO.getSpecialities().size()>0)
                    {
                        holder.mTextSecondLine.setText(emergencyContactsVO.getSpecialities().get(0)!=null?emergencyContactsVO.getSpecialities().get(0).getDisplayName():"");
                    }
                }
                downloadAndSetProfileImage(emergencyContactsVO.getProfilePicId(), holder.picImageView);
            } else {
                if (emergencyContactsVO.getStatus().equalsIgnoreCase("Pending")) {
                    holder.mTextSecondLine.setText(emergencyContactsVO.getRelationship() + " Request " + emergencyContactsVO.getStatus());
                } else {
                    holder.mTextSecondLine.setText(emergencyContactsVO.getRelationship());
                }
                setProfileImage(patientUserVO, holder.picImageView);
            }
            holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                   /* FragmentDialogAlertWithTwoButtons mFragmentDialogAlertWithTwoButtons = FragmentDialogAlertWithTwoButtons.newInstance(getString(R.string.label_cancel), getString(R.string.OK), "", getString(R.string.lable_delete_contacts).replace("name",emergencyContactsVO.getFirstName()));
                    if (!mFragmentDialogAlertWithTwoButtons.isVisible()) {
                        mFragmentDialogAlertWithTwoButtons.show(getFragmentManager(), "DialogAlertWithTwoButtons");
                    }*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
                        builder.setMessage(getString(R.string.lable_delete_contacts).replace("name",emergencyContactsVO.getFirstName()+" "+emergencyContactsVO.getLastName()));
                        builder.setTitle("");
                        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                showProgress(true);

                                RequestQueue mRequestQueue= AppVolley.getRequestQueue();
                                mRequestQueue.add(createDeleteContactRequest(emergencyContactsVO.getPatientAccessId(),0,position,contactsVOs,null));
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
            setCallButton(emergencyContactsVO, holder);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return contactsVOs.size();
        }

        public void downloadAndSetProfileImage(long picId, final ImageView profilePic) {
            if (picId != 0) {
                String imageUrl = WebServiceUrls.getPatientAttachment
                        + picId+"&patientId="+currentSelectedPatientId+"&moduleType="+Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
                String url = WebServiceUrls.serverUrl + imageUrl;
                Log.d(TAG, "Url=" + url);
                Picasso
                        .with(getApplicationContext())
                        .load(url)
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.emergency_default_profile)
                        .into(profilePic);
            } else {
                Picasso.with(getApplicationContext()).load(R.drawable.default_profile).transform(new CircleTransform()).into(profilePic);
            }


        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextFirstLine;
            public TextView mTextSecondLine;
            public ImageView picImageView;
            public ImageView callImageView;
            private LinearLayout parentView;

            public ViewHolder(View v) {
                super(v);
                mTextFirstLine = (TextView) v.findViewById(R.id.firstLine);
                mTextSecondLine = (TextView) v.findViewById(R.id.secondLine);
                picImageView = (ImageView) v.findViewById(R.id.icon);
                callImageView = (ImageView) v.findViewById(R.id.contact_call_button);
                parentView = (LinearLayout) v.findViewById(R.id.parent_view);


            }
        }
    }

    public class MyHospitalAdapter extends RecyclerView.Adapter<MyHospitalAdapter.ViewHolder> {
        private ArrayList<OrganisationVO> hospitaVos;
        private Context context;

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyHospitalAdapter(Context context, ArrayList<OrganisationVO> list) {
            this.hospitaVos = list;
            this.context = context;
        }

        @Override
        public MyHospitalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.contacts_row_view, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final OrganisationVO organisationVO = hospitaVos.get(position);
            holder.mTextFirstLine.setText(AppUtil.convertToCamelCase(organisationVO.getOrgName()));
            Log.d(TAG, "STATUS=" + organisationVO.getStatus());
            if (organisationVO.getStatus().equalsIgnoreCase("Pending")) {
                holder.mTextSecondLine.setText(organisationVO.getOrgType() + " Request " + organisationVO.getStatus());
            } else {
                holder.mTextSecondLine.setText(organisationVO.getOrgType());
            }
            setOrgImage(organisationVO, holder.picImageView);

            holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                   /* FragmentDialogAlertWithTwoButtons mFragmentDialogAlertWithTwoButtons = FragmentDialogAlertWithTwoButtons.newInstance(getString(R.string.label_cancel), getString(R.string.OK), "", getString(R.string.lable_delete_contacts).replace("name",emergencyContactsVO.getFirstName()));
                    if (!mFragmentDialogAlertWithTwoButtons.isVisible()) {
                        mFragmentDialogAlertWithTwoButtons.show(getFragmentManager(), "DialogAlertWithTwoButtons");
                    }*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
                    builder.setMessage(getString(R.string.lable_delete_contacts).replace("name",organisationVO.getOrgName()));
                    builder.setTitle("");
                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            showProgress(true);
                            RequestQueue mRequestQueue= AppVolley.getRequestQueue();
                            mRequestQueue.add(createDeleteContactRequest(0,organisationVO.getOrgId(),position,null,hospitaVos));
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

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return hospitaVos.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextFirstLine;
            public TextView mTextSecondLine;
            public ImageView picImageView;
            private LinearLayout parentView;

            public ViewHolder(View v) {
                super(v);
                mTextFirstLine = (TextView) v.findViewById(R.id.firstLine);
                mTextSecondLine = (TextView) v.findViewById(R.id.secondLine);
                picImageView = (ImageView) v.findViewById(R.id.icon);
                parentView = (LinearLayout) v.findViewById(R.id.parent_view);

            }
        }
    }
}
