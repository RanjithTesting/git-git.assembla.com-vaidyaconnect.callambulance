package com.patientz.activity;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import com.patientz.utils.AppUtil;
import com.patientz.utils.Log;

import java.util.List;

public class SettingActivity2 extends BaseActivity {
    private static final String TAG = "SettingActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        private Preference prefDrawOverOtherApps;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            prefDrawOverOtherApps =  findPreference("pref_draw_over_other_apps");

           prefDrawOverOtherApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
               @Override
               public boolean onPreferenceClick(Preference preference) {
                   showDialogToAskUserForPermission();
                   return true;
               }
           });
        }

        private void showDialogToAskUserForPermission() {


            try {
                Intent intent = new Intent();
                String manufacturer = android.os.Build.MANUFACTURER;
                Log.d(TAG,"manufacturer="+manufacturer);
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                }else
                {
                     intent = new Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                }

                List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if  (list.size() > 0) {
                    startActivity(intent);
                }else
                {
                    AppUtil.showToast(getActivity(),"Your Phone already supports this feature");
                }

            } catch (Exception e) {
            }
        }


    }


}
