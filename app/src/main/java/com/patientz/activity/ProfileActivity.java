package com.patientz.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.databases.DatabaseHandler;
import com.patientz.fragments.DemographicsFragment;
import com.patientz.fragments.EhrViewFragment;
import com.patientz.fragments.InsuranceFragment;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CircleTransform;
import com.patientz.utils.Constant;
import com.patientz.utils.ImageUtil;
import com.patientz.utils.Log;
import com.patientz.utils.MultipartRequest;
import com.patientz.webservices.WebServiceUrls;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ProfileActivity extends BaseActivity implements ViewPager.OnPageChangeListener,InsuranceFragment.OnListFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TextView profileName, profileAge, profileGender;
    private ImageView profilePic;
    private AppBarLayout appBarLayout;
    private static final String INSURANCE_ID = "id";
    private final int RESPONSE_CODE_ADD_NEW_INSURANCE = 789;
    private TabLayout tabLayout;
    public static Uri fileUri = null;
    public static String attachment_uri = "";
    private PatientUserVO patientUserVO;
    private String mCurrentPhotoPath;
    private ProgressBar progressBar;
    private DatabaseHandler mDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "*********** onCreate ***************");
        setContentView(R.layout.activity_profile);
        mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
        try {
            patientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
        }catch (Exception e)
        {

        }
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        profileName = (TextView) findViewById(R.id.profile_name);
        profileAge = (TextView) findViewById(R.id.profile_age);
        profileGender = (TextView) findViewById(R.id.profile_gender);
        profilePic = (ImageView) findViewById(R.id.profile_avatar);
        profilePic.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(mViewPager);
        Intent mIntent=getIntent();
        if(mIntent!=null)
        {
            switch (mIntent.getIntExtra("goTo",0))
            {
                case Constant.EMHR_VIEW_SCREEN:
                    mViewPager.setCurrentItem(Constant.EMHR_VIEW_SCREEN);
                    break;
                case Constant.INSURANCE_VIEW_SCREEN:
                    mViewPager.setCurrentItem(Constant.INSURANCE_VIEW_SCREEN);
                    break;
            }
        }
    }

    private void setUpProfileDetails() {
        try
        {
            UserInfoVO userInfoVO = mDatabaseHandler.getUserInfo(patientUserVO.getPatientId());
            profileName.setText(AppUtil.convertToCamelCase(userInfoVO.getFirstName()) + " "
                    + AppUtil.convertToCamelCase(userInfoVO.getLastName()));
            Log.d(TAG, AppUtil.convertToCamelCase(userInfoVO.getFirstName()) + " "
                    + AppUtil.convertToCamelCase(userInfoVO.getLastName()));
            profileAge.setText(AppUtil.convertToAge(userInfoVO.getDateOfBirth(), getApplicationContext()) + "  ");
            Log.d(TAG, "age " + AppUtil.convertToAge(userInfoVO.getDateOfBirth(), getApplicationContext()));
            profileGender.setText((userInfoVO.getGender() == 2 ? getString(R.string.female) : getString(R.string.male)));
            setProfileImage(patientUserVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProfileImage(PatientUserVO mPatientUserVO) {
        boolean isFileExists = false;
        if (mPatientUserVO != null) {
            isFileExists = AppUtil.checkIfFileExists(getApplicationContext(),
                    mPatientUserVO.getPicName());
            Log.d(TAG, "File Exists ???" + isFileExists);

            if (!isFileExists) {
                if (mPatientUserVO.getPicId() > 0 && AppUtil.isOnline(getApplicationContext())) {
                    // call webservice to download the image if exists
                    String imageUrl =WebServiceUrls.getPatientAttachment+mPatientUserVO.getPicId()+"&patientId="+mPatientUserVO.getPicId()+"&moduleType="+ Constant.IMAGE_MODULE_TYPE_PROFILE_PIC;
                    AppUtil.downloadImage(ProfileActivity.this, imageUrl, profilePic, mPatientUserVO.getPicName());
                } else {
                    Picasso.with(this).load(R.drawable.emergency_default_profile).transform(new CircleTransform()).into(profilePic);
                }
            } else {
                String localPath = Environment.getExternalStorageDirectory()
                        .getPath()
                        + getResources().getString(
                        R.string.profileImagePath);
                File f = new File(localPath + mPatientUserVO.getPicName());
                Picasso.with(this).load(f).transform(new CircleTransform()).into(profilePic);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!TextUtils.isEmpty(patientUserVO.getRole())) {
            if (!patientUserVO.getRole().equalsIgnoreCase(Constant.EMERGENCY_CONTACT)) {
                getMenuInflater().inflate(R.menu.menu_profile, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_edit) {
            //AppUtil.showToast(getApplicationContext(),"yet to be implemented");
            //return true;
            Log.d(TAG, "CURRENT SELECTED ITEM=" + mViewPager.getCurrentItem());
            Intent mIntent = null;
            int currentItem = mViewPager.getCurrentItem();
            if (mViewPager.getCurrentItem() == 0) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.PROFILE_EDIT_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.PROFILE_EDIT_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                mIntent = new Intent(this, ActivityEditProfile.class);
                mIntent.putExtra("key", "edit");
            } else if (currentItem == 2) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.INSURANCE_EDIT_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_EDIT_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                mIntent = new Intent(this, ActivityEditInsurance.class);
                // mIntent.putExtra("key", "edit");
            } else if (currentItem == 1) {
                HashMap<String, Object> upshotData = new HashMap<>();
                upshotData.put(Constant.UpshotEvents.EMHR_EDIT_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMHR_EDIT_CLICKED);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                mIntent = new Intent(this, ActivityEHRUpdate.class);
                mIntent.putExtra("key", "edit");
            }
            startActivity(mIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(InsuranceVO item) {
        Intent intent = new Intent(this, InsuranceDetailsActivity.class);
        if (item != null) {
            Log.d("onListFragmentInteraction", "insurance id : " + item.getInsuranceId());
            intent.putExtra(INSURANCE_ID, item.getInsuranceId());
            startActivityForResult(intent, Constant.REQUEST_CODE_INSURANCE_UPDATE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_avatar:

                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, Constant.REQUEST_CODE_CAMERA_INTENT);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        final String STORAGE_PATH = getApplicationContext()
                .getResources().getString(
                        R.string.profileImagePath);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        File storageDir = new File(Environment
                .getExternalStorageDirectory(), STORAGE_PATH);
        Log.d(TAG, "storageDir=" + storageDir);
        File image = File.createTempFile(
                imageFileName,   //prefix
                ".jpg",          //suffix
                storageDir       //directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult\nrequestCode,resultCode="+requestCode+","+resultCode);
        if (requestCode == Constant.REQUEST_CODE_CAMERA_INTENT && resultCode == RESULT_OK) {
            //setPic();
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue mRequestQueue = AppVolley.getRequestQueue();
            mRequestQueue.add(createFileRequest());

        }

        if (requestCode == Constant.REQUEST_CODE_GALLERY_INTENT && resultCode == RESULT_OK) {
            //setPic();
            Uri fileUri = data.getData();
            mCurrentPhotoPath = getRealPathFromURI(fileUri); // from Gallery
            if (mCurrentPhotoPath == null)
                mCurrentPhotoPath = fileUri.getPath();
            progressBar.setVisibility(View.VISIBLE);

            RequestQueue mRequestQueue = AppVolley.getRequestQueue();
            mRequestQueue.add(createFileRequest());

        }
        if(requestCode == Constant.REQUEST_CODE_INSURANCE_UPDATE)
        {
            Log.d("refreshing fragment","Insurance");
            mViewPager.setAdapter(null);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);
            mViewPager.setCurrentItem(2);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null,
                null, null);

        if (cursor == null)
            return null;

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private MultipartRequest createFileRequest() {

        boolean isExternalStorageWriteable = ImageUtil.isExternalStorageWriteable();
        // String filePath = "";
        //File[] mFiles;
        MultipartRequest multipartRequest = null;
        String szServerUrl =
                WebServiceUrls.serverUrl + WebServiceUrls.insertPatientProfilePicture;
        if (isExternalStorageWriteable) {

            multipartRequest = new MultipartRequest(getApplicationContext(), patientUserVO.getPatientId(), mCurrentPhotoPath,
                    szServerUrl, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    com.patientz.utils.Log.d(TAG, "RESPONSE \n" + response);
                    ResponseVO responseVo = null;
                    try {
                        if (response != null) {
                            Gson gson = new Gson();
                            Type objectType = new TypeToken<ResponseVO>() {
                            }.getType();

                            responseVo = gson.fromJson(
                                    response, objectType);

                            if (responseVo != null) {
                                responseHandler(responseVo);
                            } else {
                                responseHandler(responseVo);
                            }
                        } else {
                            responseHandler(responseVo);
                        }
                    } catch (JsonParseException e) {
                        responseHandler(responseVo);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.INVISIBLE);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        switch (networkResponse.statusCode)
                        {
                            case Constant.HTTP_CODE_SERVER_BUSY:
                                AppUtil.showErrorCodeDialog(ProfileActivity.this);
                                break;
                        }
                    }else
                    {
                        AppUtil.showErrorDialog(getApplicationContext(),error);

                    }
                }
            });
        } else {
            Log.d(TAG, "sd card not writeable");
            progressBar.setVisibility(View.INVISIBLE);
        }
        return multipartRequest;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpProfileDetails();
    }

    private void responseHandler(ResponseVO mResponseVO) {
        progressBar.setVisibility(View.INVISIBLE);
        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:
                    AppUtil.showToast(getApplicationContext(), "Profile Pic Uploaded");
                     mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
                    try {
                        mDatabaseHandler.insertUsers(mResponseVO.getPatientUserVO());
                        for (PatientUserVO mPatientUserVO : mResponseVO.getPatientUserVO()) {
                            if (mPatientUserVO.getPatientId() == patientUserVO.getPatientId()) {
                                setProfileImage(mPatientUserVO);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;


                default:
                    AppUtil.showToast(getApplicationContext(), mResponseVO.getResponse());
                    break;
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = profilePic.getWidth();
        int targetH = profilePic.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        profilePic.setImageBitmap(bitmap);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("onPageSelected","position="+position);
        HashMap<String, Object> upshotData = new HashMap<>();
        switch (position)
        {
            case 0:
                upshotData.put(Constant.UpshotEvents.PROFILE_VIEW_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.PROFILE_VIEW_CLICKED);
                break;

            case 1:
                upshotData.put(Constant.UpshotEvents.EMHR_VIEW_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.EMHR_VIEW_CLICKED);
                break;

            case 2:
                upshotData.put(Constant.UpshotEvents.INSURANCE_VIEW_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_VIEW_CLICKED);
                long patientId=AppUtil.getCurrentSelectedPatientId(getApplicationContext());
                InsuranceVO mInsuranceVO = mDatabaseHandler.getPurchasedInsurances(patientId);
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                if(mInsuranceVO==null)
                {
                    completePendingInsurancePurchaseIfExists(patientId);
                }
                if(!defaultSharedPreferences.getBoolean("verifyDialogActionTaken",false)) {
                    if (mInsuranceVO != null) {
                        if (!mInsuranceVO.isEmailVerified() || !mInsuranceVO.isMobileNumberVerified()) {
                            showDialogtoVerifyRequiredDetails(mInsuranceVO);
                        }
                    }
                }
                break;
        }
        Log.d(TAG,"upshot data="+upshotData.entrySet());
    }
    private void completePendingInsurancePurchaseIfExists(long patientId) {
        final SharedPreferences mSharedPreferences=getSharedPreferences(String.valueOf(patientId), Context.MODE_PRIVATE);
        Log.d("sp=",mSharedPreferences+"");
        Log.d("patientId=",mSharedPreferences.contains("patientId")+"");

        if(mSharedPreferences.contains("patient_id"))
        {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setMessage(getString(R.string.incomplete_ins_purchase))
                    .setPositiveButton(getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent mIntent = new Intent(ProfileActivity.this, AddInsuredDetailsActivity.class);
                                    mIntent.putExtra("patientId",mSharedPreferences.getLong("patient_id",0));
                                    mIntent.putExtra("paytm_ref_id",mSharedPreferences.getString("paytm_ref_id",""));
                                    mIntent.putExtra("totalAmount",mSharedPreferences.getInt("amount_paid",0));
                                    mIntent.putExtra("couponCode",mSharedPreferences.getString("coupon_code",""));
                                    startActivity(mIntent);
                                }
                            }).show();
        }

    }
    private void showDialogtoVerifyRequiredDetails(final InsuranceVO mInsuranceVO) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        new AlertDialog.Builder(ProfileActivity.this)
                .setMessage(getString(R.string.verify_mobile_email_msg))
                .setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                defaultSharedPreferences.edit().putBoolean("verifyDialogActionTaken",true).commit();
                                Intent intent = new Intent(ProfileActivity.this, InsuranceDetailsActivity.class);
                                    Log.d("onListFragmentInteraction", "insurance id : " + mInsuranceVO.getInsuranceId());
                                    intent.putExtra(INSURANCE_ID, mInsuranceVO.getInsuranceId());
                                    startActivityForResult(intent, Constant.REQUEST_CODE_INSURANCE_UPDATE);
                            }
                        })
                .setNegativeButton(getString(R.string.lable_later),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                defaultSharedPreferences.edit().putBoolean("verifyDialogActionTaken",true).commit();
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "screen position : " + position);
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new DemographicsFragment();
                    break;
                case 1:
                    fragment = new EhrViewFragment();
                    break;
                case 2:
                    fragment = new InsuranceFragment();//new InsuranceDetailsFragment();
                    fragment.setArguments(getIntent().getExtras());
                    Log.d(TAG,"Bundle to IFragment="+getIntent().getExtras());
                    break;
            }
            return fragment;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_tab_profile);
                case 1:
                    return getString(R.string.title_tab_emergency_record);
                case 2:
                    return getString(R.string.label_insurance);
            }
            return null;
        }
    }
}
