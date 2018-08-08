package com.patientz.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.patientz.VO.BloodyFriendVO;
import com.patientz.activity.R;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServiceFetchContacts extends IntentService {
    private static final String TAG = "IntentServiceFetchContacts";
    protected ResultReceiver mReceiver;

    public IntentServiceFetchContacts() {
        super("IntentServiceFetchContacts");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mReceiver = intent.getParcelableExtra(getString(R.string.package_name) + "." + Constant.CONTACTS_RECEIVER);
        try {
            ArrayList<BloodyFriendVO> mBloodyFriendVOs = AppUtil.readPhoneContacts(IntentServiceFetchContacts.this);
//            if (mBloodyFriendVOs.size() > 0) {
            Log.e("readPhoneContacts size", "--> " + mBloodyFriendVOs.size());
//            }
            deliverResultToReceiver(567);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deliverResultToReceiver(int resultCode) {
        Log.d(TAG, "deliverResultToReceiver");
       /* Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.package_name) + "." + Constant.RESULT_CONTACTS_KEY,contactlist);
        //bundle.putParcelable(getString(R.string.package_name) + "." + Constant.RESULT_CONTACTS_KEY,contactlist);
        //bundle.putSerializable(getString(R.string.package_name) + "." + Constant.RESULT_CONTACTS_KEY, contactlist);*/
        mReceiver.send(resultCode, new Bundle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
