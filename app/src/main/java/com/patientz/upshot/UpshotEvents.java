package com.patientz.upshot;

import com.brandkinesis.BKProperties;
import com.brandkinesis.BrandKinesis;

import java.util.HashMap;

/**
 * Created by sumit on 22/7/16.
 */
public class UpshotEvents {

    public static String createPageEvent(String screenName){
        HashMap<String, Object> pageData = new HashMap<>();
        pageData.put(BrandKinesis.BK_CURRENT_PAGE,screenName);
        String eventID = "";

        if (BrandKinesis.getBKInstance() != null)
        {
           eventID = BrandKinesis.getBKInstance().createEvent(BKProperties.BKEventType.BK_EVENT_PAGEVIEW,
                   BKProperties.BKEventSubType.BK_EVENT_PAGEVIEW_NATIVE.getValue(), pageData, true);
        }
    return eventID;
    }

    public static void closePageEvent(String eventId){
        if (BrandKinesis.getBKInstance() != null)
        {
            BrandKinesis.getBKInstance().closeEvent(eventId);
        }
    }

    public static void createCustomEvent(HashMap<String,Object> data,int subType){
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        if(bkInstance != null) {
            String id = bkInstance.createEvent(BKProperties.BKEventType.BK_EVENT_CUSTOM, subType, data, false);
        }
    }
}
