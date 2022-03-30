package com.gidi.bio_console.kidney;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.KidneyInfoBean;
import com.gidi.bio_console.utils.LineChartUtils;
import com.gidi.bio_console.utils.StringUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

public class KidneyChartFragment extends BaseFragment {

	private LineChart mArtPreChart;
	private LineChart mArtFlowChart;
	private LineChart mVeinPreChart;
	private LineChart mVeinFlowChart;
	private LineChart mVeinResIndexChart;
	private LineChart mArtResIndexChart;
	/**肝动脉压力**/
	private List<Entry> mArtPreList;
	/**肝动脉流量**/
	private List<Entry> mArtFlowList;
	private List<Entry> mArtResIndexList;
	/**门静脉压力**/
	private List<Entry> mVeinPreList;
	private List<Entry> mVeinFlowList;
	private List<Entry> mVeinResIndexList;
	
	private LineChartUtils mArtPreChartUtils;
	private LineChartUtils mVeinPreChartUtils;
	private LineChartUtils mArtFlowChartUtils;
	private LineChartUtils mVeinFlowChartUtils;
	private LineChartUtils mArtResIndexChartUtils;
	private LineChartUtils mVeinResIndexChartUtils;	
	
	private List<String> timeStamps = new ArrayList<String>() ;
	private List<KidneyInfoBean> mRecordList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initVariables();
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseList();
		releaseChart();		
	}



	private void initVariables(){
		mRecordList = new ArrayList<KidneyInfoBean>();
		
		mArtPreList = new ArrayList<Entry>();
		mArtFlowList = new ArrayList<Entry>();
		mArtResIndexList = new ArrayList<Entry>();
		
		mVeinPreList = new ArrayList<Entry>();
		mVeinFlowList = new ArrayList<Entry>();
		mVeinResIndexList = new ArrayList<Entry>();		
		timeStamps = new ArrayList<String>();		
	}
	
	private void initLineChart(){
		mArtPreChartUtils = new LineChartUtils(getActivity(), mArtPreChart);
		mArtPreChartUtils.initLineChart();
		mArtPreChartUtils.setScaleEnable(false);
		mArtPreChartUtils.setDescription(getActivity().getResources().getString(R.string.preset_perfusion_left_kidney_pressure));
		mArtPreChartUtils.setLineChartYLeft();
		
		mVeinPreChartUtils = new LineChartUtils(getActivity(), mVeinPreChart);
		mVeinPreChartUtils.initLineChart();
		mArtPreChartUtils.setScaleEnable(false);
		mVeinPreChartUtils.setDescription(getActivity().getResources().getString(R.string.preset_perfusion_right_kidney_pressure));
		mVeinPreChartUtils.setLineChartYLeft();
		
		mArtFlowChartUtils = new LineChartUtils(getActivity(), mArtFlowChart);
		mArtFlowChartUtils.initLineChart();
		mArtPreChartUtils.setScaleEnable(false);
		mArtFlowChartUtils.setDescription(getActivity().getResources().getString(R.string.preset_perfusion_left_kidney_flow));
		mArtFlowChartUtils.setLineChartYLeft();
		
		mVeinFlowChartUtils = new LineChartUtils(getActivity(), mVeinFlowChart);
		mVeinFlowChartUtils.initLineChart();
		mArtPreChartUtils.setScaleEnable(false);
		mVeinFlowChartUtils.setDescription(getActivity().getResources().getString(R.string.preset_perfusion_right_kidney_flow));
		mVeinFlowChartUtils.setLineChartYLeft();
		
		mArtResIndexChartUtils = new LineChartUtils(getActivity(), mArtResIndexChart);
		mArtResIndexChartUtils.initLineChart();
		mArtPreChartUtils.setScaleEnable(false);
		mArtResIndexChartUtils.setDescription(getActivity().getResources().getString(R.string.record_title_left_kidney_artery_resistindex));
		mArtResIndexChartUtils.setLineChartYLeft();
		
		mVeinResIndexChartUtils = new LineChartUtils(getActivity(), mVeinResIndexChart);
		mVeinResIndexChartUtils.initLineChart();
		mArtPreChartUtils.setScaleEnable(false);
		mVeinResIndexChartUtils.setDescription(getActivity().getResources().getString(R.string.record_title_right_kidney_artery_resistindex));
		mVeinResIndexChartUtils.setLineChartYLeft();
	}
	

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_kidney_record_chart;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mArtPreChart = (LineChart)rootView.findViewById(R.id.record_chart_artery_pressure_chart);
		mArtFlowChart = (LineChart)rootView.findViewById(R.id.record_chart_artery_flow_chart);
		mVeinPreChart = (LineChart)rootView.findViewById(R.id.record_chart_vein_pressure_chart);
		mVeinFlowChart = (LineChart)rootView.findViewById(R.id.record_chart_vein_flow_chart);
		mArtResIndexChart = (LineChart)rootView.findViewById(R.id.record_chart_artery_resistindex_chart);
		mVeinResIndexChart = (LineChart)rootView.findViewById(R.id.record_chart_vein_resistindex_chart);		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		initLineChart();
	}
	
	public void setRecordList(ArrayList<KidneyInfoBean> recordList){
		if(null == mRecordList){
			mRecordList = new ArrayList<KidneyInfoBean>();
		}
		mRecordList.clear();
		mRecordList.addAll(recordList);
		onNotifyUpdateChart();		
	}

	private void onNotifyUpdateChart(){
		clearAllList();
		if(mRecordList.size() > 0){
			int index = 0;
			for(KidneyInfoBean msg : mRecordList){
				if(null != msg){
					Float mArtPreValue = StringUtil.convertToFloat(msg.getLeftKidneyArtMeanPre(), 0.0f);
					Float mArtFlowValue = StringUtil.convertToFloat(msg.getLeftKidneyArtFreal(), 0.0f);
					Float mVeinPreValue = StringUtil.convertToFloat(msg.getRightKidneyArtPmean(), 0.0f);
					Float mVeinFlowValue =  StringUtil.convertToFloat(msg.getRightKidneyArtFreal(), 0.0f);						
					Float artResIndexValue = getResistIndex(msg.getLeftKidneyArtResistIndex());
					Float veinResIndexValue = getResistIndex(msg.getRightKidneyArtResistIndex());
					
					mArtPreList.add(new Entry(index, mArtPreValue)) ;
					mArtFlowList.add(new Entry(index, mArtFlowValue)) ;
					mVeinPreList.add(new Entry(index, mVeinPreValue)) ;
					mVeinFlowList.add(new Entry(index, mVeinFlowValue)) ;
					mArtResIndexList.add(new Entry(index, artResIndexValue)) ;
					mVeinResIndexList.add(new Entry(index, veinResIndexValue)) ;
					timeStamps.add(msg.getMsgTime());

				}
				index++;
			}

			mArtPreChartUtils.setLineChartData(timeStamps, mArtPreList, 
					"Pressure/time",
					getActivity().getResources().getString(R.string.preset_perfusion_left_kidney_pressure), 
					R.color.blue);
			
			mArtFlowChartUtils.setLineChartData(timeStamps, mArtFlowList, 
					"Flow/time",
					getActivity().getResources().getString(R.string.preset_perfusion_left_kidney_flow), 
					R.color.blue);
			
			mVeinPreChartUtils.setLineChartData(timeStamps, mVeinPreList, 
					"Pressure/time",
					getActivity().getResources().getString(R.string.preset_perfusion_right_kidney_pressure), 
					R.color.blue);
			
			mVeinFlowChartUtils.setLineChartData(timeStamps, mVeinFlowList, 
					"Flow/time",
					getActivity().getResources().getString(R.string.preset_perfusion_right_kidney_flow), 
					R.color.blue);
			
			mArtResIndexChartUtils.setLineChartData(timeStamps, mArtResIndexList, 
					"ResistIndex/time",
					getActivity().getResources().getString(R.string.record_title_left_kidney_artery_resistindex), 
					R.color.blue);
			
			mVeinResIndexChartUtils.setLineChartData(timeStamps, mVeinResIndexList, 
					"ResistIndex/time",
					getActivity().getResources().getString(R.string.record_title_right_kidney_artery_resistindex), 
					R.color.blue);
			
		}else{
			mArtPreChartUtils.clearChart();
			mVeinPreChartUtils.clearChart();
			mArtFlowChartUtils.clearChart();
			mVeinFlowChartUtils.clearChart();
			mArtResIndexChartUtils.clearChart();
			mVeinResIndexChartUtils.clearChart();
		}

	}
	
	private Float getResistIndex(String resistIndex){
		Float mResistIndex = (float)5.00;
		if(null != resistIndex){
			if(resistIndex.equals("∞")){
				mResistIndex = (float)5.00;
			}else{
				mResistIndex = StringUtil.convertToFloat(resistIndex, 0.00f);
			}
		}		
		return mResistIndex;
	}
	
	private void clearAllList(){
		if(null != mArtPreList){
			mArtPreList.clear();
		}
		if(null != mArtFlowList){
			mArtFlowList.clear();
		}

		if(null != mVeinPreList){
			mVeinPreList.clear();
		}
		
		if(null != mVeinFlowList){			
			mVeinFlowList.clear();
		}
		
		if(null != mArtResIndexList){
			mArtResIndexList.clear();			
		}
				
		if(null != mVeinResIndexList){
			mVeinResIndexList.clear();			
		}
		
		if(null != timeStamps){
			timeStamps.clear();
		}

	}
	
	private void releaseList(){
		if(null != mRecordList){
			mRecordList.clear();
			mRecordList = null;
		}

		
		if(null != mArtPreList){
			mArtPreList.clear();
			mArtPreList = null;
		}
		if(null != mArtFlowList){
			mArtFlowList.clear();
			mArtFlowList = null;
		}

		if(null != mVeinPreList){
			mVeinPreList.clear();
			mVeinPreList = null;
		}
		
		if(null != mVeinFlowList){			
			mVeinFlowList.clear();
			mVeinFlowList = null;
		}
		
		if(null != mArtResIndexList){
			mArtResIndexList.clear();
			mArtResIndexList = null;			
		}
				
		if(null != mVeinResIndexList){
			mVeinResIndexList.clear();
			mVeinResIndexList = null;
		}
		
		if(null != timeStamps){
			timeStamps.clear();
			timeStamps = null;
		}
	}
	
	private void releaseChart(){
		if(null != mArtPreChart){
			mArtPreChart.clear();
			mArtPreChart = null;
		}
		if(null != mArtFlowChart){
			mArtFlowChart.clear();
			mArtFlowChart = null;
		}
		if(null != mVeinPreChart){
			mVeinPreChart.clear();
			mVeinPreChart = null;
		}
		if(null != mVeinFlowChart){
			mVeinFlowChart.clear();
			mVeinFlowChart = null;
		}

		if(null != mVeinResIndexChart){
			mVeinResIndexChart.clear();
			mVeinResIndexChart = null;
		}
		if(null != mArtResIndexChart){
			mArtResIndexChart.clear();
			mArtResIndexChart = null;
		}
	}

}
