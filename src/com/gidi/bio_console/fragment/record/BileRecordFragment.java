package com.gidi.bio_console.fragment.record;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.adapter.BileRecordAdapter;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.BileMsgBean;
import com.gidi.bio_console.mgr.LineChartMgr;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

public class BileRecordFragment extends BaseFragment{

	private ListView mBileListView;
	private LineChart mPerHour_TotalChart;
	private LineChart mTotalChart;
	private BileRecordAdapter mAdapter;
	
	private List<BileMsgBean> mAllList;//所有的胆汁记录
	private List<BileMsgBean> mPerHourList;//每个小时内的数
	private List<BileMsgBean> mTotalList;//每间隔一小时胆汁总量列表
	
	private List<Entry> mPerHourChartList;
	private List<Entry> mTotalChartList;
	
	private List<String> timeStamps;//胆汁记录时间戳
	private List<String> mPerHourTimeStamps;//每间隔一小时胆汁记录的时间戳
	
	
	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_record_bile;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mBileListView = (ListView)rootView.findViewById(R.id.record_data_bile_listview);
		mPerHour_TotalChart = (LineChart)rootView.findViewById(R.id.record_bile_perhour_chart);
		mTotalChart = (LineChart)rootView.findViewById(R.id.record_bile_total_chart);
		mBileListView.setAdapter(mAdapter);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		
	}
	
	
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
		mAllList = new ArrayList<BileMsgBean>();
		mPerHourList = new ArrayList<BileMsgBean>();
		mTotalList = new ArrayList<BileMsgBean>();
		mPerHourChartList = new ArrayList<Entry>();
		mTotalChartList = new ArrayList<Entry>();
		timeStamps = new ArrayList<String>();
		mPerHourTimeStamps = new ArrayList<String>();
		mAdapter = new BileRecordAdapter(getActivity().getApplicationContext());
		
	}
	
	/**
	 * 
	 * @param list
	 */
	public void setBileRecordList(ArrayList<BileMsgBean> list){
		mAllList.clear();
		mAllList.addAll(list);//所有胆汁记录
		updateBileListView();
		
		mTotalList.clear();				
		for(int i = 0; i< list.size(); i++){		
			if(i % 15 == 0 && i != list.size()-1){
				mTotalList.add(list.get(i));				
			}
			if(i > 15 && i==(list.size()-1)){
				mTotalList.add(list.get(list.size() -1));				
			}
		}//从总的胆汁数据中挑出间隔一小时的数据放入mTotalList中
			
		onNotifyTotalChart();//每小时胆汁的滴数
		
		onNotifyPerHourChart(mTotalList);// 每小时胆汁的总数
	}
	
	/**更新列表**/
	private void updateBileListView(){
		mAdapter.setRecordList(mAllList);
		mAdapter.notifyDataSetChanged();
	}
	
	/**每小时胆汁变化量**/
	private void onNotifyPerHourChart(List<BileMsgBean> totalList){
		if(null != mPerHourList){
			mPerHourList.clear();
		}
		List<BileMsgBean> tempList = new ArrayList<BileMsgBean>();
		tempList.clear();
		tempList.addAll(totalList);
		int lastCount = 0;
		for(int i = 0; i < tempList.size();i++){
			BileMsgBean bean = tempList.get(i);
			int count = bean.getBileCount();
			if(i == 0){
				mPerHourList.add(bean);
			}else{
				int diffCount = count - lastCount;
				bean.setBileCount(diffCount);
				mPerHourList.add(bean);
			}
			lastCount = count;
		}
		if(null != mPerHourChartList){
			mPerHourChartList.clear();
		}
		if(null != mPerHourTimeStamps){
			mPerHourTimeStamps.clear();
		}
		if(null != mPerHourList && mPerHourList.size()>0){
			int index = 0;
			for(BileMsgBean bean : mPerHourList){
				mPerHourChartList.add(new Entry(index, bean.getBileCount()));
				mPerHourTimeStamps.add(index, bean.getTime());
				index ++;
			}			
			LineChartMgr.showChart(getActivity(), mPerHour_TotalChart, mPerHourTimeStamps, mPerHourChartList, 
					getActivity().getResources().getString(R.string.record_chart_perhour_hour_title), "Bile Production/time", 
					getActivity().getResources().getString(R.string.unit_perhour_bile), 
					R.color.color_preset_artery_perfusion_mode);
		}else{
			LineChartMgr.clearChart(mPerHour_TotalChart);
		}
		
	}
	
	
	/**更新胆汁总量图表***/
	private void onNotifyTotalChart(){
		
		if(null != mTotalChartList){
			mTotalChartList.clear();
		}
		
		if(null != timeStamps){
			timeStamps.clear();
		}
		
		if(null != mTotalList && mTotalList.size() > 0){
			int index = 0;
			for(BileMsgBean bean2 : mTotalList){
				Log.i("BileRecordFragment---",  "count " +bean2.getBileCount());
				mTotalChartList.add(new Entry(index, bean2.getBileCount()));
				timeStamps.add(index, bean2.getTime());
				index ++;
			}
						
			LineChartMgr.showChart(getActivity(), mTotalChart, timeStamps, mTotalChartList, 
					getActivity().getResources().getString(R.string.record_chart_total_bile_title), "Bile Production/time", 
					getActivity().getResources().getString(R.string.unit_perhour_bile), 
					R.color.color_preset_artery_perfusion_mode);
		}else{
			LineChartMgr.clearChart(mTotalChart);
		}
	}
	
	
	private void releaseList(){
		if(null != mAllList){
			mAllList.clear();
			mAllList = null;
		}
		
		if(null != mPerHourList){
			mPerHourList.clear();
			mPerHourList = null;
		}
		
		if(null != mTotalList){
			mTotalList.clear();
			mTotalList = null;
		}
		
		if(null != mPerHourChartList){
			mPerHourChartList.clear();
			mPerHourChartList = null;
		}
		
		if(null != mTotalChartList){
			mTotalChartList.clear();
			mTotalChartList = null;
		}
	}
	
	private void releaseChart(){
		if(null != mPerHour_TotalChart){
			mPerHour_TotalChart.clear();
			mPerHour_TotalChart = null;
		}
		
		if(null != mTotalChart){
			mTotalChart.clear();
			mTotalChart = null;
		}
	}

}
