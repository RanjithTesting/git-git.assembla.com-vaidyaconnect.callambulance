package com.patientz.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.os.ResultReceiver;

import com.patientz.activity.BuildConfig;
import com.patientz.activity.R;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServiceFetchAddress extends IntentService {
    private static final String TAG = "IntentServiceFetchAddress";
    protected ResultReceiver mReceiver;
    private Location location;

    public IntentServiceFetchAddress() {
        super("IntentServiceFetchAddress");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mReceiver = intent.getParcelableExtra(
                    getString(R.string.package_name) + "." + Constant.RECEIVER);
            location = intent.getParcelableExtra(
                    getString(R.string.package_name) + "." + Constant.LOCATION_DATA_EXTRA);
            Log.d(TAG, "Location=" + location);
            getLocationUsingGoogleMapsGeocodingApi(location);
        }
    }

    private void getLocationUsingGoogleMapsGeocodingApi(Location location) {
        JSONObject ret = getLocationInfo(location.getLatitude(), location.getLongitude());

        Log.d(TAG, "result=" + ret);

        try {
            deliverResultToReceiver(ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONObject getLocationInfo(double lat, double lng) {

        Log.d(TAG, "ADDRESS LOCATION=" + "https://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + getString(R.string.server_api_key));
        HttpGet httpGet = new HttpGet("https://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + getString(R.string.server_api_key));
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            mReceiver.send(Constant.SUCCESS_RESULT, null);
            Log.d(TAG, "exc=" + e.getMessage());

        }


        return jsonObject;

    }

    private String returnAddress(Address address) {
        String concatinatedAddress = null;
        if (address != null) {
            if (address.getFeatureName() != null) {
                concatinatedAddress = address.getFeatureName() + ",";
            }
            if (address.getSubLocality() != null) {
                concatinatedAddress = concatinatedAddress
                        + address.getSubLocality() + ",";
            }
            if (address.getLocality() != null) {
                concatinatedAddress = concatinatedAddress
                        + address.getLocality();
            }
            return getString(R.string.near) + " \t" + concatinatedAddress;
        } else {
            return " \t";
        }
    }

    private void deliverResultToReceiver(JSONObject addressObject) throws JSONException {
        try {
            JSONObject address;
            Bundle bundle = new Bundle();
            if (addressObject != null) {
                JSONArray addressesList = addressObject.getJSONArray("results");
                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString("lat", String.valueOf(location.getLatitude()));
                mEditor.putString("lon", String.valueOf(location.getLongitude()));
                address = addressesList.getJSONObject(0);
                JSONArray addressComponentsJsonArray = address.getJSONArray("address_components");
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {
                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("postal_code")) {
                        Log.d("POSTAL_CODE=",jsonObject.getString("long_name"));
                        bundle.putString(BuildConfig.APPLICATION_ID + "." + Constant.POSTAL_CODE, jsonObject.getString("long_name"));
                        mEditor.putString(Constant.POSTAL_CODE, jsonObject.getString("long_name")).commit();
                        break;
                    }
                }
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {
                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("administrative_area_level_1")) {
                        bundle.putString(getString(R.string.package_name) + "." + Constant.STATE, jsonObject.getString("long_name"));
                        bundle.putString(getString(R.string.package_name) + "." + Constant.STATE_SHORT_NAME, jsonObject.getString("short_name"));
                        mEditor.putString(Constant.STATE, jsonObject.getString("long_name")).commit();
                        mEditor.putString("state_short_name", jsonObject.getString("short_name")).commit();
                        Log.d("STATE=",jsonObject.getString("long_name"));

                        break;
                    }
                }
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {

                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("administrative_area_level_2")) {
                        bundle.putString(BuildConfig.APPLICATION_ID + "." + Constant.DISTRICT, jsonObject.getString("long_name"));
                        mEditor.putString(Constant.DISTRICT, jsonObject.getString("long_name")).commit();
                        Log.d("DISTRICT=",jsonObject.getString("long_name"));

                        break;
                    }
                }
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {

                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("political") && typesArray.toString().contains("locality") && !typesArray.toString().contains("sublocality")) {
                        bundle.putString(BuildConfig.APPLICATION_ID + "." + Constant.CITY, jsonObject.getString("long_name"));
                        mEditor.putString(Constant.CITY, jsonObject.getString("long_name")).commit();
                        Log.d("CITY=",jsonObject.getString("long_name"));

                        break;
                    }
                }
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {

                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("sublocality_level_1")) {
                        bundle.putString(BuildConfig.APPLICATION_ID + "." + Constant.LOCALITY, jsonObject.getString("long_name"));
                        Log.d("LOCALITY=",jsonObject.getString("long_name"));

                        break;
                    }
                }
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {

                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("sublocality_level_2")) {
                        bundle.putString(BuildConfig.APPLICATION_ID + "." + Constant.ADDRESS_LINE_1, jsonObject.getString("long_name"));
                        Log.d("ADDRESS_LINE_1=",jsonObject.getString("long_name"));
                        break;
                    }
                }
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {
                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("country")) {
                        bundle.putString(getString(R.string.package_name) + "." + Constant.COUNTRY, jsonObject.getString("long_name"));
                        bundle.putString(getString(R.string.package_name) + "." + Constant.COUNTRY_SHORT_NAME, jsonObject.getString("short_name"));
                        mEditor.putString(Constant.COUNTRY, jsonObject.getString("long_name")).commit();
                        mEditor.putString("country_short_name", jsonObject.getString("short_name")).commit();
                        Log.d("COUNTRY=",jsonObject.getString("long_name"));
                        break;
                    }
                }
                for (int i = 0; i < addressComponentsJsonArray.length(); i++) {
                    JSONObject jsonObject = addressComponentsJsonArray.getJSONObject(i);
                    JSONArray typesArray = jsonObject.getJSONArray("types");
                    if (typesArray.toString().contains("route")) {
                        bundle.putString(BuildConfig.APPLICATION_ID + "." + Constant.ADDRESS_LINE_2, jsonObject.getString("long_name"));
                        Log.d("ADDRESS_LINE_2=",jsonObject.getString("long_name"));
                        break;
                    }
                }

                bundle.putString(BuildConfig.APPLICATION_ID + "." + Constant.FORMATTED_ADDRESS, address.getString("formatted_address"));
                mEditor.putString("address", address.getString("formatted_address"));
                mEditor.commit();
                mReceiver.send(Constant.SUCCESS_RESULT, bundle);
            } else {
                mReceiver.send(Constant.SUCCESS_RESULT, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mReceiver.send(Constant.SUCCESS_RESULT, null);
        }
    }
}