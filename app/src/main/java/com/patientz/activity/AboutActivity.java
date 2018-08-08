package com.patientz.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.patientz.VO.UserVO;
import com.patientz.upshot.UpshotEvents;
import com.patientz.utils.AppUtil;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;
import com.patientz.webservices.WebServiceUrls;

import java.util.HashMap;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AboutActivity";
    private ImageView followFB, followTwitter, inviteFriends;
    private TextView appVersion, webSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        followFB = (ImageView) findViewById(R.id.follow_fb);
        followTwitter = (ImageView) findViewById(R.id.follow_twitter);
        inviteFriends = (ImageView) findViewById(R.id.invite_friends);
        webSite = (TextView) findViewById(R.id.website);
        appVersion = (TextView) findViewById(R.id.version);
        followFB.setOnClickListener(this);
        followTwitter.setOnClickListener(this);
        inviteFriends.setOnClickListener(this);
        webSite.setOnClickListener(this);
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null)
            appVersion.setText(getString(R.string.version) + " : " + info.versionName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void openFollowLink(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        if(AppUtil.isAvailable(getApplicationContext(),i))
        {
            startActivity(i);
        }else
        {
            AppUtil.showToast(getApplicationContext(),"No Application found to open this url");
        }
    }


    @Override
    public void onClick(View v) {
        HashMap<String, Object> upshotData = new HashMap<>();

        switch (v.getId()) {
            case R.id.follow_fb:
                upshotData.put(Constant.UpshotEvents.ABOUT_SCREEN_FOLLOW_ON_FB, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ABOUT_SCREEN_FOLLOW_ON_FB);
                Log.d(TAG,"upsh ot data="+upshotData.entrySet());
                openFollowLink(WebServiceUrls.facebook);
                break;
            case R.id.follow_twitter:
                upshotData.put(Constant.UpshotEvents.ABOUT_SCREEN_FOLLOW_ON_TWITTER, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ABOUT_SCREEN_FOLLOW_ON_TWITTER);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                openFollowLink(WebServiceUrls.twitter);
                break;
            case R.id.invite_friends:
                upshotData.put(Constant.UpshotEvents.ABOUT_SCREEN_INVITE_FRIENDS, true);
                UpshotEvents.createCustomEvent(upshotData, Constant.UpshotEventsId.ABOUT_SCREEN_INVITE_FRIENDS);
                Log.d(TAG,"upshot data="+upshotData.entrySet());
                inviteFriends();
                break;
            case R.id.website:
                openFollowLink(WebServiceUrls.web_url_link);
                break;
        }
    }
    public  void inviteFriends() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        UserVO user = AppUtil.getLoggedUser(getApplicationContext());
        String url = getString(R.string.send_invite);
        if(user!=null)
        {
            if(user.getUserId()!=0) {
                url = url + "  \n" + generateDynamicLink(user.getUserId());//url + "?referralCode=" +user.getUserId();
            }else
            {
                url = url + "\n  http://bit.ly/callambulance";
            }
        }else
        {
            url = url + "\n  http://bit.ly/callambulance";
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_using)));
    }



    private  String generateDynamicLink(long referralID) {
        String link = "https://s8geh.app.goo.gl/?link=https://callambulance.in?" +
                "referralCode=" + referralID
                + "&utm_campaign=invite" + "&utm_source=ca_app" + "&utm_medium=in_app"
                + "&apn=com.patientz.activity"+"&referralCode=" + referralID;
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, MODE_PRIVATE);
        String deepLink = sharedPreferences.getString(Constant.DYNAMIC_LINK_2, "");
        Log.d(TAG,"DYNAMIC_LINK_2="+deepLink);
        if (TextUtils.isEmpty(deepLink)) {
            return link;
        } else {
            return deepLink;
        }
    }
}
