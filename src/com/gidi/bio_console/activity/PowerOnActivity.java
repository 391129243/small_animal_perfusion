package com.gidi.bio_console.activity;


import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseActivity;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.kidney.KidneyPresetActivity;
import com.gidi.bio_console.listener.OnNetWorkChangedListener;
import com.gidi.bio_console.receiver.NetWorkChangeReceiver;
import com.gidi.bio_console.utils.PreferenceUtil;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
/**只有的启动页面**/
public class PowerOnActivity extends BaseActivity implements  OnNetWorkChangedListener,OnClickListener{

	private static String TAG = "PowerOnActivity"	;
	private NetWorkChangeReceiver netWorkReceiver;
	private BioConsoleApplication mApplication;
	private PowerOnActivity mActivity;
	private AudioManager mAudioManager;
	private TextView mModeLiverTv;
	private TextView mModeKidneyTv;
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_power_on;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--PowerOnActivity onCreate--");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        addActivity();
        initReceiver();
        initData(); 	
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
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();		
		 Log.i(TAG, "--PowerOnActivity onDestroy--");
		removeActivity();
		unregisterReceiver();		
	}
    
	private void initReceiver(){
       // 网络状态改变监听
        registerReceiver(netWorkReceiver = new NetWorkChangeReceiver(), new IntentFilter(Constants.NETWORK_CONNECTION_CHANGE));
        netWorkReceiver.registerListener(this);
	}
	
	private void unregisterReceiver(){
		if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
            netWorkReceiver.unregisterListener(this);
            netWorkReceiver = null;
        }
	}
	
	

	@Override
	public void onNetWorkChange(boolean isConnected, int type) {
		// TODO Auto-generated method stub
		Log.i(TAG, "--isConnected--" + isConnected);
	}


	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mModeLiverTv = (TextView)findViewById(R.id.mode_liver_persion_tv);
		mModeKidneyTv = (TextView)findViewById(R.id.mode_kidney_persion_tv);
	}
	
	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mModeLiverTv.setOnClickListener(this);
		mModeKidneyTv.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mAudioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,maxVolume , AudioManager.FLAG_PLAY_SOUND);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND);
		
	}
	
	private void addActivity(){
		mActivity = this;
		mApplication = (BioConsoleApplication)getApplication();
		mApplication.addActivity(mActivity);
	}
	
	private void removeActivity(){
		mApplication.removeActivity(mActivity);
		mActivity = null;
		mApplication = null;
	}
	
	private void finishActivity(){
		
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
        finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mode_liver_persion_tv:
			Intent liverIntent = new Intent(PowerOnActivity.this, SystemPresetActivity.class);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
			startActivity(liverIntent);
			finishActivity();
			break;
		case R.id.mode_kidney_persion_tv:
			Intent kidneyIntent = new Intent(PowerOnActivity.this, KidneyPresetActivity.class);	
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.PERFUION_STYSTEM, Constants.KIDNEY_PERFUSION_STYSTEM);
			startActivity(kidneyIntent);
			finishActivity();
			break;
		default:
			break;
		}
	}
}
