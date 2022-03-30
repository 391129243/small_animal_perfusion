package com.gidi.bio_console.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.adapter.WifiConnectAdapter;
import com.gidi.bio_console.base.BaseActivity;
import com.gidi.bio_console.bean.MScanWifi;
import com.gidi.bio_console.listener.WifiReceiverActionListener;
import com.gidi.bio_console.receiver.WifiReceiver;
import com.gidi.bio_console.view.WifiNoticeDialog;

/**1、显示wifi列表
 * **/
public class WlanConnectActivity extends BaseActivity {

	private static String TAG = "WlanConnectActivity";
	private Context mContext;
	private CheckBox openWlanCk;
	private TextView mWifiStatusTxt;
	private TextView conStatusTxt;
	private TextView seachingTxt;
	private ListView mWifiListView;
	private ImageView backImg;
	private WifiConnectAdapter wifiConnectAdapter;
	private WifiManager wifiManager;
	private WifiInfo mWifiInfo;
	private ConnectivityManager connectivityManager;
	private WifiReceiver wifiReceiver;
	private WifiReceiverActionListener wifiReceiverActionListener;
	private List<ScanResult> scanResultList;//网络连接列表
	private List<MScanWifi> mWifiScanList;
	private WifiNoticeDialog connectDialog;
	private WifiNoticeDialog forgetDialog;
	private WifiNoticeDialog reconnectDialog;
	private UIHandler mUIHandler = null;
	private ConnectWifiAsyncTask connectWifiAsyncTask;
	
	
	private static final int MSG_WIFI_CONNECT_STATE = 601;
	private static final int MSG_WIFI_SCANRESULT = 600;
	private static final int MSG_WIFI_TURN_OFF = 602;
	private static final int MSG_UPDATE_WIFILIST = 603;
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_wlanconnect;
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		openWlanCk = (CheckBox)findViewById(R.id.wifi_control_checkbox);
		mWifiStatusTxt = (TextView)findViewById(R.id.wlan_txt);
		conStatusTxt = (TextView)findViewById(R.id.connect_status_txt);
		mWifiListView = (ListView)findViewById(R.id.wifi_listview);
		seachingTxt = (TextView)findViewById(R.id.wifi_seaching_txt);
		backImg = (ImageView)findViewById(R.id.title_back_img);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) getApplicationContext()  
		          .getSystemService(Context.CONNECTIVITY_SERVICE);
		openWlanCk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					openWifi();
					startScan();
				}else{
					closeWifi();
				}
			}
		});
		
		mWifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				MScanWifi mScanWifi= mWifiScanList.get(position);
				ScanResult scanResult = mScanWifi.getScanResult();
				WifiConfiguration config = isExits(scanResult.SSID);
				if(config == null){
					//伟配置，连接过
					//有密码和无密码
					if(SecurityMode.OPEN.equals(getSecurityMode(scanResult))){
						//无密码
						Log.i(TAG, "----connect not password----");
						connecStatus(true);
						WifiConfiguration wifiConfiguration = createWifiConfiguration(scanResult.SSID, "", SecurityMode.OPEN);
						connectWifiByPwd(wifiConfiguration);
					}else{
						//有密码
						Log.i(TAG, "----connect by password----");
						showConnectWifiDialog(scanResult);
					}
				}else{
					//1配置，连接的要disconenct
					Log.i(TAG, "----33333--getCurrentWifiSSID()--" + getCurrentWifiSSID());
					if(isGivenWifiConnected(scanResult.SSID)){
						showForgetWifiDialog(mScanWifi);
					}else{
						//2配置过，但没连接则重新连接
						showReconnectDialog(scanResult,config);
					}					
				}
			}
		});
		backImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mContext = this;
		wifiConnectAdapter = new WifiConnectAdapter(getApplicationContext());
		mWifiListView.setAdapter(wifiConnectAdapter);
		scanResultList = new ArrayList<ScanResult>();
		mWifiScanList = new ArrayList<MScanWifi>();
		mUIHandler = new UIHandler(this);
		initWifiStatus();
		registerReceiver();
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext = null;
		if(null != scanResultList){
			scanResultList.clear();
			scanResultList = null;
		}
		
		if(null != mWifiScanList){
			mWifiScanList.clear();
			mWifiScanList = null;
		}
		
		
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);	
			mUIHandler = null;
		}

		cancelDialog();
		unRegisterReceiver();
	}
	
	
	
	private void registerReceiver(){
		wifiReceiverActionListener = new WifiReceiverActionListener() {
			//搜索返回wifi列表
			@Override
			public void onWifiResultBack(List<ScanResult> list) {
				// TODO Auto-generated method stub
				/***已经连接的wifi**/
				if(list.size() > 0){
					seachingTxt.setVisibility(View.GONE);
					List<ScanResult> tmpList = list;
					if(null != scanResultList){
						scanResultList.clear();
						for(ScanResult tmp : tmpList){
							if(isGivenWifiConnected(tmp.SSID) && !scanResultList.contains(tmp.SSID)){
								//已经连接的
								scanResultList.add(tmp);
							}
							if(isGivenWifiSaved(tmp.SSID)&& !scanResultList.contains(tmp.SSID)){
								scanResultList.add(tmp);
							}
							
						}
						
						for (ScanResult tmp : scanResultList) {
							//Log.i("WlanConnectActivity", "tmp---" + tmp);
							tmpList.remove(tmp);
						}						
						scanResultList.addAll(tmpList);
					}	
					
					mWifiScanList.clear();
					for (ScanResult scanResult : scanResultList) {
						 
						int wifiLevel = wifiManager.calculateSignalLevel(scanResult.level, 4); 
						String securityMode = getSecurityModeString(scanResult);
						MScanWifi mScanWifi = new MScanWifi(scanResult,securityMode, wifiLevel);
						mWifiScanList.add(mScanWifi);
					}
					mUIHandler.sendEmptyMessage(MSG_WIFI_SCANRESULT);
				}			
				
			}
			
			@Override
			public void onWifiOpening() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onWifiOpened() {
				// TODO Auto-generated method stub
				mWifiStatusTxt.setText(R.string.wlan_open);
			}
			
			@Override
			public void onWifiConnected(WifiInfo wifiInfo,NetworkInfo networkInfo) {
				// TODO Auto-generated method stub
				mWifiInfo  = wifiInfo;
				if(null != mUIHandler){
					Message msg = Message.obtain();
					msg.obj = networkInfo;
					msg.what = MSG_WIFI_CONNECT_STATE;
					mUIHandler.sendMessage(msg);
				}
			}
			
			@Override
			public void onWifiClosing() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onWifiClosed() {
				// TODO Auto-generated method stub
				Log.i(TAG, "WifiReceiver----onWifiClosed");
				clearWifi();
				mWifiStatusTxt.setText(R.string.wlan_close);
				scanResultList.clear();
				mWifiScanList.clear();
				mUIHandler.sendEmptyMessage(MSG_WIFI_TURN_OFF);
			}
		};
		wifiReceiver = new WifiReceiver(wifiReceiverActionListener ,wifiManager);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mContext.registerReceiver(wifiReceiver, intentFilter);
	}

	private void unRegisterReceiver(){
		if(null != wifiReceiver){
			unregisterReceiver(wifiReceiver);
			wifiReceiver = null;
		}
	}
	
	private void cancelDialog(){
		if(null != connectDialog){
			connectDialog.dismiss();
			connectDialog = null;
		}		
		if(null != forgetDialog){
			forgetDialog.dismiss();
			forgetDialog = null;
		}
		
		if(null != reconnectDialog){
			reconnectDialog.dismiss();
			reconnectDialog = null;
		}
	}
	
	private void initWifiStatus(){
		if(null!= wifiManager){
			if(wifiManager.isWifiEnabled()){
				openWlanCk.setChecked(true);
			}else{
				openWlanCk.setChecked(false);
			}
		}
	}
	
	private void connecStatus(boolean flag){
		if(flag){
			conStatusTxt.setText(R.string.wlan_connecting);
			conStatusTxt.setVisibility(View.VISIBLE);
		}else{
			conStatusTxt.setVisibility(View.INVISIBLE);
		}
	}
	
	
	/***
	 * 1、从扫描结果中将已经连接的wifi添加到wifi列表中
	 * 2、从所有WifiList中将已经添加过的连接wifi移除
	 * 3、将剩余的wifi添加到wifilist中
	 * 
	 * @return
	 */
	
