package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.HospitalVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.RecordSchemaAttributes;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.adapters.AdapterCustomObjectsList;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.services.StickyNotificationForeGroundService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivityEHRUpdate extends BaseActivity implements View.OnClickListener, Filterable {
    private static String TAG = "ActivityEHRUpdate";
    ProgressDialog dialog;
    Button saveDetails;
    View headerNav;
    ImageView editDetails;
    private LinearLayout wrapperLayout;
    LinkedHashMap<String, String> healthRecordLinkedHashMap;
    InputMethodManager imm;
    long roleOfLoggedInUser;
    LinkedHashMap<String, String> recordSchemaLinkedHashMap;
    private long hrId = 0;
    private Exception exp;
    private UserInfoVO mUserInfoVO;
    LinkedHashMap<String, String> mapForUI;
    ArrayList<RecordSchemaAttributes> rsaListForUI;
    private LinearLayout mLoaderStatusView;
    private ScrollView rootView;
    private RequestQueue mRequestQueue;
    private TextView tvAllergies, tvMedicalCondition/*, tvBloodGroup*/;
    private Spinner spinnerPregnant;
    private RelativeLayout parentSpinnerPregnant;
    private EditText etMedicalInstruction, etMedicineAllergies, etMedicationForChronicIllness, etLanguagesSpoken;
    private AutoCompleteTextView etPreferredOrgBranch;
    private DatabaseHandler mDatabaseHandler;
    private PatientUserVO patientUserVO;
    private HealthRecordVO mHealthRecordVO;
    ImageView ivPlusAllergy;
    SharedPreferences sp;
    boolean etPreferredOrgBranchStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ehrupdate);


        setToolbar();
        mRequestQueue = AppVolley.getRequestQueue();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {

            mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
            long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
            patientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
            mHealthRecordVO = mDatabaseHandler.getUserHealthRecord(Constant.RECORD_TYPE_EHR, patientUserVO.getPatientId());
            getUiIds();

            if (mHealthRecordVO != null && !mHealthRecordVO.equals("")) {
                populateFields(mHealthRecordVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(getIntent()!=null)
        {
            Log.d(TAG,"Intent not empty");
            if(getIntent().getBooleanExtra(Constant.FOCUS_PREFERRED_HOSPITAL,false))
            {
                Log.d(TAG,"etPreferredOrgBranch requestFocus");
                etPreferredOrgBranch.requestFocus();
            }
        }
    }

    private void populateFields(HealthRecordVO mHealthRecordVO) {


        LinkedHashMap<String, String> healthRecordMap = mHealthRecordVO.getHealthRecord();
        tvAllergies.setText(healthRecordMap.get("Allergies"));
        tvMedicalCondition.setText(healthRecordMap.get("Medical_Condition"));
//        tvBloodGroup.setText(healthRecordMap.get("Blood_Group"));
        etMedicalInstruction.setText(healthRecordMap.get("Medical_Instruction"));
        etMedicationForChronicIllness.setText(healthRecordMap.get("Medication_for_Chronic_Illness"));
        etPreferredOrgBranch.setText(healthRecordMap.get("preferredOrgBranch"));
        etPreferredOrgBranchStatus = true;
        etMedicineAllergies.setText(healthRecordMap.get("Medicine_Allergies"));
        etLanguagesSpoken.setText(healthRecordMap.get("Languages_Spoken"));
        if (!TextUtils.isEmpty(healthRecordMap.get("preferredOrgBranchId"))) {
            sp.edit().putLong("preferredOrgBranchId", Long.parseLong(healthRecordMap.get("preferredOrgBranchId"))).commit();
        }
        if (!TextUtils.isEmpty(healthRecordMap.get("preferredOrgId"))) {
            sp.edit().putLong("preferredOrgId", Long.parseLong(healthRecordMap.get("preferredOrgId"))).commit();
        }


        for (int i = 0; i < spinnerPregnant.getCount(); i++) {
            if (spinnerPregnant.getItemAtPosition(i).equals(
                    healthRecordMap.get("Pregnant"))) {
                spinnerPregnant.setSelection(i);
            }
        }

    }

    private void getUiIds() throws Exception {
        mLoaderStatusView = (LinearLayout) findViewById(R.id.loading_status);
        rootView = (ScrollView) findViewById(R.id.root_view);
        tvAllergies = (TextView) findViewById(R.id.tv_allergies);
        tvMedicalCondition = (TextView) findViewById(R.id.tv_medical_condition);
//        tvBloodGroup = (TextView) findViewById(R.id.tv_blood_group);
        etMedicalInstruction = (EditText) findViewById(R.id.et_medication);
        etMedicineAllergies = (EditText) findViewById(R.id.et_medication_allergies);
        etMedicationForChronicIllness = (EditText) findViewById(R.id.et_medication_for_chronic_illness);
        etPreferredOrgBranch = (AutoCompleteTextView) findViewById(R.id.et_preferred_org_branch);

        populateOrgBranchesAutoComplete();
        etLanguagesSpoken = (EditText) findViewById(R.id.et_languages_spoken);
        spinnerPregnant = (Spinner) findViewById(R.id.spinner_pregnant);
        parentSpinnerPregnant = (RelativeLayout) findViewById(R.id.parent_pregnant);
        setPregnantSpinnerVisibility();
        tvAllergies.setMovementMethod(new ScrollingMovementMethod());
        tvAllergies.setSelected(true);
        ivPlusAllergy = (ImageView) findViewById(R.id.iv_plus_allergies);
        ivPlusAllergy.setColorFilter(Color.parseColor("#ca3b3a"), PorterDuff.Mode.MULTIPLY);
        ImageView ivPlusMC = (ImageView) findViewById(R.id.iv_plus_medical_condition);
        ivPlusAllergy.setOnClickListener(this);
        ivPlusMC.setOnClickListener(this);

        etPreferredOrgBranch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etPreferredOrgBranchStatus = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPreferredOrgBranchStatus = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void populateOrgBranchesAutoComplete() {
        DatabaseHandlerAssetHelper mDatabaseHandler = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
        ItemAutoTextAdapter adapter = this.new ItemAutoTextAdapter(mDatabaseHandler);
        etPreferredOrgBranch.setAdapter(adapter);
        etPreferredOrgBranch.setOnItemClickListener(adapter);
    }

    class ItemAutoTextAdapter extends CursorAdapter
            implements AdapterView.OnItemClickListener {

        private DatabaseHandlerAssetHelper databaseHandler;

        /**
         * Constructor. Note that no cursor is needed when we create the
         * adapter. Instead, cursors are created on demand when completions are
         * needed for the field. (see
         * {@link ItemAutoTextAdapter#runQueryOnBackgroundThread(CharSequence)}.)
         *
         * @param databaseHandler The AutoCompleteDbAdapter in use by the outer class
         *                        object.
         */
        public ItemAutoTextAdapter(DatabaseHandlerAssetHelper databaseHandler) {
            // Call the CursorAdapter constructor with a null Cursor.
            super(ActivityEHRUpdate.this, null);
            this.databaseHandler = databaseHandler;
        }

        /**
         * Invoked by the AutoCompleteTextView field to get completions for the
         * current input.
         * <p/>
         * NOTE: If this method either throws an exception or returns null, the
         * Filter class that invokes it will log an error with the traceback,
         * but otherwise ignore the problem. No choice list will be displayed.
         * Watch those error logs!
         *
         * @param constraint The input entered thus far. The resulting query will
         *                   search for states whose name begins with this string.
         * @return A Cursor that is positioned to the first row (if one exists)
         * and managed by the activity.
         */
        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            Cursor cursor = databaseHandler.getMatchingStates(ActivityEHRUpdate.this,
                    (constraint != null ? constraint.toString() : null), Constant.ORG_TYPE_URL_HOSPITAL);

            return cursor;
        }

        /**
         * Called by the AutoCompleteTextView field to get the text that will be
         * entered in the field after a choice has been made.
         *
         * @param //Cursor The cursor, positioned to a particular row in the list.
         * @return A String representing the row's text value. (Note that this
         * specializes the base class return value for this method,
         * which is {@link CharSequence}.)
         */
        @Override
        public String convertToString(Cursor cursor) {
            final int columnIndex = cursor.getColumnIndexOrThrow("display_name");
            final int columnIndex1 = cursor.getColumnIndexOrThrow("emergency_phone_number");
            final String orgBranch = cursor.getString(columnIndex);
            Log.d(TAG, "BRANCH NAME=" + orgBranch);
            final String emergency_phone_number = cursor.getString(columnIndex1);
            Log.d(TAG, "EMERGENCY PNO=" + emergency_phone_number);

            String str = orgBranch + "," + (!TextUtils.isEmpty(emergency_phone_number) ? emergency_phone_number : "");
            return str;
        }

        /**
         * Called by the ListView for the AutoCompleteTextView field to display
         * the text for a particular choice in the list.
         *
         * @param view    The TextView used by the ListView to display a particular
         *                choice.
         * @param context The context (Activity) to which this form belongs;
         *                equivalent to {@code SelectState.this}.
         * @param cursor  The cursor for the list of choices, positioned to a
         *                particular row.
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final String text = convertToString(cursor);
            ((TextView) view).setText(text);
        }

        /**
         * Called by the AutoCompleteTextView field to display the text for a
         * particular choice in the list.
         *
         * @param context The context (Activity) to which this form belongs;
         *                equivalent to {@code SelectState.this}.
         * @param cursor  The cursor for the list of choices, positioned to a
         *                particular row.
         * @param parent  The ListView that contains the list of choices.
         * @return A new View (really, a TextView) to hold a particular choice.
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view =
                    inflater.inflate(android.R.layout.simple_dropdown_item_1line,
                            parent, false);

            return view;
        }

        /**
         * Called by the AutoCompleteTextView field when a choice has been made
         * by the user.
         *
         * @param listView The ListView containing the choices that were displayed to
         *                 the user.
         * @param view     The field representing the selected choice
         * @param position The position of the choice within the list (0-based)
         * @param id       The id of the row that was chosen (as provided by the _id
         *                 column in the cursor.)
         */
        @Override
        public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
            // Get the cursor, positioned to the corresponding row in the result set
            Cursor cursor = (Cursor) listView.getItemAtPosition(position);

            // Get the state's capital from this row in the database.


            Log.d(TAG, "ORG BRANCH ID=" + cursor.getInt(cursor.getColumnIndexOrThrow("_id")));

            sp.edit().putLong("preferredOrgBranchId", cursor.getInt(cursor.getColumnIndexOrThrow("_id"))).commit();
            sp.edit().putLong("preferredOrgId", cursor.getInt(cursor.getColumnIndexOrThrow("org_id"))).commit();

            final int columnIndex = cursor.getColumnIndexOrThrow("display_name");
            final int columnIndex1 = cursor.getColumnIndexOrThrow("emergency_phone_number");
            final String orgBranch = cursor.getString(columnIndex);
            final String emergency_phone_number = cursor.getString(columnIndex1);
            String str = orgBranch + "," + (!TextUtils.isEmpty(emergency_phone_number) ? "," + emergency_phone_number : "");
            etPreferredOrgBranch.setText(str);
            etPreferredOrgBranchStatus = true;
            // Update the parent class's TextView
            //mStateCapitalView.setText(capital);
        }
    }

    private void setPregnantSpinnerVisibility() throws Exception {
        UserInfoVO mUserInfoVO = mDatabaseHandler.getUserInfo(patientUserVO.getPatientId());
        if (mUserInfoVO != null || !mUserInfoVO.equals("")) {
            if (mUserInfoVO.getGender() == Constant.MALE) {
                parentSpinnerPregnant.setVisibility(View.GONE);

            } else {
                parentSpinnerPregnant.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.title_activity_activity_ehrupdate);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_save_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                Log.d(TAG, "SAVE CLICKED");
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.EMHR_EDIT_SUBMIT_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMHR_EDIT_SUBMIT_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                if(tracker!=null) {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("EHR Added/Updated")
                            .build());
                }
                try {
                    updateRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateRecord() throws Exception {
        LinkedHashMap<String, String> healthRecordMap = getHealthRecordMap();
        if (healthRecordMap != null) {
            if (!healthRecordMap.isEmpty()) {
                setValues(healthRecordMap);
            } else {
                AppUtil.showToast(getApplicationContext(), getString(R.string.toast_msg_empty_emhr));
            }
        }

    }

    private LinkedHashMap<String, String> getHealthRecordMap() {
        LinkedHashMap<String, String> healthRecordMap = new LinkedHashMap<String, String>();

        if (etPreferredOrgBranchStatus == true && sp.getLong("preferredOrgBranchId", 0) != 0 && !TextUtils.isEmpty(etPreferredOrgBranch.getText().toString())) {
            healthRecordMap.put("preferredOrgBranchId", "" + sp.getLong("preferredOrgBranchId", 0));
        }

        if (etPreferredOrgBranchStatus == true && sp.getLong("preferredOrgId", 0) != 0 && !TextUtils.isEmpty(etPreferredOrgBranch.getText().toString())) {
            healthRecordMap.put("preferredOrgId", "" + sp.getLong("preferredOrgId", 0));
        }
        if (!TextUtils.isEmpty(etPreferredOrgBranch.getText().toString())) {
            healthRecordMap.put("preferredOrgBranch", etPreferredOrgBranch.getText().toString());
        }
        if (spinnerPregnant.getSelectedItem() != null) {
            if (!spinnerPregnant.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                healthRecordMap.put("Pregnant", spinnerPregnant.getSelectedItem().toString());
            }
        }
        if (!TextUtils.isEmpty(tvAllergies.getText().toString())) {
            healthRecordMap.put("Allergies", tvAllergies.getText().toString());
        }
        if (!TextUtils.isEmpty(etMedicineAllergies.getText().toString())) {
            healthRecordMap.put("Medicine_Allergies", etMedicineAllergies.getText().toString());
        }
        if (!TextUtils.isEmpty(tvMedicalCondition.getText().toString())) {
            healthRecordMap.put("Medical_Condition", tvMedicalCondition.getText().toString());
        }
        if (!TextUtils.isEmpty(etMedicationForChronicIllness.getText().toString())) {
            healthRecordMap.put("Medication_for_Chronic_Illness", etMedicationForChronicIllness.getText().toString());
        }
        if (!TextUtils.isEmpty(etMedicalInstruction.getText().toString())) {
            healthRecordMap.put("Medical_Instruction", etMedicalInstruction.getText().toString());
        }
        if (!TextUtils.isEmpty(etLanguagesSpoken.getText().toString())) {
            healthRecordMap.put("Languages_Spoken", etLanguagesSpoken.getText().toString());
        }
        return healthRecordMap;
    }

    private void setValues(LinkedHashMap<String, String> healthRecordMap) throws Exception {
        if (AppUtil.isOnline(getApplicationContext())) {
            Log.d(TAG, "setValues()-->healthRecordMap"
                    + healthRecordMap.entrySet());
            hrId = mHealthRecordVO.getId();
            Log.d(TAG, "hrId" + hrId);
            HealthRecordVO mHealthRecordVO = new HealthRecordVO();
            mHealthRecordVO.setPatientId(patientUserVO.getPatientId());
            Log.d(TAG, "current patient id :" + mHealthRecordVO.getPatientId());
            mHealthRecordVO.setStartDate(new Date());
            mHealthRecordVO.setHealthRecord(healthRecordMap);
            mHealthRecordVO.setRecordType("EHR");
            mHealthRecordVO.setId(hrId);
            Log.d(TAG,
                    "sending HealthRecordVO "
                            + mHealthRecordVO.getHealthRecord());
            Gson gson = new Gson();
            gson = new GsonBuilder().setDateFormat("EEE MMM d HH:mm:ss Z yyyy")
                    .create();
            Type typeOfObjectVO = new TypeToken<HealthRecordVO>() {
            }.getType();
            try {
                String toSendJson = gson
                        .toJson(mHealthRecordVO, typeOfObjectVO);
                Log.d("json: ", toSendJson);
                showProgress(true);
                mRequestQueue.add(createUpdateEHRRequest(toSendJson));
            } catch (Exception e) {
                Log.d(TAG, "EXCEPTION=" + e.getMessage());

            }
        } else {
            AppUtil.showDialog(ActivityEHRUpdate.this, getString(R.string.offlineMode));
        }
    }


    private StringRequest createUpdateEHRRequest(final String json) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.updateEHR;
        Log.d(TAG, "URL \n" + szServerUrl);
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
                Log.d(TAG, "******************* onErrorResponse ******************* \n");
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEHRUpdate.this);
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
                            builder.create().show();
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
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("lastSyncId", String.valueOf(AppUtil.getEventLogID(getApplicationContext())));
                params.put("patientId", String.valueOf(patientUserVO.getPatientId()));
                params.put("record", json);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {

        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.toast_msg_emhr_updated));

                    AppUtil.sendCampaignDetails(getApplicationContext(), Constant.EVENT_EHR);


                    for (PatientUserVO mPatientUserVO : mResponseVO.getPatientUserVO()) {
                        if (mPatientUserVO.getPatientId() == patientUserVO.getPatientId()) {
                            try {
                                HealthRecordVO mHealthRecordVO = mPatientUserVO.getRecordVO().getHealthRecordVO();
                                if (mHealthRecordVO != null) {
                                    Log.d(TAG, "HEALTH RECORD NOT NULL");
                                    mDatabaseHandler.insertUserInfo(mPatientUserVO.getRecordVO(), mPatientUserVO.getBloodGroup(), mPatientUserVO.getPatientHandle());// this is done because we need to store ambulance number selected in user_tbl
                                    mDatabaseHandler.insertUserHealthRecord(mHealthRecordVO, mPatientUserVO.getPatientId());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //sp.edit().putBoolean("logoStatus", true).commit();
                            sp.edit().putLong("preferredOrgBranchId", 0).commit();
                            sp.edit().putLong("preferredOrgId", 0).commit();
                            try {
                                Intent startIntent = new Intent(ActivityEHRUpdate.this, StickyNotificationForeGroundService.class);
                                startIntent.setAction(Constant.ACTION.EMERGENCY_PREPAREDNESS);
                                startService(startIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            finish();
                            break;
                        }
                    }
                    break;
                case Constant.RESPONSE_FAILURE:
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.failure_msg));
                case Constant.RESPONSE_NOT_LOGGED_IN:
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.not_logged_in_error_msg));
                    break;
                case Constant.RESPONSE_SERVER_ERROR:
                    AppUtil.showToast(getApplicationContext(),
                            getString(R.string.server_error_msg));
                    break;
                case Constant.TERMINATE:
                    finish();
                    break;
                default:
                    break;
            }
        }
        showProgress(false);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Log.d(TAG, "IF");
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

            rootView.setVisibility(View.VISIBLE);
            rootView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rootView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            Log.d(TAG, "ELSE");
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            rootView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setPrefferedAmbulanceProvider(LinkedHashMap<String, String> recordSchemaMap) {
        String[] prefferedAmbulanceProvider = recordSchemaMap.get("preferredAmbulanceProvider").split(",");
        if (prefferedAmbulanceProvider.length == 3) {
            sp.edit().putString("preferredAmbulanceProviderPhoneNo", prefferedAmbulanceProvider[2]).commit();
        } else if (prefferedAmbulanceProvider.length == 2) {
            sp.edit().putString("preferredAmbulanceProviderPhoneNo", prefferedAmbulanceProvider[1]).commit();
        } else {
            sp.edit().putString("preferredAmbulanceProviderPhoneNo", null).commit();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "REQUEST CODE=" + requestCode);
        Log.d(TAG, "RESULT CODE=" + resultCode);
        Log.d(TAG, "INTENT DATA =" + data);
        if (data != null) {
            switch (requestCode) {
                case Constant.REQUEST_CODE_ALLERGIES:
                    if (resultCode == RESULT_OK) {
                        tvAllergies.setText(TextUtils.join(",", data.getStringArrayListExtra("allergies")));
                    }
                    break;
                case Constant.REQUEST_CODE_MEDICATION:
                    if (resultCode == RESULT_OK) {
                        tvMedicalCondition.setText(TextUtils.join(",", data.getStringArrayListExtra("medical_condition")));

                    }
                    break;
                case Constant.REQUEST_CODE_BLOOD_GROUP:
                    if (resultCode == RESULT_OK) {
                        Log.d(TAG, data.getStringExtra("blood_group"));
//                        tvBloodGroup.setText(data.getStringExtra("blood_group"));

                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_plus_allergies:
                Intent intentAllergies = new Intent(this, ActivityDialogAllergyTypes.class);
                String[] existingAllergiesList = getExistingAllergies();
                //intentAllergies.putStringArrayListExtra("existingAllergiesList",existingAllergiesList);
               /* ArrayList<String> mStrings=new ArrayList<String>();
                if(!TextUtils.isEmpty(tvAllergies.getText()))
                {
                    mStrings.add(tvAllergies.getText().toString());
                }*/
                intentAllergies.putExtra("existingAllergiesList", existingAllergiesList);
                startActivityForResult(intentAllergies, Constant.REQUEST_CODE_ALLERGIES);
                break;
            case R.id.iv_plus_medical_condition:
                String[] existingMedicalConditions = getExistingMedicalConditions();
                Intent intentMedicalCondition = new Intent(this, ActivityDialogMedicalConditions.class);
                intentMedicalCondition.putExtra("existingMedicalConditionList", existingMedicalConditions);
                startActivityForResult(intentMedicalCondition, Constant.REQUEST_CODE_MEDICATION);
                break;
        }

    }

    private String[] getExistingAllergies() {
        String[] existingAllergiesList = new String[0];
        if (tvAllergies.getText() != null || tvAllergies.getText().equals("")) {
            existingAllergiesList = tvAllergies.getText().toString().split(",");
        }
        return existingAllergiesList;
    }

    private String[] getExistingMedicalConditions() {
        String[] existingMedicalConditions = new String[0];
        if (tvMedicalCondition.getText() != null || tvMedicalCondition.getText().equals("")) {
            existingMedicalConditions = tvMedicalCondition.getText().toString().split(",");
        }
        return existingMedicalConditions;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class GetHospitalsFromDbAsync extends AsyncTask<String[], ArrayList<HospitalVO>, ArrayList<HospitalVO>> {

        ProgressDialog dialog;
        AutoCompleteTextView autoCompleteTextView;
        private String TAG = "GetHospitalsFromDbAsync";

        public GetHospitalsFromDbAsync(AutoCompleteTextView autoCompleteTextView) {
            this.autoCompleteTextView = autoCompleteTextView;
        }


        @Override
        protected ArrayList<HospitalVO> doInBackground(String[]... params) {
            DatabaseHandlerAssetHelper mDatabaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(getApplicationContext());
            ArrayList<HospitalVO> mHospitalVOs = null;
            try {
                mHospitalVOs = mDatabaseHandlerAssetHelper.getHospitalsList();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return mHospitalVOs;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.progress);
            dialog = new ProgressDialog(ActivityEHRUpdate.this);
            dialog.setMessage(getApplicationContext().getString(R.string.progress_dialog_loading_hospitals));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }


        @Override
        protected void onPostExecute(ArrayList<HospitalVO> mHospitalVOs) {
            super.onPostExecute(mHospitalVOs);
            dialog.dismiss();
            if (mHospitalVOs != null) {
                Log.d(TAG, "HOSPITALS SIZE=" + mHospitalVOs.size());
                AdapterCustomObjectsList mAdapterCustomObjectsList = new AdapterCustomObjectsList(getApplicationContext(), mHospitalVOs);
                autoCompleteTextView.setAdapter(mAdapterCustomObjectsList);
                autoCompleteTextView.setThreshold(4);

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HospitalVO hospitalVO = (HospitalVO) parent.getAdapter().getItem(position);
                        String displayText;
                        displayText = hospitalVO.getName() + "," + getAddress(hospitalVO);
                        autoCompleteTextView.setText(displayText);

                    }
                });
            } else {
            }
        }

    }

    private static String getAddress(HospitalVO hospitalVO) {
        String address = null;
        String additionalAddress = hospitalVO.getAdditionalAddress();
        if (!TextUtils.isEmpty(additionalAddress)) {
            address = additionalAddress + ",";
        }
        address = (address == null ? hospitalVO.getCity() : address + hospitalVO.getCity());
        Log.d(TAG, "ADDRESS=" + address);
        Log.d(TAG, "ADDITIONAL ADDRESS=" + hospitalVO.getAdditionalAddress() + "");
        Log.d(TAG, "CITY=" + hospitalVO.getCity() + "");

        return address;

    }

    private static String getAddress2(String additionalAddress, String city) {
        String address = null;
        if (!TextUtils.isEmpty(additionalAddress)) {
            address = additionalAddress + ",";
        }
        address = (address == null ? city : address + city);
        Log.d(TAG, "ADDRESS=" + address);
        Log.d(TAG, "ADDITIONAL ADDRESS=" + additionalAddress + "");
        Log.d(TAG, "CITY=" + city + "");

        return address;

    }
}
