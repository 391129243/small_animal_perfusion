package com.gidi.bio_console.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.gidi.bio_console.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class LineChartUtils {

	private LineChart lineChart;
	private Context mContext;
	public LineChartUtils(Context mContext, LineChart lineChart){
		this.mContext = mContext.getApplicationContext();
		this.lineChart  = lineChart;
	}
	
	/**初始化图表***/
	public void initLineChart(){
		// 是否显示表格颜色
        lineChart.setDrawGridBackground(true);
        // 禁止绘制图表边框的线
        lineChart.setDrawBorders(false);
        lineChart.setTouchEnabled(true);
        // 是否可以拖拽
        lineChart.setDragEnabled(true);
        // 是否可以缩放
        lineChart.setScaleEnabled(true);
        // 如果禁用，可以在x和y轴上分别进行缩放
        lineChart.setPinchZoom(false);
        Legend mLegend = lineChart.getLegend();
        // 图例样式 (CIRCLE圆形；LINE线性；SQUARE是方块）
        mLegend.setForm(LegendForm.SQUARE);
        // 图例大小
        mLegend.setFormSize(8f);
        // 图例上的字体颜色
        mLegend.setTextColor(mContext.getResources().getColor(R.color.blue));
        mLegend.setTextSize(11f);
        // 图例的显示和隐藏
        mLegend.setEnabled(true);
	}
	
	public void setDescription(String title){
		Description desc = new Description();
		desc.setText(title);
		desc.setPosition(0f, 0f);
		desc.setTextSize(16f);
		desc.setTextColor(mContext.getResources().getColor(R.color.black));
		lineChart.setNoDataText(mContext.getResources().getString(R.string.chart_no_data));
		lineChart.setDescription(desc);
	}
	
	public void setScaleEnable(boolean isEnable){
		lineChart.setScaleEnabled(isEnable);
	}
	
	/**设置图表Y轴**/
	public void setLineChartYLeft(){
		YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setStartAtZero(true);
        //leftAxis.setAxisMaxValue(maxvalue);
	}
	
	public void setLineChartData(List<String> xDataList,List<Entry> yDataList,String curveLable,String title,int color){
		ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
		//Y轴数据集合
		LineDataSet lineDataSet = new LineDataSet(yDataList, curveLable);
		//不显示坐标轴点的数据
		lineDataSet.setDrawValues(false);
		lineDataSet.setDrawCircles(false);
		lineDataSet.setHighlightEnabled(true);
		//设置线宽
		lineDataSet.setLineWidth(2.0f);
		lineDataSet.setColor(mContext.getResources().getColor(color));
		//高亮线的颜色
		lineDataSet.setHighLightColor(mContext.getResources().getColor(R.color.color_artery));
		//设置曲线和X轴的没有填充
		lineDataSet.setDrawFilled(false);
		//设置坐标轴在左侧
		lineDataSet.setAxisDependency(AxisDependency.LEFT);
	    // 设置每条曲线图例标签名
        lineDataSet.setLabel(title);
        lineDataSet.setValueTextSize(14f);
        // 曲线弧度（区间0.05f-1f，默认0.2f）
        lineDataSet.setCubicIntensity(0.2f);
        // 设置为曲线显示,false为折线
        
        lineDataSets.add(lineDataSet);
        
        LineData lineData = new LineData(lineDataSets);
        lineChart.setData(lineData);
        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        // 显示X轴上的刻度值
        xAxis.setDrawLabels(true);
        // 设置X轴的数据显示在报表的下方
        xAxis.setPosition(XAxisPosition.BOTTOM);
        // 轴线
         xAxis.setDrawAxisLine(true);
        // 设置不从X轴发出纵向直线
        xAxis.setValueFormatter(new TimeAxisValueFormatter(xDataList));
        xAxis.setDrawGridLines(true);//x网格线
        // 执行的动画,x轴（动画持续时间）
        lineChart.animateX(2500);
        lineChart.invalidate();
        lineChart.notifyDataSetChanged();
	}
	
	
	/**清除图表**/
	public void clearChart(){
        lineChart.clear();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();   
	}
	
	private static class TimeAxisValueFormatter implements IAxisValueFormatter{

		private List<String> mDataList = new ArrayList<String>();;
		public TimeAxisValueFormatter(List<String> DataList){
			this.mDataList.clear();
			this.mDataList = DataList;
			Log.i("ChartUtil", "mDataList size" + mDataList.size());
		}
		
		@Override
		public String getFormattedValue(float value, AxisBase axis) {
			// TODO Auto-generated method stub
			int index = (int)value;
			if(index < 0 || index >= mDataList.size()){
				return "";
			}else{
				if(null != mDataList.get((int)value)){
					return DateFormatUtil.getDateHMS(mDataList.get((int)value));
				}else{
					return "";
				}
			}
			
		}
		
	}
}
