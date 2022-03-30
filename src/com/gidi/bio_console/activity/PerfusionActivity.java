package com.gidi.bio_console.activity;

import java.io.File;
import java.lang.ref.WeakReference;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.R;

import com.gidi.bio_console.base.BaseFragmentActivity;
import com.gidi.bio_console.bean.BloodGasSamplingBean;
import com.gidi.bio_console.bean.DeviceStatusBean;
import com.gidi.bio_console.bean.HeatDissStatusBean;
import com.gidi.bio_console.common.ErrorCode;
import com.gidi.bio_console.common.NormalCode;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.DefValues;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.fragment.PerfusionFragment;
import com.gidi.bio_console.fragment.PerfusionFragment.OnPerfusionListener;
import com.gidi.bio_console.fragment.SettingFragment;
import com.gidi.bio_console.fragment.SettingFragment.onSettingListener;
import com.gidi.bio_console.fragment.ctrl.FragmentController;
import com.gidi.bio_console.listener.KeyBoardActionListener;
import com.gidi.bio_console.listener.OnNetWorkChangedListener;
import com.gidi.bio_console.mgr.SystemAlarmMgr;
import com.gidi.bio_console.receiver.NetWorkChangeReceiver;
import com.gidi.bio_console.serialport.SerialPortInstruct;
import com.gidi.bio_console.service.BackService;
import com.gidi.bio_console.service.BackService.BackBinder;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.ScreenShotUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.utils.ToastUtils;
import com.gidi.bio_console.view.CommonPopupWindow;
import com.gidi.bio_console.view.CustomBatteryView;
import com.gidi.bio_console.view.CustomDialog;
import com.gidi.bio_console.view.CustomSpeedDialog;
import com.gidi.bio_console.view.DateTimeView;
import com.gidi.bio_console.view.NoticeDialog;
import com.gidi.bio_console.view.SystemKeyBoardEditText;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PerfusionActivity extends BaseFragmentActivity implements OnClickListener, onSettingListener, OnNetWorkChangedListener, OnPerfusionListener {

	private static final String TAG = "PerfusionActivity";
	private Context mContext;
	private BioConsoleApplication mApplication;
	private PerfusionActivity mActivity;
	private DatabaseMgr mDatabaseMgr;
	private SystemAlarmMgr mSystemAlarmMgr;
	private SerialMsgReceiver mSerialMsgReceiver;
	private NetWorkChangeReceiver netWorkChangedReceiver;
	private FragmentController mFragmentController;
	private FragmentManager mFragmentManager;
	private Fragment mCurrentFrag;
	private PerfusionFragment mPerfusionFragment;	
	private SettingFragment mSettingFragment;
	//private BloodGasFragment mBloodGasFragment;
	private BackBinder mBinder;
	private UIHandler mUIHandler;
	private AudioManager mAudioManager;
	private SQLiteAsyncTask mSQliteAsyncTask;
	private CustomDialog mStopDialog;
	private CustomSpeedDialog mTempDialog;
	private CustomDialog mSampleTimeDialog;
	private CustomDialog mExitPerfusionDialog;
	private CustomSpeedDialog mSpeedDialog;
	private NoticeDialog emergencyStopDialog;
	private CommonPopupWindow window_lock;
	private RelativeLayout main_root_layout;
	private Button unlockBtn;
	public TextView mTitleTxt;
	private TextView mAlertTxt;
	public ImageView mTitleImg;
	//private ImageView wifiImg;
	private ImageView mVolumeImg;
	private TextView mSysStatusTxt;
	private CustomBatteryView batteryImg;
	private DateTimeView mDateTimeText;	
	
	/**计时器**/
	private Chronometer mChronTimer;//向上计时
	private ImageView mCounterPlayImg;
	private ImageView mCounterResetImg;
	private ImageView mStopImg;
	private Button mLockBtn;
	private Button mSpeedBtn;
	private Button mTempBtn;	
	private Button mExitBtn;	
	private TextView mSettingBtn;
	private TextView mPerfusionBtn;
	private TextView mAlarmImg;
	private TextView mRecordBtn;
	//private ImageView mAddBloodGasImg;
	private ImageView mBarLockImg;
	private ImageView emergencyStopImg;
	//private TextView mBloodGasImg;
	/**目标流量**/	
	private String liverNum;
	private boolean isLock = false;
	/**肝动脉灌注模式 0是恒压,1是搏动**/
	private int mArtMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE;
	/**门静脉灌注模式 2是恒压; 3是恒流**/
	private int mVeinMode = Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE;
	private int alarmVolume;
	private boolean isMute = false;
	/**警报状态**/
	private boolean isWarnStatus = false;
	/**报警状态**/
	private boolean isAlertStatus = false;	
	/**水箱是否关闭**/
	private boolean isTempClose = false;
	/**电池异常**/
	private boolean isBatteryAbnormal = false;
	/**电池正常仅仅使用电池**/
	private boolean isUseBattery = false;
	private int tempMode;
	private int curFragment = 3;

	private boolean isFlag = false;
	private boolean mIsPlay = false;
	private long mRecordTime = 0;//记录的总时间
	
	private static final int MSG_CANCEL_ASYNCTASK = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initVariable();
		initUpdateSysTime();
		bindService();		
		registerReceiver();
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkSystemAlarm();
		//checkWifiState();
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();	
		unregisterReceiver();
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
			runnable = null;
		}
		if(null != mFragmentController){
			mFragmentController.destoryController();
			mFragmentController = null;
		}
		
		if(null != mSQliteAsyncTask){
			mSQliteAsyncTask.cancel(true);
			mSQliteAsyncTask = null;
		}
		
		unbindService();			
		cancelDialog();		
		finishActivity();
		Log.i(TAG, "--onDestroy()--");
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_main_perfusion;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		main_root_layout = (RelativeLayout)findViewById(R.id.main_perfusion_layout);
		mTitleImg = (ImageView)findViewById(R.id.function_title_icon);
		//wifiImg = (ImageView)findViewById(R.id.top_bar_wifi_img);
		mSysStatusTxt = (TextView)findViewById(R.id.top_bar_system_status_img);
		mTitleTxt = (TextView)findViewById(R.id.title_text);
		mAlertTxt = (TextView)findViewById(R.id.base_alert_text);
		mLockBtn = (Button)findViewById(R.id.main_perfusion_lock_btn);
		mStopImg = (ImageView)findViewById(R.id.main_perfusion_stop_restart_img);
		mTempBtn = (Button)findViewById(R.id.main_perfusion_temp_btn);
		mVolumeImg = (ImageView)findViewById(R.id.top_bar_volume_img);
		mSpeedBtn = (Button)findViewById(R.id.main_perfusion_pumpspeed_btn);
		mSettingBtn = (TextView)findViewById(R.id.main_perfusion_setting_btn);
		mPerfusionBtn = (TextView)findViewById(R.id.main_perfusion_perfusion_btn);
		mExitBtn = (Button)findViewById(R.id.main_perfusion_exit_btn);
		mAlarmImg = (TextView)findViewById(R.id.main_perfusion_alarm_img);
		mRecordBtn = (TextView)findViewById(R.id.main_perfusion_record_btn);
		//mAddBloodGasImg = (ImageView)findViewById(R.id.main_perfusion_add_bloodgas_record_img);
		mBarLockImg = (ImageView)findViewById(R.id.top_bar_lock_img);
		mDateTimeText = (DateTimeView)findViewById(R.id.sys_time_ll);
		batteryImg = (CustomBatteryView)findViewById(R.id.top_bar_battery_img);
		emergencyStopImg = (ImageView)findViewById(R.id.top_bar_emergency_stop_img);
		//mBloodGasImg = (TextView)findViewById(R.id.main_perfusion_bloodgas_btn);
		mSpeedBtn.setBackgroundResource(R.drawable.shape_perfusion_right_btn_enable_bg);
		mSpeedBtn.setTextColor(getResources().getColor(R.color.color_perfusion_right_btn_text_normal));
		initCountView();
	}
	
	/***
	 * 计时器初始化
	 */
	private void initCountView(){
	    mCounterPlayImg = (ImageView)findViewById(R.id.counter_play_img);
		mCounterResetImg = (ImageView)findViewById(R.id.counter_reset_img);
		mChronTimer = (Chronometer)findViewById(R.id.perfusion_chronmeter);
		mChronTimer.setText("00:00:00");
		
		mCounterPlayImg.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if (!mIsPlay) {
					onChronometerStart();
					mIsPlay = true;
					mCounterPlayImg
							.setImageResource(R.drawable.timer_stop_selector);
				} else {
					onChronometerStop();
					mIsPlay = false;
					mCounterPlayImg
							.setImageResource(R.drawable.timer_play_selector);
				}
				
			}
		});
		mCounterResetImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onChronometerReset();
				mIsPlay= false;
				mCounterPlayImg.setImageResource(R.drawable.timer_play_selector);
				
			}
		});
		mChronTimer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer arg0) {
					// TODO Auto-generated method stub
				int textLength = mChronTimer.getText().length();
				if(textLength == 5){			
					mChronTimer.setText("00:" + mChronTimer.getText().toString());
				}else if(textLength == 7){
					mChronTimer.setText("0" + mChronTimer.getText().toString());
				}
			}
		});
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mLockBtn.setOnClickListener(this);
		mStopImg.setOnClickListener(this);
		mTempBtn.setOnClickListener(this);
		mVolumeImg.setOnClickListener(this);
		mSpeedBtn.setOnClickListener(this);
		mSettingBtn.setOnClickListener(this);
		mPerfusionBtn.setOnClickListener(this);
		mAlarmImg.setOnClickListener(this);
		mRecordBtn.setOnClickListener(this);
		//mAddBloodGasImg.setOnClickListener(this);
		//mBloodGasImg.setOnClickListener(this);	
		mExitBtn.setOnClickListener(this);		
	}
	
	private void initVariable(){
		mContext = this;
		mActivity = this;
		mApplication = (BioConsoleApplication)getApplication();
		mApplication.addActivity(mActivity);
		
		mDatabaseMgr = DatabaseMgr.getInstance(getApplicationContext());
		mSystemAlarmMgr = SystemAlarmMgr.getInstance(mApplication);
		mUIHandler = new UIHandler(this);
		//初始化音量的状态
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
		
		if(mApplication != null && !mApplication.isStopPump()){
			mSysStatusTxt.setBackgroundColor(getResources().getColor(R.color.green));
			mSysStatusTxt.setText(R.string.perfusion_system_state_running);
		}else{
			mSysStatusTxt.setBackgroundColor(getResources().getColor(R.color.red));
			mSysStatusTxt.setText(R.string.perfusion_system_state_stop);
		}

		
		mFragmentController = FragmentController.getInstance(PerfusionActivity.this, R.id.perfusion_container_fragment);
		liverNum = PreferenceUtil.getInstance(getApplicationContext())
				.getStringValue(SharedConstants.LIVER_NUMBER, "");
		mArtMode = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		mVeinMode = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		setTabSelection(3);
		mPerfusionFragment = (PerfusionFragment)mFragmentController.getFragment(3);
		mPerfusionFragment.setOnPerfusionListener(this);
		mSettingFragment = (SettingFragment)mFragmentController.getFragment(2);
		mSettingFragment.settingListener(this);
		//mBloodGasFragment = (BloodGasFragment)mFragmentController.getFragment(4);
		mPerfusionBtn.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_pressed_bg);
		mTitleTxt.setText(R.string.btn_perfusion);
		mTitleImg.setImageResource(R.drawable.function_title_liver_perfusion);
		
	}
	
	//定时更新时间
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
	
	//其他页面有报警进入主灌注页面要进行报警检测
	private void checkSystemAlarm(){
		if(null != mSystemAlarmMgr){
			if(mSystemAlarmMgr.isSysInterrput()||mSystemAlarmMgr.isArteryBubAlert()
				|| mSystemAlarmMgr.isVeinBubAlert()|| !mApplication.isArteryHeartConnect() || !mApplication.isVeinHeartConnect()
				|| mSystemAlarmMgr.getArtPreAlarmStatus()!=0 || mSystemAlarmMgr.getVeinPreAlarmStatus() != 0 
				|| mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus() != 0|| mSystemAlarmMgr.getVeinFlowAlarmStatus() != 0 
				|| !mSystemAlarmMgr.isArteryBackFlow()|| !mSystemAlarmMgr.isVeinBackFlow() 
				|| mSystemAlarmMgr.getTempStatus()!=0 || mSystemAlarmMgr.isArtSpeedAlarm() 
				|| mSystemAlarmMgr.isVeinSpeedAlarm()|| mSystemAlarmMgr.isBatteryAlarm() 
				|| mSystemAlarmMgr.isLevelAlarm()|| mSystemAlarmMgr.isEmergencyStopAlarm()){				
					if(mUIHandler != null){
						mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
					}				
				}else if(!mSystemAlarmMgr.isArteryBubAlert() && !mSystemAlarmMgr.isVeinBubAlert()
					&& mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect() 
					&& mSystemAlarmMgr.getArtPreAlarmStatus()==0 && mSystemAlarmMgr.getVeinPreAlarmStatus() == 0
					&& mSystemAlarmMgr.getArtFlowAlarmStatus() == 0 && mSystemAlarmMgr.getVeinFlowAlarmStatus() == 0
					&& !mSystemAlarmMgr.isArteryBackFlow() && !mSystemAlarmMgr.isVeinBackFlow() 
					&& !mSystemAlarmMgr.isBatteryAlarm() && !mSystemAlarmMgr.isLevelAlarm()
					&& !mSystemAlarmMgr.isEmergencyStopAlarm()
					&& !mSystemAlarmMgr.isArtSpeedAlarm()&& !mSystemAlarmMgr.isVeinSpeedAlarm()
					&&(mSystemAlarmMgr.getArtWarnFlowStatus()!= 0 
					||mSystemAlarmMgr.getVeinWarnFlowStatus()!= 0
					||mSystemAlarmMgr.getArtWarnPreStatus()!= 0
					||mSystemAlarmMgr.getVeinWarnPreStatus()!= 0)){
					if(mUIHandler != null){
						mUIHandler.sendEmptyMessage(Constants.MSG_WARNING_INFO);
					}
				}else{
					if(mUIHandler != null){
						mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
					}
				}
		}
		

	}
	//检测wifi状态
