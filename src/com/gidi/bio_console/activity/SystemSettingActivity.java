package com.gidi.bio_console.activity;


import java.lang.ref.WeakReference;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseActivity;
import com.gidi.bio_console.bean.DeviceStatusBean;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.kidney.KidneyPerfusionLogActivity;
import com.gidi.bio_console.listener.OnNetWorkChangedListener;
import com.gidi.bio_console.mgr.SystemAlarmMgr;
import com.gidi.bio_console.receiver.NetWorkChangeReceiver;
import com.gidi.bio_console.serialport.SerialPortInstruct;
import com.gidi.bio_console.service.BackService;
import com.gidi.bio_console.service.BackService.BackBinder;
import com.gidi.bio_console.utils.CommonUtil;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.NetworkUtil;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.utils.ToastUtils;
import com.gidi.bio_console.view.CustomBatteryView;
import com.gidi.bio_console.view.CustomDialog;
import com.gidi.bio_console.view.DateTimeView;
import com.gidi.bio_console.view.NoticeDialog;
import com.gidi.bio_console.view.SystemSettingItem;
import com.gidi.bio_console.view.SystemSettingItem.OnSystemSetItemClickListener;

public class SystemSettingActivity extends BaseActivity implements OnClickListener, OnNetWorkChangedListener{
	private static String TAG = "SystemSettingActivity";
	private Context mContext;
	private RelativeLayout mTitleLayout;
	private SystemSettingItem wifiSetItem;
	private SystemSettingItem languageSetItem;
	private SystemSettingItem timeSetItem;
	private SystemSettingItem exportSetItem;
	private SystemSettingItem disinfectionItem;//消毒模式
	private SystemSettingItem aboutUsItem;
	private ImageView mTitleImg;
	private ImageView wifiImg;

	private ImageView volumeImg;
	private CustomBatteryView batteryImg;
	private DateTimeView mDateTimeTxt;
	private ImageView emergencyStopImg;
	private TextView mInfoTxt;
	private UIHandler mUIHandler;
	private NetWorkChangeReceiver netWorkReceiver;
	private SerialMsgReceiver mSerialMsgReceiver;
	private SystemAlarmMgr mSystemAlarmMgr;
	private AudioManager mAudioManager;	
	private NoticeDialog emergencyStopDialog;
	private CustomDialog disinfectDialog;
	private BioConsoleApplication mApplication;
	private BackBinder mBinder;
	private int alarmVolume;
	private TextView mTitleTxt;
	private ImageView backImg;
    private static final int CHANGE_LANGUAGE_REQUEST_CODE = 1;	    
    //技术性报警
	private boolean isMute = false;
	/**电池异常**/
	private boolean isBatteryAbnormal = false;
	/**仅仅使用电池**/
	private boolean isUseBattery = false;
    
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_systemsetting;
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mTitleLayout = (RelativeLayout)findViewById(R.id.system_setting_title_layout);
		mTitleTxt = (TextView)mTitleLayout.findViewById(R.id.base_title_text);
		mTitleImg = (ImageView)mTitleLayout.findViewById(R.id.base_function_title_icon);
		mDateTimeTxt = (DateTimeView)mTitleLayout.findViewById(R.id.sys_time_ll);
		wifiImg = (ImageView)mTitleLayout.findViewById(R.id.base_title_wifi_img);
		
		volumeImg = (ImageView)mTitleLayout.findViewById(R.id.base_title_volume_img);
		batteryImg = (CustomBatteryView)mTitleLayout.findViewById(R.id.base_title_battery_img);
		emergencyStopImg = (ImageView)mTitleLayout.findViewById(R.id.base_title_emergency_stop_img);
		mInfoTxt = (TextView)mTitleLayout.findViewById(R.id.base_alert_text);
		wifiSetItem = (SystemSettingItem)findViewById(R.id.system_set_wifi);
		languageSetItem = (SystemSettingItem)findViewById(R.id.system_set_language);
		timeSetItem = (SystemSettingItem)findViewById(R.id.system_set_time);
		exportSetItem = (SystemSettingItem)findViewById(R.id.system_set_output_record);
		disinfectionItem = (SystemSettingItem)findViewById(R.id.system_set_disinfection_mode);
		aboutUsItem = (SystemSettingItem)findViewById(R.id.system_set_aboutus);
		backImg = (ImageView)findViewById(R.id.title_back_img);
		mTitleTxt.setText(R.string.system_set_title);
		mTitleImg.setImageResource(R.drawable.function_title_sys_setting_img);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		languageSetItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				//选择语言对话框

