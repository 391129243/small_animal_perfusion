package com.gidi.bio_console.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.bean.KidneyInfoBean;
import com.gidi.bio_console.bean.SerialMessage;
import com.gidi.bio_console.common.ErrorCode;
import com.gidi.bio_console.constant.BroadcastActions;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.DefValues;
import com.gidi.bio_console.constant.SerialMsgConstant;
import com.gidi.bio_console.constant.SerialPortConstant;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.db.DatabaseMgr;
import com.gidi.bio_console.serialport.SerialPortInstruct;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
/****
 * 后台的Service
 * 用于播放报警铃声
 * 接收串口消息，发送串口消息指令
 * 使用线程池进行管理线程
 * @author Administrator
 *
 */
public class BackService extends Service {

	private static final String TAG = "BackService";
	private Binder mBinder = new BackBinder();
	private MediaPlayer mMediaPlayer;			
	private BioConsoleApplication mApplication;
	//数据库操作对象
	private DatabaseMgr mDatabaseMgr = null;	
	private SerialPortFinder mSerialPortFinder = null;
	private SerialPort mSerialPort_perfusion = null;	
	//使用线程池管理定时任务
	//private ScheduledExecutorService mScheduledThreadPool = Executors.newScheduledThreadPool(1);
	private ScheduledThreadPoolExecutor  scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
	private ReadSerialPortThread mReadThread;	
	private HandleDisconnectThread mHandleDisThread;
	private InputStream mInputStream;	
	private OutputStream mOutputStream;	
	private StringBuffer tempBuffer = new StringBuffer();
    private Handler mStatusHandler;
	private AssetFileDescriptor fd;
	private boolean isLiverPerfusion = false;
	private boolean isPlaying = false;
	private boolean isStop = false;
	private boolean isArteryCnect = false;
	private boolean isVeinCnect = false;
	/**系统通信中断**/
	private boolean isSytstemInterrupt = false;
	//流量状态
	private int arteryFlowStatus = 1;
	private int veinFlowStatus = 1;	
	//压力状态
	private int arteryPreStatus = 1;//默认断连
	private int veinPreStatus = 1;
	
	private boolean isArtBubble = false;
	private boolean isVeinBubble = false;
	
	//泵速报警状态
	private boolean isArtSpeedAlarm = false;
	private boolean isVeinSpeedAlarm = false;
	private String perfusionNum;
	/**瞬时压力**/
	private String artery_Preal = DefValues.DEDAULT_NULL;
	private String vein_Preal = DefValues.DEDAULT_NULL;
	/**瞬时流量**/
	private String artery_Freal = DefValues.DEDAULT_NULL;
	private String vein_Freal = DefValues.DEDAULT_NULL;
	/**平均动脉压**/
	private String artery_PMean = DefValues.DEDAULT_NULL;
	private String vein_PMean = DefValues.DEDAULT_NULL;	
	private String artery_FMean = DefValues.DEDAULT_NULL; //平均流量
	private String vein_FMean = DefValues.DEDAULT_NULL;
	/**收缩压**/
	private String artery_PSyst = DefValues.DEDAULT_NULL;
	private String vein_PSyst = DefValues.DEDAULT_NULL;
	/**舒张压**/
	private String artery_PDias = DefValues.DEDAULT_NULL;
	private String vein_PDias = DefValues.DEDAULT_NULL;
	/**脉率**/
	private String artery_PFreq = DefValues.DEDAULT_NULL;
	private String vein_PFreq = DefValues.DEDAULT_NULL;;
	//泵速
	private String artery_Speed = DefValues.DEDAULT_NULL;	
	private String vein_Speed = DefValues.DEDAULT_NULL;
	private String temp = DefValues.DEDAULT_NULL;	//温度
	private String artery_Bubble = DefValues.DEDAULT_NULL;//气泡
	private String vein_Bubble = DefValues.DEDAULT_NULL;
	private String artery_resistindex = DefValues.DEDAULT_NULL;//阻力指数
	private String vein_resistindex = DefValues.DEDAULT_NULL;

	private int currentMode = 0;
	private int curArtMode = Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE;//0:恒压、1：搏动压
	private int curVeinMode;//2：恒压，3恒流
	/**心跳消息**/
	private long mCurHeartbeat = 0 ;
	private long mArteryLastHeartBeat =0;
	private long mVeinLastHeartBeat = 0;
	
	/*** 泵速报警消息*/
	private long mArteryLastSpeedTime = 0;
	private long mVeinLastSpeedTime = 0;

	private long mVeinLastBubbleTime = 0;
	private int mVeinBubbleCount = 0;
	private long mArteryLastBubbleTime = 0;
	private int mArteryBubbleCount = 0;


