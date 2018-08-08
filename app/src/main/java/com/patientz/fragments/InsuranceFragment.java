package com.patientz.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.InsuranceUpload;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.activity.ActivityEditInsurance;
import com.patientz.activity.AddInsuredDetailsActivity;
import com.patientz.activity.InsurancePolicyCoverageInfoActivity;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.CommonUtils;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class InsuranceFragment extends BaseFragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG ="InsuranceFragment" ;
    private static final int REQUEST_CODE_INSURANCE_UPDATE =3 ;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private PatientUserVO patientUserVO;
    private UserInfoVO userInfoVO;

    private DatabaseHandler mDatabaseHandler;
    private ArrayList<InsuranceVO> insuranceLists;
    private Button btAddExistingPolicy;
    private LinearLayout llAddExistingPolicy;
    private ProgressDialog dialog;
    private InsuranceRecyclerViewAdapter mInsuranceRecyclerViewAdapter;

    private TextView emptytext,tvEmail;
    private Button btBuyAccidentPolicy;
    private SwipeRefreshLayout swipeContainer;

    private void findViewsForExistingInsurances(View view) {
        recyclerView = view.findViewById(R.id.list);
        emptytext =  view.findViewById(R.id.emptytext);
        tvEmail =  view.findViewById(R.id.tv_email);
        tvEmail.setOnClickListener(this);
        llAddExistingPolicy = view.findViewById( R.id.ll_add_existing_policy );
        btBuyAccidentPolicy =view.findViewById( R.id.bt_buy_accident_policy );
        llAddExistingPolicy.setOnClickListener(this);
        btBuyAccidentPolicy.setOnClickListener(this);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestQueue mRequestQueue= AppVolley.getRequestQueue();
                InsuranceVO mInsuranceVO=mDatabaseHandler.getPurchasedInsurances(patientUserVO.getPatientId());
                if(mInsuranceVO!=null)
                {
                    mRequestQueue.add(getInsuranceUpload(mInsuranceVO));
                }else
                {
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        swipeContainer.setColorSchemeResources(R.color.harvard_crimpson,
                R.color.yellow_green,
                R.color.harvard_crimpson,
                R.color.yellow_green);
    }


    @Override
    public void onClick(View v) {
        HashMap<String, Object> upshotData = new HashMap<>();

        switch (v.getId())
        {
            case R.id.ll_add_existing_policy:
                upshotData.put(Constant.UpshotEvents.INSURANCE_ADD_EXISTING_POLICY_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_ADD_EXISTING_POLICY_CLICKED);
                Intent mIntent = new Intent(getActivity(), ActivityEditInsurance.class);
                getActivity().startActivityForResult(mIntent,REQUEST_CODE_INSURANCE_UPDATE);
                break;
            case R.id.bt_buy_accident_policy:
                upshotData.put(Constant.UpshotEvents.INSURANCE_BUY_NOW_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_BUY_NOW_CLICKED);
                InsuranceVO mInsuranceVO=mDatabaseHandler.getPurchasedInsurances(patientUserVO.getPatientId());
                final SharedPreferences mSharedPreferences=getActivity().getSharedPreferences(String.valueOf(patientUserVO.getPatientId()), Context.MODE_PRIVATE);
                Log.d("sp=",mSharedPreferences+"");
                Log.d("patientId=",mSharedPreferences.contains("patientId")+"");
                if(!mSharedPreferences.contains("patient_id"))
                {
                    if(mInsuranceVO==null)
                    {
                        int age=AppUtil.getAge(userInfoVO.getDateOfBirth(),getActivity());
                        if(age>=18 && age<=65)
                        {
                            Intent intent = new Intent(getActivity(), InsurancePolicyCoverageInfoActivity.class);
                            startActivityForResult(intent,REQUEST_CODE_INSURANCE_UPDATE);
                            getActivity().finish();
                        }else
                        {
                            AppUtil.showDialog(getActivity(),getActivity().getString(R.string.ins_buy_age_restriction_msg));
                        }
                    }else
                    {
                        AppUtil.showDialog(getActivity(),getActivity().getString(R.string.msg_restrict_user_to_buy_same_ins_twice));
                    }
                }else
                {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.incomplete_ins_purchase))
                            .setPositiveButton(getString(R.string.OK),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent mIntent = new Intent(getActivity(), AddInsuredDetailsActivity.class);
                                            mIntent.putExtra("patientId",mSharedPreferences.getLong("patient_id",0));
                                            mIntent.putExtra("paytm_ref_id",mSharedPreferences.getString("paytm_ref_id",""));
                                            mIntent.putExtra("totalAmount",mSharedPreferences.getInt("amount_paid",0));
                                            mIntent.putExtra("couponCode",mSharedPreferences.getString("coupon_code",""));
                                            startActivity(mIntent);
                                        }
                                    }).show();
                }
                break;
            case R.id.tv_email:
                upshotData.put(Constant.UpshotEvents.INSURANCE_SUPPORT_CLICKED,true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.INSURANCE_SUPPORT_CLICKED);
                sendEmail();
                break;
        }
    }
    public InsuranceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static InsuranceFragment newInstance(int columnCount) {
        InsuranceFragment fragment = new InsuranceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        try {
            patientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);
            userInfoVO = mDatabaseHandler.getUserInfo(currentSelectedPatientId);

            insuranceLists = mDatabaseHandler.getAllInsurances(currentSelectedPatientId);
            if(insuranceLists.size()>0)
            {
                Collections.sort(insuranceLists, new Comparator<InsuranceVO>() {
                    public int compare(InsuranceVO o1, InsuranceVO o2) {
                        return o2.getUpdatedDate().compareTo(o1.getUpdatedDate());
                    }
                });
                 view= inflater.inflate(R.layout.insurance_landing_page_with_existing_insurances, container, false);
                 findViewsForExistingInsurances(view);
                 mInsuranceRecyclerViewAdapter=new InsuranceRecyclerViewAdapter(getActivity(), insuranceLists, mListener);
                 recyclerView.setAdapter(mInsuranceRecyclerViewAdapter);
            }else
            {
                 view= inflater.inflate(R.layout.insurance_landing_page_with_no_existing_insurances, container, false);
                 findViewsForNoInsurances(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle mBundle=getArguments();
        Log.d(TAG,"refresh="+mBundle.getBoolean("refresh"));
        if(mBundle.getBoolean("refresh")) {
            if(swipeContainer!=null) {
                swipeContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(true);
                        RequestQueue mRequestQueue = AppVolley.getRequestQueue();
                        InsuranceVO mInsuranceVO = mDatabaseHandler.getPurchasedInsurances(patientUserVO.getPatientId());
                        if (mInsuranceVO != null) {
                            mRequestQueue.add(getInsuranceUpload(mInsuranceVO));
                        }
                    }
                });
            }
        }
        //completePendingInsurancePurchaseIfExists();
        return view;
    }



    private void findViewsForNoInsurances(View view) {
        llAddExistingPolicy = view.findViewById( R.id.ll_add_existing_policy );
        btBuyAccidentPolicy = (Button)view.findViewById( R.id.bt_buy_accident_policy );
        tvEmail =  view.findViewById(R.id.tv_email);
        tvEmail.setOnClickListener(this);
        llAddExistingPolicy.setOnClickListener(this);
        btBuyAccidentPolicy.setOnClickListener(this);
    }
    public StringRequest getInsuranceUpload(InsuranceVO insuranceVO) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.insuranceUploadList+"?id="+insuranceVO.getInsuranceUploadId()+"&patientId="+insuranceVO.getPatientId();
        Log.d(TAG,"getInsuranceUpload>>url="+szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.POST,
                szServerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swipeContainer.setRefreshing(false);
                Log.d(TAG, "getInsuranceUpload>>response:" + response);
                if(response!=null) {
                    Gson gson = new Gson();
                    Type objectType = new TypeToken<InsuranceUpload>() {
                    }.getType();
                    InsuranceUpload insuranceUpload = gson.fromJson(response, objectType);
                    DatabaseHandler mDatabaseHandler=DatabaseHandler.dbInit(getActivity());
                    mDatabaseHandler.insertUserUploadInsurance(insuranceUpload);
                    try {
                        insuranceLists.clear();
                        mInsuranceRecyclerViewAdapter.notifyDataSetChanged();
                        ArrayList<InsuranceVO> mInsuranceVOS = mDatabaseHandler.getAllInsurances(patientUserVO.getPatientId());
                        insuranceLists.addAll(mInsuranceVOS);
                        Collections.sort(insuranceLists, new Comparator<InsuranceVO>() {
                            public int compare(InsuranceVO o1, InsuranceVO o2) {
                                return o2.getUpdatedDate().compareTo(o1.getUpdatedDate());
                            }
                        });
                        mInsuranceRecyclerViewAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getActivity(), super.getHeaders());
            }
        };
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        mRequest.setRetryPolicy(new DefaultRetryPolicy(30000,2,2));
        return mRequest;
    }
    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(patientUserVO.getRole()!=null)
        {
            if (!patientUserVO.getRole().equalsIgnoreCase(Constant.EMERGENCY_CONTACT)) {
                menu.clear();
                inflater.inflate(R.menu.menu_add_insurance, menu);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
public void sendEmail()
{
    Intent i;
    i = new Intent(Intent.ACTION_SENDTO, Uri.parse(getActivity()
            .getString(R.string.mailTo) + getActivity().getString(R.string.email_callambulance_support)));
    i.putExtra(Intent.EXTRA_SUBJECT,getActivity().getString(R.string.insurance_issue)+": "+CommonUtils.getSP(getActivity()).getString(Constant.USER_ID,null)+","+patientUserVO.getPhoneNumber());
    getActivity().startActivity(Intent.createChooser(i,
            getActivity().getString(R.string.sendMailTo)));
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_insurance) {
            Intent mIntent = new Intent(getActivity(), ActivityEditInsurance.class);
            getActivity().startActivityForResult(mIntent,REQUEST_CODE_INSURANCE_UPDATE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(InsuranceVO item);
    }
}
