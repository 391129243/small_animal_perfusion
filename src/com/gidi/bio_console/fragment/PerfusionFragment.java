package com.gidi.bio_console.fragment;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.common.ErrorCode;
import com.gidi.bio_console.common.NormalCode;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.DefValues;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.mgr.SystemAlarmMgr;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.view.CustomParasTextView;
import com.gidi.bio_console.view.CustomSecParamText;

public class PerfusionFragment extends BaseFragment{

	private static final String TAG = "PerfusionFragment";
	private SerialMsgReceiver mSerialMsgReceiver;
	private Context mContext;
	private BioConsoleApplication mApplication = null;
	private SystemAlarmMgr mSystemAlarmMgr = null;
	private CustomParasTextView mArtRealPTxt;
	/**目标压力/实际压力**/
	private CustomParasTextView mArtFlowTxt;
	public CustomParasTextView mVeinPreTxt;
	private CustomParasTextView mVeinFlowTxt;
	/**门静脉目标压力**/
	private CustomParasTextView mArtTarPTxt;
	private CustomSecParamText mArtSpeedTxt;
	private CustomSecParamText mVeinSpeedTxt;
	private CustomSecParamText mPulseRateTxt;

	private CustomParasTextView mTempTxt;
	private CustomParasTextView mResisIndexTxt;//阻力指数
	/**流量/目标流量**/
//	private CustomParasTextView mFlowPercentTxt;

	private LinearLayout mArtPreLayout;
	private LinearLayout mArtFlowLayout;
	private LinearLayout mVeinPreLayout;
	private LinearLayout mVeinFlowLayout;
	
	/**肝动脉流量、压力，门静脉流量，压力图表**/
	private GraphicalView mArtPreChart;
	private GraphicalView mArtFlowChart;
	private GraphicalView mVeinPreChart;
	private GraphicalView mVeinFlowChart;
	
    private XYMultipleSeriesDataset mArtPreDataset;
    private XYMultipleSeriesDataset mArtFlowDataset;
    private XYMultipleSeriesDataset mVeinPreDataSet;
    private XYMultipleSeriesDataset mVeinFlowDataSet;
    
    private XYMultipleSeriesRenderer mArtPreRenderer;
    private XYMultipleSeriesRenderer mArtFlowRender;
    private static final int SERIES_NR=1;
    private TimeSeries mArtPreSeries;
    private TimeSeries mArtFlowSeries;
    private TimeSeries mArtFMeanSeries;//平均流量
    private TimeSeries mVeinPreSeries;
    private TimeSeries mVeinFlowSeries;

    private ScheduledExecutorService mScheduledThreadPool;
    private TimerTask mArtPreChartTask;
    private TimerTask mArtFlowChartTask; 
    private TimerTask mVeinPreChartTask;
    private TimerTask mVeinFlowChartTask; 
	private DecimalFormat df = new DecimalFormat("#0.0");  
    private float mArtPreY = 0.0f;
    private long mArtPreX;
    private float mVeinPreY = 0.0f;
    private long mVeinPreX;
    private float mArtFlowY = 0.0f;
    private long mArtFlowX;
    private long mArtFmeanX;
    private float mVeinFlowY = 0.0f;
    private long mVeinFlowX;
    private Date[] xArtPreCache = new Date[400];
    private int[] yArtPrecache = new int[400];
    private Date[] xVeinPreCache = new Date[400];
    private int[] yVeinPrecache = new int[400];
    
    private Date[] xArtFlowCache = new Date[400];
    private float[] yArtFlowCache = new float[400];
    private Date[] xVeinFlowCache = new Date[400];
    private float[] yVeinFlowCache = new float[400];
    private Date[] xArtFmeanCache = new Date[400];
    private float[] yArtFmeanCache = new float[400];
	/****/
	private float mArtTarPre;
	private float mArtTarFlow;
	private float mArtMinTarPre;
	private float mArtMaxTarPre;
	private float mVeinTarPre;
	private float mVeinTarFlow;
	private float mVeinConstFlow;
	
	private float mArtMeanPre;
	private float mVeinMeanPre;
	private int mArtMeanFlow;
	private int mVeinMeanFlow;
	
	private float mArtPreAlertMin = 0.0f;
	private float mArtPreAlertMax = 120.0f; 
	private float mArtFlowAlertMin = 0.0f;
	private float mArtFlowAlertMax = 1000.0f; 
	private float mVeinPreAlertMin = 0.0f;
	private float mVeinPreAlertMax = 14.0f;
	private float mVeinFlowAlertMin = 0.0f;
	private float mVeinFlowAlertMax = 2000.0f; 
	private boolean isTempFlag= false;
	private float targetTemp = 0.0f;
	
	private int mVeinMode = Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE;//2:const p 3:const f
	private int mArtMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE; // 0 ：const 1：pulse
	private int mTempMode; //0:normal 1:cold

	private int liver_weight;
	private long lastckeckTemp =0;
	private int index =0;
	
	private UIHandler mUIHandler = null;
    private static final int UPDATE_ART_PRESSURE_CHAT = 400;
    private static final int UPDATE_ART_FLOW_CHAT = 401;
    private static final int UPDATE_VEIN_PRESSURE_CHAT = 402;
    private static final int UPDATE_VEIN_FLOW_CHAT = 403;
    
    private OnPerfusionListener mListener;
    
	public interface OnPerfusionListener{
		void onPressureStatus(int pumptype, boolean isAlarm,int errorCode);
		void onFlowStatus(int pumptype,boolean isAlarm,int errorCode);
		void onTempStatus(int temp_status,boolean isAlarm);
	}
    
