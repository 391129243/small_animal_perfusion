package com.gidi.bio_console.base;

import java.util.Locale;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.serialport.SerialPortInstruct;
import com.gidi.bio_console.service.BackService;
import com.gidi.bio_console.service.BackService.BackBinder;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.utils.ToastUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

public abstract class BaseActivity extends Activity {

	private static final String TAG = "BaseActivity";
	
	private BioConsoleApplication mApplication;
	
	private RemoteCtrlReceiver remoteCtrlReceiver;
	
	private BackBinder mBinder;
		
	protected abstract int getLayoutId();
	//抽象函数用于继承
	protected abstract void initViews(Bundle savedInstanceState);	
	
    protected abstract void initListener();
    
	protected abstract void initData();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		mApplication = (BioConsoleApplication)getApplication();
		switchLanguage(PreferenceUtil.getInstance(getApplicationContext())
				.getStringValue(SharedConstants.LANGUAGE, "zh"));
		bindService();
		registerReciver();
		initViews(savedInstanceState);
		initListener();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService();
		unregisterReceiver();
	}


	protected void switchLanguage(String language) {
		Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
       if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
        	 config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, dm);
        PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LANGUAGE, language);
    }
	
	/**
	 * Shielding back key
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            return true;  
        }  
        return false;  
    }  
	

	private void registerReciver(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_SPEED);
		intentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_TEMP);
		intentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_ARTERY_SPEED);
		intentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_VEIN_SPEED);
		remoteCtrlReceiver = new RemoteCtrlReceiver();
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(remoteCtrlReceiver, intentFilter);		
	}
	
	private void unregisterReceiver(){
		if(null != remoteCtrlReceiver){
			LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(remoteCtrlReceiver);
			remoteCtrlReceiver = null;			
		}		
	}
	
	private void bindService(){
		Intent service = new Intent(BaseActivity.this, BackService.class);
		bindService(service, connection, BIND_AUTO_CREATE);
	}
	
	private void unbindService(){
		unbindService(connection);
	}
	
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBinder = (BackBinder)service;
		
		}
	};
	
	private class RemoteCtrlReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(BroadcastActions.RECEIVE_REMOTE_CTRL_SPEED)){
				int sysflag = intent.getIntExtra("sysflag", 1);
				Log.i("RemoteCtrl", "base sysflag----" + sysflag);
				if(sysflag == 0){
					if(mApplication.isStopPump()){
						sendRestartSysMsg();
					}
					
				}else{
					if(!mApplication.isStopPump()){
						sendStopSysMsg();
					}
					
				}
			}else if(action.equals(BroadcastActions.RECEIVE_REMOTE_CTRL_TEMP)){
					
				
			}else if(action.equals(BroadcastActions.RECEIVE_REMOTE_CTRL_ARTERY_SPEED)){
				if(mApplication.isStopPump()){
					int artery_tarSpeed  =  intent.getIntExtra("arterySpeedflag", 0);
					//并保存
					if(artery_tarSpeed >= 0 && artery_tarSpeed <=5000){
						PreferenceUtil.getInstance(getApplicationContext())
							.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, artery_tarSpeed);
						Log.i(TAG, "base sendArteryTarSpeedMsg----" + artery_tarSpeed);
						sendArteryTarSpeedMsg(artery_tarSpeed);
					}
					
					
				}

			}else if(action.equals(BroadcastActions.RECEIVE_REMOTE_CTRL_VEIN_SPEED)){
				if(mApplication.isStopPump()){
					int vein_tarSpeed  =  intent.getIntExtra("veinSpeedflag", 0);
					if(vein_tarSpeed >= 0 && vein_tarSpeed <= 5000){
						PreferenceUtil.getInstance(getApplicationContext())
							.setValueByName(SharedConstants.TARGET_VEIN_SPEED, vein_tarSpeed);
						Log.i(TAG, "base sendArteryTarSpeedMsg----" + vein_tarSpeed);
						sendVeinTarSpeedMsg(vein_tarSpeed);
					}
					
				}
			}
		}
		
	}
	
	/**发送停止泵转动的消息**/
	private void sendStopSysMsg(){
		if(null != mBinder){
			mBinder.sendSerialCmdMsg(SerialPortInstruct.STOP_SYSTEM_ONE);
			mBinder.sendSerialCmdMsg(SerialPortInstruct.STOP_SYSTEM_TWO);
		}
		mApplication.setStopPump(true);
	}

	/**发送重启泵转动的消息**/
	private void sendRestartSysMsg(){
		Log.i(TAG, "sendRestartSysMsg");
		float mArteryPreTarget =  PreferenceUtil.getInstance(getApplicationContext()).getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
		float mVeinPreTarget =  PreferenceUtil.getInstance(getApplicationContext()).getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
		if(null != mBinder){
			String target_pre1_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_ONE, (int)(mArteryPreTarget*10),"1");
			String target_pre2_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_TWO, (int)(mVeinPreTarget*10),"1");
			mBinder.sendSerialCmdMsg(target_pre1_msg);
			mBinder.sendSerialCmdMsg(target_pre2_msg);
		}
		mApplication.setStopPump(true);
	}
	
	/**set the pump target pump speed**/
	private void sendArteryTarSpeedMsg(int arterySpeed){
		Log.i(TAG, "sendArteryTarSpeedMsg" + arterySpeed);
		if(null != mBinder){
			String artery_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_ONE, arterySpeed,"0");			
			mBinder.sendSerialCmdMsg(artery_speed_msg);
		}
	}
	
	/**set the pump target pump speed**/
	private void sendVeinTarSpeedMsg(int veinSpeed){
		Log.i(TAG, "sendVeinTarSpeedMsg" + veinSpeed);
		if(null != mBinder){			
			String vein_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_TWO, veinSpeed,"0");			
			mBinder.sendSerialCmdMsg(vein_speed_msg);
		}
	}


    public void displayToast(int resId){
        ToastUtils.showToast(getApplicationContext(),getResources().getString(resId));
    }

    public void displayToast(String message){
        ToastUtils.showToast(getApplicationContext(),message);
    }
}
