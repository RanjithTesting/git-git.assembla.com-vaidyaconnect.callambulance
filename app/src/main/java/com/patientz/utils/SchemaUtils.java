package com.patientz.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.HospitalVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.RecordSchemaAttributes;
import com.patientz.VO.UserInfoVO;
import com.patientz.VO.UserRecordVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SchemaUtils implements Filterable {
    private static final String TAG = "SchemaUtils";
    private static Activity mContext;
    private static RecordSchemaAttributes mRecordSchemaAttributes;
    private static String prefFile;
    public static int id;
    UserInfoVO mUserInfoVO;
    public static ArrayList<RecordSchemaAttributes> rsaListForUI;
    static LinkedHashMap<String, String> inflateLinkedHashMapHr;

    /* Attachment module */
    static String attachFileType;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    static final String STATE_URI = "Attachment_uri";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static String getDisplayNameFromSchemaKeysPreference(String key,
                                                                Context context, String preferenceFileName) {
        Log.d("SchemaUtils", "key--->" + key);
        Log.d("SchemaUtils", "preferenceFileName" + preferenceFileName);

        String displayName = context.getSharedPreferences(preferenceFileName,
                Context.MODE_PRIVATE).getString(key, null);
        Log.d("SchemaUtils", "displayName from sp" + displayName);
        return displayName;
    }

   /* public static void paintUI(ViewGroup wrapperLayout, Activity context,
                               RecordSchemaAttributes recordSchemaAttributes,
                               String schemaKeysPrefFilename,
                               LinkedHashMap<String, String> healthRecordLinkedHashMap2, int i) {
        inflateLinkedHashMapHr = healthRecordLinkedHashMap2;
        id = i;
        mContext = context;
        mRecordSchemaAttributes = recordSchemaAttributes;
        prefFile = schemaKeysPrefFilename;
        paintLabel(context, wrapperLayout);
        Log.d(TAG, "Label >>" + mRecordSchemaAttributes.getAttributeKey());
        paintElement(mRecordSchemaAttributes.getUiElement(), context,
                wrapperLayout);
        // paintDivider(context,wrapperLayout);
    }*/

  /*  public static void paintLabel(Context mContext, ViewGroup wrapper) {
        LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tv = (TextView) mLayoutInflater.inflate(R.layout.textview_style, null);
        // tv.setPadding(5, 0, 0, 2);
        tv.setLayoutParams(lparams);
        String label = getDisplayNameFromSchemaKeysPreference(
                mRecordSchemaAttributes.getAttributeKey(), mContext, prefFile);
        tv.setText(label);
        wrapper.addView(tv);

    }

    public static void paintElement(String uiElement, Context context,
                                    ViewGroup wrapper) {
        *//*
         * if(recordTypeEqualsRecord()) { //paintAttachment(context,wrapper); }
		 * else {
		 *//*
        if (uiElement.equals(Constant.TEXTAREA)
                || uiElement.equals(Constant.TEXTFIELD)) {
            if (mRecordSchemaAttributes.getAttributeKey().equalsIgnoreCase("preferredHospital") || mRecordSchemaAttributes.getAttributeKey().equalsIgnoreCase("preferredAmbulanceProvider")) {
                paintAutoCompleteEditText(context, wrapper);

            } else {

                paintEditText(context, wrapper);
            }
        }
        if (uiElement.equals(Constant.DROPDOWN)
                && mRecordSchemaAttributes.getAttributeType().equalsIgnoreCase(
                Constant.ATTRITBUTE_TYPE_ALPHA_NUMERIC)) {
            paintDropDown(context, wrapper);
        }
        if (uiElement.equals(Constant.DROPDOWN)
                && mRecordSchemaAttributes.getAttributeType().equalsIgnoreCase(
                Constant.ATTRITBUTE_TYPE_TIME)) {
            paintTimePicker(context, wrapper);
        }
        if (uiElement.equals(Constant.CHECKBOX)) {
            paintCheckBox(context, wrapper);
        }
        if (uiElement.equals(Constant.RADIOBUTTON)) {
            paintRadioButton(context, wrapper);
        }
        *//* } *//*
    }*/

  /*  private static void paintAutoCompleteEditText(Context context, ViewGroup wrapper) {
        Log.d(TAG, "paintAutoCompleteEditText:" + mRecordSchemaAttributes.getAttributeKey());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) mLayoutInflater.inflate(
                R.layout.custom_autocomplete_editext, null);
        if (!TextUtils.isEmpty(mRecordSchemaAttributes.getRegExDescription())) {
            if ("Decimal".equalsIgnoreCase(mRecordSchemaAttributes
                    .getRegExDescription().trim())) {
                autoCompleteTextView.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else if ("Numeric".equalsIgnoreCase(mRecordSchemaAttributes
                    .getRegExDescription().trim())) {
                autoCompleteTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

        }
        autoCompleteTextView.setLayoutParams(layoutParams);
        if (inflateLinkedHashMapHr != null
                && inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                .getAttributeKey()) != null) {

            autoCompleteTextView.setText(inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                    .getAttributeKey()));
            autoCompleteTextView.setSingleLine(true);
        }

        autoCompleteTextView.setId(id);
        // int r=id++;
        // Log.d("paintEditText", r+"");

        wrapper.addView(autoCompleteTextView);
        if (mRecordSchemaAttributes.getAttributeKey().equalsIgnoreCase("preferredHospital")) {
            GetHospitalsFromDbAsync getHospitalsFromDbAsync = new GetHospitalsFromDbAsync(autoCompleteTextView);
            getHospitalsFromDbAsync.execute();

        } else {
            DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(mContext);
            try {
                ArrayList<AmbulanceProviderVO> mAmbulanceProviderVOs = mDatabaseHandler.getAllPrefferedAmbulanceProvidersList();

                Log.d(TAG, "AMBULANCE PROVIDERS SIZE=" + mAmbulanceProviderVOs.size());
                AdapterCustomAmbulanceProvidersList mAdapterCustomObjectsList = new AdapterCustomAmbulanceProvidersList(mContext, mAmbulanceProviderVOs);
                autoCompleteTextView.setAdapter(mAdapterCustomObjectsList);
                autoCompleteTextView.setThreshold(1);
            } catch (Exception e) {
            }
            //callWebserviceToGetPrefferedEmergencyProviders(autoCompleteTextView);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AmbulanceProviderVO ambulanceProviderVO = (AmbulanceProviderVO) parent.getAdapter().getItem(position);
                    autoCompleteTextView.setText(getSelectedAmbulanceProvider(ambulanceProviderVO));

                }
            });
        }
    }*/

  /*  private static String getSelectedAmbulanceProvider(AmbulanceProviderVO ambulanceProviderVO) {
        String selectedAmbulanceProvider = "";
        String COMMA = ",";

        if (ambulanceProviderVO.getOrganizationName() != null) {

            selectedAmbulanceProvider = ambulanceProviderVO.getOrganizationName() + COMMA;

        }
        if (ambulanceProviderVO.getLocation() != null) {
            if (ambulanceProviderVO.getLocation().getLocationName() != null) {
                if (!TextUtils.isEmpty(ambulanceProviderVO.getLocation().getLocationName())) {
                    selectedAmbulanceProvider = selectedAmbulanceProvider + ambulanceProviderVO.getLocation().getLocationName() + COMMA;
                }
            }
        }
        if (!TextUtils.isEmpty(ambulanceProviderVO.getPhoneNumber())) {
            selectedAmbulanceProvider = selectedAmbulanceProvider + ambulanceProviderVO.getPhoneNumber();
        }


        *//*else if(ambulanceProviderVO.getLocation()!=null)
        {
            if(ambulanceProviderVO.getLocation().getLocationName()!=null)
            {
                if(!TextUtils.isEmpty(ambulanceProviderVO.getLocation().getLocationName()))
                {
                selectedAmbulanceProvider=ambulanceProviderVO.getLocation().getLocationName();
                }
            }
        }else if(ambulanceProviderVO.getPhoneNumber()!=null)
        {
            selectedAmbulanceProvider=ambulanceProviderVO.getPhoneNumber();
        }else
        {

        }*//*
        return selectedAmbulanceProvider;
    }*/

    @Override
    public Filter getFilter() {
        return null;
    }

/*
    public static class GetHospitalsFromDbAsync extends AsyncTask<String[], ArrayList<HospitalVO>, ArrayList<HospitalVO>> {

        ProgressDialog dialog;
        AutoCompleteTextView autoCompleteTextView;
        private String TAG = "GetHospitalsFromDbAsync";

        public GetHospitalsFromDbAsync(AutoCompleteTextView autoCompleteTextView) {
            this.autoCompleteTextView = autoCompleteTextView;
        }


        @Override
        protected ArrayList<HospitalVO> doInBackground(String[]... params) {
            DatabaseHandlerAssetHelper mDatabaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(mContext);
            ArrayList<HospitalVO> mHospitalVOs = null;
            try {
                mHospitalVOs = mDatabaseHandlerAssetHelper.getHospitalsList();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return mHospitalVOs;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Drawable drawable = mContext.getResources().getDrawable(
                    R.drawable.progress);
            dialog = new ProgressDialog(mContext);
            dialog.setMessage(mContext.getString(R.string.progress_dialog_loading_hospitals));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }


        @Override
        protected void onPostExecute(ArrayList<HospitalVO> mHospitalVOs) {
            super.onPostExecute(mHospitalVOs);
            dialog.dismiss();
            if (mHospitalVOs != null) {
                Log.d(TAG, "HOSPITALS SIZE=" + mHospitalVOs.size());
                AdapterCustomObjectsList mAdapterCustomObjectsList = new AdapterCustomObjectsList(mContext, mHospitalVOs);
                autoCompleteTextView.setAdapter(mAdapterCustomObjectsList);
                autoCompleteTextView.setThreshold(1);

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HospitalVO hospitalVO = (HospitalVO) parent.getAdapter().getItem(position);
                        String displayText;
                        displayText = hospitalVO.getName() + "," + getAddress(hospitalVO);
                        autoCompleteTextView.setText(displayText);

                    }
                });
            } else {
            }
        }

    }*/

    private static String getAddress(HospitalVO hospitalVO) {
        String address = null;
        String additionalAddress = hospitalVO.getAdditionalAddress();
        if (!TextUtils.isEmpty(additionalAddress)) {
            address = additionalAddress + ",";
        }
        address = (address == null ? hospitalVO.getCity() : address + hospitalVO.getCity());
        Log.d(TAG, "ADDRESS=" + address);
        Log.d(TAG, "ADDITIONAL ADDRESS=" + hospitalVO.getAdditionalAddress() + "");
        Log.d(TAG, "CITY=" + hospitalVO.getCity() + "");

        return address;

    }

    private static boolean recordTypeEqualsRecord() {
        // TODO Auto-generated method stub
        Log.d(TAG,
                "attribute type " + mRecordSchemaAttributes.getAttributeType()
                        + "");
        if (mRecordSchemaAttributes.getAttributeType() != null) {
            Log.d(TAG,
                    "attribute type "
                            + mRecordSchemaAttributes.getAttributeType() + "");

            return mRecordSchemaAttributes.getAttributeType().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.record));
        }
        return false;
        /* return false; */
    }

    public static PatientUserVO getPatientData(Context context) {
        DatabaseHandler dh = DatabaseHandler.dbInit(context);
        PatientUserVO patientUserVO = null;
        try {
            patientUserVO = dh.getProfile("Self");
            UserInfoVO infoVO = dh.getUserInfo(patientUserVO.getPatientId());
            UserRecordVO recordVO = new UserRecordVO();
            recordVO.setUserInfoVO(infoVO);
            patientUserVO.setRecordVO(recordVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return patientUserVO;
    }

    private static void paintAttachment(Context context, ViewGroup wrapper2) {
        LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(mContext);
        tv.setPadding(0, 0, 0, 2);
        tv.setLayoutParams(lparams);
        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher,
                0, 0, 0);
        tv.setText(mContext.getString(R.string.chooseimage) + " : ");
        tv.setId(id);
        wrapper2.addView(tv);
        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // attachmentAdd();
            }
        });

    }

    public static void paintEditText(Context mContext, ViewGroup wrapper) {
        Log.d(TAG, "paintEditText()--> id is :" + id);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 2, 5, 2);
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        EditText editText = (EditText) mLayoutInflater.inflate(
                R.layout.custom_edit_view, null);
        if (!TextUtils.isEmpty(mRecordSchemaAttributes.getRegExDescription())) {
            if ("Decimal".equalsIgnoreCase(mRecordSchemaAttributes
                    .getRegExDescription().trim())) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else if ("Numeric".equalsIgnoreCase(mRecordSchemaAttributes
                    .getRegExDescription().trim())) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

        }
        editText.setLayoutParams(layoutParams);
        if (inflateLinkedHashMapHr != null
                && inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                .getAttributeKey()) != null) {
            editText.setText(inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                    .getAttributeKey()));
            editText.setSingleLine(true);
        }

        editText.setId(id);
        // int r=id++;
        // Log.d("paintEditText", r+"");

        wrapper.addView(editText);

    }

    public static void paintDropDown(Context mContext, ViewGroup wrapper) {
        String decodedRegex = "";

        if (mContext != null) {
            Log.d("paintDropDown", "" + mContext);
            LayoutInflater mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Spinner spinner = (Spinner) mLayoutInflater.inflate(
                    R.layout.custom_spinner_style_layout, null);
            try {
                decodedRegex = getDecodedRegex(mRecordSchemaAttributes
                        .getRegEx());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] dropDownValuesFromRegex = decodedRegex.split("\\|");
            String[] dropDownValuesWithSelect = new String[dropDownValuesFromRegex.length + 1];
            dropDownValuesWithSelect[0] = "Select";
            System.arraycopy(dropDownValuesFromRegex, 0,
                    dropDownValuesWithSelect, 1, dropDownValuesFromRegex.length);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item,
                    dropDownValuesWithSelect);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            if (inflateLinkedHashMapHr != null
                    && inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                    .getAttributeKey()) != null) {
                Iterator<LinkedHashMap.Entry<String, String>> entries;
                entries = inflateLinkedHashMapHr.entrySet().iterator();
                Log.d(TAG, inflateLinkedHashMapHr.entrySet().toString());

                while (entries.hasNext()) {
                    LinkedHashMap.Entry<String, String> entry = entries.next();
                    Log.d(TAG, "paintDropDown ,map values" + entry.getValue()
                            + "");
                    if (entry.getKey().equalsIgnoreCase(
                            mRecordSchemaAttributes.getAttributeKey())) {
                        for (int i = 0; i < spinner.getCount(); i++) {
                            if (spinner.getItemAtPosition(i).equals(
                                    entry.getValue())) {
                                Log.d(TAG, entry.getValue() + "else if 2");
                                spinner.setSelection(i);
                            }
                        }
                    }
                }
            }
            spinner.setId(id);
            // int r=id++;
            // Log.d("paintDropDown", r+"");

            wrapper.addView(spinner);
        } else
            Log.d("paintDropDown", "ERROR!!!! mContext null");

    }

   /* private static void paintTimePicker(Context context, ViewGroup wrapper) {
        RelativeLayout mRelativeLayout = new RelativeLayout(context);
        // Painting text view to display time
        TextView mTextView = new TextView(mContext);
        mTextView.setId(id);
        mTextView.setText(mContext.getString(R.string.empty_time_value));
        mTextView.setGravity(Gravity.BOTTOM);
        mTextView.setClickable(true);

        mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.app_icon, 0);
        mTextView.setCompoundDrawablePadding(20);
        mTextView.setTextColor(context.getResources().getColor(R.color.black));

		*//*
		 * ImageView mImageView = new ImageView(context);
		 * mImageView.setId(1000);
		 *//*

        RelativeLayout.LayoutParams paramsTV = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // paramsTV.addRule(RelativeLayout.ALIGN_BOTTOM,mImageView.getId());
        paramsTV.setMargins(8, 0, 0, 0);

		*//*
		 * RelativeLayout.LayoutParams paramIV = new
		 * RelativeLayout.LayoutParams( 100, 100);
		 * paramIV.addRule(RelativeLayout.RIGHT_OF,mTextView.getId());
		 * mImageView.setBackgroundResource(R.drawable.cal_plus_ic_selector);
		 * 
		 * mRelativeLayout.addView(mImageView, paramIV);
		 *//*
        mRelativeLayout.addView(mTextView, paramsTV);
        wrapper.addView(mRelativeLayout);
        if (inflateLinkedHashMapHr != null
                && inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                .getAttributeKey()) != null) {
            mTextView.setText(inflateLinkedHashMapHr
                    .get(mRecordSchemaAttributes.getAttributeKey()));

        }
        mTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SchemaUtils.TimePickerFragment newFragment = new TimePickerFragment(
                        v);
                newFragment.show(((FragmentActivity) mContext)
                        .getSupportFragmentManager(), "timePicker");
            }
        });

    }*/

    public static void paintCheckBox(Context mContext, ViewGroup wrapper) {
        CheckBox checkBox = new CheckBox(mContext);
        if (inflateLinkedHashMapHr != null
                && inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                .getAttributeKey()) != null) {
            if (inflateLinkedHashMapHr.get(
                    mRecordSchemaAttributes.getAttributeKey())
                    .equalsIgnoreCase("yes")) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(true);
            }
        }
        checkBox.setId(id);
        // id++;
        wrapper.addView(checkBox);
    }

    public static void paintRadioButton(Context mContext, ViewGroup wrapper) {
        Log.d(TAG, "paintDropDown()--> id is :" + id);
        String decodedRegex = "";
        try {
            decodedRegex = getDecodedRegex(mRecordSchemaAttributes.getRegEx());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] radioButtonLabels = decodedRegex.split("\\|");
        RadioButton[] rb = new RadioButton[radioButtonLabels.length];
        RadioGroup rg = new RadioGroup(mContext);
        rg.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < radioButtonLabels.length; i++) {
            rb[i] = new RadioButton(mContext);
            rb[i].setId(i + 100);
            rb[i].setText(radioButtonLabels[i]);
            rb[i].setTextColor(mContext.getResources().getColor(R.color.black));
            rg.addView(rb[i]); // the RadioButtons are added to the radioGroup
            rg.setId(id);
            Log.d("Checked radio button label-->", radioButtonLabels[i] + "");

            if (inflateLinkedHashMapHr != null
                    && inflateLinkedHashMapHr.get(mRecordSchemaAttributes
                    .getAttributeKey()) != null) {
                if (inflateLinkedHashMapHr.get(
                        mRecordSchemaAttributes.getAttributeKey())
                        .equalsIgnoreCase(radioButtonLabels[i])) {
                    rb[i].setChecked(true);
                } else {

                }
            }

        }
        // int r=id++;
        // Log.d("rgbutoon", r+"");
        wrapper.addView(rg);
        Log.d("Checked radio button id-->", rg.getCheckedRadioButtonId() + "");
    }

    /*
     * public static void paintDivider(Context mContext, ViewGroup wrapper) {
     * LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
     * LinearLayout.LayoutParams.MATCH_PARENT, 1); layoutParams.setMargins(0, 2,
     * 0, 2); View ruler = new View(mContext);
     * ruler.setBackgroundColor(mContext.
     * getResources().getColor(R.color.doctrz_green));
     * ruler.setLayoutParams(layoutParams); wrapper.addView(ruler, new
     * ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 1));
     * Log.d(TAG, "no of calls to paintDivider");
     * ((ViewGroup)wrapper.getParent()).removeView(wrapper);
     * wrapper.addView(ruler); }
     */
