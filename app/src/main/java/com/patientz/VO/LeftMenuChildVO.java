package com.patientz.VO;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

public class LeftMenuChildVO {

	Drawable icon;
	String title;
	Fragment mFragment;
	boolean isEmergencyContactsAvailable;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Fragment getmFragment() {
		return mFragment;
	}

	public void setmFragment(Fragment mFragment) {
		this.mFragment = mFragment;
	}

	public boolean isEmergencyContactsAvailable() {
		return isEmergencyContactsAvailable;
	}

	public void setEmergencyContactsAvailable(
			boolean isEmergencyContactsAvailable) {
		this.isEmergencyContactsAvailable = isEmergencyContactsAvailable;
	}

}
