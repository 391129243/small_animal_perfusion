package com.gidi.bio_console.fragment.bloodgas;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.BloodGasBean;
import com.gidi.bio_console.mgr.LineChartMgr;
import com.gidi.bio_console.utils.StringUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

public class BloodGasChartFragment extends BaseFragment {

	private static final String TAG = "BloodGasChartFragment";
	private LineChart mASTChart;
	private LineChart mALTChart;
	private LineChart mGLUChart;
	private LineChart mPHChart;
	private LineChart mHCTChart;
	private LineChart mPCO2Chart;
	private LineChart mPO2Chart;
	private LineChart mBinarnateChart;
	private LineChart mLacChart;
	private ArrayList<Entry> mPHList;
	private ArrayList<Entry> mPCO2List;
	private ArrayList<Entry> mPO2List;	
	private ArrayList<Entry> mBinarnateList;
	private ArrayList<Entry> mASTList;
	private ArrayList<Entry> mALTList;
	private ArrayList<Entry> mHCTList;
	private ArrayList<Entry> mGluList;
	private ArrayList<Entry> mLacList;

	private ArrayList<BloodGasBean> mBloodGasList;
	private ArrayList<String> timeStamps = new ArrayList<String>() ;

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.i(TAG, "--onAttach--");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "--onCreate--");
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "--onDestroy--");
		releaseList();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i(TAG, "--onDestroyView--");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "--onResume--");
		
	}
	
	

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(getUserVisibleHint()){
			
		}
	}


	public void setBloodGasList(ArrayList<BloodGasBean> bloodGasList){
		mBloodGasList.clear();
		mBloodGasList.addAll(bloodGasList);
		onNotifyUpdateChart();
		
	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_bloodgas_chart;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mASTChart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_ast_chart);
		mALTChart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_alt_chart);
		mGLUChart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_glu_chart);
		mPHChart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_ph_chart);
		mHCTChart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_hct_chart);
		mPCO2Chart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_pco2_chart);
		mPO2Chart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_po2_chart);
		mBinarnateChart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_bicarbonate_chart);
		mLacChart = (LineChart)rootView.findViewById(R.id.bloodgas_chart_lac_chart);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		initVariables();
	}

	private void initVariables(){
		mASTList = new ArrayList<Entry>();
		mALTList = new ArrayList<Entry>();
		mHCTList = new ArrayList<Entry>();
		mBinarnateList = new ArrayList<Entry>();
		mGluList = new ArrayList<Entry>();
		mPHList = new ArrayList<Entry>();
		mPCO2List = new ArrayList<Entry>();
		mPO2List = new ArrayList<Entry>();
		mLacList = new ArrayList<Entry>();
		timeStamps = new ArrayList<String>();
		mBloodGasList = new ArrayList<BloodGasBean>();	
		//mPhLineChartManager = new LineChartManager(mPHChart);
	}
	
	private void clearAllList(){

		if(null != mASTList){
			mASTList.clear();
		}
		if(null != mALTList){
			mALTList.clear();
		}
		if(null != mHCTList){
			mHCTList.clear();
		}
		if(null != mBinarnateList){
			mBinarnateList.clear();
		}

		if(null != mGluList){
			mGluList.clear();
		}
		if(null != mPHList){
			mPHList.clear();
		}

		if(null != mPCO2List){
			mPCO2List.clear();
		}
		if(null != mPCO2List){
			mPO2List.clear();
		}
		if(null != mLacList){
			mLacList.clear();
		}

		if(null != timeStamps){
			timeStamps.clear();
		}
		
	}
	
	private void onNotifyUpdateChart(){
		clearAllList();
		if(mBloodGasList.size() > 0){
			for(int i = 0; i< mBloodGasList.size();i++){
				BloodGasBean msg = mBloodGasList.get(i);
				if(null != msg){
					float mALT = StringUtil.convertToFloat(msg.getAlt(), 0.0f);
					int mAST = StringUtil.convertToInt(msg.getAst(), 0);
					float mGlu = StringUtil.convertToFloat(msg.getGlu(), 0.0f);
					float mPH = StringUtil.convertToFloat(msg.getPh(), 0.0f);
					float mPCO2  =  StringUtil.convertToFloat(msg.getPco2(), 0.000f);
					int PO2 =  StringUtil.convertToInt(msg.getPo2(), 0);
					int HCT =  StringUtil.convertToInt(msg.getHct(), 0);
					float Lac = StringUtil.convertToFloat(msg.getLac(), 0.00f);
					float binarnate = StringUtil.convertToFloat(msg.getBicarbonate(), 0.0f);
										
					mALTList.add(new Entry(i, mALT)) ;
					mASTList.add(new Entry(i, mAST)) ;
					mGluList.add(new Entry(i, mGlu)) ;
					mPO2List.add(new Entry(i, PO2)) ;
					mPHList.add(new Entry(i, mPH)) ;
					mPCO2List.add(new Entry(i, mPCO2)); 
					mHCTList.add(new Entry(i, HCT));
					mLacList.add(new Entry(i,Lac));
					mBinarnateList.add(new Entry(i, binarnate));
					timeStamps.add(msg.getSampleTime());
				}
			}

			LineChartMgr.showChart(getActivity(), mALTChart, timeStamps, mALTList, 
					getActivity().getResources().getString(R.string.title_alt_chart), "ALT(U/L)", 
					getActivity().getResources().getString(R.string.unit_alt), 
					R.color.red);
			LineChartMgr.showChart(getActivity(), mASTChart, timeStamps, mASTList, 
					getActivity().getResources().getString(R.string.ASAT), "AST", 
					getActivity().getResources().getString(R.string.unit_ast),
					R.color.orange);
			LineChartMgr.showChart(getActivity(), mGLUChart, timeStamps, mGluList, 
					getActivity().getResources().getString(R.string.Glu), "GLU(mmol/L)", 
					getActivity().getResources().getString(R.string.unit_glu),
					R.color.yellow);
			LineChartMgr.showChart(getActivity(), mHCTChart,timeStamps, mHCTList, 
					getActivity().getResources().getString(R.string.hct), "Hct",
					getActivity().getResources().getString(R.string.unit_hct),
					R.color.green);
			LineChartMgr.showChart(getActivity(), mPCO2Chart, timeStamps, mPCO2List, 
					getActivity().getResources().getString(R.string.pCO2), "PCO2", 
					getActivity().getResources().getString(R.string.unit_pco2),
					R.color.blue);
			LineChartMgr.showChart(getActivity(), mPO2Chart,timeStamps, mPO2List, 
					getActivity().getResources().getString(R.string.pO2), "PO2",
					getActivity().getResources().getString(R.string.unit_po2),
					R.color.purple);
			LineChartMgr.showChart(getActivity(), mPHChart,timeStamps, mPHList, 
					getActivity().getResources().getString(R.string.ph), "PH",
					"",R.color.seagreen);
			LineChartMgr.showChart(getActivity(), mBinarnateChart,timeStamps, mBinarnateList, 
					getActivity().getResources().getString(R.string.Bicarbonate), "Binarnate",
					"",R.color.color_perfusion_vein_flow);
			LineChartMgr.showChart(getActivity(), mLacChart,timeStamps, mLacList, 
					getActivity().getResources().getString(R.string.Lac), "Lac(mmol/L)",
					"",R.color.darkred);
		}else{
			LineChartMgr.clearChart(mASTChart);
			LineChartMgr.clearChart(mALTChart);
			LineChartMgr.clearChart(mGLUChart);
			LineChartMgr.clearChart(mHCTChart);
			LineChartMgr.clearChart(mPCO2Chart);
			LineChartMgr.clearChart(mPO2Chart);
			LineChartMgr.clearChart(mPHChart);
			LineChartMgr.clearChart(mBinarnateChart);
			LineChartMgr.clearChart(mLacChart);
			//mPhLineChartManager.clearChart();
		}
	}
	
	private void releaseList(){
		if(null != mASTList){
			mASTList.clear();
			mASTList = null;
		}
		if(null != mALTList){
			mALTList.clear();
			mALTList = null;
		}
		if(null != mHCTList){
			mHCTList.clear();
			mHCTList = null;
		}
		if(null != mBinarnateList){
			mBinarnateList.clear();
			mBinarnateList = null;
		}

		if(null != mGluList){
			mGluList.clear();
			mGluList = null;
		}
		if(null != mPHList){
			mPHList.clear();
			mPHList = null;
		}

		if(null != mPCO2List){
			mPCO2List.clear();
			mPCO2List = null;
		}
		if(null != mPCO2List){
			mPO2List.clear();
			mPO2List= null;
		}
		if(null != mLacList){
			mLacList.clear();
			mLacList =null;
		}

		if(null != timeStamps){
			timeStamps.clear();
			timeStamps = null;
		}
		
		if(null != mBloodGasList){
			mBloodGasList.clear();
			mBloodGasList = null;
		}
	}
	
	
}
