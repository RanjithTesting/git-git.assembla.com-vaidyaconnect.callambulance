package com.patientz.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.patientz.VO.PatientUserVO;
import com.patientz.adapters.InvitationAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.services.CallAmbulanceSyncService;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.ArrayList;

public class ActivityInvitation extends BaseActivity {
    private static final String TAG = "ActivityInvitation";
    ArrayList<PatientUserVO> ulist;
    private BroadcastReceiver dbChangesReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private InvitationAdapter mAdapter;
    private  long patientId = 0;
    private ListView mListView;
    private ArrayList<PatientUserVO> uInvitations;
    private LinearLayout parentProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        TextView tvEmptyList = (TextView) findViewById(android.R.id.empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_activity_invitation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FINISH ACTIVITY");
                finish();
            }
        });
         parentProgressBar = (LinearLayout) findViewById(R.id.loading_status);
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        PatientUserVO mPatientUserVO = null;
        try {
            mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
            if (mPatientUserVO != null) {
                patientId = mPatientUserVO.getPatientId();
            }

            try {
                uInvitations = getInvitations();
                mAdapter = new InvitationAdapter(ActivityInvitation.this,
                        uInvitations, mListView, parentProgressBar, patientId);
                mListView.setAdapter(mAdapter);
            } catch (Exception e) {
                Log.d(TAG, "EXCEPTION=" + e.getMessage());
            }
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.ACTION_LOGIN_VERIFY_COMPLETE);

            dbChangesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equalsIgnoreCase(Constant.ACTION_LOGIN_VERIFY_COMPLETE)) {
                        int result=intent.getIntExtra("result",1);
                       switch (result)
                       {
                           case Constant.SUCCESS:
                               Log.d("SUCCUESS","SUCCUESS");
                               try {
                                   uInvitations = getInvitations();
                                   mAdapter = new InvitationAdapter(ActivityInvitation.this,
                                           uInvitations, mListView, parentProgressBar, patientId);
                                   mListView.setAdapter(mAdapter);
                               } catch (Exception e) {
                                   Log.d(TAG, "EXCEPTION=" + e.getMessage());
                               }
                               break;
                           case Constant.FAILED:
                               Log.d("SUCCUESS","SUCCUESS");
                               AppUtil.showToast(getApplicationContext(),"Sync Failed,Try again");
                               break;
                       }
                    }
                    if (action.equalsIgnoreCase("ACTION_TASKS_PERFORMED")) {
                        Log.d(TAG, "ACTION_TASKS_PERFORMED");
                        try {
                            ArrayList<PatientUserVO> uInvitations = getInvitations();
                            uInvitations.clear();
                            uInvitations.addAll(uInvitations);
                            mAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Log.d(TAG, "EXCEPTION=" + e.getMessage());
                        }

                    }
                }
            };
            mLocalBroadcastManager.registerReceiver(dbChangesReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PatientUserVO> getInvitations() throws Exception {
        DatabaseHandler dh = DatabaseHandler.dbInit(getApplicationContext());
        long currentSelectedUserId = AppUtil.getCurrentSelectedUserId(getApplicationContext());
        Log.d(TAG, "Querying for user id = " + currentSelectedUserId);
        ArrayList<PatientUserVO> invitationList = dh.getAllInvitationsForUser(currentSelectedUserId);
        Log.d(TAG, "NO OF INVITATIONS=" + invitationList.size());
        return invitationList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invitation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            AppUtil.showToast(getApplicationContext(), getString(R.string.toast_msg_sync_in_progress));
            Intent iservice = new Intent(this, CallAmbulanceSyncService.class);
            startService(iservice);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbChangesReceiver != null) {
            mLocalBroadcastManager.unregisterReceiver(dbChangesReceiver);
        }
    }
}
