package com.gidi.bio_console.listener;

/**wifi连接回调接口**/
public interface OnWifiConnectListener {

	void onStart(String SSID);
	
	void onFailure(String SSID);
	
	void onFinish();
}
