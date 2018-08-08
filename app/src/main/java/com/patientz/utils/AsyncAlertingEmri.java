package com.patientz.utils;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.patientz.VO.PatientUserVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.webservices.WebServiceUrls;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AsyncAlertingEmri extends AsyncTask<String[], String, SoapPrimitive> {

    Exception exc;
    private String TAG = "AsyncAlertingEmri";
    private Context applicationContext;
    private String emergencyToken;
    private String address;
    private long patientId;

    private Location mLastLocation;
    String NAMESPACE = "http://tempuri.org/";
    String METHOD_NAME = "InsertIncidentData";
    String SOAP_ACTION = "http://tempuri.org/InsertIncidentData";
    String URL = "http://mapp.emri.in/EMRIWebservice1/";

    public AsyncAlertingEmri(Context applicationContext, Location mLastLocation, String emergencyToken, String address, long patientId) {
        Log.d(TAG, "ASYNC ALERTING EMRI CALLED");
        this.applicationContext = applicationContext;
        this.mLastLocation = mLastLocation;
        this.emergencyToken = emergencyToken;
        this.patientId=patientId;
        this.address = address;
        Log.d(TAG, "LAST LOCATION=" + mLastLocation);
        Log.d(TAG, "EMERGENCY TOKEN =" + emergencyToken);
        Log.d(TAG, "ADDRESS=" + address);
        Log.d(TAG, "LAST LOCATION=" + mLastLocation);

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected SoapPrimitive doInBackground(String[]... params) {
        SoapPrimitive resultsString = getResult();
        return resultsString;
    }

    @Override
    protected void onPostExecute(SoapPrimitive response) {
        super.onPostExecute(response);

    }

    private SoapPrimitive getResult() {

        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapPrimitive resultsString = null;

		/*SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(applicationContext);

		Request.addProperty("mobileNumber",
				sp.getString("mobile_number", ""));*/
        if (mLastLocation != null) {
            Log.d(TAG, "LAST LOCATION=" + mLastLocation);
            Log.d(TAG, "LAST LOCATION lat=" + mLastLocation.getLatitude());
            Log.d(TAG, "LAST LOCATION long=" + mLastLocation.getLongitude());
            Log.d(TAG, "LAST LOCATION accuracy=" + mLastLocation.getAccuracy());
            Request.addProperty("latitude", String.valueOf(mLastLocation.getLatitude()));
            Request.addProperty("longitude", String.valueOf(mLastLocation.getLongitude()));
            Request.addProperty("AccLatLong", String.valueOf(mLastLocation.getAccuracy()));
        } else {
            Request.addProperty("latitude", "0.0");
            Request.addProperty("longitude", "0.0");
            Request.addProperty("AccLatLong", "0.0");
        }

        Request.addProperty("mobileNumber",getPhoneNumber()); // Send mobile number of emergency patient without +91-


        Request.addProperty("calldtm", new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss").format(Calendar.getInstance()
                .getTime()));
        Request.addProperty("integratingOrganization", "DOCTRZ");

        if (emergencyToken != null) {
            Request.addProperty(
                    "urlstring", WebServiceUrls.serverEmergencyUrl + emergencyToken);
        } else {
            Request.addProperty("urlstring", WebServiceUrls.stranger_emergency_url);
        }
        Request.addProperty("emergencyReferenceNo", 0);
        Request.addProperty("address", address);
        Request.addProperty("ercpflag", "Yes");

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

    private Object getPhoneNumber() {
        String mobileNumber = null;

        try {
            DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(applicationContext);
            PatientUserVO mPatientUserVO=mDatabaseHandler.getProfile(patientId);
            String phoneNumber=mPatientUserVO.getPhoneNumber();
            if(!TextUtils.isEmpty(phoneNumber))
            {
                if(phoneNumber.contains("+91-"))                {
                    String[] splitMobileNO=phoneNumber.split("-");
                    mobileNumber=splitMobileNO[1];
                }else                {
                    mobileNumber=phoneNumber;
                }

            }else
            {
                if(!TextUtils.isEmpty(mDatabaseHandler.getSelfUsersPhoneNumber()))
                {
                    if(mDatabaseHandler.getSelfUsersPhoneNumber().contains("+91-")) {
                        String[] splitMobileNO = mDatabaseHandler.getSelfUsersPhoneNumber().split("-");
                        mobileNumber = splitMobileNO[1];
                    }else
                    {
                        mobileNumber=mDatabaseHandler.getSelfUsersPhoneNumber();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG,"AsyncAlertingEmri>>mobileNumber"+mobileNumber);
        return  mobileNumber;
    }

    private final HttpTransportSE getHttpTransportSE(String URL) {
        HttpTransportSE ht = new HttpTransportSE(URL, applicationContext.getResources().getInteger(R.integer.emri_call_timeout));
        ht.debug = true;
        ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        return ht;
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(
            SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }
}
