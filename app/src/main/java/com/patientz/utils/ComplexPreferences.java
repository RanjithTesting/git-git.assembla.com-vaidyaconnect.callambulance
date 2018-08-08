package com.patientz.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patientz.VO.RecordSchemaAttributes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComplexPreferences {
	private ComplexPreferences complexPreferences;
	private Context context;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private  Gson GSON = new Gson();
	Type typeOfObject = new TypeToken<ArrayList<RecordSchemaAttributes>>() {
	}.getType();

	public ComplexPreferences(Context context, String namePreferences, int mode) {
		this.context = context;
		if (namePreferences == null || namePreferences.equals("")) {
			namePreferences = "ComplexPreferences";
			Log.d("ComplexPreferences,ComplexPreferences-->namePreferences",
					namePreferences);
		}

		preferences = context.getSharedPreferences(namePreferences, mode);
		editor = preferences.edit();
	}

	public ComplexPreferences() {
		// TODO Auto-generated constructor stub
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public void putObject(String key, ArrayList<RecordSchemaAttributes> object) {
		if (object == null) {
			throw new IllegalArgumentException("Object is null");
		}
		if (key.equals("") || key == null) {
			throw new IllegalArgumentException("Key is empty or null");
		}
		editor.putString(key, GSON.toJson(object));
	}

	public void putKey(HashMap<String, String> schemaKeys) {
		for (Map.Entry entry : schemaKeys.entrySet()) {
			if (entry.getValue() != null && entry.getKey() != null) {
				editor.putString(entry.getKey().toString(), entry.getValue()
						.toString());
				Log.d("ComplexPreferences-putKey","key,val: " + entry.getKey() + ","
						+ entry.getValue());
			}
			editor.commit();
		}
	}
	public void putLongValue(String key,long value) {
		editor.putLong(key, value);
			editor.commit();
	}

	public void commit() {
		editor.commit();
	}

	public ArrayList<RecordSchemaAttributes> getObject(String key) {
		Log.d("ComplexPreference.java,getObject-->key", key);
		Log.d("ComplexPreference.java,getObject-->preferences", preferences
				+ "");
		String gson = preferences.getString(key, null);
		if (gson == null) {
			Log.d("ComplexPreference.java", "getObject :gson" + gson);

			return null;
		} else {
			try {
				return GSON.fromJson(gson, typeOfObject);
			} catch (Exception e) {
				throw new IllegalArgumentException("Object stored with key "
						+ key + " is instance of other class");
			}
		}
	}
	public boolean deletePreferences(String key)
	{
	    editor.remove(key).commit();
	    return false;
	}

	public long getLongValue(String key) {
		return preferences.getLong(key ,0);
	}
}
