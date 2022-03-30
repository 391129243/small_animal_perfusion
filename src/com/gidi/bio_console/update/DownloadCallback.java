package com.gidi.bio_console.update;

/****
 * ���صĻص��ӿ�
 *
 */
public interface DownloadCallback {

	public void onDownloadPrepare();

	public void onChangeProgress(int progress);

	public void onCompleted(boolean success,String errorMsg);

	public boolean onCancel();
}

