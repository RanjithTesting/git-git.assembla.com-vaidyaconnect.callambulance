package com.patientz.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.patientz.VO.InsuranceVO;
import com.patientz.VO.KeyValueObject;
import com.patientz.VO.PatientUserVO;
import com.patientz.activity.R;
import com.patientz.adapters.KeyValueAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class InsuranceDetailsFragment extends BaseListFragment {

	private static final String TAG = "InsuranceDetailsFragment";
	TextView message;
	int menuItem;
	private View mListView;
	private View mLoaderStatusView;
	private LoadDataAsync loadDataAsync;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
/*		tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
		tracker.send(MapBuilder.createAppView().build());*/
		loadDataAsync = new LoadDataAsync();
		loadDataAsync.execute();
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menuItem != 0) {
			inflater.inflate(menuItem, menu);
		}
		Log.d("Insurance details", "onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View v = inflater.inflate(R.layout.fragment_insurancedetails,
				container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView = getListView();
		mLoaderStatusView = view.findViewById(R.id.loading_status);
	}

	private class LoadDataAsync extends AsyncTask<Void, Void, Void> {
		ArrayList<InsuranceVO> insuranceVOs;
		List<KeyValueObject> insuranceList = new ArrayList<KeyValueObject>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			DatabaseHandler dh = DatabaseHandler.dbInit(getActivity());
			PatientUserVO mPatientUserVO = null;
			try {
                
				DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(getActivity());
				long currentSelectedPatientId=AppUtil.getCurrentSelectedPatientId(getActivity());
				mPatientUserVO=mDatabaseHandler.getProfile(currentSelectedPatientId);
				insuranceVOs = dh.getAllInsurances(mPatientUserVO.getPatientId());
				//Log.d(TAG," Logged in user : "+AppUtil.convertToJsonString(mPatientUserVO));
				//Log.d(TAG," All insurances stored : "+AppUtil.convertToJsonString(insuranceVOs));
				insuranceList = loadData(insuranceVOs.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (insuranceList != null && insuranceVOs !=null) {

				KeyValueAdapter insurance_adapter = new KeyValueAdapter(
						getActivity(), insuranceList, "InsuranceDetailsFragment",insuranceVOs,null);
				setListAdapter(insurance_adapter);
				showProgress(false);
			}
		}
	}
	public List<KeyValueObject> loadData(InsuranceVO mInsuranceInfoVO) {
		List<KeyValueObject> insuranceList = new ArrayList<KeyValueObject>();

		//prepareMenu(mPatientUserVO);
		if (mInsuranceInfoVO != null) {

			KeyValueObject policyCompany = new KeyValueObject();
			KeyValueObject policyName = new KeyValueObject();
			KeyValueObject policyNo = new KeyValueObject();
			KeyValueObject policyStartDate = new KeyValueObject();
			KeyValueObject policyEndDate = new KeyValueObject();
			KeyValueObject policyCoverage = new KeyValueObject();
			KeyValueObject policyContactNumber = new KeyValueObject();
			policyCompany.setKey("Policy Provider");
			policyCompany.setValue(mInsuranceInfoVO.getInsPolicyCompany());
			insuranceList.add(policyCompany);
			policyName.setKey("Policy Name");
			policyName.setValue(mInsuranceInfoVO.getInsPolicyName());
			insuranceList.add(policyName);
			policyNo.setKey("Policy Number");
			policyNo.setValue(mInsuranceInfoVO.getInsPolicyNo());
			insuranceList.add(policyNo);
			policyStartDate.setKey("Policy Start Date");
			policyStartDate.setValue(DateFormat.format("E, MMM dd, yyyy",
					mInsuranceInfoVO.getInsPolicyStartDate()).toString());
			insuranceList.add(policyStartDate);
			policyEndDate.setKey("Policy End Date");
			policyEndDate.setValue(DateFormat.format("E, MMM dd, yyyy",
					mInsuranceInfoVO.getInsPolicyEndDate()).toString());
			insuranceList.add(policyEndDate);
			policyCoverage.setKey("Coverage");
			policyCoverage.setValue(mInsuranceInfoVO.getInsPolicyCoverage());
			insuranceList.add(policyCoverage);
			policyContactNumber.setKey("Claim Phone Number");
			// policyContactNumber.setValue(mInsuranceInfoVO.getPolicyContactNumber());
			policyContactNumber
					.setValue(mInsuranceInfoVO.getClaimPhoneNumber());
			insuranceList.add(policyContactNumber);
		}

		return  insuranceList;
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

}
