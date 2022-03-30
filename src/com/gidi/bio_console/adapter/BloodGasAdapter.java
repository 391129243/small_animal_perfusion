package com.gidi.bio_console.adapter;

import java.util.ArrayList;

import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.BloodGasBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BloodGasAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<BloodGasBean> mBloodGasList = new ArrayList<BloodGasBean>();
	
	public BloodGasAdapter(Context context){
		mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setBloodGasList(ArrayList<BloodGasBean> list){
		mBloodGasList.clear();
		mBloodGasList.addAll(list);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBloodGasList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBloodGasList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		final BloodGasBean bloodGasBean = mBloodGasList.get(position);
		if(null == convertView){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.module_list_item_bloodgas_layout, parent, false);
			viewHolder.samplingTime = (TextView)convertView.findViewById(R.id.item_bloodgas_sampletime);
			viewHolder.ast = (TextView)convertView.findViewById(R.id.item_bloodgas_ast);
			viewHolder.alt = (TextView)convertView.findViewById(R.id.item_bloodgas_alt);
			viewHolder.Glu = (TextView)convertView.findViewById(R.id.item_bloodgas_glu);
			viewHolder.pH = (TextView)convertView.findViewById(R.id.item_bloodgas_ph);
			viewHolder.pO2 = (TextView)convertView.findViewById(R.id.item_bloodgas_po2);
			viewHolder.pCO2 = (TextView)convertView.findViewById(R.id.item_bloodgas_pco2);
			viewHolder.bicarbonate = (TextView)convertView.findViewById(R.id.item_bloodgas_bicarbonate);
			viewHolder.hct = (TextView)convertView.findViewById(R.id.item_bloodgas_hct);
			viewHolder.lac = (TextView)convertView.findViewById(R.id.item_bloodgas_lac);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		if(position % 2 == 0){
			convertView.setBackgroundResource(R.color.color_blood_gas_item_even);
		}else {
			convertView.setBackgroundResource(R.color.color_blood_gas_item_odd);
		}
		viewHolder.samplingTime.setText(bloodGasBean.getSampleTime());
		viewHolder.ast.setText(bloodGasBean.getAst());
		viewHolder.alt.setText(bloodGasBean.getAlt());
		viewHolder.Glu.setText(bloodGasBean.getGlu());
		viewHolder.pH.setText(bloodGasBean.getPh());
		viewHolder.pO2.setText(bloodGasBean.getPo2());
		viewHolder.pCO2.setText(bloodGasBean.getPco2());
		viewHolder.bicarbonate.setText(bloodGasBean.getBicarbonate());
		viewHolder.hct.setText(bloodGasBean.getHct());
		viewHolder.lac.setText(bloodGasBean.getLac());
		return convertView;
	}

	static class ViewHolder{
		TextView samplingTime;
		TextView ast;
		TextView alt;
		TextView Glu;
		TextView pH;
		TextView pO2;
		TextView pCO2;
		TextView bicarbonate;
		TextView hct;
		TextView lac;
	}

}