				startActivityForResult(new Intent(SystemSettingActivity.this, LanguageActivity.class),
						CHANGE_LANGUAGE_REQUEST_CODE);
			}
		});
		wifiSetItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				Log.i("SystemSettingActivity", "wifi");
				Intent intent = new Intent(SystemSettingActivity.this, WlanConnectActivity.class);
				startActivity(intent);

			}
		});
		timeSetItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				Log.i("SystemSettingActivity", "time");
				Intent intent = new Intent(SystemSettingActivity.this, TimeSettingActivity.class);
				startActivity(intent);

			}
		});
		exportSetItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				Log.i("SystemSettingActivity", "export");
				String system_perfusion = PreferenceUtil.getInstance(getApplicationContext())
						.getStringValue(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
				
				if(system_perfusion.equals(Constants.LIVER_PERFUSION_STYSTEM)){

					Intent liverintent = new Intent(SystemSettingActivity.this, PerfusionLogActivity.class);
					startActivity(liverintent);
				}else if(system_perfusion.equals(Constants.KIDNEY_PERFUSION_STYSTEM)){
					Intent kidneyintent = new Intent(SystemSettingActivity.this, KidneyPerfusionLogActivity.class);
					startActivity(kidneyintent);
				}
				
			}
		});
		
		disinfectionItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				//弹出对话框提示是否确定使用消毒模式
				showDisinfectDialog(mContext.getResources().getString(R.string.dialog_notice_disinfection));
			}
		});
		
		aboutUsItem.setOnSystemSetItemClickListener(new OnSystemSetItemClickListener() {
			
			@Override
			public void onSystemSetItemClick() {
				// TODO Auto-generated method stub
				Log.i("SystemSettingActivity", "about");
				Intent intent = new Intent(SystemSettingActivity.this, AboutUsActivity.class);
				startActivity(intent);
			}
		});
		backImg.setOnClickListener(this);
		volumeImg.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mContext = this;
		mApplication = (BioConsoleApplication)getApplication();
		mUIHandler = new UIHandler(this);		
		mSystemAlarmMgr = new SystemAlarmMgr((BioConsoleApplication)getApplication());
		String versionName = CommonUtil.getAppVersionName(getApplicationContext());
		aboutUsItem.setSysItemContent(versionName);
		Locale locale;
		locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        Log.i("xxxx", "SystemSettingActivity" + language);
        if(language.equals("zh")){
        	languageSetItem.setSysItemContent(getResources().getString(R.string.chinese));
        }else if(language.equals("en")){
        	languageSetItem.setSysItemContent(getResources().getString(R.string.english));
        }
        
        //初始化声音为12，即使关机前是静音
      	mAudioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		alarmVolume = (int)((maxVolume * 4)/5);
      	int currentVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
      	if(0 == currentVol){
      		volumeImg.setImageResource(R.drawable.volume_ctrl_mute_bg_selector);
      		isMute = true;
      	}else{
      		volumeImg.setImageResource(R.drawable.volume_ctrl_normal_bg_selector);
      		isMute = false;
      	}
	}
	
	private void initUpdateSysTime(){
		mUIHandler.postDelayed(runnable, 1000);		
	}
	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			//TODO:延时要干的其他事情
			mUIHandler.sendEmptyMessage(Constants.MSG_UPDATE_TIME);
			mUIHandler.postDelayed(runnable, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");		
		initData();	
		initUpdateSysTime();
		bindService();
		registerReceiver();
	}

		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(NetworkUtil.isWifiConnected(getApplicationContext())){
			wifiImg.setImageResource(R.drawable.wifi_on);
		}else{
			wifiImg.setImageResource(R.drawable.wifi_off);
		}	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("SystemSettingActivity", "onDestroy");
		mContext = null;
		mBinder = null;
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
			runnable = null;
		}
		
		if(null != emergencyStopDialog){
			emergencyStopDialog.dismiss();
			emergencyStopDialog = null;
		}
		if(null != disinfectDialog){
			disinfectDialog.dismiss();
			disinfectDialog = null;
		}
		
		unregisterReceiver();
		unbindService();

	}
	
	private void bindService(){
		Intent service = new Intent(SystemSettingActivity.this, BackService.class);
		bindService(service, connection, BIND_AUTO_CREATE);
	}
	
	private void unbindService(){
		unbindService(connection);
	}
	
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBinder = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBinder = (BackBinder)service;
			
		}
	};

	private void registerReceiver(){
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_SYSTEM_ITERRUPT);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_CNECT);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_CNECT);
		//监听压力传感器是否断链
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);
		
		//监听流量传感器是否断连
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);
		//电池电量状态
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_BATTERY_STATE);
		//仪器状态
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_DEVICE_STATE);

		mSerialMsgReceiver = new SerialMsgReceiver();
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mSerialMsgReceiver, mIntentFilter);
		
		// 网络状态改变监听
        registerReceiver(netWorkReceiver = new NetWorkChangeReceiver(), new IntentFilter(Constants.NETWORK_CONNECTION_CHANGE));
        netWorkReceiver.registerListener(this);
	}

	
	private void unregisterReceiver(){
		if(null != mSerialMsgReceiver){
			LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSerialMsgReceiver);
			mSerialMsgReceiver = null;			
		}
		
		if(null != netWorkReceiver){
			unregisterReceiver(netWorkReceiver);
          netWorkReceiver.unregisterListener(this);
          netWorkReceiver = null;
		}
	}
	
	
	
	/***
	 * 检查仪器状态
	 * @param liquid_level 水箱液位状态
	 * @param charging_switch 电池状态 0：异常，1：正常
	 * @param charging_state 充电状态  0：没有充电，1：充电完成 2:正在充电
	 * @param power_state 电源状态  0:电池,1:AC
	 * @param emergency_stop_switch 急停开关
	 */
	/***
	 * 检查仪器状态
	 * @param liquid_level 0:是没水，1是有水水箱液位状态
	 * @param battery_state 电池状态 0：异常，1：正常
	 * @param charging_state 充电状态  0：没有充电，1：充电完成 2:正在充电
	 * @param power_state 电源状态  0:电池,1:AC
	 * @param emergency_stop_switch 急停开关 0:是按下，1：未按
	 */
	
	private void checkDeviceState(DeviceStatusBean devicebean){
		//检查水箱液位状态
		if(null != devicebean && null != mSystemAlarmMgr){
			int liquid_level = devicebean.getLiquid_level();
			int battery_state = devicebean.getBattery_state();
			int charging_state = devicebean.getCharging_state(); 
			int power_state = devicebean.getPower_state();
			int emergency_stop_switch = devicebean.getEmergency_stop_switch();
			
			if(liquid_level == 0){
				if(!mSystemAlarmMgr.isLevelAlarm()){
					mSystemAlarmMgr.setLevelAlarm(true);
					mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
				}
			}else{
				if(mSystemAlarmMgr.isLevelAlarm()){
					mSystemAlarmMgr.setLevelAlarm(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
				}
			}
			//检查紧急开关按钮
			if(emergency_stop_switch == 0){
				if(!mSystemAlarmMgr.isEmergencyStopAlarm()){
					mSystemAlarmMgr.setEmergencyStopAlarm(true);
					emergencyStopImg.setVisibility(View.VISIBLE);
					mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
					showEmergencyStopDialog(getResources().getString(R.string.dialog_notice_emergencystop));
					
				}
			}else{
				if(mSystemAlarmMgr.isEmergencyStopAlarm()){
					emergencyStopImg.setVisibility(View.GONE);
					mSystemAlarmMgr.setEmergencyStopAlarm(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
					
				}
				if(null != emergencyStopDialog){
					emergencyStopDialog.dismiss();
					emergencyStopDialog = null;
				}
			}
			
			//电池正常的状态下，使用AC
			if(battery_state == 1){
				
				isBatteryAbnormal = false;
				batteryImg.setVisibility(View.VISIBLE);
				batteryImg.setBatteryLevelVisible(true);			
				if(power_state == 1 ){
					////使用AC,显示充电，充电完成或正在充电		
					isUseBattery = false;
					batteryImg.setChargingVisible(true);
					if(charging_state ==1){
						//使用AC,电池正常，充电完成				
						batteryImg.setChargingImage(R.drawable.battery_charging);
					}else if(charging_state == 2){
						//使用AC,电池正常，正在充电
						batteryImg.setChargingImage(R.drawable.battery_charging);
					}else if(charging_state == 0){
						//使用AC,电池正常，没有充电
						batteryImg.setChargingImage(R.drawable.ac_plug);
					}
									
				}else if(power_state == 0){
					//使用电池,则不显示充电
					isUseBattery = true;
					batteryImg.setChargingVisible(false);
				}
			}else{
				//电池异常,使用AC
				isBatteryAbnormal = true;
				batteryImg.setChargingVisible(true);
				batteryImg.setBatteryLevelVisible(false);
				batteryImg.setChargingImage(R.drawable.ac_plug);
				batteryImg.setBatteryImage(R.drawable.battery_abnormal);
			}		
		}
	}
	
	/**电池正常**/
	private void checkBatteryState(int battery_level){
		if(!isBatteryAbnormal){
			batteryImg.setBatteryLevelVisible(true);
			if(null != batteryImg){
				if(battery_level == 99){
					battery_level = 100;
				}
				batteryImg.setBatteryLevelText(battery_level + "%");
			}
			if(battery_level<=10){
				batteryImg.setBatteryImage(R.drawable.battery_alert);	
			}else if(battery_level >= 10 && battery_level < 20){
				batteryImg.setBatteryImage(R.drawable.battery_10);			
			}else if(battery_level >= 20 && battery_level < 30){
				batteryImg.setBatteryImage(R.drawable.battery_20);	
			}else if(battery_level >= 30 && battery_level < 40){
				batteryImg.setBatteryImage(R.drawable.battery_30);	
			}else if(battery_level >= 40 && battery_level < 50){
				batteryImg.setBatteryImage(R.drawable.battery_40);	
			}else if(battery_level >= 50 && battery_level < 60){
				batteryImg.setBatteryImage(R.drawable.battery_50);	
			}else if(battery_level >= 60 && battery_level < 70){
				batteryImg.setBatteryImage(R.drawable.battery_60);	
			}else if(battery_level >= 70 && battery_level < 80){
				batteryImg.setBatteryImage(R.drawable.battery_70);	
			}else if(battery_level >= 80 && battery_level < 90){
				batteryImg.setBatteryImage(R.drawable.battery_80);	
			}else if(battery_level >= 90 && battery_level < 99){
				batteryImg.setBatteryImage(R.drawable.battery_90);	
			}else if(battery_level >= 99){
				batteryImg.setBatteryImage(R.drawable.battery_full);	
			}
			if(isUseBattery){
				if(battery_level <= 30){
					if(mSystemAlarmMgr != null ){
						if(!mSystemAlarmMgr.isBatteryAlarm()){
							mSystemAlarmMgr.setBatteryAlarm(true);
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
						}
					}
				}else{
					if(mSystemAlarmMgr != null ){
						if(mSystemAlarmMgr.isBatteryAlarm()){
							mSystemAlarmMgr.setBatteryAlarm(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
				}
			}else{
				if(mSystemAlarmMgr.isBatteryAlarm()){
					mSystemAlarmMgr.setBatteryAlarm(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);					
				}				
			}			
		}		
	}
	
	private void showEmergencyStopDialog(String message){
		if(null != emergencyStopDialog){
			emergencyStopDialog.dismiss();
			emergencyStopDialog = null;
		}
		if(null == emergencyStopDialog){
			emergencyStopDialog = new NoticeDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(message);
			NoticeDialog.Builder builder = new NoticeDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_warning));
			builder.setSingleButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					emergencyStopDialog.dismiss();
					emergencyStopDialog = null;
				}
			});
			
			emergencyStopDialog = builder.create();
			emergencyStopDialog.show();
		}
	}
	
	private void showDisinfectDialog(String message){
		if(null != disinfectDialog){
			disinfectDialog.dismiss();
			disinfectDialog = null;
		}
		if(null == disinfectDialog){
			disinfectDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(message);
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_warning));
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					//发送消毒信息
					
					if(null != mBinder){
						String tarTempMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_NORMAL_TEMPERUTURE,(int)(100*10),"1");
						mBinder.sendSerialCmdMsg(tarTempMsg);
					}else{
						ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.system_set_disinfect));
					}
					disinfectDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					disinfectDialog.dismiss();
					disinfectDialog = null;
				}
			});
			
			disinfectDialog = builder.create();
			disinfectDialog.show();
		}
	}
	
	public static class UIHandler extends Handler{
		
		WeakReference<SystemSettingActivity> mActivityReference;
			
		public UIHandler(SystemSettingActivity mActivity){
			mActivityReference = new WeakReference<SystemSettingActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final SystemSettingActivity mActivity = mActivityReference.get();
			String errorMsg = "";		
			if(null == mActivity){
				return;
			}
			switch (msg.what) {
			//报警
			case Constants.MSG_ALERT_INFO:
				if(mActivity.mSystemAlarmMgr.isSysInterrput()){
					errorMsg = mActivity.getResources().getString(R.string.error_system_communication_interrupt);
				}else{
					if(!mActivity.mApplication.isArteryHeartConnect() || !mActivity.mApplication.isVeinHeartConnect()
							|| mActivity.mSystemAlarmMgr.getArtPreAlarmStatus() != 0
							|| mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus()!= 0
							|| mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus()!= 0
							|| mActivity.mSystemAlarmMgr.isBatteryAlarm()
							|| mActivity.mSystemAlarmMgr.getVeinFlowAlarmStatus() != 0){
						errorMsg = 	mActivity.mSystemAlarmMgr.getSysAlarmMsg();					
					}
				}
				mActivity.mInfoTxt.setText(errorMsg);	
				mActivity.mInfoTxt.setTextColor(mActivity.getResources().getColor(R.color.alert_color));

				if(!mActivity.mApplication.IsMusicPlaying() && null != mActivity.mBinder ){
					Log.i(TAG, "play alert music");		
					mActivity.mApplication.setMusicPlaying(true);
					mActivity.mBinder.startPlay(3);	
					mActivity.mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_HIGH_PRIORITY_ALARM);						
				}	
				break;
				
			case Constants.MSG_NORMAL_INFO:

				if(mActivity.mApplication.isArteryHeartConnect() && mActivity.mApplication.isVeinHeartConnect()
						&& !mActivity.mSystemAlarmMgr.isBatteryAlarm()
						&& mActivity.mSystemAlarmMgr.getArtPreAlarmStatus() == 0 
						&& mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus() == 0
						&& mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus() == 0
						&& mActivity.mSystemAlarmMgr.getVeinFlowAlarmStatus()== 0
						&& !mActivity.mSystemAlarmMgr.isSysInterrput()){						
					mActivity.mInfoTxt.setTextColor(mActivity.getResources()
							.getColor(R.color.color_syspreset_bg));
					mActivity.mInfoTxt.setText("");
					//
					if(null != mActivity.mBinder && mActivity.mApplication.IsMusicPlaying()){
						Log.i(TAG, "stop music");
						mActivity.mBinder.stopPlay();
						mActivity.mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_NORMAL_PRIORITY_ALARM);
						mActivity.mApplication.setMusicPlaying(false);
					}
				}else{
					errorMsg = mActivity.mSystemAlarmMgr.getSysAlarmMsg();
					mActivity.mInfoTxt.setTextColor(mActivity.getResources().getColor(R.color.alert_color));
					mActivity.mInfoTxt.setText(errorMsg);
				}
				
				break;
			case Constants.MSG_UPDATE_TIME:
				String sysTime = DateFormatUtil.getNowTime();
				String sysDate = DateFormatUtil.getNowDate();
				
				mActivity.mDateTimeTxt.setDateText(sysDate);
				mActivity.mDateTimeTxt.setTimeText(sysTime);
				break;
			case Constants.MSG_UPDATE_DEVICE_STATE:
				DeviceStatusBean deviceStateBean = (DeviceStatusBean)msg.obj;
				mActivity.checkDeviceState(deviceStateBean);
				break;
			}
		}
		
	}

	private class SerialMsgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			
			if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_CNECT)){

				String mArterydisconnect = intent.getStringExtra(SerialMsgConstant.ARTERY_CNECT);
				Log.i(TAG, "action--mArterydisconnect-" + mArterydisconnect);
				if(null != mApplication){
					if(mArterydisconnect.equals("disconnect")){
						if(mApplication.isArteryHeartConnect()){
							mApplication.setArteryHeartConnect(false);	
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
						}										
					}else if(mArterydisconnect.equals("connect")){
					
						if(!mApplication.isArteryHeartConnect()){
							mApplication.setArteryHeartConnect(true);
							mSystemAlarmMgr.setSysInterrput(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
							
						}
					}
				}

			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_CNECT)){
				
				String mVeindisconnect = intent.getStringExtra(SerialMsgConstant.VEIN_CNECT);	
				Log.i(TAG, "action--mVeindisconnect-" + mVeindisconnect);
				if(null != mApplication){
					if(mVeindisconnect.equals("disconnect")){
						if(mApplication.isVeinHeartConnect()){
							mApplication.setVeinHeartConnect(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
							
						}					
					}else if(mVeindisconnect.equals("connect")){
						if(!mApplication.isVeinHeartConnect()){
							mApplication.setVeinHeartConnect(true);
							mSystemAlarmMgr.setSysInterrput(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
							
						}
					}
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_SYSTEM_ITERRUPT)){
				boolean isSystemInterrupt = intent.getBooleanExtra(SerialMsgConstant.STSTEM_ITERRUPT, false);
				if(isSystemInterrupt){
					mSystemAlarmMgr.setSysInterrput(true);
					mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
				}else{
					mSystemAlarmMgr.setSysInterrput(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL)){
				String pump1_Preal = intent.getStringExtra(SerialMsgConstant.ARTERY_PREAL);
				if(null != pump1_Preal && null != mSystemAlarmMgr){
					if(pump1_Preal.equals("error1")){
						//肝动脉压力断连						
						if(mSystemAlarmMgr.getArtPreAlarmStatus() != 1){
							mSystemAlarmMgr.setArtPreAlarmStatus(1);													
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
						}					
					}else{
						
						if(mSystemAlarmMgr.getArtPreAlarmStatus() != 0){							
							mSystemAlarmMgr.setArtPreAlarmStatus(0);	
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
										
					}
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL)){
				String pump2_Preal = intent.getStringExtra(SerialMsgConstant.VEIN_PREAL);
				if(null != pump2_Preal && null != mSystemAlarmMgr){
					if(pump2_Preal.equals("error1")){
						//肝动脉流量断连						
						if(mSystemAlarmMgr.getVeinPreAlarmStatus() != 1){
							mSystemAlarmMgr.setVeinPreAlarmStatus(1);													
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
						}					
					}else{
						
						if(mSystemAlarmMgr.getVeinPreAlarmStatus() != 0){							
							mSystemAlarmMgr.setVeinPreAlarmStatus(0);	
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
										
					}
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL)){
				String pump1_Freal = intent.getStringExtra(SerialMsgConstant.ARTERY_FREAL);
				if(null != pump1_Freal){
					if(pump1_Freal.equals("error1")){
						//肝动脉流量断连						
						if(mSystemAlarmMgr.getArtFlowAlarmStatus() != 1){
							mSystemAlarmMgr.setArtFlowAlarmStatus(1);													
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
						}					
					}else if(pump1_Freal.equals("error2")){
						if(mSystemAlarmMgr.getArtFlowAlarmStatus()!= 2){
							mSystemAlarmMgr.setArtFlowAlarmStatus(2);													
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
						}
						
					}else{
						
						if(mSystemAlarmMgr.getArtFlowAlarmStatus() != 0){							
							mSystemAlarmMgr.setArtFlowAlarmStatus(0);	
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
										
					}
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL)){
				String pump2_Freal = intent.getStringExtra(SerialMsgConstant.VEIN_FREAL);
				if(null != pump2_Freal){
					if(pump2_Freal.equals("error1")){
						//肝动脉流量断连
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() != 1){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(1);						
							Log.i(TAG, "disconnect vein flow");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
						}
						
					}else if(pump2_Freal.equals("error2")){
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() != 2){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(2);						
							Log.i(TAG, "abnormal vein flow");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);;
						}
						
					}else{
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() != 0){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(0);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_BATTERY_STATE)){
				String battery_voltage = intent.getStringExtra(SerialMsgConstant.BATTERY_VOLTAGE);//电池电压
				int battery_level = intent.getIntExtra(SerialMsgConstant.BATTERY_LEVEL,0);//电池电量
				checkBatteryState(battery_level);
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_DEVICE_STATE)){
				int liquid_level_state = intent.getIntExtra(SerialMsgConstant.LIQUID_LEVEL,0);//液位状态，0是没有水，1是有水
				int battery_state = intent.getIntExtra(SerialMsgConstant.BATTERY_STATE,0);//充电开关 0:没按下，1：按下
				int charging_state = intent.getIntExtra(SerialMsgConstant.CHARGING_STATE,0);//充电状态 0:没有充电，1：充电完成 2：正在充电
				int power_state = intent.getIntExtra(SerialMsgConstant.POWER_STATE,0);//是否使用电源  1表示AC 0表示 电池
				int emergency_stop_switch = intent.getIntExtra(SerialMsgConstant.EMERGENCY_STOP_SWITCH,0);//急停开关
				DeviceStatusBean deviceStateBean = new DeviceStatusBean(liquid_level_state,battery_state, charging_state,
						power_state, emergency_stop_switch);
				Message device_state_msg = Message.obtain();
				device_state_msg.what = Constants.MSG_UPDATE_DEVICE_STATE;
				device_state_msg.obj = deviceStateBean;
				if(null != mUIHandler){
					mUIHandler.sendMessage(device_state_msg);
				}
				
			}
		}
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_back_img:
			finish();
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			break;
		case R.id.base_title_volume_img:
			if(isMute){
				isMute = false;
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, alarmVolume, AudioManager.FLAG_PLAY_SOUND);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmVolume, AudioManager.FLAG_PLAY_SOUND);
				volumeImg.setImageResource(R.drawable.volume_ctrl_normal_bg_selector);
			}else{
				isMute = true;
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_PLAY_SOUND);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
				volumeImg.setImageResource(R.drawable.volume_ctrl_mute_bg_selector);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onNetWorkChange(boolean isConnected, int type) {
		// TODO Auto-generated method stub
		if(isConnected){
			//连接服务器进行远程控制
			wifiImg.setImageResource(R.drawable.wifi_on);
		}else{
			wifiImg.setImageResource(R.drawable.wifi_off);
		}
	}

}