/*
    public static PatientDataHandler getPatientData(Activity context) {
        PatientDataHandler mPatientDataHandler = PatientDataHandler
                .getInstance();
        if (mPatientDataHandler.isEmpty()) {
            context.finish();
        } else {
            Log.d("getPatientDta", "else");

            return mPatientDataHandler;
        }
        return mPatientDataHandler;
    }
*/

    public static ArrayList<RecordSchemaAttributes> getSchemaAttributesFromSharedPreference(
            Context context, String attrPrefFileName,
            String attrPrefFileNameKey) {
        Log.d(TAG, "check file name $$$$ : getSortedMap" + attrPrefFileName);
        Log.d(TAG, "check key $$$$: getSortedMap" + attrPrefFileNameKey);
        ComplexPreferences mComplexPreferences = new ComplexPreferences(
                context, attrPrefFileName, Context.MODE_PRIVATE);
        Log.d(TAG,
                "getSchemaAttributesFromSharedPreference-->attrPrefFileNameKey"
                        + attrPrefFileNameKey);
        Log.d(TAG, "getSchemaAttributesFromSharedPreference-->context  "
                + context);

        return mComplexPreferences.getObject(attrPrefFileNameKey);
    }

    public static LinkedHashMap<String, String> setMapForUIBasedOnRole(
            FragmentActivity mActivity,
            ArrayList<RecordSchemaAttributes> recordSchemaAttributesList,
            LinkedHashMap<String, String> healthRecordLinkedHashMap, UserInfoVO mUserInfoVO) {
        new ArrayList<RecordSchemaAttributes>();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        rsaListForUI = new ArrayList<RecordSchemaAttributes>();
        Log.d(TAG, "GENDER :" + mUserInfoVO.getGender() + "");
        Log.d(TAG, "ATTRIBUTES KEY :" + recordSchemaAttributesList.get(0).getAttributeKey() + "");
        for (int i = 0; i < recordSchemaAttributesList.size(); i++) {
            Log.d(TAG, "KEY=" + recordSchemaAttributesList.get(i) + "");
            if ((recordSchemaAttributesList.get(i).getAttributeKey()
                    .equalsIgnoreCase("Pregnant") && (mUserInfoVO.getGender() == 1))
                    || (mUserInfoVO.getRole().equalsIgnoreCase(mActivity.getResources()
                    .getString(R.string.care_giver))
                    && ((recordSchemaAttributesList.get(i)
                    .getAttributeKey()
                    .equalsIgnoreCase("Donate_Blood")) || (recordSchemaAttributesList
                    .get(i).getAttributeKey()
                    .equalsIgnoreCase("Organ_Donation"))) || (mUserInfoVO.getRole()
                    .equalsIgnoreCase(mActivity.getResources()
                            .getString(R.string.owner))
                    && (!mUserInfoVO.getRelationship().equalsIgnoreCase(mActivity
                    .getResources().getString(R.string.self))) && ((recordSchemaAttributesList
                    .get(i).getAttributeKey()
                    .equalsIgnoreCase("Donate_Blood")) || (recordSchemaAttributesList
                    .get(i).getAttributeKey()
                    .equalsIgnoreCase("Organ_Donation")))))) {
                Log.d(TAG, "KEY in IF :"
                        + recordSchemaAttributesList.get(i).getAttributeKey()
                        + "");
                continue;
            } else {
                Log.d(TAG, "KEY in else :"
                        + recordSchemaAttributesList.get(i).getAttributeKey()
                        + "");
                Log.d(TAG, "ids for UI elements  :" + i + "");

                rsaListForUI.add(recordSchemaAttributesList.get(i));
                if (healthRecordLinkedHashMap != null) {

                    map.put(recordSchemaAttributesList.get(i).getAttributeKey(),
                            healthRecordLinkedHashMap
                                    .get(recordSchemaAttributesList.get(i)
                                            .getAttributeKey()));
                } else {
                    map.put(recordSchemaAttributesList.get(i).getAttributeKey(),
                            null);
                }
            }
        }
        Log.d(TAG, "mapForUI:\n" + map.entrySet().toString());
        return map;

    }

    public static LinkedHashMap<String, String> getUIValuesMap(
            ArrayList<RecordSchemaAttributes> rsaListForUI,
            LinkedHashMap<String, String> healthRecordMap, Activity context) {
        // LinkedHashMap<String, String> mapWithUIvalues = new
        // LinkedHashMap<String, String>();
        String value = null;
        String key = null;
        Log.d(TAG, "incoming Mapp-->" + healthRecordMap);

        for (int i = 0; i < rsaListForUI.size(); i++) {
            String uiElement = rsaListForUI.get(i).getUiElement();
            key = rsaListForUI.get(i).getAttributeKey();
            Log.d(TAG, "index: " + i + " key: " + key + "");
            String attType = rsaListForUI.get(i).getAttributeType();

            if (checkIfTextFieldOrTextArea(uiElement, attType)) {
                EditText editText = (EditText) context.findViewById(i);
                if (editText.getText() != null) {
                    value = editText.getText().toString();
                }
            }
            if (uiElement.equals("DROPDOWN")
                    && rsaListForUI.get(i).getAttributeType()
                    .equals(Constant.ATTRITBUTE_TYPE_ALPHA_NUMERIC)) {
                Spinner spinner = (Spinner) context.findViewById(i);
                if (spinner.getSelectedItem().toString()
                        .equalsIgnoreCase("Select")) {
                    value = "";
                } else {
                    value = spinner.getSelectedItem().toString();
                }

            }
            if (uiElement.equals("DROPDOWN")
                    && rsaListForUI.get(i).getAttributeType()
                    .equals(Constant.ATTRITBUTE_TYPE_TIME)) {
                TextView tv = (TextView) context.findViewById(i);
                if (tv.getText() != null) {
                    value = tv.getText().toString();
                } else {
                    value = "";
                }
            }
            if (uiElement.equals("RADIOBUTTON")) {
                RadioGroup rg = (RadioGroup) context.findViewById(i);
                RadioButton selectedRadioButton = (RadioButton) context
                        .findViewById(rg.getCheckedRadioButtonId());
                if (selectedRadioButton == null) {
                    value = null;
                } else {
                    value = selectedRadioButton.getText().toString();
                }
            }
            if (uiElement.equals("CHECKBOX")) {
                CheckBox checkBox = (CheckBox) context.findViewById(i);
                if (checkBox.isChecked()) {
                    value = "Yes";
                } else {
                    value = "No";
                }
            }
            if (key != null) {
                healthRecordMap.put(key, value);
            }
        }
        Log.d(TAG, "outgoing Mapp-->" + healthRecordMap);

        return healthRecordMap;
    }

    public static LinkedHashMap<String, String> getFinalHealthRecordMap(
            ArrayList<RecordSchemaAttributes> recordSchemaAttributesList,
            LinkedHashMap<String, String> uiValuesMap,
            LinkedHashMap<String, String> healthRecordMap) {
        LinkedHashMap<String, String> rsMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < recordSchemaAttributesList.size(); i++) {
            if (uiValuesMap.containsKey(recordSchemaAttributesList.get(i)
                    .getAttributeKey())) {
                rsMap.put(recordSchemaAttributesList.get(i).getAttributeKey(),
                        uiValuesMap.get(recordSchemaAttributesList.get(i)
                                .getAttributeKey()));
            } else {
                if (healthRecordMap != null) {
                    if (healthRecordMap.get(recordSchemaAttributesList.get(i)
                            .getAttributeKey()) != null) {
                        rsMap.put(recordSchemaAttributesList.get(i)
                                .getAttributeKey(), healthRecordMap
                                .get(recordSchemaAttributesList.get(i)
                                        .getAttributeKey()));
                    } else {
                        rsMap.put(recordSchemaAttributesList.get(i)
                                .getAttributeKey(), "");
                    }
                } else {
                    rsMap.put(recordSchemaAttributesList.get(i)
                            .getAttributeKey(), "");
                }
            }
        }
        Log.d(TAG, "getRecordSchemaMap :::: " + rsMap.entrySet().toString());
        return rsMap;
    }

    public static LinkedHashMap<String, String> getErrorMap(
            ArrayList<RecordSchemaAttributes> rsaListForUI,
            LinkedHashMap<String, String> healthRecordMap, Context context,
            String recordType) {

        String errorMsg = null;
        String attributeKey;
        String value;
        String decodedRegex = "";

        LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();

        for (int i = 0; i < rsaListForUI.size(); i++) {
            errorMsg = null;
            attributeKey = rsaListForUI.get(i).getAttributeKey();
            value = healthRecordMap.get(rsaListForUI.get(i).getAttributeKey());

            if (rsaListForUI.get(i).isMandatory()) {

                if (isValueEmpty(value)) {

                    errorMsg = getErrorMsgForBlank(attributeKey, context,
                            recordType);
                    Log.d("TAG", errorMsg);

                } else {

                    if (checkIfTextFieldOrTextArea(rsaListForUI.get(i)
                            .getUiElement(), rsaListForUI.get(i)
                            .getAttributeType())) {
                        try {
                            decodedRegex = getDecodedRegex(rsaListForUI.get(i)
                                    .getRegEx());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (!value.matches(decodedRegex)) {
                            errorMsg = getErrorMsgForValueMismatch(
                                    attributeKey, rsaListForUI.get(i)
                                            .getRegExDescription(), context);
                        }
                    }
                }
            }

            errorMap.put(rsaListForUI.get(i).getAttributeKey(), errorMsg);
        }
        return errorMap;
    }

    private static String getDecodedRegex(String encodedRegex)
            throws UnsupportedEncodingException {
        return URLDecoder.decode(encodedRegex, Constant.CHAR_SET_NAME);

    }

    private static String getErrorMsgForValueMismatch(String attributeKey,
                                                      String regEx, Context context) {
        // TODO Auto-generated method stub
        return "Field "
                + SchemaUtils.getDisplayNameFromSchemaKeysPreference(
                attributeKey, context,
                Constant.EHR_SCHEMA_KEYS_PREF_FILENAME) + " should be "
                + regEx + ".";
    }

    private static String getErrorMsgForBlank(String attributeKey,
                                              Context context, String recordFileName) {
        // TODO Auto-generated method stub

        return "Field "
                + SchemaUtils.getDisplayNameFromSchemaKeysPreference(
                attributeKey, context, recordFileName)
                + " cannot be left blank";
    }

    private static boolean isValueEmpty(String value) {
        // TODO Auto-generated method stub
        return (value.length() == 0 || value == null || value
                .equalsIgnoreCase(""));
    }

    public static boolean setError(
            ArrayList<RecordSchemaAttributes> rsaListForUI,
            LinkedHashMap<String, String> errorLinkedHashMap, Activity context) {

        boolean anyError = false;

        for (int i = 0; i < rsaListForUI.size(); i++) {

            if (checkIfTextFieldOrTextArea(rsaListForUI.get(i).getUiElement(),
                    rsaListForUI.get(i).getAttributeType())
                    && !rsaListForUI.get(i).getAttributeKey()
                    .equalsIgnoreCase(Constant.ATTACHMENT)) {
                Log.d(TAG,
                        "error-->"
                                + errorLinkedHashMap.get(rsaListForUI.get(i)
                                .getAttributeKey()));

                EditText editText = (EditText) context.findViewById(i);

                editText.setError(errorLinkedHashMap.get(rsaListForUI.get(i)
                        .getAttributeKey()));

                if (errorLinkedHashMap.get(rsaListForUI.get(i)
                        .getAttributeKey()) != null) {
                    anyError = true;
                }
            } else {
                // FIXME implement set error for other ui elements
            }
        }
        return anyError;

    }

  /*  public static void attachmentAdd(final Activity mContext,
                                     final Fragment patientVisitDetailsFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.select_attachment_type)
                .setItems(R.array.attachment_option_type,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        captureImageFromGalleryOrCamera(mContext,
                                                patientVisitDetailsFragment);
                                        attachFileType = "document";
                                        break;
                                    case 1:
                                        captureImageFromGalleryOrCamera(mContext,
                                                patientVisitDetailsFragment);
                                        attachFileType = "picture";
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
    }*/

    public static void captureImageFromGalleryOrCamera(final AppCompatActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.select_attachment)
                .setItems(R.array.attachment_option_array,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(
                                                MediaStore.ACTION_IMAGE_CAPTURE);
                                        Uri fileUri = getOutputMediaFileUri(
                                                MEDIA_TYPE_IMAGE, activity);
                                        String attachmentUri = fileUri.toString()
                                                .trim().substring(7);
                                        SharedPreferences sp = activity
                                                .getSharedPreferences(
                                                        Constant.FILE_ATTACHMENT_URI_CONTAINER,
                                                        Activity.MODE_PRIVATE);
                                        Editor mEditor = sp.edit();
                                        mEditor.clear().commit();
                                        mEditor.putString("attachmentUri",
                                                attachmentUri);
                                        mEditor.commit();
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                fileUri);
                                        activity.startActivityForResult(intent,
                                                Constant.REQUEST_CODE_CAMERA_INTENT);
                                        break;
                                    case 1:
                                        Intent intent2 = new Intent(
                                                Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                        activity.startActivityForResult(Intent
                                                        .createChooser(intent2,
                                                                "Complete action using"),
                                                SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
    }

    private static Uri getOutputMediaFileUri(int type, Context context) {
        return Uri.fromFile(getOutputMediaFile(type, context));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type, Context context) {
        final String STORAGE_PATH = context.getResources().getString(
                R.string.uploadPath);

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        Log.d(TAG, Environment.getExternalStorageState());
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(), STORAGE_PATH);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("AddVisitActivity", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        Log.d(TAG,
                "CHecking media file absolute path:"
                        + mediaFile.getAbsolutePath());
        return mediaFile;
    }

    public static void downloadAttachedFile(Activity context,
                                            String attachmentFileId) {
        UserInfoVO mUserInfoVO = getPatientData(context)
                .getRecordVO().getUserInfoVO();
        long patientId = mUserInfoVO.getPatientId();
        String fileName = "visit_" + mUserInfoVO.getFirstName().trim() + "_"
                + Calendar.getInstance().getTimeInMillis() + ".jpg";
/*        DownloadFile mDownloadFile = new DownloadFile(context,
                attachmentFileId, patientId, fileName);
        mDownloadFile.execute();*/
    }

    public static LinkedHashMap<String, String> getHealthRecordMap(
            ArrayList<RecordSchemaAttributes> recordSchemaAttributesList) {
        // FIXME write function here... set empty valued map and store it in
        // healthRecordMap
        LinkedHashMap<String, String> healthRecordMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < recordSchemaAttributesList.size(); i++) {
            healthRecordMap.put(recordSchemaAttributesList.get(i)
                    .getAttributeKey(), null);
        }
        return healthRecordMap;
    }

    private static boolean checkIfTextFieldOrTextArea(String uiElement,
                                                      String attType) {
        boolean chkAttType = true;
        boolean chkUiEle = (uiElement.equals("TEXTAREA") || uiElement
                .equals("TEXTFIELD"));
        if (attType != null) {
            chkAttType = !(attType.equalsIgnoreCase(Constant.ATT_TYPE_RECORD));
        }

        return chkAttType && chkUiEle;
    }

    public static ArrayList<RecordSchemaAttributes> getSchemaAttrListInSharedPreference(
            Context context, String schemaAttrPrefFileName,
            String schemaAttrPrefKeyName) {
        Gson gson = new Gson();
        String json = context.getSharedPreferences(schemaAttrPrefFileName,
                Context.MODE_PRIVATE).getString(schemaAttrPrefKeyName, "");
        Log.d("check return type",
                gson.fromJson(json,
                        new TypeToken<ArrayList<RecordSchemaAttributes>>() {
                        }.getType()) + "");
        return gson.fromJson(json,
                new TypeToken<ArrayList<RecordSchemaAttributes>>() {
                }.getType());
    }

    public static void storeSchemaAttrListInSharedPreference(Context context,
                                                             ArrayList<RecordSchemaAttributes> attrList,
                                                             String schemaAttrPrefFileName, String schemaAttrPrefKeyName) {
        SharedPreferences mPrefs = context.getSharedPreferences(
                schemaAttrPrefFileName, Context.MODE_PRIVATE);
        Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(attrList);
        prefsEditor.putString(schemaAttrPrefKeyName, json);
        prefsEditor.commit();
        Log.d("check attr list stored",
                mPrefs.getString(schemaAttrPrefKeyName, ""));
    }

    public static void storeSchemaKeysInSharedPreference(Context context,
                                                         HashMap<String, String> schemaKeys, String schemaKeyKey) {
        Log.d(TAG, "in storeSchemaKeysInSharedPreference");
        Log.d(TAG, "in storeSchemaKeysInSharedPreference , schemaKeys="
                + schemaKeys.entrySet());

        SharedPreferences mPrefs = context.getSharedPreferences(schemaKeyKey,
                Context.MODE_PRIVATE);
        Editor prefsEditor = mPrefs.edit();
        for (Map.Entry entry : schemaKeys.entrySet()) {
            if (entry.getValue() != null && entry.getKey() != null) {
                Log.d(TAG, "schemaKeys");

                prefsEditor.putString(entry.getKey().toString(), entry
                        .getValue().toString());
                Log.d(TAG, mPrefs.getString(entry.getKey().toString(), ""));
                System.out.println("key,val: " + entry.getKey() + ","
                        + entry.getValue());
            }
            prefsEditor.commit();
        }

    }

    static void storeVersionInSharedPref(Context mContext, Date version,
                                         String spFileName) {
        long timeStamp = version.getTime();
        ComplexPreferences mComplexPreferences = new ComplexPreferences(
                mContext, spFileName, Context.MODE_PRIVATE);
        mComplexPreferences.putLongValue(Constant.VERSION_KEY, timeStamp);
    }

    public static String getVersionFromSharedPref(Context mContext,
                                                  String spFileName) {
        ComplexPreferences mComplexPreferences = new ComplexPreferences(
                mContext, spFileName, Context.MODE_PRIVATE);
        long version = mComplexPreferences.getLongValue(Constant.VERSION_KEY);
        String versionStringFormat = AppUtil.getDate(version,
                "EEE MMM dd hh:mm:ss zzz yyyy");
        return versionStringFormat;
    }

    public static void deleteSchemaRelatedSharedPrefernce(Context context,
                                                          String fileName) {
        SharedPreferences mPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        mPreferences.edit().clear().commit();
    }

    public static String getVersionFromSp(Context context,
                                          String spFileName, String key) {
        String versionStringFormat = "";
        ComplexPreferences mComplexPreferences = new ComplexPreferences(
                context, spFileName, Context.MODE_PRIVATE);
        long version = mComplexPreferences.getLongValue(key);
        if (version != 0) {
            versionStringFormat = AppUtil.getDate(version,
                    "EEE MMM dd hh:mm:ss zzz yyyy");
            Log.d(TAG, "SU>>getVersionFromSp>>getDate" + versionStringFormat);
        }
        return versionStringFormat;
    }

  /*  public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        View v;
        public TimePickerFragment() {
        }
        public TimePickerFragment(View v) {
            this.v = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    false);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // view.setIs24HourView(true);
            ((TextView) v).setText(hourOfDay + ":" + minute);
        }
    }*/

    private static Type entityType = new TypeToken<LinkedHashMap<String, String>>() {
    }.getType();

    public static void saveEmergencyHealthRecord(LinkedHashMap<String, String> map, Context context) {
        Gson gson = new Gson();
        String data = gson.toJson(map, entityType);

        context.getSharedPreferences("emergencyHealthRecord", 0)
                .edit()
                .putString("emhr", data)
                .commit();
        Log.d(TAG, "Saving emhr ..");
    }

    public static LinkedHashMap<String, String> loadEmergencyHealthRecord(Context context) {
        Gson gson = new Gson();
        String data = context.getSharedPreferences("emergencyHealthRecord", 0).getString("emhr", "");

        return gson.fromJson(data, entityType);
    }
}
