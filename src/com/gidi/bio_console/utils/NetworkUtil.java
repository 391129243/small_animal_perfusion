package com.gidi.bio_console.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gidi.bio_console.BioConsoleApplication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.widget.Toast;

public class NetworkUtil {

	/***
	 * 判断网络是否可用
	 * @return
	 */
	public static boolean isNetAvailable(){
		ConnectivityManager connectivityManager = (ConnectivityManager)BioConsoleApplication.getInstance()
								.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo == null){
			return false;
		}
		return networkInfo.isAvailable();
	}
	
	/***
	 * 判断网络是否连接
	 */
	public static boolean isNetConnected(){
		ConnectivityManager connectivityManager = (ConnectivityManager)BioConsoleApplication.getInstance()
								.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo!= null && networkInfo.isConnected()){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断当前wifi是否连接
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context){
		if(null != context){
			ConnectivityManager mConnectivityManager = (ConnectivityManager)context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if(null != mWifiNetworkInfo){
				if(mWifiNetworkInfo.isAvailable()){
					if(mWifiNetworkInfo.isConnected()){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断当前网络类型
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context){
		ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if(null != mNetworkInfo && mNetworkInfo.isAvailable()){
			return mNetworkInfo.getType();
		}
		return -1;
	}
	
	/***
	 * check the url invalid
	 * @param url
	 * @return
	 */
	public static boolean checkURL(String url){
		try {
			URL mUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection)mUrl.openConnection();
			connection.connect();
			if(connection.getResponseCode() == 200){
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static interface Type{
		String _WIFI = "WIFI";
		String _GPRS = "GPRS";
		String _CMWAP = "CMWAP";
	}
	
	/**
	 * Determine whether the network is connected
	 * @param context
	 * @return
	 */
	public static boolean isConnect(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getNetType(Context mContext){
	      ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
    	  State stategprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
    	  State statewifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
    	  if(State.CONNECTED==statewifi && State.CONNECTED==stategprs){
    		  return Type._WIFI;
    	  }
    	  if(State.CONNECTED!=stategprs  && State.CONNECTED==statewifi){
    		  return Type._WIFI;
    	  }
    	  if(State.CONNECTED!=statewifi  && State.CONNECTED==stategprs){
    		  Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
        	  try {
        		  ContentResolver cr = mContext.getContentResolver();
        	      Cursor cursor = cr.query(PREFERRED_APN_URI,
        	      new String[] { "_id", "apn", "type" }, null, null, null);
        	      cursor.moveToFirst();
        	      if (cursor.isAfterLast()) {
        	    	  return Type._GPRS;
        	      }
        	      String apn = cursor.getString(1);
        	      if (apn.toUpperCase().equals("CMWAP")){
        	    	  return Type._CMWAP;
        	      }else if (apn.toUpperCase().equals("CMNET")){
        	    	  return Type._GPRS;
        	      }else{
        	    	  return Type._GPRS;
        	      }
       	     } catch (Exception ep) {
       	    	 ep.printStackTrace();
       	     }
    	  }
    	  return Type._GPRS;
	}
	

	
	/**
	 * @param mContext
	 * @return
	 */
	public static boolean checkSoftStage(Context mContext){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
			return true;
		}else{
			Toast.makeText(mContext, "----", Toast.LENGTH_LONG).show();
			return false;
		}
	}
	


	public static String isNaN(String msg){
		Pattern pattern = Pattern.compile("^[+-]?\\d*[.]?\\d*$");  
		Matcher isNum = pattern.matcher(msg);  
		if(isNum.matches()){  
			return Double.parseDouble(msg)+"";
		}else{
			return "no";
		}
	}
	
	/**
	 * 获取本地ip地址
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();
        
        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
	
    public static SecurityMode getSecurityMode(ScanResult scanResult){
        if (scanResult.capabilities.contains("WEP")) {
            return SecurityMode.WEP;
        } else if (scanResult.capabilities.contains("PSK")) {
            return SecurityMode.WPA;//WPA/WPA2 PSK
        } else if (scanResult.capabilities.contains("EAP")) {
            return SecurityMode.WPA2;
        }
        return SecurityMode.OPEN;
    }
    /**
     * 这个枚举用于表示网络加密模式
     */
    public enum SecurityMode {
        OPEN, WEP, WPA, WPA2
    }

}