//	private void checkWifiState(){
//		//进入灌注设备界面：屏幕默认锁屏功能
//		if (NetworkUtil.isWifiConnected(getApplicationContext())){
//			wifiImg.setImageResource(R.drawable.wifi_on);
//		}else {
//			wifiImg.setImageResource(R.drawable.wifi_off);
//		}
//	}
	


	private void bindService() {
		// TODO Auto-generated method stub
		Intent service = new Intent(PerfusionActivity.this, BackService.class);
		bindService(service, connection, BIND_AUTO_CREATE);
		//Intent remoteService = new Intent(PerfusionActivity.this, RemoteService.class);
		//startService(remoteService);
	}
	
	private void unbindService(){
		unbindService(connection);
		connection = null;
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
	
	
	/**监听泵连接的消息**/
	private void registerReceiver(){
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_SYSTEM_ITERRUPT);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_CNECT);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_CNECT);
		//监听压力传感器是否断连		
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);		
		//监听流量传感器是否断连
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);
		//监听气泡
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_QREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_QREAL);
		
		//泵速报警
		mIntentFilter.addAction(BroadcastActions.ACTION_ALARM_PUMP_ONE_SPEED);
		mIntentFilter.addAction(BroadcastActions.ACTION_ALARM_PUMP_TWO_SPEED);
		//电池电量状态
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_BATTERY_STATE);
		//仪器状态
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_DEVICE_STATE);
		//散熱狀態
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_HEATDISS_STATE);
		//远程控制
		mIntentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_SPEED);
		mIntentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_TEMP);
		mIntentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_ARTERY_SPEED);
		mIntentFilter.addAction(BroadcastActions.RECEIVE_REMOTE_CTRL_VEIN_SPEED);
		mSerialMsgReceiver = new SerialMsgReceiver();
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mSerialMsgReceiver, mIntentFilter);	
		registerReceiver(netWorkChangedReceiver = new NetWorkChangeReceiver(), new IntentFilter(Constants.NETWORK_CONNECTION_CHANGE));
        netWorkChangedReceiver.registerListener(this);		
	}

	
	private void unregisterReceiver(){
		if(null != mSerialMsgReceiver){
			LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSerialMsgReceiver);
			mSerialMsgReceiver = null;			
		}
		
		if(null != netWorkChangedReceiver) {
            netWorkChangedReceiver.unregisterListener(this);
            unregisterReceiver(netWorkChangedReceiver);
            netWorkChangedReceiver = null;
		}
	}
	
	private void cancelDialog(){

		if(null != mTempDialog){
			mTempDialog.dismiss();
			mTempDialog = null;
		}
		
		if(null != mSpeedDialog){
			mSpeedDialog.dismiss();
			mSpeedDialog = null;
		}
		
		if(null != mSampleTimeDialog){
			mSampleTimeDialog.dismiss();
			mSampleTimeDialog = null;
		}
		
		if(null != mStopDialog){
			mStopDialog.dismiss();
			mStopDialog = null;
		}
		
		if(null != mExitPerfusionDialog){
			mExitPerfusionDialog.dismiss();
			mExitPerfusionDialog = null;
		}
	}	
	
	private void finishActivity(){
		mApplication.removeActivity(mActivity);
		mContext = null;
		mActivity = null;
		mApplication = null;
		window_lock = null;
		mSystemAlarmMgr = null;
		mAudioManager = null;
		mDatabaseMgr = null;
		mCurrentFrag = null;		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_perfusion_lock_btn:
			if(!isLock){
				lockScreen();
				isLock = true;
				mBarLockImg.setImageResource(R.drawable.top_bar_lock_status);
				mLockBtn.setClickable(false);
				unlockBtn.setClickable(true);
			}
			break;
			
/*		case R.id.main_perfusion_screenshot_btn:
			screenShot();
			displayToast(R.string.screenshot_succee);
			break;*/
			
		case R.id.main_perfusion_stop_restart_img:
			showStopSysDialog();		
			break;
			
		case R.id.main_perfusion_temp_btn:
			showTempSettingDialog();
			break;
		
		case R.id.top_bar_volume_img:
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
			
		case R.id.main_perfusion_pumpspeed_btn:			
			if(mApplication.isStopPump()){
				changeSpeedDialog();
			}
			break;
			
		case R.id.main_perfusion_alarm_img:
			setRightBtnEnable(false);
			setTabSelection(0);
			curFragment = 0;
			clearTabSelection();
			setTabSelectionTitle(mContext.getResources().getString(R.string.alarm_info), 
					R.drawable.function_title_alarm_img);
			mAlarmImg.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_pressed_bg);
			break;
	
		case R.id.main_perfusion_record_btn:
			setRightBtnEnable(false);
			setTabSelection(1);
			curFragment = 1;
			clearTabSelection();
			setTabSelectionTitle(mContext.getResources().getString(R.string.data_record), 
					R.drawable.function_title_record_img);
			mRecordBtn.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_pressed_bg);
			break;
			
		case R.id.main_perfusion_setting_btn:
			setRightBtnEnable(false);
			setTabSelection(2);
			curFragment = 2;
			clearTabSelection();
			setTabSelectionTitle(mContext.getResources().getString(R.string.settings), 
					R.drawable.function_title_perfusion);
			mSettingBtn.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_pressed_bg);
			break;
		
		case R.id.main_perfusion_perfusion_btn:
			setRightBtnEnable(true);
			setTabSelection(3);
			curFragment = 3;
			clearTabSelection();
			setTabSelectionTitle(mContext.getResources().getString(R.string.btn_perfusion), 
					R.drawable.function_title_liver_perfusion);
			mPerfusionBtn.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_pressed_bg);
			break;
		
/*		case R.id.main_perfusion_bloodgas_btn:
			setRightBtnEnable(false);
			setTabSelection(4);
			curFragment = 4;
			clearTabSelection();
			setTabSelectionTitle(mContext.getResources().getString(R.string.blood_gas), 
					R.drawable.function_title_bloodgas_img);
			mBloodGasImg.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_pressed_bg);
			break;*/
			
