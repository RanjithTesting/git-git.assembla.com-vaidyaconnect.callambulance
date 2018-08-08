package com.patientz.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.patientz.activity.R;

public class CustomProgressDialog extends ProgressDialog {
	
	String msg;
	Context mContext;

	public CustomProgressDialog(Activity context, String msg) {
		super(context);
		this.msg = msg;
		this.mContext=context;
		super.setIndeterminate(true);
		/*Drawable drawable = mContext.getResources().getDrawable(R.drawable.progress);
		super.setIndeterminateDrawable(drawable);*/
		super.setMessage(msg);
		super.setCancelable(false);
		}

}
