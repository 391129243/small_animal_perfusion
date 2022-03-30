package com.gidi.bio_console.utils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.os.Environment;

/**
 * 上传日志文件工具类
 */
public class UploadUtil {

	private static final int TIME_OUT = 5 * 1000;
	private static final String CHARSET = "UTF-8"; //设置编码  
	private static final int ZIP_SUCCESS = 1;
	private static final int ZIP_FAIL = 0;
	private static final int ZIP_NOT_FILE = 2;
	private static final int ZIP_FILE_EXIST = 3;
	
	private static final int UPLOAD_SUCC = 1;
	private static final int UPLOAD_FAIL = 999;
	private static final int UPLOAD_NULL_INTERNET = 0;
	private static int buf_num = 1024;
	/**需要压缩的文件的目录**/
	private static final String  Log_Path = Environment.getExternalStorageDirectory().getAbsolutePath()
											+ "Bio_Console/Log/";

	/***
	 * 压缩文件
	 * @param zipFileName 压缩后的名字 当前的压缩日期作为压缩后的文件名
	 */
	public static int zipFileTool(File sourceFilePath ,String zipFileName){
		//判断是否是目录，
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			File zipFile = new File(zipFileName);//压缩后的文件				
			if(zipFile.exists()){
				return ZIP_FILE_EXIST;
			}else{
				if(sourceFilePath.isDirectory()){
					//列出目录中的文件列表
					File[] logFiles = sourceFilePath.listFiles();
					if(logFiles == null || logFiles.length < 1){
						return ZIP_NOT_FILE; 
					}else{
						fos = new FileOutputStream(zipFile);
						zos = new ZipOutputStream(new BufferedOutputStream(fos));
						for(File logFile : logFiles){							
							BufferedInputStream bis = new BufferedInputStream(new FileInputStream(logFile));
							String filename = logFile.getName();
							ZipEntry entry = new ZipEntry(filename);
							zos.putNextEntry(entry);
							int count;     
				            byte data[] = new byte[buf_num];     
				            while ((count = bis.read(data, 0, buf_num)) != -1) {     
				            	zos.write(data, 0, count);     
				            }     
				            bis.close();							
						}
						return ZIP_SUCCESS;//压缩成功
					}
					
				}else{
					return ZIP_FAIL;
				}
			 }	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ZIP_FAIL;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ZIP_FAIL;
			}finally{
				try {
					if(null != zos){
						zos.close();
						zos = null;
					}
					if(null != fos){
						fos.close();
						fos = null;
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
				
		}
		
	}
	
	/**向Web服务器上传压缩文件
	 * 首先判断网络是否可用
	 * **/
	public static int uploadFileTool(Context context,String url,File zipfile){
		
		if(!NetworkUtil.isConnect(context)){
			
			return UPLOAD_NULL_INTERNET;
		}else{
			try {
				//边界标识
				String BOUNDARY = UUID.randomUUID().toString();
				String CONTENT_TYPE = "multipart/form-data";
				String PREFIX = "--" ; 
				String LINE_END = "\r\n";  
				URL mUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection)mUrl.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setConnectTimeout(TIME_OUT);
				connection.setRequestProperty(CHARSET,"UTF-8");
				connection.setRequestProperty("connection", "keep-alive");
				connection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
				if(null != zipfile){
					OutputStream outputStream = connection.getOutputStream();
					DataOutputStream dos = new DataOutputStream(outputStream);
					StringBuffer sb = new StringBuffer();
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""  
	                        + zipfile.getName() + "\"" + LINE_END);
					sb.append("Content-Type : application/zip" + LINE_END);
					sb.append(LINE_END);//一行空格
					dos.write(sb.toString().getBytes());
					//再发送zip文件的二进制数据
					FileInputStream fis = new FileInputStream(zipfile);
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = fis.read(buffer)) != -1){
						dos.write(buffer, 0, len);
					}
					fis.close();
					dos.write(LINE_END.getBytes());  
	                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)  
	                        .getBytes();  
	                dos.write(end_data);  
	                dos.flush(); 
	                //获取响应码
	                int res = connection.getResponseCode();
					if(res == 200){
						
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return UPLOAD_FAIL;				
			}
			return UPLOAD_SUCC;
		}
		
	}
	
	/**	
	 * 删除文件目录下的zip文件
	 */
	private static void deleteZipFile(String fileDir){
		String extension = ".zip";	
		File dirfile = new File(fileDir);
		if(dirfile == null || !dirfile.isDirectory()){
			return ;
		}
		
		if(dirfile.isDirectory()){
			File[] files = dirfile.listFiles();
			for(File file : files){
				if(file.isFile() && file.getName().endsWith(extension)){
					file.delete();					
				}
			}
		}		
	}
}
