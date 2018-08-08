package com.patientz.VO;

import java.util.List;

import android.graphics.drawable.Drawable;

public class LeftMenuGroupVO {
	
	Drawable icon;
	String title;
	List<LeftMenuChildVO> menuChildVOs;
	
	
	
	
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
	public List<LeftMenuChildVO> getMenuChildVOs() {
		return menuChildVOs;
	}
	public void setMenuChildVOs(List<LeftMenuChildVO> menuChildVOs) {
		this.menuChildVOs = menuChildVOs;
	}
	
	
	
	

}
