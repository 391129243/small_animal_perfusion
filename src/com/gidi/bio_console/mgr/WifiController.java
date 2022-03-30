package com.gidi.bio_console.mgr;


import java.util.List;

import com.gidi.bio_console.listener.OnWifiConnectListener;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/***
 * 负责wifi的扫描、连接、判断wifi是否可用
 * @author 80657
 *
 */
public class WifiController {
	private static final String TAG = "WifiController";	
	
	private WifiManager mWifiManager;
	private Context mContext;
	private OnWifiConnectListener onWifiConnectListener;
	
	private static WifiController mInstance = null;

	public WifiController(Context context){
		this.mContext = context;
		mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	
	public WifiController getInstance(Context context){
		if(null == mInstance){
			synchronized (WifiController.class) {
				if(null == mInstance){
					mInstance = new WifiController(context);
				}
			}
		}
		return mInstance;
	}

	/**scan all wifi**/
	public void scanWifi(){
		if(!isEnableWifi()){
			return;
		}
		mWifiManager.startScan();
	}
	
	/**open or close wifi 当wifi是打开时，调用则为关闭，wifi关闭时，调用则打开**/
	
	public void openCloseWifi(){
		/**判断当前wifi的状态**/
		if(isEnableWifi()){
			mWifiManager.setWifiEnabled(false);
		}else{
			mWifiManager.setWifiEnabled(true);
		}
	}


	/**判断wifi是否可用 true 表示wifi打开，false 表示 wifi 关闭**/
	
	private boolean isEnableWifi(){
		return mWifiManager.isWifiEnabled();
	}
	
	/**获取当前连接WiFi的信息**/
	public WifiInfo getConnectInfo(){
		try {
			return mWifiManager.getConnectionInfo();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	/**断开WiFi连接
	 * @param netId
	 * @return 是否断开
	 * **/
	public Boolean disconnectWifi(int netId){
		boolean isDisable = mWifiManager.disableNetwork(netId);
		boolean isDisconnect = mWifiManager.disconnect();
		return isDisable && isDisconnect;
	}
	
	public void closeWifi(){
		if(mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
		}
	}
	
	public List<ScanResult> getWifiScanResult(){
		return mWifiManager.getScanResults();
	}
	
	/**判断配置再系统中是否存在**/
	private WifiConfiguration isExits(WifiConfiguration config){
		List<WifiConfiguration> existConfigs = mWifiManager.getConfiguredNetworks();
		for(WifiConfiguration wificonfig :existConfigs){
			if(wificonfig.SSID.equals(config.SSID)){
				config.networkId = wificonfig.networkId;
				return config;
			}
		}
		return null;
	}
	
    /**
     * 获取NetworkId
     *
     * @param scanResult 扫描到的WIFI信息
     * @return 如果有配置信息则返回配置的networkId 如果没有配置过则返回-1
     */
	public int getNetworkIdFromConfig(ScanResult scanResult){
		String SSID = String.format("\"%s\"", scanResult.SSID);
		List<WifiConfiguration> existConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existConfigs) {
            if (existingConfig.SSID.equals(SSID)) {
                return existingConfig.networkId;
            }
        }
		return -1;
	}
	
	
	/****
	 * 通过密码连接wifi
	 */
	private void connectWifiByPwd( ScanResult scanResult,  String pwd, OnWifiConnectListener listener){
		String SSID = scanResult.SSID;		
		//加密方式
		SecurityMode  securityMode = getSecurityMode(scanResult);
		
		//生成配置文件
		WifiConfiguration wifiConfiguration = createWifiConfiguration(SSID, pwd, securityMode);			
	}
	
	/***
	 * 通过networkI的连接到wifi
	 * @param SSID :wifi名称
	 * @param listener 连接的监听器
	 */
	
	public void connectWifiByNetWorkId(String SSID,int networkId, OnWifiConnectListener listener){
		onWifiConnectListener = listener;
		//连接开始的回调
		onWifiConnectListener.onStart(SSID);
		//判断networkid是否有效 若是-1表示参数不正确
		if(-1 != networkId){
			if(null != onWifiConnectListener){
				onWifiConnectListener.onFailure(SSID);
				onWifiConnectListener.onFinish();
				onWifiConnectListener = null;
			}
			return;
		}
		
		//获取当前的网络连接
		WifiInfo wifiInfo = getConnectInfo();
		if(null != wifiInfo){
			//断开当前的连接
			boolean isDisconnect = disconnectWifi(wifiInfo.getNetworkId());
			if(!isDisconnect){
				//断开当前网络失败
				if(null != onWifiConnectListener){
					onWifiConnectListener.onFailure(SSID);
					onWifiConnectListener.onFinish();
					onWifiConnectListener = null;
				}
				return;
			}
		}
		
		//连接wifi
		boolean isEnable = mWifiManager.enableNetwork(networkId, true);
		if(!isEnable){
			//连接失败
			if(null != onWifiConnectListener){
				onWifiConnectListener.onFailure(SSID);
				onWifiConnectListener.onFinish();
				onWifiConnectListener = null;
			}
		}
		
	}
	 /**
     * 这个枚举用于表示网络加密模式
     */
    public enum SecurityMode {
        OPEN, WEP, WPA, WPA2
    }
    
    /**
     * 获取WiFi的加密方式
     */
    public SecurityMode getSecurityMode(ScanResult scanResult){
    	String capabilities =  scanResult.capabilities;
    	if(capabilities.contains("WPA")){
    		return SecurityMode.WPA;
    	} else if(capabilities.contains("WEP")){
    		return SecurityMode.WEP;
    	} else if(capabilities.contains("WPA2")){
    		return SecurityMode.WPA2;
    	} else{
        	return SecurityMode.OPEN;
    	}

    }
    
    /**
     * 生成wifi配置文件
     */
    private WifiConfiguration createWifiConfiguration(String SSID, String pwd, SecurityMode securityMode){
    	
    	WifiConfiguration wifiConfiguration = new WifiConfiguration();
    	//支持这个配置身份证协议
    	wifiConfiguration.allowedAuthAlgorithms.clear();
    	//组密码支持这个配置的设置
    	wifiConfiguration.allowedGroupCiphers.clear();
    	//支持组密码管理协议
    	wifiConfiguration.allowedKeyManagement.clear();
    	//一组两两对WPA密码呗该配置支持
    	wifiConfiguration.allowedPairwiseCiphers.clear();
    	//支持安全协议
    	wifiConfiguration.allowedProtocols.clear();
    	wifiConfiguration.SSID = "\"" + SSID + "\"";
    	if(securityMode == SecurityMode.OPEN){
    		wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

    	}else if(securityMode == SecurityMode.WEP){
    		wifiConfiguration.hiddenSSID = true;
    		wifiConfiguration.wepKeys[0] = "\"" + pwd + "\"";
    		wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
    		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
    		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
    		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
    		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
    		wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    		wifiConfiguration.wepTxKeyIndex = 0;
    	} else if(securityMode == SecurityMode.WPA){
    		wifiConfiguration.preSharedKey = "\"" + pwd + "\"";
    		wifiConfiguration.hiddenSSID = true;
    		wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
    		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
    		wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
    		wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
    		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
    		wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
    		wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
    	}   	
    	return wifiConfiguration;
    }
}
