package com.patientz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.OrganisationVO;
import com.patientz.VO.PatientUserVO;
import com.patientz.VO.ResponseVO;
import com.patientz.adapters.HospitalsAndOrganisationSearchAdapter;
import com.patientz.databases.DatabaseHandler;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.AppVolley;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchHospitalsAndOrganisationsActivityFragment extends ListFragment {

    private static final String TAG = "SearchHospitalsAndOrganisationsActivityFragment";
    private Button submitSearch;
    private EditText searchBox;
    private String searchQuery, orgType;
    private ArrayList<OrganisationVO> orgSearchList;
    private long id = -1;
    private Spinner orgTypeSpinner, searchOrgByIsdSpinner;
    InputMethodManager imm;
    PatientUserVO mPatientUserVO = new PatientUserVO();
    TextView message;
    Button subscribeOrg;
    boolean noResult = false;
    private RequestQueue mRequestQueue;
    private View mSearchView;
    private View mLoaderStatusView;


    public SearchHospitalsAndOrganisationsActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mRequestQueue = AppVolley.getRequestQueue();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_hospitals_and_organisations, container,
                false);
        searchBox = (EditText) v.findViewById(R.id.org_search_text);
        orgTypeSpinner = (Spinner) v.findViewById(R.id.org_searchby_spinner);
        mLoaderStatusView = v.findViewById(R.id.loading_status);
        // subscribeOrg = (Button) v.findViewById(R.id.org_subscribe);
        AppUtil.setSpinnerValues(getActivity(), orgTypeSpinner,
                R.array.array_orgtype);
        searchOrgByIsdSpinner = (Spinner) v.findViewById(R.id.org_isdCode);
        submitSearch = (Button) v.findViewById(R.id.org_search_button);
        searchBox.requestFocus();
        message = (TextView) v.findViewById(R.id.message);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        DatabaseHandler mDatabaseHandler = DatabaseHandler.dbInit(getActivity());
        long currentSelectedPatientId = AppUtil.getCurrentSelectedPatientId(getActivity());
        try {
            mPatientUserVO = mDatabaseHandler.getProfile(currentSelectedPatientId);//PatientDataHandler.getInstance().getmPatientUserVO();

            imm = (InputMethodManager) getActivity().getSystemService(
                    Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBox, 0);
            submitSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    if (AppUtil.isOnline(getActivity())) {
                        HashMap<String, Object> upshotData = new HashMap<>();
                        upshotData.put(Constant.UpshotEvents.CT_ORGANISATION_SEARCH_CLICKED,true);
                        UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.CT_ORGANISATION_SEARCH_CLICKED);
                        Log.d(TAG,"upshot data="+upshotData.entrySet());
                        searchQuery(v);
                    } else {
                        AppUtil.showDialog(getActivity(),
                                getString(R.string.offlineMode));
                    }
                }
            });
            mSearchView = getListView();
        } catch (Exception exc) {

        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void searchQuery(View view) {
        // searchBy = searchDoctorBySpinner.getSelectedItem().toString();
        searchQuery = searchBox.getText().toString().trim();
        orgType = orgTypeSpinner.getSelectedItem().toString().trim();
        if (searchQuery.length() != 0) {
            // new SearchAsync().execute(searchQuery);
            String info = null;
            info = "?searchTerm=" + searchQuery + "&orgTypeId="
                    + AppUtil.getOrgTypeCode(getActivity(), orgType) + "&patientId="
                    + mPatientUserVO.getPatientId();
            showProgress(true);
            mRequestQueue.add(createSearchRequest(info));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.enter_search_text))
                    .setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                }
                            }).show();
        }
    }


    private StringRequest createSearchRequest(final String params) {
        String szServerUrl = WebServiceUrls.serverUrl + WebServiceUrls.searchOrgForPatient + params;
        com.patientz.utils.Log.d(TAG, "URL \n" + szServerUrl);
        StringRequest mRequest = new StringRequest(Request.Method.GET,
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
                com.patientz.utils.Log.d(TAG, "******************* onErrorResponse ******************* \n");
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AppUtil.showErrorCodeDialog(getActivity());
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(getActivity(),error);

                }
            }

        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppUtil.addHeadersForApp(getActivity(), super.getHeaders());
            }
        };

        return mRequest;
    }


    private void responseHandler(ResponseVO mResponseVO) {

        if (mResponseVO != null) {

            switch ((int) mResponseVO.getCode()) {
                case Constant.RESPONSE_SUCCESS:
                    orgSearchList = new ArrayList();
                    Log.d("Check RESPONSE", "" + mResponseVO.getOrgList());
                    if (mResponseVO.getOrgList() != null && mResponseVO.getOrgList().size() != 0) {
                        Log.d("SDF", "getDoctorsVOList is not null");

                        // message.setVisibility(View.GONE);
                        // subscribeOrg.setVisibility(View.GONE);
                        orgSearchList = mResponseVO.getOrgList();

                        noResult = true;
                    } else {
                        AppUtil.showDialog(getActivity(),
                                getString(R.string.thereisnoexsting) + " " + orgTypeSpinner.getSelectedItem() + " " + getString(R.string.foryoursearch));
                        Log.d("SDF", "list is empty");
                        noResult = true;
                    }
                    HospitalsAndOrganisationSearchAdapter mSearchAdapter = new HospitalsAndOrganisationSearchAdapter(
                            getActivity(), orgSearchList,
                            mPatientUserVO.getPatientId());
                    setListAdapter(mSearchAdapter);
                    break;
                case Constant.RESPONSE_FAILURE:
                    AppUtil.showToast(getActivity(),
                            getString(R.string.failure_msg));
                case Constant.RESPONSE_NOT_LOGGED_IN:
                    AppUtil.showToast(getActivity(),
                            getString(R.string.not_logged_in_error_msg));
                    break;
                case Constant.RESPONSE_SERVER_ERROR:
                    AppUtil.showToast(getActivity(),
                            getString(R.string.server_error_msg));
                    break;
                case Constant.TERMINATE:
                    //finish();
                    break;
                default:
                    break;
            }
        }
        showProgress(false);
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

            mSearchView.setVisibility(View.VISIBLE);
            mSearchView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mSearchView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoaderStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