	private SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private static final int MODE_SINGLE_LOOP = 0;//循环播放
	private int curPlayAlarmRing = 0;//0:是没有播放
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "--onBind--");
		return mBinder;
	}

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mDatabaseMgr = DatabaseMgr.getInstance(getApplicationContext());
		mApplication = (BioConsoleApplication)getApplication();
		initMediaPlayer();
		openSerialPort();		
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("BioConsoleApplication", "--Service onStartCommand--");
		initSerialPort();
		//启动后发送停止泵的消息
		startInitSpeed();
		Log.i(TAG, "--onStartCommand--Stop System");
		startScheduleThread();		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("BioConsoleApplication", "--BackSerce onDestroy--");	
		closeSerialPort();	
		releasePlayer();
		shutDownThread();
		closeReadThread();
		
		if(null != mHandleDisThread){
			mHandleDisThread.interrupt();
			mHandleDisThread = null;
		}
		
		if(null != mStatusHandler){
			mStatusHandler.removeCallbacksAndMessages(null);
			mStatusHandler = null;
		}
		tempBuffer = null;
	}
	
	private void closeReadThread(){
		if(null != mReadThread){
			mReadThread.interrupt();
			mReadThread = null;			
		}	
		isStop = true;
	}
	
	private void startReadThread(){
		Log.i(TAG, "startReadThread ");
		isStop = false;
		mReadThread = new ReadSerialPortThread();		
		mReadThread.start();
	}
	
	public class BackBinder extends Binder{
		
		public BackService getService(){  
            return BackService.this;  
        } 
		
		/**start play the alarm music**/
		public void startPlay(int position){
			play(position);
		}
		
		/**stop play the alarm music**/
		public void stopPlay(){
			stop();
		}
		
		public void releasePlay(){
			releasePlayer();
		}
		
		public boolean isPlaying(){
			return isPlay();
		}
		
		/**send serial port cmd msg**/
		public void sendSerialCmdMsg(String msg){
			sendSerialCmd(msg);
		}

		public void setCurArtPerfusionMode(int artMode){
			setCurrentArtMode(artMode);
		}
		
		public void setCurVeinPerfusionMode(int veinMode){
			setCurrentVeinMode(veinMode);
		}
		
		public void setCurPerfusionMode(int artMode,int veinMode){
			setCurrentArtMode(artMode);
			setCurrentVeinMode(veinMode);
		}

		public void setPerfusionStatus(boolean isPerfusion){
			isLiverPerfusion = isPerfusion;
		}
		
		
		public void restoreVariable(){
			perfusionNum = "";
		}
		
		public int getCurPlayPosition(){
			return getPlayPosition();
		}
	

	}
		
	/**
	 * initialize the MediaPlayer
	 */
	private void initMediaPlayer(){
		Log.i(TAG, "--initMediaPlayer--");
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mMediaPlayer.start();
			}
		});
		
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(isPlaying){
					switch (currentMode) {
					case MODE_SINGLE_LOOP:
						mMediaPlayer.start();
						break;

					default:
						break;
					}
				}
				
			}
		});
		
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		try {
			fd = getAssets().openFd("medium.mp3");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//启动后停止泵
	private void startInitSpeed(){
		boolean isCrash = PreferenceUtil.getInstance(getApplicationContext()).getBooleanValue(SharedConstants.IS_CRASH, false);
		boolean isFirstZeroPre = PreferenceUtil.getInstance(getApplicationContext())
				.getBooleanValue(SharedConstants.IS_PRESSURE_ZERO_FIRST, true);
		
		boolean isPerfusion = PreferenceUtil.getInstance(getApplicationContext())
				.getBooleanValue(SharedConstants.IS_PERFUSION, false);

		if(isFirstZeroPre || !isPerfusion || !isCrash){
			Log.i("BioConsoleApplication", "--backservice startInitSpeed--");	
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_ARTERY_SPEED, 0);
			PreferenceUtil.getInstance(getApplicationContext())
				.setValueByName(SharedConstants.TARGET_VEIN_SPEED, 0);
			PreferenceUtil.getInstance(getApplicationContext()).
				setValueByName(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, "");
			PreferenceUtil.getInstance(getApplicationContext()).
				setValueByName(SharedConstants.LASTEST_VEIN_TARGET_ORDER, "");
			//sendSerialCmd(SerialPortInstruct.STOP_SYSTEM_ONE);
			//sendSerialCmd(SerialPortInstruct.STOP_SYSTEM_TWO);
		}
	}
	
	/****
	 * play the alarm/alert music
	 * @param currentMusic
	 */
	private void play(int position){

		if(null != mMediaPlayer && mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}else{
			mMediaPlayer.reset();
		}
		try {
			if(null != fd){
				if(position == 1){
					curPlayAlarmRing = 1;
					fd = getAssets().openFd("low.mp3");
				}else if(position == 2){
					curPlayAlarmRing = 2;
					fd = getAssets().openFd("medium.mp3");
				}else if(position == 3){
					curPlayAlarmRing = 3;
					fd = getAssets().openFd("high.mp3");
				}
				mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),fd.getStartOffset());
			}		
			mMediaPlayer.prepareAsync();//prepare fail可能已经同步过了
			isPlaying = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			//mMediaPlayer = null;
			Log.v(TAG, "play Exception " + e);
		} 
		Log.v(TAG, "[Play] Start Preparing at ");
		
	}
	
	/***
	 * 停止播放
	 */
	private void stop(){
		if(null != mMediaPlayer){
			mMediaPlayer.stop();
		}
		isPlaying = false;
		curPlayAlarmRing = 0;
	}
	
	private void releasePlayer(){
		if(null != mMediaPlayer){
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	private boolean isPlay(){
		return isPlaying;
	}
	
	private int getPlayPosition(){
		return curPlayAlarmRing;
	}

	private void setCurrentArtMode(int artMode){
		this.curArtMode = artMode;
	}
	
	private void setCurrentVeinMode(int veinMode){
		this.curVeinMode = veinMode;
	}
	

	
	/**
	 * 发送串口消息
	 * @param mSerialPort 
	 * 			
	 * @param cmd 
	 * 			新消息
	 */
	public void sendSerialCmd(String cmd){
		try{
			Log.i(TAG, "sendSerialCmd----" + cmd);
			StringBuilder builder = new StringBuilder();
			builder.append("*").append(cmd).append("#");
			String strTmp = builder.toString();
			
			byte[] buffer = strTmp.getBytes();
			mOutputStream.write(buffer);
			//mOutputStream.write('\n');
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 打开串口
	 */
	String DEFAULT_PORT = SerialPortConstant.SERIAL_PORT_2;//UART2
	final int BaudRate_breath = 115200;
	public void openSerialPort(){
		mSerialPortFinder = new SerialPortFinder();
		String[] devicesPath = mSerialPortFinder.getAllDevicesPath();
		if(null != devicesPath){
			try {
				for(String path : devicesPath){
					Log.i(TAG, "openSerialPort" + "path " + path);
					if(path.equals(DEFAULT_PORT)){
						mSerialPort_perfusion = new SerialPort(new File(path), BaudRate_breath, 0);
						Log.i(TAG, "openSerialPort" + "mSerialPort_perfusion " + mSerialPort_perfusion);      
					}
					
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.getMessage();
			}
		}
	}
	
	
	
	/**
	 * 初始化串口
	 * */
	public void initSerialPort(){
		mInputStream = mSerialPort_perfusion.getInputStream();
		mOutputStream = mSerialPort_perfusion.getOutputStream();
		startReadThread();
		mHandleDisThread = new HandleDisconnectThread();
		mHandleDisThread.start();
		
	}
	


	/**
	 * 关闭串口
	 * */
	public void closeSerialPort(){
		if(null != mSerialPort_perfusion){
			mSerialPort_perfusion.close();
			mSerialPort_perfusion = null;
		}
		if(mOutputStream != null){
			try {
				mOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mOutputStream = null;
		}
		
		if(mInputStream != null){
			try {
				mInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mInputStream = null;
		}
	}
		
	/**关闭线程**/
	private void shutDownThread(){
		
		if(scheduledThreadPoolExecutor != null &&!scheduledThreadPoolExecutor.isShutdown()){
			scheduledThreadPoolExecutor.shutdownNow();
			scheduledThreadPoolExecutor = null;
		}
	}
	
	/***
	 * 
	 * @author 检查系统链路的线程
	 *
	 */
	private class HandleDisconnectThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Looper.prepare();
			mStatusHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					checkLineStatus();
					sendEmptyMessageDelayed(0,500);
				}
			};
			mStatusHandler.sendEmptyMessage(0);
			Looper.loop();
		}
		
	}
	
	/**
	 * 检测数据链路链接状态
	 * @author 80657
	 *
	 */
	private void checkLineStatus(){
		mCurHeartbeat = new Date(System.currentTimeMillis()).getTime();
		if(isArteryCnect && mCurHeartbeat != 0 
        		&& mArteryLastHeartBeat >= 0 
        		&& mCurHeartbeat - mArteryLastHeartBeat >= DefValues.DEFAULT_TIME_INTERVAL_CONNECTED){
           // Artery heart disconnect 
   
           isArteryCnect = false;
		   Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_CNECT);                   	
           intent.putExtra(SerialMsgConstant.ARTERY_CNECT, "disconnect");  
           insertAlarmMessage(ErrorCode.E_DISCONNECT_ARTERY_MSG,"error");
           Log.i(TAG, "disconnect artery message");
           LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);       
           
        }else if(isVeinCnect && mCurHeartbeat != 0 
        		&& mVeinLastHeartBeat >= 0 
        		&& (mCurHeartbeat - mVeinLastHeartBeat) >= DefValues.DEFAULT_TIME_INTERVAL_CONNECTED){
        	// Vein heart disconnect 
        	isVeinCnect = false;
        	insertAlarmMessage(ErrorCode.E_DISCONNECT_VEIN_MSG,"error");
        	Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_CNECT);                   	
            intent.putExtra(SerialMsgConstant.VEIN_CNECT, "disconnect");               	
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            Log.i(TAG, "disconnect vein message");           
            
        }else if(!isVeinCnect && !isArteryCnect && mCurHeartbeat - mVeinLastHeartBeat >= DefValues.DEFAULT_TIME_SYSTEM_INTERUPT
        		&& mCurHeartbeat - mArteryLastHeartBeat >= DefValues.DEFAULT_TIME_SYSTEM_INTERUPT){
        	//系统通信中断 
        	if(!isSytstemInterrupt){
        		isSytstemInterrupt = true;
        		insertAlarmMessage(ErrorCode.E_INTERRUPT_SYSTEM,"error");
        		Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_SYSTEM_ITERRUPT);                   	
                intent.putExtra(SerialMsgConstant.STSTEM_ITERRUPT, isSytstemInterrupt);    
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        	}          

        	Log.i(TAG, "system interupt　mInputStream" + mInputStream); 
        	
        } else if(isArteryCnect && (mCurHeartbeat - mArteryLastSpeedTime) >= DefValues.DEFAULT_TIME_SPEED_SENSOR){
        	//取消泵速报警
        	if(isArtSpeedAlarm){
        		isArtSpeedAlarm = false;
            	Intent intent = new Intent(BroadcastActions.ACTION_ALARM_PUMP_ONE_SPEED);                   	
                intent.putExtra(SerialMsgConstant.ARTERY_SPEED_ALARM, false);               	
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        	}
        }else if(isVeinCnect && (mCurHeartbeat - mVeinLastSpeedTime) >= DefValues.DEFAULT_TIME_SPEED_SENSOR){
        	//取消泵速报警
        	if(isVeinSpeedAlarm){
        		isVeinSpeedAlarm = false;
            	Intent intent = new Intent(BroadcastActions.ACTION_ALARM_PUMP_TWO_SPEED);                   	
                intent.putExtra(SerialMsgConstant.VEIN_SPEED_ALARM, false);               	
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        	}
        }        
	}
	
	/**
	 * 读取串口数据的线程
	 * @author 80657
	 *
	 */
	private class ReadSerialPortThread extends Thread {
		@Override  
	    public void run() {			
            super.run();  
            
            while (!isStop && !isInterrupted()) {
            	
                int size;  
                try {  
                    if (mInputStream == null){      
                    	Log.i(TAG, "ReadSerialPortThread " + "mInputStream is null");
                    	return;
                    	
                    }
                    int num = 0;
                    num = mInputStream.available();
                    byte[] buffer = new byte[num];	                  
                    size = mInputStream.read(buffer,0,buffer.length);
                   
                    if (size > 0) {	                         	
                        String message = new String(buffer, 0, size);    
                        onHandlerMessage(message);                    
                    }  		                    
                } catch (Exception e) {  
                    e.printStackTrace();  
                    Log.i(TAG, "ReadThread Exception e" +  e);
                    closeReadThread();
                    startReadThread();
                   return;  

                }  
	         }  
	     }  
	}  
	
	private void onHandlerMessage(String message){
		//Log.i(TAG, "onHandlerMessage " + message);
		if(message != null ){
			if(message.length() >= 23){
				String[] msgArray = StringUtil.splitString(message);
				onHandlerSerialMsg(msgArray);					
			}else{
				//拼接不完整的信息				
				tempBuffer.append(message);					
				int startpos = tempBuffer.indexOf("*");
				int endpos = tempBuffer.indexOf("#");
				if(startpos != -1 && endpos  != -1){
					if(startpos <= endpos){
						String newString  = tempBuffer.toString().substring(startpos, endpos);
						tempBuffer.replace(startpos, endpos, "");	
						onHandlerSerialMsg(newString);
						
					}else {
						tempBuffer.replace(0, startpos, "");
						
					}
				}
			}	
		}			
	}	
		
	private void onHandlerSerialMsg(String[] msgArray){
		for(String msg : msgArray){
			onHandlerSerialMsg(msg)	;		
		}
	}
	
	/**处理每一条的数据**/
	private void onHandlerSerialMsg(String msg){			
		if(msg != null && msg.length() == 20){			
			if(msg.startsWith("*pump1_Preal_")&& msg.endsWith("P")){
				if(msg.contains("error1")){
					//disconnect						
					if(arteryPreStatus != 1){
						arteryPreStatus = 1;
						Log.i(TAG, "BackService normal artery pressure error1");
						insertAlarmMessage(ErrorCode.E_DISCONNECT_PRESSURE_ARTERY_MSG,"error");		                    
					}	
					if(arteryPreStatus == 1){
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);                   	
	                    intent.putExtra(SerialMsgConstant.ARTERY_PREAL, "error1");               	
	                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
                    Log.i(TAG, "no receive the artery pressure message");
					
				}else{
					//正常
					arteryPreStatus = 0;
					artery_Preal = StringUtil.getString_Num(msg);					
					if(null != artery_Preal){						
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PREAL);                   	
	                	intent.putExtra(SerialMsgConstant.ARTERY_PREAL, artery_Preal);               	
	                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);               	
					}		
					
				}					
			}else if(msg.startsWith("*pump2_Preal_")&& msg.endsWith("p")){				
				if(msg.contains("error1")){
					//disconnect						
					if(veinPreStatus != 1){
						veinPreStatus = 1;
						Log.i(TAG, "BackService normal vein pressure error1");
						insertAlarmMessage(ErrorCode.E_DISCONNECT_PRESSURE_VEIN_MSG,"error");
						//insertAlarmMessage(MsgConstants.DISCONNECT_PORTAL_VEIN_PRESSURE,"error", 1);		                    
					}	
					if(veinPreStatus == 1){
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);                   	
	                    intent.putExtra(SerialMsgConstant.VEIN_PREAL, "error1");               	
	                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
                    Log.i(TAG, "no receive the vein pressure message");
					
				}else{
					//正常
					veinPreStatus = 0;
					vein_Preal = StringUtil.getString_Num(msg);					
					if(null != vein_Preal){						
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PREAL);                   	
	                	intent.putExtra(SerialMsgConstant.VEIN_PREAL, vein_Preal);               	
	                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);               	
					}		
					
				}
		  }else if(msg.startsWith("*pump1_Pzero")){
				//压力调零
				if(msg.contains("succe")){					
					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_ONE_PREAL);                   	
                	intent.putExtra("pump1_Pzero", "succe");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					
				}else if(msg.contains("erro")){

					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_ONE_PREAL);                   	
                	intent.putExtra("pump1_Pzero", "erro");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump2_Pzero")){
				if(msg.contains("succe")){
					//Log.i(TAG, "---pump2_Pzero succe---");
					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_TWO_PREAL);                   	
                	intent.putExtra("pump2_Pzero", "succe");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

				}else if(msg.contains("erro")){
					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_TWO_PREAL);                   	
                	intent.putExtra("pump2_Pzero", "erro");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump1_Fzero")){
				//肝动脉流量调零
				if(msg.contains("succe")){					
					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_ONE_FREAL);                   	
                	intent.putExtra("pump1_Fzero", "succe");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					
				}else if(msg.contains("erro")){
					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_ONE_FREAL);                   	
                	intent.putExtra("pump1_Fzero", "erro");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump2_Fzero")){
				//门静脉流量调零
				if(msg.contains("succe")){					
					//Log.i(TAG, "---pump2_Fzero succe---");
					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_TWO_FREAL);                   	
                	intent.putExtra("pump1_Fzero", "succe");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					
				}else if(msg.contains("erro")){
					Intent intent = new Intent(BroadcastActions.ACTION_ZERO_PUMP_TWO_FREAL);                   	
                	intent.putExtra("pump2_Fzero", "erro");               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump1_Freal_")&& msg.endsWith("F")){
				if(msg.contains("error1")){
					//disconnect						
					if(arteryFlowStatus != 1){
						arteryFlowStatus = 1;
						Log.i("sensorArt", "BackService normal artery flow error1");
						insertAlarmMessage(ErrorCode.E_DISCONNECT_FLOW_ARTERY_MSG,"error");		                    
					}	
					if(arteryFlowStatus == 1){
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);                   	
	                    intent.putExtra(SerialMsgConstant.ARTERY_FREAL, "error1");               	
	                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
                    Log.i(TAG, "no receive the artery flow message");
					
				}else if(msg.contains("error2")){
					//abnormal											
					if(arteryFlowStatus != 2){						
						arteryFlowStatus = 2;
						Log.i(TAG,  "BackService normal artery flow error2");
						insertAlarmMessage(ErrorCode.E_ABNORMAL_FLOW_ARTERY_MSG,"error");
					}
					if(arteryFlowStatus == 2){
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL); 
	                    intent.putExtra(SerialMsgConstant.ARTERY_FREAL, "error2");               	
	                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
				}else{
					//正常
					arteryFlowStatus = 0;
					artery_Freal = StringUtil.getString_Num(msg);					
					if(null != artery_Freal){						
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FREAL);                   	
	                	intent.putExtra(SerialMsgConstant.ARTERY_FREAL, artery_Freal);               	
	                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);               	
					}		
					
				}				
				
			}else if(msg.startsWith("*pump2_Freal_")&& msg.endsWith("f")){
				//门静脉瞬时流量
				if(msg.contains("error1")){
					if(veinFlowStatus != 1){
						veinFlowStatus = 1;
						insertAlarmMessage(ErrorCode.E_DISCONNECT_FLOW_VEIN_MSG,"error");						
					}
					if(veinFlowStatus == 1){
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);                   	
	                	intent.putExtra(SerialMsgConstant.VEIN_FREAL, "error1");               	
	                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);  
					}
	
				}else if(msg.contains("error2")){
					if(veinFlowStatus != 2){
						veinFlowStatus = 2;
						insertAlarmMessage(ErrorCode.E_ABNORMAL_FLOW_VEIN_MSG,"error");	
					}
					if(veinFlowStatus == 2){
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);                   	
	                	intent.putExtra(SerialMsgConstant.VEIN_FREAL, "error2");               	
	                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent); 
					}

				}else{
					veinFlowStatus = 0;
					vein_Freal = StringUtil.getString_Num(msg);
					if(null != vein_Freal){
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FREAL);                   	
	                	intent.putExtra(SerialMsgConstant.VEIN_FREAL, vein_Freal);               	
	                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);               	
					}	
					
				}					
			}else if(msg.startsWith("*pump1_Sreal_")&& msg.endsWith("S")){
				artery_Speed = StringUtil.getString_Num(msg);
				//转速1
				if(null != artery_Speed){					
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_SREAL);                   	
                	intent.putExtra(SerialMsgConstant.ARTERY_SPEED, artery_Speed);               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
				
			}else if(msg.startsWith("*pump2_Sreal_")&& msg.endsWith("s")){
				vein_Speed = StringUtil.getString_Num(msg);
				//转速2
				if(null != vein_Speed){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_SREAL);                   	
                	intent.putExtra(SerialMsgConstant.VEIN_SPEED, vein_Speed);                	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump1_Treal_")&& msg.endsWith("T")){
				temp = StringUtil.getString_Num(msg);
				if(null != temp){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_TREAL);                   	
                	intent.putExtra(SerialMsgConstant.ARTERY_TEMP, temp);               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump1_Qreal_")&& msg.endsWith("Q")){
				Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_QREAL);
				if(msg.contains("error")){
					//肝动脉无气泡时,若原先有气泡，现在恢复
					if(isArtBubble){
						isArtBubble = false;
						Log.i(TAG, "artery bubble to no bubble");
						intent.putExtra(SerialMsgConstant.ARTERY_BUBBLE, DefValues.DEDAULT_NULL);						
						intent.putExtra("pump1_stop", false);
						mArteryLastBubbleTime = new Date(System.currentTimeMillis()).getTime();
					}
				}else{
					//有气泡
					artery_Bubble = StringUtil.getString_Num(msg);						
					if(null != artery_Bubble){
						float Qreal = StringUtil.convertToFloat(artery_Bubble, 0.0f);
						//气泡大于0.8才上报,连续有气泡大于2秒
						if(!StringUtil.isAllZero(artery_Bubble) && (Qreal > 0.8)
						&& (mCurHeartbeat - mArteryLastBubbleTime > 2000)){
							intent.putExtra(SerialMsgConstant.ARTERY_BUBBLE, artery_Bubble);
							intent.putExtra("pump1_stop", false);
							Log.i(TAG, "attery bubble detected--" + msg);
							if(!isArtBubble){
								isArtBubble = true;
								insertAlarmMessage(ErrorCode.E_BUBBLE_ARTERY_ALERT_MSG,"error");								
							}
							mArteryBubbleCount++;
							//连续大于20次上报，大于2秒才停泵
							if((mCurHeartbeat - mArteryLastBubbleTime > DefValues.DEFAULT_TIME_BUBBLE_DURATION )&& mArteryBubbleCount > 20){							
								Log.i(TAG, "artery bubble stop mVeinBubbleCount--" + mVeinBubbleCount);
								mArteryBubbleCount = 0;
								intent.putExtra("pump1_stop", true);
							}
						}else{
							//小于0.8不上报，气泡恢复
							if(isArtBubble &&(Qreal <0.8)){
								isArtBubble = false;
							}
							intent.putExtra(SerialMsgConstant.ARTERY_BUBBLE, DefValues.DEDAULT_NULL);						
							intent.putExtra("pump1_stop", false);
							mArteryLastBubbleTime = new Date(System.currentTimeMillis()).getTime();
						}
					}
				}
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			
			}else if(msg.startsWith("*pump2_Qreal_")&& msg.endsWith("q")){
				//门静脉气泡
				Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_QREAL); 
				if(msg.contains("error")){
					//门静脉没有气泡
					if(isVeinBubble){
						isVeinBubble = false;
						Log.i(TAG, "artery bubble to no bubble");
						intent.putExtra(SerialMsgConstant.VEIN_BUBBLE, DefValues.DEDAULT_NULL);						
						intent.putExtra("pump1_stop", false);
						
					}
					mVeinLastBubbleTime = new Date(System.currentTimeMillis()).getTime();
				}else{
					//门静脉有气泡
					vein_Bubble = StringUtil.getString_Num(msg);
					if(null != vein_Bubble){
						float Qreal = StringUtil.convertToFloat(vein_Bubble, 0.0f);
						if(!StringUtil.isAllZero(vein_Bubble) && Qreal > 0.8
								&& mCurHeartbeat - mVeinLastBubbleTime > DefValues.DEFAULT_TIME_BUBBLE_DURATION){
							if(!isVeinBubble){
								isVeinBubble = true;
								insertAlarmMessage(ErrorCode.E_BUBBLE_VEIN_ALERT_MSG,"error");
							}
							intent.putExtra(SerialMsgConstant.VEIN_BUBBLE, vein_Bubble);
							intent.putExtra("pump2_stop", false);
							mVeinBubbleCount++ ;
							if(mCurHeartbeat - mVeinLastBubbleTime > DefValues.DEFAULT_TIME_BUBBLE_DURATION && mVeinBubbleCount > 20){							
								Log.i(TAG, "vein bubble stop mVeinBubbleCount--" + mVeinBubbleCount);
								mVeinBubbleCount = 0;
								intent.putExtra("pump2_stop", true);
							}
						}else{
							//小于0.8//气泡恢复
							if(isVeinBubble){
								isVeinBubble = false;
							}

							intent.putExtra(SerialMsgConstant.VEIN_BUBBLE, DefValues.DEDAULT_NULL);
							intent.putExtra("pump2_stop", false);
							mVeinLastBubbleTime = new Date(System.currentTimeMillis()).getTime();
						}
					}				
				}
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);	
				
			}else if(msg.startsWith("*pump1_Pfreq_")&&msg.endsWith("R")){
				//左肾动脉脉率
				artery_PFreq = StringUtil.getString_Num(msg);
				if(null != artery_PFreq){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PFREQ);                   	
                	intent.putExtra(SerialMsgConstant.ARTERY_PFREQ, artery_PFreq);               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump2_Pfreq_")&&msg.endsWith("r")){
				//右肾动脉脉率
				vein_PFreq = StringUtil.getString_Num(msg);
				if(null != vein_PFreq){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PFREQ);                   	
                	intent.putExtra(SerialMsgConstant.VEIN_PFREQ, vein_PFreq);               	
                	LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump1_PDias_")&&msg.endsWith("D")){
				//肝动脉舒张压
				artery_PDias = StringUtil.getString_Num(msg);
				if(null != artery_PDias){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PDIAS);
					intent.putExtra(SerialMsgConstant.ARTERY_PDIAS, artery_PDias);               	
	                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
				
			}else if(msg.startsWith("*pump2_PDias_")&&msg.endsWith("d")){
				//2舒张压
				vein_PDias = StringUtil.getString_Num(msg);
				if(null != vein_PDias){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PDIAS);
					intent.putExtra(SerialMsgConstant.VEIN_PDIAS, vein_PDias);               	
	                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
				
			}else if(msg.startsWith("*pump1_PSyst_")&&msg.endsWith("Y")){
				//肝动脉收缩压
				artery_PSyst = StringUtil.getString_Num(msg);
				if(null != artery_PSyst){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PSYST);
					intent.putExtra(SerialMsgConstant.ARTERY_PSYST, artery_PSyst);               	
	                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump2_PSyst_")&&msg.endsWith("y")){
				//2收缩压
				vein_PSyst = StringUtil.getString_Num(msg);
				if(null != vein_PSyst){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PSYST);
					intent.putExtra(SerialMsgConstant.VEIN_PSYST, vein_PSyst);               	
	                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump1_PMean_") && msg.endsWith("M")){
				//肝动脉平均压
				artery_PMean = StringUtil.getString_Num(msg);
				if(null != artery_PMean){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_PMEAN);
					intent.putExtra(SerialMsgConstant.ARTERY_PMEAN, artery_PMean);               	
	                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
			}else if(msg.startsWith("*pump2_PMean_") && msg.endsWith("m")){
				//门静脉平均压				
				vein_PMean = StringUtil.getString_Num(msg);
				if(null != vein_PMean){			
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_PMEAN);    
					intent.putExtra(SerialMsgConstant.VEIN_PMEAN, vein_PMean);   
					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
				
			}else if(msg.startsWith("*pump1_FMean_") && msg.endsWith("G")){
				//肝动脉平均流量				
				if(msg.contains("error1")){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FMEAN);
					intent.putExtra(SerialMsgConstant.ARTERY_FMEAN, "error1");  
					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}else if(msg.contains("error2")){	
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FMEAN);
					intent.putExtra(SerialMsgConstant.ARTERY_FMEAN, "error2");    
					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}else{
					artery_FMean  = StringUtil.getString_Num(msg);
					if(null != artery_FMean){	
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_FMEAN);
						intent.putExtra(SerialMsgConstant.ARTERY_FMEAN, artery_FMean);     
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
				}
				
			}else if(msg.startsWith("*pump2_FMean_") && msg.endsWith("g")){
				//门静脉平均流量
				
				if(msg.contains("error1")){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FMEAN);
					intent.putExtra(SerialMsgConstant.VEIN_FMEAN, "error1"); 
					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}else if(msg.contains("error2")){
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FMEAN);
					intent.putExtra(SerialMsgConstant.VEIN_FMEAN, "error2"); 
					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}else{
					vein_FMean  = StringUtil.getString_Num(msg);
					if(null != vein_FMean){		
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_FMEAN);
						intent.putExtra(SerialMsgConstant.VEIN_FMEAN, vein_FMean);
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
				}				
								
			}else if(msg.startsWith("*pump1_Cnect_")){

				if(!isArteryCnect){
					isArteryCnect = true;	
					if(isSytstemInterrupt){
						isSytstemInterrupt = false;
					}
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_ONE_CNECT);                   	
                    intent.putExtra(SerialMsgConstant.ARTERY_CNECT, "connect");  
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
				mArteryLastHeartBeat = new Date(System.currentTimeMillis()).getTime();
			}else if(msg.startsWith("*pump2_Cnect_")) {

				if(!isVeinCnect){
					isVeinCnect = true;
					if(isSytstemInterrupt){
						isSytstemInterrupt = false;
					}
					Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_PUMP_TWO_CNECT);                   	
                    intent.putExtra(SerialMsgConstant.VEIN_CNECT, "connect");   
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
				mVeinLastHeartBeat = new Date(System.currentTimeMillis()).getTime();
				
			}else if(msg.startsWith("*pump1_Salam_")){
				//泵一泵速报警
				if(!isArtSpeedAlarm){
					isArtSpeedAlarm = true;
					Log.i(TAG, "---isArtSpeedAlarm---"); 
					Intent intent = new Intent(BroadcastActions.ACTION_ALARM_PUMP_ONE_SPEED);  
					intent.putExtra(SerialMsgConstant.ARTERY_SPEED_ALARM, isArtSpeedAlarm);  
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}
				
                mArteryLastSpeedTime = new Date(System.currentTimeMillis()).getTime();
				
			}else if(msg.startsWith("*pump2_Salam_")){
				//泵2泵速报警
				if(!isVeinSpeedAlarm){
					isVeinSpeedAlarm = true;
					Intent intent = new Intent(BroadcastActions.ACTION_ALARM_PUMP_TWO_SPEED);  
					intent.putExtra(SerialMsgConstant.VEIN_SPEED_ALARM, isVeinSpeedAlarm);  
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}                   
                mVeinLastSpeedTime = new Date(System.currentTimeMillis()).getTime();
				
			}else if(msg.startsWith("*pump1_Ptarg_") && msg.endsWith("A")){
				//左肾动脉/肝动脉脉恒压模式 恒定目标压力上报
				if(isLiverPerfusion){					
					if(curArtMode == Constants.LEFT_ARTERY_CONST_PRESSURE_MODE){
						//肝动脉是恒压
						String num = StringUtil.getString_Num(msg);
						if(null != num){							
							float artTarPreCur = StringUtil.convertToFloat(num, 0.0f);
							float ArtTarPreLast = PreferenceUtil.getInstance(getApplicationContext())
									.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_PRESSURE, 0.0f);
						
							if(ArtTarPreLast != artTarPreCur){
								Log.i(TAG, "---artTarPreCur---" + artTarPreCur + " ArtTarPreLast---" + ArtTarPreLast);
								String target_artery_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_ONE, (int)(ArtTarPreLast*10),"1");
								sendSerialCmd(target_artery_msg);
							}
							
						}
						
					}
				}					 
			}else if(msg.startsWith("*pump2_Ptarg_") && msg.endsWith("a")){
				//右肾动脉/门静脉恒压模式 恒定目标压力上报
				if(isLiverPerfusion){
					if(curVeinMode == Constants.RIGHT_ARTERY_CONST_PRESSURE_MODE){
						float VeinTarPreLast = PreferenceUtil.getInstance(getApplicationContext())
								.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_PRESSURE, 0.0f);
						
						String target = StringUtil.getString_Num(msg);
						if(null != target){
							float veinTarPreCur = StringUtil.convertToFloat(target, 0.0f);
							if(VeinTarPreLast != veinTarPreCur){
								String target_vein_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_PRESSURE_TWO, (int)(VeinTarPreLast*10),"1");
								Log.i("vvv", "---veinTarPreCur---" + veinTarPreCur + " VeinTarPreLast---" + VeinTarPreLast);
								sendSerialCmd(target_vein_msg);
							}
						}
					}
					
				}
				
			}else if(msg.startsWith("*pump1_Ftarg_" )&& msg.endsWith("H")){
				//左肾动脉/肝动脉(恒流模式)目标流量上报
				if(isLiverPerfusion){
					if(curArtMode == Constants.LEFT_ARTERY_CONST_FLOW_MODE){
						
						float ArtTarFlowLast = PreferenceUtil.getInstance(getApplicationContext())
								.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_CONST_FLOW, 0.0f);
						String target = StringUtil.getString_Num(msg);
						if(null != target){
							float artTarFlowCur = StringUtil.convertToFloat(target, 0.0f);							
							if(ArtTarFlowLast != artTarFlowCur){
								String target_art_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_FLOW_ONE, (int)(ArtTarFlowLast),"0");
								Log.i(TAG, "---ArtTarFlowLast---" + ArtTarFlowLast + " artTarFlowCur---" + artTarFlowCur);
								sendSerialCmd(target_art_msg);
							}
						}
					}
				}
			}else if(msg.startsWith("*pump2_Ftarg_" )&& msg.endsWith("h")){					
				//右肾动脉/门静脉是恒流模式,上报的恒定目标流量
				if(isLiverPerfusion){
					if(curVeinMode == Constants.RIGHT_ARTERY_CONST_FLOW_MODE){
						float VeinTarFlowLast = PreferenceUtil.getInstance(getApplicationContext())
								.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_CONST_FLOW, 0.0f);
						String target = StringUtil.getString_Num(msg);
						if(null != target){
							float veinTarFlowCur = StringUtil.convertToFloat(target, 0.0f);
							
							if(VeinTarFlowLast != veinTarFlowCur){
								String target_vein_msg = StringUtil.appendMsg(SerialPortInstruct.SET_TARGET_FLOW_TWO, (int)(VeinTarFlowLast),"0");
								Log.i(TAG, "---VeinTarFlowLast---" + VeinTarFlowLast + " veinTarFlowCur---" + veinTarFlowCur);
								sendSerialCmd(target_vein_msg);
							}
						}
					}
				}					
			}else if(msg.startsWith("*pump1_PtarB_") && msg.endsWith("A")){
				//左肾动脉/肝动脉搏动模式 ,上报搏动目标压力
				if(isLiverPerfusion){
					if(curArtMode == Constants.LEFT_ARTERY_PULSE_PRESSURE_MODE){
						float ArtTarMaxPre = 0.0f;
						float ArtTarMinPre = 0.0f;
						float ArtTarPreMinLast = PreferenceUtil.getInstance(getApplicationContext())
								.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MIN, 0.0f);
						float ArtTarPreMaxLast = PreferenceUtil.getInstance(getApplicationContext())
								.getFloatValue(SharedConstants.TARGET_LEFT_KIDNED_PRESSURE_MAX, 0.0f);
						String num = getNumOfMsg(msg);
						int length = num.length();
						String max = num.substring(0, 3);
						String min = num.substring(3, length-1);

						if(max.startsWith("0")){
							ArtTarMaxPre = StringUtil.convertToFloat(max.substring(1, max.length()), 0.0f);
						} else{
							ArtTarMaxPre = StringUtil.convertToFloat(max, 0.0f);
						}
						if(min.startsWith("0")){
							ArtTarMinPre = StringUtil.convertToFloat(min.substring(1, min.length()), 0.0f);
						} else{
							ArtTarMinPre = StringUtil.convertToFloat(min, 0.0f);
						}
/*						Log.i("vvv", "ArtTarPreMaxLast----" + ArtTarPreMaxLast);
						Log.i("vvv", "ArtTarMaxPre----" + ArtTarMaxPre);
						Log.i("vvv", "ArtTarMinPre----" + ArtTarMinPre);
						Log.i("vvv", "ArtTarPreMinLast----" + ArtTarPreMinLast);*/
						if(ArtTarPreMaxLast != ArtTarMaxPre || ArtTarPreMinLast != ArtTarMinPre){
							String target_artery_msg = StringUtil.appendMinMaxPre(SerialPortInstruct.SET_TARGET_PULSE_PRESSURE_ONE,(int)ArtTarPreMinLast, (int)ArtTarPreMaxLast);
							Log.i("vvv", "target_artery_msg----" + target_artery_msg); 
							sendSerialCmd(target_artery_msg);
						}
					}
					
				}
				
			}else if(msg.startsWith("*pump2_PtarB_") && msg.endsWith("a")){
				//右左肾动脉/门静脉搏动模式 ,上报搏动目标压力
				if(isLiverPerfusion){
					if(curVeinMode == Constants.RIGHT_ARTERY_PULSE_PRESSURE_MODE){
						float VeinTarMaxPre = 0.0f;
						float VeinTarMinPre = 0.0f;
						float VeinTarPreMinLast = PreferenceUtil.getInstance(getApplicationContext())
								.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MIN, 0.0f);
						float VeinTarPreMaxLast = PreferenceUtil.getInstance(getApplicationContext())
								.getFloatValue(SharedConstants.TARGET_RIGHT_KIDNED_PRESSURE_MAX, 0.0f);
						String num = getNumOfMsg(msg);
						int length = num.length();
						String max = num.substring(0, 3);
						String min = num.substring(3, length-1);
						if(max.startsWith("0")){
							VeinTarMaxPre = StringUtil.convertToFloat(max.substring(1, max.length()), 0.0f);
						} else{
							VeinTarMaxPre = StringUtil.convertToFloat(max, 0.0f);
						}
						if(min.startsWith("0")){
							VeinTarMinPre = StringUtil.convertToFloat(min.substring(1, min.length()), 0.0f);
						} else{
							VeinTarMinPre = StringUtil.convertToFloat(min, 0.0f);
						}
						if(VeinTarPreMaxLast != VeinTarMaxPre || VeinTarPreMinLast != VeinTarMinPre){
							String target_vein_msg = StringUtil.appendMinMaxPre(SerialPortInstruct.SET_TARGET_PULSE_PRESSURE_TWO,
									(int)VeinTarPreMinLast, (int)VeinTarPreMaxLast);
							Log.i("vvv", "target_vein_msg----" + target_vein_msg); 
							sendSerialCmd(target_vein_msg);
						}
					}
					
				}
				
			}else if(msg.startsWith("*pump1_Starg_") && msg.endsWith("B")){
				//目标泵速
				String tarArtSpeed = StringUtil.getString_Num(msg);					
				String lastOrder = PreferenceUtil.getInstance(getApplicationContext()).getStringValue(SharedConstants.LASTEST_ARTERY_TARGET_ORDER, "");
				if(!lastOrder.equals("")&& !lastOrder.equals(SerialPortInstruct.STOP_SYSTEM_ONE)){					
					//若是最后一条目标是停泵则
					if(lastOrder.startsWith("pump1_Ptarg")||lastOrder.startsWith("pump1_PtarB")|| lastOrder.startsWith("pump1_Ftarg")){
						sendSerialCmd(lastOrder);								
					}else if(lastOrder.startsWith("pump1_Starg")){
						int artTarSpeedLast = PreferenceUtil.getInstance(getApplicationContext())
								.getIntValue(SharedConstants.TARGET_ARTERY_SPEED, 0);
						int artTarSpeedCur = StringUtil.convertToInt(tarArtSpeed, 0);
						if(artTarSpeedLast > 0 && 0 == artTarSpeedCur){
							String art_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_ONE, artTarSpeedLast,"0");	
							Log.i(TAG, "---artTarSpeedCur---" + artTarSpeedCur + " artTarSpeedLast---" + artTarSpeedLast);
							sendSerialCmd(art_speed_msg);
						}
					}											
				}
			}else if(msg.startsWith("*pump2_Starg_") && msg.endsWith("b")){
				//门静脉目标泵速				
				String targetSpeed = StringUtil.getString_Num(msg);				
				String lastOrder = PreferenceUtil.getInstance(getApplicationContext()).getStringValue(SharedConstants.LASTEST_VEIN_TARGET_ORDER, "");
				if(!lastOrder.equals("") && !lastOrder.equals(SerialPortInstruct.STOP_SYSTEM_TWO)){
					if(lastOrder.startsWith("pump2_Ptarg") ||lastOrder.startsWith("pump2_PtarB")|| lastOrder.startsWith("pump2_Ftarg")){
						sendSerialCmd(lastOrder);
					}else if(lastOrder.startsWith("pump2_Starg")){
						int VeinTarSpeedLast = PreferenceUtil.getInstance(getApplicationContext())
								.getIntValue(SharedConstants.TARGET_VEIN_SPEED, 0);						
						if(null != targetSpeed){
							int VeinTarSpeedCur = StringUtil.convertToInt(targetSpeed, 0);							
							if(VeinTarSpeedLast > 0 && 0 == VeinTarSpeedCur){
								String vein_speed_msg = StringUtil.appendMsg(SerialPortInstruct.SET_PUMP_SPEED_TWO, VeinTarSpeedLast,"0");	
								Log.i(TAG, "---VeinTarSpeedCur---" + VeinTarSpeedCur + " VeinTarSpeedLast---" + VeinTarSpeedLast);
								sendSerialCmd(vein_speed_msg);
							}
						}
					}
				}
									
			}
			else if(msg.startsWith("*pump1_Ttarg_") && msg.endsWith("I")){
				 String mTarTemp = StringUtil.getString_Num(msg);
				 Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_TARGET_TEMP);
				 intent.putExtra(SerialMsgConstant.TARGET_TEMP, mTarTemp);            	
	             LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}else if(msg.startsWith("*pump1_Alarm_") && msg.endsWith("Z")){
				if(msg.contains("err00")){
					
					if(curPlayAlarmRing != 0){
						if(curPlayAlarmRing == 1){
							sendSerialCmd(SerialPortInstruct.SET_LOW_PRIORITY_ALARM);
						}else if(curPlayAlarmRing == 2){
							sendSerialCmd(SerialPortInstruct.SET_MEDIUM_PRIORITY_ALARM);
						}else if(curPlayAlarmRing == 3){
							sendSerialCmd(SerialPortInstruct.SET_HIGH_PRIORITY_ALARM);
						}
					}
				}else if(msg.contains("err01")){
					
					if(curPlayAlarmRing != 1){
						if(curPlayAlarmRing == 0){
							sendSerialCmd(SerialPortInstruct.SET_NORMAL_PRIORITY_ALARM);
						}else if(curPlayAlarmRing == 2){
							sendSerialCmd(SerialPortInstruct.SET_MEDIUM_PRIORITY_ALARM);
						}else if(curPlayAlarmRing == 3){
							sendSerialCmd(SerialPortInstruct.SET_HIGH_PRIORITY_ALARM);
						}
					}
				}else if(msg.contains("err02")){
					
					if(curPlayAlarmRing != 2){
						if(curPlayAlarmRing == 0){
							sendSerialCmd(SerialPortInstruct.SET_NORMAL_PRIORITY_ALARM);
						}else if(curPlayAlarmRing == 1){
							sendSerialCmd(SerialPortInstruct.SET_LOW_PRIORITY_ALARM);
						}else if(curPlayAlarmRing == 3){
							sendSerialCmd(SerialPortInstruct.SET_HIGH_PRIORITY_ALARM);
						}
					}
				}else if(msg.contains("err03")){
					
					if(curPlayAlarmRing != 3){
						if(curPlayAlarmRing == 0){
							sendSerialCmd(SerialPortInstruct.SET_NORMAL_PRIORITY_ALARM);
						}else if(curPlayAlarmRing == 1){
							sendSerialCmd(SerialPortInstruct.SET_LOW_PRIORITY_ALARM);
						}else {
							sendSerialCmd(SerialPortInstruct.SET_MEDIUM_PRIORITY_ALARM);
						}

					}
				}
				
			}else if(msg.startsWith("*pump1_Batty_") && msg.endsWith("U")){
				//电池电量
				String battery = StringUtil.getAllNumFromMsg(msg);
				
				if(battery != null && !battery.equals("")){
					if(battery.length()== 5){
						//前三位是电压
						String battery_voltage = battery.substring(0, 3);
						String battery_level = battery.substring(3, 5);
						//后两位是电池电量
						if(battery_level.startsWith("0")&&battery_level.length()==2){
							battery_level = battery_level.substring(1);
						}
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_BATTERY_STATE);
						intent.putExtra(SerialMsgConstant.BATTERY_VOLTAGE, battery_voltage);
						intent.putExtra(SerialMsgConstant.BATTERY_LEVEL, StringUtil.convertToInt(battery_level, 0));
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
				}
			}else if(msg.startsWith("*pump1_State_")&& msg.endsWith("V")){
				//仪器状态
				String state = StringUtil.getAllNumFromMsg(msg);
				if(state != null && !state.equals("")){
					if(state.length() == 5){
						int liquid_level_state = StringUtil.convertToInt(state.substring(0,1), 0);//液位状态
						int battery_state = StringUtil.convertToInt(state.substring(1, 2),1);//电池状态,0：异常，1正常
						int charging_state = StringUtil.convertToInt(state.substring(2, 3),0);//充电状态
						int power_state = StringUtil.convertToInt(state.substring(3, 4),0);//是否使用电源
						int emergency_stop_switch = StringUtil.convertToInt(state.substring(4, 5),0);//急停开关
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_DEVICE_STATE);
						intent.putExtra(SerialMsgConstant.LIQUID_LEVEL, liquid_level_state);
						intent.putExtra(SerialMsgConstant.BATTERY_STATE, battery_state);
						intent.putExtra(SerialMsgConstant.CHARGING_STATE, charging_state);
						intent.putExtra(SerialMsgConstant.POWER_STATE, power_state);
						intent.putExtra(SerialMsgConstant.EMERGENCY_STOP_SWITCH, emergency_stop_switch);
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
						
					}
				}

			}else if(msg.startsWith("*pump1_HeFan_") & msg.endsWith("W")){
				//pump1_HeFan_001230W
				//散热状态
				String state = StringUtil.getAllNumFromMsg(msg);
				if(state != null && !state.equals("")){
					if(state.length() == 5){
						
						int android_heatdiss = StringUtil.convertToInt(state.substring(2, 3),1);//安卓散热
						int power_heatdiss = StringUtil.convertToInt(state.substring(3, 4),1);//电源散热
						int semi_heatdiss = StringUtil.convertToInt(state.substring(4, 5),1);//半导体
						Intent intent = new Intent(BroadcastActions.ACTION_RECEIVE_HEATDISS_STATE);
						intent.putExtra(SerialMsgConstant.ANDROID_HEATDISS, android_heatdiss);
						intent.putExtra(SerialMsgConstant.POWER_HEATDISS, power_heatdiss);
						intent.putExtra(SerialMsgConstant.SEMI_HEATDISS, semi_heatdiss);
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);						
					}
				}
			}

		}
			
		
	}

	private void startScheduleThread(){
	
		/**每60秒定时存储
		 ***/
		scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				saveSerialMsg();
				
			}
		}, 0, 60, TimeUnit.SECONDS);
		
