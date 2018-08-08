package com.patientz.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.KeyValueObject;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.RecordSchemaAttributes;
import com.patientz.activity.R;
import com.patientz.adapters.HealthRecordAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotManager;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.utils.SchemaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class EhrViewFragment extends BaseListFragment {
    Context context;
    static EhrViewFragment emergencyHealthRecordFragment;
    static boolean check = true;
    String TAG = "EhrViewFragment";

    String verificationStatus;
    private View mListView;
    private View mLoaderStatusView;
    private LoadDataAsync loadDataAsync;

    public static EhrViewFragment newInstance(Context context) {

        if (emergencyHealthRecordFragment != null) {

        } else
            emergencyHealthRecordFragment = new EhrViewFragment();

        return emergencyHealthRecordFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        loadDataAsync = new LoadDataAsync();
        loadDataAsync.execute();
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.fragment_view_health_record,
                container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = getListView();
        mLoaderStatusView = view.findViewById(R.id.loading_status);
        super.onViewCreated(view, savedInstanceState);
    }

    private class LoadDataAsync extends AsyncTask<Void, Void, Void> {
        HealthRecordVO healthRecordVO = new HealthRecordVO();
        List<KeyValueObject> list = new ArrayList<KeyValueObject>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHandler dh = DatabaseHandler.dbInit(getActivity());
            try {

                DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
                long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
                Log.d(TAG,"Current_Selected_patient="+currentSelectedPatientId);
                PatientUserVO mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
                healthRecordVO = dh.getUserHealthRecord(Constant.RECORD_TYPE_EHR, mPatientUserVO.getPatientId());
                Log.d(TAG, "HEALTHRECORVO=" + healthRecordVO);
                Log.d(TAG, "record id=" + healthRecordVO.getId());
                Log.d(TAG, "health record=" + healthRecordVO.getHealthRecord());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                list = loadData(healthRecordVO);
                HashMap<String, Object> bkData = new HashMap<>();
                for (KeyValueObject keyValueObject : list) {

                    Log.e(TAG, "key===>" + keyValueObject.getKey());

                    bkData.put(keyValueObject.getKey(), TextUtils.isEmpty(keyValueObject.getValue()) ? "No" : "Yes");
                }
                UpshotManager.createHealthRecordCustomEvent(bkData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list != null) {
                HealthRecordAdapter health_record_adapter = new HealthRecordAdapter(
                        getActivity(), list);
                setListAdapter(health_record_adapter);
                showProgress(false);
            }
        }
    }

    private List<KeyValueObject> loadData(HealthRecordVO healthRecordVO) throws Exception {
        Log.d("1", "1");
        //LinkedHashMap<String, String> healthRecordMap = SchemaUtils.loadEmergencyHealthRecord(getActivity());
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        PatientUserVO mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
        HealthRecordVO mHealthRecordVO = mDatabaseHandler.getUserHealthRecord(Constant.RECORD_TYPE_EHR, mPatientUserVO.getPatientId());
        LinkedHashMap<String, String> healthRecordMap = mHealthRecordVO.getHealthRecord();
        Log.d(TAG, "HEALTH RECORD typE=" + mHealthRecordVO.getRecordType());
        List<KeyValueObject> list = new ArrayList<KeyValueObject>();
        if (healthRecordMap != null)
            for (String key : healthRecordMap.keySet()) {
                Log.d("3", "3");
                Log.d("EhrViewFragment", key);
                if (SchemaUtils.getDisplayNameFromSchemaKeysPreference(key,
                        getActivity(), Constant.EHR_SCHEMA_KEYS_PREF_FILENAME) != null) {
                    Log.d("4", "4");

                    KeyValueObject holder = new KeyValueObject();
                    if (!key.equalsIgnoreCase("preferredOrgBranchId") && !key.equalsIgnoreCase("preferredOrgId")) {
                        if (TextUtils.equals(key,
                                getResources().getString(R.string.pregnant))) {
                            if (SchemaUtils.getPatientData(getActivity())
                                    .getRecordVO()
                                    .getUserInfoVO().getGender() == 2) {
                                holder.setKey(SchemaUtils
                                        .getDisplayNameFromSchemaKeysPreference(
                                                key,
                                                getActivity(),
                                                Constant.EHR_SCHEMA_KEYS_PREF_FILENAME));
                                holder.setValue(healthRecordMap.get(key));
                                list.add(holder);

                            }
                        } else {
                            String displayName=SchemaUtils
                                    .getDisplayNameFromSchemaKeysPreference(key,
                                            getActivity(),
                                            Constant.EHR_SCHEMA_KEYS_PREF_FILENAME);
                            holder.setKey(displayName.equalsIgnoreCase(Constant.PREFERRED_HOSPITAL)?Constant.PREFERRED_EMERGENCY_PROVIDER:displayName);
                            holder.setValue(healthRecordMap.get(key));
                            list.add(holder);
                        }
                    }
                }
            }
        if (healthRecordVO != null && healthRecordVO.getLastUpdatedUserRoleId() == Constant.ROLE_DOCTOR) {
            verificationStatus = getString(R.string.verifiedBy)
                    + healthRecordVO
                    .getLastUpdatedUserDisplayName();

        } else {
            verificationStatus = getString(R.string.verificationPending);

        }
       /* KeyValueObject holder = new KeyValueObject();
        holder.setKey(getString(R.string.verification));
        holder.setValue(verificationStatus);
        list.add(holder);*/

        //}

        return list;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (loadDataAsync != null) {
            loadDataAsync.cancel(true);
        }
    }

    /**
     * Shows the progress UI and hides the list.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoaderStatusView.setVisibility(View.VISIBLE);
            mLoaderStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoaderStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mListView.setVisibility(View.VISIBLE);
            mListView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mListView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private boolean checkIfSpExists(String spFileName, String key) {

        ArrayList<RecordSchemaAttributes> mRecordSchemaAttributes = SchemaUtils
                .getSchemaAttributesFromSharedPreference(getActivity(),
                        spFileName, key);
        return mRecordSchemaAttributes == null;
    }
}
