package com.patientz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.patientz.adapters.AdapterMedicalCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityDialogMedicalConditions extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ActivityDialogAllergyTypes";
    ArrayList<String> medications = new ArrayList<>();
    EditText etOthers;
    private AdapterMedicalCondition adapterMedicalCondition;
    List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_medical_conditions);
        final GridView gridView = (GridView) findViewById(R.id.grid_view_medical_conditions);
        Button btAdd = (Button) findViewById(R.id.bt_add);
        Button btCancel = (Button) findViewById(R.id.bt_cancel);
        Intent mIntent = getIntent();
        String[] existingMedicalConditionList = mIntent.getStringArrayExtra("existingMedicalConditionList");
        stringList = new ArrayList<String>(Arrays.asList(existingMedicalConditionList));
        etOthers = (EditText) findViewById(R.id.et_others);
        btAdd.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        adapterMedicalCondition = new AdapterMedicalCondition(ActivityDialogMedicalConditions.this, R.layout.adapter_medical_conditions, getListOfMedicalConditions(), existingMedicalConditionList);
        gridView.setAdapter(adapterMedicalCondition);
        getOtherAllergyTypes();
        etOthers.setText(android.text.TextUtils.join(",", stringList));
        etOthers.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    passDataBack();
                }
                return false;
            }
        });
    }

    private ArrayList<String> getListOfMedicalConditions() {
        ArrayList<String> medicalConditions = new ArrayList<String>();
        medicalConditions.add("Diabetes");
        medicalConditions.add("Hypertension");
        medicalConditions.add("Heart");
        medicalConditions.add("Asthma");
        medicalConditions.add("Kidney");
        medicalConditions.add("Thyroid");
        return medicalConditions;
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    public void passDataBack() {
        medications = adapterMedicalCondition.getCheckedItems();
        Intent mIntent = new Intent();

        String otherMedicalConditions = etOthers.getText().toString();
        if (!TextUtils.isEmpty(otherMedicalConditions)) {
            medications.add(otherMedicalConditions);
        }
        //if (medications != null && medications.size() != 0) {

            mIntent.putStringArrayListExtra("medical_condition", medications);
        //}
        setResult(RESULT_OK, mIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                passDataBack();
                break;
            case R.id.bt_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }

    private void getOtherAllergyTypes() {
        for (String allergy : getListOfMedicalConditions()) {
            if (stringList.contains(allergy)) {

                stringList.remove(allergy);
            }
        }
    }
}
