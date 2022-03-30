package com.gidi.bio_console.remotectrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.gidi.bio_console.utils.DateFormatUtil;

import com.gidi.bio_console.constant.RemoteCtrlContants;

//import com.gidi.bio_console.entity.RemoteCtrlContants;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** 姣忎竴涓湇鍔″櫒瀛愮嚎绋� **/
public class TcpServerSocketThread implements Runnable {

	private Socket serverSocket;
	private InputStream is;
	private OutputStream os;
	private String ip = null;
	private boolean isRun = true;
	private Handler mHandler;

	public TcpServerSocketThread(Socket socket, Handler handler) {
		this.serverSocket = socket;
		this.ip = serverSocket.getInetAddress().toString();
		this.mHandler = handler;
		Log.i("TcpMsgServer", "---ServerSocketThread-ip--" + ip + "----"
				+ DateFormatUtil.getSysFullTime());

	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// serverSocket.setKeepAlive(true);
			// serverSocket.setSoTimeout(10000);
			// serverSocket.
			is = serverSocket.getInputStream();
			os = serverSocket.getOutputStream();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int len = 0;
		Boolean headflag = false;
		Boolean endflag = false;
		byte buf[] = new byte[1024];
		String endString = "    ";
		byte[] end = endString.getBytes();
		StringBuffer recMsgBuffer = new StringBuffer();
		Log.i("TcpMsgServer", "---ServerSocketThread---" + serverSocket);
		Log.i("TcpMsgServer", "---ServerSocketThread-isRun--" + isRun);
		Log.i("TcpMsgServer", "---ServerSocketThread-serverSocket.isClosed()--"
				+ serverSocket.isClosed());
		Log.i("TcpMsgServer",
				"---ServerSocketThread-serverSocket.isInputShutdown()--"
						+ serverSocket.isInputShutdown());
		while (isRun && !serverSocket.isClosed()
				&& !serverSocket.isInputShutdown()) {
			try {
				Thread.sleep(5000);
				Log.i("TcpMsgServer",
						"sleep 3 second" + "   " + serverSocket.getPort()
								+ "    " + android.os.Process.myTid()
								+ serverSocket.isClosed()
								+ serverSocket.isInputShutdown());
				while (is.available() > 0) {
					// while((len = is.read(buf)) != -1){

					len = is.read(buf);
					Log.i("TcpMsgServer", "error getInputStream len -----" + len);
					if (len <= 0) {
						break;
					}

					String temp = new String(buf, 0, len);

					//Log.i("TcpMsgServer", "---recv --" + temp);

					recMsgBuffer.append(temp);
					String tempString = recMsgBuffer.toString();

					byte head[] = { 0x0B };

					byte end1[] = { 0x1C, 0x0D };

					String headString = new String(head);

					String end1String = new String(end1);

					if (headflag = false) {
						if (tempString.contains(headString)) {

							headflag = true;
						}
					} else {
						if (tempString.contains(end1String)) {

							headflag = false;

							int headindex = tempString.indexOf(headString);

							int endindex = tempString.indexOf(end1String);

							Log.i("TcpMsgServer", "---start--" + headindex);

							Log.i("TcpMsgServer", "---end--" + endindex);
							if (endindex > headindex) {
								String target = tempString.substring(
										headindex + 1, endindex);

								Log.i("TcpMsgServer", "---targetstring--"
										+ android.os.Process.myTid() + target);

								recMsgBuffer.delete(0, endindex + 2);

								os.write(end);

								sendResponeMsg(target);
							}

						}

					}
				}
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				Log.i("TcpMsgServer", "error read InputStream timeout -----"
						+ e.toString());
				e.printStackTrace();
				handleClose();
				// break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("TcpMsgServer", "error  -----" + e.toString());
				handleClose();
			}
		}
		handleClose();
	}

	private void handleClose() {
		try {
			Log.i("TcpMsgServer",
					"socket close-----" + android.os.Process.myTid());
			serverSocket.close();
			serverSocket.shutdownInput();
			serverSocket.shutdownOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendResponeMsg(String recMsg) {
		Message msg = Message.obtain();
		msg.what = RemoteCtrlContants.MSG_RECEIVE_BIOINDEX_DATA;
		// msg.what = 123;
		msg.obj = recMsg;
		mHandler.sendMessage(msg);
	}

	public void setIsRun(boolean isrun) {
		this.isRun = isrun;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public void close() {
		try {
			if (null != serverSocket) {
				serverSocket.close();
				serverSocket = null;
			}

			if (null != is) {
				is.close();
				is = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
