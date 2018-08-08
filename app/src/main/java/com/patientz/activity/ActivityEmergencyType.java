package com.patientz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.patientz.adapters.AdapterEmergencyType;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.HashMap;

public class ActivityEmergencyType extends BaseActivity {

    private static final String TAG = "ActivityEmergencyType";
    long millisUntilFinishedd = 5000;
    private CountDownTimer countDownTimer;
    private Integer[] mThumbIds = {R.drawable.selector_emergency_type_head_injury, R.drawable.selector_emergency_type_heart_attack, R.drawable.selector_emergency_type_accident,
            R.drawable.selector_emergency_type_police, R.drawable.selector_emergency_type_pregnancy,
            R.drawable.selector_emergency_type_fire};
    private String[] mThumbText = {"Head Injury", "Heart Attack", "Accident", "Police", "Pregnancy",
            "Fire",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setPageTitle(toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GridView gridView = (GridView) findViewById(R.id.grid_view_emergency_types);
        TextView tvEmergencyTypeCounter = (TextView) findViewById(R.id.tv_emergency_type_counter);

        gridView.setAdapter(new AdapterEmergencyType(getApplicationContext(), R.layout.adapter_emergency_type, mThumbIds, mThumbText));
        //x AlphaAnimation mAlphaAnimation=new AlphaAnimation();xx
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(TAG, "********* grid item clicked ***********");
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.EMERGENCY_TYPE_SELECTED,true);
                upshotData.put(Constant.UpshotEvents.EMERGENCY_TYPE,mThumbText[position]);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_TYPE_SELECTED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit().putInt("selectedEmergencyType", position+1).commit();
                setEmergenceProviderAsEMRI(position);
                Intent intent = new Intent(ActivityEmergencyType.this,
                        ActivityMaps.class);
                startActivity(intent);
                finish();

            }
        });
        defaultSettingEmergencyTypeAsAccident(tvEmergencyTypeCounter);
    }

    private void setPageTitle(Toolbar toolbar) {
        SharedPreferences mSharedPreferences=PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (mSharedPreferences.getBoolean(Constant.IS_TEST_EMERGENCY,false)) {
            toolbar.setTitle(R.string.test);
        }else
        {
            toolbar.setTitle(R.string.title_activity_activity_emergency_type);
        }
    }

    private void setEmergenceProviderAsEMRI(int position) {
        if(position==Constant.EMERGENCTY_TYPE_POLICE || position==Constant.EMERGENCTY_TYPE_FIRE)
        {
            SharedPreferences mSharedPreferences = getApplicationContext()
                    .getSharedPreferences(Constant.COMMON_SP_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putString("emergencyNumber","108");
            mEditor.commit();

        }
    }

    private void defaultSettingEmergencyTypeAsAccident(final TextView tvEmergencyTypeCounter) {

        countDownTimer = new CountDownTimer(millisUntilFinishedd, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.EMERGENCY_TYPE_SELECTED,true);
                upshotData.put(Constant.UpshotEvents.EMERGENCY_TYPE,"Accident");
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMERGENCY_TYPE_SELECTED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());

                millisUntilFinishedd = millisUntilFinished;
                Log.d(TAG, "Starting emergency call in  "
                        + millisUntilFinished);
                Log.d(TAG, "Starting emergency call in  "
                        + (millisUntilFinished) / 1000 + " s");
                tvEmergencyTypeCounter.setText(((millisUntilFinished) / 1000) + getString(R.string.secs));
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "********* countDownTimer Finished ***********");
                cancel();
                Intent intent = new Intent(ActivityEmergencyType.this,
                        ActivityMaps.class);
                startActivity(intent);
                finish();
            }

        };
        countDownTimer.start();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "********* onRestoreInstanceState ***********");
        millisUntilFinishedd = savedInstanceState.getLong("counterTimeRemaining");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "********* onSaveInstanceState ***********");
        outState.putLong("counterTimeRemaining", millisUntilFinishedd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_kill:
                Log.d(TAG, "Killing Activity");
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "********* onStop ***********");
        if (countDownTimer != null) {
            Log.d(TAG, "countDownTimer cancelled");
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "********* onDestroy ***********");
    }
}
