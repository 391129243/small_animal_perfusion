package com.gidi.bio_console.listener;

import java.util.List;

import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

/**收到wifi广播更新UI的接口**/
public interface WifiReceiverActionListener {

	/**wifi打开**/
	void onWifiOpened();
	
	/**wifi打开中**/
	void onWifiOpening();
	
	/**wifi关闭**/
	void onWifiClosed();
	
	/**wifi关闭中**/
	void onWifiClosing();
	
	/**连接上**/
	void onWifiConnected(WifiInfo wifiInfo,NetworkInfo networkInfo);
	
	/**扫描结果**/
	void onWifiResultBack(List<ScanResult> list);
}
