package com.gidi.bio_console.kidney;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.KidneyInfoBean;

public class KidneyDataFragment extends BaseFragment {

	private ListView mRecordListView;
	private KidneyInfoAdapter kidneyInfoAdapter = null;
	private ArrayList<KidneyInfoBean> mRecordList;
	
		
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
		if(null != mRecordList){
			mRecordList.clear();
			mRecordList = null;
		}
	}

	private void initVariables(){
		mRecordList = new ArrayList<KidneyInfoBean>();
		kidneyInfoAdapter = new KidneyInfoAdapter(getBaseActivity().getApplicationContext());
	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_kidney_record_data;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mRecordListView = (ListView)rootView.findViewById(R.id.record_data_listview);
		mRecordListView.setAdapter(kidneyInfoAdapter);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		
	}

	public void setRecordList(ArrayList<KidneyInfoBean> recordList){
		if(null != mRecordList){
			mRecordList.clear();
			mRecordList.addAll(recordList);
			if(null != kidneyInfoAdapter){
				kidneyInfoAdapter.setList(mRecordList);
				kidneyInfoAdapter.notifyDataSetChanged();
			}
		}
	}
	
}
