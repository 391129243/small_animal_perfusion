package com.gidi.bio_console.kidney;


import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.listener.KeyBoardActionListener;
import com.gidi.bio_console.log.LogUtil;
import com.gidi.bio_console.mgr.KidneySysAlarmMgr;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.utils.ToastUtils;
import com.gidi.bio_console.view.CustomDialog;
import com.gidi.bio_console.view.CustomSpeedDialog;
import com.gidi.bio_console.view.SystemKeyBoardEditText;
import com.gidi.bio_console.view.TitleContentText;

/***
 * 肾脏灌注设置
 * @author admin
 * 保存灌注参数
 *
 */
public class KidneySetPerfusionFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = "SettingPerfusinFragment";
	private Context mContext;
	private BioConsoleApplication mApplication;
	private SerialMsgReceiver mSerialMsgReceiver;
	private KidneySysAlarmMgr mSystemAlarmMgr;
	private CustomSpeedDialog mArtModeDialog;
	private CustomSpeedDialog mVeinModeDialog;
	private UIHandler mUIHandler;
	/**左右肾质量**/
	private SystemKeyBoardEditText mLeftKidneyWeightEt;	
	private SystemKeyBoardEditText mRightKidneyWeightEt;
	
	private RelativeLayout mArtModeLayout;
	private RelativeLayout mVeinModeLayout;
	private TitleContentText mArtModeTxt;
	private TitleContentText mVeinModeTxt;
		
	/**肝动脉目标压力**/
	private TitleContentText mArtTarPreTxt;
	/**肝动脉目标压力**/
	private TitleContentText mArtMinTarPreTxt;
	/**肝动脉目标压力**/
	private TitleContentText mArtMaxTarPreTxt;
	/**左肾目标流量**/
	private TitleContentText mArtTarFlowTv;
	
	
	/**门静脉目标压力**/
	private TitleContentText mVeinTarPreTxt;
	private TitleContentText mVeinTarFlowTxt;
	private TitleContentText mVeinMinTarPreTv,mVeinMaxTarPreTv;
	

	private TextView mCurArtPreTxt;;
	private TextView mCurVeinPreTxt;
	private TextView mCurVeinFlowTxt;
	private TextView mCurArtFlowTxt;
	private TextView mLiverNumTxt;
	
	private TextView mZeroArtPreTxt;
	private TextView mZeroArtFlowTxt;
	private TextView mZeroVeinPreTxt;
	private TextView mZeroVeinFlowTxt;
	
	/**肝动脉对话框设置灌注模式**/
	private RadioGroup mArtModeRg;	
	private RadioButton mArtConstPRb;
	private RadioButton mArtPulsePRb;
	private RadioButton mArtConstFRb;
	private RelativeLayout mPulsePreLayout;
	private RelativeLayout mConstPreLayout;
	private RelativeLayout mConstFlowLayout;
	private SystemKeyBoardEditText mDlg_ArtConstPEt;
	private SystemKeyBoardEditText mDlg_ArtConstFEt;
	private SystemKeyBoardEditText mDlg_ArtMaxTarPreEt;
	private SystemKeyBoardEditText mDlg_ArtMinTarPreEt;
	private RadioGroup mDlg_MaxArtRg;
	private RadioGroup mDlg_MinArtRg;
	private RadioGroup mDlg_ConstArtPreRg;
	private RadioGroup mDlg_ConstArtFlowRg;
	private TextView mDlg_MaxArtTxt;
	private TextView mDlg_MinArtTxt;
	private TextView mDlg_dividerTxt;
	private TextView mDlg_ConstArtPreTv;
	private TextView mDlg_ConstArtFlowTv;
	private TextView mDlg_ArtTitleTxt;
	/**门静脉灌注模式**/
	private RadioGroup mVeinModeRg;
	private RadioButton mVeinConstPRb;
	private RadioButton mVeinConstFRb;
	private RadioButton mVeinPulseRb;
	private SystemKeyBoardEditText mDlg_VeinConstPreEt;
	private SystemKeyBoardEditText mDlg_VeinConstFlowEt;
	private SystemKeyBoardEditText mDlg_VeinMaxTarPreEt;
	private SystemKeyBoardEditText mDlg_VeinMinTarPreEt;
	private LinearLayout mDlg_VeinConstFlowLayout;
	private LinearLayout mDlg_VeinConstPreLayout;
	private RelativeLayout mDlg_VeinPulsePreLayout;
	private RadioGroup mDlg_VeinConstPreRg;
	private RadioGroup mDlg_VeinConstFlowRg;
	private RadioGroup mDlg_VeinMaxPreRg;
	private RadioGroup mDlg_VeinMinPreRg;
	
	private TextView mDlg_VeinTarTxt;
	private TextView mDlg_MaxVeinTv, mDlg_MinVeinTv,mDlg_pulsedivider;
	private TextView mDlg_VeinTitleTxt;
	private CustomDialog mZeroPressureDialog;//mode ：0：肝动脉压力调零，1：门静脉压力调零
	private CustomDialog mZeroFlowDialog;//mode ：0：肝动脉流量调零，1：门静脉流量调零
	
	private String kidneyNum;
	//private int weight;
	private int mTempMode;

	private float mLastVeinTarPre;
	private float mLastVeinTarFlow;
	private float mLastVeinMinTarPre;
	private float mLastVeinMaxTarPre;
	
	private float mLastArtTarPre;
	private float mLastArtTarFlow;
	private float mLastArtMinTarPre;
	private float mLastArtMaxTarPre;
	private int mLastArtMode;// 0 :const 1 pulse
	private int mLastVeinMode;// 2:const pre 3: const flow

	private boolean isFirstVisible = true;
	private boolean mIsArteryZeroSuc = false;
	private boolean mIsVeinZeroSuc = false;

	private onKidneySetPerfusionListener onSettingPerfusionListener;
	public interface onKidneySetPerfusionListener{
		void onFinishSetPerfison(boolean isArtSet, boolean isVeinSet,boolean isArtModeChange, boolean isVeinModeChange);
		void onSetZeroPressure(int pumptype);
		void onSetZeroFlow(int pumptype);
	}
	
	public  void setOnSettingPerfusionListener(onKidneySetPerfusionListener listener){
		this.onSettingPerfusionListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		registerReceiver();
		initVariables();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "---onResume---");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "---onDestroy---");
		mContext = null;
		mDlg_ArtConstPEt = null;
		mDlg_ArtConstFEt = null;
		mDlg_ArtMaxTarPreEt = null;
		mDlg_ArtMinTarPreEt = null;
		
		mDlg_VeinConstPreEt = null;
		mDlg_VeinConstFlowEt= null;
		mDlg_VeinMaxTarPreEt = null;
		mDlg_VeinMinTarPreEt = null;
		unregisterReceiver();
		cancelDialog();
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
		}
		if(null != onSettingPerfusionListener){
			onSettingPerfusionListener = null;
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Log.i(TAG, "---onHiddenChanged---" + hidden + "-- isFirstVisible--" + isFirstVisible);
		if(!hidden){
			//显示当前的目标压力目标温度
			initData();
		}else{
			//隐藏
			if(isFirstVisible){
				isFirstVisible = false;
			}else{
				cancelDialog();
				savePerfusionParams();
			}
		}
	}
	
	

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
	}
	

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_kidney_perfusion_setting;
	}
	
	private void initVariables(){
		mApplication = (BioConsoleApplication)getActivity().getApplication();
		mSystemAlarmMgr = KidneySysAlarmMgr.getInstance(mApplication);
		mUIHandler = new UIHandler(this);
		mContext = getActivity();
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mLiverNumTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_liver_et);

		kidneyNum = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getStringValue(SharedConstants.KIDNEY_NUM, "");
		mLiverNumTxt.setText(kidneyNum);
		mLeftKidneyWeightEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.setting_perfusion_weight_et);
		mRightKidneyWeightEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.setting_perfusion_right_kidney_weight_et);
		mVeinTarPreTxt = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_const_pre_txt);
		mArtTarFlowTv = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_art_const_flow_value_tv);
		mArtTarPreTxt = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_art_constp_mode_txt);
		mArtMinTarPreTxt = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_art_pulse_min_mode_txt);
		mArtMaxTarPreTxt = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_art_pulse_max_mode_txt);
		mVeinTarFlowTxt = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_target_flow_txt);
		mVeinMinTarPreTv = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_right_kidney_art_pulse_min_tv);
		mVeinMaxTarPreTv = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_right_kideny_art_pulse_max_tv);
		
		
		mCurVeinPreTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_vein_curpre_txt);
		mCurArtPreTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_art_curpre_txt);
		mCurVeinFlowTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_vein_curflow_txt);
		mCurArtFlowTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_art_curflow_txt);
		
		mZeroArtFlowTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_artery_zero_flow_img);
		mZeroArtPreTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_artery_zero_pre_img);
		mZeroVeinFlowTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_vein_zero_flow_img);
		mZeroVeinPreTxt = (TextView)rootView.findViewById(R.id.setting_perfusion_vein_zero_pre_img);

		mArtModeLayout = (RelativeLayout)rootView.findViewById(R.id.setting_perfusion_artery_pressure_layout);
		mVeinModeLayout = (RelativeLayout)rootView.findViewById(R.id.setting_perfusion_vein_mode_layout);
		mArtModeTxt = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_art_mode_txt);
		mVeinModeTxt = (TitleContentText)rootView.findViewById(R.id.setting_perfusion_vein_mode_txt);

		initData();	
	}
	
	
	private void initData(){
		
		int leftKidneyWeight = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.LEFT_KIDNEY_WEIGHT, 0);
		int rightKidneyWeight = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.RIGHT_KIDNEY_WEIGHT, 0);
		

		mLastArtTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
		mLastArtTarFlow = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, 0.0f);
		mLastArtMinTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, 0.0f);
		mLastArtMaxTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, 0.0f);
		
		
		mLastVeinTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
		mLastVeinTarFlow = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, 0.0f);
		mLastVeinMinTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, 0.0f);
		mLastVeinMaxTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, 0.0f);

		mLastArtMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.LEFT_KIDNEY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		mLastVeinMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.RIGHT_KIDNEY_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		mTempMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);//0：normal

		mLeftKidneyWeightEt.setText(String.valueOf(leftKidneyWeight));
		mRightKidneyWeightEt.setText(String.valueOf(rightKidneyWeight));
		
		initPerfusionMode();

	}

	
	private void initPerfusionMode(){
		if (mLastArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE) {//恒压			
			mArtTarPreTxt.setContentText(String.valueOf(mLastArtTarPre));
			constArtModeStatus();
		}else if(mLastArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			pulseArtModeStatus();
			mArtMinTarPreTxt.setContentText(String.valueOf(mLastArtMinTarPre));
			mArtMaxTarPreTxt.setContentText(String.valueOf(mLastArtMaxTarPre));
		}else if(mLastArtMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE){
			//左肾动脉恒流
			mArtTarFlowTv.setContentText(String.valueOf(mLastArtTarFlow));
			constFlowArtModeStatus();
		}
		
		if( mLastVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			//vein const pressure
			constPreVeinModeStatus();
			mVeinTarPreTxt.setContentText(String.valueOf(mLastVeinTarPre));
		}else if(mLastVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			//vein const flow
			constFlowVeinModeStatus();
			mVeinTarFlowTxt.setContentText(String.valueOf(mLastVeinTarFlow));
		}else if(mLastVeinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
			//右肾动脉搏动
			pulseVeinModeStatus();
			mVeinMinTarPreTv.setContentText(String.valueOf(mLastVeinMinTarPre));
			mVeinMaxTarPreTv.setContentText(String.valueOf(mLastVeinMaxTarPre));
		}
	}
	
	private void constArtModeStatus(){
		mArtModeTxt.setContentText(mContext.getResources().getString(R.string.preset_artery_const_pressure));
		mArtTarPreTxt.setVisibility(View.VISIBLE);
		mArtTarFlowTv.setVisibility(View.GONE);
		mArtMinTarPreTxt.setVisibility(View.GONE);
		mArtMaxTarPreTxt.setVisibility(View.GONE);
	}
	
	private void pulseArtModeStatus(){
		mArtModeTxt.setContentText(mContext.getResources().getString(R.string.preset_artery_pulse_pressure));
		mArtTarPreTxt.setVisibility(View.GONE);
		mArtTarFlowTv.setVisibility(View.GONE);
		mArtMinTarPreTxt.setVisibility(View.VISIBLE);
		mArtMaxTarPreTxt.setVisibility(View.VISIBLE);
	}
	
	private void constFlowArtModeStatus(){
		mArtModeTxt.setContentText(mContext.getResources().getString(R.string.preset_vein_const_flow));
		mArtTarFlowTv.setVisibility(View.VISIBLE);
		mArtTarPreTxt.setVisibility(View.GONE);
		mArtMinTarPreTxt.setVisibility(View.GONE);
		mArtMaxTarPreTxt.setVisibility(View.GONE);
	}
	
	private void constPreVeinModeStatus(){
		mVeinModeTxt.setContentText(mContext.getResources().getString(R.string.preset_vein_const_pressure));
		mVeinTarPreTxt.setVisibility(View.VISIBLE);
		mVeinTarFlowTxt.setVisibility(View.GONE);
		mVeinMinTarPreTv.setVisibility(View.GONE);
		mVeinMaxTarPreTv.setVisibility(View.GONE);
	}
	
	private void constFlowVeinModeStatus(){
		mVeinModeTxt.setContentText(mContext.getResources().getString(R.string.preset_vein_const_flow));
		mVeinTarPreTxt.setVisibility(View.GONE);
		mVeinTarFlowTxt.setVisibility(View.VISIBLE);
		mVeinMinTarPreTv.setVisibility(View.GONE);
		mVeinMaxTarPreTv.setVisibility(View.GONE);
	}
	
	private void pulseVeinModeStatus(){
		mVeinModeTxt.setContentText(mContext.getResources().getString(R.string.preset_artery_pulse_pressure));
		mVeinMinTarPreTv.setVisibility(View.VISIBLE);
		mVeinMaxTarPreTv.setVisibility(View.VISIBLE);
		mVeinTarPreTxt.setVisibility(View.GONE);
		mVeinTarFlowTxt.setVisibility(View.GONE);
	}
	
	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mZeroArtFlowTxt.setOnClickListener(this);
		mZeroArtPreTxt.setOnClickListener(this);
		mZeroVeinFlowTxt.setOnClickListener(this);
		mZeroVeinPreTxt.setOnClickListener(this);
		mArtModeLayout.setOnClickListener(this);
		mVeinModeLayout.setOnClickListener(this);
		mLeftKidneyWeightEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				saveLiverWeightParam();
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				
			}
		});

	}
	
	
	/**注册监听器
	 * 监听脉率、流量、泵速、温度、气泡和压强
	 * **/
	private void registerReceiver(){
		mSerialMsgReceiver = new SerialMsgReceiver();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BroadcastActions.ACTION_ZERO_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_ZERO_PUMP_TWO_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_ZERO_PUMP_ONE_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_ZERO_PUMP_TWO_FREAL);

		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);
		
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PMEAN);
		mSerialMsgReceiver = new SerialMsgReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSerialMsgReceiver, mIntentFilter);		
	}

	
	private void unregisterReceiver(){
		if(null != mSerialMsgReceiver){
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSerialMsgReceiver);
			mSerialMsgReceiver = null;			
		}
	}
	
	
	private void cancelDialog(){
		if(null != mArtModeDialog){
			mArtModeDialog.dismiss();
			mArtModeDialog = null;
		}
		
		if(null != mVeinModeDialog){
			mVeinModeDialog.dismiss();
			mVeinModeDialog = null;
		}
		
		if(null != mZeroPressureDialog){
			mZeroPressureDialog.dismiss();
			mZeroPressureDialog = null;
		}
		
		if(null != mZeroFlowDialog){
			mZeroFlowDialog.dismiss();
			mZeroFlowDialog = null;
		}
		
	}
	
	/**
	 * 修改肝动脉灌注模式对话框
	 */
	private void showChangeArtModeDialog(){
		if(null != mArtModeDialog){
			mArtModeDialog.dismiss();
			mArtModeDialog = null;
		}
		if(null == mArtModeDialog){
			mArtModeDialog = new CustomSpeedDialog(getActivity());
			LayoutInflater mInflater = (LayoutInflater)getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_change_kidney_artery_perfusion_mode, null);
		    final int mArtMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
					.getIntValue(SharedConstants.LEFT_KIDNEY_PERFUSION_MODE, Constants.LEFT_ARTERY_CONST_PRESSURE_MODE);
			initArtDialogView(view, mArtMode);
			initArtDialogListener(mArtMode);
			CustomSpeedDialog.Builder builder = new CustomSpeedDialog.Builder(getBaseActivity());
			builder.setContentView(view);
			builder.setTitle(getBaseActivity().getResources().getString(R.string.dialog_title_change_left_kidney_artery_mode));
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int arteryMode = mArtMode;
					if(mArtConstPRb.isChecked()){
						arteryMode = Constants.LEFT_ARTERY_CONST_PRESSURE_MODE;//恒压模式
						mArtModeTxt.setContentText(mContext.getResources().getString(R.string.preset_artery_const_pressure));
						String strconstArtPre = mDlg_ConstArtPreTv.getText().toString();
						if(StringUtil.isEmpty(strconstArtPre) || strconstArtPre.equals(getResources().getString(R.string.string_null))){
							displayToast(R.string.error_value_cold_perfusion_left_kidney_target_pre);
							return;
						}else if(strconstArtPre.endsWith(".")){
							displayToast(R.string.error_target_left_kieney_pre_value_format_error);
							return;
						}else{
							float constTarPre = StringUtil.convertToFloat(strconstArtPre, 0.0f);
							if(mTempMode == 0){
								if(constTarPre > 100.0f){
									displayToast(R.string.error_max_value_normal_perfusion_target_const_left_kidney_artery_pre);
									return;
								}
							}else if(mTempMode == 1){
								if(constTarPre > 40.0f){
									displayToast(R.string.error_max_value_cold_perfusion_target_const_left_kidney_artery_pre);
									return;
								}
							}
							mArtTarPreTxt.setContentText(strconstArtPre);
							constArtModeStatus();
						}
						
						
					}else if(mArtPulsePRb.isChecked()){
						arteryMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE;//搏动模式
						String strminArtPre = mDlg_MinArtTxt.getText().toString();
						String strmaxArtPre = mDlg_MaxArtTxt.getText().toString();
						float  minArtPre = StringUtil.convertToFloat(strminArtPre, 0.0f);
						float  maxArtPre = StringUtil.convertToFloat(strmaxArtPre, 0.0f);
						if(StringUtil.isEmpty(strmaxArtPre)){
							displayToast(R.string.error_value_normal_perfusion_left_kidney_target_pre_max_null);
							return;
						}else if(StringUtil.isEmpty(strminArtPre)){
							displayToast(R.string.error_value_normal_perfusion_left_kidney_target_pre_min_null);
							return;
						}else if(maxArtPre > 120.0f){
							displayToast(R.string.error_target_left_kidney_pre_max_limit);
							return;
						}else if(minArtPre >= 100.0f ){
							displayToast(R.string.error_target_left_kidney_pre_min_limit);
							return;
						}else if(compareArtPulsePreDiff(minArtPre, maxArtPre)){
							displayToast(R.string.error_target_left_kidney_pre_min_max_difference);
							return;
						}
						mArtModeTxt.setContentText(mContext.getResources().getString(R.string.preset_artery_pulse_pressure));						
						mArtMinTarPreTxt.setContentText(strminArtPre);
						mArtMaxTarPreTxt.setContentText(strmaxArtPre);
						pulseArtModeStatus();
					}else if(mArtConstFRb.isChecked()){
						arteryMode = Constants.LEFT_ARTERY_CONST_FLOW_MODE;
						String strConstArtFlow = mDlg_ConstArtFlowTv.getText().toString();
						
						float artConstFlow = StringUtil.convertToFloat(strConstArtFlow, 0.0f);
						if(artConstFlow > 1500.0f){
							displayToast(R.string.error_max_value_left_kidney_target_flow);
							return;
						}else if(StringUtil.isEmpty(strConstArtFlow)){
							displayToast(R.string.error_value_right_kidney_artery_target_flow_null);
							return;
						}else if(strConstArtFlow.endsWith(".")){
							displayToast(R.string.error_value_end_with_decimal);
							return;
						}
						mArtTarFlowTv.setContentText(strConstArtFlow);//主页面显示
						constFlowArtModeStatus();
					}
					PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
						.setValueByName(SharedConstants.LEFT_KIDNEY_PERFUSION_MODE, arteryMode);
		
					mArtModeDialog.dismiss();
					mArtModeDialog = null;

				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mArtModeDialog.dismiss();
					mArtModeDialog = null;

				}
			});
			
			mArtModeDialog = builder.create();
			mArtModeDialog.show();
		}
	}
	
	/**
	 * 初始化肝动脉灌注设置对话框视图
	 * @param view
	 * @param artMode
	 */
	private void initArtDialogView(View view, int  artMode){
		
		mArtModeRg = (RadioGroup)view.findViewById(R.id.dialog_artry_mode_rg);
		mArtConstPRb = (RadioButton)view.findViewById(R.id.dialog_artery_const_pre_rb);
		mArtPulsePRb = (RadioButton)view.findViewById(R.id.dialog_artery_pulse_pre_rb);
		mArtConstFRb = (RadioButton)view.findViewById(R.id.dialog_artery_const_flow_rb);
		
		mPulsePreLayout = (RelativeLayout)view.findViewById(R.id.setting_artery_pulse_pressure_layout);
		mConstPreLayout = (RelativeLayout)view.findViewById(R.id.setting_artery_const_pressure_layout);
		mConstFlowLayout = (RelativeLayout)view.findViewById(R.id.setting_artery_const_flow_layout);
		mDlg_MaxArtRg = (RadioGroup)view.findViewById(R.id.set_art_pulse_max_pre_radiogroup);
		mDlg_MinArtRg = (RadioGroup)view.findViewById(R.id.set_art_pulse_min_pre_radiogroup);
		mDlg_ConstArtPreRg = (RadioGroup)view.findViewById(R.id.set_art_const_radiogroup);
		mDlg_ConstArtFlowRg = (RadioGroup)view.findViewById(R.id.set_art_const_flow_radiogroup);
		mDlg_ArtConstPEt = (SystemKeyBoardEditText)view.findViewById(R.id.setting_art_const_tarpressure_et);
		mDlg_ArtMaxTarPreEt = (SystemKeyBoardEditText)view.findViewById(R.id.setting_art_max_tarpressure_et);
		mDlg_ArtMinTarPreEt = (SystemKeyBoardEditText)view.findViewById(R.id.setting_art_min_tarpressure_et);
		mDlg_ArtConstFEt = (SystemKeyBoardEditText)view.findViewById(R.id.setting_art_const_tarflow_et);
		//显示数值
		mDlg_ArtTitleTxt = (TextView)view.findViewById(R.id.setting_artery_target_pressure_title);
		mDlg_MaxArtTxt = (TextView)view.findViewById(R.id.setting_art_max_txt);
		mDlg_MinArtTxt = (TextView)view.findViewById(R.id.setting_art_min_txt);
		mDlg_dividerTxt = (TextView)view.findViewById(R.id.setting_art_divider_txt);
		mDlg_ConstArtPreTv = (TextView)view.findViewById(R.id.setting_art_const_pre_tv);
		mDlg_ConstArtFlowTv = (TextView)view.findViewById(R.id.setting_art_const_flow_tv);
		
		mDlg_ArtMaxTarPreEt.setCurrentView(view);
		mDlg_ArtMinTarPreEt.setCurrentView(view);
		mDlg_ArtConstPEt.setCurrentView(view);
		mDlg_ArtConstFEt.setCurrentView(view);
		float ArtTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
		float ArtTarFlow = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, 0.0f);
		float ArtMinTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, 0.0f);
		float ArtMaxTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, 0.0f);

		if(artMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			mArtConstPRb.setChecked(true);
			constArtModeStatus();
			mPulsePreLayout.setVisibility(View.GONE);
			mConstFlowLayout.setVisibility(View.GONE);
			mConstPreLayout.setVisibility(View.VISIBLE);
			mDlg_MaxArtTxt.setVisibility(View.GONE);
			mDlg_MinArtTxt.setVisibility(View.GONE);
			mDlg_dividerTxt.setVisibility(View.GONE);
			mDlg_ConstArtFlowTv.setVisibility(View.GONE);
			mDlg_ConstArtPreTv.setVisibility(View.VISIBLE);
			mDlg_ArtTitleTxt.setText(R.string.preset_pattern_left_target_artery_pressure);
			mDlg_ConstArtPreTv.setText(String.valueOf(ArtTarPre));
		}else if(artMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			pulseArtModeStatus();
			mArtPulsePRb.setChecked(true);			
			mPulsePreLayout.setVisibility(View.VISIBLE);
			mConstPreLayout.setVisibility(View.GONE);
			mConstFlowLayout.setVisibility(View.GONE);
			mDlg_MaxArtTxt.setVisibility(View.VISIBLE);
			mDlg_MinArtTxt.setVisibility(View.VISIBLE);
			mDlg_dividerTxt.setVisibility(View.VISIBLE);
			mDlg_ConstArtPreTv.setVisibility(View.GONE);
			mDlg_ConstArtFlowTv.setVisibility(View.GONE);
			mDlg_ArtTitleTxt.setText(R.string.preset_pattern_left_target_artery_pressure);
			mDlg_MaxArtTxt.setText(String.valueOf(ArtMaxTarPre));
			mDlg_MinArtTxt.setText(String.valueOf(ArtMinTarPre));
		}else if(artMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE){
			mArtConstFRb.setChecked(true);
			constFlowArtModeStatus();
			mPulsePreLayout.setVisibility(View.GONE);
			mConstPreLayout.setVisibility(View.GONE);
			mConstFlowLayout.setVisibility(View.VISIBLE);
			mDlg_MaxArtTxt.setVisibility(View.GONE);
			mDlg_MinArtTxt.setVisibility(View.GONE);
			mDlg_dividerTxt.setVisibility(View.GONE);
			mDlg_ConstArtPreTv.setVisibility(View.GONE);
			mDlg_ConstArtFlowTv.setVisibility(View.VISIBLE);
			mDlg_ArtTitleTxt.setText(R.string.preset_pattern_left_target_artery_flow);
			mDlg_ConstArtFlowTv.setText(String.valueOf(ArtTarFlow));
		}
	}
	
	private void initArtDialogListener(int artMode){
		//最大目标值
		mDlg_ArtMaxTarPreEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float maxTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(maxTarPre > 120.0f){
					editable.replace(0,editable.length(), "120");
					displayToast(R.string.error_target_left_kidney_pre_max_limit);
					mDlg_MaxArtTxt.setText(120+"");
				}else{
					mDlg_MaxArtTxt.setText(editable);
				}

				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_max_pressure = mDlg_ArtMaxTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mDlg_MaxArtTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MaxArtTxt.setText(art_max_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_MaxArtTxt.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_max_pressure = mDlg_ArtMaxTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mDlg_MaxArtTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MaxArtTxt.setText(art_max_pressure);
				}
			}
		});
		//搏动最小目标值
		mDlg_ArtMinTarPreEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub

				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 100.0f){
					editable.replace(0,editable.length(), "100");
					displayToast(R.string.error_target_left_kidney_pre_min_limit);	
					mDlg_MinArtTxt.setText(100 + "");
				}else{
					mDlg_MinArtTxt.setText(editable);
				}

			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_min_pressure = mDlg_ArtMinTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mDlg_MinArtTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MinArtTxt.setText(art_min_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_MinArtTxt.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_min_pressure = mDlg_ArtMinTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mDlg_MinArtTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MinArtTxt.setText(art_min_pressure);
				}
			}
		});
		mDlg_MaxArtRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_art_pulse_one_rb:
				case R.id.dlg_set_art_pulse_two_rb:
				case R.id.dlg_set_art_pulse_three_rb:
				case R.id.dlg_set_art_pulse_third_rb:
					setNormalView(mDlg_ArtMaxTarPreEt);
					String art_max_pre = radioButton.getText().toString();					
					mDlg_MaxArtTxt.setText(art_max_pre);
					break;
				default:
					break;
				}
			}
		});
		mDlg_MinArtRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_art_pulse_min_one_rb:
				case R.id.dlg_set_art_pulse_min_two_rb:
				case R.id.dlg_set_art_pulse_min_three_rb:
				case R.id.dlg_set_art_pulse_min_third_rb:
					setNormalView(mDlg_ArtMinTarPreEt);
					String art_min_pre = radioButton.getText().toString();					
					mDlg_MinArtTxt.setText(art_min_pre);
					break;
				default:
					break;
				}
			}
		});
		mDlg_ArtMaxTarPreEt.setOnClickListener(this);
		mDlg_ArtMinTarPreEt.setOnClickListener(this);
		
		//肝动脉恒压目标值
		mDlg_ArtConstPEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				
				float constTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);
				if(mTempMode == 0){
					if(constTarPre > 100.0f){
						editable.replace(0,editable.length(), "100");
						displayToast(R.string.error_max_value_normal_perfusion_target_const_left_kidney_artery_pre);
						mDlg_ConstArtPreTv.setText(100 + "");
						return;
					}else{
						mDlg_ConstArtPreTv.setText(editable);
					}
				}else if(mTempMode == 1){
					if(constTarPre > 40.0f){
						editable.replace(0,editable.length(), "40");
						displayToast(R.string.error_max_value_cold_perfusion_target_const_left_kidney_artery_pre);
						mDlg_ConstArtPreTv.setText(40 + "");
						return;
					}else{
						mDlg_ConstArtPreTv.setText(editable);
					}
				}
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_const_pressure = mDlg_ArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_const_pressure)){
					mDlg_ConstArtPreTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_ConstArtPreTv.setText(art_const_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_ConstArtPreTv.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_const_pressure = mDlg_ArtConstPEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_const_pressure)){
					mDlg_ConstArtPreTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_ConstArtPreTv.setText(art_const_pressure);
				}
			}
		});
		mDlg_ConstArtPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_art_const_one_rb:
				case R.id.dlg_set_art_const_two_rb:
				case R.id.dlg_set_art_const_three_rb:
				case R.id.dlg_set_art_const_third_rb:
					setNormalView(mDlg_ArtConstPEt);
					String art_const_pre= radioButton.getText().toString();					
					mDlg_ConstArtPreTv.setText(art_const_pre);
					break;
				default:
					break;
				}
			}
		});
		
		mDlg_ArtConstPEt.setOnClickListener(this);
		mDlg_ArtConstFEt.setOnClickListener(this);
		mDlg_ArtConstFEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float art_flow = StringUtil.convertToFloat(editable.toString(),0.0f);  				
				if(art_flow > 1500.0f){
					editable.replace(0,editable.length(), "1500");
					mDlg_ConstArtFlowTv.setText(1500+"");		
					displayToast(R.string.error_max_value_left_kidney_target_flow);
					return;						
				}else{
					mDlg_ConstArtFlowTv.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String vein_const_flow = mDlg_ArtConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mDlg_ConstArtFlowTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_ConstArtFlowTv.setText(vein_const_flow);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_ConstArtFlowTv.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_flow = mDlg_ArtConstFEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mDlg_ConstArtFlowTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_ConstArtFlowTv.setText(vein_const_flow);
				}
			}
		});
		//左肾动脉恒流模式
		mDlg_ConstArtFlowRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_art_const_flow_one_rb:
				case R.id.dlg_set_art_const_flow_two_rb:
				case R.id.dlg_set_art_const_flow_three_rb:
				case R.id.dlg_set_art_const_flow_four_rb:
					setNormalView(mDlg_ArtConstFEt);
					String art_const_flow = radioButton.getText().toString();					
					mDlg_ConstArtFlowTv.setText(art_const_flow);
					break;
				default:
					break;
				}
			}
		});
		
		mArtModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkId) {
			// TODO Auto-generated method stub
			switch (checkId) {
				case R.id.dialog_artery_const_pre_rb:		
					mDlg_ArtTitleTxt.setText(R.string.preset_pattern_left_target_artery_pressure);
					mPulsePreLayout.setVisibility(View.GONE);
					mConstPreLayout.setVisibility(View.VISIBLE);
					mConstFlowLayout.setVisibility(View.GONE);
					mDlg_MaxArtTxt.setVisibility(View.GONE);
					mDlg_MinArtTxt.setVisibility(View.GONE);
					mDlg_dividerTxt.setVisibility(View.GONE);
					mDlg_ConstArtFlowTv.setVisibility(View.GONE);
					mDlg_ConstArtPreTv.setVisibility(View.VISIBLE);
					break;
			
				case R.id.dialog_artery_pulse_pre_rb:
					mDlg_ArtTitleTxt.setText(R.string.preset_pattern_left_target_artery_pressure);
					mPulsePreLayout.setVisibility(View.VISIBLE);
					mConstPreLayout.setVisibility(View.GONE);
					mConstFlowLayout.setVisibility(View.GONE);
					mDlg_MaxArtTxt.setVisibility(View.VISIBLE);
					mDlg_MinArtTxt.setVisibility(View.VISIBLE);
					mDlg_dividerTxt.setVisibility(View.VISIBLE);
					mDlg_ConstArtFlowTv.setVisibility(View.GONE);
					mDlg_ConstArtPreTv.setVisibility(View.GONE);
					break;
				case R.id.dialog_artery_const_flow_rb:
					mDlg_ArtTitleTxt.setText(R.string.preset_pattern_left_target_artery_flow);
					mPulsePreLayout.setVisibility(View.GONE);
					mConstPreLayout.setVisibility(View.GONE);
					mConstFlowLayout.setVisibility(View.VISIBLE);
					mDlg_MaxArtTxt.setVisibility(View.GONE);
					mDlg_MinArtTxt.setVisibility(View.GONE);
					mDlg_dividerTxt.setVisibility(View.GONE);
					mDlg_ConstArtFlowTv.setVisibility(View.VISIBLE);
					mDlg_ConstArtPreTv.setVisibility(View.GONE);
					break;
				default:					
					break;
				}
			}
		});
	}
	
	private void showZeroPressureDialog(final int mode){
		if(null != mZeroPressureDialog){
			mZeroPressureDialog.dismiss();
			mZeroPressureDialog = null;
		}
		if(null == mZeroPressureDialog){
			mZeroPressureDialog = new CustomDialog(this.getActivity());
			LayoutInflater mInflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(R.string.dialog_msg_perfusion_add_bloodgas_sampletime);
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_sample_time));
			if(mode == 0){
				builder.setMessage(R.string.dialog_notice_zero_left_kidney_artery_pressure);
			}else{
				builder.setMessage(R.string.dialog_notice_zero_right_kidney_artery_pressure);
			}

			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(mode == 0){
						if(null != onSettingPerfusionListener){
							onSettingPerfusionListener.onSetZeroPressure(0);
						}
					}else{
						if(null != onSettingPerfusionListener){
							onSettingPerfusionListener.onSetZeroPressure(1);
						}
					}
					mZeroPressureDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mZeroPressureDialog.dismiss();
					mZeroPressureDialog = null;
				}
			});
			mZeroPressureDialog = builder.create();
			mZeroPressureDialog.show();
			
		}
	}
	
	/**
	 * 流量调零
	 * @param mode 0：肝动脉流量，1：门静脉流量
	 * 
	 */
	private void showZeroFlowDialog(final int mode){
		if(null != mZeroFlowDialog){
			mZeroFlowDialog.dismiss();
			mZeroFlowDialog = null;
		}
		if(null == mZeroFlowDialog){
			mZeroFlowDialog = new CustomDialog(this.getActivity());
			LayoutInflater mInflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_common_hint_layout, null);
			TextView mHintTxt = (TextView)view.findViewById(R.id.common_dialog_hint_txt);
			mHintTxt.setText(R.string.dialog_msg_perfusion_add_bloodgas_sampletime);
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(view);
			builder.setTitleIcon(R.drawable.ic_ask);
			builder.setTitle(mContext.getResources().getString(R.string.dialog_title_sample_time));
			if(mode == 0){
				builder.setMessage(R.string.dialog_notice_zero_flow);
			}else{
				builder.setMessage(R.string.dialog_notice_zero_flow);
			}

			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(mode == 0){
						if(null != onSettingPerfusionListener){
							onSettingPerfusionListener.onSetZeroFlow(0);
						}
					}else{
						if(null != onSettingPerfusionListener){
							onSettingPerfusionListener.onSetZeroFlow(1);
						}
					}
					mZeroFlowDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mZeroFlowDialog.dismiss();
					mZeroFlowDialog = null;
				}
			});
			mZeroFlowDialog = builder.create();
			mZeroFlowDialog.show();
			
		}
	}
	
	/**
	 * 修改门静脉灌注模式和目标值
	 */
	private void showChangeVeinModeDialog(){
		if(null != mVeinModeDialog){
			mVeinModeDialog.dismiss();
			mVeinModeDialog = null;
		}
		if(null == mVeinModeDialog){
			mVeinModeDialog = new CustomSpeedDialog(getActivity());
			LayoutInflater mInflater = (LayoutInflater)getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_change_kidney_vein_perfusion_mode, null);
			final int mVeinMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
					.getIntValue(SharedConstants.RIGHT_KIDNEY_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
			initVeinDialogView(view,mVeinMode);
			initVeinDialogListener(mVeinMode);
			CustomSpeedDialog.Builder builder = new CustomSpeedDialog.Builder(getBaseActivity());
			builder.setContentView(view);
			builder.setTitleIcon(0);
			builder.setTitle(getBaseActivity().getResources().getString(R.string.dialog_title_change_right_kidney_artery_mode));
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int veinMode = mVeinMode;
					if(mVeinConstPRb.isChecked()){
						veinMode = Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE;	
						String strveinConstPre = mDlg_VeinTarTxt.getText().toString();
						float veinConstPre = StringUtil.convertToFloat(strveinConstPre, 0.0f);
						if(mTempMode == 0){
							
							if(veinConstPre > 100.0f){
								displayToast(R.string.error_max_value_normal_perfusion_target_const_right_kidney_artery_pre);
								return;
							}
						}else if(mTempMode == 1){
							if(veinConstPre > 40.0f){
								displayToast(R.string.error_max_value_cold_perfusion_target_const_right_kidney_artery_pre);
								return;
							}
						}else if(strveinConstPre.equals("")||strveinConstPre.equals("--")){
							displayToast(R.string.error_value_right_kidney_artery_target_pressure_null);
							return;
						}else if(strveinConstPre.endsWith(".")){
							displayToast(R.string.error_target_right_kieney_pre_value_format_error);
							return;
						}	
					
						mVeinTarPreTxt.setContentText(strveinConstPre);						
						constPreVeinModeStatus();
					}else if(mVeinConstFRb.isChecked()){
						veinMode = Constants.RIGHT_ARTERY_CONST_FLOW_MODE;
						String strveinConstFlow = mDlg_VeinTarTxt.getText().toString();
						float veinConstFlow = StringUtil.convertToFloat(strveinConstFlow, 0.0f);
						if(veinConstFlow > 1500.0f){
							displayToast(R.string.error_max_value_right_kidney_target_flow);
							return;
						}else if(StringUtil.isEmpty(strveinConstFlow)){
							displayToast(R.string.error_value_right_kidney_artery_target_flow_null);
							return;
						}else if(strveinConstFlow.endsWith(".")){
							displayToast(R.string.error_value_end_with_decimal);
							return;
						}
						
						mVeinTarFlowTxt.setContentText(strveinConstFlow);
						constFlowVeinModeStatus();
					}else if(mVeinPulseRb.isChecked()){
						veinMode = Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE;;//搏动模式
						String strminVeinPre = mDlg_MinVeinTv.getText().toString();
						String strmaxVeinPre = mDlg_MaxVeinTv.getText().toString();
						float  minVeinPre = StringUtil.convertToFloat(strminVeinPre, 0.0f);
						float  maxVeinPre = StringUtil.convertToFloat(strmaxVeinPre, 0.0f);
						if(StringUtil.isEmpty(strmaxVeinPre)){
							displayToast(R.string.error_value_normal_perfusion_right_kidney_target_pre_max_null);
							return;
						}else if(StringUtil.isEmpty(strmaxVeinPre)){
							displayToast(R.string.error_value_normal_perfusion_right_kidney_target_pre_min_null);
							return;
						}else if(maxVeinPre > 120.0f){
							displayToast(R.string.error_target_right_kidney_pre_max_limit);
							return;
						}else if(minVeinPre >= 100.0f ){
							displayToast(R.string.error_target_right_kidney_pre_min_limit);
							return;
						}else if(compareArtPulsePreDiff(minVeinPre, maxVeinPre)){
							displayToast(R.string.error_target_right_kidney_pre_min_max_difference);
							return;
						}					
						mVeinMinTarPreTv.setContentText(strminVeinPre);
						mVeinMaxTarPreTv.setContentText(strmaxVeinPre);
						pulseVeinModeStatus();
					}
					PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
						.setValueByName(SharedConstants.RIGHT_KIDNEY_PERFUSION_MODE, veinMode);
					mVeinModeDialog.dismiss();
					mVeinModeDialog = null;
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mVeinModeDialog.dismiss();
					mVeinModeDialog = null;

				}
			});
			
			mVeinModeDialog = builder.create();
			mVeinModeDialog.show();
		}
	}
	
	private void initVeinDialogView(View view, int veinMode){
		mVeinModeRg = (RadioGroup)view.findViewById(R.id.dialog_vein_mode_rg);
		mVeinConstFRb =  (RadioButton)view.findViewById(R.id.dialog_vein_const_flow_rb);
		mVeinConstPRb = (RadioButton)view.findViewById(R.id.dialog_vein_const_pre_rb);
		mVeinPulseRb = (RadioButton)view.findViewById(R.id.dialog_vein_pulse_pressure_rb);
		
		mDlg_VeinConstPreEt = (SystemKeyBoardEditText)view.findViewById(R.id.dlg_setting_vein_target_pressure_et);
		mDlg_VeinConstFlowEt = (SystemKeyBoardEditText)view.findViewById(R.id.dlg_setting_vein_target_flow_et);
		mDlg_VeinMaxTarPreEt = (SystemKeyBoardEditText)view.findViewById(R.id.dlg_setting_vein_max_tarpressure_et);
		mDlg_VeinMinTarPreEt = (SystemKeyBoardEditText)view.findViewById(R.id.dlg_setting_vein_min_tarpressure_et);		
		
		mDlg_VeinConstFlowLayout = (LinearLayout)view.findViewById(R.id.dlg_setting_vein_flow_layout);
		mDlg_VeinConstPreLayout = (LinearLayout)view.findViewById(R.id.dlg_setting_vein_pressure_layout);
		mDlg_VeinPulsePreLayout= (RelativeLayout)view.findViewById(R.id.dlg_setting_vein_pulse_pressure_layout);
		
		
		
		mDlg_VeinConstFlowRg = (RadioGroup)view.findViewById(R.id.dlg_set_vein_flow_radiogroup);
		mDlg_VeinConstPreRg = (RadioGroup)view.findViewById(R.id.dlg_set_vein_pre_radiogroup);
		mDlg_VeinMaxPreRg = (RadioGroup)view.findViewById(R.id.dlg_set_vein_pulse_max_pre_radiogroup);
		mDlg_VeinMinPreRg = (RadioGroup)view.findViewById(R.id.dlg_set_vein_pulse_min_pre_radiogroup);
		
		
		
		mDlg_VeinTitleTxt = (TextView)view.findViewById(R.id.dlg_setting_vein_title);
		mDlg_VeinTarTxt = (TextView)view.findViewById(R.id.dlg_setting_vein_target_txt);
		mDlg_pulsedivider = (TextView)view.findViewById(R.id.dlg_setting_vein_divider_tv);
		mDlg_MaxVeinTv =(TextView)view.findViewById(R.id.dlg_setting_vein_max_tv);
		mDlg_MinVeinTv =(TextView)view.findViewById(R.id.dlg_setting_vein_min_tv);
		
		
		mDlg_VeinMaxTarPreEt.setCurrentView(view);
		mDlg_VeinMinTarPreEt.setCurrentView(view);
		mDlg_VeinConstFlowEt.setCurrentView(view);
		mDlg_VeinConstPreEt.setCurrentView(view);
		float VeinTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
		
		float VeinTarFlow = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, 0.0f);
		
		float VeinTarMaxPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, 0.0f);
		float VeinTarMinPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, 0.0f);
		if(veinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			mVeinConstPRb.setChecked(true);
			mDlg_VeinConstPreLayout.setVisibility(View.VISIBLE);
			mDlg_VeinConstFlowLayout.setVisibility(View.GONE);
			mDlg_VeinPulsePreLayout.setVisibility(View.GONE);
			mDlg_VeinTarTxt.setVisibility(View.VISIBLE);
			mDlg_MaxVeinTv.setVisibility(View.GONE);
			mDlg_MinVeinTv.setVisibility(View.GONE);
			mDlg_pulsedivider.setVisibility(View.GONE);
			mDlg_VeinTitleTxt.setText(R.string.preset_pattern_right_target_artery_pressure);
			mDlg_VeinTarTxt.setText(String.valueOf(VeinTarPre));
		}else if(veinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){//恒流
			mVeinConstFRb.setChecked(true);
			mDlg_VeinConstPreLayout.setVisibility(View.GONE);
			mDlg_VeinConstFlowLayout.setVisibility(View.VISIBLE);
			mDlg_VeinPulsePreLayout.setVisibility(View.GONE);
			mDlg_VeinTitleTxt.setText(R.string.preset_pattern_right_target_artery_flow);
			mDlg_VeinTarTxt.setVisibility(View.VISIBLE);
			mDlg_MaxVeinTv.setVisibility(View.GONE);
			mDlg_MinVeinTv.setVisibility(View.GONE);
			mDlg_pulsedivider.setVisibility(View.GONE);
			mDlg_VeinTarTxt.setText(String.valueOf(VeinTarFlow));
		}else if(veinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
			mVeinPulseRb.setChecked(true);
			mDlg_VeinConstPreLayout.setVisibility(View.GONE);
			mDlg_VeinConstFlowLayout.setVisibility(View.GONE);
			mDlg_VeinPulsePreLayout.setVisibility(View.VISIBLE);
			mDlg_VeinTitleTxt.setText(R.string.preset_pattern_right_target_artery_pressure);
			mDlg_VeinTarTxt.setVisibility(View.GONE);
			mDlg_MaxVeinTv.setVisibility(View.VISIBLE);
			mDlg_MinVeinTv.setVisibility(View.VISIBLE);
			mDlg_pulsedivider.setVisibility(View.VISIBLE);
			mDlg_MaxVeinTv.setText(String.valueOf(VeinTarMaxPre));
			mDlg_MinVeinTv.setText(String.valueOf(VeinTarMinPre));
			
		}
	}
	
	private void initVeinDialogListener(int veinMode){		
		mVeinModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(RadioGroup group, int checkId) {
				// TODO Auto-generated method stub
				float VeinTarPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
				
				float VeinTarFlow = PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, 0.0f);
				float VeinTarMaxPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, 0.0f);
				float VeinTarMinPre = PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, 0.0f);
				switch (checkId) {
				case R.id.dialog_vein_const_pre_rb:
					
					mDlg_VeinTitleTxt.setText(R.string.preset_pattern_right_target_artery_pressure);
					mDlg_VeinPulsePreLayout.setVisibility(View.GONE);
					mDlg_VeinConstPreLayout.setVisibility(View.VISIBLE);
					mDlg_VeinConstFlowLayout.setVisibility(View.GONE);	
					mDlg_MaxVeinTv.setVisibility(View.GONE);
					mDlg_MinVeinTv.setVisibility(View.GONE);
					mDlg_pulsedivider.setVisibility(View.GONE);
					mDlg_VeinTarTxt.setVisibility(View.VISIBLE);
					mDlg_VeinTarTxt.setText(String.valueOf(VeinTarPre));
					break;
					
				case R.id.dialog_vein_const_flow_rb:
					mDlg_VeinTitleTxt.setText(R.string.preset_pattern_right_target_artery_flow);
					mDlg_VeinPulsePreLayout.setVisibility(View.GONE);
					mDlg_VeinConstPreLayout.setVisibility(View.GONE);
					mDlg_VeinConstFlowLayout.setVisibility(View.VISIBLE);
					mDlg_MaxVeinTv.setVisibility(View.GONE);
					mDlg_MinVeinTv.setVisibility(View.GONE);
					mDlg_pulsedivider.setVisibility(View.GONE);
					mDlg_VeinTarTxt.setVisibility(View.VISIBLE);
					mDlg_VeinTarTxt.setText(String.valueOf(VeinTarFlow));
					break;
				case R.id.dialog_vein_pulse_pressure_rb:
					mDlg_VeinTitleTxt.setText(R.string.preset_pattern_right_target_artery_pressure);
					mDlg_VeinPulsePreLayout.setVisibility(View.VISIBLE);
					mDlg_VeinConstPreLayout.setVisibility(View.GONE);
					mDlg_VeinConstFlowLayout.setVisibility(View.GONE);	
					mDlg_MaxVeinTv.setVisibility(View.VISIBLE);
					mDlg_MinVeinTv.setVisibility(View.VISIBLE);
					mDlg_pulsedivider.setVisibility(View.VISIBLE);
					mDlg_VeinTarTxt.setVisibility(View.GONE);
					mDlg_MaxVeinTv.setText(String.valueOf(VeinTarMaxPre));
					mDlg_MinVeinTv.setText(String.valueOf(VeinTarMinPre));
					break;
				default:
					break;
				}
			}
		});
		mDlg_VeinConstPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_vein_pre_one_rb:
				case R.id.dlg_set_vein_pre_two_rb:
				case R.id.dlg_set_vein_pre_three_rb:
				case R.id.dlg_set_vein_pre_four_rb:
					setNormalView(mDlg_VeinConstPreEt);
					String vein_const_pre= radioButton.getText().toString();					
					mDlg_VeinTarTxt.setText(vein_const_pre);
					break;
				default:
					break;
				}
			}
		});
		mDlg_VeinConstFlowRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_vein_flow_one_rb:
				case R.id.dlg_set_vein_flow_two_rb:
				case R.id.dlg_set_vein_flow_three_rb:
				case R.id.dlg_set_vein_flow_third_rb:
					setNormalView(mDlg_VeinConstFlowEt);
					String vein_const_flow= radioButton.getText().toString();					
					mDlg_VeinTarTxt.setText(vein_const_flow);
					break;
				default:
					break;
				}
			}
		});
		mDlg_VeinMaxPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_vein_pulse_max_one_rb:
				case R.id.dlg_set_vein_pulse_max_two_rb:
				case R.id.dlg_set_vein_pulse_max_three_rb:
				case R.id.dlg_set_vein_pulse_max_four_rb:
					setNormalView(mDlg_VeinMaxTarPreEt);
					String vein_pulse_max_pre = radioButton.getText().toString();					
					mDlg_MaxVeinTv.setText(vein_pulse_max_pre);
					break;
				default:
					break;
				}
			}
		});
		
		mDlg_VeinMinPreRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkId) {
				// TODO Auto-generated method stub
				int id= radiogroup.getCheckedRadioButtonId();
				RadioButton radioButton = (RadioButton)radiogroup.findViewById(id);
				switch (id) {
				case R.id.dlg_set_vein_pulse_min_one_rb:
				case R.id.dlg_set_vein_pulse_min_two_rb:
				case R.id.dlg_set_vein_pulse_min_three_rb:
				case R.id.dlg_set_vein_pulse_min_four_rb:
					setNormalView(mDlg_VeinMinTarPreEt);
					String vein_pulse_max_pre = radioButton.getText().toString();					
					mDlg_MinVeinTv.setText(vein_pulse_max_pre);
					break;
				default:
					break;
				}
			}
		});
		
		
		
		
		mDlg_VeinConstPreEt.setOnClickListener(this);
		mDlg_VeinConstPreEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				String strveinConstPre = editable.toString();
				float veinConstPre = StringUtil.convertToFloat(strveinConstPre, 0.0f);
				if(mTempMode == 0){//常温灌注不高于14mmHg，低温灌注不高于8mmHg
					if(veinConstPre > 100.0f){
						editable.replace(0,editable.length(), "100");
						displayToast(R.string.error_max_value_normal_perfusion_target_const_right_kidney_artery_pre);
						mDlg_VeinTarTxt.setText(100+"");
						return;
					}
				}else if(mTempMode == 1){
					if(veinConstPre > 40.0f){
						editable.replace(0,editable.length(), "40");
						displayToast(R.string.error_max_value_cold_perfusion_target_const_right_kidney_artery_pre);
						mDlg_VeinTarTxt.setText(40+"");
						return;
					}
				}
				if(strveinConstPre.endsWith(".")){
					displayToast(R.string.error_value_end_with_decimal);
					return;
				}
				mDlg_VeinTarTxt.setText(editable);
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String vein_const_pressure = mDlg_VeinConstPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_pressure)){
					mDlg_VeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_VeinTarTxt.setText(vein_const_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_VeinTarTxt.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_pressure = mDlg_VeinConstPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_pressure)){
					mDlg_VeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_VeinTarTxt.setText(vein_const_pressure);
				}
			}
		});
		mDlg_VeinConstFlowEt.setOnClickListener(this);
		mDlg_VeinConstFlowEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float vein_flow = StringUtil.convertToFloat(editable.toString(),0.0f);  				
				if(vein_flow > 1500.0f){
					editable.replace(0,editable.length(), "1500");
					mDlg_VeinTarTxt.setText(1500+"");		
					displayToast(R.string.error_max_value_right_kidney_target_flow);
					return;						
				}else{
					mDlg_VeinTarTxt.setText(editable);
				}
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String vein_const_flow = mDlg_VeinConstFlowEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mDlg_VeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_VeinTarTxt.setText(vein_const_flow);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_VeinTarTxt.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String vein_const_flow = mDlg_VeinConstFlowEt.getText().toString().trim();
				if(StringUtil.isEmpty(vein_const_flow)){
					mDlg_VeinTarTxt.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_VeinTarTxt.setText(vein_const_flow);
				}
			}
		});
		
		mDlg_VeinMaxTarPreEt.setOnClickListener(this);
		mDlg_VeinMaxTarPreEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				float maxTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(maxTarPre > 120.0f){
					editable.replace(0,editable.length(), "120");
					displayToast(R.string.error_target_right_kidney_pre_max_limit);
					mDlg_MaxVeinTv.setText(120+"");
				}else{
					mDlg_MaxVeinTv.setText(editable);
				}

				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_max_pressure = mDlg_VeinMaxTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mDlg_MaxVeinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MaxVeinTv.setText(art_max_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_MaxVeinTv.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_max_pressure = mDlg_VeinMaxTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_max_pressure)){
					mDlg_MaxVeinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MaxVeinTv.setText(art_max_pressure);
				}
			}
		});
		

		mDlg_VeinMinTarPreEt.setOnClickListener(this);
		mDlg_VeinMinTarPreEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub

				float minTarPre = StringUtil.convertToFloat(editable.toString(), 0.0f);			
				if(minTarPre > 100.0f){
					editable.replace(0,editable.length(), "100");
					displayToast(R.string.error_target_right_kidney_pre_min_limit);	
					mDlg_MinVeinTv.setText(100 + "");
				}else{
					mDlg_MinVeinTv.setText(editable);
				}

			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				String art_min_pressure = mDlg_VeinMinTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mDlg_MinVeinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MinVeinTv.setText(art_min_pressure);
				}
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				mDlg_MinVeinTv.setText(getResources().getString(R.string.string_null));
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				String art_min_pressure = mDlg_VeinMinTarPreEt.getText().toString().trim();
				if(StringUtil.isEmpty(art_min_pressure)){
					mDlg_MinVeinTv.setText(getResources().getString(R.string.string_null));
				}else{
					mDlg_MinVeinTv.setText(art_min_pressure);
				}
			}
		});
	}
	
	
	
	private void savePerfusionParams(){
		
		saveLiverWeightParam();
		boolean isArtSet = false;
		boolean isVeinSet = false;
		boolean isChangeArtMode = false;
		boolean isChangeVeinMode = false;
		int mArtMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.LEFT_KIDNEY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		int mVeinMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.RIGHT_KIDNEY_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		if(mArtMode != mLastArtMode){
			isChangeArtMode = true;
		} 
		Log.i("111", "---savePerfusionParams---" + "mVeinMode---" + "   " + mVeinMode + "mLastVeinMode" + "   "+ mLastVeinMode);
		if(mVeinMode != mLastVeinMode){
			isChangeVeinMode = true;
		}
		float artConstPre = StringUtil.convertToFloat(mArtTarPreTxt.getContentText(), 0.0f);	
		float artTarMinPre = StringUtil.convertToFloat(mArtMinTarPreTxt.getContentText(), 0.0f);
		float artTarMaxPre = StringUtil.convertToFloat(mArtMaxTarPreTxt.getContentText(), 0.0f);
		float artConstFlow = StringUtil.convertToFloat(mArtTarFlowTv.getContentText(), 0.0f);	
		
			
		float veinTarMinPre = StringUtil.convertToFloat(mVeinMinTarPreTv.getContentText(), 0.0f);
		float veinTarMaxPre = StringUtil.convertToFloat(mVeinMaxTarPreTv.getContentText(), 0.0f);
		float veinConstPre = StringUtil.convertToFloat(mVeinTarPreTxt.getContentText(), 0.0f);
		float veinConstFlow = StringUtil.convertToFloat(mVeinTarFlowTxt.getContentText(), 0.0f);
		
		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE ){
			//1、左肾动脉恒压、右肾动脉恒压
			switch (mVeinMode) {
			case Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE:
				if(mLastArtTarPre != artConstPre){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, artConstPre);
				
				}
				if(mLastVeinTarPre != veinConstPre){
					isVeinSet = true;
					PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, veinConstPre);
				}
				break;
			case Constants.RIGHT_ARTERY_CONST_FLOW_MODE:
				if(mLastArtTarPre != artConstPre){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, artConstPre);			
				}
				if(mLastVeinTarFlow != veinConstFlow){
					isVeinSet = true;
					PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, veinConstFlow);
				}
				break;
			case Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE:
				//3、左肾动脉恒压、右肾动脉搏动
				if(mLastArtTarPre != artConstPre){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, artConstPre);			
				}
				if(mLastVeinMinTarPre != veinTarMinPre || mLastVeinMaxTarPre != veinTarMaxPre){
					isVeinSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, veinTarMinPre);
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, veinTarMaxPre);
				}
				break;
			default:
				break;
			}
									
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE ){
			switch (mVeinMode) {
			case Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE:
				//4、左肾动脉搏动，右肾动脉恒压
				if(mLastArtMinTarPre != artTarMinPre || mLastArtMaxTarPre != artTarMaxPre){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, artTarMinPre);
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, artTarMaxPre);
				}
				if(mLastVeinTarPre != veinConstPre){
					isVeinSet = true;
					PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, veinConstPre);
				}
				break;
			case Constants.RIGHT_ARTERY_CONST_FLOW_MODE:
				//5、左肾动脉搏动，右肾动脉恒流
				if(mLastArtMinTarPre != artTarMinPre || mLastArtMaxTarPre != artTarMaxPre){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, artTarMinPre);
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, artTarMaxPre);
				}
				if(mLastVeinTarFlow != veinConstFlow){
					isVeinSet = true;
					PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, veinConstFlow);
				}
				break;
				
			case Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE:
				//6、左肾动脉搏动，右肾动脉搏动
				if(mLastArtMinTarPre != artTarMinPre || mLastArtMaxTarPre != artTarMaxPre){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, artTarMinPre);
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, artTarMaxPre);
				}
				if(mLastVeinMinTarPre != veinTarMinPre || mLastVeinMaxTarPre != veinTarMaxPre){
					isVeinSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, veinTarMinPre);
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, veinTarMaxPre);
				}
				break;
			default:
				break;
			}
			
		}else if(mArtMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE){
			//左肾动脉模式----恒流
			switch (mVeinMode) {
			case Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE:
				if(mLastArtTarFlow != artConstFlow){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, artConstFlow);			
				}
				if(mLastVeinMinTarPre != veinTarMinPre || mLastVeinMaxTarPre != veinTarMaxPre){
					isVeinSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, veinTarMinPre);
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, veinTarMaxPre);
				}
				break;
			case Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE:
				//8、左肾动脉恒流，右肾动脉恒压
				if(mLastArtTarFlow != artConstFlow){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, artConstFlow);			
				}
				if(mLastVeinTarPre != veinConstPre){
					isVeinSet = true;
					PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, veinConstPre);
				}
				break;
				
			case Constants.RIGHT_ARTERY_CONST_FLOW_MODE:
				if(mLastArtTarFlow != artConstFlow){
					isArtSet = true;
					PreferenceUtil.getInstance(mContext.getApplicationContext())
						.setValueByName(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, artConstFlow);			
				}
				if(mLastVeinTarFlow != veinConstFlow){
					isVeinSet = true;
					PreferenceUtil.getInstance(getActivity().getApplicationContext())
						.setValueByName(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, veinConstFlow);			
				}
				
				break;
			default:
				break;
			}

		}
			

		if(null != onSettingPerfusionListener){
			onSettingPerfusionListener.onFinishSetPerfison(isArtSet, isVeinSet,isChangeArtMode,isChangeVeinMode);
		}
	
	}
	
	private void saveLiverWeightParam(){
		int left_kidney_Weight = StringUtil.convertToInt(mLeftKidneyWeightEt.getText().toString().trim(), 0);
		int right_kidney_Weight = StringUtil.convertToInt(mRightKidneyWeightEt.getText().toString().trim(), 0);
		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.LEFT_KIDNEY_WEIGHT, left_kidney_Weight);
		PreferenceUtil.getInstance(getBaseActivity().getApplicationContext())
			.setValueByName(SharedConstants.RIGHT_KIDNEY_WEIGHT, right_kidney_Weight);

	}
	

	/**
	 * 比较肝动脉搏动模式下压力差值，目标压力差值不得高于40mmHg
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	private boolean compareArtPulsePreDiff(float minValue, float maxValue){
		if((maxValue - minValue) > 40.0f){
			return 	true;
		}
		return false;
	}
	
	private class SerialMsgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(null != mApplication){
				if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL)){
					String pump1_Preal = intent.getStringExtra(SerialMsgConstant.ARTERY_PREAL);	
					if(null != pump1_Preal && null != mSystemAlarmMgr){
						if(pump1_Preal.equals("error1")){
							//肝动脉压力断连						
							if(mSystemAlarmMgr.getArtPreAlarmStatus() != 1){
								mSystemAlarmMgr.setArtPreAlarmStatus(1);													
								mCurArtPreTxt.setText(R.string.string_null);
							}					
						}else{
							
							if(mSystemAlarmMgr.getArtPreAlarmStatus() != 0){							
								mSystemAlarmMgr.setArtPreAlarmStatus(0);	
							}
							mCurArtPreTxt.setText(pump1_Preal);					
						}
					}
					
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL)){
					String pump2_Preal = intent.getStringExtra(SerialMsgConstant.VEIN_PREAL);					
					if(null != pump2_Preal && null != mSystemAlarmMgr){
						if(pump2_Preal.equals("error1")){
							//肝动脉流量断连						
							if(mSystemAlarmMgr.getVeinPreAlarmStatus() != 1){
								mSystemAlarmMgr.setVeinPreAlarmStatus(1);													
								mCurVeinPreTxt.setText(R.string.string_null);
							}					
						}else{
							
							if(mSystemAlarmMgr.getVeinPreAlarmStatus() != 0){							
								mSystemAlarmMgr.setVeinPreAlarmStatus(0);	
								
							}
							mCurVeinPreTxt.setText(pump2_Preal);
											
						}
					}
										
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL)){
					String pump1_Freal = intent.getStringExtra(SerialMsgConstant.ARTERY_FREAL);
					if(null != pump1_Freal){
						if(pump1_Freal.equals("error1")){
							if(mSystemAlarmMgr.getArtFlowAlarmStatus() !=1){
								mSystemAlarmMgr.setArtFlowAlarmStatus(1);
								mCurArtFlowTxt.setText(R.string.string_null);
								LogUtil.i(TAG, "disconnect artery flow");	
							}
						}else if(pump1_Freal.equals("error2")){
							if(mSystemAlarmMgr.getArtFlowAlarmStatus() !=2){
								mSystemAlarmMgr.setArtFlowAlarmStatus(2);
								mCurArtFlowTxt.setText(R.string.string_null);
								LogUtil.i(TAG, "abnormal artery flow");	
							}
						}else{
							if(mSystemAlarmMgr.getArtFlowAlarmStatus() != 0){
								mSystemAlarmMgr.setArtFlowAlarmStatus(0);
							}
							mCurArtFlowTxt.setText(pump1_Freal);
						}
					}
					
									
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL)){
					String pump2_Freal = intent.getStringExtra(SerialMsgConstant.VEIN_FREAL);
					if(pump2_Freal != null){
						if(pump2_Freal.equals("error1")){
							if(mSystemAlarmMgr.getVeinFlowAlarmStatus() !=1){
								mSystemAlarmMgr.setVeinFlowAlarmStatus(1);
							}
						}else if(pump2_Freal.equals("error2")){
							if(mSystemAlarmMgr.getVeinFlowAlarmStatus() !=2){
								mSystemAlarmMgr.setVeinFlowAlarmStatus(2);
							}
						}else{
							if(mSystemAlarmMgr.getVeinFlowAlarmStatus()!=0){
								mSystemAlarmMgr.setVeinFlowAlarmStatus(0);
							}
							mCurVeinFlowTxt.setText(pump2_Freal);
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
					String result = intent.getStringExtra("pump2_Pzero");
					if(null != mUIHandler && null != result){
						Message msg = Message.obtain();
						msg.what = Constants.MSG_ZERO_PRESSURE_TWO;
						msg.obj = result;
						mUIHandler.sendMessage(msg);
					}
				}else if(action.equals(BroadcastActions.ACTION_ZERO_PUMP_ONE_FREAL)){
					//肝动脉流量调零
					String result = intent.getStringExtra("pump1_Fzero");
					if(null != mUIHandler && null != result){
						Message msg = Message.obtain();
						msg.what = Constants.MSG_ZERO_FLOW_ONE;
						msg.obj = result;
						mUIHandler.sendMessage(msg);
					}
				}else if(action.equals(BroadcastActions.ACTION_ZERO_PUMP_TWO_FREAL)){
					//门静脉流量调零
					String result = intent.getStringExtra("pump2_Fzero");
					if(null != mUIHandler && null != result){
						Message msg = Message.obtain();
						msg.what = Constants.MSG_ZERO_FLOW_TWO;
						msg.obj = result;
						mUIHandler.sendMessage(msg);
					}
				}
			}
			
		}		
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.setting_perfusion_artery_zero_flow_img:
			if(mApplication != null){
				if(mApplication.isStopPump()){
					showZeroFlowDialog(0);
				}else{
					ToastUtils.showToast(getActivity().getApplicationContext(), 
							getActivity().getResources().getString(R.string.dialog_notice_zero_flow)
							, R.drawable.preset_btn_zero_enable);
				}
			}

			break;
			
		case R.id.setting_perfusion_artery_zero_pre_img:
			showZeroPressureDialog(0);			
			break;
			
		case R.id.setting_perfusion_vein_zero_flow_img:			
			if(mApplication != null){
				if(mApplication.isStopPump()){
					showZeroFlowDialog(1);
				}else{
					ToastUtils.showToast(getActivity().getApplicationContext(), 
							getActivity().getResources().getString(R.string.dialog_notice_zero_flow)
							, R.drawable.preset_btn_zero_enable);
				}
			}
			break;
			
		case R.id.setting_perfusion_vein_zero_pre_img:
			showZeroPressureDialog(1);	
			break;	
		case R.id.setting_perfusion_artery_pressure_layout:
			showChangeArtModeDialog();
			break;
			
		case R.id.setting_perfusion_vein_mode_layout:
			showChangeVeinModeDialog();
			break;
			
		case R.id.setting_art_max_tarpressure_et:
			setSelectedView(mDlg_ArtMaxTarPreEt);
			mDlg_MaxArtRg.clearCheck();
			clearEdit(mDlg_ArtMaxTarPreEt);
			break;
			
		case R.id.setting_art_min_tarpressure_et:
			setSelectedView(mDlg_ArtMinTarPreEt);
			mDlg_MinArtRg.clearCheck();
			clearEdit(mDlg_ArtMinTarPreEt);
			
			break;
		case R.id.setting_art_const_tarpressure_et:
			setSelectedView(mDlg_ArtConstPEt);
			mDlg_ConstArtPreRg.clearCheck();
			clearEdit(mDlg_ArtConstPEt);
			break;
			
		case R.id.setting_art_const_tarflow_et:
			setSelectedView(mDlg_ArtConstFEt);
			mDlg_ConstArtFlowRg.clearCheck();
			clearEdit(mDlg_ArtConstFEt);
			break;
			
		case R.id.dlg_setting_vein_max_tarpressure_et:
			setSelectedView(mDlg_VeinMaxTarPreEt);
			mDlg_VeinMaxPreRg.clearCheck();
			clearEdit(mDlg_VeinMaxTarPreEt);
			break;
		case R.id.dlg_setting_vein_min_tarpressure_et:
			setSelectedView(mDlg_VeinMinTarPreEt);
			mDlg_VeinMinPreRg.clearCheck();
			clearEdit(mDlg_VeinMinTarPreEt);
			break;
			
		case R.id.dlg_setting_vein_target_pressure_et:
			setSelectedView(mDlg_VeinConstPreEt);
			mDlg_VeinConstPreRg.clearCheck();
			clearEdit(mDlg_VeinConstPreEt);
			break;
			
		case R.id.dlg_setting_vein_target_flow_et:
			setSelectedView(mDlg_VeinConstFlowEt);
			mDlg_VeinConstFlowRg.clearCheck();
			clearEdit(mDlg_VeinConstFlowEt);
			break;
		default:
			break;
		}
	}
	
	private void clearEdit(SystemKeyBoardEditText edittext){
		edittext.setText("");
	}

	private static class UIHandler extends Handler{
		
		private final WeakReference<KidneySetPerfusionFragment> mFragReference;
		public UIHandler(KidneySetPerfusionFragment fragment) {  
			mFragReference = new WeakReference<KidneySetPerfusionFragment>(fragment);  
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
				String result = (String)msg.obj;				
				if(result.equals("succe")){
					if(!mFragReference.get().mIsArteryZeroSuc){
						mFragReference.get().displayToast(R.string.success_zero_left_kidney_artery_pressure);
						mFragReference.get().mIsArteryZeroSuc = true;
					}
				}else if(result.endsWith("erro")){
					mFragReference.get().displayToast(R.string.error_pre_left_kidney_artery_zero_fail);
				}	
				
				
				break;
				
			case Constants.MSG_ZERO_PRESSURE_TWO:
				String result_two = (String)msg.obj;
				if(result_two.equals("succe")){
					if(!mFragReference.get().mIsVeinZeroSuc){
						mFragReference.get().displayToast(R.string.success_zero_right_kidney_artery_pressure);
						mFragReference.get().mIsVeinZeroSuc = true;
					}						
				}else if(result_two.equals("erro")){
					mFragReference.get().displayToast(R.string.error_pre_right_kidney_artery_zero_fail);
				}							
				break;


			default:
				break;
			}
		}  
		 
	}
	

}