/*		case R.id.main_perfusion_add_bloodgas_record_img:
			//点击出现对话框 询问是否需要保留当前日期作为采样日期
			showAddSampleTimeDialog();
			break;*/
		
		case R.id.main_perfusion_exit_btn:
			showExitPerfusionDialog();
			break;
			
		default:
			break;
		}
	}
	
	/**设置底部tab切换
	 * 实现背景颜色、字体**/
	private void setTabSelection(int position){
		mFragmentController.showFragment(position);
	}
	
	private void clearTabSelection(){
		mAlarmImg.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_normal_bg);
		mRecordBtn.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_normal_bg);
		mSettingBtn.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_normal_bg);
		mPerfusionBtn.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_normal_bg);
		//mBloodGasImg.setBackgroundResource(R.drawable.shape_perfusion_bottom_btn_normal_bg);
	}
	
	/**设置tab对应的标题**/
	private void setTabSelectionTitle(String title, int titleImgResId){
		mTitleTxt.setText(title);
		mTitleImg.setImageResource(titleImgResId);
	}
	
	 public void switchContent(Fragment from, Fragment to) {		 
        if (mCurrentFrag != to) {
        	mCurrentFrag = to;
            FragmentTransaction transaction = mFragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.slide_right_in, R.anim.slide_left_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.perfusion_container_fragment, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
	 }

	/**锁屏**/
	private void lockScreen(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int screenHeight = metrics.heightPixels;
		// create popup window
		window_lock = new CommonPopupWindow(this, R.layout.pop_window_lock,
				ViewGroup.LayoutParams.MATCH_PARENT,
				screenHeight) {
			@Override
			protected void initView() {
				View view = getContentView();
				unlockBtn = (Button) view.findViewById(R.id.unlock_tv);

			}

			@Override
			protected void initEvent() {
				unlockBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						window_lock.getPopupWindow().dismiss();						
						mBarLockImg.setImageResource(R.drawable.top_bar_unlock_status);
						mLockBtn.setClickable(true);
						unlockBtn.setClickable(false);
						isLock = false;
					}
				});
			}

			@Override
			protected void initWindow() {
				super.initWindow();
				PopupWindow instance = getPopupWindow();
				// 设置PopupWindow是否能响应外部点击事件
				instance.setOutsideTouchable(true);
				// 设置PopupWindow是否能响应点击事件
				instance.setTouchable(true); // 设置PopupWindow可触摸
				instance.setFocusable(true); // 设置PopupWindow可获得焦点
			}
		};
		
		window_lock.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

	}

	/**截屏操作**/
	private void screenShot(){
		final ImageView screenshot_Bmp;
		String PATH_SCREENSHOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bio_Console/ScreenShot/";
		File sceenShotDir = new File(PATH_SCREENSHOT);
        if (!sceenShotDir.exists()|| !sceenShotDir.isDirectory()) {  
        	sceenShotDir.mkdirs();         	
        }
        String subFileDir = liverNum + "_" + DateFormatUtil.getNowDate();
        File liverfileDir = new File(PATH_SCREENSHOT, subFileDir);
        if(!liverfileDir.exists() || !liverfileDir.isDirectory()){
        	liverfileDir.mkdir();
        }
        
        Bitmap bitmap = ScreenShotUtil.saveScreenShot(liverNum,(PerfusionActivity)mContext, liverfileDir);
        screenshot_Bmp = new ImageView(mContext);
        screenshot_Bmp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
        screenshot_Bmp.setImageBitmap(bitmap);
        main_root_layout.addView(screenshot_Bmp);
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils
				.loadAnimation(mContext.getApplicationContext(), R.anim.screen_cut);
		scaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (main_root_layout != null)
					main_root_layout.removeView(screenshot_Bmp);
			}
		});
		screenshot_Bmp.startAnimation(scaleAnimation);

	}
	
	/**停泵
	 * 发送肝动脉和门静脉的stop指令
	 * 
	 * **/
	private void stopSystemPump(){
		sendStopSysMsg();			
		mApplication.setStopPump(true);
		mStopImg.setImageResource(R.drawable.right_bar_tab_restart);
		mSpeedBtn.setBackgroundResource(R.drawable.selector_perfusion_right_btn_normal_bg);
		mSysStatusTxt.setBackgroundColor(getResources().getColor(R.color.red));
		mSysStatusTxt.setText(R.string.perfusion_system_state_stop);
	}
	
	
	private void restartSystemPump(){
		sendRestartSysMsg();
		mApplication.setStopPump(false);
		mStopImg.setImageResource(R.drawable.right_bar_tab_pause);
		mSpeedBtn.setBackgroundResource(R.drawable.shape_perfusion_right_btn_enable_bg);
		mSpeedBtn.setTextColor(getResources().getColor(R.color.color_perfusion_right_btn_text_normal));
		mSysStatusTxt.setBackgroundColor(getResources().getColor(R.color.green));
		mSysStatusTxt.setText(R.string.perfusion_system_state_running);
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

			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, SerialPortInstruct.STOP_SYSTEM_ONE);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, SerialPortInstruct.STOP_SYSTEM_TWO);
		}
	}
	
	/**发送重启泵转动的消息**/
	private void sendRestartSysMsg(){
		Log.i(TAG, "--sendRestartSysMsg-");
		if(null != mBinder){			
			//搏动1、恒压0
			sendArtPerfusionMsg();			
			//门静脉
			sendVeinPerfusionMsg();						
		}
	}
	
	/**肝动脉灌注目标值
	 * 1、判断当前的灌注模式，是搏动模式：1和恒压模式:0
	 * 2、根据灌注模式下发灌注目标值
	 * 
	 * **/
	private void sendArtPerfusionMsg(){
		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			float mArtTarPre = PreferenceUtil.getInstance(getApplicationContext())
					.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
			if(null != mBinder){
				String strArtTarMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_ONE, (int)(mArtTarPre*10),"1");
				PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, strArtTarMsg);
				mBinder.sendSerialCmdMsg(strArtTarMsg);
			}
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			float mArtMinTarPre = PreferenceUtil.getInstance(getApplicationContext())
					.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, 0.0f);
			float mArtMaxTarPre = PreferenceUtil.getInstance(getApplicationContext())
					.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, 0.0f);
			String strPulseArtmsg = StringUtil.appendMinMaxPre(SerialPortInstruct.SET_TARGET_PULSE_PRESSURE_ONE,(int)mArtMinTarPre,(int)mArtMaxTarPre);
			PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, strPulseArtmsg);
			if(null != mBinder){
				mBinder.sendSerialCmdMsg(strPulseArtmsg);
			}
		}		
		sendLimitSpeedMsg();
	}
	
	/**
	 * 门静脉灌注目标值
	 * 1、判断当前的灌注模式是恒压模式：2；还是恒流模式：2
	 * 2、根据门静脉灌注值下发灌注目标值
	 */
	private void sendVeinPerfusionMsg(){
		
		String strVeinTarMsg = "";
		if(mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			float mVeinTarPre = PreferenceUtil.getInstance(getApplicationContext())
					.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
			strVeinTarMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_TWO, (int)(mVeinTarPre*10),"1");
			
		}else if(mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			float mVeinTarFlow = PreferenceUtil.getInstance(getApplicationContext())
					.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, 0.0f);
			strVeinTarMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_FLOW_TWO, (int)(mVeinTarFlow),"0");
		}
		
		if(null != mBinder){
			mBinder.sendSerialCmdMsg(strVeinTarMsg);
			PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, strVeinTarMsg);
		}
		sendLimitSpeedMsg();
	}
	
	/**send speed limit message
	 * 发送泵速限制信息***/
	private void sendLimitSpeedMsg(){
		int artLimitSpeed = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.LIMIT_SPEED_ONE, 1500);
		int veinLimitSpeed = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.LIMIT_SPEED_TWO, 1200);
		String artLimitSpeedMsg = StringUtil.appendMsg(SerialPortInstruct.LIMIT_SPEED_ONE, (int)(artLimitSpeed),"0");		
		String veinLimitSpeedMsg = StringUtil.appendMsg(SerialPortInstruct.LIMIT_SPEED_TWO, (int)(veinLimitSpeed),"0");		
		if(null != mBinder){
			mBinder.sendSerialCmdMsg(artLimitSpeedMsg);
			mBinder.sendSerialCmdMsg(veinLimitSpeedMsg);
		}
	}
	
	/**set the pump target pump speed**/
	private void sendArteryTarSpeedMsg(int arterySpeed){
		Log.i(TAG, "sendArteryTarSpeedMsg" + arterySpeed);
		if(null != mBinder){
			String artery_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_ONE, arterySpeed,"0");		
			PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, artery_speed_msg);
			mBinder.sendSerialCmdMsg(artery_speed_msg);
		}
	}
	
	/**set the pump target pump speed**/
	private void sendVeinTarSpeedMsg(int veinSpeed){
		Log.i(TAG, "sendVeinTarSpeedMsg---" +  veinSpeed);
		if(null != mBinder){			
			String vein_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_TWO, veinSpeed,"0");			
			mBinder.sendSerialCmdMsg(vein_speed_msg);
			PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, vein_speed_msg);
			
		}
	}
	
	private void sendTargetTemp(float tarTemp,int tempMode){
		if(null != mPerfusionFragment){
			mPerfusionFragment.setTargetTemp(tarTemp,tempMode);
		}
	}
	
	
	
	/**close temperature control
	 * message:pump1_Ttarg_close_1
	 * **/
	private void sendStopTempCtrl(){
		PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TEMP_CTRL, false);	
		if(null != mBinder){
			mBinder.sendSerialCmdMsg(SerialPortInstruct.CLOSE_TEMP_CTRL);
		}
		if(null != mPerfusionFragment){
			mPerfusionFragment.setTempAlarmFlag();
		}
	}
	
	/**
	 * 目标温度控制
	 * target temperature control
	 * @param temperature
	 * TEMP_PERFUSION_MODE: 0:normal;1:cold
	 */
	private void sendTempCtrl(float temperature){
		String mTarTempMsg = "";
		int mTempMode = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);
		if(null != mBinder){
			if(mTempMode == 0){
				mTarTempMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_NORMAL_TEMPERUTURE,(int)(temperature*10),"1");			
								
			}else if(mTempMode == 1){
				mTarTempMsg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_COLD_TEMPERUTURE,(int)(temperature*10),"1");						
			}
			mBinder.sendSerialCmdMsg(mTarTempMsg);
		}
		if(null != mPerfusionFragment){
			mPerfusionFragment.setTempAlarmFlag();
		}
	}
	

			
