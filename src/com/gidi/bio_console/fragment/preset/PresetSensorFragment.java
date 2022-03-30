package com.gidi.bio_console.fragment.preset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.activity.SystemSettingActivity;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.view.PresetStatusView;


public class PresetSensorFragment extends BaseFragment implements OnClickListener{

	private Context mContext;
	private ImageView next_btn;
	private ImageView mLogOutputImg;
	private ImageView mSensorBgImg;//灌注的传感器原理图
	private TextView sensor_artPre_txt;
	private TextView sensor_veinPre_txt;
	private TextView sensor_veinflow_txt;
	private TextView sensor_artflow_txt;
	private TextView mLSensorTitleTv;
	private TextView mRLSensorTitleTv;
	private PresetStatusView mSensorPre_A;
	private PresetStatusView mSensorPre_B;
	private PresetStatusView mSensorFlow_A;
	private PresetStatusView mSensorFlow_B;
	int mArtertyPreStatus = 1;//0:normal,1:disconnect,
	int mVeinPreStatus = 1;//0:normal,1:disconnect,
	int mArtertyFlowStatus = 1;//0:normal,1:disconnect,2:abnormal
	int mVeinFlowStatus = 1;//0:normal,1:disconnect,2:abnormal
	private String perfusion_system ;
	
	public interface ChangeTabListener {
		void next();		
	}
	
	private ChangeTabListener mTabListener;
	