/**************************************************wifi 操作**********************************************************/	
	/**
	 * 开启wifi功能
	 */
	private void openWifi(){
		if(null != wifiManager){						
			if(!wifiManager.isWifiEnabled()){
				wifiManager.setWifiEnabled(true);
			}												
		}
	}

	/**
	 * 关闭wifi功能
	 */
	private void closeWifi(){
		if(null != wifiManager){						
			if(!wifiManager.isWifiEnabled()){
				wifiManager.setWifiEnabled(false);
			}												
		}
	}

	/***
	 * open start scan wifi
	 * @return
	 */
	private boolean startScan(){
		if(null != wifiManager){
			return wifiManager.startScan();
		}
		return false;
	}
	
	/****
	 * close wifi
	 */
	private void clearWifi(){
		if(null != scanResultList){
			scanResultList.clear();
			wifiConnectAdapter.setWifiList(mWifiScanList);
			wifiConnectAdapter.notifyDataSetChanged();
		}
	}
	

	
	/****
	 * 新开线程是使用wifi配置文件连接wifi
	 */
	private void connectWifiByPwd(WifiConfiguration wifiConfiguration){
		Log.i(TAG, "connectWifiByPwd");
		if(null == connectWifiAsyncTask){
			connectWifiAsyncTask = new ConnectWifiAsyncTask(wifiConfiguration);
			connectWifiAsyncTask.execute();
		}
	}

	/**断开WiFi连接，当连接过的网络点击forget时
	 * @param networkId
	 * @return 是否断开
	 * **/
	public boolean disconnectWifi(int networkId){
		Log.i(TAG, "disconnectWifi--netId---" + networkId);
		boolean isDisable = wifiManager.disableNetwork(networkId);
		boolean isDisconnect = wifiManager.disconnect();
		return isDisable && isDisconnect;
	}
	
	/**忘记连接的账号和密码***/
	private boolean forgetWifi(String scanresult_ssid){	
		boolean isForget = false;
		List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            Log.i(TAG, "removeWifiBySsid ssid=" + ssid);
            if (ssid.equals("\"" + scanresult_ssid + "\"")) {
                Log.d(TAG, "removeWifiBySsid success, SSID = " + wifiConfig.SSID + " netId = " + String.valueOf(wifiConfig.networkId));
                wifiManager.removeNetwork(wifiConfig.networkId);
                isForget = wifiManager.saveConfiguration();
            }
        }
        return isForget;
	}
	
	
	/***
	 * 判断系统是否保存当前wifi的信息
	 * @param SSID
	 * @return
	 */
	private WifiConfiguration isExits(String SSID){
		//得到配置好的网络
		List<WifiConfiguration> existConfigs = wifiManager.getConfiguredNetworks();
		for(WifiConfiguration wifiConfiguration : existConfigs){
			if(wifiConfiguration.SSID.equals("\"" + SSID + "\"")){
				Log.i(TAG, "wifiConfiguration.SSID---" + " " + wifiConfiguration.SSID);
				return wifiConfiguration;
			}
		}
		return null;
	} 
	
	
	/**
	 * 判断当前是否已经连接
	 * @param ssid
	 * @return
	 */
	private boolean isGivenWifiConnected(String ssid){
		return isWifiConnected() && getCurrentWifiSSID().equals(ssid);
		
	}
	
	private boolean isGivenWifiSaved(String ssid){
		List<WifiConfiguration> existConfigs = wifiManager.getConfiguredNetworks();
		for(WifiConfiguration wifiConfiguration : existConfigs){
			if(getCurrentWifiSSID(wifiConfiguration.SSID).equals(ssid) && wifiConfiguration.status != 0){
				return true;
			}
		}
		return false;
	}
	
	private String getCurrentWifiSSID(String configurationSSID){
		String ssid = "";
        if (configurationSSID.substring(0, 1).equals("\"")
                && configurationSSID.substring(configurationSSID.length() - 1).equals("\"")) {
            ssid = configurationSSID.substring(1, configurationSSID.length() - 1);
        }
     
        return ssid;
	}
	
	/***
	 * 判断saved的值
	 * @return
	 */
	
	
	//得到当前连接的wifi ssid
	private String getCurrentWifiSSID(){
		String ssid = "";
		ssid = wifiManager.getConnectionInfo().getSSID();
        if (ssid.substring(0, 1).equals("\"")
                && ssid.substring(ssid.length() - 1).equals("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
	}
	
	/**判断当前网络是否处于WiFi连接状态**/
	public boolean isWifiConnected(){
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(wifiNetworkInfo.isConnected()){
			return true;
		}else if(wifiNetworkInfo.isAvailable()){
			return true;
		}
		return false;
	}
	
	/**获取当前的netid用于断开wifi**/
	public int getNetworkId(){
		int networkId = wifiManager.getConnectionInfo().getNetworkId();
		return networkId;
	}
		
	private String getSecurityModeString(ScanResult scanResult){
		if (scanResult.capabilities.contains("WEP")) {
			return "WEP";
		} else if (scanResult.capabilities.contains("WPA-PSK") && !scanResult.capabilities.contains("WPA2-PSK")) {
			return "WPA-PSK";
		} else if (!scanResult.capabilities.contains("WPA-PSK") && scanResult.capabilities.contains("WPA2-PSK")) {
			return "WPA2-PSK";
		} else if(scanResult.capabilities.contains("WPA-PSK") && scanResult.capabilities.contains("WPA2-PSK")){
			return "WPA/WPA2-PSK";
		} else if(scanResult.capabilities.contains("EAP")){
			return "WPA_EAP";
		}
		return "None";	
	}
	
	
	private SecurityMode getSecurityMode(ScanResult scanResult){
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
	   /**
     * 生成wifi配置文件
     * @param SSID:wifi账号
     * @param pwd 输入的wifi密码
     * @param securityMode 加密模式
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
/**********************************************************************************************************/
	
    /**弹出连接WiFi的dialog**/
	private void showConnectWifiDialog(final ScanResult scanResult){
		if(null != connectDialog){
			connectDialog.dismiss();
			connectDialog = null;
		}
		if(null == connectDialog){
			connectDialog = new WifiNoticeDialog(this);			
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_connect_wifi, null);
			TextView ssidTv = (TextView)view.findViewById(R.id.dialog_wifi_ssid_tv);
			final String ssid = scanResult.SSID;
			final EditText mPwdEt = (EditText)view.findViewById(R.id.dialog_wifi_pwd_et);
			CheckBox pwdVisibleCk = (CheckBox)view.findViewById(R.id.dialog_show_pwd_ck);
			WifiNoticeDialog.Builder builder = new WifiNoticeDialog.Builder(this);
			builder.setContentView(view);
			ssidTv.setText(ssid);
			pwdVisibleCk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
					// TODO Auto-generated method stub
					if(isCheck){
						//显示
						mPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						mPwdEt.setSelection(mPwdEt.getText().toString().length());
					}else{
						mPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
						mPwdEt.setSelection(mPwdEt.getText().toString().length());
					}
				}
			});
			
			builder.setConnectButton(R.string.dialog_connect, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					String pwd = mPwdEt.getText().toString().trim();
					if(pwd.equals("")){
						displayToast(R.string.toast_wifi_null);
						return; 
					}
					if(pwd.length() < 8){
						displayToast(R.string.toast_wifi_length);
						return; 
					}
					SecurityMode mode = getSecurityMode(scanResult);
					Log.i(TAG, "--mode---" + mode);
					//如果其他的在连接应该断开连接
					WifiConfiguration wifiConfiguration = createWifiConfiguration(ssid, pwd, mode);
					connectWifiByPwd(wifiConfiguration);
					connecStatus(true);
					connectDialog.dismiss();					
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					connectDialog.dismiss();
					connectDialog = null;
				}
			});
			connectDialog = builder.create();
			connectDialog.show();
		}
	}

	
    /**断开WiFi的dialog
     * 正在连接的有forget 和 cancel状态
     * **/
	private void showForgetWifiDialog(final MScanWifi scanWifi){
		if(null != forgetDialog){
			forgetDialog.dismiss();
			forgetDialog = null;
		}
		
		if(null == forgetDialog){
			forgetDialog = new WifiNoticeDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_message_disconnect_wifi, null);
			TextView ssidTv = (TextView)view.findViewById(R.id.dialog_wifi_ssid_tv);
			TextView statusTv = (TextView)view.findViewById(R.id.dialog_wifi_status_txt);
			TextView signalTv = (TextView)view.findViewById(R.id.dialog_wifi_signal_strength_txt);
			TextView securityTv = (TextView)view.findViewById(R.id.dialog_wifi_security_txt);			
			WifiNoticeDialog.Builder builder = new WifiNoticeDialog.Builder(this);
			builder.setContentView(view);
			ssidTv.setText(scanWifi.getScanResult().SSID);
			statusTv.setText(mContext.getString(R.string.wlan_connect));
			signalTv.setText(scanWifi.getWifiLevel()+"");			
			securityTv.setText(scanWifi.getSecurityMode() + "");
			builder.setForgetButton(R.string.dialog_forget, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					//	断开当前wifi
					int networkId = getNetworkId();
					boolean isForget = forgetWifi(scanWifi.getScanResult().SSID);
					if(isForget){
						mUIHandler.sendEmptyMessage(MSG_UPDATE_WIFILIST);
					}
					forgetDialog.dismiss();
				}
			});
			
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					forgetDialog.dismiss();
					forgetDialog = null;
				}
			});
			
			forgetDialog = builder.create();
			forgetDialog.show();
		}
	}
	
	/****
	 * 已经连接过的只有forget、connect、cancel状态
	 */
	private void showReconnectDialog(final ScanResult scanResult,final WifiConfiguration wifiConfiguration){
		if(null != reconnectDialog){
			reconnectDialog.dismiss();
			reconnectDialog = null;
		}
		if(null == reconnectDialog){
			reconnectDialog = new WifiNoticeDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_message_reconnect_wifi, null);
			TextView ssidTv = (TextView)view.findViewById(R.id.dialog_wifi_ssid_tv);
			WifiNoticeDialog.Builder builder = new WifiNoticeDialog.Builder(this);
			builder.setContentView(view);
			ssidTv.setText(scanResult.SSID);
			builder.setForgetButton(R.string.dialog_forget, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					//	断开当前wifi
					//int networkId = getNetworkId();
					Log.i(TAG, "showReconnectDialog--forget---scanResult.SSID--" + scanResult.SSID);
					boolean isForget = forgetWifi(scanResult.SSID);
					if(isForget){
						mUIHandler.sendEmptyMessage(MSG_UPDATE_WIFILIST);
					}
					reconnectDialog.dismiss();
				}
			});
			
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					reconnectDialog.dismiss();
					reconnectDialog = null;
				}
			});
			builder.setConnectButton(R.string.dialog_connect, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					connecStatus(true);
					connectWifiByPwd(wifiConfiguration);
					reconnectDialog.dismiss();
				}
			});
			
			reconnectDialog = builder.create();
			reconnectDialog.show();
		}
	}
	
	

	
