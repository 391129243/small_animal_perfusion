package com.gidi.bio_console.mgr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.gidi.bio_console.R;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class LineChartMgr {
	/****
	 * 显示图表
	 * @param context
	 * @param lineChart
	 * 			图表对象
	 * @param xDataList
	 * 			X轴数据
	 * @param yDataList
	 * 			Y轴数据
	 * @param title
	 * 			图表标题
	 * @param curveLable
	 * 			曲线图例名称
	 * @param unitName
	 * 			坐标点击弹出框的提示的单位
	 */
	public static void showChart(Context context, LineChart lineChart, List<String> xDataList,
	        List<Entry> yDataList, String title, String curveLable,String unitName,int color){
		//设置数据
		LineData lineData = setLineData(context, xDataList, yDataList, curveLable, title,color,true);
		lineChart.setData(lineData);
		//是否添置边框
		
		//曲线描述,设置标题名称标题大小，标题颜色
		Description desc = new Description();
		desc.setText(title);
		desc.setPosition(0f, 0f);
		desc.setTextSize(16f);
		desc.setTextColor(context.getApplicationContext().getResources().getColor(R.color.black));
		lineChart.setDescription(desc);
		lineChart.setNoDataText(context.getApplicationContext().getResources().getString(R.string.chart_no_data));
		// 是否显示表格颜色
		lineChart.setDrawBorders(false);
        // 禁止绘制图表边框的线
        lineChart.setDrawGridBackground(true);
        // 表格的的颜色，在这里是是给颜色设置一个透明度
        // 设置是否启动触摸响应
        lineChart.setTouchEnabled(true);
        // 是否可以拖拽
        lineChart.setDragEnabled(true);
        // 是否可以缩放
        lineChart.setScaleEnabled(true);
        // 如果禁用，可以在x和y轴上分别进行缩放
        lineChart.setPinchZoom(false);
        // lineChart.setMarkerView(mv);
        // 设置背景色
        // lineChart.setBackgroundColor(getResources().getColor(R.color.bg_white));
        // 图例对象
        Legend mLegend = lineChart.getLegend();
        // mLegend.setPosition(LegendPosition.BELOW_CHART_CENTER);
        // 图例样式 (CIRCLE圆形；LINE线性；SQUARE是方块）
        mLegend.setForm(LegendForm.SQUARE);
        // 图例大小
        mLegend.setFormSize(8f);
        // 图例上的字体颜色
        mLegend.setTextColor(context.getApplicationContext().getResources().getColor(color));
        mLegend.setTextSize(11f);
        // 图例字体
        // mLegend.setTypeface(mTf);
        // 图例的显示和隐藏
        mLegend.setEnabled(true);
        // 隐藏右侧Y轴（只在左侧的Y轴显示刻度）
        lineChart.getAxisRight().setEnabled(false);
        
        setXAxis(xDataList,lineChart);
        
        // 执行的动画,x轴（动画持续时间）
        lineChart.animateX(2500);
        lineChart.invalidate();
        lineChart.notifyDataSetChanged();
	}
	
	/**设置X轴**/
	private static void setXAxis(List<String> xDataList , LineChart lineChart){
		XAxis xAxis = lineChart.getXAxis();
        // 显示X轴上的刻度值
        xAxis.setDrawLabels(true);
        // 设置X轴的数据显示在报表的下方
        xAxis.setPosition(XAxisPosition.BOTTOM);
        // 轴线
        xAxis.setDrawAxisLine(true);
        // 设置不从X轴发出纵向直线

        xAxis.setLabelCount(xDataList.size(),true);
        xAxis.setValueFormatter(new TimeAxisValueFormatter(xDataList));

        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);//x网格线
        //避免裁剪第一个和最后一个标签
        xAxis.setAvoidFirstLastClipping(true);
	}
	
	private static class TimeAxisValueFormatter implements IAxisValueFormatter{

		private List<String> mDataList;
		public TimeAxisValueFormatter(List<String> DataList){
			this.mDataList = DataList;
		}
		
		@Override
		public String getFormattedValue(float value, AxisBase axis) {
			// TODO Auto-generated method stub
			if((int)value < mDataList.size()&& (int)value > -1){
				return DateFormatUtil.getDateHMS(mDataList.get((int)value));
			}else{
				return "";
			}
		}
		
	}
	
	/***
	 * 曲线的赋值与设置
	 * @author Administrator
	 * 
	 */
	private static LineData setLineData(Context context, List<String> xDataList, List<Entry>yDataList,String curveLable,String title,int color, boolean isDrawCircle){
		ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
		//Y轴数据集合
		LineDataSet lineDataSet = new LineDataSet(yDataList, curveLable);
		//不显示坐标轴点的数据
		lineDataSet.setValueTextColor(Color.BLUE);
		lineDataSet.setDrawValues(true);
		lineDataSet.setDrawCircles(isDrawCircle);
		lineDataSet.setDrawCircleHole(false);
		lineDataSet.setHighlightEnabled(true);
		setLineDataColor(lineDataSet,context,color);
		//设置线宽
		lineDataSet.setLineWidth(2.0f);
		lineDataSet.setColor(context.getApplicationContext().getResources().getColor(color));
		//高亮线的颜色
		lineDataSet.setHighLightColor(context.getApplicationContext().getResources().getColor(R.color.color_artery));
		//设置曲线和X轴的没有填充
		lineDataSet.setDrawFilled(false);
		//设置坐标轴在左侧
		lineDataSet.setAxisDependency(AxisDependency.LEFT);
	    // 设置每条曲线图例标签名
        lineDataSet.setLabel(title);
        lineDataSet.setValueTextSize(9f);
        // 曲线弧度（区间0.05f-1f，默认0.2f）
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setCircleRadius(4f);
        // 设置为曲线显示,false为折线        
        lineDataSets.add(lineDataSet);
        
        LineData lineData = new LineData(lineDataSets);
        return lineData;
	}
	
	public static void clearChart(LineChart lineChart) {
        lineChart.clear();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();        
    }
	
	/**设置描述信息**/
	public void setDiscription(Context context,LineChart lineChart, String title,int color){
		Description desc = new Description();
		desc.setText(title);
		desc.setPosition(0f, 0f);
		desc.setTextSize(16f);
		desc.setTextColor(context.getApplicationContext().getResources().getColor(R.color.black));
		lineChart.setDescription(desc);
	}
	
	/**设置折线的颜色**/
	public static void setLineDataColor(LineDataSet lineDataSet ,Context context, int color){
		lineDataSet.setColor(context.getApplicationContext().getResources().getColor(color));
	}
}
