package com.gidi.bio_console.update;

import java.io.File;

import com.gidi.bio_console.R;
import com.gidi.bio_console.utils.NetworkUtil;
import com.gidi.bio_console.utils.ToastUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class DownloadImpl implements DownloadCallback{
	private static final String TAG = "DownloadImpl";
	private Context mContext;
	private String apkPath;
	private String downloadUrl;
	private LayoutInflater mInflater;
	private AlertDialog mDownloadDialog;
	private ProgressBar mProgressBar;
	private boolean interceptFlag = false;
	
	public DownloadImpl(Context context, String downloadUrl, String apkPath){
		this.mContext = context;
		this.downloadUrl = downloadUrl;
		//this.apkPath = apkPath;
		this.apkPath = apkPath;
		Log.i(TAG, "apkPath--" + apkPath);
		mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public void onDownloadPrepare() {
		// TODO Auto-generated method stub
		if(NetworkUtil.checkSoftStage(mContext)){		
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.download_version);
			View view = mInflater.inflate(R.layout.layout_download_apk, null);
			mProgressBar = (ProgressBar)view.findViewById(R.id.download_progress);
			builder.setView(view);
			
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					interceptFlag = true; 
				}
			});
			mDownloadDialog = builder.create();
			mDownloadDialog.show();
		}
	}

	@Override
	public void onChangeProgress(int progress) {
		// TODO Auto-generated method stub
		mProgressBar.setProgress(progress);
		
	}

	@Override
	public void onCompleted(boolean success, String errorMsg) {
		
		// TODO Auto-generated method stub
		if(null != mDownloadDialog){
			mDownloadDialog.dismiss();
		}
		if(success){
			installApk();
		}else{
			ToastUtils.showToast(mContext.getApplicationContext(), errorMsg);		
		}
	}

	@Override
	public boolean onCancel() {
		// TODO Auto-generated method stub
		return interceptFlag;
	}

	private void installApk(){
		File file = new File(apkPath);
		if(!file.exists()){
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}
