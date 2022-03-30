package com.gidi.bio_console.adapter;

import java.util.ArrayList;


import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.PerfusionLogBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class PerfusionLogAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<PerfusionLogBean> mList = new ArrayList<PerfusionLogBean>();
		
	public PerfusionLogAdapter(Context context){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}
	
	public void setPerfusionLogList(ArrayList<PerfusionLogBean> list){
		mList.clear();
		mList.addAll(list);
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size()>0 ? mList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder ;
		final PerfusionLogBean perfusionLogBean = mList.get(position);
		if(null == convertView){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.module_list_item_perfusion_log_layout, parent,false);
			holder.indexTxt = (TextView)convertView.findViewById(R.id.item_record_history_index);
			holder.liverIDTxt = (TextView)convertView.findViewById(R.id.item_list_record_history_id);
			holder.startTimeTxt = (TextView)convertView.findViewById(R.id.item_list_record_history_time);
			holder.weightTxt = (TextView)convertView.findViewById(R.id.item_list_record_history_liver_weight);
			holder.arteryMode = (TextView)convertView.findViewById(R.id.item_list_record_history_artery_mode);
			holder.veinMode = (TextView)convertView.findViewById(R.id.item_list_record_history_vein_mode);
			holder.checkbox = (CheckBox)convertView.findViewById(R.id.item_list_record_check);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.indexTxt.setText(String.valueOf(perfusionLogBean.getIndex()));
		holder.liverIDTxt.setText(perfusionLogBean.getLiverName());
		holder.startTimeTxt.setText(perfusionLogBean.getStartTime());
		holder.weightTxt.setText(perfusionLogBean.getLiverWeight());
		holder.arteryMode.setText(perfusionLogBean.getArtery_mode());
		holder.veinMode.setText(perfusionLogBean.getVein_mode());
		if(perfusionLogBean.isCheck()){
			holder.checkbox.setChecked(true);
			holder.checkbox.setBackgroundResource(R.drawable.item_checked);
		}else{
			holder.checkbox.setChecked(false);
			holder.checkbox.setBackgroundResource(R.drawable.item_unchecked);
		}
	
		
		return convertView;
	}

	static class ViewHolder{
		TextView liverIDTxt;
		TextView weightTxt;
		TextView startTimeTxt;
		TextView arteryMode;
		TextView veinMode;
		TextView indexTxt;
		CheckBox checkbox;
	}
}
