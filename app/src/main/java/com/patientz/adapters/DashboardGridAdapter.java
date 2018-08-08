package com.patientz.adapters;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.VO.EmergencyContactsVO;
import com.patientz.VO.HealthRecordVO;
import com.patientz.VO.InsuranceVO;
import com.patientz.VO.OrgBranchVO;
import com.patientz.VO.UserInfoVO;
import com.patientz.activity.R;
import com.patientz.databases.DatabaseHandler;
import com.patientz.databases.DatabaseHandlerAssetHelper;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DashboardGridAdapter extends RecyclerView.Adapter<DashboardGridAdapter.ViewHolder> {
    private static final String TAG = "DashboardGridAdapter";
    private Context mContext;
    private Integer[] images;
    private String[] imageDescriptions;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private DatabaseHandler mDatabaseHandler;
    private long  currentSelectedProfile;

    // data is passed into the constructor
    public DashboardGridAdapter(Context context, Integer[] images,String[] imageDescriptions) {
        mContext=context;
        this.mInflater = LayoutInflater.from(context);
        this.images = images;
        this.imageDescriptions = imageDescriptions;
        mDatabaseHandler = DatabaseHandler.dbInit(getApplicationContext());
        currentSelectedProfile = AppUtil.getCurrentSelectedPatientId(getApplicationContext());
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.dashboard_grid_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDashboardGrid.setText(imageDescriptions[position]);
        holder.ivDashboardGridIcon.setImageResource(images[position]);
        switch (position)
        {
            case 2:
                try {
                    HealthRecordVO emergencyHealthRecord = mDatabaseHandler.getUserHealthRecord(Constant.RECORD_TYPE_EHR, currentSelectedProfile);
                    if (emergencyHealthRecord.getHealthRecord() != null && !emergencyHealthRecord.getHealthRecord().equals("") && emergencyHealthRecord.getHealthRecord().size() != 0) {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_green_tick);
                    } else {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_red_cross);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 0:
                try {
                    ArrayList<EmergencyContactsVO> ecList = mDatabaseHandler.getEmergencyContacts(currentSelectedProfile, Constant.EMERGENCY_CONTACT);
                    ArrayList<EmergencyContactsVO> careTeamList = mDatabaseHandler.getEmergencyContacts(currentSelectedProfile, Constant.CARE_GIVER);
                    if (ecList.size()>0 || careTeamList.size()>0) {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_green_tick);
                    } else {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_red_cross);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    long preferredOrgBranchId = mDatabaseHandler.getPreferredOrgBranchId(currentSelectedProfile);
                    if (preferredOrgBranchId != 0) {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_green_tick);
                        DatabaseHandlerAssetHelper databaseHandlerAssetHelper = DatabaseHandlerAssetHelper.dbInit(mContext);
                        Log.d(TAG, ">>PreferredOrgBranchId=" + preferredOrgBranchId);
                        OrgBranchVO mOrgBranchVO = databaseHandlerAssetHelper.getPreferredOrgBranch(preferredOrgBranchId);
                        Log.d(TAG, "Org Logo id=" + mOrgBranchVO.getOrgLogoId());

                        if (mOrgBranchVO.getOrgLogoId() != 0) {
                            String fileName = "img_" + mOrgBranchVO.getOrgLogoId();
                            boolean isFileExists = AppUtil.checkIfFileExists(mContext, fileName);
                            if (!isFileExists) {
                                AppUtil.downloadImage(mContext, mOrgBranchVO.getOrgLogoId(), fileName, holder.ivDashboardGridIcon, null);
                            } else {
                                final String STORAGE_PATH = mContext
                                        .getResources().getString(
                                                R.string.profileImagePath);
                                File cnxDir = new File(Environment
                                        .getExternalStorageDirectory(), STORAGE_PATH);
                                if (!cnxDir.exists()) {
                                    cnxDir.mkdirs();
                                }
                                File file = new File(cnxDir, fileName);
                                Picasso.with(mContext).load(file).resize(100, 100).into(holder.ivDashboardGridIcon);
                            }
                        } else {
                            Picasso.with(mContext).load(R.drawable.dashboard_emri).resize(130, 130).into(holder.ivDashboardGridIcon);
                        }
                    } else {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_red_cross);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    UserInfoVO mInfoVO = mDatabaseHandler.getUserInfo(currentSelectedProfile);
                    String[] bloodGroupArray = mContext.getResources().getStringArray(R.array.array_bloodgroup);
                    List<String> stringList = new ArrayList<String>(Arrays.asList(bloodGroupArray));
                    if (stringList.contains(mInfoVO.getBloodGroup())) {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_green_tick);
                    } else {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_red_cross);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    ArrayList<InsuranceVO> mInsuranceVOs = mDatabaseHandler.getAllInsurances(currentSelectedProfile);
                    Log.d(TAG,"mInsuranceVOs size="+mInsuranceVOs.size());
                    if (mInsuranceVOs.size()!=0) {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_green_tick);
                    } else {
                        holder.ivDashboardTick.setImageResource(R.drawable.dashboard_red_cross);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                holder.ivDashboardTick.setVisibility(View.INVISIBLE);
                break;
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return images.length;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDashboardGrid;
        ImageView ivDashboardTick,ivDashboardGridIcon;

        ViewHolder(View itemView) {
            super(itemView);
            ivDashboardGridIcon = (ImageView)itemView.findViewById( R.id.iv_dashboard_grid_icon );
            ivDashboardTick = (ImageView)itemView.findViewById( R.id.iv_dashboard_tick );
            tvDashboardGrid = (TextView)itemView.findViewById( R.id.tv_dashboard_grid );
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return imageDescriptions[id];
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
