package com.patientz.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.patientz.VO.KeyValueObject;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.activity.CallAmbulance;
import com.patientz.activity.R;
import com.patientz.adapters.KeyValueAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.upshot.Tag;
import com.patientz.upshot.UpshotManager;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DemographicsFragment extends BaseListFragment {
    public static final String TAG = "Demographics Fragment";
    // public Uri fileUri = null;
    // public String attachment_uri = "";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    static final String STATE_URI = "Attachment_uri";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    public static int attachmentId = 0;
    public static Uri fileUri = null;
    public static String attachment_uri = "";
    ImageView profilePicture;
    PatientUserVO mPatientUserVO;
    // Attachment variables
    String attachFileType;
    Button attachmentButton;
    private ProgressDialog dialog;
    private View mListView;
    private View mLoaderStatusView;
    private LoadDataAsync loadDataAsync;
    private Tracker tracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CallAmbulance application = (CallAmbulance) getActivity().getApplication();
        tracker = application.getDefaultTracker();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        tracker.setScreenName(Tag.getTagForScreen(getClass().getSimpleName()));
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        Log.d("SCREEN NAME=",this.getClass().getSimpleName());
        if(isAdded())
        {
            loadDataAsync = new LoadDataAsync();
            loadDataAsync.execute();
        }
        super.onResume();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        if (container == null) {
            return null;
        }
        return inflater.inflate(R.layout.fragment_demographics, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = getListView();
        mLoaderStatusView = view.findViewById(R.id.loading_status);
        super.onViewCreated(view, savedInstanceState);
    }

    public List<KeyValueObject> populateView(UserInfoVO mInfoVO) {

        //setHeader(pHeaderWidget, mInfoVO);
        List<KeyValueObject> contactList = new ArrayList<KeyValueObject>();
        KeyValueObject address = new KeyValueObject();
        KeyValueObject companyName = new KeyValueObject();

        KeyValueObject city = new KeyValueObject();
        KeyValueObject pinCode = new KeyValueObject();
        KeyValueObject phoneNumber = new KeyValueObject();
        KeyValueObject emailId = new KeyValueObject();
        KeyValueObject financialStatus = new KeyValueObject();
        KeyValueObject familyHistory = new KeyValueObject();
        KeyValueObject habits = new KeyValueObject();
        KeyValueObject remarks = new KeyValueObject();
        KeyValueObject maritalStatus = new KeyValueObject();
        KeyValueObject foodHabits = new KeyValueObject();
        KeyValueObject bloodDonation = new KeyValueObject();
        KeyValueObject organDonation = new KeyValueObject();
        KeyValueObject bloodGroup = new KeyValueObject();



        phoneNumber.setKey("Phone Number");
        phoneNumber.setValue(mInfoVO.getPhoneNumber());
        contactList.add(phoneNumber);

        emailId.setKey("Email ID");
        emailId.setValue(mInfoVO.getEmailId());
        contactList.add(emailId);

        bloodGroup.setKey("Blood Group");
        bloodGroup.setValue(mInfoVO.getBloodGroup());
        contactList.add(bloodGroup);

        bloodDonation.setKey("Willing To Donate Blood?");
        bloodDonation.setValue(mInfoVO.getBloodDonation());
        contactList.add(bloodDonation);


        KeyValueObject lastBloodDonationDate = new KeyValueObject();
        KeyValueObject preferredBloodBank = new KeyValueObject();
        KeyValueObject notifyBloodDonationRequest = new KeyValueObject();
        if(mInfoVO.getPreferredBloodBankId()!=0)
        {
        DatabaseHandlerAssetHelper mDatabaseHandlerAssetHelper=DatabaseHandlerAssetHelper.dbInit(getActivity());
        OrgBranchVO preferredBloodBankVO=mDatabaseHandlerAssetHelper.getPreferredOrgBranch(mInfoVO.getPreferredBloodBankId());
        preferredBloodBank.setKey("Preferred Blood Bank");
        preferredBloodBank.setValue(preferredBloodBankVO.getDisplayName()+(!TextUtils.isEmpty(preferredBloodBankVO.getTelephone())?(","+preferredBloodBankVO.getTelephone()):""));
        contactList.add(preferredBloodBank);
        }


        if(mInfoVO.getLastBloodDonationDate()!=null)
        {
            SimpleDateFormat mSimpleDateFormat=new SimpleDateFormat(getString(R.string.display_date_format));
            lastBloodDonationDate.setKey("Last Blood Donation Date");
            lastBloodDonationDate.setValue(mSimpleDateFormat.format(mInfoVO.getLastBloodDonationDate()));
            contactList.add(lastBloodDonationDate);
        }


        notifyBloodDonationRequest.setKey("Notify About Blood Donation?");
        if(mInfoVO.isNotifyBloodDonationRequest())
        {
            notifyBloodDonationRequest.setValue("Yes");
        }else
        {
            notifyBloodDonationRequest.setValue("No");
        }
        contactList.add(notifyBloodDonationRequest);


        organDonation.setKey("Willing To Donate Organ?");
        organDonation.setValue(mInfoVO.getOrganDonation());
        contactList.add(organDonation);

        address.setKey("Address");
        address.setValue(mInfoVO.getAddress());

        address.setKey("Company Name");
        address.setValue(mInfoVO.getCompanyName());
        contactList.add(address);

        city.setKey("City, State, Country");
        city.setValue(checkNullForStateCityCountry(mInfoVO));
        contactList.add(city);

        pinCode.setKey("Pincode");
        pinCode.setValue(mInfoVO.getPinCode());
        contactList.add(pinCode);

        maritalStatus.setKey("Marital Status");
        maritalStatus.setValue(mInfoVO.getMaritalStatus());
        contactList.add(maritalStatus);

        foodHabits.setKey("Food Habits");
        foodHabits.setValue(mInfoVO.getFoodHabits());
        contactList.add(foodHabits);

        Log.e("###Blood Group", "-->" + mInfoVO.getBloodGroup());



        familyHistory.setKey("Family History");
        familyHistory.setValue(mInfoVO.getFamilyHistory());
        contactList.add(familyHistory);

        financialStatus.setKey("Financial Status");
        financialStatus.setValue(mInfoVO.getFinancialStatus());
        contactList.add(financialStatus);

        habits.setKey("Habits");
        habits.setValue(mInfoVO.getHabits());
        contactList.add(habits);

        remarks.setKey("Remarks");
        remarks.setValue(mInfoVO.getRemarks());
        contactList.add(remarks);
        return contactList;
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

    public String checkNullForStateCityCountry(UserInfoVO mUserInfoVO) {
        String cityStateCountry = "";
        String comma = ",";
        if (mUserInfoVO.getCity() != null) {
            cityStateCountry = mUserInfoVO.getCity() + comma;
        }
        if (mUserInfoVO.getState() != null) {
            cityStateCountry = cityStateCountry + mUserInfoVO.getState()
                    + comma;
        }
        if (mUserInfoVO.getCountry() != null) {
            cityStateCountry = cityStateCountry + mUserInfoVO.getCountry();
        }
        return cityStateCountry;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, attachment_uri);
                    //Uri uri = Uri.parse(attachment_uri);
                    setAttachmentUriForImageCapturedThroughCamera();
                    //	makeWebServiceCall();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled the image capture
                    fileUri = null;
                } else {
                    // Image capture failed, advise user
                }
                break;
            case SELECT_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    fileUri = data.getData();
                    attachment_uri = getRealPathFromURI(fileUri); // from Gallery
                    if (attachment_uri == null)
                        attachment_uri = fileUri.getPath(); // from File Manager
                    Uri realPath = Uri.parse(attachment_uri);
                    //makeWebServiceCall();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    fileUri = null;
                } else {
                    // Launch fail
                }

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void setAttachmentUriForImageCapturedThroughCamera() {
        SharedPreferences sp = getActivity().getSharedPreferences(
                Constant.FILE_ATTACHMENT_URI_CONTAINER, Activity.MODE_PRIVATE);
        attachment_uri = sp.getString("attachmentUri", "");
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null,
                null, null);

        if (cursor == null)
            return null;

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private class LoadDataAsync extends AsyncTask<Void, Void, Void> {
        UserInfoVO infoVO = new UserInfoVO();
        List<KeyValueObject> contactList = new ArrayList<KeyValueObject>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHandler dh = DatabaseHandler.dbInit(getActivity());
            PatientUserVO patientUserVO = null;
            try {
                DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
                long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
                patientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
                infoVO = dh.getUserInfo(patientUserVO.getPatientId());
                contactList = populateView(infoVO);

                Log.e("infoVO--->", "--->" + infoVO.getBloodGroup());

                HashMap<String, Object> bkData = new HashMap<>();
                for (KeyValueObject keyValueObject : contactList) {
                    bkData.put(keyValueObject.getKey(), TextUtils.isEmpty(keyValueObject.getValue()) ? "No" : "Yes");
                }
                UpshotManager.createProfileCustomEvent(bkData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (infoVO != null) {

                KeyValueAdapter contact_adapter = new KeyValueAdapter(getActivity(),
                        contactList, "DemographicsFragment", null, infoVO);
                setListAdapter(contact_adapter);
                showProgress(false);
            }
        }
    }


}
