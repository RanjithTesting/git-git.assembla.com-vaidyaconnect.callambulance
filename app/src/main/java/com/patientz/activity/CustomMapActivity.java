package com.patientz.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.patientz.utils.Log;

/**
 * Created by sunil on 13/1/17.
 */

public class CustomMapActivity extends BaseActivity implements OnMapReadyCallback {
    private String TAG = "CustomMapActivity";
    private MapFragment mapFragment;
    private LatLng latLng;
    private Location mLocation=new Location("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_map);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.title_activity_ambulance_providers_list);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvToolDone=(TextView) toolbar.findViewById(R.id.toolbar_done);
        tvToolDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startIntentServiceFetchAddress(mLocation);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latlng", latLng);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();


            }
        });
        Intent mIntent=getIntent();
        latLng=mIntent.getParcelableExtra("latlng");
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mLocation.setLatitude(latLng.latitude);
        mLocation.setLongitude(latLng.longitude);
        googleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.d("animate camera", "finish called");


            }

            @Override
            public void onCancel() {
                Log.d("animate camera", "cancel called");

            }
        });
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("centerLat", cameraPosition.target.latitude + "");
                Log.d("centerLong", cameraPosition.target.longitude + "");
                mLocation.setLatitude(cameraPosition.target.latitude);
                mLocation.setLongitude(cameraPosition.target.longitude);
                latLng=new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude);
            }
        });
    }
}

