package com.patientz.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.InsuranceUpload;
import com.patientz.VO.UserInfoVO;
import com.patientz.adapters.AutoCompleteSimpleAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddInsuredDetailsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddInsuredDetailsActivity";
    private static final String ACTION_MSG_RECEIVED = "ACTION_MSG_RECEIVED";
    private EditText etFirstName;
    private EditText etLastName;
    private Button btGender;
    private ImageView ivCalImage;
    private boolean dateOfBirthSelected;
    private TextView tvDate;
    private TextView tvMonth;
    private TextView tvYear, tvDobError;
    private ImageView ivDobDownArrow;
    private EditText etAadhar1;
    private EditText etAadhar2;
    private EditText etAadhar3;
    private EditText etEmail;
    private EditText etMobile;
    private EditText etAddress1;
    private EditText etAddress2;
    private EditText etCity;
    private AutoCompleteTextView etState;
    private EditText etDistrict;
    private EditText etPincode;
    private EditText etNomineeName;
    private EditText etNomineeRelationship;
    private Button btContinue,btNomineeRelationship;
    private ProgressDialog mDialog;
    private int gender;
    private  UserInfoVO mUserInfoVO;
    private Date dateOfBirth = Calendar.getInstance().getTime();
    private LinearLayout llDOB;
    private RequestQueue mRequestQueue;
    private DatabaseHandler mDatabaseHandler;
    private int nomineeSelectedPostion=-1;

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            dateOfBirth = calendar.getTime();
            dateOfBirthSelected = true;
            tvDobError.setError(null);
            tvDate.setText(new SimpleDateFormat("dd").format(dateOfBirth));
            tvYear.setText(new SimpleDateFormat("yyyy").format(dateOfBirth));
            tvMonth.setText(new SimpleDateFormat("MMM").format(dateOfBirth));
        }
    };


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-12-21 11:49:14 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);
        btGender = (Button) findViewById(R.id.bt_gender);
        btNomineeRelationship = (Button) findViewById(R.id.bt_nominee_relationship);

        ivCalImage = (ImageView) findViewById(R.id.iv_cal_image);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        tvYear = (TextView) findViewById(R.id.tv_year);




        ivDobDownArrow = (ImageView) findViewById(R.id.iv_dob_down_arrow);
        etAadhar1 = (EditText) findViewById(R.id.et_aadhar1);
        etAadhar1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 4) {
                    etAadhar2.requestFocus();
                }

            }
        });
        etAadhar2 = (EditText) findViewById(R.id.et_aadhar2);
        etAadhar2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 4) {
                    etAadhar3.requestFocus();
                }

            }
        });
        etAadhar3 = (EditText) findViewById(R.id.et_aadhar3);
        etEmail = (EditText) findViewById(R.id.et_email);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etAddress1 = (EditText) findViewById(R.id.et_address1);
        tvDobError = findViewById(R.id.tv_dob_error);
        etAddress2 = (EditText) findViewById(R.id.et_address2);
        etCity = (EditText) findViewById(R.id.et_city);
        etState = (AutoCompleteTextView) findViewById(R.id.et_state);
        String states[] =getResources().getStringArray(R.array.states_n_union_territories);
        AutoCompleteSimpleAdapter adapter = new AutoCompleteSimpleAdapter(this,
                R.layout.textview_style, Arrays.asList(states));
        etState.setThreshold(1);
        etState.setAdapter(adapter);
        etDistrict = (EditText) findViewById(R.id.et_district);
        etPincode = (EditText) findViewById(R.id.et_pincode);
        etNomineeName = (EditText) findViewById(R.id.et_nominee_name);
        llDOB = findViewById(R.id.ll_dob);
        etNomineeRelationship = (EditText) findViewById(R.id.et_nominee_relationship);
        etNomineeRelationship.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE
                        || id == EditorInfo.IME_NULL
                        || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etNomineeRelationship.getWindowToken(), 0);
                    sendInsuredDetailsToServer();
                    return true;
                } else {
                    return false;
                }
            }
        });
        btContinue =  findViewById(R.id.bt_continue);
        btContinue.setEnabled(true);
        btContinue.setClickable(true);
        btGender.setOnClickListener(this);
        btNomineeRelationship.setOnClickListener(this);
        btContinue.setOnClickListener(this);
        llDOB.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_gender:
                showGenderSelectionDialog();
                break;
            case R.id.bt_nominee_relationship:
                showNomineeRelationshipsListDialog();
                break;
            case R.id.bt_continue:
                btContinue.setEnabled(false);
                btContinue.setClickable(false);
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.INSURANCE_ADD_INSURED_DETAILS_SAVE_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_ADD_INSURED_DETAILS_SAVE_CLICKED);
                sendInsuredDetailsToServer();
                break;
            case R.id.ll_dob:
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTime(dateOfBirth);
                new DatePickerDialog(AddInsuredDetailsActivity.this, AlertDialog.THEME_HOLO_LIGHT, mDateSetListener,
                        mCalendar.get(Calendar.YEAR), mCalendar
                        .get(Calendar.MONTH), mCalendar
                        .get(Calendar.DAY_OF_MONTH)).show();

                break;

        }
    }



    private void sendInsuredDetailsToServer() {
        if(isFieldsValidationSuccess())
        {
            mDialog=CommonUtils.showProgressDialog(AddInsuredDetailsActivity.this);
            HashMap<String,String> params=new HashMap<>();
            params.put("patientId", String.valueOf(mUserInfoVO.getPatientId()));
            params.put("firstName",etFirstName.getText().toString());
            params.put("lastName",etLastName.getText().toString());
            params.put("gender", String.valueOf(gender));

            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            //SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            String dob = fmt.format(dateOfBirth);
            params.put("dob", dob);
            //params.put("aadharNo", etAadhar1.getText().toString()+etAadhar2.getText().toString()+etAadhar3.getText().toString());
            params.put("email", etEmail.getText().toString());
            params.put("mobileNumber", etMobile.getText().toString());
            params.put("address1", etAddress1.getText().toString());
            if(etAddress2.getText()!=null)
            {
                params.put("address2",etAddress2.getText().toString());
            }
            params.put("city", etCity.getText().toString());
            params.put("state", etState.getText().toString());
            params.put("district", etDistrict.getText().toString());
            params.put("pinCode", etPincode.getText().toString());
            params.put("nomineeName", etNomineeName.getText().toString());
            params.put("nomineeRelationShip", btNomineeRelationship.getText().toString());
            params.put("isMobileNumberVerified", String.valueOf(false));
            params.put("isEmailVerified", String.valueOf(false));
            String couponCode=getIntent().getStringExtra("couponCode");
            params.put("couponCode", !TextUtils.isEmpty(couponCode)?couponCode:"");
            if(getIntent().getStringExtra("paytm_ref_id")!=null)
            {
                params.put("paytmRefId", getIntent().getStringExtra("paytm_ref_id"));
            }else
            {
                params.put("paytmRefId", "");
            }
            Log.d(TAG,"params="+params);
            mRequestQueue.add(uploadDetails(params));
        }else
        {
            btContinue.setEnabled(true);
            btContinue.setClickable(true);
        }
    }

    private StringRequest uploadDetails(final HashMap<String, String> params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.insuranceUpload;

        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE \n" + response);
                btContinue.setEnabled(true);
                btContinue.setClickable(true);
               dismissProgressDialog();
               if(response!=null)
               {

                   Gson gson = new Gson();
                   Type objectType = new TypeToken<InsuranceUpload>() {
                   }.getType();
                   InsuranceUpload insuranceUpload = gson.fromJson(
                           response, objectType);
                   clearPendingInsuranceSharedPreference(insuranceUpload.getPatientId());
                   mDatabaseHandler.insertUserUploadInsurance(insuranceUpload);
                   Intent mIntent = new Intent(AddInsuredDetailsActivity.this, InsuranceCapturedActivity.class);
                   mIntent.putExtra("insuranceUpload", response);
                   Intent intent=getIntent();
                   int totalAmount=intent.getIntExtra("totalAmount",0);

                   Log.d("totalAmount",totalAmount+"");
                   mIntent.putExtra("totalAmount",totalAmount);
                   callVerifyEmailWebservice(insuranceUpload);
                   startActivity(mIntent);
                   finish();
               }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "******************* onErrorResponse ******************* \n");
                btContinue.setEnabled(true);
                btContinue.setClickable(true);
                dismissProgressDialog();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(AddInsuredDetailsActivity.this);
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
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }

    private void clearPendingInsuranceSharedPreference(long patientId) {
        final SharedPreferences mSharedPreferences=getSharedPreferences(String.valueOf(patientId),Context.MODE_PRIVATE);
        mSharedPreferences.edit().clear().commit();
    }

    private void callVerifyEmailWebservice(final InsuranceUpload insuranceUpload) {
        new Thread() {
            public void run() {
                RequestQueue mRequestQueue=AppVolley.getRequestQueue();
                mRequestQueue.add(tvVerifyEmail(insuranceUpload));
            }
        }.start();
    }



    @Override
    protected void onStop() {
        super.onStop();
        dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if(mDialog!=null)
        {
            if(mDialog.isShowing())
            {
                mDialog.dismiss();
            }
        }
    }

    private void showGenderSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                AddInsuredDetailsActivity.this);
        String[] genderArray = getResources().getStringArray(R.array.add_patient_gender_entry);
        builder.setItems(genderArray,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int position) {
                        gender = position + 1;
                        btGender.setText(position == 0 ? "Male" : "Female");
                    }
                }).show();
    }
    private void showNomineeRelationshipsListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                AddInsuredDetailsActivity.this);
        final String[] nomineeArray = getResources().getStringArray(R.array.relationships);
        builder.setItems(nomineeArray,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int position) {
                        nomineeSelectedPostion=position;
                        switch (position)
                        {
                            case 0:
                                btNomineeRelationship.setText(nomineeArray[0]);
                                break;
                            case 1:
                                btNomineeRelationship.setText(nomineeArray[1]);
                                break;
                            case 2:
                                btNomineeRelationship.setText(nomineeArray[2]);

                                break;
                            case 3:
                                btNomineeRelationship.setText(nomineeArray[3]);

                                break;
                            case 4:
                                btNomineeRelationship.setText(nomineeArray[4]);

                                break;
                            case 5:
                                btNomineeRelationship.setText(nomineeArray[5]);

                                break;
                            case 6:
                                btNomineeRelationship.setText(nomineeArray[6]);

                                break;
                            case 7:
                                btNomineeRelationship.setText(nomineeArray[7]);

                                break;
                            case 8:
                                btNomineeRelationship.setText(nomineeArray[8]);

                                break;
                            case 9:
                                btNomineeRelationship.setText(nomineeArray[9]);

                                break;
                            case 10:
                                btNomineeRelationship.setText(nomineeArray[10]);

                                break;
                            case 11:
                                btNomineeRelationship.setText(nomineeArray[11]);

                                break;
                            case 12:
                                btNomineeRelationship.setText(nomineeArray[12]);

                                break;
                            case 13:
                                btNomineeRelationship.setText(nomineeArray[13]);

                                break;
                            case 14:
                                btNomineeRelationship.setText(nomineeArray[14]);

                                break;
                            case 15:
                                btNomineeRelationship.setText(nomineeArray[15]);

                                break;
                            case 16:
                                btNomineeRelationship.setText(nomineeArray[16]);

                                break;
                            case 17:
                                btNomineeRelationship.setText(nomineeArray[17]);
                                break;
                            case 18:
                                btNomineeRelationship.setText(nomineeArray[18]);
                                break;
                        }
                    }
                }).show();
    }
    private StringRequest tvVerifyEmail(InsuranceUpload insuranceUpload) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.verifyEmail+"patientId="+insuranceUpload.getPatientId()+"&id="+insuranceUpload.getId();

        Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null)
                {
                    Log.d(TAG,"response="+response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "******************* onErrorResponse ******************* \n");
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getApplicationContext(), super.getHeaders());
            }
        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mRequest;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_insured_details);
         mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_insured_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRequestQueue= AppVolley.getRequestQueue();
        findViews();
        Intent mIntent = getIntent();
        long patientId = mIntent.getLongExtra("patientId", 0);
        try {
            mUserInfoVO = mDatabaseHandler.getUserInfo(patientId);
            etFirstName.setText(mUserInfoVO.getFirstName());
            etLastName.setText(mUserInfoVO.getLastName());
            btGender.setText(mUserInfoVO.getGender() == 2 ? getString(R.string.female) : getString(R.string.male));
            gender = mUserInfoVO.getGender();
            //setDateOfBirth(mUserInfoVO);
            setMobileNo(mUserInfoVO);
            etEmail.setText(mUserInfoVO.getEmailId());
            etMobile.setText(mUserInfoVO.getPhoneNumber());
            etCity.setText(mUserInfoVO.getCity());
            etState.setText(!TextUtils.isEmpty(mUserInfoVO.getState())?mUserInfoVO.getState():"");
            etPincode.setText(mUserInfoVO.getPinCode());
            //etAddress1.setText(mUserInfoVO.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMobileNo(UserInfoVO mUserInfoVO) {
        if (mUserInfoVO.getPhoneNumber() != null) {
            String phoneNumberWithoutIsd;
            String phoneNumber=mUserInfoVO.getPhoneNumber();
            if(phoneNumber.contains("-"))
            {
                String[] splitMobileNO=phoneNumber.split("-");
                phoneNumberWithoutIsd=splitMobileNO[1];
            }else
            {
                phoneNumberWithoutIsd=phoneNumber;
            }
            etMobile.setText(phoneNumberWithoutIsd);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setDateOfBirth(UserInfoVO mUserInfoVO) {
        dateOfBirth = mUserInfoVO.getDateOfBirth();
        Log.d(TAG,"dateOfBirth="+dateOfBirth);
        dateOfBirthSelected = true;
        tvDate.setText(new SimpleDateFormat("dd").format(dateOfBirth));
        tvYear.setText(new SimpleDateFormat("yyyy").format(dateOfBirth));
        tvMonth.setText(new SimpleDateFormat("MMM").format(dateOfBirth));
    }

    public boolean isFieldsValidationSuccess()
    {
        boolean result = true;
        ArrayList<View> errorProneEdittext = new ArrayList<View>();

        if (TextUtils.isEmpty(etFirstName.getText().toString())) {
            etFirstName.setError(getString(R.string.fname_mandatory_error_msg));
            errorProneEdittext.add(etFirstName);
            result = false;
        }
        if (TextUtils.isEmpty(etLastName.getText().toString())) {
            etLastName.setError(getString(R.string.lname_mandatory_error_msg).replace("Surname", "Last Name"));
            errorProneEdittext.add(etLastName);
            result = false;
        }
        if (gender == 0) {
            AppUtil.showToast(getApplicationContext(), getString(R.string.gender_mandatory));
            result = false;
        }
        if (nomineeSelectedPostion == -1) {
            AppUtil.showToast(getApplicationContext(), getString(R.string.nominee_relationship_is_mandatory));
            result = false;
        }
        Log.d(TAG,"dateOfBirthSelected="+dateOfBirthSelected);
        int age=AppUtil.getAge(dateOfBirth,getApplicationContext());
        if (!dateOfBirthSelected || (age<18 || age>65)) {
            tvDobError.setError(getString(R.string.date_of_birth_is_mandatory));
            errorProneEdittext.add(tvDobError);
            result = false;
        }
        if (!AppUtil.isValidEmail(etEmail.getText().toString())) {
            etEmail.setError(getString(R.string.email_error_msg));
            errorProneEdittext.add(etEmail);
            result = false;
        }
        if (!AppUtil.isValidMobileNumber(etMobile.getText().toString())) {
            etMobile.setError(getString(R.string.phone_validation_error));
            errorProneEdittext.add(etMobile);
            result = false;

        }
        if (TextUtils.isEmpty(etAddress1.getText().toString())) {
            etAddress1.setError(getString(R.string.address_mandatory));
            errorProneEdittext.add(etAddress1);
            result = false;

        }
        if (TextUtils.isEmpty(etCity.getText().toString())) {
            etCity.setError(getString(R.string.city_is_mandatory));
            errorProneEdittext.add(etCity);

            result = false;
        }
        if (TextUtils.isEmpty(etState.getText().toString())) {
            etState.setError(getString(R.string.state_is_mandatory));
            errorProneEdittext.add(etState);
            result = false;
        }

        if (TextUtils.isEmpty(etDistrict.getText().toString())) {
            etDistrict.setError(getString(R.string.district_is_mandatory));
            errorProneEdittext.add(etDistrict);

            result = false;

        }
        if (TextUtils.isEmpty(etPincode.getText().toString())) {
            etPincode.setError(getString(R.string.pincode_is_mandatory));
            errorProneEdittext.add(etPincode);
            result = false;
        }
        if (TextUtils.isEmpty(etNomineeName.getText().toString())) {
            etNomineeName.setError(getString(R.string.nominee_name_is_mandatory));
            errorProneEdittext.add(etNomineeName);
            result = false;
        }
        if (errorProneEdittext.size()>0) {
            errorProneEdittext.get(0).requestFocus();
            errorProneEdittext.get(0).requestFocusFromTouch();
        }
    return result;
}

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
