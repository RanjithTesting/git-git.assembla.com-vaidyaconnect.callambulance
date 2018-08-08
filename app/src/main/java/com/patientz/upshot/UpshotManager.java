package com.patientz.upshot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brandkinesis.BKProperties;
import com.brandkinesis.BKUIPrefComponents;
import com.brandkinesis.BKUserInfo;
import com.brandkinesis.BrandKinesis;
import com.brandkinesis.activitymanager.BKActivityTypes;
import com.brandkinesis.callback.BKActivityCallback;
import com.brandkinesis.callback.BKUserInfoCallback;
import com.patientz.VO.UserInfoVO;
import com.patientz.utils.Constant;
import com.patientz.utils.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sukesh on 14/7/16.
 */
public class UpshotManager {

    private static final int UPSHOT_EC_CUSTOM_EVENT_ID =16 ;
    private static final int UPSHOT_CG_CUSTOM_EVENT_ID =17 ;

    private static BrandKinesis brandKinesis;
    private static int UPSHOT_PROFILE_CUSTOM_EVENT_ID = 4;
    private static int UPSHOT_HEAlTH_RECORD_CUSTOM_EVENT_ID = 8;
    private static int UPSHOT_INSURANCE_CUSTOM_EVENT_ID = 10;

    public static BrandKinesis getUpshotInstance() {
        if (brandKinesis != null) {
            return brandKinesis;
        } else {
            throw new IllegalStateException("UpshotManager has not been initialized");
        }
    }

    public static void terminate(Context context) {
        BrandKinesis bk = BrandKinesis.getBKInstance();
        bk.terminate(context);
    }

