package com.patientz.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;

public class AppVolley {
	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;
	private static CookieHandler mCookieHandler;

	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		CookieManager cookieManager = new CookieManager();
		mCookieHandler = CookieHandler.getDefault();
		CookieHandler.setDefault(cookieManager);
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 8;
		mImageLoader = new ImageLoader(mRequestQueue, new ImageLruCache(
				cacheSize));
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static void renewSession() {
		mCookieHandler = null;
		mCookieHandler = CookieHandler.getDefault();
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}
}