	public void setOnChangeTabListener(ChangeTabListener listener){
		this.mTabListener = listener;
	}
	
				
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getBaseActivity();
		registerReceiver();
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();		
	}


		
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver();
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext = null;
	}


	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_preset_sensor;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mSensorBgImg = (ImageView)rootView.findViewById(R.id.perfusion_sensor_img);
		mLSensorTitleTv = (TextView)rootView.findViewById(R.id.left_pre_tv);
		mRLSensorTitleTv = (TextView)rootView.findViewById(R.id.right_pre_tv);
		next_btn = (ImageView)rootView.findViewById(R.id.next_btn);
		mSensorPre_A = (PresetStatusView)rootView.findViewById(R.id.rl_sensor_pressure_artery);
		mSensorPre_B = (PresetStatusView)rootView.findViewById(R.id.rl_sensor_pressure_vein);
		mSensorFlow_A = (PresetStatusView)rootView.findViewById(R.id.rl_sensor_flow_artery);
		mSensorFlow_B = (PresetStatusView)rootView.findViewById(R.id.rl_sensor_flow_vein);
		
		sensor_artPre_txt = (TextView)rootView.findViewById(R.id.sensor_pressure_artery_txt);
		sensor_veinPre_txt = (TextView)rootView.findViewById(R.id.sensor_pressure_vein_txt);
		sensor_artflow_txt = (TextView)rootView.findViewById(R.id.sensor_art_flow_txt);
		sensor_veinflow_txt = (TextView)rootView.findViewById(R.id.sensor_vein_flow_txt);
		mLogOutputImg = (ImageView)rootView.findViewById(R.id.output_img);
		
		perfusion_system = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getStringValue(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
		if(perfusion_system.equals(Constants.LIVER_PERFUSION_STYSTEM)){
			mSensorBgImg.setImageResource(R.drawable.sensor_liver);
			mSensorPre_A.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_artery_pressure));
			mSensorPre_B.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_vein_pressure));
			mSensorFlow_A.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_artery_flow));
			mSensorFlow_B.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_vein_flow));
			mLSensorTitleTv.setText(R.string.preset_pvp);
			mRLSensorTitleTv.setText(R.string.preset_hap);
		}else{
			mSensorBgImg.setImageResource(R.drawable.sensor_kidney);
			mSensorPre_A.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_left_artery_pressure));
			mSensorPre_B.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_right_artery_pressure));
			mSensorFlow_A.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_left_artery_flow));
			mSensorFlow_B.setContentText(mContext.getResources()
					.getString(R.string.preset_sensor_right_artery_flow));
			mLSensorTitleTv.setText(R.string.preset_rap);
			mRLSensorTitleTv.setText(R.string.preset_lap);
	
		}		
		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		if(mArtertyFlowStatus == 1|| mVeinFlowStatus == 1 ||
				mArtertyPreStatus == 1 || mVeinPreStatus == 1){
			next_btn.setBackgroundResource(R.drawable.oval_next_btn_unpressed_bg);
			next_btn.setVisibility(View.INVISIBLE);
			//next_btn.setText(R.string.next);
		}
		next_btn.setOnClickListener(this);
		mLogOutputImg.setOnClickListener(this);
	}
	
	private void registerReceiver(){
		IntentFilter intentFilter = new IntentFilter();
		//监听压力传感器是否断链
		intentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);
		intentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);
		//监听压力传感器流量传感器断链
		intentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);
		intentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);

		mSerialmMsgReceiver = new SerialmMsgReceiver();
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mSerialmMsgReceiver, intentFilter);
	}
	
	private void unregisterReceiver(){
		if(null != mSerialmMsgReceiver){
			LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSerialmMsgReceiver);
			mSerialmMsgReceiver = null;
		}
	}
	
	
	/**当门静脉心跳消息断连**/
	public void breakArtery(){
		breakArtPre();
		breakArtFlow();
		mArtertyPreStatus = 1;
		mArtertyFlowStatus = 1;
		Log.i("PresetSensorFragment", "breakArtery mArtertyFlowStatus --" +   mArtertyFlowStatus);
		Log.i("PresetSensorFragment", "breakArtery mArtertyPreStatus --" + mArtertyPreStatus);
		if(mArtertyFlowStatus != 1 && mVeinFlowStatus != 1 
				&&mArtertyPreStatus != 1 && mVeinPreStatus != 1){
			
			next_btn.setVisibility(View.VISIBLE);
			next_btn.setEnabled(true);
			//next_btn.setBackgroundResource(R.drawable.preset_btn_zero_enable);
			next_btn.setBackgroundResource(R.drawable.oval_next_btn_bg_selector);
		}else{
			
			next_btn.setVisibility(View.INVISIBLE);
			next_btn.setEnabled(false);
			next_btn.setBackgroundResource(R.drawable.oval_next_btn_unpressed_bg);
		}
	}
	
	public void normalArtery(){
		mArtertyPreStatus = 0;
		mArtertyFlowStatus = 0;
		normalArtPre();
		normalArtFlow();
		checkSensor();
	}
	

	//activity 调用在断连的情况下
	public void breakVein(){
		breakVeinFlow();
		breakVeinPre();
		mVeinFlowStatus = 1;
		mVeinPreStatus = 1;
		if(mArtertyFlowStatus != 1 && mVeinFlowStatus != 1 
				&&mArtertyPreStatus != 1 && mVeinPreStatus != 1){
			next_btn.setVisibility(View.VISIBLE);	
			next_btn.setEnabled(true);
			next_btn.setBackgroundResource(R.drawable.oval_next_btn_bg_selector);			
		}else{
			next_btn.setVisibility(View.INVISIBLE);
			next_btn.setEnabled(false);
			next_btn.setBackgroundResource(R.drawable.oval_next_btn_unpressed_bg);
		}
	}
	
	
	public void normalVein(){
		mVeinFlowStatus = 0;
		mVeinPreStatus = 0;
		normalVeinFlow();
		normalVeinPre();
	}
	

	
	private void checkStatusCircle(){
		if(mArtertyPreStatus != 1){
			sensor_artPre_txt.setBackgroundResource(R.drawable.circle_bg_green);
		}else{
			sensor_artPre_txt.setBackgroundResource(R.drawable.circle_bg_red);
		}
		
		if(mVeinPreStatus != 1){
			sensor_veinPre_txt.setBackgroundResource(R.drawable.circle_bg_green);
		}else{
			sensor_veinPre_txt.setBackgroundResource(R.drawable.circle_bg_red);
		}
		
		if(mArtertyFlowStatus!= 1){
			sensor_artflow_txt.setBackgroundResource(R.drawable.circle_bg_green);
		}else{
			sensor_artflow_txt.setBackgroundResource(R.drawable.circle_bg_red);
		}
		
		if(mVeinFlowStatus != 1){
			sensor_veinflow_txt.setBackgroundResource(R.drawable.circle_bg_green);
		}else{
			sensor_veinflow_txt.setBackgroundResource(R.drawable.circle_bg_red);
		}
	}

	private void breakArtPre(){
		sensor_artPre_txt.setBackgroundResource(R.drawable.circle_bg_red);
	}
	
	private void breakVeinPre(){
		sensor_veinPre_txt.setBackgroundResource(R.drawable.circle_bg_red);
	}
	
	private void breakArtFlow(){
		sensor_artflow_txt.setBackgroundResource(R.drawable.circle_bg_red);
	}
	
	private void breakVeinFlow(){
		sensor_veinflow_txt.setBackgroundResource(R.drawable.circle_bg_red);
	}
	
	private void normalArtPre(){
		sensor_artPre_txt.setBackgroundResource(R.drawable.circle_bg_green);
		checkSensor();
	}
	
	private void normalVeinPre(){
		sensor_veinPre_txt.setBackgroundResource(R.drawable.circle_bg_green);
		checkSensor();
	}
	
	private void normalArtFlow(){
		sensor_artflow_txt.setBackgroundResource(R.drawable.circle_bg_green);
		checkSensor();
	}
	
	private void normalVeinFlow(){
		sensor_veinflow_txt.setBackgroundResource(R.drawable.circle_bg_green);
		checkSensor();
	}

	
	private void checkSensor(){
		checkStatusCircle();
		if(mArtertyFlowStatus != 1 && mVeinFlowStatus != 1 
				&& mArtertyPreStatus != 1 && mVeinPreStatus != 1){
			next_btn.setVisibility(View.VISIBLE);
			next_btn.setEnabled(true);
			next_btn.setBackgroundResource(R.drawable.oval_next_btn_bg_selector);			
		}else{
			next_btn.setVisibility(View.INVISIBLE);
			next_btn.setEnabled(false);
			next_btn.setBackgroundResource(R.drawable.oval_next_btn_unpressed_bg);
		}
	}
	
	private void outputPerfusionLog(){
		Intent intent = new Intent(getActivity(), SystemSettingActivity.class);
		startActivity(intent);	
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next_btn:
			if(mArtertyFlowStatus != 1 && mVeinFlowStatus != 1 
			&& mArtertyPreStatus != 1 && mVeinPreStatus != 1){
				if(null != mTabListener){
					mTabListener.next();
				}	
			}
		
			break;
		case R.id.output_img:
			outputPerfusionLog();
			break;

		default:
			break;
		}
	}
	
	private SerialmMsgReceiver mSerialmMsgReceiver;
	private class SerialmMsgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL)){
				String pump1_Preal = intent.getStringExtra(SerialMsgConstant.ARTERY_PREAL);				
				if(null != pump1_Preal){
					if(pump1_Preal.equals("error1")){
						if(mArtertyPreStatus != 1){
							mArtertyPreStatus = 1;
							breakArtPre();
						}
					}else {
						if(mArtertyPreStatus != 0){
							mArtertyPreStatus = 0;
							normalArtPre();
						}
					}
				}
				
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL)){
				String pump2_Preal = intent.getStringExtra(SerialMsgConstant.VEIN_PREAL);
				if(null != pump2_Preal){
					if(pump2_Preal.equals("error1")){
						if(mVeinPreStatus != 1){
							mVeinPreStatus = 1;
							breakVeinPre();
						}
					}else {
						if(mVeinPreStatus != 0){
							mVeinPreStatus = 0;
							normalVeinPre();
						}
					}
				}
								
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL)){
				String pump1_Freal = intent.getStringExtra(SerialMsgConstant.ARTERY_FREAL);				
				if(null != pump1_Freal){
					if(pump1_Freal.equals("error1")){
						if(mArtertyFlowStatus != 1){
							mArtertyFlowStatus = 1;
							breakArtFlow();
						}
					}else if(pump1_Freal.equals("error2")){
						if(mArtertyFlowStatus != 2){
							mArtertyFlowStatus = 2;
						}
						checkSensor();
					}else {
						if(mArtertyFlowStatus != 0){
							mArtertyFlowStatus = 0;
							normalArtFlow();
						}
					}
				}			
			}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL)){
				String pump2_Freal = intent.getStringExtra(SerialMsgConstant.VEIN_FREAL);
				if(null != pump2_Freal){
					if(pump2_Freal.equals("error1")){
						if(mVeinFlowStatus != 1){
							mVeinFlowStatus = 1;
						}
						breakVeinFlow();
					}else if(pump2_Freal.equals("error2")){
						if(mVeinFlowStatus != 2){
							mVeinFlowStatus = 2;
						}
						checkSensor();
					}else{
						if(mVeinFlowStatus != 0){
							mVeinFlowStatus = 0;
						}
						normalVeinFlow();
					}					
				}

			}
		}
		
	}


}