	public void setOnPerfusionListener(OnPerfusionListener listener){
		this.mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext  = getActivity();
		mApplication = (BioConsoleApplication)getActivity().getApplication();
		mSystemAlarmMgr = SystemAlarmMgr.getInstance(mApplication);
		mScheduledThreadPool = Executors.newScheduledThreadPool(4);
		registerReceiver();
		initVariables();		
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshPerfusionData();			
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
		}
		unregisterReceiver();
		cancelTask();	
		//防止内存泄漏
		if(null != mListener){
			mListener = null;
		}
		mSystemAlarmMgr = null;
		mApplication = null;
	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_perfusion;
	}
	

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mArtRealPTxt = (CustomParasTextView)rootView.findViewById(R.id.perfusion_artery_pre_txt);
		mArtFlowTxt = (CustomParasTextView)rootView.findViewById(R.id.perfusion_artery_flow_txt);
		mVeinPreTxt = (CustomParasTextView)rootView.findViewById(R.id.perfusion_vein_pre_txt);
		mVeinFlowTxt = (CustomParasTextView)rootView.findViewById(R.id.perfusion_vein_flow_txt);
		mArtTarPTxt = (CustomParasTextView)rootView.findViewById(R.id.perfusion_artery_tar_pre_txt);
		mPulseRateTxt = (CustomSecParamText)rootView.findViewById(R.id.perfusion_artery_pulse_rate_txt);
		mArtSpeedTxt = (CustomSecParamText)rootView.findViewById(R.id.perfusion_artery_speed_txt);
		//mBileTxt = (CustomParasIconView)rootView.findViewById(R.id.perfusion_artery_ebile_txt);
		mTempTxt = (CustomParasTextView)rootView.findViewById(R.id.perfusion_temp_txt);
		mVeinSpeedTxt = (CustomSecParamText)rootView.findViewById(R.id.perfusion_vein_speed_txt);
		mResisIndexTxt = (CustomParasTextView)rootView.findViewById(R.id.perfusion_resistance_txt);
		mResisIndexTxt.setRightContextColor(getResources().getColor(R.color.color_perfusion_vein_flow));
		//mFlowPercentTxt= (CustomParasTextView)rootView.findViewById(R.id.perfusion_flow_percent_txt);
		//mFlowPercentTxt.setRightContextColor(getResources().getColor(R.color.color_perfusion_vein_flow));
		
		mArtPreLayout = (LinearLayout)rootView.findViewById(R.id.perfusion_art_pressure_chart);
		mArtFlowLayout = (LinearLayout)rootView.findViewById(R.id.perfusion_art_flow_chart);
		mVeinPreLayout = (LinearLayout)rootView.findViewById(R.id.perfusion_vein_pressure_chart);
		mVeinFlowLayout = (LinearLayout)rootView.findViewById(R.id.perfusion_vein_flow_chart);

	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		initChart();
	}
	
	private void initVariables(){
		mVeinMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		mArtMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		mTempMode = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getIntValue(SharedConstants.TEMP_PERFUSION_MODE, 0);
		mUIHandler = new UIHandler(this);
		mArtPreDataset = new XYMultipleSeriesDataset();		

	}
	
	private void initChart(){
        //初始化肝动脉压力图表		
		float mPreYMax = 0.0f;
		float mPreYMin = 0.0f;
		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			//恒压
			mArtTarPre =  PreferenceUtil.getInstance(mContext.getApplicationContext())
					.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
			mPreYMax = mArtTarPre + 20.0f;
			mPreYMin = mArtTarPre - 20.0f;
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			mArtMinTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext()).getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, 0.0f);
			mArtMaxTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext()).getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, 0.0f);
			mPreYMax = mArtMaxTarPre + 10.0f;
			mPreYMin = mArtMinTarPre - 10.0f;
		}
		
		//肝动脉压力
		mArtPreRenderer = getPreRenderer(mPreYMax,mPreYMin,
				getResources().getString(R.string.hepatic_artery_pressure),R.color.color_artery_pressure_chart_wave,1,5);
		mArtPreChart = ChartFactory.getTimeChartView(getActivity(), getArtPreDataSet(), mArtPreRenderer, "hh:mm:ss");
		mArtPreLayout.addView(mArtPreChart, new LayoutParams(LayoutParams.WRAP_CONTENT,190));
		mArtPreChartTask = new TimerTask() {
	        	@Override
	        	public void run() {	        		     	    
	        	    mUIHandler.sendEmptyMessage(UPDATE_ART_PRESSURE_CHAT);
	        	}
	        };
	    
	    mScheduledThreadPool.scheduleAtFixedRate(mArtPreChartTask, 20,100, TimeUnit.MILLISECONDS);
	   

	    mArtFlowRender = getFlowRenderer(600.0f,-100.0f,
	    		getResources().getString(R.string.hepatic_artery_flow),R.color.color_artery_flow_chart_wave,2,3);    
	    mArtFlowChart = ChartFactory.getTimeChartView(getActivity(), getArtFlowDataSet(), mArtFlowRender, "hh:mm:ss");
	    mArtFlowLayout.addView(mArtFlowChart, new LayoutParams(LayoutParams.WRAP_CONTENT,158));
	   
	    mArtFlowChartTask = new TimerTask() {
		   @Override
		   public void run() {	        		     	    
       	    	mUIHandler.sendEmptyMessage(UPDATE_ART_FLOW_CHAT);
       	   }
	    };

	    mScheduledThreadPool.scheduleAtFixedRate(mArtFlowChartTask, 20,100, TimeUnit.MILLISECONDS);
	   
	    /**门静脉图表**/
	    mVeinTarPre =  PreferenceUtil.getInstance(mContext.getApplicationContext())
	    		.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
		
	    float mVeinPreYMax = 15.f;
	    float mVeinPreYMin = 0;
		//初始化门静脉压力图表
	    XYMultipleSeriesRenderer mVeinPreRender = getPreRenderer(mVeinPreYMax,mVeinPreYMin,getResources()
	    		.getString(R.string.portal_vein_pressure),R.color.color_vein_pressure_chart_wave,1,5);
		mVeinPreChart = ChartFactory.getTimeChartView(getActivity(), getVeinPreDataSet(), mVeinPreRender, "hh:mm:ss");
		mVeinPreLayout.addView(mVeinPreChart, new LayoutParams(LayoutParams.WRAP_CONTENT,148));
		mVeinPreChartTask = new TimerTask() {
	        	@Override
	        	public void run() {	        		     	    
	        	    mUIHandler.sendEmptyMessage(UPDATE_VEIN_PRESSURE_CHAT);
	        	    
	        	}
	     };

	    mScheduledThreadPool.scheduleAtFixedRate(mVeinPreChartTask, 20,100, TimeUnit.MILLISECONDS);
	    XYMultipleSeriesRenderer mVeinRender = getFlowRenderer(2000.0f,0.0f,getResources()
	    		.getString(R.string.portal_vein_flow),R.color.color_vein_flow_chart_wave,1,5);
	    mVeinFlowChart = ChartFactory.getTimeChartView(getActivity(), getVeinFlowDataSet(),mVeinRender, "hh:mm:ss");
	    mVeinFlowLayout.addView(mVeinFlowChart, new LayoutParams(LayoutParams.WRAP_CONTENT,158));
	    mVeinFlowChartTask = new TimerTask() {
	        	@Override
	        	public void run() {	        		     	    
	        	    mUIHandler.sendEmptyMessage(UPDATE_VEIN_FLOW_CHAT);
	        	}
	        };

	   mScheduledThreadPool.scheduleAtFixedRate(mVeinFlowChartTask, 20,100, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 初始化显示目标数据
	 * **/
	private void refreshPerfusionData(){
		mArtMode = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getIntValue(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		mVeinMode = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getIntValue(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		mArtTarFlow = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_ARTERY_FLOW, 0.0f);
		mVeinTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
		mVeinTarFlow = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_VEIN_FLOW, 0.0f);
		mArtTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
		mVeinConstFlow = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, 0.0f);
		mArtMinTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, 0.0f);
		mArtMaxTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, 0.0f);

		
		mArtPreAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MAX_ONE, 120.0f);
		mArtPreAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MIN_ONE, 0.0f);
		mArtFlowAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MAX_ONE, 1000.0f);
		mArtFlowAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MIN_ONE, 0.0f);
		mVeinPreAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MAX_TWO, 14.0f);
		mVeinPreAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MIN_TWO, 0.0f);
		mVeinFlowAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MAX_TWO, 2000.0f);
		mVeinFlowAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MIN_TWO, 0.0f);
		
		liver_weight = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getIntValue(SharedConstants.LIVER_WEIGHT, 0);
		targetTemp = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_TEMP, 37.0f);		
		mTempTxt.setRightContentText(String.valueOf(targetTemp));

		if(mArtTarFlow != 0.0f){			
			//mArtFlowTxt.setRightContentText(new DecimalFormat("#0.000").format(mArtTarFlow));
		}else{
			mArtTarFlow = 0.0f;
			//mArtFlowTxt.setRightContentText(String.valueOf(mArtTarFlow));
		}

		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			//恒压
			mArtTarPTxt.setContentVisible(false);
			mArtTarPTxt.setSecContentVisible(true);
			mArtTarPTxt.setSecondContentText(String.valueOf(mArtTarPre));
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			//搏动
			mArtTarPTxt.setContentVisible(true);
			mArtTarPTxt.setSecContentVisible(false);
			mArtTarPTxt.setRightContentText(String.valueOf(mArtMinTarPre));
			mArtTarPTxt.setLeftContentText(String.valueOf(mArtMaxTarPre));
		}
		
		//门静脉恒压/恒流
		if(mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			//const pressure
			mVeinPreTxt.setRightContentText(String.valueOf(mVeinTarPre));
			mVeinFlowTxt.setRightContentText(new DecimalFormat("#0.0").format(mVeinTarFlow));
		}else if(mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			//const flow
			mVeinPreTxt.setRightContentText(mContext.getString(R.string.string_null));
			mVeinFlowTxt.setRightContentText(String.valueOf(mVeinConstFlow));
		}		
		
		

	}
	
	
	 private void updateArtPreChartYRange(float YMax, float YMin){
		   Log.i(TAG, "--updatePrechartXRange--");
		   mArtPreRenderer.setYAxisMax(YMax);
		   mArtPreRenderer.setYAxisMin(YMin);
		   mArtPreLayout.removeAllViews();
		   mArtPreChart = ChartFactory.getTimeChartView(getActivity(), mArtPreDataset, mArtPreRenderer, "hh:mm:ss");	   
		   mArtPreLayout.addView(mArtPreChart, new LayoutParams(LayoutParams.WRAP_CONTENT,150));
	 }
	
	private void registerReceiver(){
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_SREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_TREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_QREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PFREQ);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PDIAS);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PSYST);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FMEAN);
		
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_SREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_QREAL);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FMEAN);
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_EBILE);
		
		mIntentFilter.addAction(BroadcastActions.ACTION_RECEIVE_TARGET_TEMP);//目标温度
		mSerialMsgReceiver = new SerialMsgReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSerialMsgReceiver, mIntentFilter);	
	}
	
	private void unregisterReceiver(){
		if(null != mSerialMsgReceiver){
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSerialMsgReceiver);
			mSerialMsgReceiver = null;			
		}
	}
	
	/**convert ml/min to ml/min/100g**/
	private String convertFMeantoHundredGrams (float fmean_float){
		float num = (float)fmean_float * 100/liver_weight;
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(num);
	}	
	
	/**阻力指数**/
	private void setResIndexParameters(){
		if(null != mResisIndexTxt){
			if(mArtMeanPre != 0.0f && mArtMeanFlow != 0){
				float resist = (float)mArtMeanPre/mArtMeanFlow;
				DecimalFormat df = new DecimalFormat("0.000");
				mResisIndexTxt.setLeftContentText(df.format(resist));	
			}
			if(mArtMeanFlow <=0){
				mResisIndexTxt.setLeftContentText("∞");
			}
			
			if(mVeinMeanPre != 0.0f && mVeinMeanFlow != 0){
				float resist = (float)mVeinMeanPre/mVeinMeanFlow;
				DecimalFormat df = new DecimalFormat("0.000");
				mResisIndexTxt.setRightContentText(df.format(resist));	
			}
			if(mVeinMeanFlow <= 0){
				mResisIndexTxt.setRightContentText("∞");
			}
		}
	}
	
	private String convertBileCapacity(int bile){
		if(bile !=0){
			float bileCapacity = (float)bile/20;
			DecimalFormat df = new DecimalFormat("0.00");
			return df.format(bileCapacity);		
		}else{
			return "0";
		}
	}

	/**处理压力极限值**/
	private void onUpdatePreLimitedMsg(int pumpType,float realValue , float minValue, float maxValue){
		if(null != getBaseActivity()){
			if(realValue >= maxValue){
				if(pumpType == 0){
					if(mSystemAlarmMgr.getArtWarnPreStatus() != 2){
						mSystemAlarmMgr.setArtWarnPreStatus(2);
					}
					//mArtFlowTxt.setLeftContextColor(getResources().getColor(R.color.red));
					onErrorMsg(ErrorCode.E_PESSURE_ONE_MAX_AlARM_MSG);
				}else if(pumpType == 1){
					if(mSystemAlarmMgr.getVeinWarnPreStatus() != 2){
						mSystemAlarmMgr.setVeinWarnPreStatus(2);
					}
					mVeinPreTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
					onErrorMsg(ErrorCode.E_PESSURE_TWO_MAX_AlARM_MSG);
				}		
				
							
			}else if(realValue <= minValue){
				if(pumpType == 0){
					if(mSystemAlarmMgr.getArtWarnPreStatus() != 1){
						mSystemAlarmMgr.setArtWarnPreStatus(1);
					}					
					onErrorMsg(ErrorCode.E_PESSURE_ONE_MIN_AlARM_MSG);
				}else if(pumpType ==1){
					if(mSystemAlarmMgr.getVeinWarnPreStatus() != 1){
						mSystemAlarmMgr.setVeinWarnPreStatus(1);
					}
					mVeinPreTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
					onErrorMsg(ErrorCode.E_PESSURE_TWO_MIN_AlARM_MSG);
				}	
				
				
			}else{
				if(pumpType == 0){
					if(mSystemAlarmMgr.getArtWarnPreStatus() != 0){
						mSystemAlarmMgr.setArtWarnPreStatus(0);
					}
					
					onNormalMsg(NormalCode.N_PESSURE_ONE_MSG);	
				}else{
					if(mSystemAlarmMgr.getVeinWarnPreStatus() != 0){
						mSystemAlarmMgr.setVeinWarnPreStatus(0);
					}
					mVeinPreTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.color_perfusion_vein_pre));
					onNormalMsg(NormalCode.N_PESSURE_TWO_MSG);	
				}
									
						
			}
		}
		
	}
	
	/**处理流量极限值**/
	private void onUpdateFlowLimitedMsg(int pumpType, String realValue , float minValue, float maxValue){
		float frealValue = StringUtil.convertToFloat(realValue, 0.0f);
		if(realValue != null && null != getBaseActivity()){
			if(frealValue >= maxValue){		
				if(pumpType == 0){
					if(mSystemAlarmMgr.getArtWarnFlowStatus() != 2){
						mSystemAlarmMgr.setArtWarnFlowStatus(2);
					}
					mArtFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
					onErrorMsg(ErrorCode.E_FLOW_ONE_MAX_AlARM_MSG);
				}else if(pumpType ==1){
					//vein
					if(mSystemAlarmMgr.getVeinWarnFlowStatus() != 2){
						mSystemAlarmMgr.setVeinWarnFlowStatus(2);
					}
					mVeinFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
					onErrorMsg(ErrorCode.E_FLOW_TWO_MAX_AlARM_MSG);
				}
				
				
			}else if(frealValue <= minValue){
				if(pumpType == 0){
					if(mSystemAlarmMgr.getArtWarnFlowStatus() != 1){
						mSystemAlarmMgr.setArtWarnFlowStatus(1);
					}
					mArtFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
					onErrorMsg(ErrorCode.E_FLOW_ONE_MIN_AlARM_MSG);
				}else if(pumpType ==1){
					if(mSystemAlarmMgr.getVeinWarnFlowStatus() != 1){
						mSystemAlarmMgr.setVeinWarnFlowStatus(1);
					}
					mVeinFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
					onErrorMsg(ErrorCode.E_FLOW_TWO_MIN_AlARM_MSG);
				}
								
			}else{
				if(pumpType == 0){
					if(mSystemAlarmMgr.getArtWarnFlowStatus() != 0){
						mSystemAlarmMgr.setArtWarnFlowStatus(0);
					}
					mArtFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.color_perfusion_art_content_txt));
					onNormalMsg(NormalCode.N_FLOW_ONE_MSG);
				}else if(pumpType == 1){
					if(mSystemAlarmMgr.getVeinWarnFlowStatus() != 0){
						mSystemAlarmMgr.setVeinWarnFlowStatus(0);
					}
					mVeinFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.color_perfusion_vein_flow));
					onNormalMsg(NormalCode.N_FLOW_TWO_MSG);
				}							
			}
			
		}			
	}
	
	/***
	 * 在常温灌注的条件下
	 * 比较当前值和目标温度值
	 * @param isTempFlag 到达过设置的目标值
	 * 温度报警flag在重设温度值时会进行重设
	 */
	private void onCompareTempAndTarTemp(String strtemp){
		float temp = StringUtil.convertToFloat(strtemp, 0.0f);

		//在常温灌注的条件下
		if(mTempMode == 0){
			if (targetTemp != 0.0f){
				float minAlarmTemp = (float)(targetTemp - 2.0f);
				float maxAlarmTemp = (float)(targetTemp + 2.0f);
				if(isTempFlag){
					// 当前温度已经低于目标温度，而且曾经到达目标温度
					if(temp < minAlarmTemp){
						//报警温度异常
						if (mSystemAlarmMgr.getTempStatus() != 1){
							mSystemAlarmMgr.setTempStatus(1);
							if(null != mListener){
								mListener.onTempStatus(1,true);
							}
							mTempTxt.setLeftContextColor(mContext.getResources().getColor(R.color.red));
						}
					}else if(temp > maxAlarmTemp){
						// 当前温度已经高于目标温度+2的温度
						//报警温度过高
						if(mSystemAlarmMgr.getTempStatus() != 2){
							mSystemAlarmMgr.setTempStatus(2);
							if(null != mListener){
								mListener.onTempStatus(2,true);
							}
							mTempTxt.setLeftContextColor(mContext.getResources().getColor(R.color.red));
						}
					}else{
						setTempAlarmFlag();
					}
				}else{
					long curCheckTemp =  new Date(System.currentTimeMillis()).getTime();
					if(temp >= targetTemp){
						lastckeckTemp = curCheckTemp;
						index++;
					}
					if((index > 30) && (curCheckTemp - lastckeckTemp < 500)){
						isTempFlag = true;
						index = 0;
					}
				}			
			}
		}
		
	}
	
	public void setTargetTemp(float tartemp,int tempMode){
		mTempTxt.setRightContentText(String.valueOf(tartemp));
		targetTemp = tartemp;
		mTempMode = tempMode;
	}
	

	public void setTempAlarmFlag(){
		isTempFlag = false;
		index = 0;
		if(mSystemAlarmMgr.getTempStatus() != 0){
			mSystemAlarmMgr.setTempStatus(0);
		}
		if(null != mListener){
			mListener.onTempStatus(0,false);
		}
		mTempTxt.setLeftContextColor(mContext.getResources().getColor(R.color.white));
	}
	
	
	
	private void cancelTask(){
		if(null != mScheduledThreadPool){
			mScheduledThreadPool.shutdown();
			mScheduledThreadPool = null;
		}
		if(null != mArtPreChartTask){
			mArtPreChartTask.cancel();
			mArtPreChartTask = null;
		}
		
		if(null != mVeinPreChartTask){
			mVeinPreChartTask.cancel();
			mVeinPreChartTask = null;
		}
		
		if(null != mArtFlowChartTask){
			mArtFlowChartTask.cancel();
			mArtFlowChartTask = null;
		}
		
		if(null != mVeinFlowChartTask){
			mVeinFlowChartTask.cancel();
			mVeinFlowChartTask = null;
		}
	}
	
	
	/**更新肝动脉的图表**/
	private void updateArtPreChart(){

		int length = mArtPreSeries.getItemCount();
		if(length >= 100) length = 100;		 
		mArtPreX = new Date().getTime();		    
		for (int i = 0; i < length; i++) {
			xArtPreCache[i] =  new Date((long)mArtPreSeries.getX(i));
			yArtPrecache[i] = (int) mArtPreSeries.getY(i);
		}	    
		mArtPreSeries.clear();
		mArtPreSeries.add(new Date(mArtPreX), mArtPreY);
		for (int k = 0; k < length; k++) {
			mArtPreSeries.add(xArtPreCache[k], yArtPrecache[k]);
    	}
		mArtPreDataset.removeSeries(mArtPreSeries);
		mArtPreDataset.addSeries(mArtPreSeries);
		mArtPreChart.invalidate();
	}
	
	/**update the chart of artery flow**/
	private void updateArtFlowChart(){
		int length = mArtFlowSeries.getItemCount();
		if(length >= 50) length = 50;		 
		mArtFlowX = new Date().getTime();
		for (int i = 0; i < length; i++) {
			xArtFlowCache[i] =  new Date((long)mArtFlowSeries.getX(i));
			yArtFlowCache[i] = (float) mArtFlowSeries.getY(i);
		}		    
		mArtFlowSeries.clear();
		mArtFlowSeries.add(new Date(mArtFlowX), (float)mArtFlowY);
		for (int k = 0; k < length; k++) {
			mArtFlowSeries.add(xArtFlowCache[k], (float)yArtFlowCache[k]);
    	}
		mArtFlowDataset.removeSeries(mArtFlowSeries);
		mArtFlowDataset.addSeries(mArtFlowSeries);
		
		//平均流量
		int len = mArtFMeanSeries.getItemCount();
		if(len >= 50) len = 50;		 
		mArtFmeanX = new Date().getTime();
		for (int i = 0; i < len; i++) {
			xArtFmeanCache[i] =  new Date((long)mArtFMeanSeries.getX(i));
			yArtFmeanCache[i] = (float) mArtFMeanSeries.getY(i);
		}		    
		mArtFMeanSeries.clear();
		mArtFMeanSeries.add(new Date(mArtFmeanX), (float)mArtMeanFlow);
		for (int h= 0; h < len; h++) {
			mArtFMeanSeries.add(xArtFmeanCache[h], (float)yArtFmeanCache[h]);
    	}
		mArtFlowDataset.removeSeries(mArtFMeanSeries);
		mArtFlowDataset.addSeries(mArtFMeanSeries);
		
		mArtFlowChart.invalidate();		
	}
	
	/**update the chart of artery flow**/
	private void updateArtFMeanChart(){
		int length = mArtFMeanSeries.getItemCount();
		if(length >= 50) length = 50;		 
		mArtFmeanX = new Date().getTime();
		for (int i = 0; i < length; i++) {
			xArtFmeanCache[i] =  new Date((long)mArtFMeanSeries.getX(i));
			yArtFmeanCache[i] = (float) mArtFMeanSeries.getY(i);
		}		    
		mArtFMeanSeries.clear();
		mArtFMeanSeries.add(new Date(mArtFmeanX), (float)mArtMeanFlow);
		
		for (int k = 0; k < length; k++) {
			mArtFMeanSeries.add(xArtFmeanCache[k], (float)yArtFmeanCache[k]);
    	}
		mArtFlowDataset.removeSeries(mArtFMeanSeries);
		mArtFlowDataset.addSeries(mArtFMeanSeries);
		mArtFlowChart.invalidate();		
	}
	
	/**update the chart of vein **/
	private void updateVeinPreChart(){
		int length = mVeinPreSeries.getItemCount();
		if(length >= 50) length = 50;		 
		mVeinPreX = new Date().getTime();		    
		for (int i = 0; i < length; i++) {
			xVeinPreCache[i] =  new Date((long)mVeinPreSeries.getX(i));
			yVeinPrecache[i] = (int) mVeinPreSeries.getY(i);
		}	    
		mVeinPreSeries.clear();
		mVeinPreSeries.add(new Date(mVeinPreX), mVeinPreY);
		for (int k = 0; k < length; k++) {
			mVeinPreSeries.add(xVeinPreCache[k], yVeinPrecache[k]);
    	}
		mVeinPreDataSet.removeSeries(mVeinPreSeries);
		mVeinPreDataSet.addSeries(mVeinPreSeries);
		mVeinPreChart.invalidate();
	}
	
	private void updateVeinFlowChart(){
		int length = mVeinFlowSeries.getItemCount();
		if(length >= 50) length = 50;		 
		mVeinFlowX = new Date().getTime();
		for (int i = 0; i < length; i++) {
			xVeinFlowCache[i] =  new Date((long)mVeinFlowSeries.getX(i));
			yVeinFlowCache[i] = (float) mVeinFlowSeries.getY(i);
		}		    
		mVeinFlowSeries.clear();
		mVeinFlowSeries.add(new Date(mVeinFlowX), (float)mVeinFlowY);
		for (int k = 0; k < length; k++) {
			mVeinFlowSeries.add(xVeinFlowCache[k], (float)yVeinFlowCache[k]);
    	}
		mVeinFlowDataSet.removeSeries(mVeinFlowSeries);
		mVeinFlowDataSet.addSeries(mVeinFlowSeries);
		mVeinFlowChart.invalidate();
	}
	
	
	private XYMultipleSeriesDataset getArtPreDataSet(){
	    final int nr = 10;
	    long value = new Date().getTime();

	    for (int i = 0; i < SERIES_NR; i++) {
	    	
	    	mArtPreSeries = new TimeSeries("Demo series " + (i + 1));
		    for (int k = 0; k < nr; k++) {
		    	mArtPreSeries.add(new Date(value), 0);
		    }
	     
	      mArtPreDataset.addSeries(mArtPreSeries);
	    }
	    
	    return mArtPreDataset;
	}
	
	
	private XYMultipleSeriesDataset getArtFlowDataSet(){
	    mArtFlowDataset = new XYMultipleSeriesDataset();
	    final int nr = 10;
	    long value = new Date().getTime();

	    for (int i = 0; i < SERIES_NR; i++) {
	    	
	    	mArtFlowSeries = new TimeSeries("ArtFreal series " + (i + 1));
		    for (int k = 0; k < nr; k++) {
		    	mArtFlowSeries.add(new Date(value), (float)0.0f);
		    }
	     
		    mArtFlowDataset.addSeries(mArtFlowSeries);
	    }
	    
	    for (int j = 0; j < SERIES_NR; j++) {
	    	
	    	mArtFMeanSeries = new TimeSeries("ArtFmean series " + (j + 1));
		    for (int k = 0; k < nr; k++) {
		    	mArtFMeanSeries.add(new Date(value), (float)0.0f);
		    }
	     
		    mArtFlowDataset.addSeries(mArtFMeanSeries);
	    }
	    
	    return mArtFlowDataset;
	}
	
	 private XYMultipleSeriesDataset getVeinPreDataSet() {
		 
	    mVeinPreDataSet = new XYMultipleSeriesDataset();
	    final int nr = 10;
	    long value = new Date().getTime();
	    for (int i = 0; i < SERIES_NR; i++) {
	    	
	    	mVeinPreSeries = new TimeSeries("Demo series " + (i + 1));
		    for (int k = 0; k < nr; k++) {
		    	mVeinPreSeries.add(new Date(value), 0);
		    }
	     
		    mVeinPreDataSet.addSeries(mVeinPreSeries);
	    }
	    
	    return mVeinPreDataSet;
	}
	   
	private XYMultipleSeriesDataset getVeinFlowDataSet() {
		mVeinFlowDataSet = new XYMultipleSeriesDataset();
		final int nr = 10;
		long value = new Date().getTime();

	    for (int i = 0; i < SERIES_NR; i++) {
	    	
	    	mVeinFlowSeries = new TimeSeries("Demo series " + (i + 1));
		    for (int k = 0; k < nr; k++) {
		    	mVeinFlowSeries.add(new Date(value), (float)0.0f);
		    }
	     
		    mVeinFlowDataSet.addSeries(mVeinFlowSeries);
	    }
	    
	    return mVeinFlowDataSet;
	}
	
   private XYMultipleSeriesRenderer getPreRenderer(float YMax, float YMin, String charttitle,int rendererColor,int rendernum,int linewidth) {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();	
	    renderer.setChartTitle(charttitle);
	    renderer.setYAxisMax(YMax);
	    renderer.setYAxisMin(YMin);
	    renderer.setYLabels(5); 
	    renderer.setXLabels(5);	   
	    renderer.setBarSpacing(4f);    
	   // renderer.setMargins(new int[] {20, 30, 10, 10});
	    renderer.setMarginsColor(getResources().getColor(R.color.color_bg_chart));
	    setRenderer(renderer,rendererColor,rendernum,linewidth);
	    return renderer;
   }
   
   /**流量图表**/
   private XYMultipleSeriesRenderer getFlowRenderer(float YMax, float YMin,String flowChartTitle, int rendererColor,int rendernum,int linewidth) {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    
	    renderer.setChartTitle(flowChartTitle);
	    renderer.setYAxisMax(YMax);
	    renderer.setYAxisMin(YMin);	   
	    renderer.setYLabels(8); 
	    renderer.setXLabels(5);	   	    
	    renderer.setMarginsColor(getResources().getColor(R.color.color_bg_chart));
	    setRenderer(renderer, rendererColor,rendernum,linewidth);
	    return renderer;
	}
   
   private void setRenderer(XYMultipleSeriesRenderer renderer,int rendererColor,int num,int linewidth){
	   //图表背景颜色
	   renderer.setBackgroundColor(getResources().getColor(R.color.color_bg_chart));
	   renderer.setZoomEnabled(false,false);
	   renderer.setPanEnabled(false,false);
	   renderer.setInScroll(false);  
	   //标题的大小
	   renderer.setChartTitleTextSize(14);
	   //X轴的描述
	   renderer.setXTitle(getResources().getString(R.string.time));    
	   renderer.setAxisTitleTextSize(12);
	   renderer.setAxesColor(getResources().getColor(R.color.white));

	   renderer.setLabelsTextSize(10);    
	   //刻度的颜色
	   renderer.setLabelsColor(getResources().getColor(R.color.white));
	   renderer.setLegendTextSize(12);    
	   //X轴的颜色
	   renderer.setXLabelsColor(Color.WHITE);
	   //Y轴的颜色
	   renderer.setYLabelsColor(0,Color.WHITE);
	   //刻度在Y轴的右侧
	   renderer.setYLabelsAlign(Align.RIGHT);
	   renderer.setYLabelsPadding(2);
	   //
	   renderer.setShowLegend(false);
	   //显示网格
	   //renderer.setShowGrid(false);
	   renderer.setShowGridX(true);
	   for(int i = 1; i<= num; i++){
		   XYSeriesRenderer r = new XYSeriesRenderer();
		   if(i== 1){
			   r.setColor(getResources().getColor(rendererColor));
		   }else{
			   r.setColor(getResources().getColor(R.color.white));
		   }

		    
		   r.setChartValuesSpacing(10);
		   r.setLineWidth((float)linewidth);
		   r.setPointStyle(PointStyle.POINT);		   
		   r.setFillPoints(true);			  
		   renderer.addSeriesRenderer(r);
	   }
   }
	/***
	 * 错误状态
	 * @param errorCode
	 * @param view
	 */
	private void onErrorMsg(int errorCode){
		Message msg = Message.obtain();
		msg.arg1 = errorCode;
		switch (errorCode) {
		
			case ErrorCode.E_BUBBLE_ARTERY_ALERT_MSG:				
				//mBubbleTxt.setLeftContextColor(getResources().getColor(R.color.red));
				break;
			case ErrorCode.E_BUBBLE_VEIN_ALERT_MSG:
				//mBubbleTxt.setRightContextColor(getResources().getColor(R.color.red));
				break;

			case ErrorCode.E_FLOW_ONE_MAX_AlARM_MSG:				
			case ErrorCode.E_FLOW_ONE_MIN_AlARM_MSG:
				if(null != mListener){
					mListener.onFlowStatus(0, true, errorCode);
				}										
				break;
				
			case ErrorCode.E_FLOW_TWO_MAX_AlARM_MSG:				
			case ErrorCode.E_FLOW_TWO_MIN_AlARM_MSG:
				if(null != mListener){
					mListener.onFlowStatus(1, true, errorCode);
				}										
				break;
				
			case ErrorCode.E_PESSURE_TWO_MAX_AlARM_MSG:
			case ErrorCode.E_PESSURE_TWO_MIN_AlARM_MSG:
				if(null != mListener){
					mListener.onPressureStatus(1,true,errorCode);				
				}									
				break;
			case ErrorCode.E_PESSURE_ONE_MIN_AlARM_MSG:	
			case ErrorCode.E_PESSURE_ONE_MAX_AlARM_MSG:
				if(null != mListener){
					mListener.onPressureStatus(0,true,errorCode);				
				}									
				break;
				
				
			default:
				break;
		}
	}
	
	/****
	 * 正常信息
	 * @param normalCode
	 * @param view
	 */
	private void onNormalMsg(int normalCode){
		switch (normalCode) {
		case NormalCode.N_BUBBLE_ONE_MSG:				
			//mBubbleTxt.setLeftContextColor(getResources().getColor(R.color.color_perfusion_art_content_txt));
			break;
		case NormalCode.N_BUBBLE_TWO_MSG:
			//mBubbleTxt.setRightContextColor(getResources().getColor(R.color.color_perfusion_art_content_txt));
			break;
			
		case NormalCode.N_PESSURE_ONE_MSG:	
			if(null != mListener){
				mListener.onPressureStatus(0,false,normalCode);
			}										
			break;
			
		case NormalCode.N_PESSURE_TWO_MSG:	
			if(null != mListener){
				mListener.onPressureStatus(1,false,normalCode);
			}										
			break;	
			
		case NormalCode.N_FLOW_ONE_MSG:			
			if(null != mListener){
				mListener.onFlowStatus(0,false,normalCode);
			}				
			break;
			
		case NormalCode.N_FLOW_TWO_MSG:			
			if(null != mListener){
				mListener.onFlowStatus(1,false,normalCode);
			}				
			break;	
			
		default:
			break;
		}
		
	}
   
	
	private class SerialMsgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();				
			if(null != mApplication && null != getActivity()){
				if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL)){						
					String pump1_Preal = intent.getStringExtra(SerialMsgConstant.ARTERY_PREAL);
					float Preal_one = 0.0f;
					if(!pump1_Preal.equals("")){							
						Preal_one = StringUtil.convertToFloat(pump1_Preal, 0.0f);														
					}
					mArtPreY = Preal_one;
																														
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL)){					
					String pump2_Preal = intent.getStringExtra(SerialMsgConstant.VEIN_PREAL);
					if(null != pump2_Preal){
						float Preal_one = 0.0f;
						if(!StringUtil.isEmpty(pump2_Preal)){					
							Preal_one = StringUtil.convertToFloat(pump2_Preal, 0.0f);														
						}
						mVeinPreY = Preal_one;
					}										
									
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL)){
					String pump1_Freal = intent.getStringExtra(SerialMsgConstant.ARTERY_FREAL);
					if(!StringUtil.isEmpty(pump1_Freal)
							&& !pump1_Freal.equals("error1")
							&& !pump1_Freal.equals("error2")){
						float FReal_one = StringUtil.convertToFloat(pump1_Freal,0.0f);
						if(0.0f != FReal_one){
							mArtFlowY = FReal_one;																
						}else{
							mArtFlowY = 0.0f;
						}	
					
					}												
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL)){
					String pump2_Freal = intent.getStringExtra(SerialMsgConstant.VEIN_FREAL);
					if(!StringUtil.isEmpty(pump2_Freal)
							&& !pump2_Freal.equals("error1")
							&& !pump2_Freal.equals("error2")){
						float FReal_two = StringUtil.convertToFloat(pump2_Freal,0.0f);
						if(0.0f != FReal_two){
							mVeinFlowY = FReal_two;																
						}else{
							mVeinFlowY = 0.0f;
						}					
					}												
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_SREAL)){
					if(null != mArtSpeedTxt){
						String pump1_Sreal = intent.getStringExtra(SerialMsgConstant.ARTERY_SPEED);
						mArtSpeedTxt.setContentText(pump1_Sreal);											
					}				
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_TREAL)){
					if(null != mTempTxt){
						String pump1_Treal = intent.getStringExtra(SerialMsgConstant.ARTERY_TEMP);
						onCompareTempAndTarTemp(pump1_Treal);
						if(null != pump1_Treal){
							mTempTxt.setLeftContentText(pump1_Treal);
						}
					}
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_SREAL)){
					if(null != mVeinSpeedTxt){
						String pump2_Sreal = intent.getStringExtra(SerialMsgConstant.VEIN_SPEED);
						mVeinSpeedTxt.setContentText(pump2_Sreal);
												
					}
				}else if (action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_QREAL)) {
						
						String pump1_Qreal = intent.getStringExtra(SerialMsgConstant.ARTERY_BUBBLE);
						if(null != pump1_Qreal){
							if(!pump1_Qreal.equals(DefValues.DEDAULT_NULL)){
								onErrorMsg(ErrorCode.E_BUBBLE_ARTERY_ALERT_MSG);
								if(pump1_Qreal.equals("255")){
									displayToast(R.string.error_hepatic_artery_line);
								}
							}else{
								if(!mApplication.isStopPump()){
									onNormalMsg(NormalCode.N_BUBBLE_ONE_MSG);						
								}					
							}
						}
				}else if (action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_QREAL)) {
						
					String pump1_Qreal = intent.getStringExtra(SerialMsgConstant.VEIN_BUBBLE);
					if(null != pump1_Qreal){
						if(!pump1_Qreal.equals(DefValues.DEDAULT_NULL)){
							onErrorMsg(ErrorCode.E_BUBBLE_VEIN_ALERT_MSG);
							if(pump1_Qreal.equals("255")){
								displayToast(R.string.error_portal_vein_line);
							}
						}else{
							if(!mApplication.isStopPump()){
								onNormalMsg(NormalCode.N_BUBBLE_TWO_MSG);						
							}					
						}
					}
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PFREQ)){					
					if(null != mPulseRateTxt){
						String pump1_Pfreq = intent.getStringExtra(SerialMsgConstant.ARTERY_PFREQ);
						mPulseRateTxt.setContentText(pump1_Pfreq);
					}
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PDIAS)){
						//舒张压//右
					if(null != mArtRealPTxt){
						String pump1_PDias = intent.getStringExtra(SerialMsgConstant.ARTERY_PDIAS); 							
						mArtRealPTxt.setRightContentText(pump1_PDias);
					}
						
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PSYST)){
						//收缩压//左
					if(null != mArtRealPTxt){
						String pump1_PSyst = intent.getStringExtra(SerialMsgConstant.ARTERY_PSYST); 							
						mArtRealPTxt.setLeftContentText(pump1_PSyst);
					}
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PMEAN)){
						//肝动脉平均压
					if(null != mArtRealPTxt){
						String pump1_PMean = intent.getStringExtra(SerialMsgConstant.ARTERY_PMEAN); 
						if(null != pump1_PMean){							
							mArtRealPTxt.setSecondContentText("(" + pump1_PMean + ")");
							if(null != pump1_PMean && !pump1_PMean.equals("")){
								mArtMeanPre = StringUtil.convertToFloat(pump1_PMean, 0.0f);
								onUpdatePreLimitedMsg(0,mArtMeanPre , mArtPreAlertMin, mArtPreAlertMax);									
							}	
						}													
					}

				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FMEAN)){
						//肝动脉平均流量
					if(null != mArtFlowTxt){						
						String pump1_Fmean = intent.getStringExtra(SerialMsgConstant.ARTERY_FMEAN);

						if(null != pump1_Fmean){
							if(pump1_Fmean.equals("error1")){
								mArtFlowTxt.setLeftContentText("Error");
								mArtFlowTxt.setRightContentText(getBaseActivity().getResources()
										.getString(R.string.string_null));
								mArtFlowTxt.setLeftContextColor(getBaseActivity().getResources()
										.getColor(R.color.red));
							}else if(pump1_Fmean.equals("error2")){
								mArtFlowTxt.setLeftContentText("Error");
								mArtFlowTxt.setRightContentText(getBaseActivity().getResources()
										.getString(R.string.string_null));
								mArtFlowTxt.setLeftContextColor(getBaseActivity().getResources()
										.getColor(R.color.red));
							}else{
								mArtMeanFlow = StringUtil.convertToInt(pump1_Fmean,0);
								if(!StringUtil.isEmpty(pump1_Fmean)){		
									int artery_Fmean = StringUtil.IgnoreUnities(mArtMeanFlow);
									String handredgramsFlow = convertFMeantoHundredGrams((float)artery_Fmean);
									mArtFlowTxt.setLeftContentText(artery_Fmean + "");
									mArtFlowTxt.setRightContentText(handredgramsFlow);
									mArtFlowTxt.setLeftContextColor(getBaseActivity().getResources()
											.getColor(R.color.color_perfusion_art_content_txt));
									//mFlowPercentTxt.setLeftContentText("" + df.format(mArtMeanFlow/mArtTarFlow * 100) +"%");
									onUpdateFlowLimitedMsg(0,pump1_Fmean , mArtFlowAlertMin, mArtFlowAlertMax);
								}	
							}							
						}
												
					}
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FMEAN)){
						//门动脉平均流量
					if(null != mVeinFlowTxt){						
						String pump2_Fmean = intent.getStringExtra(SerialMsgConstant.VEIN_FMEAN);
						if(null != pump2_Fmean){
							if(pump2_Fmean.equals("error1")){
								mVeinFlowTxt.setLeftContentText("Error");
								mVeinFlowTxt.setRightContentText(getBaseActivity().getResources()
										.getString(R.string.string_null));
								mVeinFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
							}else if(pump2_Fmean.equals("error2")){
								mVeinFlowTxt.setLeftContentText("Error");
								mVeinFlowTxt.setRightContentText(getBaseActivity().getResources()
										.getString(R.string.string_null));
								mVeinFlowTxt.setLeftContextColor(getBaseActivity().getResources().getColor(R.color.red));
							}else{
								mVeinMeanFlow = StringUtil.convertToInt(pump2_Fmean, 0);
								if(!StringUtil.isEmpty(pump2_Fmean)){			
									int vein_Fmean = StringUtil.IgnoreUnities(mVeinMeanFlow);
									String handredgramsFlow = convertFMeantoHundredGrams((float)vein_Fmean);									
									mVeinFlowTxt.setLeftContentText(vein_Fmean+"");
									mVeinFlowTxt.setRightContentText(handredgramsFlow);
									mVeinFlowTxt.setLeftContextColor(getBaseActivity().getResources()
											.getColor(R.color.color_perfusion_vein_flow));
									//mFlowPercentTxt.setRightContentText("" + df.format(mVeinMeanFlow/mVeinTarFlow * 100)+ "%");
									onUpdateFlowLimitedMsg(1,pump2_Fmean , mVeinFlowAlertMin, mVeinFlowAlertMax);
								}
							}								
						 }
												
					  }
				}else if(action.equals(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PMEAN)){
						//门静脉平均压力
					if(null != mVeinPreTxt){
						String pump2_PMean = intent.getStringExtra(SerialMsgConstant.VEIN_PMEAN); 
						if(null != pump2_PMean){
							mVeinMeanPre = StringUtil.convertToFloat(pump2_PMean, 0.0f);
							mVeinPreTxt.setLeftContentText(String.valueOf(mVeinMeanPre));
							if(!StringUtil.isEmpty(pump2_PMean)){
								onUpdatePreLimitedMsg(1,mVeinMeanPre , mVeinPreAlertMin, mVeinPreAlertMax);
							}	
						}
													
					}

				}
