package com.gidi.bio_console.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	private String FILE_NAME = "Bio_console_share";	
	private SharedPreferences mSp;
	private SharedPreferences.Editor mEditor;
	
	public PreferenceUtil(Context context){
		this.mSp =  context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);  
		this.mEditor = mSp.edit();
	}
	
	private static PreferenceUtil instance;
	
	public static PreferenceUtil getInstance(Context context){
		if(null == instance){
			instance = new PreferenceUtil(context);
		}
		return instance;
	}
	
	public void setValueByName(String name, String value){
		mEditor.putString(name, value);
		mEditor.commit();
	}
	
	public String getStringValue(String name, String value){
		return mSp.getString(name, value);
	}
	
	public void setValueByName(String name, int value){
		mEditor.putInt(name, value);
		mEditor.commit();
	}
	
	public int getIntValue(String name, int value){
		return mSp.getInt(name, value);
	}
	
	public void setValueByName(String name, boolean value){
		mEditor.putBoolean(name, value);
		mEditor.commit();
	}
	
	public boolean getBooleanValue(String name, boolean value){
		return mSp.getBoolean(name, value);
	}
	
	public void setValueByName(String name, long value){		
		mEditor.putLong(name, value);
		mEditor.commit();
	}
	
	public long getLongValue(String name, long  value){
		return mSp.getLong(name, value);
	}
	
	public void setValueByName(String name ,float value){
		mEditor.putFloat(name, value);
		mEditor.commit();
	}
	
	public float getFloatValue(String name ,float value){
		return mSp.getFloat(name, value);
	}
}
