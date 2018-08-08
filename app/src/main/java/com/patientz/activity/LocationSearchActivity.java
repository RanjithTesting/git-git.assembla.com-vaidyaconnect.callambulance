package com.patientz.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.patientz.adapters.PlaceAutocompleteAdapter;
import com.patientz.service.IntentServiceFetchAddress;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

public class LocationSearchActivity extends BaseActivity  implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "LocationSearchActivity";
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;
    private ListView recylerView;
    private LatLng mLatLon;
    private AddressResultReceiver mResultReceiver;
    private Intent mIntent;


    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent=getIntent();

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mResultReceiver = new AddressResultReceiver(new Handler());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.activity_location_search);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);
        mAutocompleteView.setThreshold(0);
        if(mIntent.getStringExtra("source")==null) {
            mAutocompleteView.setHint(getString(R.string.label_search_location));
        }else {
            mAutocompleteView.setHint(getString(R.string.label_location_search_ambulance_hint));
        }
        recylerView=(ListView)
                findViewById(R.id.recylerView);
        final ImageView clearButton = (ImageView) findViewById(R.id.iv_org_places_cancel);
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
            }
        });
        final TextView tvNoNetwork = (TextView) findViewById(R.id.tv_no_internet);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });
        // Register a listener that receives callbacks when a suggestion has been selected

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        recylerView.setAdapter(mAdapter);

        //mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG,"onTextChanged");
                Log.d(TAG,"charSequence="+charSequence);
                if (AppUtil.isOnline(getApplicationContext())) {
                    if(!TextUtils.isEmpty(mAutocompleteView.getText())) {
                        if (tvNoNetwork.getVisibility() == View.VISIBLE)
                            tvNoNetwork.setVisibility(View.GONE);

                        if (recylerView.getVisibility() == View.GONE)
                            recylerView.setVisibility(View.VISIBLE);
                        mAdapter.getFilter().filter(charSequence);
                    } else {
                        Log.d(TAG,"SETTING ADAPTER NULL");
                        recylerView.setVisibility(View.GONE);
                    }
                }else
                {
                    recylerView.setVisibility(View.GONE);
                    tvNoNetwork.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {



            }
        });
        recylerView.setOnItemClickListener(mAutocompleteClickListener);
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(GoogleApiClient,
     * String...)
     */


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            mLatLon=place.getLatLng();


            if(mIntent.getStringExtra("source")==null){

                if(place!=null) {
                    Log.d(TAG, "ADDRESS=" + place.getAddress());
                    Log.d(TAG, "Attribution=" + place.getAttributions());
                    Log.d(TAG, "LOCALE=" + place.getLocale());
                    Log.d(TAG, "NAME=" + place.getName());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("latlng", mLatLon);
                    resultIntent.putExtra("locality", place.getAddress());
                    resultIntent.putExtra("tab_position",getIntent().getIntExtra("tab_position",0));
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }

                Log.i(TAG, "Place details received: " + place.getName());
            }else
            {
                Intent mIntent = new Intent(LocationSearchActivity.this, CustomMapActivity.class);
                mIntent.putExtra("latlng",mLatLon);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(mIntent);
                finish();
            }
            // Get the Place object from the buffer.


            places.release();
        }
    };
    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            Log.d(TAG, "ON RECEIVE CALLED");
            try {
                if (resultData != null) {
                    Intent mIntent = new Intent(LocationSearchActivity.this, NearbyOrgsActivity.class);
                    mIntent.putExtra("latLon", mLatLon);

                    String locality=resultData.getString(getString(R.string.package_name) + "." + Constant.FORMATTED_ADDRESS, "");
                            //","+ resultData.getString(getString(R.string.package_name) + "." + Constant.LOCALITY, "");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("latlng", mLatLon);
                    resultIntent.putExtra("locality", locality);
                    resultIntent.putExtra("tab_position",getIntent().getIntExtra("tab_position",0));
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
