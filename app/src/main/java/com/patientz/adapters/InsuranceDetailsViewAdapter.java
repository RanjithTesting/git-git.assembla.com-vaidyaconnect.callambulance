package com.patientz.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.patientz.VO.InsuranceVO;
import com.patientz.VO.KeyValueObject;
import com.patientz.VO.UserInfoVO;
import com.patientz.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sukesh on 4/4/16.
 */
public class InsuranceDetailsViewAdapter extends BaseAdapter {
    private List<KeyValueObject> cList;
    Context context;
    int position;
    View convertView;
    UserInfoVO mInfoVO;
    String flag;
    InsuranceVO mInsuranceVO;

    public InsuranceDetailsViewAdapter(Context context, List<KeyValueObject> contactList,
                                       String flag, InsuranceVO insuranceVO, UserInfoVO userInfoVO) {
        this.mInsuranceVO = insuranceVO;
        this.mInfoVO = userInfoVO;
        this.cList = contactList;
        this.context = context;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return cList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        this.position = position;
        // When convertView is not null, we can reuse it directly, there is no
        // nee
        // to reinflate it. We only inflate a new View when the convertView
        // supplied
        // by ListView is null.\

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.profile_row, null);

            // Creates a ViewHolder and store references to the two children
            // views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.title = (TextView) rowView.findViewById(R.id.key);
            holder.value = (TextView) rowView.findViewById(R.id.value);
            holder.callButton = (ImageView) rowView
                    .findViewById(R.id.patientCall);
            holder.emailButton = (ImageView) rowView
                    .findViewById(R.id.patientMsg);

            rowView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) rowView.getTag();
        }

        if (position == 0 && flag.equalsIgnoreCase("DemographicsFragment")
                && mInfoVO.getPhoneNumber() != null) {
            //holder.callButton.setVisibility(View.VISIBLE);
            holder.title.setCompoundDrawables(null, null, context.getResources().getDrawable(R.drawable.emergency_calling), null);
            LinearLayout profileRowLayout = (LinearLayout) rowView
                    .findViewById(R.id.profileRowParentLayout);
            List<PhoneNumber> allNumbers = new ArrayList<>();
            if (mInfoVO.getPhoneNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setNumberType(context
                        .getString(R.string.hint_phone_number));
                phoneNumber.setNumber(mInfoVO
                        .getPhoneNumber());
                allNumbers.add(phoneNumber);
            }
            if (mInfoVO.getAltPhoneNumber() != null) {
                PhoneNumber alternateNumber = new PhoneNumber();
                alternateNumber.setNumberType(context
                        .getString(R.string.altPhoneNumber));
                alternateNumber.setNumber(mInfoVO
                        .getAltPhoneNumber());
                allNumbers.add(alternateNumber);
            }
            // makeCall(profileRowLayout, allNumbers);
        } else {
            holder.callButton.setVisibility(View.GONE);

        }
        if ((position == 1)
                && (flag.equalsIgnoreCase("DemographicsFragment") && !TextUtils
                .isEmpty(mInfoVO.getEmailId()))) {
            //holder.emailButton.setVisibility(View.VISIBLE);
            LinearLayout profileRowLayout = (LinearLayout) rowView
                    .findViewById(R.id.profileRowParentLayout);
            profileRowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // sendMessage(mInfoVO.getEmailId());
                }
            });
        } else {
            holder.emailButton.setVisibility(View.GONE);
        }
        if (position == getCount() - 1
                && flag.equalsIgnoreCase("InsuranceDetailsFragment")) {
            //holder.callButton.setVisibility(View.VISIBLE);
            holder.title.setCompoundDrawables(null, null, context.getResources().getDrawable(R.drawable.emergency_calling), null);
            LinearLayout profileRowLayout = (LinearLayout) rowView
                    .findViewById(R.id.profileRowParentLayout);
            List<PhoneNumber> insuranceNumbers = new ArrayList<>();

            if (mInsuranceVO.getClaimPhoneNumber() != null) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setNumberType(context
                        .getString(R.string.hint_phone_number));
                phoneNumber.setNumber(mInsuranceVO
                        .getClaimPhoneNumber());
                insuranceNumbers.add(phoneNumber);
            }
            //  makeCall(profileRowLayout, insuranceNumbers);
        }
        // Bind the data efficiently with the holder.
        holder.title.setText(cList.get(position).getKey());
        holder.value.setText(cList.get(position).getValue());
        return rowView;
    }

    static class ViewHolder {
        ImageView emailButton;
        TextView title;
        TextView value;
        ImageView callButton;

    }
/*

    public void makeCall(LinearLayout profileRowLayout,
                         final List<PhoneNumber> allNumbers) {
        if (allNumbers.size() !=0 ) {
            int i = 0;
            CharSequence[] cs = new String[allNumbers.size()];
            while (i != allNumbers.size()) {
                cs[i] = allNumbers.get(i).getNumberType()
                        + allNumbers.get(i).getNumber();
                i++;
            }
            final CharSequence[] items = cs;
            profileRowLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setTitle(context.getString(R.string.callNumber));
                    builder.setItems(items,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int item) {
                                    Toast.makeText(
                                            context,
                                            allNumbers.get(item).getNumber(),
                                            Toast.LENGTH_SHORT).show();
                                    String phonee = context
                                            .getString(R.string.tel)
                                            + allNumbers.get(item)
                                            .getNumber();
                                    Intent intent = new Intent(
                                            Intent.ACTION_DIAL, Uri
                                            .parse(phonee));
                                    context.startActivity(intent);
                                }
                            }).show();
                }
            });
        }
    }

    public void sendMessage(String email) {
        Intent i;
        i = new Intent(Intent.ACTION_SENDTO, Uri.parse(context
                .getString(R.string.mailTo) + email));
        context.startActivity(Intent.createChooser(i,
                context.getString(R.string.sendMailTo)));
    }
*/

    public class PhoneNumber {
        String numberType;
        String number;

        public String getNumberType() {
            return numberType;
        }

        public void setNumberType(String numberType) {
            this.numberType = numberType;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

}
