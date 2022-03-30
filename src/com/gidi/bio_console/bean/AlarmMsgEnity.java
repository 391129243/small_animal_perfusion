package com.gidi.bio_console.bean;



public class AlarmMsgEnity {
	private String msgType;
	private String liverNum;
	private String errorCode;
	private String alarmTime;
	

	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getLiverNum() {
		return liverNum;
	}
	public void setLiverNum(String liverNum) {
		this.liverNum = liverNum;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
