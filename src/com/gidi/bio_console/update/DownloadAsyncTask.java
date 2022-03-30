package com.gidi.bio_console.update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.gidi.bio_console.R;

import android.content.Context;
import android.os.AsyncTask;

public class DownloadAsyncTask extends AsyncTask<String, Integer, Integer> {
	
	private Context mContext;
	private DownloadCallback downCallBack;
	private HttpURLConnection urlConn;
	private static final int TYPE_FAIL = 0;
	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_CANCEL = 2;	
	
	public DownloadAsyncTask(Context context ,DownloadCallback downloadCallback){
		this.mContext = context;
		this.downCallBack = downloadCallback;
	}
	
	@Override
	protected void onPreExecute() {
		downCallBack.onDownloadPrepare();
		super.onPreExecute();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		
		String apkDownloadUrl = params[0];//apk下载地址
		String apkSavePath = params[1];   //apk在sd卡中的安装位置
		File saveFile = new File(apkSavePath);
		if(!saveFile.isFile()){
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!saveFile.exists()){
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			URL url = new URL(apkDownloadUrl);
			urlConn = (HttpURLConnection)url.openConnection();
			
			urlConn.setConnectTimeout(5000);
			urlConn.connect();
			is = urlConn.getInputStream();
			int length = urlConn.getContentLength();//文件大小  

			fos = new FileOutputStream(saveFile);
			bos = new BufferedOutputStream(fos);
			int count = 0;
			int numread = 0;
			byte buf[] = new byte[1024];
			
			while((numread = is.read(buf)) != -1){
				if(downCallBack.onCancel()){
					bos.close();
					return TYPE_CANCEL;
				}else{
					count += numread;
					int progressCount = (int)(((float)count / length) * 100);
					bos.write(buf, 0, numread);
					publishProgress(progressCount);
					
				}
			}
			bos.flush();
			return TYPE_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return TYPE_FAIL;
		}finally{
			try {
				if(null != bos)
					bos.close();
				if(null != is)
					is.close();
				if(downCallBack.onCancel() && null != saveFile){
					saveFile.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return TYPE_FAIL;
			}
		}		
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		downCallBack.onChangeProgress(values[0]);
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
			case TYPE_CANCEL:
				downCallBack.onCompleted(false, mContext.getResources().getString(R.string.update_cancel));
				break;
			case TYPE_FAIL:
				downCallBack.onCompleted(false, mContext.getResources().getString(R.string.fail_to_update));
				break;
			case TYPE_SUCCESS:
				downCallBack.onCompleted(true, null);
				break;
			default:
				break;
		}		
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		if(null != urlConn){
			urlConn.disconnect();
		}
		super.onCancelled();
	}
	
	private long getContentLength(String downloadUrl){
		long contentLength = 0;		
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(3000);
			conn.connect();
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				contentLength = conn.getContentLength();			
			}
			conn.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contentLength;
	}
}