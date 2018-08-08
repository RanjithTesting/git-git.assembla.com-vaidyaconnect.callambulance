package com.patientz.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.patientz.VO.InsuranceVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.services.StickyNotificationInsuranceFGService;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class InsuranceCapturedActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvPurchaseSuccessful;
    private TextView tvAccidentInsuranceAmt;
    private TextView tvPaymentRefNo;
    private TextView tvPaymentDate;
    private TextView tvWhatsapp;
    private TextView tvName;
    private TextView tvSumInsured;
    private TextView tvGender;
    private TextView tvDob;
    private TextView tvAadharNo;
    private TextView tvEmailId;
    private TextView tvMobileNo;
    private TextView tvAddress;
    private TextView tvNomineeName;
    private TextView tvNomineeRelation;
    private Button btShare;
    private Button btContinue;




    private void findViews() throws JSONException {
        toolbar = (Toolbar)findViewById( R.id.toolbar );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.title_activity_insurance_captured);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvPurchaseSuccessful = (TextView)findViewById( R.id.tv_purchase_successful );
        tvAccidentInsuranceAmt = (TextView)findViewById( R.id.tv_accident_insurance_amt );
        tvPaymentRefNo = (TextView)findViewById( R.id.tv_payment_ref_no );
        tvPaymentDate = (TextView)findViewById( R.id.tv_payment_date );
        tvWhatsapp = (TextView)findViewById( R.id.tv_whatsapp );
        tvName = (TextView)findViewById( R.id.tv_name );
        tvGender = (TextView)findViewById( R.id.tv_gender );
        tvDob = (TextView)findViewById( R.id.tv_dob );
        tvAadharNo = (TextView)findViewById( R.id.tv_aadhar_no );
        tvEmailId = (TextView)findViewById( R.id.tv_email_id );
        tvMobileNo = (TextView)findViewById( R.id.tv_mobile_no );
        tvAddress = (TextView)findViewById( R.id.tv_address );
        tvNomineeName = (TextView)findViewById( R.id.tv_nominee_name );
        tvPaymentRefNo = (TextView)findViewById( R.id.tv_payment_ref_no );
        tvNomineeRelation = (TextView)findViewById( R.id.tv_nominee_relation );
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a, EEE,d MMM yyyy");
        String txnDate = formatter.format(new Date());
        tvPaymentDate.setText(txnDate);

        tvSumInsured = (TextView)findViewById( R.id.tv_sum_insured );
        btShare = (Button)findViewById( R.id.bt_share );
        btContinue = (Button)findViewById( R.id.bt_continue );
        btShare.setOnClickListener( this );
        btContinue.setOnClickListener( this );
        DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());
        long patientId=AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        InsuranceVO mInsuranceVO=mDatabaseHandler.getPurchasedInsurances(patientId);
        populateData(mInsuranceVO);

    }

    private void populateData(InsuranceVO mInsuranceVO) {
        Intent intent=getIntent();
        int totalAmount=intent.getIntExtra("totalAmount",Constant.APOLLO_MUNICH_INS_AMOUNT);

        Log.d("totalAmount",totalAmount+"");
        tvAccidentInsuranceAmt.setText("₹ "+totalAmount);
        tvName.setText(mInsuranceVO.getFirstName()+" "+mInsuranceVO.getLastName());
        tvSumInsured.setText("₹"+AppUtil.getFormattedAmount(mInsuranceVO.getInsPolicyCoverage()));
        tvGender.setText(mInsuranceVO.getGender()==1?getString(R.string.male):getString(R.string.female));
        tvDob.setText(new SimpleDateFormat("dd-MMM-yyyy").format(mInsuranceVO.getDateOfBirth()));
        tvAadharNo.setText(mInsuranceVO.getAadharNo());
        tvEmailId.setText(mInsuranceVO.getEmail());
        tvMobileNo.setText(mInsuranceVO.getMobileNumber());
        tvAddress.setText(mInsuranceVO.getAddress1()+(!TextUtils.isEmpty(mInsuranceVO.getAddress2())?", "+mInsuranceVO.getAddress2():"")+", "+mInsuranceVO.getCity()+", "+mInsuranceVO.getDistrict()+", "+mInsuranceVO.getPinCode());
        tvNomineeName.setText(mInsuranceVO.getNomineeName());
        tvNomineeRelation.setText(mInsuranceVO.getNomineeRelationShip());
        if(!TextUtils.isEmpty(mInsuranceVO.getPaytmRefId()))
        {
            tvPaymentRefNo.setText("Payment Ref #"+mInsuranceVO.getPaytmRefId()+" | ");
        }
    }

    @Override
    public void onClick(View v) {
        if ( v == btShare ) {
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.INSURANCE_SUMMARY_DETAILS_SHARE_CLICKED,true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_SUMMARY_DETAILS_SHARE_CLICKED);
            shareInsuranceDetails();

        } else if ( v == btContinue ) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            defaultSharedPreferences.edit().remove("verifyDialogActionTaken").commit();
            HashMap<String, Object> upshotData = new HashMap<>();
            upshotData.put(Constant.UpshotEvents.INSURANCE_SUMMARY_DETAILS_CONTINUE_CLICKED,true);
            UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_SUMMARY_DETAILS_CONTINUE_CLICKED);
            Intent intent = new Intent(InsuranceCapturedActivity.this, ProfileActivity.class);
            intent.putExtra("goTo", Constant.INSURANCE_VIEW_SCREEN);
            Intent startIntent = new Intent(getApplicationContext(), StickyNotificationInsuranceFGService.class);
            startIntent.setAction(Constant.ACTION.START);
            //startService(startIntent);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_captured);

        try {
            findViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    public  void shareInsuranceDetails() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String url = getString(R.string.share_about_insurence);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(sendIntent, "Share using"));
    }
}
