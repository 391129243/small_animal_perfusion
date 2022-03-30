package com.gidi.bio_console.utils;

import com.google.gson.Gson;

public class GsonUtil {

	private static Gson mInstance = null;
	public static Gson getInstance(){
		if(null == mInstance){
			mInstance = new Gson();
		}
		return mInstance;
	}
	
	//使用Gson解析为具体的对象
	public <T> T fromGson(String jsonString,Class<T> cls){
		T t = null;
        if (mInstance != null) {
            t = mInstance.fromJson(jsonString, cls);
        }
        return t;
	}
	
	//形成json
	public  String toGson(Object object){
		String gsonString = null;
        if (mInstance != null) {
            gsonString = mInstance.toJson(object);
        }
        return gsonString;
		
	}
}
