package com.gidi.bio_console.utils;

import java.io.File;
import java.io.IOException;


import android.os.Environment;

public class FileUtils {

	//public static final String USB_PATH_LU = "/storage/usbdisk/";
	
	public static final String EXCEL_SUFFIX = ".xls";
	/**判断是否有Usb设备***/
	public static boolean isExistUdisk(String usb_path){
		if(!fileIsExists(usb_path)){
			return false;
		}
		
		return true;
	}
	
	public static boolean fileIsExists(String path){
        try{
            File f=new File(path);
            if(!f.exists()){
                return false;
            }
         
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
	

	
	
	/**
	 * 在U盘上创建目录
	 */
	public static File createUdiskDir(String usb_path ,String dirName){
		File dir = new File(usb_path + dirName);
		if (!dir.exists()|| !dir.isDirectory()) {  
			dir.mkdirs();         	
        }
		return dir;
	}
	
	public static File createUdiskFile(String usb_path ,String fileName){
		 File file = new File(usb_path + fileName);
	     try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return file;
	}
	
	public static File createDir(File dirName, String subDirName){
		File dir = new File(dirName , subDirName);		
		if (!dir.exists()|| !dir.isDirectory()) {  
			dir.mkdir();         	
        }
		return dir;
	}
	
	/**
	 * 检查外部存储器是否可写
	 * @author xuxiaoshan
	 * @version 1.5.11
	 */
	public boolean isExternalStorageWritable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		return false;		
	}
	
	/**
	 * 只读检查
	 */
	public boolean isExternalStorageReadable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)||
			Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			return true;
			
		}
		return false;
	}
}
