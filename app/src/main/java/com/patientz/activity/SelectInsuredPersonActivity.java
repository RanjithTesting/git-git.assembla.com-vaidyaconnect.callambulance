package com.patientz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.patientz.VO.UserInfoVO;
import com.patientz.adapters.SelectInsuredPersonAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SelectInsuredPersonActivity extends BaseActivity implements View.OnClickListener, SelectInsuredPersonAdapter.OnItemClickListener {
    private static final int REQUEST_CODE_ADD_MEMBER =6 ;
    private DatabaseHandler mDatabaseHandler;
    LinearLayout addPerson;
    private ArrayList<UserInfoVO> userInfoVOS;
    private SelectInsuredPersonAdapter selectInsuredPersonAdapter;
    private TextView tvPurchaseSuccessful;
    private TextView tvAccidentInsuranceAmt;
    private TextView tvPaymentRefNo;
    private TextView tvPaymentDate;
    private RecyclerView recyclerView;
    private Button btContinue;


    private void findViews() {
        tvPurchaseSuccessful = (TextView)findViewById( R.id.tv_purchase_successful );
        tvAccidentInsuranceAmt = (TextView)findViewById( R.id.tv_accident_insurance_amt );
        tvPaymentRefNo = (TextView)findViewById( R.id.tv_payment_ref_no );
        tvPaymentDate = (TextView)findViewById( R.id.tv_payment_date );
        recyclerView = (RecyclerView)findViewById( R.id.recycler_view );
        btContinue = (Button)findViewById( R.id.bt_continue );
        btContinue.setOnClickListener( this );
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_insured_person);
        findViews();
        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.insurance_payment_successful);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDatabaseHandler=DatabaseHandler.dbInit(getApplicationContext());
        recyclerView = findViewById(R.id.recycler_view);
        Intent mIntent=getIntent();

        Bundle paytmBundle=mIntent.getBundleExtra("paytmBundle");
        if(paytmBundle!=null) {
            tvAccidentInsuranceAmt.setText(paytmBundle.getString("TXNAMOUNT"));
            tvPaymentRefNo.setText("Payment Ref #" + paytmBundle.getString("TXNID") + " | ");
            try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").parse(paytmBundle.getString("TXNDATE"));
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a, EEE,d MMM yyyy");
                String txnDate = formatter.format(date1);
                tvPaymentDate.setText(txnDate);
            } catch (Exception e) {

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            userInfoVOS = mDatabaseHandler.getAllUsersInfoVO();
            Collections.sort(userInfoVOS, new Comparator<UserInfoVO>() {
                @Override
                public int compare(UserInfoVO c1, UserInfoVO c2) {
                    return c1.getFirstName().compareTo(c2.getFirstName());
                }
            });
            UserInfoVO addMore=new UserInfoVO();
            addMore.setFirstName("Add More");
            addMore.setLastName("");
            userInfoVOS.add(addMore);
            selectInsuredPersonAdapter=new SelectInsuredPersonAdapter(getApplicationContext(), userInfoVOS,this);
            recyclerView.setAdapter(selectInsuredPersonAdapter);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_CODE_ADD_MEMBER:
                Log.d("REQUEST CODE=",REQUEST_CODE_ADD_MEMBER+"");
                userInfoVOS = mDatabaseHandler.getAllUsersInfoVO();
                selectInsuredPersonAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onListItemClicked(int position, UserInfoVO userInfoVO) {
         if(position==(userInfoVOS.size()-1))
         {
             Intent mIntent = new Intent(SelectInsuredPersonActivity.this, ActivityAddMember.class);
             startActivityForResult(mIntent,REQUEST_CODE_ADD_MEMBER);
         }else
         {
             Intent mIntent = new Intent(SelectInsuredPersonActivity.this, AddInsuredDetailsActivity.class);
             mIntent.putExtra("patientId",userInfoVO.getPatientId());
             userInfoVO.getPatientId();
             startActivity(mIntent);
         }
    }
}
