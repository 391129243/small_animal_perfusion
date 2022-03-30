package com.gidi.bio_console.kidney;

import java.util.ArrayList;


import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.KidneyPerfusionLogBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class KidneyPerfusionLogAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<KidneyPerfusionLogBean> mList = new ArrayList<KidneyPerfusionLogBean>();
		
	public KidneyPerfusionLogAdapter(Context context){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}
	
	public void setPerfusionLogList(ArrayList<KidneyPerfusionLogBean> list){
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
		final KidneyPerfusionLogBean perfusionLogBean = mList.get(position);
		if(null == convertView){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.module_list_item_kidney_perfusion_log_layout, parent,false);
			holder.indexTxt = (TextView)convertView.findViewById(R.id.item_record_history_index);
			holder.kidneyIDTv = (TextView)convertView.findViewById(R.id.item_list_record_history_id);
			holder.startTimeTv = (TextView)convertView.findViewById(R.id.item_list_record_history_time);
			holder.leftKidneyWeightTv = (TextView)convertView.findViewById(R.id.item_list_record_history_left_kidney_weight);
			holder.rightKidneyWeightTv = (TextView)convertView.findViewById(R.id.item_list_record_history_right_kidney_weight);
			holder.leftKidneyModeTv = (TextView)convertView.findViewById(R.id.item_list_record_history_artery_mode);
			holder.rightKidneyModeTv = (TextView)convertView.findViewById(R.id.item_list_record_history_vein_mode);
			holder.checkbox = (CheckBox)convertView.findViewById(R.id.item_list_record_check);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.indexTxt.setText(String.valueOf(perfusionLogBean.getIndex()));
		holder.kidneyIDTv.setText(perfusionLogBean.getKidneyName());
		holder.startTimeTv.setText(perfusionLogBean.getStartTime());
		holder.leftKidneyWeightTv.setText(perfusionLogBean.getLeftKidneyWeight());
		holder.rightKidneyWeightTv.setText(perfusionLogBean.getRightKidneyWeight());
		holder.leftKidneyModeTv.setText(perfusionLogBean.getLeftKidneyMode());
		holder.rightKidneyModeTv.setText(perfusionLogBean.getRightKidneyMode());
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
		TextView kidneyIDTv;
		TextView leftKidneyWeightTv;
		TextView rightKidneyWeightTv;
		TextView startTimeTv;
		TextView leftKidneyModeTv;
		TextView rightKidneyModeTv;
		TextView indexTxt;
		CheckBox checkbox;
	}
}
