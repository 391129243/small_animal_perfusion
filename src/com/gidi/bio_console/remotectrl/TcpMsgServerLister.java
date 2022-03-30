package com.gidi.bio_console.remotectrl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.util.Log;


public class TcpMsgServerLister extends Thread{

	private static final String TAG = "TcpMsgServer";
	private int mPort = 8000;
	private boolean isListen = true;
	private ServerSocket serverSocket;
	private Executor threadPool = Executors.newCachedThreadPool();
	private ArrayList<TcpServerSocketThread> mServerThreadList = new ArrayList<TcpServerSocketThread>();
	private Handler mHandler;
	TcpServerSocketThread threadBackup;
	
	Socket socketTemp;
	
	public TcpMsgServerLister(int port, Handler handler){
		this.mPort = port;
		this.mHandler = handler;
	}
	
	public void setListen(boolean islisten){
		this.isListen = islisten;
	}
	
	private Socket getSocket(ServerSocket serverSocket){
		try {
			
			return serverSocket.accept();
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(TAG, "---getSocket Exception--" + e.getMessage());
			return null;
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Log.i(TAG, "---run--" + mPort);
			serverSocket = new ServerSocket(mPort);
			//serverSocket.setSoTimeout(5000);
			while (isListen) {
				Socket socket = getSocket(serverSocket);
				Log.i(TAG, "---getSocket --" + socket);
				if(null != socket){
					//isListen = false;
					//new TcpServerSocketThread(socket,mHandler).start();
					if(threadBackup != null){
						
						threadBackup.setIsRun(false);
					}
					
					TcpServerSocketThread thread = new TcpServerSocketThread(socket,mHandler);
					
					threadBackup = thread;
					
					threadPool.execute(thread);
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	
	public void closeSelf(){
		isListen = false;
		if(null != serverSocket){
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(TcpServerSocketThread thread : mServerThreadList){
			thread.setIsRun(false);
			thread.close();
		}
		mServerThreadList.clear();
		mServerThreadList = null;
		
	}
}