/*************************************************************************************************************/	
	class ConnectWifiAsyncTask  extends AsyncTask<Void,Void, Boolean>{

		private WifiConfiguration mWifiConfiguration;
		public ConnectWifiAsyncTask(WifiConfiguration wifiConfiguration){
			this.mWifiConfiguration = wifiConfiguration;
		}
		
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub		
			boolean result = false; 
			int networkId = wifiManager.addNetwork(mWifiConfiguration);
			result = wifiManager.enableNetwork(networkId, true);
			Log.i(TAG, "doInBackground Wifi--netId---" + networkId);
			return result;
		}		
		
		@Override
		protected void onPostExecute(Boolean Boolean) {
			// TODO Auto-generated method stub
			 super.onPostExecute(Boolean);
			 if(null != connectWifiAsyncTask){
					connectWifiAsyncTask.cancel(true);
					connectWifiAsyncTask = null;
				}
		}

	}
	
	public static class UIHandler extends Handler{
		
		WeakReference<WlanConnectActivity> mActivityReference;
		
		public UIHandler(WlanConnectActivity mActivity){
			mActivityReference = new WeakReference<WlanConnectActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final WlanConnectActivity mActivity = mActivityReference.get();
			if(null == mActivity){
				return;
			}
			switch (msg.what) {
			case MSG_WIFI_SCANRESULT:			
				mActivity.wifiConnectAdapter.setWifiList(mActivity.mWifiScanList);
				mActivity.wifiConnectAdapter.notifyDataSetChanged();
				break;
				
			case MSG_WIFI_CONNECT_STATE:
				
				NetworkInfo networkInfo = (NetworkInfo)msg.obj;				
				if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
					mActivity.connecStatus(false);
				}else if(networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)){
					Log.i(TAG, "UIHandler---disconnected" + networkInfo.getState());
				}
				mActivity.wifiConnectAdapter.setWifiList(mActivity.mWifiScanList);
				mActivity.wifiConnectAdapter.notifyDataSetChanged();
				break;
				
			case MSG_UPDATE_WIFILIST:
				mActivity.startScan();
				break;
				
			case MSG_WIFI_TURN_OFF:
				mActivity.wifiConnectAdapter.emptyWifiList();
				mActivity.seachingTxt.setText(R.string.wlan_turn_off_status_hint);
				mActivity.seachingTxt.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
		
		
	}
}
