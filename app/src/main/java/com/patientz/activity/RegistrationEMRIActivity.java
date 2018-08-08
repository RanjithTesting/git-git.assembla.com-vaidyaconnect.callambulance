package com.patientz.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;

import com.patientz.VO.PatientUserVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.CustomProgressDialog;
import com.patientz.utils.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class RegistrationEMRIActivity extends FragmentActivity implements EMRIRegisterMobileFragment.EMRIRegistrationListener {

    private static final String TAG = "RegistrationEMRIActivity";
    private int backPressCount = 0;
    private String mobileNumber;
    private Fragment mContent;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emri_registration_v2);
        mBundle = getIntent().getExtras();
        EMRIRegisterMobileFragment emriRegisterMobileFragment = new EMRIRegisterMobileFragment();
        emriRegisterMobileFragment.setArguments(mBundle);
        switchContent(emriRegisterMobileFragment);
    }


    @Override
    public void onBackPressed() {
        switch (backPressCount) {
            case 0:
                AppUtil.showToast(getApplicationContext(), getString(R.string.mobileno_is_mandatory));
                break;
            case 1:
                AppUtil.showToast(getApplicationContext(), getString(R.string.pressback_onemoretime));
                break;
            case 2:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        backPressCount++;
    }

    private void switchContent(Fragment fragment) {
        mContent = fragment;
        if (mContent != null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.emergency_content_frame, mContent).commit();
    }

    @Override
    public void RegisterNumberWithEMRI(String number) {
        Log.d(TAG, "Registring number with EMRI");
        this.mobileNumber = number;
        RegistrationEMRI emri = new RegistrationEMRI(getApplicationContext());
        emri.execute();
    }

    private void insertNumberIntoDB(DatabaseHandler dh,
                                    final TelephonyManager tm, String mobileNumber) {
        try {
            dh.insertDeviceDetails(getApplicationContext(), mobileNumber, tm);
            SharedPreferences setting = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());// getPreferences(MODE_PRIVATE);
            setting.edit().putString("mobile_number", mobileNumber).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RegistrationEMRI extends AsyncTask<String, String, SoapPrimitive> {
        private Context context;
        private ProgressDialog dialog;


        public RegistrationEMRI(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new CustomProgressDialog(RegistrationEMRIActivity.this,
                    getString(R.string.wait_msg));
            dialog.show();
        }

        @Override
        protected SoapPrimitive doInBackground(String... params) {
            SoapPrimitive resultsString = EMRIWebServiceCall();
            return resultsString;
        }

        @Override
        protected void onPostExecute(SoapPrimitive result) {
            super.onPostExecute(result);
            dialog.dismiss();
            Log.d(TAG, "RESULT  FROM EMRI=" + result);

            if (result != null) {
                int resultCode = Integer.parseInt(result.toString());
                Log.d(TAG, "RESULT CODE FROM EMRI=" + resultCode);
                if (resultCode == 1) {
                    AppUtil.showToast(context, getString(R.string.reg_success));
                    DatabaseHandler dh = DatabaseHandler.dbInit(getApplicationContext());
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    insertNumberIntoDB(dh, tm, mobileNumber);
                    switchContent(new RegistrationSecondSIM());
                } else {
                    AppUtil.showToast(context, getString(R.string.reg_failed));
                }
            } else {
                switchContent(new RegistrationSecondSIM());
            }
        }

        @Override
        protected void onCancelled() {
            dialog.cancel();
            super.onCancelled();
        }

    }

    private SoapPrimitive EMRIWebServiceCall() {
        String NAMESPACE = "http://tempuri.org/";
        String METHOD_NAME = "InsertIncidentData";
        String SOAP_ACTION = "http://tempuri.org/InsertIncidentData";
        //String URL = "http://mapp.emri.in/Registrationwebservice/Service.asmx?";
        String URL = "http://mapp.emri.in/regdoctors/";
        PatientUserVO profileVO = new PatientUserVO();

        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapPrimitive resultsString = null;

        try {
            profileVO = AppUtil.getLoggedPatientVO(getApplicationContext());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Request.addProperty("EmailID", ""/*accounts[0].name*/);
        Request.addProperty("firstName", profileVO.getFirstName());
        Request.addProperty("lastname", profileVO.getLastName());
        Request.addProperty("mobile", mobileNumber);
        Request.addProperty("type", "Registration");
        Request.addProperty("integratingOrganization", "DOCTRZ");

		
		
		/*
         * Set the web service envelope
		 */
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(Request);
        HttpTransportSE ht = getHttpTransportSE(URL);

        try {
            ht.call(SOAP_ACTION, envelope);
            resultsString = (SoapPrimitive) envelope.getResponse();
            Log.d(TAG, "envelope request : " + ht.requestDump);
            Log.d(TAG, "envelope response: " + ht.responseDump);
            Log.d(TAG, "Response from EMRI : " + resultsString.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultsString;
    }

    private final HttpTransportSE getHttpTransportSE(String URL) {
        HttpTransportSE ht = new HttpTransportSE(URL, getResources().getInteger(R.integer.emri_call_timeout));
        ht.debug = true;
        ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        return ht;
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }

}
