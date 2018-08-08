package com.patientz.activity;

import android.app.Activity;
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

import com.patientz.utils.Log;

public class EMRIRegisterMobileFragment extends Fragment implements
		OnClickListener {
	private static final String TAG = "EMRIRegister Fragment";
	private Button nextButton;
	private String mobileNumber;
	private EditText mobileEditText;
	private View view;
	private EMRIRegistrationListener emriRegistrationListener;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		 view = inflater.inflate(R.layout.activity_registration_emri, null);
		mobileEditText = (EditText) view.findViewById(R.id.editText_sim_number);
		nextButton = (Button) view.findViewById(R.id.nextButton);
		nextButton.setOnClickListener(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		if(getArguments()!=null){
			if(getArguments().getBoolean("loginUsingMobile")){
				mobileEditText.setText(getArguments().getString("username"));
			}else{
				mobileEditText.setText(getArguments().getString("mobileNumber"));
			}
		}
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.nextButton) {
			Log.d(TAG, "NEXT BUTTON CLICKED");
			EditText EV = (EditText) view.findViewById(R.id.editText_sim_number);
			mobileNumber = EV.getText().toString();
			if (TextUtils.isEmpty(mobileNumber)
					|| mobileNumber.trim().length() != 10) {
				EV.setError(getString(R.string.mobile_no_should_10_digits));
			} else {
				emriRegistrationListener.RegisterNumberWithEMRI(mobileNumber);
			}
		}		
	}
	
	public interface EMRIRegistrationListener{
		void RegisterNumberWithEMRI(String number);
	}
	
	@Override
	public void onAttach(Activity activity) {
		try {
			emriRegistrationListener = (EMRIRegistrationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
		super.onAttach(activity);
	}
	
}
