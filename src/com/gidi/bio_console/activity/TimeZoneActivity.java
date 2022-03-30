package com.gidi.bio_console.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.adapter.TimeZoneAdapter;
import com.gidi.bio_console.base.BaseActivity;

public class TimeZoneActivity extends BaseActivity implements OnClickListener {

	private UIHandler mUIHandler;
	private ListView timezoneListView;
	//存放时区信息
	private HashMap<String, String> timezoneMap;
	//存放时区名用于列表显示
	private List<String> timeZoneList;
	private TimeZoneAdapter timeZoneAdapter;
	private ImageView backImg;
	private static final int TIMEZONE_ID = 0;
	private static final int MSG_UPDATE_TIMEZONE = 650;
	private int resultCode = 2;
	
	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.module_activity_timezone_setting;
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		timezoneListView = (ListView)findViewById(R.id.timezone_listview);
		backImg = (ImageView)findViewById(R.id.time_zone_title_back_img);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		timezoneListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				//String timeZoneId = timezoneMap.get(timeZone);
				String[] timeZone = splitTimeZoneStr(timeZoneList.get(position));
				String timeZoneGMT_id = "";
				if(timeZone != null && timeZone.length == 2){
					timeZoneGMT_id =  timeZone[1];
				}
				Intent newIntent = new Intent();
				newIntent.putExtra("timezone", timeZoneGMT_id); //将计算的值回传回去
				//通过intent对象返回结果，必须要调用一个setResult方法，
				setResult(resultCode, newIntent);
				finish();				
			}
		});
		backImg.setOnClickListener(this);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != timeZoneList){
			timeZoneList.clear();
			timeZoneList = null;
		}
		
		if(null != timezoneMap){
			timezoneMap.clear();
			timezoneMap = null;
		}
		

		if(null != mUIHandler){
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
		}
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mUIHandler = new UIHandler(this);
		timezoneMap = new HashMap<String, String>();
		timeZoneList = new ArrayList<String>();
		timeZoneAdapter = new TimeZoneAdapter(getApplicationContext());
		timezoneListView.setAdapter(timeZoneAdapter);

		getTimeZonesXMLContent();
	}
	
	
	public void getTimeZonesXMLContent() {
		XmlResourceParser xrp = null;
		try {
			timezoneMap.clear();
			timeZoneList.clear();
			xrp = getResources().getXml(R.xml.timezones);
			while (xrp.next() != XmlResourceParser.START_TAG) {
				continue;
			}
			xrp.next();
			//int readCount = 0;
			while (xrp.getEventType() != XmlResourceParser.END_TAG) {
				while (xrp.getEventType() != XmlResourceParser.START_TAG) {
					if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
						return;
					}
					xrp.next();
				}
				if (xrp.getName().equals("timezone")) {
					String id = xrp.getAttributeValue(TIMEZONE_ID);
					String timezoneName = xrp.nextText();
					timezoneMap.put(timezoneName,id);
					timeZoneList.add(timezoneName);
				}
				while (xrp.getEventType() != XmlResourceParser.END_TAG) {
					xrp.next();
				}
				xrp.next();
			}
			xrp.close();
		} catch (XmlPullParserException xppe) {
			Log.e("TimeZoneActivity", "Ill-formatted timezones.xml file");
		} catch (java.io.IOException ioe) {
			Log.w("TimeZoneActivity", "Unable to read timezones.xml file");
		} finally {
			if (null != xrp) {
				xrp.close();
			}
		}
		
		Log.i("TimeZoneActivity", "getTimeZones.timeZoneList" + timeZoneList.size());
		mUIHandler.sendEmptyMessage(MSG_UPDATE_TIMEZONE);
	}
	
	private String[] splitTimeZoneStr(String timeZone){
		 if(timeZone != null && timeZone.contains("/")){
			 String[] strArr = timeZone.split("\\/");
			 return strArr;
		 }
		 return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.time_zone_title_back_img:			
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
			finish();
			break;

		default:
			break;
		}
	}
	
	public static class UIHandler extends Handler{
		
		WeakReference<TimeZoneActivity> mActivityReference;
		
		public UIHandler(TimeZoneActivity mActivity){
			mActivityReference = new WeakReference<TimeZoneActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final TimeZoneActivity mActivity = mActivityReference.get();
			if(null == mActivity){
				return;
			}
			switch (msg.what) {
				case MSG_UPDATE_TIMEZONE:
					Log.i("TimeZoneActivity", "mActivity.timeZoneList" + mActivity.timeZoneList.size());					
					if(mActivity.timezoneMap.size()>0 && mActivity.timeZoneList.size()>0){
						mActivity.timeZoneAdapter.setTimeZoneList(mActivity.timeZoneList);
						mActivity.timeZoneAdapter.notifyDataSetChanged();
					}

				break;
			default:
				break;
			}
		}
		
		
	}




}