//		/** 保存胆汁数***/
//		mScheduledThreadPool.scheduleAtFixedRate(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub		
//				try {
//					saveBileCount();
//				} catch (Exception e) {
//					// TODO: handle exception
//					Log.i(TAG, "saveBileCount Exception" + e.getMessage());
//				}
//				
//			}
//		}, 0, 4, TimeUnit.MINUTES);
		
	}
		
	

	
	/***插入报警信息***/
	private void insertAlarmMessage(int error_code ,String msgType){
        String time = sdf.format(new Date(System.currentTimeMillis()));
		
		String perfusion_Type = PreferenceUtil.getInstance(getApplicationContext())
				.getStringValue(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
		if(perfusion_Type.equals(Constants.LIVER_PERFUSION_STYSTEM)){
			String liverNumber = PreferenceUtil.getInstance(getApplicationContext())
					.getStringValue(SharedConstants.LIVER_NUMBER, "");
			if(!liverNumber.equals("")){
				mDatabaseMgr.insertAlarmMsg(liverNumber, (error_code+"") ,msgType ,time);	
			}
		}else if(perfusion_Type.equals(Constants.KIDNEY_PERFUSION_STYSTEM)){
			String kidneyNumber = PreferenceUtil.getInstance(getApplicationContext())
					.getStringValue(SharedConstants.KIDNEY_NUM, "");
			if(!kidneyNumber.equals("")){
				mDatabaseMgr.insertKidneyAlarmMsg(kidneyNumber, (error_code+"") ,msgType ,time);	
			}
		}
							
	}
	
	/**定时解析定时存储**/
	private void saveSerialMsg(){		
		//在灌注的状态下心跳消息没有断连的情况下进行存储
		try {
			boolean isPerfusion = PreferenceUtil.getInstance(getApplicationContext())
					.getBooleanValue(SharedConstants.IS_PERFUSION, false);
			String perfusion_Type = PreferenceUtil.getInstance(getApplicationContext())
					.getStringValue(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
			if(isPerfusion){	
				if(perfusion_Type.equals(Constants.LIVER_PERFUSION_STYSTEM)){
					perfusionNum = PreferenceUtil.getInstance(getApplicationContext())
							.getStringValue(SharedConstants.LIVER_NUMBER, BioConsoleApplication.mLiverNum);		

					Log.i(TAG, "---liverNum---" + perfusionNum );
					SerialMessage serialMessage = new SerialMessage();
					if(!StringUtil.isEmpty(perfusionNum) && artery_PSyst != null && null != artery_PMean && null != artery_PDias 
							&& null != artery_PFreq && null != artery_Speed && null != artery_FMean
							&& null != vein_Speed && null != vein_FMean && null != vein_PMean){
						serialMessage.setMsgTime(DateFormatUtil.getSysFullTime());
						serialMessage.setLiverNum(perfusionNum);
						serialMessage.setTemp(temp);
						serialMessage.setArtSystPre(artery_PSyst);
						serialMessage.setArtMeanPre(artery_PMean);
						serialMessage.setArtDiasPre(artery_PDias);
						serialMessage.setArtFreqPre(artery_PFreq);
						serialMessage.setArtSpeed(artery_Speed);
						serialMessage.setArtFreal(artery_FMean);			
						serialMessage.setVeinFreal(vein_FMean);
						serialMessage.setVeinSpeed(vein_Speed);
						serialMessage.setVeinPreal(vein_PMean);
						//肝动脉平均压/肝动脉平均流量
						artery_resistindex = getResistIndex(artery_PMean,artery_FMean);
						vein_resistindex = getResistIndex(vein_PMean,vein_FMean);
						
						serialMessage.setArtResistIndex(artery_resistindex);
						serialMessage.setVeinResistIndex(vein_resistindex);
						
						String artery_ehbf = convertFMeantoHundredGrams(artery_FMean);
						serialMessage.setArtFlowEHBF(artery_ehbf);
						
						String vein_ehbf = convertFMeantoHundredGrams(vein_FMean);
						serialMessage.setVeinFlowEHBF(vein_ehbf);
										
						long result = mDatabaseMgr.saveSerialMessage(serialMessage);
						if(result == -1){
							
						}
						
					}else{
						Log.i(TAG,"saveSerialMessage----" + DateFormatUtil.getSysFullTime());
					}		
				}else if(perfusion_Type.equals(Constants.KIDNEY_PERFUSION_STYSTEM)){
					perfusionNum = PreferenceUtil.getInstance(getApplicationContext())
							.getStringValue(SharedConstants.KIDNEY_NUM, BioConsoleApplication.mLiverNum);		
	
					Log.i(TAG, "---liverNum---" + perfusionNum );
					KidneyInfoBean kidneyInfoBean = new KidneyInfoBean();
					if(!StringUtil.isEmpty(perfusionNum) && artery_PSyst != null && null != artery_PMean && null != artery_PDias 
							&& null != artery_PFreq && null != artery_Speed && null != artery_FMean
							&& null != vein_Speed && null != vein_FMean && null != vein_PMean){
						kidneyInfoBean.setMsgTime(DateFormatUtil.getSysFullTime());
						kidneyInfoBean.setKidneyNum(perfusionNum);
						kidneyInfoBean.setTemp(temp);
						kidneyInfoBean.setLeftKidneyArtSystPre(artery_PSyst);
						kidneyInfoBean.setLeftKidneyArtMeanPre(artery_PMean);
						kidneyInfoBean.setLeftKidneyArtDiasPre(artery_PDias);
						kidneyInfoBean.setLeftKidneyArtFreqPre(artery_PFreq);		
						kidneyInfoBean.setLeftKidneyArtSpeed(artery_Speed);						
						kidneyInfoBean.setLeftKidneyArtFreal(artery_FMean);	
						
						kidneyInfoBean.setRightKidneyArtFreal(vein_FMean);
						kidneyInfoBean.setRightKidneyArtFreqPre(vein_PFreq);
						kidneyInfoBean.setRightKidneyArtDiasPre(vein_PDias);
						kidneyInfoBean.setRightKidneyArtSystPre(vein_PSyst);
						kidneyInfoBean.setRightKidneyArtSpeed(vein_Speed);
						kidneyInfoBean.setRightKidneyArtPmean(vein_PMean);
						//肝动脉平均压/肝动脉平均流量
						artery_resistindex = getResistIndex(artery_PMean,artery_FMean);
						vein_resistindex = getResistIndex(vein_PMean,vein_FMean);
						
						kidneyInfoBean.setLeftKidneyArtResistIndex(artery_resistindex);
						kidneyInfoBean.setRightKidneyArtResistIndex(vein_resistindex);
	
										
						long result = mDatabaseMgr.insertKidneyPerfusionInfo(kidneyInfoBean);
						Log.i(TAG,"insertKidneyPerfusionInfo----result" + result);
						if(result == -1){
							
						}
						
					}else{
						Log.i(TAG,"insertKidneyPerfusionInfo----" + DateFormatUtil.getSysFullTime());
					}	
				}
				
			}				
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(TAG, "Exception" + e.getMessage());
		}
		
	}	
	
	private String getResistIndex(String mean_pre,String mean_flow){
		String resistIndex = "∞";
		float meanPre = StringUtil.convertToFloat(mean_pre, 0.0f);
		float meanFlow = StringUtil.convertToFloat(mean_flow, 0.0f);		
		if(meanPre != 0.0f){
			if(meanFlow > 0.0f){
				if(meanPre > 0.0f){
					float resist = (float)meanPre/meanFlow;
					DecimalFormat df = new DecimalFormat("0.00");
					resistIndex = df.format(resist);
				}else{
					resistIndex = "0";
				}
				
			}else{
				resistIndex = "∞";
			}
		}else{
			resistIndex = "0";
		}
		return resistIndex;
	}
	
	

	private String getNumOfMsg(String input){
		String result = null;	
		if(null != input && !input.equals("")){
			input = input.substring(13);
			String regEx="[^-0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(input);
			result = m.replaceAll("");
		}
		return result;
	}
	
	/**
	 * 
	 * EHBF
	 * convert ml/min to ml/min/100g**/
	private String convertFMeantoHundredGrams (String mean_flow){
		float fmean_float = StringUtil.convertToFloat(mean_flow, 0.0f);
		int liver_weight = PreferenceUtil.getInstance(getApplicationContext()).getIntValue(SharedConstants.LIVER_WEIGHT, 0);
		if(liver_weight>0){
			float num = (float)fmean_float * 100/liver_weight;
			DecimalFormat df = new DecimalFormat("0.00");
			return df.format(num);
		}else{
			return "--";
		}

	}
}
