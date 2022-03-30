package com.gidi.bio_console.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragmentActivity;
import com.gidi.bio_console.bean.DeviceStatusBean;
import com.gidi.bio_console.bean.HeatDissStatusBean;
import com.gidi.bio_console.bean.PerfusionLogBean;
import com.gidi.bio_console.common.ErrorCode;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.RemoteCtrlContants;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.fragment.preset.ParamCorrectFragment;
import com.gidi.bio_console.fragment.preset.PresetPatternFragment;
import com.gidi.bio_console.fragment.preset.PresetPatternFragment.onPresetPatternListener;
import com.gidi.bio_console.fragment.preset.PreperfusionFragment;
import com.gidi.bio_console.fragment.preset.PresetParamFragment;
import com.gidi.bio_console.fragment.preset.ParamCorrectFragment.OnParamCorrectListener;
import com.gidi.bio_console.fragment.preset.PreperfusionFragment.OnPrePerfusionListener;
import com.gidi.bio_console.fragment.preset.PresetParamFragment.OnPresetParamListener;
import com.gidi.bio_console.fragment.preset.PresetSensorFragment;
import com.gidi.bio_console.fragment.preset.PresetSensorFragment.ChangeTabListener;
import com.gidi.bio_console.listener.OnNetWorkChangedListener;
import com.gidi.bio_console.mgr.SystemAlarmMgr;
import com.gidi.bio_console.receiver.NetWorkChangeReceiver;
import com.gidi.bio_console.remotectrl.DeviceLoginMsg;
import com.gidi.bio_console.remotectrl.LoginServerMgr;
import com.gidi.bio_console.remotectrl.ProCMD;
import com.gidi.bio_console.serialport.SerialPortInstruct;
import com.gidi.bio_console.service.BackService;
import com.gidi.bio_console.service.BackService.BackBinder;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.NetworkUtil;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.view.CustomBatteryView;
import com.gidi.bio_console.view.DateTimeView;
import com.gidi.bio_console.view.NoticeDialog;
import com.google.gson.Gson;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SystemPresetActivity extends BaseFragmentActivity implements  OnNetWorkChangedListener, OnParamCorrectListener, OnPrePerfusionListener, OnPresetParamListener, onPresetPatternListener, ChangeTabListener, OnClickListener {

	private static String TAG = "SystemPresetActivity";
	private Context mContext;
	private SystemPresetActivity mActivity;
	private RelativeLayout mTitleLayout;
	private TextView mInfoTxt;
	public TextView mTitleTxt;
	private ImageView mTitleImg;
	private ImageView wifiImg;
	private ImageView mVolumeImg;
	private CustomBatteryView batteryImg;
	private DateTimeView mDateTimeTxt;
	private ImageView emergencyStopImg;
	private NoticeDialog noticeDialog;
	private NoticeDialog emergencyStopDialog;
	private BioConsoleApplication mApplication;
	private PresetSensorFragment mPresetSensorFragment;
	private ParamCorrectFragment mParamCorrectFragment;
	private PreperfusionFragment mPreperfusionFragment;
	private PresetParamFragment mPresetParamFragment;
	private PresetPatternFragment mPresetPatternFragment;
	private FragmentManager mFragmentManager;
	private SerialMsgReceiver mSerialMsgReceiver;
	private NetWorkChangeReceiver netWorkReceiver;
	private SystemAlarmMgr mSystemAlarmMgr;
	private DatabaseMgr mDatabaseMgr;
	private SearchAsyncTask searchAsyncTask;
	private BackBinder mBinder;
	private UIHandler mUIHandler;
	private AudioManager mAudioManager;	
	private int curFragment = 0;
	private int alarmVolume;
	private boolean isMute = false;
	/**电池异常**/
	private boolean isBatteryAbnormal = false;
	/**仅仅使用电池**/
	private boolean isUseBattery = false;
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_presetting;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate--" );
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initUpdateSysTime();				
		bindService();		
		registerReceiver();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(null != mTitleTxt){
			mTitleTxt.setText(R.string.system_preset);
		}
		if(NetworkUtil.isWifiConnected(getApplicationContext())){
			wifiImg.setImageResource(R.drawable.wifi_on);
		}else{
			wifiImg.setImageResource(R.drawable.wifi_off);
		}		
		crashRestart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy--" );
		finishActivity();
		unregisterReceiver();
		unbindService();
		cancelDialog();
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
			runnable = null;
		}
		if(null != searchAsyncTask){
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		
		
	}
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		mTitleLayout = (RelativeLayout)findViewById(R.id.guide_title_layout);
		mTitleTxt = (TextView)mTitleLayout.findViewById(R.id.base_title_text);	
		mTitleImg = (ImageView)mTitleLayout.findViewById(R.id.base_function_title_icon);
		wifiImg = (ImageView)mTitleLayout.findViewById(R.id.base_title_wifi_img);
		batteryImg = (CustomBatteryView)mTitleLayout.findViewById(R.id.base_title_battery_img);
		emergencyStopImg = (ImageView)mTitleLayout.findViewById(R.id.base_title_emergency_stop_img);
		mVolumeImg = (ImageView)mTitleLayout.findViewById(R.id.base_title_volume_img);
		mTitleImg.setImageResource(R.drawable.function_title_presetting_img);
		mDateTimeTxt = (DateTimeView)mTitleLayout.findViewById(R.id.sys_time_ll);
		mInfoTxt = (TextView)mTitleLayout.findViewById(R.id.base_alert_text);		
	}

	@Override
	protected void initListener(){
		initVariables();
		mVolumeImg.setOnClickListener(this);
		mPresetSensorFragment.setOnChangeTabListener(this);
		mParamCorrectFragment.setOnParamCorrectListener(this);
		mPreperfusionFragment.setOnPrePerfusionListener(this);
		mPresetParamFragment.setOnOnPresetParamListener(this);
		mPresetPatternFragment.setOnPresetPatternListener(this);
		
	}


	private void initVariables(){
		mContext = this;
		mActivity = this;
		mApplication = (BioConsoleApplication)getApplication();
		mApplication.addActivity(mActivity);

		mPresetSensorFragment = new PresetSensorFragment();
		mParamCorrectFragment = new ParamCorrectFragment();
		mPreperfusionFragment = new PreperfusionFragment();
		mPresetParamFragment = new PresetParamFragment();
		mPresetPatternFragment = new PresetPatternFragment();
		
		repalceFragment(0);
		//初始化声音为12，即使关机前是静音
		mAudioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		alarmVolume = (int)((maxVolume * 4)/5);
		int currentVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(0 == currentVol){
			mVolumeImg.setImageResource(R.drawable.volume_ctrl_mute_bg_selector);
			isMute = true;
		}else{
			mVolumeImg.setImageResource(R.drawable.volume_ctrl_normal_bg_selector);
			isMute = false;
		}
				
		mUIHandler = new UIHandler(this);
		mDatabaseMgr = DatabaseMgr.getInstance(getApplicationContext());		
		mSystemAlarmMgr = new SystemAlarmMgr(mApplication);
		if(null == searchAsyncTask){
			searchAsyncTask = new SearchAsyncTask(this);
			searchAsyncTask.execute();
		}
	}
	
	/**start time update**/
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
	
	private void bindService(){
		Intent service = new Intent(SystemPresetActivity.this, BackService.class);
		bindService(service, connection, BIND_AUTO_CREATE);
	}
	
	private void unbindService(){
		unbindService(connection);
	}
	
	private void cancelDialog(){
		if(null != noticeDialog){
			noticeDialog.dismiss();
			noticeDialog = null;
		}
		if(null != emergencyStopDialog){
			emergencyStopDialog.dismiss();
			emergencyStopDialog = null;
		}
	}
	/**
	 * 崩溃重启
	 * 在已经压力调零&&已经灌注&&崩溃的情况下重启后直接进入灌注主界面
	 */
	private void crashRestart(){
		boolean isCrash = PreferenceUtil.getInstance(getApplicationContext()).getBooleanValue(SharedConstants.IS_CRASH, false);
		boolean isFirstZeroPre = PreferenceUtil.getInstance(getApplicationContext())
				.getBooleanValue(SharedConstants.IS_PRESSURE_ZERO_FIRST, true);
		
		boolean isPerfusion = PreferenceUtil.getInstance(getApplicationContext())
				.getBooleanValue(SharedConstants.IS_PERFUSION, false);
		Log.i("BioConsoleApplication", "---crashRestart--" + "isFirstZeroPre---" + isFirstZeroPre);
		Log.i("BioConsoleApplication", "---crashRestart--" + "isPerfusion---" + isPerfusion);
		Log.i("BioConsoleApplication", "---crashRestart--" + "isCrash---" + isCrash);
		if(!isFirstZeroPre && isPerfusion && isCrash){
			Log.i("BioConsoleApplication", "---crashRestart--");
			finishSysPreActivity();
		}
	}
	
	private void finishActivity(){
		mApplication.removeActivity(mActivity);
		mActivity = null;
		mContext = null;
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
		//散热状态
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_HEATDISS_STATE);
		
		mIntentFilter.addAction(BroadcastActions.ACTION_END_BOOTUP_SYSTEM);
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
	
	private void repalceFragment(int position){
		if(null == mFragmentManager){
			mFragmentManager = getSupportFragmentManager();
		}
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		switch (position) {
		case 0:
			fragmentTransaction.replace(R.id.guide_layout, mPresetSensorFragment);

			break;

		default:
			break;
		}
		fragmentTransaction.commit();
       
	}

	public void repalceFragment(Fragment from, Fragment to){
		if(null == mFragmentManager){
			mFragmentManager = getSupportFragmentManager();
		}
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.guide_layout, to);
		fragmentTransaction.commit();
	}


	@Override
	public void sendArteryZeroPreMsg() {
		// TODO Auto-generated method stub
		if(null != mBinder){
			String zeroPreMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_PRESSURE_ONE, 0,"1");
			mBinder.sendSerialCmdMsg(zeroPreMsg);
		}
	}
	
	@Override
	public void sendVeinZeroPreMsg() {
		// TODO Auto-generated method stub
		if(null != mBinder){
			String zeroPreMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_PRESSURE_TWO, 0,"1");
			mBinder.sendSerialCmdMsg(zeroPreMsg);
		}
	}

	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			hideSoftInputMethod();
		}
		return super.onTouchEvent(event);
		
	}
	
	
	private void hideSoftInputMethod(){
		InputMethodManager im = (InputMethodManager)SystemPresetActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(getCurrentFocus()!= null && getCurrentFocus().getWindowToken() != null){
			im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	
	/**发送报警消息**/
	private void sendAlertInfoMsg(int errorCode){
		Message msg = Message.obtain();
		msg.obj = errorCode;
		msg.what = Constants.MSG_ALERT_INFO;
		mUIHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.base_title_volume_img:
			if(isMute){
				isMute = false;
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, alarmVolume, AudioManager.FLAG_PLAY_SOUND);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmVolume, AudioManager.FLAG_PLAY_SOUND);
				mVolumeImg.setImageResource(R.drawable.volume_ctrl_normal_bg_selector);
			}else{
				isMute = true;
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_PLAY_SOUND);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
				mVolumeImg.setImageResource(R.drawable.volume_ctrl_mute_bg_selector);
			}
			
			break;

		default:
			break;
		}
	}
	

	public static class UIHandler extends Handler{
		
		WeakReference<SystemPresetActivity> mActivityReference;
		
		public UIHandler(SystemPresetActivity mActivity){
			mActivityReference = new WeakReference<SystemPresetActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final SystemPresetActivity mActivity = mActivityReference.get();
			String errorMsg = "";			
			switch (msg.what) {		

				//报警
				case Constants.MSG_ALERT_INFO:
					if(mActivity.mSystemAlarmMgr.isSysInterrput()){
						errorMsg = mActivity.getResources().getString(R.string.error_system_communication_interrupt);
					}else{
						if(!mActivity.mApplication.isArteryHeartConnect() || !mActivity.mApplication.isVeinHeartConnect()
								|| mActivity.mSystemAlarmMgr.getArtPreAlarmStatus()!= 0
								|| mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus()!= 0
								|| mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus()!= 0
								|| mActivity.mSystemAlarmMgr.isBatteryAlarm()
								|| mActivity.mSystemAlarmMgr.getVeinFlowAlarmStatus() != 0
								|| mActivity.mSystemAlarmMgr.isLevelAlarm()
								|| mActivity.mSystemAlarmMgr.isEmergencyStopAlarm()
								|| mActivity.mSystemAlarmMgr.isAndroidDissAlarm()
								|| mActivity.mSystemAlarmMgr.isPowerDissAlarm()
								|| mActivity.mSystemAlarmMgr.isSemiDissAlarm()){
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
							&& !mActivity.mSystemAlarmMgr.isLevelAlarm()
							&& !mActivity.mSystemAlarmMgr.isEmergencyStopAlarm()
							&& !mActivity.mSystemAlarmMgr.isAndroidDissAlarm()
							&& !mActivity.mSystemAlarmMgr.isPowerDissAlarm()
							&& !mActivity.mSystemAlarmMgr.isSemiDissAlarm()
							&& mActivity.mSystemAlarmMgr.getArtPreAlarmStatus()== 0
							&& mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus()== 0
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
					mActivity.mDateTimeTxt.setTimeText(sysTime); //系统时间
					mActivity.mDateTimeTxt.setDateText(sysDate);
					break;
					
				case Constants.MSG_UPDATE_BATTERY_STATE:
					//更新电池状态
					int battery_level = (Integer)msg.obj;
					mActivity.checkBatteryState(battery_level);
					break;
				
				case Constants.MSG_UPDATE_DEVICE_STATE:
					DeviceStatusBean deviceStateBean = (DeviceStatusBean)msg.obj;
					mActivity.checkDeviceState(deviceStateBean);
					break;
					
				case Constants.MSG_UPDATE_HEATDISS_STATE:
					HeatDissStatusBean heatDissStatusBean = (HeatDissStatusBean)msg.obj;
					mActivity.checkHeatDissState(heatDissStatusBean);
					break;	
				default:
					break;
			}
		}		
	}

	@Override
	public void onNetWorkChange(boolean isConnected, int type) {
		// TODO Auto-generated method stub
		if(isConnected){
			//连接服务器进行远程控制
			wifiImg.setImageResource(R.drawable.wifi_on);
//			Log.i("xxs", "SystemPresetActivity---" + isConnected);
//			LoginServer();
		}else{
			wifiImg.setImageResource(R.drawable.wifi_off);
		}
	}
	
	private void LoginServer(){
		DeviceLoginMsg deviceLoginMsg = new DeviceLoginMsg(ProCMD.EProIdDeviceLogin, RemoteCtrlContants.UUID, RemoteCtrlContants.UUID, RemoteCtrlContants.LOGIN_PWD);
		Gson gson = new Gson();
		String loginMsg = gson.toJson(deviceLoginMsg);
		LoginServerMgr mLoginServerMgr = new LoginServerMgr(loginMsg);
		mLoginServerMgr.Login();
	}
	
	/***从硬件检测进入压力调零**/
	@Override
	public void next() {
		// TODO Auto-generated method stub
		String message = mContext.getResources().getString(R.string.dialog_notice_cistern);
		showNoticeDialog(0,message);	
	}

	@Override
	public void completeParamCorrect() {
		// TODO Auto-generated method stub
		//弹出提示的对话框提示旋回压力传感器旋钮
		hideSoftInputMethod();
		String message = mContext.getResources().getString(R.string.dialog_preset_notice_dialog_message);
		showNoticeDialog(1,message);		
	}

	/**结束预灌注，维持500转速进行**/
	@Override
	public void compeletePerfusion() {
		// TODO Auto-generated method stub
		repalceFragment(mPreperfusionFragment,mPresetParamFragment);
		//下发停止的指令
		//cmpltPreperfusion();
		hideSoftInputMethod();
	}
	
	/**结束肝动脉的质量id等输入**/
	@Override
	public void onPresetParamNext(String liverName) {
		// TODO Auto-generated method stub
		BioConsoleApplication.mLiverNum = liverName;
		repalceFragment(mPresetParamFragment,mPresetPatternFragment);
		hideSoftInputMethod();
	}
	
	
	private void showNoticeDialog(final int position ,String message){
		if(null != noticeDialog){
			noticeDialog.dismiss();
			noticeDialog = null;
		}
		if(null == noticeDialog){
			noticeDialog = new NoticeDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(message);
			NoticeDialog.Builder builder = new NoticeDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(mContext.getResources().getString(R.string.prompt));
			builder.setSingleButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(position == 0){
						repalceFragment(mPresetSensorFragment,mParamCorrectFragment);
						curFragment = 1;
					}else if(position == 1){
						repalceFragment(mParamCorrectFragment,mPreperfusionFragment);
						curFragment = 2;
					}
					noticeDialog.dismiss();
					noticeDialog = null;
				}
			});
			
			noticeDialog = builder.create();
			noticeDialog.show();
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
		
	private void sendStopSysMsg(){
		Log.i(TAG, "sendStopSysMsg--" );
		if(null != mBinder){
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, 0);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_VEIN_SPEED, 0);
			mBinder.sendSerialCmdMsg(SerialPortInstruct.STOP_SYSTEM_ONE);
			mBinder.sendSerialCmdMsg(SerialPortInstruct.STOP_SYSTEM_TWO);
		}
	}
	
	private void cmpltPreperfusion(){
		if(null != mBinder){
			int mVeinTarSpeed = StringUtil.convertToInt(500, 0);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_VEIN_SPEED, mVeinTarSpeed);
			String vein_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_TWO, mVeinTarSpeed,"0");			
			mBinder.sendSerialCmdMsg(vein_speed_msg);
			
			int mArtTarSpeed = StringUtil.convertToInt(500, 0);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, mArtTarSpeed);
			String artery_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_ONE, mArtTarSpeed,"0");			
			mBinder.sendSerialCmdMsg(artery_speed_msg);
		}
	}

	/**预灌注下发门静脉灌注泵速**/
	@Override
	public void sendVeinTarSpeedMsg(String veinspeed) {
		// TODO Auto-generated method stub
		
		if(null != mBinder){
			int mVeinTarSpeed = StringUtil.convertToInt(veinspeed, 0);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_VEIN_SPEED, mVeinTarSpeed);
			String vein_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_TWO, mVeinTarSpeed,"0");			
			mBinder.sendSerialCmdMsg(vein_speed_msg);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, vein_speed_msg);
		}
		hideSoftInputMethod();
	}

	@Override
	public void sendArtTarSpeedMsg(String artspeed) {
		// TODO Auto-generated method stub
		
		if(null != mBinder){
			int mArtTarSpeed = StringUtil.convertToInt(artspeed, 0);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, mArtTarSpeed);
			String artery_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_ONE, mArtTarSpeed,"0");			
			mBinder.sendSerialCmdMsg(artery_speed_msg);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, artery_speed_msg);
		}
		hideSoftInputMethod();
	}

	@Override
	public void sendZeroFlowMsg(int type) {
		// TODO Auto-generated method stub
		if(null != mBinder){
			if(type == 1){
				String zeroArtFMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_FLOW_ONE, 0,"3");
				mBinder.sendSerialCmdMsg(zeroArtFMsg);
			}else if(type == 2){
				String zereVeinFMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_FLOW_TWO, 0,"3");
				mBinder.sendSerialCmdMsg(zereVeinFMsg);
			}
			
		}
	}

	private void saveZeroTarSpeed(){
		String target_temp_msg = "";
		PreferenceUtil.getInstance(getApplicationContext())
			.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, 0);
		PreferenceUtil.getInstance(getApplicationContext())
			.setValueByName(SharedConstants.TARGET_VEIN_SPEED, 0);
		PreferenceUtil.getInstance(getApplicationContext())
			.setValueByName(SharedConstants.START_PERFUSION_TIME, System.currentTimeMillis());
		int mTempMode = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);
		float mTarTemp = PreferenceUtil.getInstance(getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_TEMP, 37.0f);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TEMP_CTRL, true);	
		if(mTempMode == 0){
			target_temp_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_NORMAL_TEMPERUTURE, (int)(mTarTemp*10),"1");
		}else if(mTempMode == 1){
			target_temp_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_COLD_TEMPERUTURE, (int)(mTarTemp*10),"1");		
		}
		mBinder.sendSerialCmdMsg(target_temp_msg);		
	}
	
	
	@Override
	public void ArtPVeinP(float mArtPreTarget, float mVeinPreTarget) {
		// TODO Auto-generated method stub
		if(null != mBinder){
			saveZeroTarSpeed();
			String strTarArtPMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_ONE, (int)(mArtPreTarget*10),"1");
			String strTarVeinPMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_TWO, (int)(mVeinPreTarget*10),"1");
			mBinder.sendSerialCmdMsg(strTarArtPMsg);
			mBinder.sendSerialCmdMsg(strTarVeinPMsg);
			mBinder.setCurPerfusionMode(0,2);
			mBinder.setPerfusionStatus(true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.IS_PERFUSION, true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, strTarArtPMsg);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, strTarVeinPMsg);

		}
		savePerfusionLog(0,2);
		finishSysPreActivity();
	}

	@Override
	public void ArtPVeinF(float mArtPreTarget, float mVeinFlowTarget) {
		// TODO Auto-generated method stub
		if(null != mBinder){
			saveZeroTarSpeed();
			String strTarArtPMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_ONE, (int)(mArtPreTarget*10),"1");
			String strTarVeinFMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_FLOW_TWO, (int)mVeinFlowTarget,"0");
			mBinder.sendSerialCmdMsg(strTarArtPMsg);
			mBinder.sendSerialCmdMsg(strTarVeinFMsg);
			mBinder.setCurPerfusionMode(0,3);
			mBinder.setPerfusionStatus(true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.IS_PERFUSION, true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, strTarArtPMsg);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, strTarVeinFMsg);
		}
		savePerfusionLog(0,3);
		finishSysPreActivity();
	}

	/**肝动脉搏动模式下发最大目标压和最小目标压**/
	@Override
	public void ArtMinMaxPVeinP(float mArtMinPreTarget, float ArtMaxPreTarget,
			float mVeinPreTarget) {
		// TODO Auto-generated method stub
		if(null != mBinder){
			saveZeroTarSpeed();
			String strTarArtPMsg = StringUtil.appendMinMaxPre(SerialPortInstruct.SET_TARGET_PULSE_PRESSURE_ONE,(int)mArtMinPreTarget,(int)ArtMaxPreTarget);
			String strTarVeinPMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_TWO, (int)(mVeinPreTarget*10),"1");
			mBinder.sendSerialCmdMsg(strTarArtPMsg);
			mBinder.sendSerialCmdMsg(strTarVeinPMsg);
			mBinder.setCurPerfusionMode(1,2);
			mBinder.setPerfusionStatus(true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.IS_PERFUSION, true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, strTarArtPMsg);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, strTarVeinPMsg);
		}
		savePerfusionLog(1,2);
		finishSysPreActivity();
	}

	@Override
	public void ArtMinMaxPVeinF(float mArtMinPreTarget, float ArtMaxPreTarget,
			float mVeinFlowTarget) {
		// TODO Auto-generated method stub
		if(null != mBinder){
			saveZeroTarSpeed();
			String strTarArtPMsg = StringUtil.appendMinMaxPre(SerialPortInstruct.SET_TARGET_PULSE_PRESSURE_ONE,(int)mArtMinPreTarget,(int)ArtMaxPreTarget);
			String strTarVeinFMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_FLOW_TWO, (int)mVeinFlowTarget,"0");
			mBinder.sendSerialCmdMsg(strTarArtPMsg);
			mBinder.sendSerialCmdMsg(strTarVeinFMsg);
			mBinder.setCurPerfusionMode(1,3);
			mBinder.setPerfusionStatus(true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.IS_PERFUSION, true);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, strTarArtPMsg);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, strTarVeinFMsg);
		}
		savePerfusionLog(1,3);
		finishSysPreActivity();
	}


	/**根据灌注模式发送目标温度**/
	@Override
	public void sendTargetTemp(String temp, int tempmode) {
		// TODO Auto-generated method stub
		String target_temp_msg = "";
		float temperature = StringUtil.convertToFloat(temp, 37.0f);
		PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_TEMP, temperature);
		if(tempmode == 0){
			target_temp_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_NORMAL_TEMPERUTURE, (int)(temperature*10),"1");
		}else if(tempmode == 1){
			target_temp_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_COLD_TEMPERUTURE, (int)(temperature*10),"1");		
		}
		mBinder.sendSerialCmdMsg(target_temp_msg);	
	}

	@Override
	public void closeTempCtrl() {
		// TODO Auto-generated method stub		
		if(null != mBinder){
			mBinder.sendSerialCmdMsg(SerialPortInstruct.CLOSE_TEMP_CTRL);
		}	
	}
	
	/**send speed limit message
	 * 发送泵速限制信息***/
	private void sendLimitSpeedMsg(){
		int artLimitSpeed = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.LIMIT_SPEED_ONE, 1500);
		int veinLimitSpeed = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.LIMIT_SPEED_TWO, 1500);
		String artLimitSpeedMsg = StringUtil.appendMsg(SerialPortInstruct.LIMIT_SPEED_ONE, (int)(artLimitSpeed),"0");		
		String veinLimitSpeedMsg = StringUtil.appendMsg(SerialPortInstruct.LIMIT_SPEED_TWO, (int)(veinLimitSpeed),"0");		
		if(null != mBinder){
			mBinder.sendSerialCmdMsg(artLimitSpeedMsg);
			mBinder.sendSerialCmdMsg(veinLimitSpeedMsg);
		}
	}

	/***进入灌注模式***/
	private void finishSysPreActivity(){
		sendLimitSpeedMsg();
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.IS_PERFUSION, true);
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.IS_CRASH, false);
		Intent intent = new Intent(SystemPresetActivity.this, PerfusionActivity.class);		
		startActivity(intent);
		finish();
	}
	
	/***保存灌注条件**/
	private void savePerfusionLog(int ArtMode, int veinMode){
		String mArt_Mode = "Pulse Pressure";
		String mVein_Mode = "Const Flow";
		String liverNum = PreferenceUtil.getInstance(getApplicationContext())
				.getStringValue(SharedConstants.LIVER_NUMBER, "");
		int liverweight = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.LIVER_WEIGHT, 0);
		if(ArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			mArt_Mode = "Const Pressure";
		}else if(ArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			mArt_Mode = "Pulse Pressure";
		}
		 
		if(veinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			mVein_Mode = "Const Pressure";
		}else if(veinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
			mVein_Mode = "Const Flow";
		}
		long startTime = System.currentTimeMillis();
		PerfusionLogBean perfusionLogBean = new PerfusionLogBean();
		perfusionLogBean.setLiverName(liverNum);
		perfusionLogBean.setLiverWeight(String.valueOf(liverweight));
		perfusionLogBean.setStartTime(DateFormatUtil.formatFullDate(startTime));
		perfusionLogBean.setArtery_mode(mArt_Mode);
		perfusionLogBean.setVein_mode(mVein_Mode);
		DatabaseMgr.getInstance(getApplicationContext()).insertLiverPerfusionLog(perfusionLogBean);
	}

	@Override
	public void sendZeroPressureMsg(int type) {
		// TODO Auto-generated method stub
		if(type == 1){
			sendArteryZeroPreMsg();
		}else if(type == 2){
			sendVeinZeroPreMsg();
		}
	}
	
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
		if(null != devicebean && null != mSystemAlarmMgr && null != mUIHandler){
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


	/**电池状态**/
	private void checkBatteryState(int battery_level){
		if(!isBatteryAbnormal && null != mUIHandler){
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
							//showLowBatteryDialog(R.string.dialog_notice_low_battery);							
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

	/****
	 * 检测散热状态
	 * android_heatdiss：安卓散热状态：0异常，1正常
	 * power_heatdiss:电源散热
	 * semi_heatdiss:半导体散热
	 * @param heatDissStatusBean
	 */
	private void checkHeatDissState(HeatDissStatusBean heatDissStatusBean){
		if(null != heatDissStatusBean && null != mSystemAlarmMgr && null != mUIHandler){
			int android_heatdiss = heatDissStatusBean.getAndroid_heatdiss();
			int power_heatdiss = heatDissStatusBean.getPower_heatdiss();
			int semi_heatdiss = heatDissStatusBean.getSemi_heatdiss();
			if(android_heatdiss == 0){
				if(!mSystemAlarmMgr.isAndroidDissAlarm()){
					mSystemAlarmMgr.setAndroidDissAlarm(true);
					insertAlarmMessage(ErrorCode.E_ANDROID_HEATDISS,"error");
					mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
				}
			}else{
				if(mSystemAlarmMgr.isAndroidDissAlarm()){
					mSystemAlarmMgr.setAndroidDissAlarm(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
				}
			}
			
			if(power_heatdiss == 0){
				if(!mSystemAlarmMgr.isPowerDissAlarm()){
					mSystemAlarmMgr.setPowerDissAlarm(true);
					insertAlarmMessage(ErrorCode.E_POWER_HEATDISS,"error");
					mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
				}
			}else{
				if(mSystemAlarmMgr.isPowerDissAlarm()){
					mSystemAlarmMgr.setPowerDissAlarm(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
				}
			}
			
			if(semi_heatdiss == 0){
				if(!mSystemAlarmMgr.isSemiDissAlarm()){
					mSystemAlarmMgr.setSemiDissAlarm(true);
					insertAlarmMessage(ErrorCode.E_SEMI_HEARDIS,"error");
					mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
				}
			}else{
				if(mSystemAlarmMgr.isSemiDissAlarm()){
					mSystemAlarmMgr.setSemiDissAlarm(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
				}
			}
		}
	}
	
	
	
	//查询数据库中灌注数据的所有的liverName
	private static class SearchAsyncTask extends AsyncTask<Void, Integer, ArrayList<String>>{

		private WeakReference<SystemPresetActivity> weakActivity;
		public SearchAsyncTask(SystemPresetActivity activity){
			weakActivity = new WeakReference<SystemPresetActivity>(activity);
		}
		
		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			final SystemPresetActivity mActivity = weakActivity.get();
			if(null == mActivity){
				return null;
			}
			ArrayList<String> list = mActivity.mDatabaseMgr.getAllPerfusionLiverName();
			Log.i(TAG, "doInBackground result---" + list.size());
			return list;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SystemPresetActivity mActivity = weakActivity.get();
			if(result.size()>0){
				if(null != mActivity.mPresetParamFragment){
					Log.i(TAG, "result---" + result.size());
					mActivity.mPresetParamFragment.setIDNameList(result);
				}
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
							sendAlertInfoMsg(ErrorCode.E_DISCONNECT_ARTERY_MSG);
							if(null != mPresetSensorFragment){
								mPresetSensorFragment.breakArtery();
							}
						}										
					}else if(mArterydisconnect.equals("connect")){
					
						if(!mApplication.isArteryHeartConnect()){
							mApplication.setArteryHeartConnect(true);
							mSystemAlarmMgr.setSysInterrput(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
							if(null != mPresetSensorFragment){
								mPresetSensorFragment.normalArtery();
							}
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
							sendAlertInfoMsg(ErrorCode.E_DISCONNECT_VEIN_MSG);
							if(null != mPresetSensorFragment){
								mPresetSensorFragment.breakVein();
							}
						}					
					}else if(mVeindisconnect.equals("connect")){
						if(!mApplication.isVeinHeartConnect()){
							mApplication.setVeinHeartConnect(true);
							mSystemAlarmMgr.setSysInterrput(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
							if(null != mPresetSensorFragment){
								mPresetSensorFragment.normalVein();
							}
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
							sendAlertInfoMsg(ErrorCode.E_DISCONNECT_PRESSURE_ARTERY_MSG);
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
							sendAlertInfoMsg(ErrorCode.E_DISCONNECT_PRESSURE_VEIN_MSG);
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
							sendAlertInfoMsg(ErrorCode.E_DISCONNECT_FLOW_ARTERY_MSG);
						}					
					}else if(pump1_Freal.equals("error2")&& curFragment == 2){//为什么curFragment == 2
						if(mSystemAlarmMgr.getArtFlowAlarmStatus()!= 2){
							mSystemAlarmMgr.setArtFlowAlarmStatus(2);													
							sendAlertInfoMsg(ErrorCode.E_ABNORMAL_FLOW_VEIN_MSG);
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
							sendAlertInfoMsg(ErrorCode.E_DISCONNECT_FLOW_VEIN_MSG);
						}
						
					}else if(pump2_Freal.equals("error2")&& curFragment == 2){
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() != 2){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(2);						
							Log.i(TAG, "abnormal vein flow");
							sendAlertInfoMsg(ErrorCode.E_ABNORMAL_FLOW_VEIN_MSG);
						}
						
					}else{
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() != 0){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(0);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
				}
				
			}else if(action.equals(BroadcastActions.ACTION_END_BOOTUP_SYSTEM)){
				boolean isPerfusion = PreferenceUtil.getInstance(getApplicationContext())
						.getBooleanValue(SharedConstants.IS_PERFUSION, false);				
				//将灌注状态为false 传入
				if(null != mBinder){
					mBinder.setPerfusionStatus(isPerfusion);
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_BATTERY_STATE)){
				String battery_voltage = intent.getStringExtra(SerialMsgConstant.BATTERY_VOLTAGE);//电池电压
				int battery_level = intent.getIntExtra(SerialMsgConstant.BATTERY_LEVEL,0);//电池电量
				Message battery_level_msg = Message.obtain();
				battery_level_msg.what = Constants.MSG_UPDATE_BATTERY_STATE;
				battery_level_msg.obj = battery_level;
				if(null != mUIHandler){
					mUIHandler.sendMessage(battery_level_msg);
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_DEVICE_STATE)){
				int liquid_level_state = intent.getIntExtra(SerialMsgConstant.LIQUID_LEVEL,0);//液位状态，0是没有水，1是有水
				int battery_state = intent.getIntExtra(SerialMsgConstant.BATTERY_STATE,0);//电池状态 0：异常，1：正常
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
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_HEATDISS_STATE)){
				int android_heatdiss = intent.getIntExtra(SerialMsgConstant.ANDROID_HEATDISS,1);//安卓散热
				int power_heatdiss = intent.getIntExtra(SerialMsgConstant.POWER_HEATDISS,1);//电源散热
				int semi_heatdiss = intent.getIntExtra(SerialMsgConstant.SEMI_HEATDISS,1);//半导体	
				HeatDissStatusBean heatDissStatusBean = new HeatDissStatusBean(android_heatdiss,power_heatdiss ,semi_heatdiss);
				Message heat_diss_msg = Message.obtain();
				heat_diss_msg.what = Constants.MSG_UPDATE_HEATDISS_STATE;
				heat_diss_msg.obj = heatDissStatusBean;
				if(null != mUIHandler){
					mUIHandler.sendMessage(heat_diss_msg);
				}
				
			}
		}
	}
}