/*	*//**插入一条血气数据至血气表中**//*
	private void addBloodGasRecord(){
		String currentTime = DateFormatUtil.getSysFullTime();
		Log.i(TAG, "addBloodGasRecord---" + " currentTime " + currentTime);
		BloodGasSamplingBean mBloodGasSamplingBean = new BloodGasSamplingBean();
		mBloodGasSamplingBean.setLiverNum(liverNum);
		mBloodGasSamplingBean.setSamplingTime(currentTime);		
		mBloodGasSamplingBean.setChecked(0);
		mSQliteAsyncTask = new SQLiteAsyncTask(this,mBloodGasSamplingBean);
		mSQliteAsyncTask.execute(mBloodGasSamplingBean);	
		*//**如果当前在血气界面**//*
		if(curFragment == 4){
			mBloodGasFragment.exeSearchSampleTimeTask();
		}
	}*/
	
	private void setRightBtnEnable(boolean isEnable){
		mSpeedBtn.setEnabled(isEnable);
		mStopImg.setEnabled(isEnable);
	}
	
	/***
	 * 检查仪器状态
	 * 电池正常的情况下，如果使用AC,充电图标显示，充电状态1,2显示闪电充电图标，充电状态：0（没有充电）：显示插头图标
	 * @param liquid_level 水箱液位状态
	 * @param charging_switch 充电开关 0：没按下，1：按下
	 * @param charging_state 充电状态  0：没有充电，1：充电完成 2:正在充电
	 * @param power_state 电源状态  0：AC,1:电池
	 * @param emergency_stop_switch 急停开关
	 */
	private void checkDeviceState(DeviceStatusBean devicebean){
		if(null != devicebean && null != mSystemAlarmMgr){
			int liquid_level = devicebean.getLiquid_level();
			int battery_state = devicebean.getBattery_state();
			int charging_state = devicebean.getCharging_state(); 
			int power_state = devicebean.getPower_state();
			int emergency_stop_switch = devicebean.getEmergency_stop_switch();
			//检查水箱液位状态
			if(liquid_level == 0){
				if(!mSystemAlarmMgr.isLevelAlarm()){
					mSystemAlarmMgr.setLevelAlarm(true);
					insertAlarmMessage(ErrorCode.E_ALARM_LIQUID_LEVEL,"error");
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
					insertAlarmMessage(ErrorCode.E_SYSTEM_EMERGENCY,"error");
					mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
					emergencyStopImg.setVisibility(View.VISIBLE);
					showEmergencyStopDialog(getResources().getString(R.string.dialog_notice_emergencystop));
				}
			}else{
				if(mSystemAlarmMgr.isEmergencyStopAlarm()){				
					mSystemAlarmMgr.setEmergencyStopAlarm(false);
					mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
					emergencyStopImg.setVisibility(View.GONE);
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
				batteryImg.setChargingImage(R.drawable.battery_charging);
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
	
	/***
	 * 处理电池充电状态
	 * @param battery_level
	 */
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
			if(isUseBattery ){
				if(null != mSystemAlarmMgr && null != mUIHandler){
					if(battery_level <= 30){
						if(!mSystemAlarmMgr.isBatteryAlarm()){
							mSystemAlarmMgr.setBatteryAlarm(true);
							insertAlarmMessage(ErrorCode.E_LOWER_BATTERY, "error");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
						}
						
					}else{
						if(mSystemAlarmMgr.isBatteryAlarm()){
							mSystemAlarmMgr.setBatteryAlarm(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
				}
				
			}else{
				if(null != mSystemAlarmMgr && null != mUIHandler){
					if(mSystemAlarmMgr.isBatteryAlarm()){
						mSystemAlarmMgr.setBatteryAlarm(false);
						mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);					
					}
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
		if(null != heatDissStatusBean 
				&& null != mSystemAlarmMgr && null != mUIHandler){
			int android_heatdiss = heatDissStatusBean.getAndroid_heatdiss();
			int power_heatdiss = heatDissStatusBean.getPower_heatdiss();
			int semi_heatdiss = heatDissStatusBean.getSemi_heatdiss();
			if(android_heatdiss == 0){
				if(!mSystemAlarmMgr.isAndroidDissAlarm()){
					mSystemAlarmMgr.setAndroidDissAlarm(true);
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
	

	
	/**处理气泡消息**/
	private void onHandleBubbleStatus(int pumptype, boolean isStop ,boolean isAlert,int errorCode){
		if(null != mSystemAlarmMgr){
			if(pumptype == 0){
				//肝动脉				
				mSystemAlarmMgr.setArteryBubAlert(isAlert);
			}else if(pumptype ==1){
				//门静脉
				mSystemAlarmMgr.setVeinBubAlert(isAlert);
			}

		}		
		if(null != mUIHandler){
			if(!isAlert){
				mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
			}else{
				mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
			}
		}		
	}

		
	private void displayToast(int resourceId){
		ToastUtils.showToast(getApplicationContext(), mContext.getResources().getString(resourceId));
	}
	
	/**set the textview is selected **/
	private void setSelectedView(TextView view){
		view.setBackgroundResource(R.drawable.shape_temp_choice_pressed_bg);
		view.setTextColor(getResources().getColor(R.color.blue));
	}

	private void setNormalView(TextView view){
		view.setBackgroundResource(R.drawable.shape_temp_choice_normal_bg);
		view.setTextColor(getResources().getColor(R.color.white));
	}

	/**弹出是否点击泵的提示**/
	private void showStopSysDialog(){
		if(null != mStopDialog){
			mStopDialog.dismiss();
			mStopDialog = null;
		}
		if(null == mStopDialog){
			mStopDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mStopText = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			if(mApplication.isStopPump()){
				//已经停止
				mStopText.setText(R.string.dialog_restart_message);
			}else{
				mStopText.setText(R.string.dialog_stop_message);
			}
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(R.string.prompt);
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setPositiveButton(R.string.dialog_ok,  new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(!mApplication.isStopPump()){
						//没有停止
						stopSystemPump();

					}else{
						restartSystemPump();
						if(mSystemAlarmMgr.isArteryBubAlert()){
							onHandleBubbleStatus(0,false,false, NormalCode.N_BUBBLE_ONE_MSG);
						}
						if(mSystemAlarmMgr.isVeinBubAlert()){
							onHandleBubbleStatus(1,false,false, NormalCode.N_BUBBLE_TWO_MSG);
						}
					}
					mStopDialog.dismiss();
				}
			});
			
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mStopDialog.dismiss();
					mStopDialog = null;
				}
			});
			mStopDialog = builder.create();
			mStopDialog.show();
		}
	}
		
	
	/**setting the target temperature
	 * 
	 * 
	 **/
	private void showTempSettingDialog(){
		final float mLast_TarTemp = PreferenceUtil.getInstance(getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_TEMP, 37.0f);
		final boolean isTempCtrl = PreferenceUtil.getInstance(getApplicationContext())
				.getBooleanValue(SharedConstants.TEMP_CTRL, false);//默认是关，
		final int mLastTempMode = PreferenceUtil.getInstance(getApplicationContext())
				.getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);//0：normal；1：cold
		tempMode = mLastTempMode;
		if(null != mTempDialog){
			mTempDialog.dismiss();
			mTempDialog = null;
		}
		if(null == mTempDialog){
			mTempDialog = new CustomSpeedDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_change_target_temp, null);
			final CheckBox mCtrlTempCb = (CheckBox)view.findViewById(R.id.temp_control_checkbox);
			final RadioGroup mTempModeRg = (RadioGroup)view.findViewById(R.id.dlg_temp_mode_rg);
			final RadioButton mHMP_Rb = (RadioButton)view.findViewById(R.id.dlg_temp_hmp_rb);
			final RadioButton mNMP_Rb = (RadioButton)view.findViewById(R.id.dlg_temp_nmp_rb);
			final RelativeLayout tempView = (RelativeLayout)view.findViewById(R.id.target_temp_layout);
			final RelativeLayout modeView = (RelativeLayout)view.findViewById(R.id.on_off_temp_mode_layout);
			final SystemKeyBoardEditText mCustomTempEt = (SystemKeyBoardEditText)view.findViewById(R.id.dialog_change_set_temp_et);
			mCustomTempEt.setCurrentView(view);
			final RadioGroup mTarTempRg = (RadioGroup)view.findViewById(R.id.dialog_change_set_temp_radiogroup);
			final TextView mTarTempTxt = (TextView)view.findViewById(R.id.dialog_setting_temp_txt);
			mTarTempTxt.setText(String.valueOf(mLast_TarTemp));
			mCustomTempEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
				
				@Override
				public void onTextChange(Editable editable) {
					// TODO Auto-generated method stub
					//实时判断温度超过37度提示
					float temp = StringUtil.convertToFloat(editable.toString().trim(), (float)37.0);
					if(temp >  37.0f){
						editable.replace(0,editable.length(), "37");
						displayToast(R.string.error_target_temp_max_tip);
						mTarTempTxt.setText(37 +"");
						return;
					}else{
						mTarTempTxt.setText(temp + "");
					}
					
				}
				
				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					String target_temp = mCustomTempEt.getText().toString().trim();
					if(StringUtil.isEmpty(target_temp)){
						mTarTempTxt.setText(getResources().getString(R.string.string_null));
					}else{
						if(StringUtil.isDicimals(target_temp)){
							mTarTempTxt.setText(target_temp);
						}else{
							mTarTempTxt.setText(StringUtil.getInteger(target_temp));
						}
					}
				}
				
				@Override
				public void onClearAll() {
					// TODO Auto-generated method stub
					mTarTempTxt.setText(getResources().getString(R.string.string_null));
				}
				
				@Override
				public void onClear() {
					// TODO Auto-generated method stub
					String target_temp = mCustomTempEt.getText().toString().trim();
					if(StringUtil.isEmpty(target_temp)){
						mTarTempTxt.setText(getResources().getString(R.string.string_null));
					}else{
						if(StringUtil.isDicimals(target_temp)){
							mTarTempTxt.setText(target_temp);
						}else{
							mTarTempTxt.setText(StringUtil.getInteger(target_temp));
						}
					}
				}
			});
			mCustomTempEt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mTarTempRg.clearCheck();
					mCustomTempEt.setText("");
					setSelectedView(mCustomTempEt);
				}
			});
			mTarTempRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
					// TODO Auto-generated method stub
					int id = radiogroup.getCheckedRadioButtonId();
					RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
					switch (id) {
					case R.id.dialog_change_set_temp_four_rb:
					case R.id.dialog_change_set_temp_eight_rb:
					case R.id.dialog_change_set_temp_twenty_five_rb:
					case R.id.dialog_change_set_temp_thirdty_seven_rb:
						setNormalView(mCustomTempEt);
						String temp = radioButton.getText().toString();					
						mTarTempTxt.setText(temp);
						break;
					default:
						break;
					}
				}
			});
			
			if(isTempCtrl){
				mCtrlTempCb.setChecked(true);
				tempView.setVisibility(View.VISIBLE);
				modeView.setVisibility(View.VISIBLE);
			}else{
				mCtrlTempCb.setChecked(false);
				tempView.setVisibility(View.GONE);
				modeView.setVisibility(View.GONE);
			}
			if(mLastTempMode == 0){
				mNMP_Rb.setChecked(true);
				mHMP_Rb.setChecked(false);
			}else if(mLastTempMode == 1){
				mNMP_Rb.setChecked(false);
				mHMP_Rb.setChecked(true);
			}
			CustomSpeedDialog.Builder builder = new CustomSpeedDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(R.string.dialog_title_set_temp);
			mCtrlTempCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						tempView.setVisibility(View.VISIBLE);
						modeView.setVisibility(View.VISIBLE);
					}else{
						tempView.setVisibility(View.GONE);
						modeView.setVisibility(View.GONE);
						//发送关闭水箱						
					}
				}
			});
			mTempModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup radiogroup, int isChecked) {
					// TODO Auto-generated method stub
					switch (radiogroup.getCheckedRadioButtonId()) {
					case R.id.dlg_temp_hmp_rb:
						tempMode = 1;
						break;
					case R.id.dlg_temp_nmp_rb:
						tempMode = 0;
						break;
					}
				}
			});
			
			builder.setPositiveButton(R.string.dialog_ok,  new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(mCtrlTempCb.isChecked()){
						//开
						PreferenceUtil.getInstance(getApplicationContext()).setValueByName(SharedConstants.TEMP_CTRL, true);
						String temp = mTarTempTxt.getText().toString();
						if(!StringUtil.isEmpty(temp)|| !temp.endsWith(".")|| temp.equals("--")){
							float mTarTemp = StringUtil.convertToFloat(temp, 37.0f);
							if(mTarTemp > 37){
								displayToast(R.string.error_target_temp_max_tip);						
								return;
							}else if(mTarTemp <0){
								displayToast(R.string.error_target_temp_min_tip);						
								return;
							}
							if(isTempClose){
								//原来时关闭的
								isTempClose = false;
								if(mBinder != null){
									sendTempCtrl(mLast_TarTemp);
								}
							}
							if((mLast_TarTemp != mTarTemp)||(mLastTempMode != tempMode)){
								sendTargetTemp(mTarTemp,tempMode);
								PreferenceUtil.getInstance(getApplicationContext())
									.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, tempMode);
								PreferenceUtil.getInstance(getApplicationContext())
									.setValueByName(SharedConstants.TARGET_TEMP, mTarTemp);
								sendTempCtrl(mTarTemp);					
							}
							
						}
					}else{
						//关
						isTempClose = true;
						sendStopTempCtrl();
					}
					PreferenceUtil.getInstance(getApplicationContext())
						.setValueByName(SharedConstants.TEMP_PERFUSION_MODE, tempMode);
					mTempDialog.dismiss();
				}
			});
			
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mTempDialog.dismiss();
					mTempDialog = null;
				}
			});
			mTempDialog = builder.create();
			mTempDialog.show();
		}
	}
	
	
	
	/**
	 * change the perfusion pump speed
	 */
	private void changeSpeedDialog(){
		if(null != mSpeedDialog){
			mSpeedDialog.dismiss();
			mSpeedDialog = null;
		}
		if(null == mSpeedDialog){
			mSpeedDialog = new CustomSpeedDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_change_pump_speed, null);			
			final SystemKeyBoardEditText mTarArterySpeedEt = (SystemKeyBoardEditText)view.findViewById(R.id.target_artery_speed_edit);
			final SystemKeyBoardEditText mTarVeinSpeedEt = (SystemKeyBoardEditText)view.findViewById(R.id.target_vein_speed_edit);
			final RadioGroup mArtSpeedRg = (RadioGroup)view.findViewById(R.id.dialog_art_speed_set_radiogroup);
			final RadioGroup mVeinSpeedRg = (RadioGroup)view.findViewById(R.id.dialog_vein_speed_set_radiogroup);
			final TextView mTarArtSpeedTxt = (TextView)view.findViewById(R.id.dialog_art_setting_speed_txt);
			final TextView mTarVeinSpeedTxt = (TextView)view.findViewById(R.id.dialog_vein_setting_speed_txt);
			mTarArterySpeedEt.setCurrentView(view);
			mTarVeinSpeedEt.setCurrentView(view);
			final int lastArtSpeed = PreferenceUtil.getInstance(getApplicationContext())
					.getIntValue(SharedConstants.TARGET_ARTERY_SPEED, 0);
			final int lastVeinSpeed = PreferenceUtil.getInstance(getApplicationContext())
					.getIntValue(SharedConstants.TARGET_VEIN_SPEED, 0);
			mTarArtSpeedTxt.setText(String.valueOf(lastArtSpeed));
			mTarVeinSpeedTxt.setText(String.valueOf(lastVeinSpeed));
			mTarArterySpeedEt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mArtSpeedRg.clearCheck();					
					setSelectedView(mTarArterySpeedEt);
					mTarArterySpeedEt.setText("");
				}
			});
			mTarArterySpeedEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
				
				@Override
				public void onTextChange(Editable editable) {
					// TODO Auto-generated method stub
					int arterySpeed = StringUtil.convertToInt(mTarArterySpeedEt.getText().toString().trim(), 0);
					//判断不能超过3000
					if(arterySpeed > 3000){
						editable.replace(0,editable.length(), "3000");
						displayToast(R.string.perfusion_error_pumpspeed);
						mTarArtSpeedTxt.setText(3000+"");
					}else{
						mTarArtSpeedTxt.setText(arterySpeed +"");
					}
				}
				
				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					String artery_speed = mTarArterySpeedEt.getText().toString().trim();
					if(StringUtil.isEmpty(artery_speed)){
						mTarArtSpeedTxt.setText(getResources().getString(R.string.string_null));
					}else{
						mTarArtSpeedTxt.setText(artery_speed);
					}
				}
				
				@Override
				public void onClearAll() {
					// TODO Auto-generated method stub
					mTarArtSpeedTxt.setText(getResources().getString(R.string.string_null));
				}
				
				@Override
				public void onClear() {
					// TODO Auto-generated method stub
					String artery_speed = mTarArterySpeedEt.getText().toString().trim();
					if(StringUtil.isEmpty(artery_speed)){
						mTarArtSpeedTxt.setText(getResources().getString(R.string.string_null));
					}else{
						mTarArtSpeedTxt.setText(artery_speed);
					}
				}
			});

			mTarVeinSpeedEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
				
				@Override
				public void onTextChange(Editable editable) {
					// TODO Auto-generated method stub					
					int veinSpeed = StringUtil.convertToInt(mTarVeinSpeedEt.getText().toString().trim(),0);
					if(veinSpeed > 3000){
						editable.replace(0,editable.length(), "3000");
						displayToast(R.string.perfusion_error_pumpspeed);
						mTarVeinSpeedTxt.setText(3000+"");
					}else{
						mTarVeinSpeedTxt.setText(veinSpeed +"");
					}
				}
				
				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					String vein_speed = mTarVeinSpeedEt.getText().toString().trim();
					if(StringUtil.isEmpty(vein_speed)){
						mTarVeinSpeedTxt.setText(getResources().getString(R.string.string_null));
					}else{
						mTarVeinSpeedTxt.setText(vein_speed);
					}				
				}
				
				@Override
				public void onClearAll() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onClear() {
					// TODO Auto-generated method stub
					String vein_speed = mTarVeinSpeedEt.getText().toString().trim();
					if(StringUtil.isEmpty(vein_speed)){
						mTarVeinSpeedTxt.setText(getResources().getString(R.string.string_null));
					}else{
						mTarVeinSpeedTxt.setText(vein_speed);
					}	
				}
			});
			
			mArtSpeedRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
					// TODO Auto-generated method stub
					int id = radiogroup.getCheckedRadioButtonId();
					RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
					switch (id) {
					case R.id.dialog_art_set_speed_one_rb:
					case R.id.dialog_art_set_speed_two_rb:
					case R.id.dialog_art_set_speed_three_rb:
					case R.id.dialog_art_set_speed_four_rb:
						setNormalView(mTarArterySpeedEt);
						String arterySpeed= radioButton.getText().toString();					
						mTarArtSpeedTxt.setText(arterySpeed);
						break;
					default:
						break;
					}
				}
			});
			mVeinSpeedRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup radiogroup, int arg1) {
					// TODO Auto-generated method stub
					int id = radiogroup.getCheckedRadioButtonId();
					RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
					switch (id) {
					case R.id.dialog_vein_set_speed_one_rb:
					case R.id.dialog_vein_set_speed_two_rb:
					case R.id.dialog_vein_set_speed_three_rb:
					case R.id.dialog_vein_set_speed_four_rb:
						setNormalView(mTarVeinSpeedEt);	
						String veinSpeed= radioButton.getText().toString();					
						mTarVeinSpeedTxt.setText(veinSpeed);
						break;
					default:
						break;
					}
				}
			});
			mTarArterySpeedEt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					mArtSpeedRg.clearCheck();
					mTarArterySpeedEt.setText("");
					setSelectedView(mTarArterySpeedEt);
				}
			});
			
			mTarVeinSpeedEt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub				
					mVeinSpeedRg.clearCheck();
					mTarVeinSpeedEt.setText("");
					setSelectedView(mTarVeinSpeedEt);
				}
			});

						
			CustomSpeedDialog.Builder builder = new CustomSpeedDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitleIcon(0);
			builder.setTitle(mContext.getResources().getString(R.string.set_pumpspeed));
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int arteryTarSpeed = StringUtil.convertToInt(mTarArtSpeedTxt.getText().toString().trim(), 0);
					int veinTarSpeed = StringUtil.convertToInt(mTarVeinSpeedTxt.getText().toString().trim(), 0);
					PreferenceUtil.getInstance(getApplicationContext())
						.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, arteryTarSpeed);
					PreferenceUtil.getInstance(getApplicationContext())
						.setValueByName(SharedConstants.TARGET_VEIN_SPEED, veinTarSpeed);
					if(lastArtSpeed != arteryTarSpeed && arteryTarSpeed <= 3000){
						sendArteryTarSpeedMsg(arteryTarSpeed);						
					}
					if(lastVeinSpeed != veinTarSpeed && veinTarSpeed <= 3000){
						sendVeinTarSpeedMsg(veinTarSpeed);
					}
					mSpeedDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mSpeedDialog.dismiss();
					mSpeedDialog = null;
				}
			});
			mSpeedDialog = builder.create();
			mSpeedDialog.show();
			
		}
	}
	
	
	private void showAddSampleTimeDialog(){
		if(null != mSampleTimeDialog){
			mSampleTimeDialog.dismiss();
			mSampleTimeDialog = null;
		}
		if(null == mSampleTimeDialog){
			mSampleTimeDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(R.string.dialog_msg_perfusion_add_bloodgas_sampletime);
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_sample_time));
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//addBloodGasRecord();
					mSampleTimeDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mSampleTimeDialog.dismiss();
					mSampleTimeDialog = null;
				}
			});
			mSampleTimeDialog = builder.create();
			mSampleTimeDialog.show();
			
		}
	}
	
	private void showExitPerfusionDialog(){
		if(null != mExitPerfusionDialog){
			mExitPerfusionDialog.dismiss();
			mExitPerfusionDialog = null;
		}
		if(null == mExitPerfusionDialog){
			mExitPerfusionDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(R.string.dialog_msg_perfusion_exit_perfusion);
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_sample_time));
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					onEndPerfusion();
					mExitPerfusionDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mExitPerfusionDialog.dismiss();
					mExitPerfusionDialog = null;
				}
			});
			mExitPerfusionDialog = builder.create();

			mExitPerfusionDialog.show();
			
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
	
	/**start/restart chronometer**/
	private void onChronometerStart(){
		if(!isFlag){
			isFlag = !isFlag;
            if(mRecordTime != 0){
            	//暂停后点击重新计时
            	mChronTimer.setBase(mChronTimer.getBase() + (SystemClock.elapsedRealtime() - mRecordTime));
            }else{
            	
            	mChronTimer.setBase(SystemClock.elapsedRealtime());
            }
            if(mChronTimer.getText().equals("00:00")){
            	mChronTimer.setText("00:00:00");
            }
            mChronTimer.start();
        } 
	}
	
	/**stop chronometer**/
	private void onChronometerStop(){
		 if(isFlag){
			 isFlag = !isFlag;
			 mChronTimer.stop();
             mRecordTime = SystemClock.elapsedRealtime();
         }
	}
	/**reset chronometer*/
	private void onChronometerReset(){
		mChronTimer.stop();
		mChronTimer.setBase(SystemClock.elapsedRealtime());
        mRecordTime = 0;
        if(mChronTimer.getText().equals("00:00")){
        	mChronTimer.setText("00:00:00");
        }
        isFlag = false;
	}
	

	public static class UIHandler extends Handler{
		
		WeakReference<PerfusionActivity> mActivityReference;
		
		public UIHandler(PerfusionActivity mActivity){
			mActivityReference = new WeakReference<PerfusionActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final PerfusionActivity mActivity = mActivityReference.get();
			if(null == mActivity){
				return;
			}
			String detaillMsg= "";
			String errorMsg = "";	
			if(null != mActivity.mAlertTxt){
				detaillMsg = mActivity.mAlertTxt.getText().toString();
			}							
			switch (msg.what) {		
				//警报				
				case Constants.MSG_WARNING_INFO:	
					if(null != mActivity.mSystemAlarmMgr){
						if(!mActivity.mSystemAlarmMgr.isArteryBubAlert() && !mActivity.mSystemAlarmMgr.isVeinBubAlert()
								&& mActivity.mApplication.isArteryHeartConnect() && mActivity.mApplication.isVeinHeartConnect() 
								&& mActivity.mSystemAlarmMgr.getArtPreAlarmStatus()==0 && mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus() == 0
								&& mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus() == 0 && mActivity.mSystemAlarmMgr.getVeinFlowAlarmStatus() == 0
								&&!mActivity.mSystemAlarmMgr.isArteryBackFlow() && !mActivity.mSystemAlarmMgr.isVeinBackFlow() 
								&& !mActivity.mSystemAlarmMgr.isBatteryAlarm()
								&& !mActivity.mSystemAlarmMgr.isLevelAlarm()
								&& !mActivity.mSystemAlarmMgr.isEmergencyStopAlarm()
								&& !mActivity.mSystemAlarmMgr.isAndroidDissAlarm()
								&& !mActivity.mSystemAlarmMgr.isPowerDissAlarm()
								&& !mActivity.mSystemAlarmMgr.isSemiDissAlarm()
								&& !mActivity.mSystemAlarmMgr.isArtSpeedAlarm()&& !mActivity.mSystemAlarmMgr.isVeinSpeedAlarm()
								&&(mActivity.mSystemAlarmMgr.getArtWarnFlowStatus()!= 0 
								||mActivity.mSystemAlarmMgr.getVeinWarnFlowStatus()!= 0
								||mActivity.mSystemAlarmMgr.getArtWarnPreStatus()!= 0
								||mActivity.mSystemAlarmMgr.getVeinWarnPreStatus()!= 0)){
							if(!mActivity.isWarnStatus){
								mActivity.isWarnStatus = true;						
							}
												
							errorMsg = mActivity.mSystemAlarmMgr.getSysWarningMsg();
							if(!detaillMsg.equals(errorMsg) && !errorMsg.equals("")){
								mActivity.mAlertTxt.setText(errorMsg);
								mActivity.mAlertTxt.setTextColor(mActivity.getResources().getColor(R.color.alarm_color));
							}
							if(!mActivity.mApplication.IsMusicPlaying() && null != mActivity.mBinder){
								Log.i(TAG, "play alarm music");
								mActivity.mApplication.setMusicPlaying(true);
								mActivity.mBinder.startPlay(2);			
								mActivity.mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_MEDIUM_PRIORITY_ALARM);
							}
						}	
					}													
					break;
					
				//报警
				case Constants.MSG_ALERT_INFO:
					if(null != mActivity.mSystemAlarmMgr){
						if(mActivity.mSystemAlarmMgr.isSysInterrput()){
							errorMsg = mActivity.getResources().getString(R.string.error_system_communication_interrupt);
						}else{
							if(mActivity.mSystemAlarmMgr.isArteryBubAlert() || mActivity.mSystemAlarmMgr.isVeinBubAlert()
									|| !mActivity.mApplication.isArteryHeartConnect() || !mActivity.mApplication.isVeinHeartConnect()
									|| mActivity.mSystemAlarmMgr.getArtPreAlarmStatus()!=0 
									|| mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus() != 0			
									|| mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus() != 0
									|| mActivity.mSystemAlarmMgr.getVeinFlowAlarmStatus() != 0
									|| !mActivity.mSystemAlarmMgr.isArteryBackFlow()
									|| !mActivity.mSystemAlarmMgr.isVeinBackFlow()
									|| mActivity.mSystemAlarmMgr.getTempStatus()!=0
									|| mActivity.mSystemAlarmMgr.isArtSpeedAlarm()
									|| mActivity.mSystemAlarmMgr.isVeinSpeedAlarm()
									|| mActivity.mSystemAlarmMgr.isBatteryAlarm()
									|| mActivity.mSystemAlarmMgr.isLevelAlarm()
									|| mActivity.mSystemAlarmMgr.isEmergencyStopAlarm()
									|| mActivity.mSystemAlarmMgr.isAndroidDissAlarm()
									|| mActivity.mSystemAlarmMgr.isPowerDissAlarm()
									|| mActivity.mSystemAlarmMgr.isSemiDissAlarm()){
								
								errorMsg = mActivity.mSystemAlarmMgr.getSysAlarmMsg();
								
							}						
						}
						if(!detaillMsg.equals(errorMsg) && !errorMsg.equals("")){
							mActivity.mAlertTxt.setText(errorMsg);
							mActivity.mAlertTxt.setTextColor(mActivity.getResources().getColor(R.color.alert_color));
						}
						if(!mActivity.isAlertStatus){
							Log.i("alarm Activity", "play alert music000");
							mActivity.isAlertStatus = true;								
							mActivity.mBinder.startPlay(3);		
							mActivity.mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_HIGH_PRIORITY_ALARM);	
						}						
						if(!mActivity.mApplication.IsMusicPlaying() && null != mActivity.mBinder ){
							Log.i("Activity", "play alert music");		
							mActivity.mApplication.setMusicPlaying(true);										
						}
					}
					
					break;
					
				case Constants.MSG_NORMAL_INFO:
					if(null != mActivity.mSystemAlarmMgr){
						if(!mActivity.mSystemAlarmMgr.isArteryBubAlert() && !mActivity.mSystemAlarmMgr.isVeinBubAlert()
								&& mActivity.mApplication.isArteryHeartConnect() && mActivity.mApplication.isVeinHeartConnect()
								&& mActivity.mSystemAlarmMgr.getArtPreAlarmStatus() ==0 && mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus() == 0
								&& mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus() == 0&& mActivity.mSystemAlarmMgr.getVeinFlowAlarmStatus() == 0
								&& !mActivity.mSystemAlarmMgr.isArteryBackFlow() && !mActivity.mSystemAlarmMgr.isVeinBackFlow()
								&& !mActivity.mSystemAlarmMgr.isSysInterrput()
								&& mActivity.mSystemAlarmMgr.getArtWarnFlowStatus() == 0
								&& mActivity.mSystemAlarmMgr.getVeinWarnFlowStatus()== 0
								&& mActivity.mSystemAlarmMgr.getArtWarnPreStatus()== 0
								&& mActivity.mSystemAlarmMgr.getVeinWarnPreStatus()== 0
								&& mActivity.mSystemAlarmMgr.getTempStatus() == 0
								&& !mActivity.mSystemAlarmMgr.isArtSpeedAlarm()
								&& !mActivity.mSystemAlarmMgr.isVeinSpeedAlarm()
								&& !mActivity.mSystemAlarmMgr.isBatteryAlarm()
								&& !mActivity.mSystemAlarmMgr.isLevelAlarm()
								&& !mActivity.mSystemAlarmMgr.isEmergencyStopAlarm()
								&& !mActivity.mSystemAlarmMgr.isAndroidDissAlarm()
								&& !mActivity.mSystemAlarmMgr.isPowerDissAlarm()
								&& !mActivity.mSystemAlarmMgr.isSemiDissAlarm()){						

							mActivity.mAlertTxt.setText("");
							mActivity.isWarnStatus = false;
							mActivity.isAlertStatus = false;
							if(null != mActivity.mBinder && mActivity.mApplication.IsMusicPlaying()){
								Log.i(TAG, "stop music");
								Log.i("Activity", "stop music");
								mActivity.mBinder.stopPlay();
								mActivity.mApplication.setMusicPlaying(false);
								mActivity.mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_NORMAL_PRIORITY_ALARM);
							}
						}else {						
							if(mActivity.mSystemAlarmMgr.getArtWarnFlowStatus() == 0
									&& mActivity.mSystemAlarmMgr.getVeinWarnFlowStatus()== 0
									&& mActivity.mSystemAlarmMgr.getArtWarnPreStatus()== 0
									&& mActivity.mSystemAlarmMgr.getVeinWarnPreStatus()== 0){
									mActivity.isWarnStatus = false;//警报解除
							}
							if(!mActivity.mSystemAlarmMgr.isArteryBubAlert() && !mActivity.mSystemAlarmMgr.isVeinBubAlert()
								&& mActivity.mApplication.isArteryHeartConnect() && mActivity.mApplication.isVeinHeartConnect()
								&& mActivity.mSystemAlarmMgr.getArtPreAlarmStatus() == 0 
								&& mActivity.mSystemAlarmMgr.getVeinPreAlarmStatus() == 0
								&& mActivity.mSystemAlarmMgr.getArtFlowAlarmStatus() == 0
								&& mActivity.mSystemAlarmMgr.getVeinFlowAlarmStatus() == 0
								&& mActivity.mSystemAlarmMgr.getTempStatus() == 0
								&& !mActivity.mSystemAlarmMgr.isArteryBackFlow() && !mActivity.mSystemAlarmMgr.isVeinBackFlow()
								&& !mActivity.mSystemAlarmMgr.isSysInterrput() && !mActivity.mSystemAlarmMgr.isArtSpeedAlarm()
								&& !mActivity.mSystemAlarmMgr.isVeinSpeedAlarm()
								&& !mActivity.mSystemAlarmMgr.isBatteryAlarm()
								&& !mActivity.mSystemAlarmMgr.isLevelAlarm()
								&& !mActivity.mSystemAlarmMgr.isEmergencyStopAlarm()
								&& !mActivity.mSystemAlarmMgr.isAndroidDissAlarm()
								&& !mActivity.mSystemAlarmMgr.isPowerDissAlarm()
								&& !mActivity.mSystemAlarmMgr.isSemiDissAlarm()){
								mActivity.isAlertStatus = false;//报警解除
							}
							
							if(mActivity.isWarnStatus && !mActivity.isAlertStatus){		 
								Log.i(TAG, "isWarnStatus----");
								errorMsg = mActivity.mSystemAlarmMgr.getSysWarningMsg();
								mActivity.mAlertTxt.setText(errorMsg);
								mActivity.mAlertTxt.setTextColor(mActivity.getResources().getColor(R.color.alarm_color));
								if(mActivity.mBinder.getCurPlayPosition() != 2){
									mActivity.mBinder.startPlay(2);			
									mActivity.mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_MEDIUM_PRIORITY_ALARM);
								}
							}
							
							if(!mActivity.isWarnStatus && mActivity.isAlertStatus){		
								if(mActivity.mSystemAlarmMgr.isSysInterrput()){
									errorMsg = mActivity.getResources().getString(R.string.error_system_communication_interrupt);
								}else{
									errorMsg = mActivity.mSystemAlarmMgr.getSysAlarmMsg();	
								}
								if(mActivity.mBinder.getCurPlayPosition() != 3){
									mActivity.mBinder.startPlay(3);			
									mActivity.mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_HIGH_PRIORITY_ALARM);
								}
								mActivity.mAlertTxt.setText(errorMsg);
								mActivity.mAlertTxt.setTextColor(mActivity.getResources().getColor(R.color.alert_color));
							}
						}		
					}								
					break;
								
				case Constants.MSG_UPDATE_TIME:
					String sysTime = DateFormatUtil.getNowTime();
					String sysDate = DateFormatUtil.getNowDate();
					mActivity.mDateTimeText.setDateText(sysDate);
					mActivity.mDateTimeText.setTimeText(sysTime);
					break;
					
				case MSG_CANCEL_ASYNCTASK:
					if(null != mActivity.mSQliteAsyncTask){
						mActivity.mSQliteAsyncTask.cancel(true);
						mActivity.mSQliteAsyncTask = null;
					}
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
	
	/***
	 * 广播接收器
	 * @author Administrator
	 *
	 */
	private class SerialMsgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_CNECT)){
				String mArterydisconnect = intent.getStringExtra(SerialMsgConstant.ARTERY_CNECT);
				if(null != mArterydisconnect && null != mApplication
						&& null != mSystemAlarmMgr){
					if(mArterydisconnect.equals("disconnect")){
						if(mApplication.isArteryHeartConnect()){
							mApplication.setArteryHeartConnect(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
							Log.i(TAG, "artery heart disconnect");						
						}	
						
					}else if(mArterydisconnect.equals("connect")){
						if(!mApplication.isArteryHeartConnect()){
							mApplication.setArteryHeartConnect(true);
							mSystemAlarmMgr.setSysInterrput(false);
							Log.i(TAG, "artery heart connect");
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
				}
			
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_CNECT)){
				String mVeindisconnect = intent.getStringExtra(SerialMsgConstant.VEIN_CNECT);
				if(mVeindisconnect != null && null != mApplication 
						&& null != mSystemAlarmMgr){
					if(mVeindisconnect.equals("disconnect")){
						if(mApplication.isVeinHeartConnect()){
							mApplication.setVeinHeartConnect(false);
							Log.i(TAG, "vein heart disconnect");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
						}					
					}else if(mVeindisconnect.equals("connect")){
						if(!mApplication.isVeinHeartConnect()){
							mApplication.setVeinHeartConnect(true);							
							mSystemAlarmMgr.setSysInterrput(false);
							Log.i(TAG, "vein heart connect");
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_SYSTEM_ITERRUPT)){
				boolean isSystemInterrupt = intent.getBooleanExtra(SerialMsgConstant.STSTEM_ITERRUPT, false);
				if(null != mSystemAlarmMgr){
					if(isSystemInterrupt){
						mSystemAlarmMgr.setSysInterrput(true);
						mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
					}else{
						mSystemAlarmMgr.setSysInterrput(false);
						mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
					}
				}

			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_QREAL)){		
				//肝动脉气泡
				String pump1_Qreal = intent.getStringExtra(SerialMsgConstant.ARTERY_BUBBLE);
				
				boolean pump1_stop = intent.getBooleanExtra("pump1_stop", false);
				if(pump1_Qreal != null && null != mApplication
						&& null != mSystemAlarmMgr){
					Log.i(TAG, "pump1_Qreal----" +  pump1_Qreal);
					if(!pump1_Qreal.equals(DefValues.DEDAULT_NULL)&& !mApplication.isStopPump()){	
						boolean isBubbleStop = PreferenceUtil.getInstance(getApplicationContext()).getBooleanValue(SharedConstants.SYSTEM_SETTING_BUBBLESTOP, false);
						if(pump1_stop && isBubbleStop){
							stopSystemPump();
						}
						onHandleBubbleStatus(0,pump1_stop,true,ErrorCode.E_BUBBLE_ARTERY_ALERT_MSG);
						if(pump1_Qreal.equals("255.0")){	
							displayToast(R.string.error_hepatic_artery_line);
						}
					}else{
						if(mSystemAlarmMgr.isArteryBubAlert() && !mApplication.isStopPump()){
							Log.i(TAG, "artery bubble restore nornal");
							onHandleBubbleStatus(0,pump1_stop,false, NormalCode.N_BUBBLE_ONE_MSG);
						}
					}

				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_QREAL)){
				//门静脉气泡
				String pump2_Qreal = intent.getStringExtra(SerialMsgConstant.VEIN_BUBBLE);
				boolean pump2_stop = intent.getBooleanExtra("pump2_stop", false);
				if(null != pump2_Qreal && null != mApplication && null != mSystemAlarmMgr){
					if(!mApplication.isStopPump()){
						if(!pump2_Qreal.equals(DefValues.DEDAULT_NULL)){	
							boolean isBubbleStop = PreferenceUtil.getInstance(getApplicationContext()).getBooleanValue(SharedConstants.SYSTEM_SETTING_BUBBLESTOP, false);
							if(pump2_stop && isBubbleStop){
								stopSystemPump();
							}
							onHandleBubbleStatus(1,pump2_stop,true, ErrorCode.E_BUBBLE_VEIN_ALERT_MSG);					
							if(pump2_Qreal.equals("255.0")){
								displayToast(R.string.error_portal_vein_line);						
							}
						}else{
							//在系统有气泡系统没有停的情况下收到正常才恢复
							if(mSystemAlarmMgr.isVeinBubAlert()&& !mApplication.isStopPump()){
								Log.i(TAG, "vein bubble restore nornal");
								onHandleBubbleStatus(1,pump2_stop,false, NormalCode.N_BUBBLE_TWO_MSG);
							}
						}
					}
				}
				
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL)){
				//肝动脉压力断连
				String pump1_Preal = intent.getStringExtra(SerialMsgConstant.ARTERY_PREAL);
				if(null != pump1_Preal && null != mSystemAlarmMgr){
					if(pump1_Preal.equals("error1")){						
						if(mSystemAlarmMgr.getArtPreAlarmStatus() != 1){
							mSystemAlarmMgr.setArtPreAlarmStatus(1);																				
						}	
						mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
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
						//门静脉压力断连					
						if(mSystemAlarmMgr.getVeinPreAlarmStatus() != 1){
							mSystemAlarmMgr.setVeinPreAlarmStatus(1);													
						}
						mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
					}else{
						
						if(mSystemAlarmMgr.getVeinPreAlarmStatus() != 0){							
							mSystemAlarmMgr.setVeinPreAlarmStatus(0);	
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
										
					}
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL)){
				String pump1_Freal = intent.getStringExtra(SerialMsgConstant.ARTERY_FREAL);
				if(null != pump1_Freal && null != mApplication && null != mSystemAlarmMgr){
					if(pump1_Freal.equals("error1")){
						//肝动脉流量断连
						if(mSystemAlarmMgr.getArtFlowAlarmStatus() != 1){
							mSystemAlarmMgr.setArtFlowAlarmStatus(1);					
							Log.i("BackService", "PerfusionActivity----disconnect artery flow");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
						}
						
					}else if(pump1_Freal.equals("error2")){
						if(mSystemAlarmMgr.getArtFlowAlarmStatus() != 2){
							mSystemAlarmMgr.setArtFlowAlarmStatus(2);						
							Log.i("BackService", "PerfusionActivity---normal artery flow");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
						}
						
					}else{
						if(mSystemAlarmMgr.getArtFlowAlarmStatus()!= 0){
							mSystemAlarmMgr.setArtFlowAlarmStatus(0);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}else{
							//反流
							float FReal_one = StringUtil.convertToFloat(pump1_Freal,0.0f);
							if(FReal_one < 0.0f){
								if(!mSystemAlarmMgr.isArteryBackFlow() && !mApplication.isStopPump()){
									mSystemAlarmMgr.setArteryBackFlow(true);
									mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
								}
							}else{
								if(mSystemAlarmMgr.isArteryBackFlow()&& !mApplication.isStopPump()){
									mSystemAlarmMgr.setArteryBackFlow(false);
									mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
								}						
							}
						}
					}
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL)){
				String pump2_Freal = intent.getStringExtra(SerialMsgConstant.VEIN_FREAL);
				if(null != pump2_Freal && null != mApplication && null != mSystemAlarmMgr){
					if(pump2_Freal.equals("error1")){
						//门静脉流量断连
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() != 1){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(1);
						}
						mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
					}else if(pump2_Freal.equals("error2")){
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() != 2){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(2);
						}
						mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
					}else{
						if(mSystemAlarmMgr.getVeinFlowAlarmStatus() !=0){
							mSystemAlarmMgr.setVeinFlowAlarmStatus(0);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}else{
							//没有断连的情况，反流情况
							float FReal_two = StringUtil.convertToFloat(pump2_Freal,0.0f);
							if(FReal_two < 0.0f){
								if(!mSystemAlarmMgr.isVeinBackFlow() && !mApplication.isStopPump()){
									//在没有停机出现反流才报警停机
									mSystemAlarmMgr.setVeinBackFlow(true);
									mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
								}
							}else{
								if(mSystemAlarmMgr.isVeinBackFlow() && !mApplication.isStopPump()){
									mSystemAlarmMgr.setVeinBackFlow(false);
									mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
								}
							}
						}
						
					}
				}
							
			}else if(action.equals(BroadcastActions.ACTION_ALARM_PUMP_ONE_SPEED)){
				boolean isArtSpeedAlarm = intent.getBooleanExtra(SerialMsgConstant.ARTERY_SPEED_ALARM, false);
				if(null != mSystemAlarmMgr){
					if(isArtSpeedAlarm){
						if(!mSystemAlarmMgr.isArtSpeedAlarm()){
							mSystemAlarmMgr.setArtSpeedAlarm(true);
							insertAlarmMessage(ErrorCode.E_SPEED_ARTERY_ABNORMAL, "error");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
						}
					}else{
						//报警解除
						if(mSystemAlarmMgr.isArtSpeedAlarm()){
							mSystemAlarmMgr.setArtSpeedAlarm(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
						
				}
	
			}else if(action.equals(BroadcastActions.ACTION_ALARM_PUMP_TWO_SPEED)){
				boolean isVeinSpeedAlarm = intent.getBooleanExtra(SerialMsgConstant.VEIN_SPEED_ALARM, false);
				if(null != mSystemAlarmMgr){
					if(isVeinSpeedAlarm){
						if(!mSystemAlarmMgr.isVeinSpeedAlarm()){
							mSystemAlarmMgr.setVeinSpeedAlarm(true);
							insertAlarmMessage(ErrorCode.E_SPEED_VEIN_ABNORMAL, "error");
							mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
						}
					}else{
						//报警解除
						if(mSystemAlarmMgr.isVeinSpeedAlarm()){
							mSystemAlarmMgr.setVeinSpeedAlarm(false);
							mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
						}
					}
				}

			}else if(action.equals(BroadcastActions.RECEIVE_REMOTE_CTRL_SPEED)){
				int sysflag = intent.getIntExtra("sysflag", 1);
				if(sysflag == 0){
					if(mApplication.isStopPump()){
						//停机重启
						restartSystemPump();
					}					
				}else{
					if(!mApplication.isStopPump()){
						//正常停机
						stopSystemPump();
					}
					
				}
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_BATTERY_STATE)){
				String battery_voltage = intent.getStringExtra(SerialMsgConstant.BATTERY_VOLTAGE);//电池电压
				int battery_level = intent.getIntExtra(SerialMsgConstant.BATTERY_LEVEL,0);//电池电量
				checkBatteryState(battery_level);
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_DEVICE_STATE)){
				
				int liquid_level_state = intent.getIntExtra(SerialMsgConstant.LIQUID_LEVEL,0);//液位状态
				int battery_state = intent.getIntExtra(SerialMsgConstant.BATTERY_STATE,1);//充电开关 0:异常，1：正常
				int charging_state = intent.getIntExtra(SerialMsgConstant.CHARGING_STATE,0);//充电状态 0没有充电 1充电完成  2正在充电
				int power_state = intent.getIntExtra(SerialMsgConstant.POWER_STATE,0);//是否使用电源  0表示AC 1表示 电池
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
				
			}else if(action.equals(BroadcastActions.RECEIVE_REMOTE_CTRL_ARTERY_SPEED)){
				if(mApplication.isStopPump()){
					int artery_tarSpeed  =  intent.getIntExtra("arterySpeedflag", 0);
					//并保存
					if(artery_tarSpeed >= 0 && artery_tarSpeed <= 5000){
						PreferenceUtil.getInstance(getApplicationContext())
							.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, artery_tarSpeed);
						Log.i("RemoteCtrl", "sendArteryTarSpeedMsg----" + artery_tarSpeed);
						sendArteryTarSpeedMsg(artery_tarSpeed);
					}
									
				}

			}else if(action.equals(BroadcastActions.RECEIVE_REMOTE_CTRL_VEIN_SPEED)){
				if(mApplication.isStopPump()){
					int vein_tarSpeed  =  intent.getIntExtra("veinSpeedflag", 0);
					if(vein_tarSpeed >= 0 && vein_tarSpeed <= 5000){
						PreferenceUtil.getInstance(getApplicationContext())
							.setValueByName(SharedConstants.TARGET_VEIN_SPEED, vein_tarSpeed);
						Log.i("RemoteCtrl", "sendArteryTarSpeedMsg----" + vein_tarSpeed);
						sendVeinTarSpeedMsg(vein_tarSpeed);
					}
					
				}
			}
			
		}
	}
	
	
	private static class SQLiteAsyncTask extends AsyncTask<BloodGasSamplingBean, Integer, Long>{
		private BloodGasSamplingBean mBloodGasSampleBean;		
		private WeakReference<PerfusionActivity> weakAty;
		
        public SQLiteAsyncTask(PerfusionActivity activity,BloodGasSamplingBean bloodGasBean){
            weakAty = new WeakReference<PerfusionActivity>(activity);
            this.mBloodGasSampleBean = bloodGasBean;
        }

		@Override
		protected Long doInBackground(BloodGasSamplingBean... params) {
			// TODO Auto-generated method stub
			long result = -1;
			if(isCancelled()){
				return result;
			}
			
			PerfusionActivity mActivity = weakAty.get();
			if(null != mActivity){
				result = mActivity.mDatabaseMgr.saveBloodGasSamplingTime(mBloodGasSampleBean);
				Log.i(TAG, "--result--" + result);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			PerfusionActivity mActivity = weakAty.get();
			if(null != mActivity){
				mActivity.mUIHandler.sendEmptyMessage(MSG_CANCEL_ASYNCTASK);
			}
			
		}		
	}

	//结束灌注，退出灌注主页面
	private void onEndPerfusion() {
		// TODO Auto-generated method stub
		//停止灌注
		if(null != mBinder){
			mBinder.restoreVariable();
			mBinder.sendSerialCmdMsg(SerialPortInstruct.CLOSE_TEMP_CTRL);
			mBinder.setPerfusionStatus(false);
			sendStopSysMsg();
			//清除报警
			mBinder.stopPlay();
			mApplication.setMusicPlaying(false);
			mBinder.sendSerialCmdMsg(SerialPortInstruct.SET_NORMAL_PRIORITY_ALARM);
			isWarnStatus = false;
						
		}

		PreferenceUtil.getInstance(getApplicationContext())
			.setValueByName(SharedConstants.IS_CRASH, false);
		if(null != mApplication){
			mApplication.restoreVariables();
		}
		
		Intent endIntent = new Intent(PerfusionActivity.this, PowerOnActivity.class);
		startActivity(endIntent);
		this.finish();
	}
	
	/***
	 * @param pumptype: artery :0 vein:1
	 */
	@Override
	public void onSetZeroPressure(int pumptype) {
		// TODO Auto-generated method stub
		String zeroPreMsg = "";
		if(pumptype == 0){
			zeroPreMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_PRESSURE_ONE, 0,"1");

		}else if(pumptype == 1){
			zeroPreMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_PRESSURE_TWO, 0,"1");				
		}
		if(null != mBinder && !StringUtil.isEmpty(zeroPreMsg)){
			mBinder.sendSerialCmdMsg(zeroPreMsg);
		}
	}

	@Override
	public void onSetZeroFlow(int pumptype) {
		// TODO Auto-generated method stub
		String zeroFlowMsg = "";
		if(pumptype == 0){
			zeroFlowMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_FLOW_ONE, 0,"3");

		}else if(pumptype == 1){
			zeroFlowMsg = StringUtil.appendMsg(SerialPortInstruct.SET_ZERO_FLOW_TWO, 0,"3");				
		}
		if(null != mBinder && !StringUtil.isEmpty(zeroFlowMsg)){
			mBinder.sendSerialCmdMsg(zeroFlowMsg);
		}
	}
	
	/**
	 * 灌注设置
	 * 灌注模式和灌注目标值修改
	 * 肝动脉模式：mArtMode = 0：恒压，mVeinMode = 1：搏动
	 * 门静脉模式：mVeinMode = 2：恒压；mVeinMode = 3：恒流
	 *        
	 */
	@Override
	public void onFinishSetPerfison(boolean isArtSet, boolean isVeinSet,boolean isArtModeChange, boolean isVeinModeChange) {
		// TODO Auto-generated method stub
		if(mApplication.isStopPump()){
			if(isArtModeChange){
				mArtMode = PreferenceUtil.getInstance(getApplicationContext())
						.getIntValue(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
				if(null != mBinder){
					mBinder.setCurArtPerfusionMode(mArtMode);
				}
			}
			if(isVeinModeChange){
				mVeinMode = PreferenceUtil.getInstance(getApplicationContext())
						.getIntValue(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
				if(null != mBinder){
					mBinder.setCurVeinPerfusionMode(mVeinMode);
				}
			}
			if(isArtSet || isVeinSet){
				restartSystemPump();
			}

		}else{
			//泵是正常的
			if(isArtModeChange){
				//肝动脉灌注模式修改
				mArtMode = PreferenceUtil.getInstance(getApplicationContext())
						.getIntValue(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
				sendArtPerfusionMsg();
				//修改模式
				if(null != mBinder){
					mBinder.setCurArtPerfusionMode(mArtMode);
				}
			}else {
				//模式未改只是灌注值修改
				if(isArtSet){
					sendArtPerfusionMsg();				
				}
			}
			
			if(isVeinModeChange){
				//门静脉灌注模式修改
				mVeinMode = PreferenceUtil.getInstance(getApplicationContext())
						.getIntValue(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
				sendVeinPerfusionMsg();
				if(null != mBinder){
					mBinder.setCurVeinPerfusionMode(mVeinMode);
				}
			}else{
				//门静脉灌注模式没有修改，灌注目标值修改
				if(isVeinSet){
					sendVeinPerfusionMsg();
				}
			}
		}
		if(null != mPerfusionFragment){
			mPerfusionFragment.onNotifyPerfusionSetting();
		}
	}
	
	/***
	 * setting——alertsetting
	 * 泵速极限值设置
	 */
	@Override
	public void onFinishSetSpeedLimit(boolean isArtSet, boolean isVeinSet) {
		Log.i(TAG, "onFinishSetSpeedLimit-isArtSet---" + isArtSet + "  isVeinSet---" + isVeinSet);
		// TODO Auto-generated method stub
		if(isArtSet){
			if(mSystemAlarmMgr.isArtSpeedAlarm()){
				mSystemAlarmMgr.setArtSpeedAlarm(false);
				mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
			}
		}
		
		if(isVeinSet){
			if(mSystemAlarmMgr.isVeinSpeedAlarm()){
				mSystemAlarmMgr.setVeinSpeedAlarm(false);
				mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
			}
		}
		sendLimitSpeedMsg();
	}
	
	@Override
	public void onNetWorkChange(boolean isConnected, int type) {
		// TODO Auto-generated method stub
		if(isConnected){
			//wifiImg.setImageResource(R.drawable.wifi_on);
		}else {
			//wifiImg.setImageResource(R.drawable.wifi_off);
		}
	}

	@Override
	public void onPressureStatus(int pumptype, boolean isAlarm, int errorCode) {
		// TODO Auto-generated method stub
		if(!isAlarm){
			mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
			
		}else{			
			mUIHandler.sendEmptyMessage(Constants.MSG_WARNING_INFO);
		}
		
	}

	@Override
	public void onFlowStatus(int pumptype, boolean isAlarm, int errorCode) {
		// TODO Auto-generated method stub
		
		if(!isAlarm){
			mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
		}else{
			mUIHandler.sendEmptyMessage(Constants.MSG_WARNING_INFO);
		}
	}
	
	/***
	 * 温度状态/报警
	 */
	@Override
	public void onTempStatus(int temp_status, boolean isAlarm) {
		// TODO Auto-generated method stub
		//报警		
		if(isAlarm){
			Log.i(TAG,"onTempStatus"+ "  isAlarm" + isAlarm);
			if(mSystemAlarmMgr.getTempStatus()!= 0){
				if(temp_status == 1){
					insertAlarmMessage(ErrorCode.E_ABNORMAL_TEMP, "error");
				}else if(temp_status == 2){
					insertAlarmMessage(ErrorCode.E_OVER_TEMP, "error");
				}
				mUIHandler.sendEmptyMessage(Constants.MSG_ALERT_INFO);
			}
		}else{
			mUIHandler.sendEmptyMessage(Constants.MSG_NORMAL_INFO);
		}
	}


	@Override
	public void onFinishSaveAlert() {
		// TODO Auto-generated method stub
		if(null != mPerfusionFragment){
			mPerfusionFragment.onNotifyAlertSetting();
		}
	}	
}
