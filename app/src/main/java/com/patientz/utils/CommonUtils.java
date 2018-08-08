package com.patientz.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.patientz.activity.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;

public class CommonUtils {

    public static Dialog showProgressDialogAVI(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        return dialog;
    }

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait.....");
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }//showProgressDialog().

    public static ProgressDialog showProgressDialogWithCustomMsg(Context context,String msg) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage(msg);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }//showProgressDialog().


    public static void dismissProgressDialog(Dialog pDialog) {


        if (pDialog != null) {
            pDialog.dismiss();
        }


    }


    public static SharedPreferences getSP(Context context) {

        return context.getSharedPreferences(Constant.EMT_SP, Context.MODE_PRIVATE);

    }

    public static String getTwoDigitDecimalValue(double doubleValue) {
        return new DecimalFormat("#.##").format(doubleValue);
    }

    public static HashMap<String, String> generateParamsForUpdateEmergencyStatusWithHosptial(LatLng mLatLng, String token, String hospitalID, int status) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Constant.TOKEN, token);
        params.put(Constant.LATITUDE, String.valueOf(mLatLng.latitude));
        params.put(Constant.LONGITUDE, String.valueOf(mLatLng.longitude));
//        params.put(ServiceURLs.RequestParamKeys.DISTANCE_IN_KMS,Constants.DEFAULT_RADIUS_IN_KMS);
        params.put(Constant.DISTANCE_IN_KMS, "5");
        params.put(Constant.HOSPITAL_ID, hospitalID);
        params.put(Constant.STATUS, String.valueOf(status));

        return params;

    }
    public static String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path,
                    DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap,
                    DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            // Store to tmp file

            File file = new File(Environment.getExternalStorageDirectory(), "image_" + String.valueOf(System.currentTimeMillis()) + ".png");

            strMyImagePath = file.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }

        return strMyImagePath;

    }
}//CommonUtils Class.
