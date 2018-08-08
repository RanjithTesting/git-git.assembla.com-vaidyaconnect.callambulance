package com.patientz.adapters;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.patientz.VO.AmbulanceDetailsVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.interfaces.RecyclerViewClickListener;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by YKSIT on 5/30/2016.
 */
public class LACAdapter extends RecyclerView
        .Adapter<LACAdapter
        .ViewHolder> implements View.OnCreateContextMenuListener {


    private static final String TAG = "LACAdapter";
    private static int currentSelectedIndex = -1;
    private ArrayList<AmbulanceDetailsVO> ambulanceList;
    private String[] ambulanceCategories = new String[]{"ALSA", "BLSA", "PTA"};
    private String[] driverStatus = new String[]{"Offline", "Ready", "In Use"};
    private FragmentActivity activity;
    private AmbulanceDetailsVO ambulanceDetailsVO;
    //private CareTeamAdapterListener listener;
    private SparseBooleanArray animationItemsIndex,selectedItems;
    /*  private RecyclerView recyclerView;
      private CardView cardViewForRequestedAmbulance;*/
    private RecyclerViewOnclick mRecyclerViewOnclick;
    private boolean reverseAllAnimations = false;
    private SharedPreferences mSharedPreferences;
    private int position;
    private Location mCurrentLocation;
    private DatabaseHandler mDatabaseHandler;

    public LACAdapter(FragmentActivity activity, ArrayList<AmbulanceDetailsVO> ambulanceList, RecyclerViewOnclick mRecyclerViewOnclick, Location mCurrentLocation,int ambulance_status) {

        //this.ambulanceList.add(ambulanceList.get(0));
        //this.ambulanceList = ambulanceList;

        this.activity = activity;
        // this.recyclerView=recyclerView;
        this.mRecyclerViewOnclick = mRecyclerViewOnclick;
       // this.listener=listener;
        this.mCurrentLocation=mCurrentLocation;
        //this.cardViewForRequestedAmbulance=cardViewForRequestedAmbulance;
        Log.d(TAG, "ambulanceList size=" + ambulanceList.size());
        animationItemsIndex = new SparseBooleanArray();
        selectedItems = new SparseBooleanArray();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mDatabaseHandler=DatabaseHandler.dbInit(activity);
        this.ambulanceList=mDatabaseHandler.getNearbyAmbulanceListFromLocalDB(ambulance_status);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.delete_item_menu, menu);
    }

    @Override
    public LACAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.la_rv_item, parent, false);
        LACAdapter.ViewHolder dataObjectHolder = new LACAdapter.ViewHolder(inflatedView);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "POSTION=" + position);
        this.position=position;
        Log.d(TAG, "Ambulance list size=" + ambulanceList.size());
        ambulanceDetailsVO = ambulanceList.get(position);
        Log.d(TAG,"distanceInKms="+ambulanceDetailsVO.getDistanceInKms());
        holder.tvTimeToReach.setText(ambulanceDetailsVO.getDistanceInKms()+" Kms Away");
        new Thread() {
            public void run() {
                setDistance(ambulanceDetailsVO.getLatitude(),ambulanceDetailsVO.getLongitude(),holder.tvTimeToReach);
            }
        }.start();
        if (!TextUtils.isEmpty(ambulanceDetailsVO.getLicensePlateNo())) {
            String mystring = new String(ambulanceDetailsVO.getLicensePlateNo());
            SpannableString content = new SpannableString(mystring);
            content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
            holder.tvAmbulanceNo.setText(content);
        } else {
            holder.tvAmbulanceNo.setText("");
        }

        Log.d(TAG, "OrgName=" + ambulanceDetailsVO.getOrgName());

        if (ambulanceDetailsVO.getMinFare() != 0) {
            holder.llFare.setVisibility(View.VISIBLE);
            holder.tvMinFare.setText("Rs." + ambulanceDetailsVO.getMinFare());
            holder.ll_fare_know.setVisibility(View.GONE);
        } else {
            holder.llFare.setVisibility(View.GONE);
            holder.ll_fare_know.setVisibility(View.VISIBLE);
        }
        if (ambulanceDetailsVO.getPriceRate() != 0) {
            holder.tvPriceRate.setText("Rs." + ambulanceDetailsVO.getPriceRate() + " /Km");
        }
        if (ambulanceDetailsVO.getRideTimeRate() != 0) {
            holder.tvRideTimeRate.setText("Rs." + ambulanceDetailsVO.getRideTimeRate() + " /Km");
        }
        holder.tvOrgName.setText(ambulanceDetailsVO.getOrgName());
        //holder.tvOrgName.setText("zxczxczvczcZCXZCxczXCzxczxCxzczXC");
        if (!TextUtils.isEmpty(ambulanceDetailsVO.getCategory())) {
            holder.tvAmbulanceType.setText(ambulanceCategories[Integer.valueOf(ambulanceDetailsVO.getCategory()) - 1]);
        }

        holder.ivCallManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> bkDATA = new HashMap<>();
                bkDATA.put("Ambulance Manager Called", true);
                Log.d("Ambulance Manager Called", bkDATA.toString());
                UpshotEvents.createCustomEvent(bkDATA, 17);
                if(!TextUtils.isEmpty(ambulanceDetailsVO.getAmbManagerPhoneNo()))
                {
                    call(ambulanceDetailsVO.getAmbManagerPhoneNo());
                }else
                {
                    call(Constant.CALL_AMBULANCE_MISS_CALL_NO);
                }
            }
        });
        if (ambulanceDetailsVO.isOxygen()) {
            holder.oxygen_ll.setVisibility(View.VISIBLE);
        }
        if (ambulanceDetailsVO.isWheelChair()) {
            holder.wheelchair_ll.setVisibility(View.VISIBLE);
        }
        if (ambulanceDetailsVO.isVentilator()) {
            holder.ventilator_ll.setVisibility(View.VISIBLE);
        }
        if (ambulanceDetailsVO.isAirConditioner()) {
            holder.ac_ll.setVisibility(View.VISIBLE);
        }

        final ArrayList<String> fecilities_list = new ArrayList<String>();
        final ArrayList<String> info_list = new ArrayList<String>();
        if(!TextUtils.isEmpty(ambulanceDetailsVO.getMakeAndModel()))
        {
                info_list.add("Model - " + ambulanceDetailsVO.getMakeAndModel());
        }
        if(!TextUtils.isEmpty(ambulanceDetailsVO.getYearOfManufacture()))
        {
            info_list.add("Manufacture of Year - " + ambulanceDetailsVO.getYearOfManufacture());
        }
        if (ambulanceDetailsVO.isEMTAvailability()) {
            fecilities_list.add("EMT Availability");
        }
        if (ambulanceDetailsVO.isFreezerBox()) {
            fecilities_list.add("Freezer Box");
        }
        if (ambulanceDetailsVO.isStretcher()) {
            fecilities_list.add("Stretcher");
        }
        if (ambulanceDetailsVO.isAED()) {
            fecilities_list.add("AED");
        }
        if (ambulanceDetailsVO.getSeatingCapacityNumber() > 0) {
            fecilities_list.add("Seating Capacity Number - " + ambulanceDetailsVO.getSeatingCapacityNumber());
        }
        if (fecilities_list.size() > 0 || info_list.size() > 0) {
            holder.more_ll.setVisibility(View.VISIBLE);
        }
        /*holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG,"onLongClick");
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });*/
        holder.more_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_fecities_list);

                ImageView text_close = (ImageView) dialog.findViewById(R.id.text_close);
                TextView text_fecilities = (TextView) dialog.findViewById(R.id.text_fecilities);
                TextView text_info = (TextView) dialog.findViewById(R.id.text_info);
                ListView list_fecilities = (ListView) dialog.findViewById(R.id.list_fecilities);
                ListView list_info = (ListView) dialog.findViewById(R.id.list_info);

                if (fecilities_list.size() > 0) {
                    ArrayAdapter<String> feci_adapter = new ArrayAdapter<String>(v.getContext(), R.layout.item, fecilities_list);
                    list_fecilities.setAdapter(feci_adapter);
                } else {
                    text_fecilities.setVisibility(View.GONE);
                }

                if (info_list.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(), R.layout.item, info_list);
                    list_info.setAdapter(adapter);
                } else {
                    text_info.setVisibility(View.GONE);
                }

                text_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewOnclick.getClickedItemPosition(position);
                mRecyclerViewOnclick.getAmbulanceId(ambulanceDetailsVO.getAmbulanceId());
            }
        });
    }

    public void setDistance(String toLat, String toLon, TextView tvTimeToReach) {
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "&destinations=" + toLat + "," + toLon + "&key=" + getApplicationContext().getString(R.string.server_api_key);
            Log.d(TAG,"distance_url\n"+url);
            RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            mRequestQueue.add(createDistanceRequest(url,tvTimeToReach));
    }

    private StringRequest createDistanceRequest(String url, final TextView tvTimeToReach) {
        StringRequest mRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "createTimeCalRequestToDestination response " + response);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("OK"))
                    {
                        Log.d(TAG,"OK");
                        JSONArray rows=jsonObject.getJSONArray("rows");

                        JSONObject jo=rows.getJSONObject(0);

                        JSONArray elements=jo.getJSONArray("elements");

                        JSONObject jo1=elements.getJSONObject(0);

                        JSONObject distance=jo1.getJSONObject("distance");
                        Integer distanceInMts=(Integer)distance.get("value");
                        double distanceInKms=(double)distanceInMts/1000;
                        Log.d("distanceInKms",String.format("%.2f", distanceInKms)+"");
                        mDatabaseHandler.updateNearbyAmbLoc(ambulanceDetailsVO.getAmbulanceId(),distanceInKms);
                        tvTimeToReach.setText(distance.get("text")+" away");

                    }
                } catch (JSONException e1) {

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               /* NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode)
                    {
                        case Constant.HTTP_CODE_SERVER_BUSY:
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setCancelable(false);
                            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout. fragment_server_busy_dialog, null);
                            builder.setView(view);
                            TextView mButton= (TextView)view.findViewById(R.id.bt_done);
                            final AlertDialog mAlertDialog=builder.create();
                            mButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialog.dismiss();
                                }
                            });
                            mAlertDialog.show();
                            break;
                    }
                }else
                {
                    AppUtil.showErrorDialog(activity,error);

                }*/
            }

        }) ;
        //mRequest.setRetryPolicy(new DefaultRetryPolicy(3000,2,2));
        mRequest.setTag(Constant.CANCEL_VOLLEY_REQUEST);
        return mRequest;
    }


    @Override
    public int getItemCount() {
        return ambulanceList.size();
    }

    private void call(String emergencyPhoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + emergencyPhoneNumber));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivity(callIntent);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
            //viewHolder.cardView.setBackgroundColor(ContextCompat.getColor(activity,R.color.white));

        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
            //viewHolder.cardView.setBackgroundColor(ContextCompat.getColor(activity,R.color.new_grey));
        }
        notifyItemChanged(pos);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        ambulanceList.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }
    public interface RecyclerViewOnclick {
        public void getClickedItemPosition(int position);
        public void getAmbulanceId(long ambulanceId);

    }
    public interface CareTeamAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final TextView tvOrgName,call_text,tvMinFare, tvPriceRate, tvRideTimeRate, tvTimeToReach, tvAmbulanceType, tvAmbulanceNo;
        private final LinearLayout oxygen_ll, wheelchair_ll, ventilator_ll, ac_ll, more_ll;
        public RecyclerViewClickListener mListener;
        ImageView ivCallManager;
        private CardView cardView;
        private LinearLayout llFare, ll_fare_know;

        // private final LinearLayout llDirection;

        public ViewHolder(View itemView) {
            super(itemView);

            ivCallManager = (ImageView) itemView.findViewById(R.id.iv_call_manager);

            cardView = (CardView) itemView.findViewById(R.id.cardview);
            tvAmbulanceType = (TextView) itemView.findViewById(R.id.tv_ambulance_type);
            tvOrgName = (TextView) itemView.findViewById(R.id.tv_org_name);
            tvTimeToReach = (TextView) itemView.findViewById(R.id.tv_time_to_reach);
            tvMinFare = (TextView) itemView.findViewById(R.id.tv_min_fare);
            tvPriceRate = (TextView) itemView.findViewById(R.id.tv_price_rate);
            tvRideTimeRate = (TextView) itemView.findViewById(R.id.tv_ride_time_rate);
            tvAmbulanceNo = (TextView) itemView.findViewById(R.id.tv_ambulance_no);
            call_text = (TextView) itemView.findViewById(R.id.call_text);
            llFare = (LinearLayout) itemView.findViewById(R.id.ll_fare);
            ll_fare_know = (LinearLayout) itemView.findViewById(R.id.ll_fare_know);
            oxygen_ll = (LinearLayout) itemView.findViewById(R.id.oxygen_ll);
            wheelchair_ll = (LinearLayout) itemView.findViewById(R.id.wheelchair_ll);
            ventilator_ll = (LinearLayout) itemView.findViewById(R.id.ventilator_ll);
            ac_ll = (LinearLayout) itemView.findViewById(R.id.ac_ll);
            more_ll = (LinearLayout) itemView.findViewById(R.id.more_ll);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "onLongClick");
            //listener.onRowLongClicked(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }
}
