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

import com.patientz.adapters.AdapterAllergyTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityDialogAllergyTypes extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ActivityDialogAllergyTypes";
    ArrayList<String> allergies = new ArrayList<>();
    EditText etOthers;
    private AdapterAllergyTypes mAdapterAllergyTypes;
    String[] existingAllergiesList;
    List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_allergy_types);
        Button btAdd = (Button) findViewById(R.id.bt_add);
        Button btCancel = (Button) findViewById(R.id.bt_cancel);
        Intent mIntent=getIntent();
         existingAllergiesList=mIntent.getStringArrayExtra("existingAllergiesList");
        stringList = new ArrayList<String>(Arrays.asList(existingAllergiesList));
        etOthers = (EditText) findViewById(R.id.et_others);
        btAdd.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        final GridView gridView = (GridView) findViewById(R.id.grid_view_allery_types);
        getOtherAllergyTypes();
        etOthers.setText(android.text.TextUtils.join(",", stringList));
        mAdapterAllergyTypes = new AdapterAllergyTypes(ActivityDialogAllergyTypes.this, R.layout.adapter_allergy_types,getListOfAllergies(),existingAllergiesList);
        gridView.setAdapter(mAdapterAllergyTypes);
        etOthers.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    passBackData();
                }
                return false;
            }
        });
    }

    private void passBackData() {
        allergies = mAdapterAllergyTypes.getCheckedItems();
        Intent mIntent = new Intent();
        String otherAllergies = etOthers.getText().toString();
        if (!TextUtils.isEmpty(otherAllergies)) {
            allergies.add(otherAllergies);
        }
        mIntent.putStringArrayListExtra("allergies", allergies);
        /*if (allergies != null && allergies.size() != 0) {
        }*/
        setResult(RESULT_OK, mIntent);
        finish();
    }

    private void getOtherAllergyTypes() {
        //ArrayList<String> otherAllergies=new ArrayList<String>();
        for(String allergy:getListOfAllergies())
        {
           if(stringList.contains(allergy))
           {

               stringList.remove(allergy);
           }
        }
    }

    private ArrayList<String> getListOfAllergies() {
        ArrayList<String> allergies = new ArrayList<String>();
        allergies.add("Nuts");
        allergies.add("Egg");
        allergies.add("Dairy");
        allergies.add("Pollen");
        allergies.add("Dust");
        allergies.add("Cosmetics");
        return allergies;
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                passBackData();
                break;
            case R.id.bt_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