    public static void setUserDetails(Context context, UserInfoVO userInfoVO) {
        Bundle userInfo = new Bundle();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constant.APP_SHARED_PREFERENCE, context.MODE_PRIVATE);
        userInfo.putString(BKUserInfo.BKUserData.FIRST_NAME, "");
        userInfo.putInt(BKUserInfo.BKUserData.AGE, userInfoVO.getAge());
        userInfo.putInt(BKUserInfo.BKUserData.GENDER, userInfoVO.getGender() == 1 ? BKUserInfo.BKGender.MALE : BKUserInfo.BKGender.FEMALE);
        userInfo.putString(BKUserInfo.BKExternalIds.APPUID, sharedPreferences.getString(Constant.K_REG_ID, ""));
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = defaultSharedPreferences.getString("sel_language", "en");
        userInfo.putString(BKUserInfo.BKUserData.LANGUAGE, lang);
        HashMap<String, Object> others = new HashMap<>();
        others.put("city", userInfoVO.getCity());
        others.put("state", userInfoVO.getState());
        userInfo.putSerializable(BKUserInfo.BKUserData.OTHERS, others);
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if (bkInstance != null) {
            bkInstance.setUserInfoBundle(userInfo, new BKUserInfoCallback() {
                @Override
                public void onUserInfoUploaded(boolean uploadStatus) {
                    Log.d("UpshotManager", "UpshotManager User uploadStatus : " + uploadStatus);
                }
            });
        }
    }


    public static void showActivity(final Context context, String tag) {
        BrandKinesis brandKinesis = BrandKinesis.getBKInstance();
        if (brandKinesis == null) {
            return;
        }
        brandKinesis.getActivity(context, BKActivityTypes.ACTIVITY_ANY, tag, new BKActivityCallback() {
            @Override
            public void onActivityError(int i) {

            }

            @Override
            public void onActivityCreated() {
                //FOR GAMES
            }

            @Override
            public void onActivityDestroyed() {
                //FOR GAMES
            }

            @Override
            public boolean repeatUserSkippedActivity(BKActivityTypes bkActivityTypes) {
                return false;
            }

            @Override
            public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes bkActivityTypes, Map<String, Object> map) {
                Log.d("map=",map+"");
                if(map!=null) {
                    if(!map.isEmpty())
                    {
                        Log.e("data", map.toString());
                        String actionValue = map.get("deepLink").toString();
                        redirectDeepLink(context,actionValue); // app's logic
                    }
                }
            }

            @Override
            public boolean brandKinesisShouldRedirectForActivityType(BKActivityTypes bkActivityTypes) {
                return false;
            }

            @Override
            public boolean brandkinesisShouldEnableTapGusture(BKActivityTypes bkActivityTypes) {
                return false;
            }
        });

    }

    public static void redirectDeepLink(Context context,String deepLink){
        if(!TextUtils.isEmpty(deepLink)){
            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(deepLink);
            sanitizer.parseUrl(deepLink);
            String type = sanitizer.getValue("page");
            String url = sanitizer.getValue("url");
            Log.d("BKPushAction","type push deep link : "+type+"\n url : "+url);
            if(Tag.getActivityFromTag(type)!=null){
                Intent intent1 = new Intent(context, Tag.getActivityFromTag(type));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent1);
            }else if(TextUtils.equals("web",type)&& URLUtil.isValidUrl(url)){
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent2);
            }
        }
    }

    public static void getSurvey(Context context, String tagForSurvey) {
        BrandKinesis brandKinesis = BrandKinesis.getBKInstance();
        if (brandKinesis == null) {
            return;
        }
        Log.d("tag is", "" + tagForSurvey);
//                bkInstance.getActivity(this, BKActivityTypes.ACTIVITY_SURVEY, tagForSurvey, BKActivityCallback());
        brandKinesis.getActivity(context, BKActivityTypes.ACTIVITY_SURVEY, tagForSurvey, new BKActivityCallback() {
            @Override
            public void onActivityError(int error) {
                Log.e("TAG", "Survey activity error occurred: " + error);
            }

            @Override
            public void onActivityCreated() {
                Log.i("TAG", "Survey activity created");
            }

            @Override
            public void onActivityDestroyed() {
                Log.i("TAG", "Survey activity destroyed");
            }

            @Override
            public boolean repeatUserSkippedActivity(BKActivityTypes activityType) {
                return false;
            }

            @Override
            public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes activityType, Map<String, Object> actionData) {
            }

            @Override
            public boolean brandKinesisShouldRedirectForActivityType(BKActivityTypes activityType) {
                return true;
            }

            @Override
            public boolean brandkinesisShouldEnableTapGusture(BKActivityTypes bkActivityTypes) {
                return false;
            }
        });
    }

    BKUIPrefComponents bkuiPrefComponents = new BKUIPrefComponents() {

        @Override
        public void setPreferencesForButton(Button button, BKActivityTypes bkActivityTypes, BKActivityButtonTypes bkActivityButtonTypes) {

            switch (bkActivityTypes) {
                case ACTIVITY_SURVEY:
                    switch (bkActivityButtonTypes) {
                        case BKACTIVITY_SURVEY_CONTINUE_BUTTON:
                            button.setBackgroundColor(Color.parseColor("#ff6666"));
                            break;
                        case BKACTIVITY_SUBMIT_BUTTON:
//                            button.setTextColor(Color.parseColor("@color"));
                            break;
                        case BKACTIVITY_SURVEY_NEXT_BUTTON:
                            button.setText("Go Ahead");
                            button.setBackgroundColor(Color.parseColor("#ff33ff"));
                            break;
                        case BKACTIVITY_SURVEY_PREVIOUS_BUTTON:
                            button.setText("Go Back");
                            button.setBackgroundColor(Color.parseColor("#d279a6"));
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void setPreferencesForTextView(TextView textView, BKActivityTypes bkActivityTypes, BKActivityTextViewTypes bkActivityTextViewTypes) {

            switch (bkActivityTypes) {
                case ACTIVITY_SURVEY:
                    switch (bkActivityTextViewTypes) {
                        case BKACTIVITY_HEADER_TV:
//                            textView.setTextColor(Color.parseColor("@color"));
                            break;
                        case BKACTIVITY_QUESTION_TV:
//                            textView.setTextColor(Color.parseColor("@color"));
                            break;
                        case BKACTIVITY_SURVEY_DESC_TV:
//                            textView.setTextColor(Color.parseColor("@color"));
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void setPreferencesForImageView(ImageView imageView, BKActivityTypes bkActivityTypes, BKActivityImageViewType bkActivityImageViewType) {
            switch (bkActivityTypes) {
                case ACTIVITY_SURVEY:
                    switch (bkActivityImageViewType) {
                        case BKACTIVITY_LOGO:
//                            imageView.setBackgroundResource("@image");
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void setCheckBoxRadioSelectorResource(BKUICheckBox bkuiCheckBox, BKActivityTypes bkActivityTypes, boolean b) {
            /*switch (bkActivityTypes){
                case ACTIVITY_SURVEY:
                    if (ischeckBox) {
                        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
                        bkuiCheckBox.setSelectedCheckBox(icon);
                    } else {
                        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
                        bkuiCheckBox.setSelectedCheckBox(icon);

                    }
                    break;
            }*/
        }

        @Override
        public void setRatingSelectorResource(BKUIRating bkuiRating, BKActivityTypes bkActivityTypes, BKActivityRatingTypes bkActivityRatingTypes) {
        }

        @Override
        public void setPreferencesForUIColor(BKBGColors bkbgColors, BKActivityTypes bkActivityTypes, BKActivityColorTypes bkActivityColorTypes) {
        }
    };

    public static void getRating(Context context, String ratingTag) {
        BrandKinesis brandKinesis = BrandKinesis.getBKInstance();
        if (brandKinesis == null) {
            return;
        }
        brandKinesis.getActivity(context, BKActivityTypes.ACTIVITY_RATINGS, ratingTag, new BKActivityCallback() {

            @Override
            public void onActivityError(int error) {
                Log.e("TAG", "Rating activity error occurred: " + error);
            }

            @Override
            public void onActivityCreated() {
                Log.i("TAG", "Rating activity created");
            }

            @Override
            public void onActivityDestroyed() {
                Log.i("TAG", "Rating activity destroyed");
            }

            @Override
            public boolean repeatUserSkippedActivity(BKActivityTypes activityType) {
                return false;
            }

            @Override
            public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes activityType, Map<String, Object> actionData) {
            }

            @Override
            public boolean brandKinesisShouldRedirectForActivityType(BKActivityTypes activityType) {
                return true;
            }

            @Override
            public boolean brandkinesisShouldEnableTapGusture(BKActivityTypes bkActivityTypes) {
                return false;
            }
        });
    }

    public static void getOpinionPoll(Context context, String opinionPollTag) {
        BrandKinesis brandKinesis = BrandKinesis.getBKInstance();
        if (brandKinesis == null) {
            return;
        }
        brandKinesis.getActivity(context, BKActivityTypes.ACTIVITY_OPINION_POLL, opinionPollTag, new BKActivityCallback() {
            @Override
            public void onActivityError(int error) {
                Log.e("TAG", "Opinion Poll activity error occurred: " + error);
            }

            @Override
            public void onActivityCreated() {
                Log.i("TAG", "Opinion poll activity created");
            }

            @Override
            public void onActivityDestroyed() {
                Log.i("TAG", "Opinion poll activity destroyed");
            }

            @Override
            public boolean repeatUserSkippedActivity(BKActivityTypes activityType) {
                return false;
            }

            @Override
            public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes activityType, Map<String, Object> actionData) {
            }

            @Override
            public boolean brandKinesisShouldRedirectForActivityType(BKActivityTypes activityType) {
                return true;
            }

            @Override
            public boolean brandkinesisShouldEnableTapGusture(BKActivityTypes bkActivityTypes) {
                return false;
            }
        });
    }

    public static void getInApp(Context context, BrandKinesis bkInstance, String inAppTag) {

        Log.d("getInApp", "tag : " + inAppTag + ", bkinstance : " + bkInstance);
        BrandKinesis brandKinesis = BrandKinesis.getBKInstance();
        if (brandKinesis == null) {
            return;
        }
        brandKinesis.getActivity(context, BKActivityTypes.ACTIVITY_IN_APP_MESSAGE, inAppTag, new BKActivityCallback() {
            @Override
            public void onActivityError(int error) {
                Log.e("TAG", "InApp activity error occurred: " + error);
            }

            @Override
            public void onActivityCreated() {
                Log.i("TAG", "InApp activity created");
            }

            @Override
            public void onActivityDestroyed() {
                Log.i("TAG", "InApp activity destroyed");
            }

            @Override
            public boolean repeatUserSkippedActivity(BKActivityTypes activityType) {
                return true;
            }

            @Override
            public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes activityType, Map<String, Object> actionData) {
                Log.d("TAG", "inapp data is" + actionData);
            }

            @Override
            public boolean brandKinesisShouldRedirectForActivityType(BKActivityTypes activityType) {
                return true;
            }

            @Override
            public boolean brandkinesisShouldEnableTapGusture(BKActivityTypes bkActivityTypes) {
                return false;
            }
        });
    }

    public static void getTutorial(Context context, BrandKinesis bkInstance, String tutorialTag) {

        BrandKinesis brandKinesis = BrandKinesis.getBKInstance();
        if (brandKinesis == null) {
            return;
        }
        brandKinesis.getActivity(context, BKActivityTypes.ACTIVITY_TUTORIAL, tutorialTag, new BKActivityCallback() {
            @Override
            public void onActivityError(int error) {
                Log.e("TAG", "Tutorial activity error occurred: " + error);
            }

            @Override
            public void onActivityCreated() {
                Log.i("TAG", "Tutorial activity created");
            }

            @Override
            public void onActivityDestroyed() {
                Log.i("TAG", "Tutorial activity destroyed");
            }

            @Override
            public boolean repeatUserSkippedActivity(BKActivityTypes bkActivityTypes) {
                return true;
            }

            @Override
            public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes bkActivityTypes, Map<String, Object> map) {

            }

            @Override
            public boolean brandKinesisShouldRedirectForActivityType(BKActivityTypes bkActivityTypes) {
                return false;
            }

            @Override
            public boolean brandkinesisShouldEnableTapGusture(BKActivityTypes bkActivityTypes) {
                return true;
            }
        });
    }

    public static String createPageEvent(String screenName) {
        HashMap<String, Object> pageData = new HashMap<>();
        pageData.put(BrandKinesis.BK_CURRENT_PAGE, screenName);
        String eventID = "";

        if (BrandKinesis.getBKInstance() != null) {
            eventID = BrandKinesis.getBKInstance().createEvent(BKProperties.BKEventType.BK_EVENT_PAGEVIEW,
                    BKProperties.BKEventSubType.BK_EVENT_PAGEVIEW_NATIVE.getValue(), pageData, true);
        }
        return eventID;
    }

    public static void closePageEvent(String eventId) {
        if (BrandKinesis.getBKInstance() != null) {
            BrandKinesis.getBKInstance().closeEvent(eventId);
        }
    }

    public static void createProfileCustomEvent(HashMap<String, Object> data) {
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if (bkInstance != null) {
            String id = bkInstance.createEvent(BKProperties.BKEventType.BK_EVENT_CUSTOM, UPSHOT_PROFILE_CUSTOM_EVENT_ID, data, false);
            Log.d("Upshot Manager", "Profile custom event created id : " + id);
        }
    }

    public static void createHealthRecordCustomEvent(HashMap<String, Object> data) {
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if (bkInstance != null) {
            String id = bkInstance.createEvent(BKProperties.BKEventType.BK_EVENT_CUSTOM, UPSHOT_HEAlTH_RECORD_CUSTOM_EVENT_ID, data, false);
            Log.d("Upshot Manager", "Health Record custom event created id : " + id);
        }
    }

    public static void createInsuranceCustomEvent(HashMap<String, Object> data) {
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if (bkInstance != null) {
            String id = bkInstance.createEvent(BKProperties.BKEventType.BK_EVENT_CUSTOM, UPSHOT_INSURANCE_CUSTOM_EVENT_ID, data, false);
            Log.d("Upshot Manager", "Insurance custom event created id : " + id);
        }
    }
    public static void createEmergencyContactsCustomEvent(HashMap<String, Object> data) {
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if (bkInstance != null) {
            String id = bkInstance.createEvent(BKProperties.BKEventType.BK_EVENT_CUSTOM, UPSHOT_EC_CUSTOM_EVENT_ID, data, false);
            Log.d("Upshot Manager", "Profile custom event created id : " + id);
        }
    }
    public static void createCareGiversCustomEvent(HashMap<String, Object> data) {
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if (bkInstance != null) {
            String id = bkInstance.createEvent(BKProperties.BKEventType.BK_EVENT_CUSTOM, UPSHOT_CG_CUSTOM_EVENT_ID, data, false);
            Log.d("Upshot Manager", "Profile custom event created id : " + id);
        }
    }

}
