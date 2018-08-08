package com.patientz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.patientz.VO.PatientUserVO;
import com.patientz.adapters.AdapterCurrentEmergencies;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.DividerItemDecoration;
import com.patientz.utils.Log;
import com.patientz.utils.SortPatientUserVO;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityCurrentEmergencies extends BaseActivity {

    private static final String TAG = "ActivityCurrentEmergencies";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PatientUserVO> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_emergencies);

        TextView tvEmptyList = (TextView) findViewById(R.id.tv_empty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.actionbar_back);
        toolbar.setTitle(R.string.title_activity_current_emergencies);
        // toolbar.inflateMenu(R.menu.menu_toolbar_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        usersList = mDatabaseHandler.getUsersInEmergencies();
        SortPatientUserVO mSortPatientUserVO = new SortPatientUserVO();
            /* Adding stranger in case of emergency */
        Collections.sort(usersList, mSortPatientUserVO);
        if (usersList.size() != 0) {
            tvEmptyList.setVisibility(View.GONE);
            mAdapter = new AdapterCurrentEmergencies(getApplicationContext(), usersList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            tvEmptyList.setVisibility(View.VISIBLE);
            tvEmptyList.setText(R.string.no_patients_in_emergencies);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_cancel, menu);
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (usersList.size() != 0) {
            ((AdapterCurrentEmergencies) mAdapter).setOnItemClickListener(new AdapterCurrentEmergencies.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    long patientId = usersList.get(position).getPatientId();
                    Intent mIntent = new Intent(ActivityCurrentEmergencies.this, ActivityEmergencyTrackAndRevoke.class);
                    mIntent.putExtra("patientId", patientId);
                    SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    defaultSp.edit().putString("emergency_token", usersList.get(position).getEmergencyToken()).commit();
                    startActivity(mIntent);
                    finish();


                }
            });
        }
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
    }


    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private Context appContext;
        private RecyclerView mRecyclerView;
        private GestureDetector gestureDetector;
        private Clicklistener clicklistener;

        public RecyclerTouchListener(Context applicationContext, final RecyclerView mRecyclerView, final Clicklistener clicklistener) {
            appContext = applicationContext;
            this.mRecyclerView = mRecyclerView;
            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    Log.d(TAG, "onInterceptTouchEvent");
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, mRecyclerView.getChildPosition(child));

                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && gestureDetector.onTouchEvent(e) && clicklistener != null) {
                Log.d(TAG, "onInterceptTouchEvent");

            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.d(TAG, "onTouchEvent");

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface Clicklistener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }
}