//				else if(action.equals(BroadcastActions.ACTION_RECEIVE_EBILE)){
//					if(null != mBileTxt){
//						int countBile = intent.getIntExtra(SerialMsgConstant.EBILE,0);							
//						//成功清零后
//						Log.i(TAG, " artery countBile---" + countBile);
//						if(mApplication.isBileClear()){
//							//显示胆汁
//							mBileTxt.setLeftContentText(String.valueOf(countBile));
//							mBileTxt.setRightContentText(convertBileCapacity(countBile));
//						}							
//					}
//				}
				else if(action.equals(BroadcastActions.ACTION_RECEIVE_TARGET_TEMP)){
						//目标温度
					String tempTarget = intent.getStringExtra(SerialMsgConstant.TARGET_TEMP);
					if(null != tempTarget){
						mTempTxt.setRightContentText(tempTarget);
					}
				}
				setResIndexParameters();
			}		
		}
	}
	
	private static class UIHandler extends Handler{
		
		private final WeakReference<PerfusionFragment> weakFragment;
		public UIHandler(PerfusionFragment fragment) {  
			weakFragment = new WeakReference<PerfusionFragment>(fragment);  
	    }
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final PerfusionFragment mFragment = weakFragment.get();
			if(null == mFragment){
				return;
			}
			switch (msg.what) {
					
				case UPDATE_ART_PRESSURE_CHAT:
					mFragment.updateArtPreChart();
					break;
				
				case UPDATE_ART_FLOW_CHAT:				
					mFragment.updateArtFlowChart();
					break;
				
				case UPDATE_VEIN_PRESSURE_CHAT:
					mFragment.updateVeinPreChart();
					break;
				case UPDATE_VEIN_FLOW_CHAT:
					mFragment.updateVeinFlowChart();
					break;
				default:
					break;
			}
		}  
		 
	}

	//更新灌注目标值
	public void onNotifyPerfusionSetting(){
		mArtMode = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getIntValue(SharedConstants.ARTERY_PERFUSION_MODE, Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE);
		mVeinMode = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getIntValue(SharedConstants.VEIN_PERFUSION_MODE, Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE);
		mArtTarFlow = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_ARTERY_FLOW, 0.0f);
		mVeinTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
		mVeinTarFlow = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_VEIN_FLOW, 0.0f);
		mArtTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
		mVeinConstFlow = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, 0.0f);
		mArtMinTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, 0.0f);
		mArtMaxTarPre = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, 0.0f);

		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			//恒压
			mArtTarPTxt.setContentVisible(false);
			mArtTarPTxt.setSecContentVisible(true);
			mArtTarPTxt.setSecondContentText(String.valueOf(mArtTarPre));
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			//搏动
			mArtTarPTxt.setContentVisible(true);
			mArtTarPTxt.setSecContentVisible(false);
			mArtTarPTxt.setRightContentText(String.valueOf(mArtMinTarPre));
			mArtTarPTxt.setLeftContentText(String.valueOf(mArtMaxTarPre));
		}
		
		//门静脉恒压/恒流
		if(mVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
			//const pressure
			mVeinPreTxt.setRightContentText(String.valueOf(mVeinTarPre));
			mVeinFlowTxt.setRightContentText(new DecimalFormat("#0.0").format(mVeinTarFlow));
		}else if(mVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
			//const flow
			mVeinPreTxt.setRightContentText(mContext.getString(R.string.string_null));
			mVeinFlowTxt.setRightContentText(String.valueOf(mVeinConstFlow));
		}		
		
		if(mArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
			float mPreYMax = mArtTarPre + 20.0f;
			float mPreYMin = mArtTarPre - 20.0f;
			updateArtPreChartYRange(mPreYMax,mPreYMin);
		}else if(mArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
			updateArtPreChartYRange(mArtMaxTarPre+10.0f,mArtMinTarPre-10.0f);
		}
		
	}
	
	public void onNotifyAlertSetting(){
		mArtPreAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MAX_ONE, 120.0f);
		mArtPreAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MIN_ONE, 0.0f);
		mArtFlowAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MAX_ONE, 1000.0f);
		mArtFlowAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MIN_ONE, 0.0f);
		mVeinPreAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MAX_TWO, 14.0f);
		mVeinPreAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.PRESSURE_MIN_TWO, 0.0f);
		mVeinFlowAlertMax = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MAX_TWO, 2000.0f);
		mVeinFlowAlertMin = PreferenceUtil.getInstance(mContext.getApplicationContext())
				.getFloatValue(SharedConstants.FLOW_MIN_TWO, 0.0f);
	}

}