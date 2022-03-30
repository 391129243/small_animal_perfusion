package com.gidi.bio_console.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.gidi.bio_console.utils.DateFormatUtil;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class LogcatHelper {
  
    private static LogcatHelper INSTANCE = null;  
    private static String PATH_LOGCAT; 
    private LogDumper mLogDumper = null;  
    private int mPId;  
	private static final String FILE_NAME_SUFFIX = ".log";
	private int SDCARD_LOG_FILE_SAVE_DAYS = 5;  //最多保存5天
	private final int SDCARD_TYPE = 0;          //当前的日志记录类型为存储在SD卡下面  
	private final int MEMORY_TYPE = 1;          //当前的日志记录类型为存储在内存中  
	private int CURR_LOG_TYPE = SDCARD_TYPE;    //当前的日志记录类型  
	private SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyyMMdd-HH-mm-ss",
			Locale.CHINA);  
  
    /** 
     *  
     * 初始化
     *  
     * */  
    public void init(Context context) {  
    	
        if (Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {
        	PATH_LOGCAT = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Bio_Console/Log/";
            //PATH_LOGCAT = getDiskCacheDir(context)+ File.separator + "Log/"; 
            CURR_LOG_TYPE = SDCARD_TYPE;
        } else {
            PATH_LOGCAT = context.getFilesDir().getPath()  
                    + File.separator + "Bio_Console" + File.separator + "Log/"; 
            CURR_LOG_TYPE = MEMORY_TYPE;
        }
        
        File dir = new File(PATH_LOGCAT);  
        if (!dir.exists()|| !dir.isDirectory()) {  
        	dir.mkdirs();  
        } 
        
    }  
  
    public static LogcatHelper getInstance(Context context) {  
        if (INSTANCE == null) {  
            INSTANCE = new LogcatHelper(context);  
        }  
        return INSTANCE;  
    }  
  
    private LogcatHelper(Context context) {  
        init(context);  
        //获取当前进程
        mPId = android.os.Process.myPid();  
    }  
  
    public void start() {  
        if (mLogDumper == null){
        	mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);  
            mLogDumper.start();  
        }           
    }  
  
    public void stop() {  
    	Log.i("BioConsoleApplication", "--LogcatHelper stop--");
        if (mLogDumper != null) {  
            mLogDumper.stopLogs();  
            mLogDumper = null;  
        }  
    }  
  
    private class LogDumper extends Thread {  
  
        private Process logcatProc;  
        private BufferedReader mReader = null;  
        private boolean mRunning = true;  
        String cmds = null;  
        private String mPID;  
        private FileOutputStream out = null;  
  
        public LogDumper(String pid, String dir) {  
            mPID = pid;  
            try {  
            	File newFile = new File(dir +   
                        DateFormatUtil.getFileName() + FILE_NAME_SUFFIX);
            	Log.i("LogcatHelper", "path" + newFile.getAbsolutePath());
            	
            	if(!newFile.exists()){
            		newFile.getParentFile().mkdirs();
            		newFile.createNewFile();
        		}
            	sleep(1000);
            	deleteSDcardExpiredLog(newFile);
                out = new FileOutputStream(newFile);  
            } catch (FileNotFoundException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
  
            /** 
             *  
             * ��־�ȼ���*:v , *:d , *:w , *:e , *:f , *:s 
             *  
             *  
             * */  
  
            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";  
            // cmds = "logcat  | grep \"(" + mPID + ")\"";
            // cmds = "logcat -s way";
           cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";  
            //cmds = "logcat *:w | grep \"(" + mPID + ")\"";
        }  
  
        public void stopLogs() {  
            mRunning = false;  
        }  
  
        @Override  
        public void run() {  
            try {  
                logcatProc = Runtime.getRuntime().exec(cmds);  
                mReader = new BufferedReader(new InputStreamReader(  
                        logcatProc.getInputStream()), 512);  
                String line = null;  
                while (mRunning && (line = mReader.readLine()) != null) {  
                    if (!mRunning) {  
                        break;  
                    }  
                    if (line.length() == 0) {  
                        continue;  
                    }  
                    if (out != null && line.contains(mPID)) {  
                        out.write((DateFormatUtil.getSysFullTime() + "   " + line + "\n")  
                                .getBytes());  
                    }  
                }  
  
            } catch (IOException e) {  
                e.printStackTrace();  
            } finally {  
                if (logcatProc != null) {  
                    logcatProc.destroy();  
                    logcatProc = null;  
                }  
                if (mReader != null) {  
                    try {  
                        mReader.close();  
                        mReader = null;  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                }  
                if (out != null) {  
                    try {  
                        out.close(); 
                        out = null; 
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                    
                }  
  
            }  
  
        }    
    }  
    
    /**
     * 获取SD卡的Android/data/包名的路径
     *
     ***/
    private static String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath ;
       // return new File(cachePath + File.separator + uniqueName);
    }  
  
    /***
     * 删除多余5个的日志
     */
    private void deleteSDcardExpiredLog(File newFile){
    	if(CURR_LOG_TYPE == SDCARD_TYPE){
    		File dir = new File(PATH_LOGCAT);
    		if(dir.isDirectory()){
    			File[] allFiles = dir.listFiles();    			
    			for(File logFile :allFiles){
    				String fileName = logFile.getName();
    				if(!fileName.contains(".txt")){
	    				String createDateinfo = getFileNameWithoutRxtension(fileName);
	    				String newFileName = getFileNameWithoutRxtension(newFile.getName());
	    				if(canDeleteSDLog(createDateinfo,newFileName)){
	    					logFile.delete();
	    					Log.d("LogcatHelper", "delete expired log success,the log path is:"  
	    	                           + logFile.getAbsolutePath());
	    				}
    				}
    			}
    		}
    	}
    }
    
    /***
     * 判断日志是否可删除,比5前还提前的日志删除
     */
    private boolean canDeleteSDLog(String fileDateinfo){
    	boolean canDel = false;
    	//获取当前的日期
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_MONTH, -1 * SDCARD_LOG_FILE_SAVE_DAYS);
    	Date expireDateTime = calendar.getTime();
    	try {
			Date fileCreateDate = myLogSdf.parse(fileDateinfo);			
			canDel = fileCreateDate.before(expireDateTime);
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("LogcatHelper", e.getMessage()); 
	        canDel = false; 
		}
    	return canDel;
    }
    
    private boolean canDeleteSDLog(String fileDateinfo,String newFileName){
    	boolean canDel = false;
    	if(!fileDateinfo.equalsIgnoreCase(newFileName)){
    		canDel = true;
    	}
    	return canDel;
    }
    
    /**
     * 去除文件扩展类型(.log)
     * @param fileName
     */
    private String getFileNameWithoutRxtension(String fileName){   	
    	if(fileName.contains(".log")){
        	return fileName.substring(0, fileName.indexOf("."));  
    	}else{
    		return "";
    	}      	
    }
}
