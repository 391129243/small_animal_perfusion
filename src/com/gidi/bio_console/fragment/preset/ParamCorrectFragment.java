package com.gidi.bio_console.fragment.preset;

import java.lang.ref.WeakReference;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;


/**选择灌注模式参数校正目标压力调零 **/
public class ParamCorrectFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = "ParamCorrectFragment";
	private ImageView mCompleteBtn;
	private TextView mArteryPreTxt;
	private TextView mVeinPreTxt;
	private TextView mAZeroTitleTv;
	private TextView mVZeroTitleTv;
	private TextView mAZeroTitle_ok_Txt;
	private TextView mVZeroTitle_ok_Txt;
	private TextView mVeinZeroImg;
	private TextView mArteryZeroImg;
	private SerialMsgReceiver mSerialMsgReceiver;
	private UIHandler mUIHandler;
	private boolean mIsArteryZeroSuc = false;
	private boolean mIsVeinZeroSuc = false;

    private OnParamCorrectListener mListener;
    
	public interface OnParamCorrectListener{
		void completeParamCorrect();
		void sendArteryZeroPreMsg();
		void sendVeinZeroPreMsg();
	}
	
	public void setOnParamCorrectListener(OnParamCorrectListener listener){
		this.mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initHanddler();
		registerReceiver();
	}
	
	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_preset_zero_pressure;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mCompleteBtn = (ImageView)rootView.findViewById(R.id.param_correct_complete_btn);
		mArteryPreTxt = (TextView)rootView.findViewById(R.id.param_hepatic_artery_zero_pressure_txt);
		mVeinPreTxt = (TextView)rootView.findViewById(R.id.param_vein_zero_pressure_txt);
		mAZeroTitleTv = (TextView)rootView.findViewById(R.id.artery_zero_pressure_title);
		mVZeroTitleTv = (TextView)rootView.findViewById(R.id.vein_zero_pressure_title);
		mAZeroTitle_ok_Txt = (TextView)rootView.findViewById(R.id.mAZeroTitleTxt_ok);
		mVZeroTitle_ok_Txt = (TextView)rootView.findViewById(R.id.mVZeroTitleTxt_ok);
		mArteryZeroImg = (TextView)rootView.findViewById(R.id.sys_preset_param_artery_zero_img);
		mVeinZeroImg = (TextView)rootView.findViewById(R.id.sys_preset_param_vein_zero_img);
		String perfusion_system = PreferenceUtil.getInstance(this.getActivity().getApplicationContext())
				.getStringValue(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
		if(perfusion_system.equals(Constants.LIVER_PERFUSION_STYSTEM)){
			mAZeroTitleTv.setText(R.string.hepatic_artery);
			mVZeroTitleTv.setText(R.string.portal_vein);
		}else{
			mAZeroTitleTv.setText(R.string.left_kidney_artery);
			mVZeroTitleTv.setText(R.string.right_kidney_artery);
		}		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub		
		mCompleteBtn.setOnClickListener(this);
		mArteryZeroImg.setOnClickListener(this);
		mVeinZeroImg.setOnClickListener(this);
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
	}
	
	

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver();
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
		}
	}
	
	private void registerReceiver(){
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_ZERO_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_ZERO_PUMP_TWO_PREAL);
		mSerialMsgReceiver = new SerialMsgReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSerialMsgReceiver, mIntentFilter);
	}

	private void unregisterReceiver(){
		if(null != mSerialMsgReceiver){
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSerialMsgReceiver);
			mSerialMsgReceiver = null;	
		}
	}
	
	private void initHanddler(){
		mUIHandler = new UIHandler(this);
	}


	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.param_correct_complete_btn:
			boolean isFirstZeroPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getBooleanValue(SharedConstants.IS_PRESSURE_ZERO_FIRST, true);
			if(isFirstZeroPre){
				 PreferenceUtil.getInstance(getActivity().getApplicationContext())
				 .setValueByName(SharedConstants.IS_PRESSURE_ZERO_FIRST, false);
			}
			if(null != mListener){
				mListener.completeParamCorrect();
			}
			break;
			
		case R.id.sys_preset_param_artery_zero_img:
			
			if(null != mListener){
				mListener.sendArteryZeroPreMsg();
			}
			break;
			
		case R.id.sys_preset_param_vein_zero_img:
			if(null != mListener){
				mListener.sendVeinZeroPreMsg();
			}
			break;
			
		default:
			break;
		}
	}
	

	private static class UIHandler extends Handler{
		
		private final WeakReference<ParamCorrectFragment> mFragReference;
		public UIHandler(ParamCorrectFragment fragment) {  
			mFragReference = new WeakReference<ParamCorrectFragment>(fragment);  
	    }
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(null == mFragReference.get()){
				return;
			}
			switch (msg.what) {
			
			case Constants.MSG_ZERO_PRESSURE_ONE:
				Log.i(TAG, "zero_pressure_one");
				String result = (String)msg.obj;
				if(null != mFragReference.get().mAZeroTitle_ok_Txt){
					if(result.equals("succe")){
						if(!mFragReference.get().mIsArteryZeroSuc){
							mFragReference.get().displayToast(R.string.success_zero_hepatic_artery_pressure);
							mFragReference.get().mIsArteryZeroSuc = true;
							mFragReference.get().mAZeroTitle_ok_Txt.setVisibility(View.VISIBLE);
						}
					}else if(result.endsWith("erro")){
						mFragReference.get().displayToast(R.string.error_pre_one_zero_fail);
					}	
					
					if(mFragReference.get().mIsArteryZeroSuc && mFragReference.get().mIsVeinZeroSuc){
						mFragReference.get().mCompleteBtn.setEnabled(true);
						mFragReference.get().mCompleteBtn.setBackgroundResource(R.drawable.oval_next_btn_bg_selector);
					}
				}
				
				break;
				
			case Constants.MSG_ZERO_PRESSURE_TWO:
				String result_two = (String)msg.obj;
				if(null != mFragReference.get().mVZeroTitle_ok_Txt){
					if(result_two.equals("succe")){
						if(!mFragReference.get().mIsVeinZeroSuc){
							mFragReference.get().displayToast(R.string.success_zero_portal_vein_pressure);
							mFragReference.get().mIsVeinZeroSuc = true;
							mFragReference.get().mVZeroTitle_ok_Txt.setVisibility(View.VISIBLE);
						}						
					}else if(result_two.equals("erro")){
						mFragReference.get().displayToast(R.string.error_pre_two_zero_fail);
					}
					if(mFragReference.get().mIsArteryZeroSuc && mFragReference.get().mIsVeinZeroSuc){
						mFragReference.get().mCompleteBtn.setEnabled(true);
						mFragReference.get().mCompleteBtn.setBackgroundResource(R.drawable.oval_next_btn_bg_selector);
					}
				}
				
				break;


			default:
				break;
			}
		}  
		 
	}
	

	private class SerialMsgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL)){
				String pump1_Preal = intent.getStringExtra(SerialMsgConstant.ARTERY_PREAL);
				if(null != mArteryPreTxt){
					if(pump1_Preal.equals("")){
						//肝动脉断链
						mArteryPreTxt.setText(R.string.string_null);
					}else{	
						if(mIsArteryZeroSuc && StringUtil.convertToFloat(pump1_Preal, 0.0f) < 1.0f && StringUtil.convertToFloat(pump1_Preal, 0.0f) >= 0.0f){							
							mArteryPreTxt.setText(String.valueOf(0.0f));	
						}else{
							mArteryPreTxt.setText(pump1_Preal);	
						}
						
					}
				}
			
			}else if(intent.getAction().equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL)){
				String pump2_Preal = intent.getStringExtra(SerialMsgConstant.VEIN_PREAL);
				if(null != mVeinPreTxt){
					if(pump2_Preal.equals("")){
						mVeinPreTxt.setText(R.string.string_null);
					}else{
						if(mIsVeinZeroSuc && StringUtil.convertToFloat(pump2_Preal, 0.0f) < 1.0f && StringUtil.convertToFloat(pump2_Preal, 0.0f) >= 0.0f){
							mVeinPreTxt.setText(String.valueOf(0.0f));
						}else{
							mVeinPreTxt.setText(pump2_Preal);
						}
	
					}	
				}
			
			}else if(action.equals(BroadcastActions.ACTION_ZERO_PUMP_ONE_PREAL)){
				String result = intent.getStringExtra("pump1_Pzero");
				if(null != mUIHandler && null != result){
					Message msg = Message.obtain();
					msg.what = Constants.MSG_ZERO_PRESSURE_ONE;
					msg.obj = result;
					mUIHandler.sendMessage(msg);
				}

			}else if(action.equals(BroadcastActions.ACTION_ZERO_PUMP_TWO_PREAL)){
				String result_two = intent.getStringExtra("pump2_Pzero");
				if(null != mUIHandler && null != result_two){
					Message msg = Message.obtain();
					msg.what = Constants.MSG_ZERO_PRESSURE_TWO;
					msg.obj = result_two;
					mUIHandler.sendMessage(msg);
						
				}
					
			}
		}		
	}

}
