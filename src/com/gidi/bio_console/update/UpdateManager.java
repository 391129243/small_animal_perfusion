package com.gidi.bio_console.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.gidi.bio_console.R;
import com.gidi.bio_console.bean.UpdateInfo;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.utils.NetworkUtil;
import com.gidi.bio_console.view.CustomDialog;

public class UpdateManager {

	private static final String TAG = "UpdateUtils";
	private static final String versionPath = "http://172.16.13.2:8080/update_version.xml";
	private static Context mContext;


    private static final int MSG_CHECK_FAIL = 0;
    private static final int MSG_CHECK_SUCCESS = 1;
	private static final int MSG_NEWEST_VERSION = 2;
    private static final int MSG_CHECK_NETFAIL = 4;
	private static final int MSG_DOWNLOAD_FINISH = 6;
	
    private ProgressDialog mProgressDialog;
    private UpdateInfo mUpdateInfo;
    private InputStream result = null;
    private boolean isAccord;
	private static UpdateManager mInstance;
	private UpdateHandler mHandler;
	private String downloadPath;
	
	private static class UpdateHandler extends Handler{

		private  WeakReference<UpdateManager> mUpdateReference;

		public UpdateHandler(UpdateManager updateManager) {
			mUpdateReference = new WeakReference<UpdateManager>(updateManager);
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final UpdateManager mUpdateHandler = mUpdateReference.get();
			if(null == mUpdateHandler){
				return;
			}
			if(null != mUpdateHandler.mProgressDialog){
				mUpdateHandler.mProgressDialog.dismiss();
			}
			switch (msg.what) {			
				case MSG_NEWEST_VERSION:{
					mUpdateHandler.showNewestDialog();
					Toast.makeText(mContext, R.string.version_newest, Toast.LENGTH_SHORT).show();
					break;
				}
					
				case MSG_CHECK_SUCCESS:{
					mUpdateHandler.showNoticeDialog();
					break;
				}

				case MSG_DOWNLOAD_FINISH:

					break;
					
				case MSG_CHECK_FAIL:{
					Toast.makeText(mContext, R.string.update_error, Toast.LENGTH_SHORT).show();
				}
				
				case MSG_CHECK_NETFAIL:{
					Toast.makeText(mContext, R.string.internet_connect_unavailable, Toast.LENGTH_SHORT).show();
					break;
				}
					
				default:
					break;
			}
		}
		
	};
	
	public static UpdateManager getInstance(Context context,boolean isAccord){
		if(null == mInstance){
			mInstance = new UpdateManager(context ,isAccord);
		}
		return mInstance;
	}
	
	public UpdateManager(Context context, boolean isAccord){
		this.mContext = context;
		this.isAccord = isAccord;
		mHandler= new UpdateHandler(this);
	}
	
	
	
	public void checkNewVersion(){
		
		if(isAccord){
			mProgressDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.checking_version));
		}
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if(!NetworkUtil.isConnect(mContext)){
					mHandler.sendEmptyMessage(MSG_CHECK_NETFAIL);
				}else if(isAccord){
					try {
						result = getVersionResult(versionPath);
						if(null != result){
							if(isUpdate(result)){
								mHandler.sendEmptyMessage(MSG_CHECK_SUCCESS);
							}else{
								mHandler.sendEmptyMessage(MSG_NEWEST_VERSION);
							}
						}else{
							mHandler.sendEmptyMessage(MSG_CHECK_FAIL);
						}						
					} catch (Exception e) {
						// TODO: handle exception
						//δ���µ�����
						mHandler.sendEmptyMessage(MSG_CHECK_FAIL);
					}
				}
			}
			
			
		}.start();				
	}
	

	private InputStream getVersionResult(String versionPath){

		InputStream is = null;
		try {
			URL url = new URL(versionPath);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(5000);
			is = conn.getInputStream();
			Log.i(TAG, "getVersionResult---" + is);
			return is;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	private boolean isUpdate(InputStream is){
		try {
			mUpdateInfo = getUpdateUnfo(is);
			downloadPath = getApkSavePath();
			Log.i(TAG, "--downloadPath--" + downloadPath);
			if(mUpdateInfo.getVersionCode() > getAppVersionCode()){
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	private UpdateInfo getUpdateUnfo(InputStream is) throws IOException{

		UpdateInfo updateInfo = null;
		XmlPullParser parser = Xml.newPullParser();		
		try {
			parser.setInput(is, "utf-8");
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT){
				switch (eventType) {
				 	case XmlPullParser.START_DOCUMENT:
	                    break;
	                case XmlPullParser.END_DOCUMENT:
	                    break;
	                case XmlPullParser.TEXT:
	                    break;
					case XmlPullParser.START_TAG:
					{
						String tagName = parser.getName();
					
						if(tagName != null && tagName.equals("update")){
							updateInfo = new UpdateInfo();
							
						}else if(tagName != null && tagName.equals("versionCode")){
							String versionCode = parser.nextText();
							updateInfo.setVersionCode(Integer.parseInt(versionCode));
								
						}else if(tagName != null && tagName.equals("versionName")){
							String versionName = parser.nextText();
							updateInfo.setVersionName(versionName);
							
						}else if(tagName != null && tagName.equals("apkName")){
							String apkName = parser.nextText();
							updateInfo.setApkName(apkName);
							
						}else if(tagName != null && tagName.equals("downloadUrl")){
							String downloadurl = parser.nextText();
							updateInfo.setDownloadUrl(downloadurl);
							
						}else if("description".equals(parser.getName())){
							String description = parser.nextText();
							updateInfo.setDescription(description);

						}
						break;
					}
					
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return updateInfo;
	}
	

	public void showNoticeDialog(){

		CustomDialog noiceDialog  = new CustomDialog(mContext);
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle(R.string.version_updates);
		builder.setMessage(mUpdateInfo.getDescription());
		builder.setPositiveButton(R.string.update_immediately, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String downloadUrl = mUpdateInfo.getDownloadUrl();
				DownloadCallback downCallback = new DownloadImpl(mContext, downloadUrl,downloadPath);
				DownloadAsyncTask request = new DownloadAsyncTask(mContext,downCallback);
				Log.i(TAG, "mUpdateInfo.getDownloadUrl()---" + mUpdateInfo.getDownloadUrl());
				request.execute(mUpdateInfo.getDownloadUrl(),downloadPath);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.update_later, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		noiceDialog = builder.create();
		noiceDialog.show();

	}
	

	private void showNewestDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final LayoutInflater inflater = LayoutInflater.from(mContext);  
        View v = inflater.inflate(R.layout.layout_newest_version, null);  
		builder.setView(v);
		builder.setCancelable(true);
		Dialog newestDialog = builder.create();
		newestDialog.show();
	}
	


	public int getAppVersionCode(){
		int versionCode = 0;
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
			versionCode = pi.versionCode;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return versionCode;
	}
	
	private String getApkSavePath(){
		String downloadPath = "";
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String downloadDirPath = Environment.getExternalStorageDirectory() + File.separator + Constants.APP_NAME + File.separator+ "download";
			downloadPath = downloadDirPath + "/" + mUpdateInfo.getApkName();
			File downloadDir = new File(downloadDirPath);
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}
		}
	
		return downloadPath;
	}
}
