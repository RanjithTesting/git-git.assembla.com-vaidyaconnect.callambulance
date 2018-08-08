package com.patientz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.patientz.adapters.AdapterBloodGroups;
import com.patientz.utils.Log;

import java.util.ArrayList;

public class ActivityDialogBloodGroups extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ActivityDialogAllergyTypes";
    ArrayList<String> bloodGroupsList = new ArrayList<>();
    EditText etOthers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_blood_groups);
        final GridView gridView = (GridView) findViewById(R.id.grid_view_allery_types);
        Button btAdd = (Button) findViewById(R.id.bt_add);
        Button btCancel = (Button) findViewById(R.id.bt_cancel);
        etOthers = (EditText) findViewById(R.id.et_others);
        btAdd.setOnClickListener(this);
        btCancel.setOnClickListener(this);

        gridView.setAdapter(new AdapterBloodGroups(getApplicationContext(), R.layout.adapter_blood_groups));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(TAG, "********* grid item clicked ***********");
                RelativeLayout view = (RelativeLayout)gridView.getChildAt(position);
                view.setBackgroundResource(R.color.colorRedLight);
                String bloodGroup = (String) parent.getAdapter().getItem(position);
                Log.d(TAG, "Medication Type=" + bloodGroup);
                Intent mIntent=new Intent();
                mIntent.putExtra("blood_group", bloodGroup);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
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
                Intent mIntent = null;

                if (bloodGroupsList != null && bloodGroupsList.size() != 0) {

                    mIntent = new Intent();
                    mIntent.putStringArrayListExtra("blood_group", bloodGroupsList);
                }
                setResult(RESULT_OK, mIntent);
                finish();
                break;
            case R.id.bt_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
