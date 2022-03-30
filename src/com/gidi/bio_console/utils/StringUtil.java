package com.gidi.bio_console.utils;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gidi.bio_console.constant.DefValues;


public class StringUtil {

	public static boolean isEmpty(String input){
		if (input == null || input.isEmpty()|| input.equals(DefValues.DEDAULT_NULL)){
			return true;
		}
		return false;
	}
	
	public static boolean isRight(String input){
		if (!StringUtil.isEmpty(input) && !input.equals(DefValues.DEDAULT_NULL)){
			return true;
		}
		return false;
	}
	
	public static String[] splitString(String input){
		String[] spilteResult = null;
		spilteResult = input.split("#");
		return spilteResult;
	}
	
	//忽略个位数
	public static int IgnoreUnities(int input){
		int ignore_result = 0;
		if(input > 0){
			if(input > 10 ){
				ignore_result = (int)input/10*10;
			}else{
				ignore_result = input;
			}
		}

		return ignore_result;
	}
	
	public static String md5(String currentString){
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
            e.printStackTrace();
            return "";
		}
		
		char[] charArray = currentString.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for(int i = 0; i< charArray.length; i++){
			 byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
        for (int j = 0; j < md5Bytes.length; j++) {
        	int val = ((int) md5Bytes[j]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
		
	}
	
	
	public static int convertToInt(Object value, int defaultValue){
		if(value == null || "".equals(value.toString())){
			return defaultValue;
		}
		try {
			return Integer.parseInt(value.toString());
					
		} catch (NumberFormatException e1){
			return defaultValue;
		}catch (Exception e) {
			// TODO: handle exception
			try {
				return Double.valueOf(value.toString()).intValue();
			} catch (NumberFormatException e2){
				return defaultValue;
			}catch (Exception e3) {
				// TODO: handle exception
				return defaultValue;
			}
		}
	}
	
	public static float convertToFloat(Object value, float defaultValue){
		if(value == null || "".equals(value.toString())){
			return defaultValue;
		}
		try {
			return Float.parseFloat(value.toString());
		}  catch (NumberFormatException e){
			return defaultValue;
		}catch (Exception e1) {
			// TODO: handle exception
			try {
				return Double.valueOf(value.toString()).floatValue();
			} catch (NumberFormatException e2){
				return defaultValue;
			}catch (Exception e3) {
				// TODO: handle exception
				return defaultValue;
			}
		}
	}
	

	
	public static String appendMsg(String input, int value, String decimal){
		StringBuilder builder = new StringBuilder();	
		String msgValue = String.format("%05d",value);
		String output = input.replaceAll("xxxxx", msgValue);
		builder.append(output).append("_").append(decimal);
		return builder.toString();
		

	}
	

	public static String getString_Num(String input){
		StringBuilder builder = new StringBuilder();
		String result = null;
		String totalnum = null;
		String sub_result_num = DefValues.DEDAULT_NULL;
		if(null != input && !input.equals("")){
			input = input.substring(13);
			String regEx="[^-0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(input);
			result = m.replaceAll("");
			
			String sign = "";
			if(result.length()==6){

				String value = result.substring(1,result.length()-1);

				String decimal = result.substring(result.length()-1, result.length());
				if((result.substring(0,1)).equals("-")){
					sign = "-";
				}else{
					sign = "";
				}
				int temp = convertToInt(value, 0);
				float num = 0;
				if(!decimal.equals("0")){
					if(decimal.equals("1")){
						num = (float)temp/10;
						DecimalFormat df = new DecimalFormat("0.0");
						totalnum = df.format(num);
					}else if(decimal.equals("2")){				
						num = (float)temp/100;	
						DecimalFormat df = new DecimalFormat("0.00");
						totalnum = df.format(num);
					}else if(decimal.equals("3")){
						num = (float)temp/1000;
						DecimalFormat df = new DecimalFormat("0.000");
						totalnum = df.format(num);
					}
					
					builder.append(sign).append(totalnum);
				}else{
					if(value.startsWith("0")){						
						builder.append(sign).append(temp);
					}else{
						builder.append(sign).append(value);
					}
				}
				sub_result_num = builder.toString();
			}
									
		}
		return sub_result_num;
	}
	
	
	public static boolean isAllZero(String input){
		boolean result = false;		
		if(input != null && !input.equals("")){
			String regEx="[0]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(input);
			String output = m.replaceAll("");
			if(output.equals("")){
				result = true;
			}
		}		
		return result;
	}
	
	/**拼接搏动的目标压力**/
	public static  String appendMinMaxPre(String input,int min,int max){
		StringBuilder builder = new StringBuilder();		
		builder.append(String.valueOf(max)).append(String.valueOf(min));
		String msgValue = builder.toString();
		int subLength = 5 - msgValue.length();
		StringBuilder sb = new StringBuilder();  
		if(subLength > 0){
			for(int i = 0; i<subLength ;i++){
				sb.append("0");
			}
		}
		sb.append(msgValue);
		String output = input.replaceAll("xxxxx", sb.toString());
		return output;
	}
	
	
	
	/***
	 * 提取5个数字的，收割字符不是符号的信息
	 * 例如电池状态和仪器信息
	 */
	public static String getAllNumFromMsg(String input){
		String result = null;
		String value = null;
		if(null != input && !input.equals("")){
			input = input.substring(13);
			String regEx="[^-0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(input);
			result = m.replaceAll("");
			if(result.length()==6){					
				value = result.substring(0,result.length()-1);
			}			
		}
		return value;
	}
	
	public static boolean isDicimals(String input) {// 判断小数，与判断整型的区别在与d后面的小数点（红色）
		return input.matches("\\d+\\.\\d+$");
	}
	
	public static String getInteger(String input){
		String result = "";
		result = input.replaceFirst("^0*", "");
		return result;
	}
	
	public static String getNumOfMsg(String input){
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
}
