package com.gidi.bio_console.fragment.record;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.adapter.SerialMsgAdapter;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.SerialMessage;

public class RecordDataFragment extends BaseFragment {

	private ListView mRecordListView;
	private SerialMsgAdapter mSerialLogAdapter = null;
	private ArrayList<SerialMessage> mRecordList;
	
		
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
		mRecordList = new ArrayList<SerialMessage>();
		mSerialLogAdapter = new SerialMsgAdapter(getBaseActivity().getApplicationContext());
	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_record_data;
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mRecordListView = (ListView)rootView.findViewById(R.id.record_data_listview);
		mRecordListView.setAdapter(mSerialLogAdapter);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		
	}

	public void setRecordList(ArrayList<SerialMessage> recordList){
		if(null != mRecordList){
			mRecordList.clear();
			mRecordList.addAll(recordList);
			if(null != mSerialLogAdapter){
				mSerialLogAdapter.setList(mRecordList);
				mSerialLogAdapter.notifyDataSetChanged();
			}
		}
	}
	
}
