package com.gidi.bio_console.receiver;

import java.util.ArrayList;
import java.util.List;

import com.gidi.bio_console.listener.WifiReceiverActionListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/***
 * 接收wifi开关状态
 * 接收wifi连接的广播
 * @author 80657
 *
 */
public class WifiReceiver extends BroadcastReceiver {

	private  WifiReceiverActionListener wifiReceiverActionListener;
	private WifiManager mWifiManager;

	
	public WifiReceiver(WifiReceiverActionListener listener,WifiManager wifiManager){
		mWifiManager = wifiManager;
		this.wifiReceiverActionListener = listener;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(null == wifiReceiverActionListener){
			return;
		}
		if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
			//wifi开关变化通知
			handlerWifiState(intent);
			
		}else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
			
			handlerConnectState(intent);
			
		}else if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
			//wifi扫描结果通知
			
			handlerWifiScanResult();
		}
	}

	private void handlerWifiState(Intent intent){
		   int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
		   switch (wifiState) {
		   //wifi 已经打开
		   case WifiManager.WIFI_STATE_ENABLED:
			   if(null != wifiReceiverActionListener){
				   wifiReceiverActionListener.onWifiOpened();
			   }
			   break;
	       //wifi 正在打开
		   case WifiManager.WIFI_STATE_ENABLING:
			   if(null != wifiReceiverActionListener){
				   wifiReceiverActionListener.onWifiOpening();
			   }
			   break;
		   case WifiManager.WIFI_STATE_DISABLED:
			   if(null != wifiReceiverActionListener){
				   wifiReceiverActionListener.onWifiClosed();
			   }
			   break;
		   case WifiManager.WIFI_STATE_DISABLING:
			   if(null != wifiReceiverActionListener){
				   wifiReceiverActionListener.onWifiClosing();
			   }
			   break;
           case WifiManager.WIFI_STATE_UNKNOWN:
               //未知状态..
               break;
			default:
				break;
		}
	}
	
	/**处理wifi连接发生改变的状态**/
	private void handlerConnectState(Intent intent){
		NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);		 
		
		if(null != networkInfo && networkInfo.isConnected()){
            //连接上了,就把wifi的信息传出去
			
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (wifiInfo != null) {
                //把结果回传出去
            	wifiReceiverActionListener.onWifiConnected(wifiInfo,networkInfo);
            }
		}
	}
	
	private void  handlerWifiScanResult(){
		List<ScanResult> list = new ArrayList<ScanResult>();
		list.clear();
		list = getScanResult();
		Log.i("onWifiResultBack", "handlerWifiScanResult" + list.size());
		if(null != wifiReceiverActionListener){
			wifiReceiverActionListener.onWifiResultBack(list);
		}
	}
	

	private List<ScanResult> getScanResult(){
		if(null != mWifiManager){
			List<ScanResult> mList = new ArrayList<ScanResult>();
			mList.clear();
			mList = mWifiManager.getScanResults();//返回的队列是累计的
			if(null != mList){
				List<ScanResult> nlist = new ArrayList<ScanResult>();
				WifiInfo info = mWifiManager.getConnectionInfo();
				for(int i= 0; i< mList.size();i++){
					if(null != info && info.getSSID().equals(mList.get(i).BSSID)){
						continue;
					}
					//该热点是否已经在列表中
					int position = getItemPosition(nlist, mList.get(i));
					if(position != -1){
						//已在表内
					    if (nlist.get(position).level < mList.get(i).level) {
							nlist.remove(position);
						    nlist.add(position, mList.get(i));
					    }
					}else{
						//不在表内
						nlist.add(mList.get(i));
					}
				}
				return nlist;
			}
		}
		return null;  
	}
	
	
	private int getItemPosition(List<ScanResult>list, ScanResult item) {
	    for (int i = 0; i < list.size(); i++) {
		    if (item.SSID.equals(list.get(i).SSID)) {
			    return i;
		    }
	    }
	    return -1;
	}
	

}
