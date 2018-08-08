package com.patientz.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.patientz.VO.PatientUserVO;
import com.patientz.utils.AppUtil;
import com.patientz.utils.CustomProgressDialog;
import com.patientz.utils.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class RegistrationSecondSIM extends Fragment implements OnClickListener {

    private static final String TAG = "EMRIRegisterMobileFragment";
    private Button submitButton, skipButton;
    private String mobileNumber;
    private View view;
    private TextView topMessage;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_registration_emri, null);
        submitButton = (Button) view.findViewById(R.id.nextButton);
        skipButton = (Button) view.findViewById(R.id.skipButton);
        skipButton.setVisibility(View.VISIBLE);
        topMessage = (TextView) view.findViewById(R.id.mobile_number_text);
        topMessage.setText(getActivity().getResources().getString(R.string.mobile_number_2nd_sim_msg));
        submitButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextButton) {
            EditText EV = (EditText) view.findViewById(R.id.editText_sim_number);
            mobileNumber = EV.getText().toString();
            if (TextUtils.isEmpty(mobileNumber)
                    || mobileNumber.trim().length() != 10) {
                EV.setError(getString(R.string.mobile_no_should_10_digits));
            } else {
                RegistrationEMRI registrationEMRI = new RegistrationEMRI(getActivity());
                registrationEMRI.execute();
            }
        } else if (v.getId() == R.id.skipButton) {
            getActivity().setResult(getActivity().RESULT_OK);
            getActivity().finish();
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
            dialog = new CustomProgressDialog(getActivity(),
                    getString(R.string.wait_msg));
            dialog.show();
        }

        @Override
        protected SoapPrimitive doInBackground(String... params) {
            //SoapPrimitive resultsString = EMRIWebServiceCall();
            return null;//resultsString;
        }

        @Override
        protected void onPostExecute(SoapPrimitive result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null) {
                int resultCode = Integer.parseInt(result.toString());
                if (resultCode == 0) {
                    AppUtil.showToast(context, "Registatrion 2nd number successful");
                    getActivity().setResult(getActivity().RESULT_OK);
                    getActivity().finish();
                } else {
                    AppUtil.showToast(context, "Registatrion failed");
                }
            } else {
                getActivity().setResult(getActivity().RESULT_OK);
                getActivity().finish();
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
        String URL = "http://mapp.emri.in/Registrationwebservice/Service.asmx?";
        PatientUserVO profileVO = new PatientUserVO();

        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapPrimitive resultsString = null;

        try {
            profileVO = AppUtil.getLoggedPatientVO(getActivity());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        AccountManager mAccountManager = AccountManager.get(getActivity());
        Account[] accounts = mAccountManager
                .getAccountsByType(getString(R.string.package_name));
        Request.addProperty("EmailID", accounts[0].name);
        Request.addProperty("firstName", profileVO.getFirstName());
        Request.addProperty("lastname", profileVO.getLastName());
        Request.addProperty("mobile", mobileNumber);
        Request.addProperty("type", "DOCTRZ");

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
        HttpTransportSE ht = new HttpTransportSE(URL);
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
